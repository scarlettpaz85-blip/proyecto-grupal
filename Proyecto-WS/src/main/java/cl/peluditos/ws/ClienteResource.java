
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

    // CONSULTAR TODOS LOS CLIENTES
    @GET
    public Response consultarTodos() {

        String sql = """
                SELECT id_cliente, nombre, telefono, correo,
                       comuna, acepta_comunicaciones,
                       fecha_registro, lead_origen_id
                FROM dbo.Clientes
                ORDER BY id_cliente DESC
                """;

        List<Map<String, Object>> clientes = new ArrayList<>();

        try (Connection connection = Database.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                clientes.add(mapearCliente(rs));
            }

            return Response.ok(clientes).build();

        } catch (SQLException e) {
            System.err.println(
                    "[WS] Error al consultar clientes: " + e.getMessage());

            return Response.serverError()
                    .entity(Map.of(
                            "error", "No fue posible consultar los clientes"))
                    .build();
        }
    }

    // CONSULTAR UN CLIENTE POR ID
    @GET
    @Path("{id}")
    public Response consultarPorId(@PathParam("id") int id) {

        if (id <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "El ID debe ser mayor que cero"))
                    .build();
        }

        String sql = """
                SELECT id_cliente, nombre, telefono, correo,
                       comuna, acepta_comunicaciones,
                       fecha_registro, lead_origen_id
                FROM dbo.Clientes
                WHERE id_cliente = ?
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return Response.status(Response.Status.NOT_FOUND)
                            .entity(Map.of("error", "Cliente no encontrado"))
                            .build();
                }

                return Response.ok(mapearCliente(rs)).build();
            }

        } catch (SQLException e) {
            System.err.println(
                    "[WS] Error al consultar cliente: " + e.getMessage());

            return Response.serverError()
                    .entity(Map.of(
                            "error", "No fue posible consultar el cliente"))
                    .build();
        }
    }

    // REGISTRAR UN CLIENTE
    @POST
    public Response registrar(Cliente cliente) {

        if (cliente == null || cliente.nombre == null
                || cliente.nombre.isBlank()) {

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "El nombre es obligatorio"))
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
                    Boolean.TRUE.equals(cliente.aceptaComunicaciones));

            if (cliente.leadOrigenId == null) {
                statement.setNull(6, java.sql.Types.INTEGER);
            } else {
                statement.setInt(6, cliente.leadOrigenId);
            }

            try (ResultSet rs = statement.executeQuery()) {

                if (rs.next()) {
                    Map<String, Object> respuesta =
                            new LinkedHashMap<>();

                    respuesta.put("id_cliente",
                            rs.getInt("id_cliente"));
                    respuesta.put("nombre", cliente.nombre);
                    respuesta.put("telefono", cliente.telefono);
                    respuesta.put("correo", cliente.correo);
                    respuesta.put("comuna", cliente.comuna);
                    respuesta.put("acepta_comunicaciones",
                            Boolean.TRUE.equals(
                                    cliente.aceptaComunicaciones));
                    respuesta.put("lead_origen_id",
                            cliente.leadOrigenId);
                    respuesta.put("fecha_registro",
                            rs.getTimestamp("fecha_registro").toString());

                    return Response.status(Response.Status.CREATED)
                            .entity(respuesta)
                            .build();
                }
            }

        } catch (SQLException e) {
            System.err.println(
                    "[WS] Error al registrar cliente: " + e.getMessage());

            return Response.serverError()
                    .entity(Map.of(
                            "error", "No fue posible registrar el cliente"))
                    .build();
        }

        return Response.serverError()
                .entity(Map.of("error", "No se pudo confirmar el registro"))
                .build();
    }

    // CONVERTIR UNA FILA SQL EN UNA RESPUESTA JSON
    private Map<String, Object> mapearCliente(ResultSet rs)
            throws SQLException {

        Map<String, Object> cliente = new LinkedHashMap<>();

        cliente.put("id_cliente", rs.getInt("id_cliente"));
        cliente.put("nombre", rs.getString("nombre"));
        cliente.put("telefono", rs.getString("telefono"));
        cliente.put("correo", rs.getString("correo"));
        cliente.put("comuna", rs.getString("comuna"));
        cliente.put("acepta_comunicaciones",
                rs.getBoolean("acepta_comunicaciones"));

        cliente.put("fecha_registro",
                rs.getTimestamp("fecha_registro") == null
                        ? null
                        : rs.getTimestamp("fecha_registro").toString());

        int leadOrigenId = rs.getInt("lead_origen_id");

        cliente.put("lead_origen_id",
                rs.wasNull() ? null : leadOrigenId);

        return cliente;
    }
}


