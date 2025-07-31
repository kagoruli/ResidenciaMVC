/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author luis
 */

import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PropuestaDatos {
    
    public static ObservableList<Propuesta> obtenerPropuestasParaRevision() throws SQLException {
        ObservableList<Propuesta> propuestas = FXCollections.observableArrayList();
        String sql = "SELECT p.*, per.nombre_persona || ' ' || per.apellido_paterno_persona AS nombre_estudiante, " +
                     "a.titulo_anteproyecto AS titulo_anteproyecto " +
                     "FROM propuesta p " +
                     "JOIN estudiante e ON p.id_estudiante = e.numero_control " +
                     "JOIN persona per ON e.id_persona = per.id_persona " +
                     "JOIN anteproyecto a ON p.id_anteproyecto = a.id_anteproyecto " +
                     "WHERE p.estado IN ('Pendiente', 'Corregir')";
        
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Propuesta propuesta = new Propuesta();
                propuesta.setIdPropuesta(rs.getInt("id_propuesta"));
                propuesta.setIdEstudiante(rs.getString("id_estudiante"));
                propuesta.setIdAnteproyecto(rs.getInt("id_anteproyecto"));
                propuesta.setRutaArchivo(rs.getString("ruta_archivo"));
                propuesta.setEstado(rs.getString("estado"));
                propuesta.setComentarios(rs.getString("comentarios"));
                propuesta.setFechaEntrega(rs.getDate("fecha_entrega"));
                
                propuesta.setNombreEstudiante(rs.getString("nombre_estudiante"));
                propuesta.setTituloAnteproyecto(rs.getString("titulo_anteproyecto"));
                
                propuestas.add(propuesta);
            }
        }
        return propuestas;
    }
    
    public static boolean actualizarEstadoPropuesta(int idPropuesta, String estado, String comentarios) {
        String sql = "UPDATE propuesta SET estado = ?, comentarios = ? WHERE id_propuesta = ?";
        
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, estado);
            pstmt.setString(2, comentarios);
            pstmt.setInt(3, idPropuesta);
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
/**    
    public static boolean crearOActualizarPropuesta(Propuesta propuesta) throws SQLException {
        String sql = "INSERT INTO propuesta (id_estudiante, id_anteproyecto, ruta_archivo, estado, fecha_entrega) " +
                     "VALUES (?, ?, ?, ?, ?) " +
                     "ON CONFLICT (id_estudiante) DO UPDATE SET " +
                     "ruta_archivo = EXCLUDED.ruta_archivo, " +
                     "estado = EXCLUDED.estado, " +
                     "fecha_entrega = EXCLUDED.fecha_entrega";
        
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, propuesta.getIdEstudiante());
            pstmt.setInt(2, propuesta.getIdAnteproyecto());
            pstmt.setString(3, propuesta.getRutaArchivo());
            pstmt.setString(4, propuesta.getEstado());
            pstmt.setDate(5, propuesta.getFechaEntrega());
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
        }
    }*/
    
    public static String obtenerEstadoPropuesta(String idEstudiante) throws SQLException {
        String sql = "SELECT estado FROM propuesta WHERE id_estudiante = ?";
        
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, idEstudiante);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("estado");
                }
            }
        }
        return null;
    }
/**    
    public static Propuesta obtenerPropuestaPorEstudiante(String idEstudiante) throws SQLException {
    String sql = "SELECT * FROM propuesta WHERE id_estudiante = ?";
    
    try (Connection conn = ConexionDB.conectar();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, idEstudiante);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                Propuesta propuesta = new Propuesta();
                propuesta.setIdPropuesta(rs.getInt("id_propuesta"));
                propuesta.setIdEstudiante(rs.getString("id_estudiante"));
                propuesta.setIdAnteproyecto(rs.getInt("id_anteproyecto"));
                propuesta.setRutaArchivo(rs.getString("ruta_archivo"));
                propuesta.setEstado(rs.getString("estado"));
                propuesta.setComentarios(rs.getString("comentarios"));
                propuesta.setFechaEntrega(rs.getDate("fecha_entrega"));
                return propuesta;
            }
        }
    }
    return null;
}*/
    
    // Busca la propuesta actual (PDF anteproyecto) de un estudiante
    public static Propuesta obtenerPropuestaPorEstudiante(String idEstudiante) throws SQLException {
        String sql = "SELECT * FROM propuesta WHERE id_estudiante = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idEstudiante);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Propuesta p = new Propuesta();
                p.setIdPropuesta(rs.getInt("id_propuesta"));
                p.setIdEstudiante(rs.getString("id_estudiante"));
                p.setIdAnteproyecto(rs.getInt("id_anteproyecto"));
                p.setRutaArchivo(rs.getString("ruta_archivo"));
                p.setEstado(rs.getString("estado"));
                p.setComentarios(rs.getString("comentarios"));
                p.setFechaEntrega(rs.getDate("fecha_entrega"));
                return p;
            }
        }
        return null;
    }

    // Inserta o actualiza la propuesta
    public static boolean crearOActualizarPropuesta(Propuesta p) throws SQLException {
        String sqlSelect = "SELECT id_propuesta FROM propuesta WHERE id_estudiante = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement psSelect = conn.prepareStatement(sqlSelect)) {
            psSelect.setString(1, p.getIdEstudiante());
            ResultSet rs = psSelect.executeQuery();
            if (rs.next()) {
                // Ya existe: actualizar
                String sqlUpdate = "UPDATE propuesta SET ruta_archivo=?, estado=?, comentarios=?, fecha_entrega=?, id_anteproyecto=? WHERE id_estudiante=?";
                try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate)) {
                    psUpdate.setString(1, p.getRutaArchivo());
                    psUpdate.setString(2, p.getEstado());
                    psUpdate.setString(3, p.getComentarios());
                    psUpdate.setDate(4, p.getFechaEntrega());
                    psUpdate.setInt(5, p.getIdAnteproyecto());
                    psUpdate.setString(6, p.getIdEstudiante());
                    return psUpdate.executeUpdate() > 0;
                }
            } else {
                // Insertar nuevo
                String sqlInsert = "INSERT INTO propuesta (id_estudiante, id_anteproyecto, ruta_archivo, estado, comentarios, fecha_entrega) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement psInsert = conn.prepareStatement(sqlInsert)) {
                    psInsert.setString(1, p.getIdEstudiante());
                    psInsert.setInt(2, p.getIdAnteproyecto());
                    psInsert.setString(3, p.getRutaArchivo());
                    psInsert.setString(4, p.getEstado());
                    psInsert.setString(5, p.getComentarios());
                    psInsert.setDate(6, p.getFechaEntrega());
                    return psInsert.executeUpdate() > 0;
                }
            }
        }
    }
}