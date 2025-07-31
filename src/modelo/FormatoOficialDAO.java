/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FormatoOficialDAO {

    public static boolean insertarFormato(FormatoOficial formato) throws SQLException {
        String sql = "INSERT INTO formato_oficial (tipo, ruta_pdf, fecha_subida) VALUES (?, ?, ?)";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, formato.getTipo());
            ps.setString(2, formato.getRutaPDF());
            ps.setDate(3, formato.getFechaSubida());
            return ps.executeUpdate() == 1;
        }
    }

    public static List<FormatoOficial> obtenerTodos() throws SQLException {
        String sql = "SELECT * FROM formato_oficial ORDER BY fecha_subida DESC";
        List<FormatoOficial> lista = new ArrayList<>();
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                FormatoOficial f = new FormatoOficial();
                f.setIdFormato(rs.getInt("id_formato"));
                f.setTipo(rs.getString("tipo"));
                f.setRutaPDF(rs.getString("ruta_pdf"));
                f.setFechaSubida(rs.getDate("fecha_subida"));
                lista.add(f);
            }
        }
        return lista;
    }

    public static FormatoOficial obtenerPorTipo(String tipo) throws SQLException {
        String sql = "SELECT * FROM formato_oficial WHERE tipo = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tipo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new FormatoOficial(
                        rs.getInt("id_formato"),
                        rs.getString("tipo"),
                        rs.getString("ruta_pdf"),
                        rs.getDate("fecha_subida")
                    );
                }
            }
        }
        return null;
    }
    
    public static String obtenerRutaPorTipo(String tipo) throws SQLException {
        String sql = "SELECT ruta_pdf FROM formato_oficial WHERE LOWER(tipo) = LOWER(?)";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tipo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("ruta_pdf");
            }
        }
        return null;
    }
    
    public static FormatoOficial obtenerFormatoSolicitudResidencia() throws SQLException {
        return obtenerPorTipo("solicitud_residencia"); // O el valor correcto según tu tabla
    }

    // Puedes agregar métodos para eliminar, actualizar, etc, si lo necesitas
}
