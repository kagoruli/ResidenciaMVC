/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package controlador;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import modelo.PropuestaPropia;
import modelo.PropuestaPropiaDAO;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.sql.Date;
import java.sql.SQLException;
import modelo.EstudianteDatos;
import vista.VistaPropuestaPropia;

public class ControladorPropuestaPropia {
    private final String carpetaBase = "archivos/propuestas/";
    private VistaPropuestaPropia vista; // Para controlar el bloqueo visual

    public ControladorPropuestaPropia(VistaPropuestaPropia vista) {
        this.vista = vista;
        verificarSeleccionPrevia();
    }

    public ControladorPropuestaPropia() {
        this.vista = null;
    }

    public PropuestaPropia obtenerPropuesta(String idEstudiante) throws SQLException {
        return PropuestaPropiaDAO.obtenerPorEstudiante(idEstudiante);
    }

    public boolean puedeSubirPropuesta(PropuestaPropia propuesta) {
        return propuesta == null || propuesta.getEstado().equalsIgnoreCase("Rechazado") || propuesta.getEstado().equalsIgnoreCase("Corregir");
    }

    public File seleccionarArchivo(Window ventana) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecciona tu propuesta en PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        return fileChooser.showOpenDialog(ventana);
    }

    public String guardarArchivoPDF(File archivo, String idEstudiante, int idPropuesta) throws IOException {
        String rutaCarpeta = carpetaBase + idEstudiante;
        Files.createDirectories(Paths.get(rutaCarpeta));
        String nombreDestino = idPropuesta + ".pdf";
        Path destino = Paths.get(rutaCarpeta, nombreDestino);
        Files.copy(archivo.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);
        return rutaCarpeta + "/" + nombreDestino;
    }

    public boolean registrarPropuesta(String idEstudiante, int idAnteproyecto, File archivoPDF) throws SQLException, IOException {
        java.util.Date utilDate = new java.util.Date();
        Date fechaActual = new Date(utilDate.getTime());

        PropuestaPropia propuestaExistente = PropuestaPropiaDAO.obtenerPorEstudiante(idEstudiante);

        if (propuestaExistente != null && 
            (propuestaExistente.getEstado().equalsIgnoreCase("Corregir") || propuestaExistente.getEstado().equalsIgnoreCase("Rechazado"))) {

            // 1. Ya hay propuesta en estado corregir o rechazado → solo actualiza el PDF, estado y fecha
            String ruta = guardarArchivoPDF(archivoPDF, idEstudiante, propuestaExistente.getIdPropuesta());
            propuestaExistente.setRutaArchivo(ruta);
            propuestaExistente.setEstado("Pendiente");
            propuestaExistente.setFechaEntrega(fechaActual);
            // Puedes limpiar comentarios aquí si quieres
            propuestaExistente.setComentarios("");
            return PropuestaPropiaDAO.actualizarPropuesta(propuestaExistente);
        } else if (propuestaExistente == null) {
            // 2. No hay propuesta, inserta normalmente
            PropuestaPropia nueva = new PropuestaPropia(0, idEstudiante, idAnteproyecto, "", "Pendiente", "", fechaActual);
            boolean ok = PropuestaPropiaDAO.registrarPropuesta(nueva);
            if (!ok) return false;

            // Obtiene id generado
            PropuestaPropia registrada = PropuestaPropiaDAO.obtenerPorEstudiante(idEstudiante);
            if (registrada == null) return false;
            String ruta = guardarArchivoPDF(archivoPDF, idEstudiante, registrada.getIdPropuesta());
            registrada.setRutaArchivo(ruta);
            return PropuestaPropiaDAO.actualizarRutaArchivo(registrada.getIdPropuesta(), ruta);
        } else {
            // 3. Si ya existe y NO está en Corregir o Rechazado, NO debe dejar enviar
            return false;
        }
    }


    // --- NUEVO BLOQUEO VISUAL IGUAL QUE EN BANCO ANTEPROYECTOS ---
    public void verificarSeleccionPrevia() {
        if (vista == null) return; // Solo si el controlador tiene referencia a la vista
        try {
            if (EstudianteDatos.tieneAnteproyectoAsignado(vista.getUsuarioLogueado())) {
                vista.mostrarSoloMensaje(true);
            } else {
                vista.mostrarSoloMensaje(false);
            }
        } catch (SQLException ex) {
            vista.mostrarSoloMensaje(false);
            System.err.println("Error al verificar selección: " + ex.getMessage());
        }
    }
}
