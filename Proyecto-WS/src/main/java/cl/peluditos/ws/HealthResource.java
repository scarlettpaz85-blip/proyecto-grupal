package cl.peluditos.ws;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.Map;

/** Permite verificar que el servicio está activo. */
@Path("health")
public class HealthResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> estado() {
        return Map.of("servicio", "peluditos-ws", "estado", "ok");
    }
}
