/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package controlador;

import javafx.application.Platform;
import modelo.SolicitudResidencia;
import modelo.SolicitudResidenciaDatos;
import vista.VistaSolicitudesResidenciaJefatura;
import java.awt.Desktop;
import java.io.File;
import java.sql.SQLException;
import java.util.List;

public class ControladorSolicitudesResidenciaJefatura {
    private final VistaSolicitudesResidenciaJefatura vista;

    public ControladorSolicitudesResidenciaJefatura(VistaSolicitudesResidenciaJefatura vista) {
        this.vista = vista;
        cargarSolicitudes();

        vista.getBtnRecibir().setOnAction(e -> marcarRecibido());
        vista.getBtnDescargarPDF().setOnAction(e -> descargarPDF());
    }

    private void cargarSolicitudes() {
        new Thread(() -> {
            try {
                List<SolicitudResidencia> solicitudes = SolicitudResidenciaDatos.obtenerTodas();
                Platform.runLater(() -> {
                    vista.getListaSolicitudes().setAll(solicitudes);
                    vista.getBtnRecibir().setDisable(true);
                    vista.getBtnDescargarPDF().setDisable(true);
                });
            } catch (SQLException ex) {
                mostrarError("Error al cargar solicitudes: " + ex.getMessage());
            }
        }).start();
    }

    private void marcarRecibido() {
        SolicitudResidencia solicitud = vista.getTablaSolicitudes().getSelectionModel().getSelectedItem();
        if (solicitud == null) return;
        try {
            boolean ok = SolicitudResidenciaDatos.marcarRecibido(solicitud.getIdSolicitud());
            if (ok) {
                solicitud.setEstatus("RECIBIDO");
                vista.getTablaSolicitudes().refresh();
                mostrarInfo("Solicitud marcada como recibida.");
                vista.getBtnRecibir().setDisable(true);
            } else {
                mostrarError("No se pudo actualizar el estado.");
            }
        } catch (SQLException ex) {
            mostrarError("Error: " + ex.getMessage());
        }
    }

    private void descargarPDF() {
        SolicitudResidencia solicitud = vista.getTablaSolicitudes().getSelectionModel().getSelectedItem();
        if (solicitud == null || solicitud.getRutaPdf() == null) {
            mostrarError("No hay PDF para esta solicitud.");
            return;
        }
        try {
            File pdf = new File(solicitud.getRutaPdf());
            if (pdf.exists()) {
                Desktop.getDesktop().open(pdf);
            } else {
                mostrarError("Archivo no encontrado: " + pdf.getAbsolutePath());
            }
        } catch (Exception e) {
            mostrarError("Error al abrir PDF: " + e.getMessage());
        }
    }

    private void mostrarInfo(String msg) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void mostrarError(String msg) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
