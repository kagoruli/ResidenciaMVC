/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PropuestaPropiaDAO {

    // Registra una nueva propuesta propia
    public static boolean registrarPropuesta(PropuestaPropia propuesta) throws SQLException {
        String sql = "INSERT INTO propuesta (id_estudiante, id_anteproyecto, ruta_archivo, estado, comentarios, fecha_entrega) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, propuesta.getIdEstudiante());
            // Permitir null
            if (propuesta.getIdAnteproyecto() == null || propuesta.getIdAnteproyecto() == 0) {
                ps.setNull(2, java.sql.Types.INTEGER);
            } else {
                ps.setInt(2, propuesta.getIdAnteproyecto());
            }
            ps.setString(3, propuesta.getRutaArchivo());
            ps.setString(4, propuesta.getEstado());
            ps.setString(5, propuesta.getComentarios());
            ps.setDate(6, propuesta.getFechaEntrega());
            return ps.executeUpdate() == 1;
        }
    }

    // Obtiene la propuesta propia por estudiante
    public static PropuestaPropia obtenerPorEstudiante(String idEstudiante) throws SQLException {
        String sql = "SELECT * FROM propuesta WHERE id_estudiante = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idEstudiante);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PropuestaPropia propuesta = new PropuestaPropia();
                    propuesta.setIdPropuesta(rs.getInt("id_propuesta"));
                    propuesta.setIdEstudiante(rs.getString("id_estudiante"));
                    // Permitir null usando getObject
                    propuesta.setIdAnteproyecto((Integer) rs.getObject("id_anteproyecto"));
                    propuesta.setRutaArchivo(rs.getString("ruta_archivo"));
                    propuesta.setEstado(rs.getString("estado"));
                    propuesta.setComentarios(rs.getString("comentarios"));
                    propuesta.setFechaEntrega(rs.getDate("fecha_entrega"));
                    return propuesta;
                }
            }
        }
        return null;
    }

    // Actualiza estado y comentarios de una propuesta
    public static boolean actualizarEstadoYComentarios(int idPropuesta, String estado, String comentarios) throws SQLException {
        String sql = "UPDATE propuesta SET estado = ?, comentarios = ? WHERE id_propuesta = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setString(2, comentarios);
            ps.setInt(3, idPropuesta);
            return ps.executeUpdate() == 1;
        }
    }

    // Obtiene todas las propuestas para Jefatura
    public static List<PropuestaPropia> obtenerTodas() throws SQLException {
        List<PropuestaPropia> lista = new ArrayList<>();
        String sql = "SELECT * FROM propuesta";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PropuestaPropia propuesta = new PropuestaPropia();
                propuesta.setIdPropuesta(rs.getInt("id_propuesta"));
                propuesta.setIdEstudiante(rs.getString("id_estudiante"));
                propuesta.setIdAnteproyecto((Integer) rs.getObject("id_anteproyecto")); // <- Permitir null
                propuesta.setRutaArchivo(rs.getString("ruta_archivo"));
                propuesta.setEstado(rs.getString("estado"));
                propuesta.setComentarios(rs.getString("comentarios"));
                propuesta.setFechaEntrega(rs.getDate("fecha_entrega"));
                lista.add(propuesta);
            }
        }
        return lista;
    }
    
    public static boolean actualizarRutaArchivo(int idPropuesta, String ruta) throws SQLException {
        String sql = "UPDATE propuesta SET ruta_archivo = ? WHERE id_propuesta = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ruta);
            ps.setInt(2, idPropuesta);
            return ps.executeUpdate() == 1;
        }
    }
    
    public static boolean actualizarPropuesta(PropuestaPropia propuesta) throws SQLException {
        String sql = "UPDATE propuesta SET ruta_archivo=?, estado=?, comentarios=?, fecha_entrega=? WHERE id_propuesta=?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, propuesta.getRutaArchivo());
            ps.setString(2, propuesta.getEstado());
            ps.setString(3, propuesta.getComentarios());
            ps.setDate(4, propuesta.getFechaEntrega());
            ps.setInt(5, propuesta.getIdPropuesta());
            return ps.executeUpdate() == 1;
        }
    }
}
