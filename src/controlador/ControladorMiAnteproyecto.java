/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import modelo.*;
import vista.MiAnteproyecto;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.sql.Date;

public class ControladorMiAnteproyecto {
    private final MiAnteproyecto vista;
    private final Stage stage;
    private final String usuarioLogueado;

    public ControladorMiAnteproyecto(MiAnteproyecto vista, Stage stage, String usuarioLogueado) {
        this.vista = vista;
        this.stage = stage;
        this.usuarioLogueado = usuarioLogueado;
        configurarEventos();
    }

    private void configurarEventos() {
        vista.getBtnSubirPDFAnteproyecto().setOnAction(e -> subirPDFAnteproyecto());
        vista.getBtnDescargarFormatoAnteproyecto().setOnAction(e -> descargarFormatoAnteproyecto());

        vista.getBtnSubirPDFSolicitud().setOnAction(e -> subirPDFSolicitudResidencia());
        vista.getBtnDescargarFormatoSolicitud().setOnAction(e -> descargarFormatoSolicitud());
        vista.getBtnDescargarSolicitudPDF().setOnAction(e -> descargarSolicitudPDFEnviada());
    }

    // ------ ANTEPROYECTO ------
    private void subirPDFAnteproyecto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecciona PDF de Anteproyecto");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        File archivo = fileChooser.showOpenDialog(stage);
        if (archivo == null) return;

        try {
            String carpetaBase = "archivos/anteproyectos/" + usuarioLogueado + "/";
            Files.createDirectories(Paths.get(carpetaBase));
            String nombreArchivo = usuarioLogueado + ".pdf";
            Path destino = Paths.get(carpetaBase, nombreArchivo);
            Files.copy(archivo.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);

            EntregaAnteproyecto existente = EntregaAnteproyectoDatos.obtenerPorEstudiante(usuarioLogueado);
            EntregaAnteproyecto ea = new EntregaAnteproyecto();
            ea.setIdEstudiante(usuarioLogueado);
            ea.setRutaArchivo(destino.toString());
            ea.setEstado("Pendiente");
            ea.setComentarios(null);
            ea.setFechaEntrega(new Date(System.currentTimeMillis()));

            boolean exito;
            if (existente == null) {
                exito = EntregaAnteproyectoDatos.insertar(ea);
            } else {
                exito = EntregaAnteproyectoDatos.actualizar(ea);
            }
            if (exito) {
                mostrarAlerta("Éxito", "Archivo subido correctamente. Estado: Pendiente");
                vista.actualizarVista();
            } else {
                mostrarAlerta("Error", "No se pudo registrar el archivo en la base de datos.");
            }
        } catch (IOException ex) {
            mostrarAlerta("Error", "No se pudo copiar el archivo: " + ex.getMessage());
        } catch (Exception ex) {
            mostrarAlerta("Error", "Error: " + ex.getMessage());
        }
    }

    private void descargarFormatoAnteproyecto() {
        try {
            FormatoOficial formato = FormatoOficialDAO.obtenerPorTipo("anteproyecto");
            if (formato == null) {
                mostrarAlerta("Error", "No se ha subido el formato oficial de anteproyecto.");
                return;
            }
            java.awt.Desktop.getDesktop().open(new java.io.File(formato.getRutaPDF()));
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo abrir el PDF: " + e.getMessage());
        }
    }

    // ----- SOLICITUD RESIDENCIA -----
    private void subirPDFSolicitudResidencia() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Selecciona PDF de Solicitud de Residencia");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
    File archivo = fileChooser.showOpenDialog(stage);
    if (archivo == null) return;

    try {
        // Paso 1: Inserta la solicitud (SIN ruta PDF, solo para obtener el id)
        SolicitudResidencia solicitud = new SolicitudResidencia();
        solicitud.setIdEstudiante(usuarioLogueado);
        solicitud.setRutaPdf(""); // Temporal
        solicitud.setEstatus("ENVIADO");
        solicitud.setFechaEnvio(new Date(System.currentTimeMillis()));

        // IMPORTANTE: método que retorna el id_solicitud generado
        int idSolicitud = SolicitudResidenciaDatos.insertarRetornarID(solicitud);

        if (idSolicitud <= 0) {
            mostrarAlerta("Error", "No se pudo registrar la solicitud en la base de datos.");
            return;
        }

        // Paso 2: Guardar el archivo PDF en la ruta correcta
        String carpetaBase = "archivos/solicitudes_residencia/" + usuarioLogueado + "/";
        Files.createDirectories(Paths.get(carpetaBase));
        String nombreArchivo = idSolicitud + ".pdf";
        Path destino = Paths.get(carpetaBase, nombreArchivo);
        Files.copy(archivo.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);

        // Paso 3: Actualiza el registro con la ruta final
        String rutaFinal = carpetaBase + nombreArchivo;
        solicitud.setIdSolicitud(idSolicitud);
        solicitud.setRutaPdf(rutaFinal);
        boolean exitoRuta = SolicitudResidenciaDatos.actualizarRutaPdf(solicitud);

        if (exitoRuta) {
            mostrarAlerta("Éxito", "Solicitud de residencia enviada correctamente.");
            vista.actualizarVista();
        } else {
            mostrarAlerta("Advertencia", "Archivo guardado, pero no se pudo actualizar la ruta en la base de datos.");
        }
    } catch (IOException ex) {
        mostrarAlerta("Error", "No se pudo copiar el archivo: " + ex.getMessage());
    } catch (Exception ex) {
        mostrarAlerta("Error", "Error: " + ex.getMessage());
    }
}

    private void descargarFormatoSolicitud() {
        try {
            FormatoOficial formato = FormatoOficialDAO.obtenerPorTipo("solicitud_residencia");
            if (formato == null) {
                mostrarAlerta("Error", "No se ha subido el formato oficial de solicitud de residencia.");
                return;
            }
            java.awt.Desktop.getDesktop().open(new java.io.File(formato.getRutaPDF()));
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo abrir el PDF: " + e.getMessage());
        }
    }

    private void descargarSolicitudPDFEnviada() {
        try {
            SolicitudResidencia solicitud = SolicitudResidenciaDatos.obtenerPorEstudiante(usuarioLogueado);
            if (solicitud == null || solicitud.getRutaPdf() == null) {
                mostrarAlerta("Error", "No hay solicitud enviada aún.");
                return;
            }
            java.awt.Desktop.getDesktop().open(new java.io.File(solicitud.getRutaPdf()));
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo abrir tu PDF enviado: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
