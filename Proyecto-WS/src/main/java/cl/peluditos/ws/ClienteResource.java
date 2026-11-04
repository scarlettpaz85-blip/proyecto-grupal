
package cl.peluditos.ws;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

@Path("clientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClienteResource {

    public static class Cliente {
        public String nombre;
        public String telefono;
        public String correo;
        public String comuna;
        public Boolean aceptaComunicaciones;
        public Integer leadOrigenId;
    }

    @POST
    public Response registrar(Cliente cliente) {

        if (cliente == null || cliente.nombre == null
                || cliente.nombre.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error",
                            "El nombre es obligatorio"))
                    .build();
        }

        String sql = """
            INSERT INTO dbo.Clientes
                (nombre, telefono, correo, comuna,
                 acepta_comunicaciones, lead_origen_id)
            OUTPUT INSERTED.id_cliente,
                   INSERTED.fecha_registro
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, cliente.nombre);
            statement.setString(2, cliente.telefono);
            statement.setString(3, cliente.correo);
            statement.setString(4, cliente.comuna);

            statement.setBoolean(
                    5,
                    Boolean.TRUE.equals(cliente.aceptaComunicaciones)
            );

            if (cliente.leadOrigenId == null) {
                statement.setNull(6, java.sql.Types.INTEGER);
            } else {
                statement.setInt(6, cliente.leadOrigenId);
            }

            try (ResultSet rs = statement.executeQuery()) {

                if (rs.next()) {
                    return Response.status(Response.Status.CREATED)
                            .entity(Map.of(
                                    "id_cliente",
                                    rs.getInt("id_cliente"),
                                    "nombre", cliente.nombre,
                                    "telefono",
                                    cliente.telefono == null
                                            ? "" : cliente.telefono,
                                    "correo",
                                    cliente.correo == null
                                            ? "" : cliente.correo,
                                    "comuna",
                                    cliente.comuna == null
                                            ? "" : cliente.comuna,
                                    "acepta_comunicaciones",
                                    Boolean.TRUE.equals(
                                            cliente.aceptaComunicaciones),
                                    "lead_origen_id",
                                    cliente.leadOrigenId == null
                                            ? 0 : cliente.leadOrigenId,
                                    "fecha_registro",
                                    rs.getTimestamp("fecha_registro")
                                            .toString()
                            ))
                            .build();
                }
            }

        } catch (SQLException e) {
            System.err.println("[WS] Error al registrar cliente: "
                    + e.getMessage());

            return Response.serverError()
                    .entity(Map.of("error",
                            "No fue posible registrar el cliente"))
                    .build();
        }

        return Response.serverError()
                .entity(Map.of("error",
                        "No se pudo confirmar el registro"))
                .build();
    }
}
