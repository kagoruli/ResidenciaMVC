/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package modelo;

import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class EntregaAnteproyectoDatos {

    public static EntregaAnteproyecto obtenerPorEstudiante(String idEstudiante) throws SQLException {
        String sql = "SELECT * FROM entrega_anteproyecto WHERE id_estudiante = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idEstudiante);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                EntregaAnteproyecto ea = new EntregaAnteproyecto();
                ea.setIdEntrega(rs.getInt("id_entrega"));
                ea.setIdEstudiante(rs.getString("id_estudiante"));
                ea.setRutaArchivo(rs.getString("ruta_archivo"));
                ea.setEstado(rs.getString("estado"));
                ea.setComentarios(rs.getString("comentarios"));
                ea.setFechaEntrega(rs.getDate("fecha_entrega"));
                return ea;
            }
        }
        return null;
    }

    public static boolean insertar(EntregaAnteproyecto ea) throws SQLException {
        String sql = "INSERT INTO entrega_anteproyecto (id_estudiante, ruta_archivo, estado, comentarios, fecha_entrega) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ea.getIdEstudiante());
            ps.setString(2, ea.getRutaArchivo());
            ps.setString(3, ea.getEstado());
            ps.setString(4, ea.getComentarios());
            ps.setDate(5, ea.getFechaEntrega());
            return ps.executeUpdate() > 0;
        }
    }

    public static boolean actualizar(EntregaAnteproyecto ea) throws SQLException {
        String sql = "UPDATE entrega_anteproyecto SET ruta_archivo = ?, estado = ?, comentarios = ?, fecha_entrega = ? WHERE id_estudiante = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ea.getRutaArchivo());
            ps.setString(2, ea.getEstado());
            ps.setString(3, ea.getComentarios());
            ps.setDate(4, ea.getFechaEntrega());
            ps.setString(5, ea.getIdEstudiante());
            return ps.executeUpdate() > 0;
        }
    }
    // Consulta general con JOIN para la tabla de revisión (incluye modalidad y título)
    public static ObservableList<EntregaAnteproyecto> obtenerTodas(String filtroEstado, String filtroModalidad) throws SQLException {
        ObservableList<EntregaAnteproyecto> lista = FXCollections.observableArrayList();

        String sql =
            "SELECT ea.*, ap.titulo_anteproyecto, ap.es_propuesta " +
            "FROM entrega_anteproyecto ea " +
            "JOIN estudiante es ON ea.id_estudiante = es.numero_control " +
            "JOIN anteproyecto ap ON es.id_anteproyecto = ap.id_anteproyecto " +
            "WHERE 1=1 ";

        if (filtroEstado != null && !filtroEstado.equalsIgnoreCase("Todos")) {
            sql += " AND ea.estado = ? ";
        }
        if (filtroModalidad != null && !filtroModalidad.equalsIgnoreCase("Todos")) {
            if (filtroModalidad.equalsIgnoreCase("Banco de Anteproyectos"))
                sql += " AND ap.es_propuesta = false ";
            else if (filtroModalidad.equalsIgnoreCase("Propuesta Propia"))
                sql += " AND ap.es_propuesta = true ";
        }
        sql += "ORDER BY ea.fecha_entrega DESC";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int idx = 1;
            if (filtroEstado != null && !filtroEstado.equalsIgnoreCase("Todos")) {
                ps.setString(idx++, filtroEstado);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                EntregaAnteproyecto ea = new EntregaAnteproyecto();
                ea.setIdEntrega(rs.getInt("id_entrega"));
                ea.setIdEstudiante(rs.getString("id_estudiante"));
                ea.setRutaArchivo(rs.getString("ruta_archivo"));
                ea.setEstado(rs.getString("estado"));
                ea.setComentarios(rs.getString("comentarios"));
                ea.setFechaEntrega(rs.getDate("fecha_entrega"));
                ea.setTituloProyecto(rs.getString("titulo_anteproyecto"));
                ea.setModalidad(rs.getBoolean("es_propuesta") ? "Propuesta Propia" : "Banco de Anteproyectos");
                lista.add(ea);
            }
        }
        return lista;
    }

    // Actualiza estado y comentarios
    public static boolean actualizarEstado(int idEntrega, String nuevoEstado, String comentarios) throws SQLException {
        String sql = "UPDATE entrega_anteproyecto SET estado = ?, comentarios = ? WHERE id_entrega = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setString(2, comentarios);
            ps.setInt(3, idEntrega);
            return ps.executeUpdate() > 0;
        }
    }
    
    public static Integer obtenerIdAnteproyectoPorIdEntrega(int idEntrega) throws SQLException {
        String sql = "SELECT ap.id_anteproyecto " +
                     "FROM entrega_anteproyecto ea " +
                     "JOIN estudiante es ON ea.id_estudiante = es.numero_control " +
                     "JOIN anteproyecto ap ON es.id_anteproyecto = ap.id_anteproyecto " +
                     "WHERE ea.id_entrega = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEntrega);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_anteproyecto");
            }
        }
        return null; // Si no se encontró
    }

}