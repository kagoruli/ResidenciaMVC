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

public class PersonaDatos {

    // Inserta una nueva persona y devuelve el ID generado
    public static int insertarPersona(Persona persona) throws SQLException {
        String sql = "INSERT INTO persona (nombre_persona, apellido_paterno_persona, apellido_materno_persona, correo_persona, id_usuario) VALUES (?, ?, ?, ?, ?) RETURNING id_persona";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, persona.getNombre());
            ps.setString(2, persona.getApellidoPaterno());
            ps.setString(3, persona.getApellidoMaterno());
            ps.setString(4, persona.getCorreo());
            ps.setInt(5, persona.getIdUsuario());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_persona");
                }
            }
        }
        throw new SQLException("No se pudo insertar la persona.");
    }
}
