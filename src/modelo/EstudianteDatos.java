/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstudianteDatos {

    // Inserta un nuevo estudiante, retorna true si tuvo éxito
    public static boolean insertarEstudiante(Estudiante estudiante) throws SQLException {
        String sql = "INSERT INTO estudiante (numero_control, id_persona) VALUES (?, ?)";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estudiante.getNumeroControl());
            ps.setInt(2, estudiante.getIdPersona());
            int filas = ps.executeUpdate();
            return filas > 0;
        }
    }
    
    public static List<EstudianteVista> obtenerEstudiantesVista(String estadoFiltro) {
        List<EstudianteVista> lista = new ArrayList<>();
        String sql = "SELECT e.numero_control, p.nombre_persona, p.apellido_paterno_persona, " +
            "p.apellido_materno_persona, p.correo_persona, e.estado " +
            "FROM estudiante e " +
            "JOIN persona p ON e.id_persona = p.id_persona " +
            (estadoFiltro == null ? "" : "WHERE e.estado = ? ") +
            "ORDER BY p.apellido_paterno_persona";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (estadoFiltro != null) {
                stmt.setString(1, estadoFiltro);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(new EstudianteVista(
                    rs.getString("numero_control"),
                    rs.getString("nombre_persona"),
                    rs.getString("apellido_paterno_persona"),
                    rs.getString("apellido_materno_persona"),
                    rs.getString("correo_persona"),
                    rs.getString("estado")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public static EstudianteVista buscarEstudianteVista(String numeroControl) {
        String sql = "SELECT e.numero_control, p.nombre_persona, p.apellido_paterno_persona, " +
            "p.apellido_materno_persona, p.correo_persona, e.estado " +
            "FROM estudiante e " +
            "JOIN persona p ON e.id_persona = p.id_persona " +
            "WHERE e.numero_control = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numeroControl);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new EstudianteVista(
                    rs.getString("numero_control"),
                    rs.getString("nombre_persona"),
                    rs.getString("apellido_paterno_persona"),
                    rs.getString("apellido_materno_persona"),
                    rs.getString("correo_persona"),
                    rs.getString("estado")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean cambiarEstadoEstudiante(String numeroControl, String nuevoEstado) {
        String sql = "UPDATE estudiante SET estado = ? WHERE numero_control = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nuevoEstado);
            stmt.setString(2, numeroControl);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static int actualizarEstudianteCompleto(String numeroControlOriginal, String nuevoNumeroControl, String nombre, String paterno, String materno, String correo) {
    int idPersona = -1;
    String query = "SELECT id_persona FROM estudiante WHERE numero_control = ?";
    try (Connection conn = ConexionDB.conectar();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, numeroControlOriginal);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            idPersona = rs.getInt("id_persona");
        } else {
            return 0; // No existe el estudiante original
        }
    } catch (Exception e) {
        e.printStackTrace();
        return 0;
    }

    try (Connection conn = ConexionDB.conectar()) {
        conn.setAutoCommit(false);

        // Primero, actualiza el número de control si cambió
        if (!numeroControlOriginal.equals(nuevoNumeroControl)) {
            String sqlUpdateNC = "UPDATE estudiante SET numero_control = ? WHERE numero_control = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateNC)) {
                stmt.setString(1, nuevoNumeroControl);
                stmt.setString(2, numeroControlOriginal);
                stmt.executeUpdate();
            }
        }

        // Segundo, actualiza los datos de persona
        String sqlUpdatePersona = "UPDATE persona SET nombre_persona = ?, apellido_paterno_persona = ?, apellido_materno_persona = ?, correo_persona = ? WHERE id_persona = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sqlUpdatePersona)) {
            stmt.setString(1, nombre);
            stmt.setString(2, paterno);
            stmt.setString(3, materno);
            stmt.setString(4, correo);
            stmt.setInt(5, idPersona);
            stmt.executeUpdate();
        }

        conn.commit();
        return 1; // Éxito
    } catch (java.sql.SQLException ex) {
        String msg = ex.getMessage().toLowerCase();
        if (msg.contains("correo_persona_key") || (msg.contains("llave duplicada") && msg.contains("correo_persona"))) {
            return -1; // Correo duplicado
        } else if (msg.contains("numero_control") || (msg.contains("llave duplicada") && msg.contains("numero_control"))) {
            return -2; // Número de control duplicado
        }
        ex.printStackTrace();
        return 0;
    }
}

    public static boolean tieneAnteproyectoAsignado(String numeroControl) throws SQLException {
        String sql = "SELECT id_anteproyecto FROM estudiante WHERE numero_control = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, numeroControl);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt("id_anteproyecto") != 0;
        }
    }
    
    public static int obtenerIdAnteproyecto(String numeroControl) throws SQLException {
    String sql = "SELECT id_anteproyecto FROM estudiante WHERE numero_control = ?";
    try (Connection conn = ConexionDB.conectar();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, numeroControl);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("id_anteproyecto");
        }
        return 0;
    }
    }
    
    public static boolean actualizarAnteproyectoEstudiante(String numeroControl, int idAnteproyecto) throws SQLException {
        String sql = "UPDATE estudiante SET id_anteproyecto = ? WHERE numero_control = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAnteproyecto);
            ps.setString(2, numeroControl);
            return ps.executeUpdate() > 0;
        }
    }
}
