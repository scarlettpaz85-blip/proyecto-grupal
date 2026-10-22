package cl.peluditos.ws;

import java.net.URI;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

/** Inicia el web service REST de Peluditos Pet Chile en http://localhost:8081/api */
public class Main {
    public static final String BASE_URI = "http://0.0.0.0:8081/api/";

    public static void main(String[] args) throws Exception {
        ResourceConfig config = new ResourceConfig().packages("cl.peluditos.ws");
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), config);
        System.out.println("Web service Peluditos activo en http://localhost:8081/api/health");
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));
        Thread.currentThread().join();
    }
}
