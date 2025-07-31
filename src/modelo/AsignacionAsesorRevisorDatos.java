/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package modelo;

import java.sql.*;
import java.util.*;

public class AsignacionAsesorRevisorDatos {
    // Inserta una asignación de asesor y revisores
    public static boolean insertarAsignacion(int idAnteproyecto, String matriculaAsesor, List<String> matriculasRevisores) throws SQLException {
        // CORRECCIÓN: Usar columna correcta (matricula_docente)
        String sql = "INSERT INTO asignacion_asesor_revisor (id_anteproyecto, matricula_docente, rol) VALUES (?, ?, ?)";
        
        try (Connection conn = ConexionDB.conectar()) {
            conn.setAutoCommit(false); // Para asegurar transaccionalidad

            // Insertar Asesor
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idAnteproyecto);
                ps.setString(2, matriculaAsesor);
                ps.setString(3, "ASESOR");
                ps.executeUpdate();
            }

            // Insertar Revisores
            for (String matriculaRevisor : matriculasRevisores) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, idAnteproyecto);
                    ps.setString(2, matriculaRevisor);
                    ps.setString(3, "REVISOR");
                    ps.executeUpdate();
                }
            }

            conn.commit(); // Confirma todo
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    // Consulta si ya existe una asignación para ese anteproyecto
    public static boolean yaAsignado(int idAnteproyecto) throws SQLException {
        String sql = "SELECT COUNT(*) FROM asignacion_asesor_revisor WHERE id_anteproyecto = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAnteproyecto);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        }
        return false;
    }
}