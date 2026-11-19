
package cl.peluditos.ws;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Path("fechas-relevantes")
@Produces(MediaType.APPLICATION_JSON)
public class FechaRelevanteResource {

    public static class FechaRelevante {
        public int idFechaRelevante;
        public String nombre;
        public String tipo;
        public String fecha;
        public int diasAnticipacion;
        public String segmento;
        public boolean activa;
    }

    // Detectar errores de conexión con SQL Server
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

    // Consultar fechas relevantes activas
    @GET
    public Response listarActivas() {

        String sql = """
            SELECT id_fecha_relevante, nombre, tipo,
                   fecha, dias_anticipacion, segmento, activa
            FROM dbo.FechasRelevantes
            WHERE activa = 1
            ORDER BY fecha
            """;

        List<FechaRelevante> fechas = new ArrayList<>();

        try (Connection connection = Database.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                FechaRelevante fecha = new FechaRelevante();

                fecha.idFechaRelevante =
                        rs.getInt("id_fecha_relevante");
                fecha.nombre = rs.getString("nombre");
                fecha.tipo = rs.getString("tipo");

                if (rs.getDate("fecha") != null) {
                    fecha.fecha = rs.getDate("fecha").toString();
                }

                fecha.diasAnticipacion =
                        rs.getInt("dias_anticipacion");
                fecha.segmento = rs.getString("segmento");
                fecha.activa = rs.getBoolean("activa");

                fechas.add(fecha);
            }

            return Response.ok(fechas).build();

        } catch (SQLException e) {

            System.err.println(
                    "[WS] Error al listar fechas relevantes: "
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
                            "Ocurrió un error interno al listar las fechas relevantes"))
                    .build();
        }
    }
}

