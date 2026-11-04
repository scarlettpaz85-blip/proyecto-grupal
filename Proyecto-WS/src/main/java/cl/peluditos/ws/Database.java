package cl.peluditos.ws;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

 private static final String URL =
    "jdbc:sqlserver://localhost:1433;"
    + "databaseName=DB_PeluditosPetChile;"
    + "encrypt=true;"
    + "trustServerCertificate=true;";   

    private static final String USER = "peluditos_java";

    private static final String PASSWORD =
        System.getenv("PELUDITOS_DB_PASSWORD");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        try (Connection connection = getConnection()) {

            System.out.println("¡CONEXION EXITOSA CON SQL SERVER!");
            System.out.println("Base de datos: "
                    + connection.getCatalog());

        } catch (SQLException e) {

            System.out.println("ERROR DE CONEXIÓN:");
            System.out.println(e.getMessage());
            System.out.println("Código SQL: " + e.getErrorCode());
            System.out.println("Estado SQL: " + e.getSQLState());
        }
    }
}