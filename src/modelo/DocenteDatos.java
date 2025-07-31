/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocenteDatos {

    // Inserta un nuevo docente, retorna true si tuvo éxito
    public static boolean insertarDocente(Docente docente) throws SQLException {
        String sql = "INSERT INTO docente (matricula, id_persona) VALUES (?, ?)";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, docente.getMatricula());
            ps.setInt(2, docente.getIdPersona());
            int filas = ps.executeUpdate();
            return filas > 0;
        }
    }
    
    
    public static List<DocenteVista> obtenerDocenteVista(String estadoFiltro) {
        List<DocenteVista> lista = new ArrayList<>();
        String sql = "SELECT d.matricula, p.nombre_persona, p.apellido_paterno_persona, " +
            "p.apellido_materno_persona, p.correo_persona, d.estado " +
            "FROM docente d " +
            "JOIN persona p ON d.id_persona = p.id_persona " +
            (estadoFiltro == null ? "" : "WHERE d.estado = ? ") +
            "ORDER BY p.apellido_paterno_persona";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (estadoFiltro != null) {
                stmt.setString(1, estadoFiltro);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(new DocenteVista(
                    rs.getString("matricula"),
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

    public static DocenteVista buscarDocenteVista(String numeroControl) {
        String sql = "SELECT d.matricula, p.nombre_persona, p.apellido_paterno_persona, " +
            "p.apellido_materno_persona, p.correo_persona, d.estado " +
            "FROM docente d " +
            "JOIN persona p ON d.id_persona = p.id_persona " +
            "WHERE d.matricula = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numeroControl);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new DocenteVista(
                    rs.getString("matricula"),
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

    public static boolean cambiarEstadoDocente(String numeroTarjeta, String nuevoEstado) {
        String sql = "UPDATE docente SET estado = ? WHERE matricula = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nuevoEstado);
            stmt.setString(2, numeroTarjeta);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static int actualizarDocenteCompleto(String numeroTarjetaOriginal, String nuevoNumeroTarjeta, String nombre, String paterno, String materno, String correo) {
    int idPersona = -1;
    String query = "SELECT id_persona FROM docente WHERE matricula = ?";
    try (Connection conn = ConexionDB.conectar();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, numeroTarjetaOriginal);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            idPersona = rs.getInt("id_persona");
        } else {
            return 0; // No existe el docente original
        }
    } catch (Exception e) {
        e.printStackTrace();
        return 0;
    }

    try (Connection conn = ConexionDB.conectar()) {
        conn.setAutoCommit(false);

        // Primero, actualiza el número de tarjeta si cambió
        if (!numeroTarjetaOriginal.equals(nuevoNumeroTarjeta)) {
            String sqlUpdateNC = "UPDATE docente SET matricula = ? WHERE matricula = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateNC)) {
                stmt.setString(1, nuevoNumeroTarjeta);
                stmt.setString(2, numeroTarjetaOriginal);
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
        } else if (msg.contains("matricula") || (msg.contains("llave duplicada") && msg.contains("matricula"))) {
            return -2; // Matricula duplicada
        }
        ex.printStackTrace();
        return 0;
    }
}
    
    public static List<DocenteVista> obtenerDocenteVistaSoloDocentesActivos() {
    List<DocenteVista> lista = new ArrayList<>();
    String sql =
        "SELECT d.matricula, p.nombre_persona, p.apellido_paterno_persona, " +
        "p.apellido_materno_persona, p.correo_persona, d.estado " +
        "FROM docente d " +
        "JOIN persona p ON d.id_persona = p.id_persona " +
        "JOIN usuario u ON p.id_usuario = u.id_usuario " +
        "JOIN rol r ON u.id_rol = r.id_rol " +
        "WHERE d.estado = 'ACTIVO' AND r.nombre_rol = 'Docente' " +
        "ORDER BY p.apellido_paterno_persona";

    try (Connection conn = ConexionDB.conectar();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            lista.add(new DocenteVista(
                rs.getString("matricula"),
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


    
    
}
