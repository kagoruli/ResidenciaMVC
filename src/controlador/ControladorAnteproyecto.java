/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

/**
 *
 * @author jafet
 */
import java.sql.*;
import java.util.Collections;
import java.util.List;
import modelo.AnteproyectoDatos;
import modelo.ConexionDB;
import vista.FormularioAnteproyecto;

public class ControladorAnteproyecto {

    private FormularioAnteproyecto vista;

    public ControladorAnteproyecto(FormularioAnteproyecto vista) {
        this.vista = vista;
    }

    public void registrarAnteproyecto(String titulo, String cupo, String tipo, String descripcion, String empresa) {
        String sql = "INSERT INTO anteproyecto (titulo_anteproyecto, cupo_anteproyecto, tipo_anteproyecto, descripcion_anteproyecto, rfc_empresa, cupo_disponible) " +
                     "VALUES (?, ?, ?, ?, (SELECT rfc_empresa FROM empresa WHERE nombre_empresa = ?), ?)";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, titulo);
            ps.setInt(2, Integer.parseInt(cupo));
            ps.setString(3, tipo);
            ps.setString(4, descripcion);
            ps.setString(5, empresa);
            ps.setInt(6, Integer.parseInt(cupo));
            int filas = ps.executeUpdate();
            if (filas > 0) {
                vista.mostrarConfirmacion(); // Mostrar confirmación al usuario
            } else {
                vista.mostrarError("Error al registrar", "No se pudo registrar el anteproyecto.");
            }
        } catch (SQLException e) {
            vista.mostrarError("Error de base de datos", "Hubo un problema al guardar el anteproyecto: " + e.getMessage());
        }
    }

    public List<String> obtenerEmpresas() {
    try {
        return AnteproyectoDatos.obtenerEmpresas();
    } catch (SQLException e) {
        vista.mostrarError("Error BD", "Error al cargar empresas: " + e.getMessage());
        return Collections.emptyList();
    }
}
}

