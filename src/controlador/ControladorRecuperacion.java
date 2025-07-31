/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

/**
 *
 * @author jafet
 */
import java.sql.*;
import java.util.Random;
import modelo.ConexionDB;
import modelo.Utilidades; // Para hashSHA256
import modelo.EnviadorCorreo;

public class ControladorRecuperacion {

    public static String recuperarUsuarioYContrasena(String correo) {
        String sql = "SELECT u.nombre_usuario, u.id_usuario FROM usuario u " +
                     "INNER JOIN persona p ON u.id_usuario = p.id_usuario " +
                     "WHERE LOWER(p.correo_persona) = LOWER(?) LIMIT 1";
        if (!correo.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            return "Formato de correo inválido";
        }
        
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, correo.trim());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String usuario = rs.getString("nombre_usuario");
                int idUsuario = rs.getInt("id_usuario");
                // Generar nueva contraseña aleatoria
                String nuevaContrasena = String.format("%06d", new Random().nextInt(999999));
                String hashContrasena = Utilidades.hashSHA256(nuevaContrasena);

                // Actualiza la nueva contraseña en la BD
                String sqlUpdate = "UPDATE usuario SET contrasena_usuario = ? WHERE id_usuario = ?";
                try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate)) {
                    psUpdate.setString(1, hashContrasena);
                    psUpdate.setInt(2, idUsuario);
                    psUpdate.executeUpdate();
                }

                // Envía el correo
                EnviadorCorreo.enviarCorreoRecuperacion(
                    correo,
                    usuario,
                    nuevaContrasena
                );
                return "Se ha enviado tu usuario y una nueva contraseña al correo electrónico registrado.";

            } else {
                return "No existe ningún usuario vinculado a ese correo electrónico. Intente nuevamente.";
            }
        } catch (SQLException ex) {
            return "Error de base de datos: " + ex.getMessage();
        }
    }
}
