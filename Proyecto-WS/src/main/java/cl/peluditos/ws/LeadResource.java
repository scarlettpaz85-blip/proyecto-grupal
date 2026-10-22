package cl.peluditos.ws;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Web service de prueba (Entrega 1).
 * Guarda los leads en memoria; en entregas posteriores se conectará a una base de datos.
 */
@Path("leads")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LeadResource {

    private static final Map<Integer, Lead> LEADS = new ConcurrentHashMap<>();
    private static final AtomicInteger SECUENCIA = new AtomicInteger(0);

    /** Registra un lead. Lo invoca el external worker desde la tarea "Registrar lead". */
    @POST
    public Response registrar(Lead lead) {
        if (lead == null || lead.nombre == null || lead.nombre.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "El campo 'nombre' es obligatorio")).build();
        }
        lead.id = SECUENCIA.incrementAndGet();
        lead.estado = "NUEVO";
        lead.registrado = true;
        lead.fechaRegistro = LocalDateTime.now().toString();
        LEADS.put(lead.id, lead);
        System.out.println("[WS] Lead registrado: id=" + lead.id + ", nombre=" + lead.nombre + ", canal=" + lead.canal);
        return Response.status(Response.Status.CREATED).entity(lead).build();
    }

    /** Lista todos los leads registrados. */
    @GET
    public List<Lead> listar() {
        return new ArrayList<>(LEADS.values());
    }

    /** Obtiene un lead por su id. */
    @GET
    @Path("{id}")
    public Response obtener(@PathParam("id") int id) {
        Lead lead = LEADS.get(id);
        if (lead == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Lead no encontrado")).build();
        }
        return Response.ok(lead).build();
    }
}
