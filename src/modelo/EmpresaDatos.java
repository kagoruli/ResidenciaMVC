/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpresaDatos {

    // Verifica si existe un RFC en la base de datos
    public static boolean existeRFC(String rfc) {
        String sql = "SELECT 1 FROM empresa WHERE rfc_empresa = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rfc);
            ResultSet rs = ps.executeQuery();
            return rs.next(); // true si existe, false si no
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Considera que no existe si hay error
        }
    }
    
    // Inserta una nueva empresa en la base de datos
    public static boolean insertarEmpresa(Empresa empresa) throws SQLException {
        String sql = "INSERT INTO empresa (nombre_empresa, rfc_empresa, representante_empresa, telefono_empresa, descripcion_empresa) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, empresa.getNombre());
            ps.setString(2, empresa.getRfc());
            ps.setString(3, empresa.getRepresentante());
            ps.setString(4, empresa.getTelefono());
            ps.setString(5, empresa.getDescripcion());

            int filas = ps.executeUpdate();
            return filas > 0;
        }
    }
    
    // Lista todas las empresas
    public static List<Empresa> listarEmpresas() {
        List<Empresa> lista = new ArrayList<>();
        String sql = "SELECT nombre_empresa, rfc_empresa, representante_empresa, telefono_empresa, descripcion_empresa FROM empresa";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Empresa e = new Empresa(
                    rs.getString("nombre_empresa"),
                    rs.getString("rfc_empresa"),
                    rs.getString("representante_empresa"),
                    rs.getString("telefono_empresa"),
                    rs.getString("descripcion_empresa")
                );
                lista.add(e);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    // Cambia el estatus de una empresa
    public static boolean cambiarEstatusEmpresa(String rfc, String nuevoEstatus) {
        String sql = "UPDATE empresa SET estatus=? WHERE rfc_empresa=?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstatus);
            ps.setString(2, rfc);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Actualiza los datos de la empresa (incluido el RFC)
    public static boolean actualizarEmpresa(Empresa empresa, String rfcAnterior) throws SQLException {
        String sql = "UPDATE empresa SET nombre_empresa=?, rfc_empresa=?, representante_empresa=?, telefono_empresa=?, descripcion_empresa=? WHERE rfc_empresa=?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, empresa.getNombre());
            ps.setString(2, empresa.getRfc());
            ps.setString(3, empresa.getRepresentante());
            ps.setString(4, empresa.getTelefono());
            ps.setString(5, empresa.getDescripcion());
            ps.setString(6, rfcAnterior);
            return ps.executeUpdate() > 0;
        }
    }
    
    public static boolean existeRFCExcepto(String nuevoRFC, String rfcActual) {
        String sql = "SELECT 1 FROM empresa WHERE rfc_empresa = ? AND rfc_empresa <> ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoRFC);
            ps.setString(2, rfcActual);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static List<Empresa> listarEmpresasPorEstatus(String estatusFiltro) {
        List<Empresa> lista = new ArrayList<>();
        String sql = "SELECT nombre_empresa, rfc_empresa, representante_empresa, telefono_empresa, descripcion_empresa, estatus FROM empresa";
        if (!"TODOS".equals(estatusFiltro)) {
            sql += " WHERE estatus = ?";
        }
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (!"TODOS".equals(estatusFiltro)) {
                ps.setString(1, estatusFiltro);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Empresa e = new Empresa(
                    rs.getString("nombre_empresa"),
                    rs.getString("rfc_empresa"),
                    rs.getString("representante_empresa"),
                    rs.getString("telefono_empresa"),
                    rs.getString("descripcion_empresa"),
                    rs.getString("estatus")
                );
                lista.add(e);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }
    
    public static List<Empresa> buscarEmpresasPorRFC(String rfc) {
        List<Empresa> lista = new ArrayList<>();
        String sql = "SELECT nombre_empresa, rfc_empresa, representante_empresa, telefono_empresa, descripcion_empresa, estatus FROM empresa WHERE rfc_empresa LIKE ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + rfc + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Empresa e = new Empresa(
                    rs.getString("nombre_empresa"),
                    rs.getString("rfc_empresa"),
                    rs.getString("representante_empresa"),
                    rs.getString("telefono_empresa"),
                    rs.getString("descripcion_empresa"),
                    rs.getString("estatus")
                );
                lista.add(e);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }
    public static boolean insertarSiNoExiste(Empresa empresa) throws SQLException {
        if (existeRFC(empresa.getRfc())) {
            return false; // Ya existe
        }
        return insertarEmpresa(empresa);
    }
    // Método mejorado para obtener empresa por RFC
    public static Empresa obtenerPorRFC(String rfc) {
        String sql = "SELECT * FROM empresa WHERE rfc_empresa = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rfc);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Empresa e = new Empresa();
                e.setNombre(rs.getString("nombre_empresa"));
                e.setRfc(rs.getString("rfc_empresa"));
                e.setRepresentante(rs.getString("representante_empresa"));
                e.setTelefono(rs.getString("telefono_empresa"));
                e.setDescripcion(rs.getString("descripcion_empresa"));
                return e;
            }
        } catch (SQLException ex) {
            System.err.println("Error al buscar empresa por RFC: " + ex.getMessage());
        }
        return null;
    }
}

