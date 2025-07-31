/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class VerAnteproyectoDocenteDatos {
    public static ObservableList<VerAnteproyectoDocente> obtenerProyectosPorDocente(String matricula, String filtroRol) {
        ObservableList<VerAnteproyectoDocente> lista = FXCollections.observableArrayList();
        String sql =
            "SELECT aar.id_anteproyecto, ap.titulo_anteproyecto, ap.es_propuesta, aar.rol " +
            "FROM asignacion_asesor_revisor aar " +
            "JOIN anteproyecto ap ON aar.id_anteproyecto = ap.id_anteproyecto " +
            "WHERE aar.matricula_docente = ? ";
        if (filtroRol != null && !filtroRol.equals("Todos")) {
            sql += " AND aar.rol = ? ";
        }
        sql += " ORDER BY ap.titulo_anteproyecto";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, matricula);
            if (filtroRol != null && !filtroRol.equals("Todos")) {
                ps.setString(2, filtroRol.toUpperCase()); // "ASESOR" o "REVISOR"
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String modalidad = rs.getBoolean("es_propuesta") ? "Propuesta Propia" : "Banco de Anteproyectos";
                lista.add(new VerAnteproyectoDocente(
                    String.valueOf(rs.getInt("id_anteproyecto")),
                    rs.getString("titulo_anteproyecto"),
                    modalidad,
                    rs.getString("rol")
                ));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return lista;
    }
}

