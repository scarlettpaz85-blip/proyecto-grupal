import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * External worker de Peluditos Pet Chile (version de un solo archivo, sin librerias externas).
 *
 * 1. Pide a Flowable Cloud los trabajos pendientes del topic "peluditos-lead-topic".
 * 2. Por cada trabajo, llama al web service real: POST http://localhost:8081/api/leads
 * 3. Completa el trabajo en Flowable con leadId y registrado, para que el proceso continue.
 *
 * Uso (cmd):
 *   javac PeluditosApp.java
 *   set FLOWABLE_TOKEN=tu_token            (Flowable Work Cloud: token del MISMO ambiente)
 *   java PeluditosApp
 *
 * Flowable OSS local (Basic Authentication):
 *   set FLOWABLE_URL=http://localhost:8080/flowable-rest
 *   set FLOWABLE_USER=rest-admin
 *   set FLOWABLE_PASSWORD=test
 *   java PeluditosApp
 */
public class PeluditosApp {

    private static final String TOPIC_NAME = "peluditos-lead-topic";
    private static final String WORKER_ID = "peluditos-worker-jennifer";
    private static final String FLOWABLE_URL = env("FLOWABLE_URL", "https://iplacex.cloud.flowable.com/work");
    private static final String WS_URL = env("WS_URL", "http://localhost:8081/api/leads");
    private static final String AUTH = authHeader();
    private static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();

    public static void main(String[] args) throws Exception {
        System.out.println("==========================================");
        System.out.println(" EXTERNAL WORKER PELUDITOS PET CHILE");
        System.out.println(" Flowable : " + FLOWABLE_URL);
        System.out.println(" Topico   : " + TOPIC_NAME);
        System.out.println(" Web svc  : " + WS_URL);
        System.out.println("==========================================");
        if (AUTH == null) {
            System.out.println("[Error] Falta la autenticacion: set FLOWABLE_TOKEN=... (Cloud) o set FLOWABLE_USER / FLOWABLE_PASSWORD (OSS)");
            return;
        }
        while (true) {
            try {
                String jobs = adquirirTrabajo();
                String jobId = buscar(jobs, "\"id\"\\s*:\\s*\"([^\"]+)\"");
                if (jobId == null) {
                    System.out.println("[Worker] Sin trabajos pendientes en '" + TOPIC_NAME + "'. Reintentando en 5 s...");
                } else {
                    procesar(jobId, jobs);
                }
            } catch (Exception e) {
                System.out.println("[Error] " + e.getMessage());
            }
            Thread.sleep(5000);
        }
    }

    /** POST /external-job-api/acquire/jobs : pide 1 trabajo del topico. */
    private static String adquirirTrabajo() throws Exception {
        String body = "{\"topic\":\"" + TOPIC_NAME + "\",\"lockDuration\":\"PT5M\",\"numberOfTasks\":1,"
                + "\"numberOfRetries\":3,\"workerId\":\"" + WORKER_ID + "\",\"scope\":\"bpmn\"}";
        HttpResponse<String> r = postFlowable("/external-job-api/acquire/jobs", body);
        if (r.statusCode() != 200) {
            throw new IllegalStateException("Flowable respondio " + r.statusCode() + " al pedir trabajos: " + r.body());
        }
        return r.body();
    }

    private static void procesar(String jobId, String jobJson) throws Exception {
        System.out.println("\n[Worker] Trabajo recibido de Flowable: " + jobId);
        String nombre = variable(jobJson, "Lead sin nombre", "nombre", "nombreCliente", "nombreLead");
        String canal = variable(jobJson, "no informado", "canal", "canalOrigen", "canalContacto");
        String interes = variable(jobJson, "no informado", "interes", "producto", "tipoProducto");
        System.out.println("[Worker] Datos del proceso: nombre=" + nombre + ", canal=" + canal + ", interes=" + interes);

        try {
            // 1) Llamar al web service real
            String lead = "{\"nombre\":\"" + json(nombre) + "\",\"canal\":\"" + json(canal)
                    + "\",\"interes\":\"" + json(interes) + "\"}";
            HttpRequest wsReq = HttpRequest.newBuilder(URI.create(WS_URL))
                    .timeout(Duration.ofSeconds(15))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(lead, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> ws = HTTP.send(wsReq, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            System.out.println("[Web Service] HTTP " + ws.statusCode() + ": " + ws.body());
            if (ws.statusCode() >= 300) {
                throw new IllegalStateException("El web service respondio " + ws.statusCode());
            }
            String leadId = buscar(ws.body(), "\"id\"\\s*:\\s*(\\d+)");
            if (leadId == null) leadId = "0";

            // 2) Completar el trabajo en Flowable
            String complete = "{\"workerId\":\"" + WORKER_ID + "\",\"variables\":["
                    + "{\"name\":\"leadId\",\"type\":\"integer\",\"value\":" + leadId + "},"
                    + "{\"name\":\"registrado\",\"type\":\"boolean\",\"value\":true}]}";
            HttpResponse<String> r = postFlowable("/external-job-api/acquire/jobs/" + jobId + "/complete", complete);
            if (r.statusCode() >= 300) {
                throw new IllegalStateException("Flowable respondio " + r.statusCode() + " al completar: " + r.body());
            }
            System.out.println("[Worker] Tarea completada en Flowable (leadId=" + leadId + "). El proceso continua.");
        } catch (Exception e) {
            System.out.println("[Error] " + e.getMessage() + ". Se informa la falla a Flowable.");
            String fail = "{\"workerId\":\"" + WORKER_ID + "\",\"errorMessage\":\"Error al registrar el lead\","
                    + "\"errorDetails\":\"" + json(String.valueOf(e)) + "\",\"retryTimeout\":\"PT30S\"}";
            postFlowable("/external-job-api/acquire/jobs/" + jobId + "/fail", fail);
        }
    }

    private static HttpResponse<String> postFlowable(String path, String body) throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(FLOWABLE_URL + path))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", AUTH)
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();
        return HTTP.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    /** Busca el valor de una variable del proceso dentro del JSON del trabajo. */
    private static String variable(String jobJson, String porDefecto, String... nombres) {
        for (String n : nombres) {
            Matcher obj = Pattern.compile("\\{[^{}]*\"name\"\\s*:\\s*\"" + Pattern.quote(n) + "\"[^{}]*\\}").matcher(jobJson);
            if (obj.find()) {
                String v = buscar(obj.group(), "\"value\"\\s*:\\s*\"?([^\",}]*)");
                if (v != null && !v.isBlank() && !v.equals("null")) return v;
            }
        }
        return porDefecto;
    }

    private static String buscar(String texto, String regex) {
        Matcher m = Pattern.compile(regex).matcher(texto);
        return m.find() ? m.group(1) : null;
    }

    private static String json(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String authHeader() {
        String token = System.getenv("FLOWABLE_TOKEN");
        if (token != null && !token.isBlank()) return "Bearer " + token.trim();
        String user = System.getenv("FLOWABLE_USER");
        String pass = System.getenv("FLOWABLE_PASSWORD");
        if (user != null && !user.isBlank() && pass != null) {
            return "Basic " + Base64.getEncoder().encodeToString((user + ":" + pass).getBytes(StandardCharsets.UTF_8));
        }
        return null;
    }

    private static String env(String name, String def) {
        String v = System.getenv(name);
        return (v == null || v.isBlank()) ? def : v.trim();
    }
}
