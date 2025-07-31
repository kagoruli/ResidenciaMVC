/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author jafet
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    private static final String URL = "jdbc:postgresql://localhost:5432/residencia7";
    private static final String USER = "postgres";
    private static final String PASSWORD = "1234";

    public static Connection conectar() {
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            // Mensaje de éxito
            System.out.println("Conexión exitosa a la base de datos.");
            return conn;
        } catch (ClassNotFoundException e) {
            System.out.println("Error: Driver JDBC no encontrado");
            System.out.println("Descarga el driver desde: https://jdbc.postgresql.org/download/");
            return null;
        } catch (SQLException e) {
            System.out.println("Error de conexión SQL:");
            System.out.println("Mensaje: " + e.getMessage());
            System.out.println("Código SQL: " + e.getSQLState());
            System.out.println("Código de error PostgreSQL: " + e.getErrorCode());
            return null;
        }
    }
}
