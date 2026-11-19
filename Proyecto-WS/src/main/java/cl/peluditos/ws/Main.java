
package cl.peluditos.ws;

import java.net.URI;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Inicia el web service REST de Peluditos Pet Chile.
 */
public class Main {

    private static final String HOST = "0.0.0.0";
    private static final String DEFAULT_PORT = "8081";
    private static final String API_PATH = "/api/";

    public static final String BASE_URI = obtenerBaseUri();

    private static String obtenerBaseUri() {
        String puerto = System.getenv("PELUDITOS_WS_PORT");

        if (puerto == null || puerto.isBlank()) {
            puerto = DEFAULT_PORT;
        }

        try {
            int numeroPuerto = Integer.parseInt(puerto);

            if (numeroPuerto < 1 || numeroPuerto > 65535) {
                throw new IllegalArgumentException(
                    "El puerto debe estar entre 1 y 65535."
                );
            }

            return "http://" + HOST + ":" + numeroPuerto + API_PATH;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                "PELUDITOS_WS_PORT debe contener un número válido.", e
            );
        }
    }

    public static void main(String[] args) throws Exception {
        ResourceConfig config =
            new ResourceConfig().packages("cl.peluditos.ws");

        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(
            URI.create(BASE_URI), config
        );

        System.out.println(
            "Web service Peluditos activo en http://localhost:"
            + BASE_URI.split(":")[2].split("/")[0]
            + "/api/health"
        );

        Runtime.getRuntime().addShutdownHook(
            new Thread(server::shutdownNow)
        );

        Thread.currentThread().join();
    }
}
