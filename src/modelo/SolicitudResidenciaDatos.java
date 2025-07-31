/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SolicitudResidenciaDatos {

    public static SolicitudResidencia obtenerPorEstudiante(String idEstudiante) throws SQLException {
        String sql = "SELECT * FROM solicitud_residencia WHERE id_estudiante = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idEstudiante);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                SolicitudResidencia s = new SolicitudResidencia();
                s.setIdSolicitud(rs.getInt("id_solicitud"));
                s.setIdEstudiante(rs.getString("id_estudiante"));
                s.setRutaPdf(rs.getString("ruta_pdf"));
                s.setEstatus(rs.getString("estatus"));
                s.setFechaEnvio(rs.getDate("fecha_envio"));
                return s;
            }
        }
        return null;
    }

    public static boolean insertar(SolicitudResidencia s) throws SQLException {
        String sql = "INSERT INTO solicitud_residencia (id_estudiante, ruta_pdf, estatus, fecha_envio) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getIdEstudiante());
            ps.setString(2, s.getRutaPdf());
            ps.setString(3, s.getEstatus());
            ps.setDate(4, s.getFechaEnvio());
            return ps.executeUpdate() > 0;
        }
    }

    public static boolean actualizar(SolicitudResidencia s) throws SQLException {
        String sql = "UPDATE solicitud_residencia SET ruta_pdf = ?, estatus = ?, fecha_envio = ? WHERE id_estudiante = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getRutaPdf());
            ps.setString(2, s.getEstatus());
            ps.setDate(3, s.getFechaEnvio());
            ps.setString(4, s.getIdEstudiante());
            return ps.executeUpdate() > 0;
        }
    }

    public static boolean marcarRecibido(int idSolicitud) throws SQLException {
    String sql = "UPDATE solicitud_residencia SET estatus = 'RECIBIDO' WHERE id_solicitud = ?";
    try (Connection conn = ConexionDB.conectar();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, idSolicitud);
        return ps.executeUpdate() > 0;
    }
}


    public static List<SolicitudResidencia> obtenerRecibidasPorAnteproyecto(int idAnteproyecto) throws SQLException {
        String sql = "SELECT s.* FROM solicitud_residencia s JOIN estudiante e ON s.id_estudiante = e.numero_control WHERE s.estatus = 'Recibido' AND e.id_anteproyecto = ?";
        List<SolicitudResidencia> lista = new ArrayList<>();
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAnteproyecto);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                SolicitudResidencia s = new SolicitudResidencia();
                s.setIdSolicitud(rs.getInt("id_solicitud"));
                s.setIdEstudiante(rs.getString("id_estudiante"));
                s.setRutaPdf(rs.getString("ruta_pdf"));
                s.setEstatus(rs.getString("estatus"));
                s.setFechaEnvio(rs.getDate("fecha_envio"));
                lista.add(s);
            }
        }
        return lista;
    }

    public static boolean existeSolicitudRecibida(String idEstudiante) {
        String sql = "SELECT COUNT(*) FROM solicitud_residencia WHERE id_estudiante = ? AND estatus = 'RECIBIDO'";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idEstudiante);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static int insertarRetornarID(SolicitudResidencia solicitud) throws SQLException {
    String sql = "INSERT INTO solicitud_residencia (id_estudiante, ruta_pdf, estatus, fecha_envio) VALUES (?, ?, ?, ?) RETURNING id_solicitud";
    try (Connection conn = ConexionDB.conectar();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, solicitud.getIdEstudiante());
        ps.setString(2, ""); // Ruta vacía temporal
        ps.setString(3, solicitud.getEstatus());
        ps.setDate(4, solicitud.getFechaEnvio());
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        }
    }
    return 0;
}

    public static boolean actualizarRutaPdf(SolicitudResidencia solicitud) throws SQLException {
    String sql = "UPDATE solicitud_residencia SET ruta_pdf = ? WHERE id_solicitud = ?";
    try (Connection conn = ConexionDB.conectar();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, solicitud.getRutaPdf());
        ps.setInt(2, solicitud.getIdSolicitud());
        return ps.executeUpdate() > 0;
    }
}
    
    public static List<SolicitudResidencia> obtenerTodas() throws SQLException {
    String sql = "SELECT * FROM solicitud_residencia ORDER BY fecha_envio DESC";
    List<SolicitudResidencia> lista = new ArrayList<>();
    try (Connection conn = ConexionDB.conectar();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            SolicitudResidencia s = new SolicitudResidencia();
            s.setIdSolicitud(rs.getInt("id_solicitud"));
            s.setIdEstudiante(rs.getString("id_estudiante"));
            s.setRutaPdf(rs.getString("ruta_pdf"));
            s.setEstatus(rs.getString("estatus"));
            s.setFechaEnvio(rs.getDate("fecha_envio"));
            lista.add(s);
        }
    }
    return lista;
}

}
