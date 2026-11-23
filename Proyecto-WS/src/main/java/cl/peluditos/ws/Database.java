
package cl.peluditos.ws;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static final String HOST =
            System.getenv().getOrDefault("PELUDITOS_DB_HOST", "localhost");

    private static final String PORT =
            System.getenv().getOrDefault("PELUDITOS_DB_PORT", "1433");

    private static final String DATABASE =
            System.getenv().getOrDefault("PELUDITOS_DB_NAME", "DB_PeluditosPetChile");

    private static final String USER =
            System.getenv().getOrDefault("PELUDITOS_DB_USER", "peluditos_java");

    private static final String PASSWORD =
            System.getenv("PELUDITOS_DB_PASSWORD");

    private static final String URL =
            "jdbc:sqlserver://" + HOST + ":" + PORT + ";"
            + "databaseName=" + DATABASE + ";"
            + "encrypt=true;"
            + "trustServerCertificate=true;";

    public static Connection getConnection() throws SQLException {

        if (PASSWORD == null || PASSWORD.isBlank()) {
            throw new SQLException(
                    "No se ha configurado la variable PELUDITOS_DB_PASSWORD."
            );
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {

        try (Connection connection = getConnection()) {

            System.out.println("¡CONEXION EXITOSA CON SQL SERVER!");
            System.out.println("Base de datos: " + connection.getCatalog());

        } catch (SQLException e) {

            System.out.println("ERROR DE CONEXIÓN:");
            System.out.println(e.getMessage());
            System.out.println("Código SQL: " + e.getErrorCode());
            System.out.println("Estado SQL: " + e.getSQLState());
        }
    }
}
