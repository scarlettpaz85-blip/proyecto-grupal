
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

@Path("leads/{id}/seguimientos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SeguimientoResource {

    public static class Seguimiento {
        public String medio;
        public String comentario;
        public String resultado;
    }

    // CONSULTAR LOS SEGUIMIENTOS DE UN LEAD
    @GET
    public Response consultar(
            @PathParam("id") int id) {

        String verificarLead =
                "SELECT id_lead FROM dbo.Leads WHERE id_lead = ?";

        String consulta = """
                SELECT id_seguimiento,
                       id_lead,
                       fecha,
                       medio,
                       comentario,
                       resultado
                FROM dbo.Seguimientos
                WHERE id_lead = ?
                ORDER BY fecha DESC, id_seguimiento DESC
                """;

        try (Connection connection = Database.getConnection()) {

            // Comprobar que el lead existe
            try (PreparedStatement verificar =
                         connection.prepareStatement(verificarLead)) {

                verificar.setInt(1, id);

                try (ResultSet rs = verificar.executeQuery()) {
                    if (!rs.next()) {
                        return Response.status(Response.Status.NOT_FOUND)
                                .entity(Map.of(
                                        "error", "Lead no encontrado"))
                                .build();
                    }
                }
            }

            // Consultar los seguimientos
            List<Map<String, Object>> seguimientos =
                    new ArrayList<>();

            try (PreparedStatement statement =
                         connection.prepareStatement(consulta)) {

                statement.setInt(1, id);

                try (ResultSet rs = statement.executeQuery()) {

                    while (rs.next()) {

                        Map<String, Object> seguimiento =
                                new LinkedHashMap<>();

                        seguimiento.put(
                                "id_seguimiento",
                                rs.getInt("id_seguimiento"));

                        seguimiento.put(
                                "id_lead",
                                rs.getInt("id_lead"));

                        seguimiento.put(
                                "fecha",
                                rs.getTimestamp("fecha") == null
                                        ? null
                                        : rs.getTimestamp("fecha").toString());

                        seguimiento.put(
                                "medio",
                                rs.getString("medio"));

                        seguimiento.put(
                                "comentario",
                                rs.getString("comentario"));

                        seguimiento.put(
                                "resultado",
                                rs.getString("resultado"));

                        seguimientos.add(seguimiento);
                    }
                }
            }

            return Response.ok(seguimientos).build();

        } catch (SQLException e) {

            System.err.println(
                    "[WS] Error al consultar seguimientos: "
                            + e.getMessage());

            return Response.serverError()
                    .entity(Map.of(
                            "error",
                            "No fue posible consultar los seguimientos"))
                    .build();
        }
    }

    // REGISTRAR UN NUEVO SEGUIMIENTO
    @POST
    public Response registrar(
            @PathParam("id") int id,
            Seguimiento seguimiento) {

        if (seguimiento == null ||
                seguimiento.medio == null ||
                seguimiento.medio.isBlank()) {

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                            "error",
                            "El campo medio es obligatorio"))
                    .build();
        }

        String verificarLead =
                "SELECT id_lead FROM dbo.Leads WHERE id_lead = ?";

        String insertar = """
                INSERT INTO dbo.Seguimientos
                    (id_lead, medio, comentario, resultado)
                OUTPUT INSERTED.id_seguimiento,
                       INSERTED.fecha
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = Database.getConnection()) {

            // Comprobar que el lead existe
            try (PreparedStatement verificar =
                         connection.prepareStatement(verificarLead)) {

                verificar.setInt(1, id);

                try (ResultSet rs = verificar.executeQuery()) {
                    if (!rs.next()) {
                        return Response.status(Response.Status.NOT_FOUND)
                                .entity(Map.of(
                                        "error", "Lead no encontrado"))
                                .build();
                    }
                }
            }

            // Insertar seguimiento
            try (PreparedStatement statement =
                         connection.prepareStatement(insertar)) {

                statement.setInt(1, id);
                statement.setString(2, seguimiento.medio);
                statement.setString(3, seguimiento.comentario);
                statement.setString(4, seguimiento.resultado);

                try (ResultSet rs = statement.executeQuery()) {

                    if (rs.next()) {

                        Map<String, Object> respuesta =
                                new LinkedHashMap<>();

                        respuesta.put(
                                "id_seguimiento",
                                rs.getInt("id_seguimiento"));

                        respuesta.put("id_lead", id);

                        respuesta.put(
                                "fecha",
                                rs.getTimestamp("fecha").toString());

                        respuesta.put(
                                "medio",
                                seguimiento.medio);

                        respuesta.put(
                                "comentario",
                                seguimiento.comentario);

                        respuesta.put(
                                "resultado",
                                seguimiento.resultado);

                        return Response.status(Response.Status.CREATED)
                                .entity(respuesta)
                                .build();
                    }
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "[WS] Error al registrar seguimiento: "
                            + e.getMessage());

            return Response.serverError()
                    .entity(Map.of(
                            "error",
                            "No fue posible registrar el seguimiento"))
                    .build();
        }

        return Response.serverError()
                .entity(Map.of(
                        "error",
                        "No se pudo confirmar el seguimiento"))
                .build();
    }
}
