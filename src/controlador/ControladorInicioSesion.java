/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

/**
 *
 * @author jafet
 */


import modelo.ConexionDB;
import modelo.Utilidades;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ControladorInicioSesion {
    public static String validarCredenciales(String matricula, String password) {
        String sql = "SELECT u.nombre_usuario, u.contrasena_usuario, r.nombre_rol FROM usuario u " +
                     "JOIN rol r ON u.id_rol = r.id_rol " +
                     "WHERE u.nombre_usuario = ? AND u.contrasena_usuario = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, matricula);
            // Hashear la contraseña antes de comparar:
            String hash = Utilidades.hashSHA256(password);
            pstmt.setString(2, hash);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String rol = rs.getString("nombre_rol");

                    // NUEVO: Verifica estado según rol
                    String estado = null;
                    if ("estudiante".equalsIgnoreCase(rol)) {
                        String sqlEstado = "SELECT e.estado FROM estudiante e " +
                                "JOIN persona p ON e.id_persona = p.id_persona " +
                                "JOIN usuario u ON p.id_usuario = u.id_usuario " +
                                "WHERE u.nombre_usuario = ?";
                        try (PreparedStatement ps = conn.prepareStatement(sqlEstado)) {
                            ps.setString(1, matricula);
                            ResultSet rsEstado = ps.executeQuery();
                            if (rsEstado.next()) {
                                estado = rsEstado.getString("estado");
                            }
                        }
                    } else if ("docente".equalsIgnoreCase(rol)) {
                        String sqlEstado = "SELECT d.estado FROM docente d " +
                                "JOIN persona p ON d.id_persona = p.id_persona " +
                                "JOIN usuario u ON p.id_usuario = u.id_usuario " +
                                "WHERE u.nombre_usuario = ?";
                        try (PreparedStatement ps = conn.prepareStatement(sqlEstado)) {
                            ps.setString(1, matricula);
                            ResultSet rsEstado = ps.executeQuery();
                            if (rsEstado.next()) {
                                estado = rsEstado.getString("estado");
                            }
                        }
                    }

                    // Si el usuario está dado de baja o finalizado, retorna señal especial
                    if ("BAJA".equalsIgnoreCase(estado)) {
                        return "ESTADO_BAJA";
                    }
                    if ("FINALIZADO".equalsIgnoreCase(estado)) {
                        return "ESTADO_FINALIZADO";
                    }

                    return rol; // Si pasa, retorna el rol y entra normalmente
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al validar credenciales: " + e.getMessage());
        }
        return null; // Si no se encontró el usuario o las credenciales son incorrectas
    }
}

/**
import modelo.ConexionDB;
import modelo.Utilidades;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ControladorInicioSesion {
    public static boolean validarCredenciales(String matricula, String password) {
        String hash = Utilidades.hashSHA256(password);

        System.out.println("=== Depuración de Login ===");
        System.out.println("Usuario: " + matricula);
        System.out.println("Contraseña ingresada: " + password);
        System.out.println("Hash SHA-256 generado: " + hash);

        String sql = "SELECT nombre_usuario, contrasena_usuario FROM usuario WHERE nombre_usuario = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, matricula);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String usuarioBD = rs.getString("nombre_usuario");
                    String hashBD = rs.getString("contrasena_usuario");

                    System.out.println("Usuario en BD: " + usuarioBD);
                    System.out.println("Hash en BD:    " + hashBD);

                    if (hash.equals(hashBD)) {
                        System.out.println("¡Login exitoso!");
                        return true;
                    } else {
                        System.out.println("Hash generado NO coincide con hash en BD.");
                        return false;
                    }
                } else {
                    System.out.println("Usuario no encontrado en la base de datos.");
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al validar credenciales: " + e.getMessage());
            return false;
        }
    }
}*/
