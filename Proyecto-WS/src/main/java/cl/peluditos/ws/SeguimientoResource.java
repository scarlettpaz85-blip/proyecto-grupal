package cl.peluditos.ws;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    @POST
    public Response registrar(
            @PathParam("id") int id,
            Seguimiento seguimiento) {

        if (seguimiento == null ||
                seguimiento.medio == null ||
                seguimiento.medio.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error",
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

            try (PreparedStatement verificar =
                    connection.prepareStatement(verificarLead)) {

                verificar.setInt(1, id);

                try (ResultSet rs = verificar.executeQuery()) {
                    if (!rs.next()) {
                        return Response.status(Response.Status.NOT_FOUND)
                                .entity(Map.of("error",
                                        "Lead no encontrado"))
                                .build();
                    }
                }
            }

            try (PreparedStatement statement =
                    connection.prepareStatement(insertar)) {

                statement.setInt(1, id);
                statement.setString(2, seguimiento.medio);
                statement.setString(3, seguimiento.comentario);
                statement.setString(4, seguimiento.resultado);

                try (ResultSet rs = statement.executeQuery()) {

                    if (rs.next()) {
                        return Response.status(Response.Status.CREATED)
                                .entity(Map.of(
                                        "id_seguimiento",
                                        rs.getInt("id_seguimiento"),
                                        "id_lead", id,
                                        "fecha",
                                        rs.getTimestamp("fecha").toString(),
                                        "medio", seguimiento.medio,
                                        "comentario",
                                        seguimiento.comentario == null
                                                ? "" : seguimiento.comentario,
                                        "resultado",
                                        seguimiento.resultado == null
                                                ? "" : seguimiento.resultado
                                ))
                                .build();
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println(
                    "[WS] Error al registrar seguimiento: "
                            + e.getMessage());

            return Response.serverError()
                    .entity(Map.of("error",
                            "No fue posible registrar el seguimiento"))
                    .build();
        }

        return Response.serverError()
                .entity(Map.of("error",
                        "No se pudo confirmar el seguimiento"))
                .build();
    }
}