
package cl.peluditos.ws;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Path("oportunidades")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OportunidadResource {

    // Datos recibidos para generar una campaña
    public static class GenerarRequest {
        public Integer idFechaRelevante;
        public String processInstanceId;
    }

    // Datos recibidos al registrar el contacto
    public static class ResultadoRequest {
        public Boolean interesado;
        public String comentarioContacto;
    }

    // Información que se devuelve al consultar oportunidades
    public static class Oportunidad {
        public int idOportunidad;
        public int idCliente;
        public String cliente;
        public String telefono;
        public String mascota;
        public String fechaRelevante;
        public String estado;
        public String resultado;
    }

    // Detectar errores de conexión SQL Server
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

    // Respuesta segura para errores de base de datos
    private Response respuestaErrorBD(SQLException e, String operacion) {

        System.err.println("[WS] Error al " + operacion + ": "
                + e.getMessage());

        if (esErrorConexion(e)) {
            return Response.status(
                    Response.Status.SERVICE_UNAVAILABLE)
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

    // Ejecutar rollback sin ocultar el error original
    private void realizarRollback(Connection connection,
                                   SQLException errorOriginal) {
        try {
            connection.rollback();
        } catch (SQLException rollbackError) {
            System.err.println(
                    "[WS] Error al realizar rollback: "
                    + rollbackError.getMessage());

            errorOriginal.addSuppressed(rollbackError);
        }
    }

    // POST /api/oportunidades/generar
    @POST
    @Path("generar")
    public Response generar(GenerarRequest request) {

        if (request == null || request.idFechaRelevante == null
                || request.idFechaRelevante <= 0
                || request.processInstanceId == null
                || request.processInstanceId.isBlank()) {

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error",
                            "Debe indicar idFechaRelevante y processInstanceId"))
                    .build();
        }

        String validarFecha = """
            SELECT COUNT(*)
            FROM dbo.FechasRelevantes
            WHERE id_fecha_relevante = ?
              AND activa = 1
            """;

        String insertar = """
            INSERT INTO dbo.OportunidadesComerciales
                (id_cliente, id_fecha_relevante,
                 id_mascota, estado, fecha_generacion,
                 process_instance_id)
            SELECT
                c.id_cliente,
                f.id_fecha_relevante,
                m.id_mascota,
                'Generada',
                SYSDATETIME(),
                ?
            FROM dbo.Clientes c
            INNER JOIN dbo.FechasRelevantes f
                ON f.id_fecha_relevante = ?
            OUTER APPLY (
                SELECT TOP 1
                    ma.id_mascota
                FROM dbo.Mascotas ma
                WHERE ma.id_cliente = c.id_cliente
                ORDER BY ma.id_mascota
            ) m
            WHERE c.acepta_comunicaciones = 1
              AND f.activa = 1
              AND NOT EXISTS (
                  SELECT 1
                  FROM dbo.OportunidadesComerciales o
                  WHERE o.id_cliente = c.id_cliente
                    AND o.id_fecha_relevante = f.id_fecha_relevante
              )
            """;

        try (Connection connection = Database.getConnection()) {

            connection.setAutoCommit(false);

            try {
                // Validar fecha relevante
                try (PreparedStatement validar =
                             connection.prepareStatement(validarFecha)) {

                    validar.setInt(1, request.idFechaRelevante);

                    try (ResultSet rs = validar.executeQuery()) {
                        rs.next();

                        if (rs.getInt(1) == 0) {
                            connection.rollback();

                            return Response.status(
                                    Response.Status.NOT_FOUND)
                                    .entity(Map.of("error",
                                            "La fecha relevante no existe o está inactiva"))
                                    .build();
                        }
                    }
                }

                // Generar oportunidades sin duplicar clientes
                try (PreparedStatement statement =
                             connection.prepareStatement(insertar)) {

                    statement.setString(1, request.processInstanceId);
                    statement.setInt(2, request.idFechaRelevante);

                    statement.executeUpdate();
                }

                // Consultar antes de confirmar la transacción
                List<Oportunidad> oportunidades =
                        consultarGeneradas(
                                connection,
                                request.idFechaRelevante,
                                request.processInstanceId);

                // Confirmar solamente si todas las operaciones resultaron bien
                connection.commit();

                Map<String, Object> respuesta = new LinkedHashMap<>();
                respuesta.put("total", oportunidades.size());
                respuesta.put("oportunidades", oportunidades);

                return Response.status(Response.Status.CREATED)
                        .entity(respuesta)
                        .build();

            } catch (SQLException e) {
                realizarRollback(connection, e);
                throw e;
            }

        } catch (SQLException e) {
            return respuestaErrorBD(e, "generar las oportunidades");
        }
    }

    // Consultar las oportunidades generadas dentro de una campaña
    private List<Oportunidad> consultarGeneradas(
            Connection connection,
            int idFechaRelevante,
            String processInstanceId) throws SQLException {

        String sql = """
            SELECT
                o.id_oportunidad,
                c.id_cliente,
                c.nombre AS cliente,
                c.telefono,
                COALESCE(m.nombre, '') AS mascota,
                f.nombre AS fecha_relevante,
                o.estado,
                o.resultado
            FROM dbo.OportunidadesComerciales o
            INNER JOIN dbo.Clientes c
                ON c.id_cliente = o.id_cliente
            INNER JOIN dbo.FechasRelevantes f
                ON f.id_fecha_relevante = o.id_fecha_relevante
            LEFT JOIN dbo.Mascotas m
                ON m.id_mascota = o.id_mascota
            WHERE o.id_fecha_relevante = ?
              AND o.process_instance_id = ?
            ORDER BY o.id_oportunidad
            """;

        List<Oportunidad> oportunidades = new ArrayList<>();

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, idFechaRelevante);
            statement.setString(2, processInstanceId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Oportunidad oportunidad = new Oportunidad();

                    oportunidad.idOportunidad =
                            rs.getInt("id_oportunidad");
                    oportunidad.idCliente =
                            rs.getInt("id_cliente");
                    oportunidad.cliente =
                            rs.getString("cliente");
                    oportunidad.telefono =
                            rs.getString("telefono");
                    oportunidad.mascota =
                            rs.getString("mascota");
                    oportunidad.fechaRelevante =
                            rs.getString("fecha_relevante");
                    oportunidad.estado =
                            rs.getString("estado");
                    oportunidad.resultado =
                            rs.getString("resultado");

                    oportunidades.add(oportunidad);
                }
            }
        }

        return oportunidades;
    }

    // GET /api/oportunidades?idFechaRelevante=1
    @GET
    public Response listar(
            @QueryParam("idFechaRelevante") Integer idFechaRelevante) {

        if (idFechaRelevante != null && idFechaRelevante <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error",
                            "El identificador debe ser mayor a cero"))
                    .build();
        }

        String sql = """
            SELECT
                o.id_oportunidad,
                c.id_cliente,
                c.nombre AS cliente,
                c.telefono,
                COALESCE(m.nombre, '') AS mascota,
                f.nombre AS fecha_relevante,
                o.estado,
                o.resultado
            FROM dbo.OportunidadesComerciales o
            INNER JOIN dbo.Clientes c
                ON c.id_cliente = o.id_cliente
            INNER JOIN dbo.FechasRelevantes f
                ON f.id_fecha_relevante = o.id_fecha_relevante
            LEFT JOIN dbo.Mascotas m
                ON m.id_mascota = o.id_mascota
            WHERE (? IS NULL OR o.id_fecha_relevante = ?)
            ORDER BY o.id_oportunidad
            """;

        List<Oportunidad> oportunidades = new ArrayList<>();

        try (Connection connection = Database.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            if (idFechaRelevante == null) {
                statement.setNull(1, Types.INTEGER);
                statement.setNull(2, Types.INTEGER);
            } else {
                statement.setInt(1, idFechaRelevante);
                statement.setInt(2, idFechaRelevante);
            }

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Oportunidad oportunidad = new Oportunidad();

                    oportunidad.idOportunidad =
                            rs.getInt("id_oportunidad");
                    oportunidad.idCliente =
                            rs.getInt("id_cliente");
                    oportunidad.cliente =
                            rs.getString("cliente");
                    oportunidad.telefono =
                            rs.getString("telefono");
                    oportunidad.mascota =
                            rs.getString("mascota");
                    oportunidad.fechaRelevante =
                            rs.getString("fecha_relevante");
                    oportunidad.estado =
                            rs.getString("estado");
                    oportunidad.resultado =
                            rs.getString("resultado");

                    oportunidades.add(oportunidad);
                }
            }

            return Response.ok(oportunidades).build();

        } catch (SQLException e) {
            return respuestaErrorBD(e, "listar las oportunidades");
        }
    }

    // PUT /api/oportunidades/{id}/resultado
    @PUT
    @Path("{id}/resultado")
    public Response registrarResultado(
            @PathParam("id") int id,
            ResultadoRequest request) {

        if (id <= 0 || request == null
                || request.interesado == null) {

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error",
                            "Debe indicar un ID válido y el campo interesado"))
                    .build();
        }

        String sql = """
            UPDATE dbo.OportunidadesComerciales
            SET estado = ?,
                resultado = ?
            OUTPUT INSERTED.id_oportunidad,
                   INSERTED.estado,
                   INSERTED.resultado
            WHERE id_oportunidad = ?
            """;

        String estado = request.interesado
                ? "Ganada" : "Sin compra";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, estado);
            statement.setString(2, request.comentarioContacto);
            statement.setInt(3, id);

            try (ResultSet rs = statement.executeQuery()) {

                if (!rs.next()) {
                    return Response.status(
                            Response.Status.NOT_FOUND)
                            .entity(Map.of("error",
                                    "Oportunidad no encontrada"))
                            .build();
                }

                String resultado = rs.getString("resultado");

                Map<String, Object> respuesta = new LinkedHashMap<>();
                respuesta.put("idOportunidad",
                        rs.getInt("id_oportunidad"));
                respuesta.put("estado", rs.getString("estado"));
                respuesta.put("resultado",
                        resultado == null ? "" : resultado);

                return Response.ok(respuesta).build();
            }

        } catch (SQLException e) {
            return respuestaErrorBD(e, "registrar el resultado");
        }
    }
}

