
package cl.peluditos.ws;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Path("leads")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LeadResource {

    // Determina si el error SQL corresponde a una pérdida de conexión.
    private boolean esErrorConexion(SQLException e) {
        SQLException actual = e;

        while (actual != null) {
            String estadoSQL = actual.getSQLState();

            if (estadoSQL != null && estadoSQL.startsWith("08")) {
                return true;
            }

            actual = actual.getNextException();
        }

        return false;
    }

    // Genera una respuesta segura para los errores de base de datos.
    private Response respuestaErrorBD(SQLException e, String operacion) {

        System.err.println("[WS] Error al " + operacion + ": "
                + e.getMessage());

        if (esErrorConexion(e)) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(Map.of(
                            "error",
                            "Servicio de base de datos no disponible. Intente nuevamente más tarde."
                    ))
                    .build();
        }

        return Response.serverError()
                .entity(Map.of(
                        "error",
                        "Ocurrió un error interno al " + operacion
                ))
                .build();
    }

    // Registrar un lead
    @POST
    public Response registrar(Lead lead) {

        if (lead == null || lead.nombre == null
                || lead.nombre.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error",
                            "El campo nombre es obligatorio"))
                    .build();
        }

        if (lead.canal == null || lead.canal.isBlank()
                || lead.interes == null || lead.interes.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error",
                            "Los campos canal e interes son obligatorios"))
                    .build();
        }

        String sql = """
            INSERT INTO dbo.Leads
                (nombre, telefono, correo, canal,
                 producto_interes, estado, process_instance_id)
            OUTPUT INSERTED.id_lead, INSERTED.fecha_registro
            VALUES (?, ?, ?, ?, ?, 'Nuevo', ?)
            """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, lead.nombre);
            statement.setString(2, lead.telefono);
            statement.setString(3, lead.correo);
            statement.setString(4, lead.canal);
            statement.setString(5, lead.interes);
            statement.setString(6, lead.processInstanceId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    lead.id = rs.getInt("id_lead");
                    lead.estado = "Nuevo";
                    lead.registrado = true;

                    if (rs.getTimestamp("fecha_registro") != null) {
                        lead.fechaRegistro =
                                rs.getTimestamp("fecha_registro")
                                        .toLocalDateTime().toString();
                    }
                } else {
                    return Response.serverError()
                            .entity(Map.of("error",
                                    "No fue posible confirmar el registro del lead"))
                            .build();
                }
            }

            return Response.status(Response.Status.CREATED)
                    .entity(lead)
                    .build();

        } catch (SQLException e) {
            return respuestaErrorBD(e, "registrar el lead");
        }
    }

    // Listar leads con paginación
    @GET
    public Response listar(
            @DefaultValue("1") @QueryParam("page") int page,
            @DefaultValue("10") @QueryParam("size") int size) {

        if (page < 1 || size < 1 || size > 100) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error",
                            "Página inválida o tamaño fuera del rango permitido"))
                    .build();
        }

        List<Lead> leads = new ArrayList<>();
        long offset = (long) (page - 1) * size;

        String sql = """
            SELECT id_lead, nombre, telefono, correo,
                   canal, producto_interes, estado,
                   fecha_registro, process_instance_id
            FROM dbo.Leads
            ORDER BY id_lead
            OFFSET ? ROWS
            FETCH NEXT ? ROWS ONLY
            """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, offset);
            statement.setInt(2, size);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Lead lead = new Lead();

                    lead.id = rs.getInt("id_lead");
                    lead.nombre = rs.getString("nombre");
                    lead.telefono = rs.getString("telefono");
                    lead.correo = rs.getString("correo");
                    lead.canal = rs.getString("canal");
                    lead.interes = rs.getString("producto_interes");
                    lead.estado = rs.getString("estado");

                    if (rs.getTimestamp("fecha_registro") != null) {
                        lead.fechaRegistro =
                                rs.getTimestamp("fecha_registro")
                                        .toLocalDateTime().toString();
                    }

                    lead.processInstanceId =
                            rs.getString("process_instance_id");
                    lead.registrado = true;

                    leads.add(lead);
                }
            }

            return Response.ok(leads).build();

        } catch (SQLException e) {
            return respuestaErrorBD(e, "listar los leads");
        }
    }

    // Buscar lead por ID
    @GET
    @Path("{id}")
    public Response obtener(@PathParam("id") int id) {

        if (id < 1) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error",
                            "El ID del lead debe ser mayor que cero"))
                    .build();
        }

        String sql = """
            SELECT id_lead, nombre, telefono, correo,
                   canal, producto_interes, estado,
                   fecha_registro, process_instance_id
            FROM dbo.Leads
            WHERE id_lead = ?
            """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return Response.status(Response.Status.NOT_FOUND)
                            .entity(Map.of("error",
                                    "Lead no encontrado"))
                            .build();
                }

                Lead lead = new Lead();

                lead.id = rs.getInt("id_lead");
                lead.nombre = rs.getString("nombre");
                lead.telefono = rs.getString("telefono");
                lead.correo = rs.getString("correo");
                lead.canal = rs.getString("canal");
                lead.interes = rs.getString("producto_interes");
                lead.estado = rs.getString("estado");

                if (rs.getTimestamp("fecha_registro") != null) {
                    lead.fechaRegistro =
                            rs.getTimestamp("fecha_registro")
                                    .toLocalDateTime().toString();
                }

                lead.processInstanceId =
                        rs.getString("process_instance_id");
                lead.registrado = true;

                return Response.ok(lead).build();
            }

        } catch (SQLException e) {
            return respuestaErrorBD(e, "buscar el lead");
        }
    }

    // Datos recibidos para cerrar un lead
    public static class CierreLead {
        public Boolean compro;
        public String motivoNoCompra;
        public String comentarioNoCompra;
    }

    // Cerrar lead y crear cliente si hubo compra
    @PUT
    @Path("{id}/cierre")
    public Response cerrarLead(
            @PathParam("id") int id,
            CierreLead cierre) {

        if (id < 1) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error",
                            "El ID del lead debe ser mayor que cero"))
                    .build();
        }

        if (cierre == null || cierre.compro == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error",
                            "El campo compro es obligatorio"))
                    .build();
        }

        if (!cierre.compro &&
                (cierre.motivoNoCompra == null ||
                 cierre.motivoNoCompra.isBlank())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error",
                            "Debe indicar el motivo de no compra"))
                    .build();
        }

        String motivoCompleto = null;

        if (!cierre.compro) {
            motivoCompleto = cierre.motivoNoCompra.trim();

            if (cierre.comentarioNoCompra != null &&
                    !cierre.comentarioNoCompra.isBlank()) {
                motivoCompleto += " - Comentario: "
                        + cierre.comentarioNoCompra.trim();
            }

            if (motivoCompleto.length() > 255) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error",
                                "El motivo y comentario no pueden superar 255 caracteres"))
                        .build();
            }
        }

        String actualizarLead = """
            UPDATE dbo.Leads
            SET estado = ?,
                motivo_no_compra = ?
            OUTPUT INSERTED.id_lead,
                   INSERTED.nombre,
                   INSERTED.telefono,
                   INSERTED.correo,
                   INSERTED.estado,
                   INSERTED.motivo_no_compra
            WHERE id_lead = ?
            """;

        String buscarCliente = """
            SELECT id_cliente
            FROM dbo.Clientes WITH (UPDLOCK, HOLDLOCK)
            WHERE lead_origen_id = ?
            """;

        String insertarCliente = """
            INSERT INTO dbo.Clientes
                (nombre, telefono, correo, acepta_comunicaciones,
                 lead_origen_id)
            OUTPUT INSERTED.id_cliente
            VALUES (?, ?, ?, 0, ?)
            """;

        try (Connection connection = Database.getConnection()) {

            connection.setAutoCommit(false);

            try {
                int idCliente = 0;

                String estado = cierre.compro
                        ? "Convertido" : "No convertido";

                String nombre;
                String telefono;
                String correo;
                String motivo;

                // Actualizar lead
                try (PreparedStatement statement =
                             connection.prepareStatement(actualizarLead)) {

                    statement.setString(1, estado);
                    statement.setString(2, motivoCompleto);
                    statement.setInt(3, id);

                    try (ResultSet rs = statement.executeQuery()) {
                        if (!rs.next()) {
                            connection.rollback();

                            return Response.status(
                                    Response.Status.NOT_FOUND)
                                    .entity(Map.of("error",
                                            "Lead no encontrado"))
                                    .build();
                        }

                        nombre = rs.getString("nombre");
                        telefono = rs.getString("telefono");
                        correo = rs.getString("correo");
                        estado = rs.getString("estado");
                        motivo = rs.getString("motivo_no_compra");
                    }
                }

                // Si compró, crear o recuperar el cliente
                if (cierre.compro) {

                    try (PreparedStatement statement =
                                 connection.prepareStatement(buscarCliente)) {

                        statement.setInt(1, id);

                        try (ResultSet rs = statement.executeQuery()) {
                            if (rs.next()) {
                                idCliente = rs.getInt("id_cliente");
                            }
                        }
                    }

                    // Evitar duplicar clientes del mismo lead
                    if (idCliente == 0) {
                        try (PreparedStatement statement =
                                     connection.prepareStatement(insertarCliente)) {

                            statement.setString(1, nombre);
                            statement.setString(2, telefono);
                            statement.setString(3, correo);
                            statement.setInt(4, id);

                            try (ResultSet rs = statement.executeQuery()) {
                                if (rs.next()) {
                                    idCliente = rs.getInt("id_cliente");
                                } else {
                                    throw new SQLException(
                                            "No se pudo crear el cliente");
                                }
                            }
                        }
                    }
                }

                connection.commit();

                Map<String, Object> respuesta =
                        new LinkedHashMap<>();

                respuesta.put("idLead", id);
                respuesta.put("estado", estado);
                respuesta.put("motivoNoCompra",
                        motivo == null ? "" : motivo);

                if (cierre.compro) {
                    respuesta.put("idCliente", idCliente);
                    respuesta.put("aceptaComunicaciones", false);
                    respuesta.put("mensaje",
                            "Lead convertido y cliente registrado correctamente");
                } else {
                    respuesta.put("mensaje",
                            "Lead cerrado sin compra");
                }

                return Response.ok(respuesta).build();

            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackError) {
                    System.err.println(
                            "[WS] Error al realizar rollback: "
                            + rollbackError.getMessage());
                    e.addSuppressed(rollbackError);
                }

                throw e;

            } finally {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println(
                            "[WS] Error al restaurar autoCommit: "
                            + e.getMessage());
                }
            }

        } catch (SQLException e) {
            return respuestaErrorBD(e,
                    "cerrar el lead y registrar el resultado");
        }
    }
}

