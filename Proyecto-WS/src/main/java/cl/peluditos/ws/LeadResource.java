
package cl.peluditos.ws;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Path("leads")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LeadResource {

    // Registrar un lead en SQL Server
    @POST
    public Response registrar(Lead lead) {

        if (lead == null || lead.nombre == null
                || lead.nombre.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error",
                            "El campo 'nombre' es obligatorio"))
                    .build();
        }

        if (lead.canal == null || lead.canal.isBlank()
                || lead.interes == null || lead.interes.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error",
                            "Los campos 'canal' e 'interes' son obligatorios"))
                    .build();
        }

        String sql = """
            INSERT INTO dbo.Leads
                (nombre, telefono, correo, canal,
                 producto_interes, estado)
            OUTPUT INSERTED.id_lead, INSERTED.fecha_registro
            VALUES (?, ?, ?, ?, ?, 'Nuevo')
            """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, lead.nombre);
            statement.setString(2, lead.telefono);
            statement.setString(3, lead.correo);
            statement.setString(4, lead.canal);
            statement.setString(5, lead.interes);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    lead.id = rs.getInt("id_lead");
                    lead.estado = "Nuevo";
                    lead.registrado = true;
                    lead.fechaRegistro =
                            rs.getTimestamp("fecha_registro")
                                    .toLocalDateTime().toString();
                }
            }

            System.out.println("[WS] Lead guardado en SQL Server: "
                    + lead.id);

            return Response.status(Response.Status.CREATED)
                    .entity(lead)
                    .build();

        } catch (SQLException e) {
            System.err.println("[WS] Error al registrar lead: "
                    + e.getMessage());

            return Response.serverError()
                    .entity(Map.of("error",
                            "No fue posible registrar el lead"))
                    .build();
        }
    }

    // Listar los leads desde SQL Server con paginación
    @GET
    public Response listar(
            @DefaultValue("1") @QueryParam("page") int page,
            @DefaultValue("10") @QueryParam("size") int size) {

        // Diagnóstico temporal de paginación
        System.out.println("[PAGINACION] page=" + page
                + ", size=" + size);

        if (page < 1 || size < 1 || size > 100) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error",
                            "La página debe ser mayor a 0 y el tamaño debe estar entre 1 y 100"))
                    .build();
        }

        List<Lead> leads = new ArrayList<>();

        long offset = (long) (page - 1) * size;

        String sql = """
            SELECT id_lead, nombre, telefono, correo,
                   canal, producto_interes, estado, fecha_registro
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
                    lead.fechaRegistro =
                            rs.getTimestamp("fecha_registro")
                                    .toLocalDateTime().toString();
                    lead.registrado = true;

                    leads.add(lead);
                }
            }

            return Response.ok(leads).build();

        } catch (SQLException e) {
            System.err.println("[WS] Error al listar leads: "
                    + e.getMessage());

            return Response.serverError()
                    .entity(Map.of("error",
                            "No fue posible listar los leads"))
                    .build();
        }
    }

    // Buscar un lead por su ID
    @GET
    @Path("{id}")
    public Response obtener(@PathParam("id") int id) {

        String sql = """
            SELECT id_lead, nombre, telefono, correo,
                   canal, producto_interes, estado, fecha_registro
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
                lead.fechaRegistro =
                        rs.getTimestamp("fecha_registro")
                                .toLocalDateTime().toString();
                lead.registrado = true;

                return Response.ok(lead).build();
            }

        } catch (SQLException e) {
            System.err.println("[WS] Error al buscar lead: "
                    + e.getMessage());

            return Response.serverError()
                    .entity(Map.of("error",
                            "No fue posible buscar el lead"))
                    .build();
        }
    }

    // Datos recibidos para cerrar un lead
    public static class CierreLead {
        public Boolean compro;
        public String motivoNoCompra;
    }

    // Cerrar un lead según el resultado de la venta
    @PUT
    @Path("{id}/cierre")
    public Response cerrarLead(
            @PathParam("id") int id,
            CierreLead cierre) {

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

        String sql = """
            UPDATE dbo.Leads
            SET estado = CASE
                    WHEN ? = 1 THEN 'Convertido'
                    ELSE 'No convertido'
                END,
                motivo_no_compra = CASE
                    WHEN ? = 1 THEN NULL
                    ELSE ?
                END
            OUTPUT INSERTED.id_lead,
                   INSERTED.estado,
                   INSERTED.motivo_no_compra
            WHERE id_lead = ?
            """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setBoolean(1, cierre.compro);
            statement.setBoolean(2, cierre.compro);
            statement.setString(3, cierre.motivoNoCompra);
            statement.setInt(4, id);

            try (ResultSet rs = statement.executeQuery()) {

                if (!rs.next()) {
                    return Response.status(Response.Status.NOT_FOUND)
                            .entity(Map.of("error",
                                    "Lead no encontrado"))
                            .build();
                }

                String estado = rs.getString("estado");
                String motivo = rs.getString("motivo_no_compra");

                return Response.ok(Map.of(
                        "id", rs.getInt("id_lead"),
                        "estado", estado,
                        "motivoNoCompra",
                        motivo == null ? "" : motivo
                )).build();
            }

        } catch (SQLException e) {
            System.err.println("[WS] Error al cerrar lead: "
                    + e.getMessage());

            return Response.serverError()
                    .entity(Map.of("error",
                            "No fue posible cerrar el lead"))
                    .build();
        }
    }
}
