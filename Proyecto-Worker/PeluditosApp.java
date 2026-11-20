import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** External Worker Peluditos Pet Chile - cuatro tópicos BPMN. Java 21, sin dependencias externas. */
public class PeluditosApp {
    private static final String[] TOPICS = {
        "peluditos-lead-topic",
        "peluditos-cierre-topic",
        "peluditos-oportunidades-topic",
        "peluditos-resultado-oportunidad-topic"
    };
    private static final String WORKER_ID = env("WORKER_ID", "peluditos-worker");
    private static final String FLOWABLE_URL = trimSlash(env("FLOWABLE_URL", "http://localhost:8080/flowable-rest"));
    // WS_URL es la base /api, no una ruta de recurso.
    private static final String WS_URL = trimSlash(env("WS_URL", "http://localhost:8081/api"));
    private static final String AUTH = authHeader();
    private static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();

    public static void main(String[] args) throws Exception {
        System.out.println("==========================================");
        System.out.println(" EXTERNAL WORKER PELUDITOS PET CHILE");
        System.out.println(" Flowable : " + FLOWABLE_URL);
        System.out.println(" Web svc  : " + WS_URL);
        System.out.println(" Topics   : " + String.join(", ", TOPICS));
        System.out.println("==========================================");
        if (AUTH == null) {
            System.err.println("Falta FLOWABLE_TOKEN o FLOWABLE_USER/FLOWABLE_PASSWORD.");
            return;
        }
        while (true) {
            for (String topic : TOPICS) atenderTopic(topic);
            Thread.sleep(2000);
        }
    }

    private static void atenderTopic(String topic) {
        try {
            String jobs = adquirirTrabajo(topic);
            String jobId = first(jobs, "\"id\"\\s*:\\s*\"([^\"]+)\"");
            if (jobId == null) return;
            System.out.println("\n[Worker] Trabajo recibido topic=" + topic + " jobId=" + jobId);
            switch (topic) {
                case "peluditos-lead-topic" -> procesarLead(jobId, jobs);
                case "peluditos-cierre-topic" -> procesarCierre(jobId, jobs);
                case "peluditos-oportunidades-topic" -> procesarGeneracionOportunidades(jobId, jobs);
                case "peluditos-resultado-oportunidad-topic" -> procesarResultadoOportunidad(jobId, jobs);
                default -> throw new IllegalStateException("Topic no reconocido: " + topic);
            }
        } catch (Exception e) {
            System.err.println("[Error " + topic + "] " + e.getMessage());
        }
    }

    private static String adquirirTrabajo(String topic) throws Exception {
        String body = "{\"topic\":\"" + json(topic) + "\",\"lockDuration\":\"PT5M\",\"numberOfTasks\":1,\"numberOfRetries\":3,\"workerId\":\"" + json(WORKER_ID) + "\",\"scope\":\"bpmn\"}";
        HttpResponse<String> r = postFlowable("/external-job-api/acquire/jobs", body);
        requireOk(r, "adquirir trabajo en Flowable");
        return r.body();
    }

    private static void procesarLead(String jobId, String job) throws Exception {
        try {
            String nombre = requiredVar(job, "nombre");
            String telefono = var(job, "");
            String correo = var(job, "");
            String canal = var(job, "no informado", "canal");
            String interes = var(job, "no informado", "interes");
            String processInstanceId = first(job, "\"processInstanceId\"\\s*:\\s*\"([^\"]+)\"");
            if (processInstanceId == null) processInstanceId = var(job, "", "processInstanceId");
            String payload = "{\"nombre\":\""+json(nombre)+"\",\"telefono\":\""+json(telefono)+"\",\"correo\":\""+json(correo)+"\",\"canal\":\""+json(canal)+"\",\"interes\":\""+json(interes)+"\",\"processInstanceId\":\""+json(processInstanceId)+"\"}";
            HttpResponse<String> ws = send("POST", "/leads", payload);
            requireOk(ws, "registrar lead en Web Service");
            String id = first(ws.body(), "\"id\"\\s*:\\s*(\\d+)");
            if (id == null) throw new IllegalStateException("La respuesta del WS no contiene id: " + ws.body());
            complete(jobId, "{\"name\":\"leadId\",\"type\":\"integer\",\"value\":"+id+"},{\"name\":\"registrado\",\"type\":\"boolean\",\"value\":true}");
            System.out.println("[Worker] Lead registrado. leadId=" + id);
        } catch (Exception e) { fail(jobId, "Error al registrar lead", e); }
    }

    private static void procesarCierre(String jobId, String job) throws Exception {
        try {
            String leadId = requiredVar(job, "leadId");
            String comproText = var(job, "false", "compro");
            boolean compro = Boolean.parseBoolean(comproText);
            String motivo = var(job, "", "motivoNoCompra");
            String comentario = var(job, "", "comentarioNoCompra");
            String payload = "{\"compro\":"+compro+",\"motivoNoCompra\":\""+json(motivo)+"\",\"comentarioNoCompra\":\""+json(comentario)+"\"}";
            HttpResponse<String> ws = send("PUT", "/leads/"+leadId+"/cierre", payload);
            requireOk(ws, "cerrar lead en Web Service");
            String estado = first(ws.body(), "\"estado\"\\s*:\\s*\"([^\"]*)\"");
            if (estado == null) estado = compro ? "Convertido" : "No convertido";
            complete(jobId, "{\"name\":\"estadoLead\",\"type\":\"string\",\"value\":\""+json(estado)+"\"}");
            System.out.println("[Worker] Lead cerrado: " + estado);
        } catch (Exception e) { fail(jobId, "Error al cerrar lead", e); }
    }

    private static void procesarGeneracionOportunidades(String jobId, String job) throws Exception {
        try {
            String idFecha = requiredVar(job, "idFechaRelevante");
            String processInstanceId = first(job, "\"processInstanceId\"\\s*:\\s*\"([^\"]+)\"");
            if (processInstanceId == null) processInstanceId = var(job, "", "processInstanceId");
            String payload = "{\"idFechaRelevante\":"+idFecha+",\"processInstanceId\":\""+json(processInstanceId)+"\"}";
            HttpResponse<String> ws = send("POST", "/oportunidades/generar", payload);
            requireOk(ws, "generar oportunidades");
            String array = extractArray(ws.body(), "oportunidades");
            if (array == null) throw new IllegalStateException("La respuesta no contiene el arreglo oportunidades: " + ws.body());
            String total = first(ws.body(), "\"total\"\\s*:\\s*(\\d+)");
            if (total == null) total = Integer.toString(countObjects(array));
            // Flowable recibe oportunidades como variable JSON para el subproceso multi-instancia.
            complete(jobId, "{\"name\":\"oportunidades\",\"type\":\"json\",\"value\":"+array+"},{\"name\":\"totalOportunidades\",\"type\":\"integer\",\"value\":"+total+"}");
            System.out.println("[Worker] Campaña generada: " + total + " oportunidades.");
        } catch (Exception e) { fail(jobId, "Error al generar oportunidades", e); }
    }

    private static void procesarResultadoOportunidad(String jobId, String job) throws Exception {
        try {
            String oportunidad = variableObject(job, "oportunidad");
            if (oportunidad == null) throw new IllegalStateException("No se encontró la variable JSON oportunidad en el trabajo");
            String id = first(oportunidad, "\"idOportunidad\"\\s*:\\s*(\\d+)");
            if (id == null) throw new IllegalStateException("La variable oportunidad no contiene idOportunidad: " + oportunidad);
            String interesado = var(job, "false", "interesado");
            String comentario = var(job, "", "comentarioContacto");
            String payload = "{\"interesado\":"+Boolean.parseBoolean(interesado)+",\"comentarioContacto\":\""+json(comentario)+"\"}";
            HttpResponse<String> ws = send("PUT", "/oportunidades/"+id+"/resultado", payload);
            requireOk(ws, "registrar resultado de oportunidad");
            complete(jobId, "");
            System.out.println("[Worker] Resultado de oportunidad " + id + " registrado.");
        } catch (Exception e) { fail(jobId, "Error al registrar resultado de oportunidad", e); }
    }

    private static HttpResponse<String> send(String method, String path, String body) throws Exception {
        HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(WS_URL + path)).timeout(Duration.ofSeconds(30))
            .header("Content-Type", "application/json").header("Accept", "application/json");
        if ("POST".equals(method)) b.POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8));
        else if ("PUT".equals(method)) b.PUT(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8));
        else b.GET();
        return HTTP.send(b.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    private static void complete(String jobId, String variables) throws Exception {
        String body = "{\"workerId\":\""+json(WORKER_ID)+"\",\"variables\":["+variables+"]}";
        HttpResponse<String> r = postFlowable("/external-job-api/acquire/jobs/"+jobId+"/complete", body);
        requireOk(r, "completar trabajo en Flowable");
    }

    private static void fail(String jobId, String message, Exception e) throws Exception {
        System.err.println("[Error] " + message + ": " + e.getMessage());
        String body = "{\"workerId\":\""+json(WORKER_ID)+"\",\"errorMessage\":\""+json(message)+"\",\"errorDetails\":\""+json(String.valueOf(e))+"\",\"retryTimeout\":\"PT30S\"}";
        HttpResponse<String> r = postFlowable("/external-job-api/acquire/jobs/"+jobId+"/fail", body);
        if (r.statusCode() >= 300) System.err.println("[Error] Flowable no aceptó el reporte de fallo: HTTP " + r.statusCode() + " " + r.body());
    }

    private static HttpResponse<String> postFlowable(String path, String body) throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(FLOWABLE_URL + path)).timeout(Duration.ofSeconds(30))
            .header("Content-Type", "application/json").header("Accept", "application/json")
            .header("Authorization", AUTH).POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8)).build();
        return HTTP.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    private static void requireOk(HttpResponse<String> r, String action) {
        if (r.statusCode() >= 300) throw new IllegalStateException(action + " respondió HTTP " + r.statusCode() + ": " + r.body());
    }

    private static String var(String job, String def, String... names) {
        for (String name : names) {
            Pattern p = Pattern.compile("\\{[^{}]*\\\"name\\\"\\s*:\\s*\\\""+Pattern.quote(name)+"\\\"[^{}]*\\}");
            Matcher m = p.matcher(job);
            if (m.find()) {
                String obj = m.group();
                Matcher vm = Pattern.compile("\\\"value\\\"\\s*:\\s*(?:\\\"((?:\\\\\\.|[^\\\"])*)\\\"|([^,}]+))").matcher(obj);
                if (vm.find()) {
                    String value = vm.group(1) != null ? vm.group(1) : vm.group(2);
                    return unescape(value.trim());
                }
            }
        }
        return def;
    }

    private static String requiredVar(String job, String name) {
        String v = var(job, null, name);
        if (v == null || v.isBlank() || "null".equals(v)) throw new IllegalStateException("Falta la variable obligatoria '"+name+"'");
        return v;
    }

    private static String variableObject(String job, String name) {
        Matcher nameMatch = Pattern.compile("\\\"name\\\"\\s*:\\s*\\\"" + Pattern.quote(name) + "\\\"").matcher(job);
        if (!nameMatch.find()) return null;
        int valueKey = job.indexOf("\"value\"", nameMatch.end());
        if (valueKey < 0) return null;
        int colon = job.indexOf(':', valueKey) + 1;
        while (colon < job.length() && Character.isWhitespace(job.charAt(colon))) colon++;
        if (colon >= job.length() || job.charAt(colon) != '{') return null;
        return balanced(job, colon, '{', '}');
    }

    private static String extractArray(String json, String key) {
        Matcher m = Pattern.compile("\\\""+Pattern.quote(key)+"\\\"\\s*:").matcher(json);
        if (!m.find()) return null;
        int start = json.indexOf('[', m.end());
        return start < 0 ? null : balanced(json, start, '[', ']');
    }

    private static String balanced(String s, int start, char open, char close) {
        int depth = 0; boolean quoted = false, escaped = false;
        for (int i=start; i<s.length(); i++) {
            char c=s.charAt(i);
            if (quoted) { if (escaped) escaped=false; else if (c=='\\') escaped=true; else if (c=='"') quoted=false; continue; }
            if (c=='"') quoted=true;
            else if (c==open) depth++;
            else if (c==close && --depth==0) return s.substring(start,i+1);
        }
        return null;
    }

    private static int countObjects(String array) {
        int count=0; boolean quoted=false, escaped=false; int depth=0;
        for (int i=0;i<array.length();i++) { char c=array.charAt(i);
            if (quoted) { if (escaped) escaped=false; else if(c=='\\') escaped=true; else if(c=='"') quoted=false; continue; }
            if(c=='"') quoted=true; else if(c=='{') { if(depth==0) count++; depth++; } else if(c=='}') depth--;
        }
        return count;
    }

    private static String first(String s, String regex) { Matcher m=Pattern.compile(regex).matcher(s); return m.find()?m.group(1):null; }
    private static String json(String s) { return s==null?"":s.replace("\\","\\\\").replace("\"","\\\"").replace("\n","\\n").replace("\r","\\r").replace("\t","\\t"); }
    private static String unescape(String s) { return s.replace("\\\"","\"").replace("\\n","\n").replace("\\r","\r").replace("\\t","\t").replace("\\\\","\\"); }
    private static String trimSlash(String s) { while(s.endsWith("/")) s=s.substring(0,s.length()-1); return s; }
    private static String env(String name, String def) { String v=System.getenv(name); return v==null||v.isBlank()?def:v.trim(); }
    private static String authHeader() {
        String token=System.getenv("FLOWABLE_TOKEN"); if(token!=null&&!token.isBlank()) return "Bearer "+token.trim();
        String user=System.getenv("FLOWABLE_USER"), pass=System.getenv("FLOWABLE_PASSWORD");
        if(user!=null&&!user.isBlank()&&pass!=null) return "Basic "+Base64.getEncoder().encodeToString((user+":"+pass).getBytes(StandardCharsets.UTF_8));
        return null;
    }
}
