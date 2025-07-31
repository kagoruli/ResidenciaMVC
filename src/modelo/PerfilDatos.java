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

public class PerfilDatos {
    public static Perfil obtenerPerfilPorUsuario(String usuarioLogueado) {
        Perfil perfil = null;
        String sql = "SELECT p.nombre_persona, p.apellido_paterno_persona, p.apellido_materno_persona, " +
                "p.correo_persona, r.nombre_rol " +
                "FROM persona p " +
                "JOIN usuario u ON p.id_usuario = u.id_usuario " +
                "JOIN rol r ON u.id_rol = r.id_rol " +
                "WHERE u.nombre_usuario = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuarioLogueado);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String nombreCompleto = rs.getString("nombre_persona") + " " +
                                       rs.getString("apellido_paterno_persona") + " " +
                                       rs.getString("apellido_materno_persona");
                String correo = rs.getString("correo_persona");
                String rol = rs.getString("nombre_rol");
                String icono = rol.equalsIgnoreCase("estudiante") ? "estudiante" : "docente";
                perfil = new Perfil();
                perfil.setNombreCompleto(nombreCompleto);
                perfil.setUsuario(usuarioLogueado);
                perfil.setCorreo(correo);
                perfil.setRol(rol);
                perfil.setIcono(icono);
            }
        } catch (Exception ex) {
            System.err.println("Error obteniendo datos de perfil: " + ex.getMessage());
        }
        return perfil;
    }
}
