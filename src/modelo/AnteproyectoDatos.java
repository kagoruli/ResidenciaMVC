/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AnteproyectoDatos {

    // Inserta un nuevo anteproyecto en la base de datos
    public static boolean insertarAnteproyecto(Anteproyecto anteproyecto) throws SQLException {
        String sql = "INSERT INTO anteproyecto (titulo_anteproyecto, cupo_anteproyecto, tipo_anteproyecto, descripcion_anteproyecto, rfc_empresa) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, anteproyecto.getTitulo());
            ps.setInt(2, anteproyecto.getCupo());
            ps.setString(3, anteproyecto.getTipo());
            ps.setString(4, anteproyecto.getDescripcion());
            ps.setString(5, anteproyecto.getRfcEmpresa());

            int filas = ps.executeUpdate();
            return filas > 0;
        }
    }

    // Obtiene todas las empresas registradas en la base de datos
    public static List<String> obtenerEmpresas() throws SQLException {
        List<String> empresas = new ArrayList<>();
        String sql = "SELECT nombre_empresa FROM empresa";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                empresas.add(rs.getString("nombre_empresa"));
            }
        }
        return empresas;
    }
    
    public static AnteproyectoBanco obtenerAnteproyectoPorId(int id) throws SQLException {
        String sql = "SELECT a.id_anteproyecto, a.titulo_anteproyecto, a.cupo_disponible, "
                   + "a.tipo_anteproyecto, a.descripcion_anteproyecto, e.nombre_empresa "
                   + "FROM anteproyecto a "
                   + "JOIN empresa e ON a.rfc_empresa = e.rfc_empresa "
                   + "WHERE a.id_anteproyecto = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                AnteproyectoBanco a = new AnteproyectoBanco();
                a.setIdAnteproyecto(rs.getInt("id_anteproyecto"));
                a.setTitulo(rs.getString("titulo_anteproyecto"));
                a.setCupoDisponible(rs.getInt("cupo_disponible"));
                a.setTipo(rs.getString("tipo_anteproyecto"));
                a.setDescripcion(rs.getString("descripcion_anteproyecto"));
                a.setEmpresa(rs.getString("nombre_empresa"));
                return a;
            }
            return null;
        }
    }
    
    public static boolean actualizarCupoDisponible(int idAnteproyecto) throws SQLException {
        String sql = "UPDATE anteproyecto SET cupo_disponible = cupo_disponible - 1 " +
                     "WHERE id_anteproyecto = ? AND cupo_disponible > 0";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAnteproyecto);
            return ps.executeUpdate() > 0;
        }
    }
    
    public static ObservableList<AnteproyectoBanco> obtenerAnteproyectos(boolean soloConCupo) throws SQLException {
        String sql = "SELECT a.id_anteproyecto, a.titulo_anteproyecto, "
                   + "a.cupo_disponible, a.tipo_anteproyecto, "
                   + "a.descripcion_anteproyecto, e.nombre_empresa "
                   + "FROM anteproyecto a "
                   + "JOIN empresa e ON a.rfc_empresa = e.rfc_empresa "
                   + "WHERE a.estatus = 'ACTIVO' "
                   + "AND a.es_propuesta = false "; // <-- Solo banco de anteproyectos

        if (soloConCupo) {
            sql += "AND a.cupo_disponible > 0 ";
        }

        ObservableList<AnteproyectoBanco> lista = FXCollections.observableArrayList();

        try (Connection conn = ConexionDB.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                AnteproyectoBanco a = new AnteproyectoBanco();
                a.setIdAnteproyecto(rs.getInt("id_anteproyecto"));
                a.setTitulo(rs.getString("titulo_anteproyecto"));
                a.setCupoDisponible(rs.getInt("cupo_disponible"));
                a.setTipo(rs.getString("tipo_anteproyecto"));
                a.setDescripcion(rs.getString("descripcion_anteproyecto"));
                a.setEmpresa(rs.getString("nombre_empresa"));
                lista.add(a);
            }
        }
        return lista;
    }


    
    public static List<Anteproyecto> listarAnteproyectos(String estatusFiltro, String rfcEmpresaFiltro) {
        List<Anteproyecto> lista = new ArrayList<>();
        String sql = "SELECT a.id_anteproyecto, a.titulo_anteproyecto, a.cupo_anteproyecto, a.tipo_anteproyecto, a.descripcion_anteproyecto, " +
                     "a.rfc_empresa, e.nombre_empresa, a.estatus " +
                     "FROM anteproyecto a " +
                     "JOIN empresa e ON a.rfc_empresa = e.rfc_empresa ";

        List<Object> params = new ArrayList<>();
        boolean tieneWhere = false;
        if (!estatusFiltro.equalsIgnoreCase("TODOS")) {
            sql += "WHERE a.estatus = ? ";
            params.add(estatusFiltro);
            tieneWhere = true;
        }
        if (rfcEmpresaFiltro != null && !rfcEmpresaFiltro.equalsIgnoreCase("TODAS")) {
            sql += tieneWhere ? "AND " : "WHERE ";
            sql += "a.rfc_empresa = ? ";
            params.add(rfcEmpresaFiltro);
        }

        sql += "ORDER BY a.id_anteproyecto DESC";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i+1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Anteproyecto ant = new Anteproyecto(
                    rs.getInt("id_anteproyecto"),
                    rs.getString("titulo_anteproyecto"),
                    rs.getInt("cupo_anteproyecto"),
                    rs.getString("tipo_anteproyecto"),
                    rs.getString("descripcion_anteproyecto"),
                    rs.getString("rfc_empresa"),
                    rs.getString("nombre_empresa"),
                    rs.getString("estatus")
                );
                lista.add(ant);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    // Actualizar anteproyecto
    public static boolean actualizarAnteproyecto(Anteproyecto ant) {
        String sql = "UPDATE anteproyecto SET titulo_anteproyecto=?, cupo_anteproyecto=?, tipo_anteproyecto=?, descripcion_anteproyecto=?, rfc_empresa=? WHERE id_anteproyecto=?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ant.getTitulo());
            ps.setInt(2, ant.getCupo());
            ps.setString(3, ant.getTipo());
            ps.setString(4, ant.getDescripcion());
            ps.setString(5, ant.getRfcEmpresa());
            ps.setInt(6, ant.getIdAnteproyecto());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    // Cambiar estatus
    public static boolean cambiarEstatus(int idAnteproyecto, String nuevoEstatus) {
        String sql = "UPDATE anteproyecto SET estatus=? WHERE id_anteproyecto=?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstatus);
            ps.setInt(2, idAnteproyecto);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    // Listar empresas para ComboBox
    public static List<Empresa> listarEmpresas() {
        List<Empresa> lista = new ArrayList<>();
        String sql = "SELECT rfc_empresa, nombre_empresa FROM empresa ORDER BY nombre_empresa";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Empresa e = new Empresa();
                e.setRfc(rs.getString("rfc_empresa"));
                e.setNombre(rs.getString("nombre_empresa"));
                lista.add(e);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }
    
    public static int contarEstudiantesAsignados(int idAnteproyecto) {
        String sql = "SELECT COUNT(*) FROM estudiante WHERE id_anteproyecto = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAnteproyecto);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
    
    public static List<String[]> obtenerEstudiantesAsignados(int idAnteproyecto) {
        List<String[]> estudiantes = new ArrayList<>();
        String sql = "SELECT e.numero_control, p.nombre_persona, p.apellido_paterno_persona, p.apellido_materno_persona " +
                     "FROM estudiante e " +
                     "JOIN persona p ON e.id_persona = p.id_persona " +
                     "WHERE e.id_anteproyecto = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAnteproyecto);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String numero = rs.getString("numero_control");
                String nombreCompleto = rs.getString("nombre_persona") + " " + 
                                        rs.getString("apellido_paterno_persona") + " " + 
                                        rs.getString("apellido_materno_persona");
                estudiantes.add(new String[]{numero, nombreCompleto});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return estudiantes;
    }
    
    // Obtiene proyectos por RFC de empresa
    public static List<Anteproyecto> listarAnteproyectosPorRFC(String rfcEmpresa) {
        List<Anteproyecto> lista = new ArrayList<>();
        String sql = "SELECT id_anteproyecto, titulo_anteproyecto, cupo_anteproyecto, tipo_anteproyecto, descripcion_anteproyecto, rfc_empresa, estatus FROM anteproyecto WHERE rfc_empresa = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rfcEmpresa);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Anteproyecto ant = new Anteproyecto(
                    rs.getInt("id_anteproyecto"),
                    rs.getString("titulo_anteproyecto"),
                    rs.getInt("cupo_anteproyecto"),
                    rs.getString("tipo_anteproyecto"),
                    rs.getString("descripcion_anteproyecto"),
                    rs.getString("rfc_empresa"),
                    null, // nombre_empresa (opcional aquí)
                    rs.getString("estatus")
                );
                lista.add(ant);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }
    
    // Regresa los ID de los proyectos vinculados a una empresa (por RFC)
    public static List<Integer> obtenerIdsProyectosPorEmpresa(String rfcEmpresa) {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT id_anteproyecto FROM anteproyecto WHERE rfc_empresa = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rfcEmpresa);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ids.add(rs.getInt("id_anteproyecto"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }

    // Verifica si algún proyecto tiene estudiantes asignados
    public static boolean algunProyectoConEstudiantes(List<Integer> idsProyectos) {
        String sql = "SELECT COUNT(*) FROM estudiante WHERE id_anteproyecto = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int id : idsProyectos) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static List<String> obtenerTitulosProyectosPorEmpresa(String rfcEmpresa) {
        List<String> titulos = new ArrayList<>();
        String sql = "SELECT titulo_anteproyecto FROM anteproyecto WHERE rfc_empresa = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rfcEmpresa);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                titulos.add(rs.getString("titulo_anteproyecto"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return titulos;
    }
    
    public static int insertarRetornarID(Anteproyecto ante, boolean esPropuesta) throws SQLException {
        String sql = "INSERT INTO anteproyecto (titulo_anteproyecto, cupo_anteproyecto, tipo_anteproyecto, descripcion_anteproyecto, rfc_empresa, estatus, cupo_disponible, es_propuesta) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, ante.getTitulo());
            ps.setInt(2, ante.getCupo());
            ps.setString(3, ante.getTipo());
            ps.setString(4, ante.getDescripcion());
            ps.setString(5, ante.getRfcEmpresa());
            ps.setString(6, ante.getEstatus());
            ps.setInt(7, ante.getCupoDisponible());
            ps.setBoolean(8, esPropuesta);
            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        }
    }
}

