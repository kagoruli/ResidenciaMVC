/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author jafet
 */
import java.sql.*;

public class UsuarioDatos {

    // Inserta un nuevo usuario y devuelve el ID generado
    public static int insertarUsuario(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuario (nombre_usuario, contrasena_usuario, id_rol) VALUES (?, ?, ?) RETURNING id_usuario";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombreUsuario());
            ps.setString(2, usuario.getContrasena()); // Aquí debe ir ya hasheada
            ps.setInt(3, usuario.getIdRol());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_usuario");
                }
            }
        }
        throw new SQLException("No se pudo insertar el usuario.");
    }
    public static String obtenerRol(String usuario) {
        String rol = null;
        String sql = "SELECT r.nombre_rol FROM usuario u " +
                     "JOIN rol r ON u.id_rol = r.id_rol " +
                     "WHERE u.nombre_usuario = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                rol = rs.getString("nombre_rol");
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener rol: " + e.getMessage());
        }

        return rol;
    }
}
