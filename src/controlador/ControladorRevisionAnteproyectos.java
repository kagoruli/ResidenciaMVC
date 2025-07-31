/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package controlador;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import modelo.EntregaAnteproyecto;
import modelo.EntregaAnteproyectoDatos;
import modelo.SolicitudResidenciaDatos;
import vista.RevisionAnteproyectos;
import vista.WizardAsignacionAsesorRevisor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import modelo.DocenteVista;

public class ControladorRevisionAnteproyectos {
    private final RevisionAnteproyectos vista;

    public ControladorRevisionAnteproyectos(RevisionAnteproyectos vista) {
        this.vista = vista;
        cargarDatos();
        configurarEventos();
    }

    private void cargarDatos() {
        try {
            String estado = vista.getFiltroEstado().getValue();
            String modalidad = vista.getFiltroModalidad().getValue();
            ObservableList<EntregaAnteproyecto> lista = EntregaAnteproyectoDatos.obtenerTodas(estado, modalidad);
            vista.cargarDatos(lista);
        } catch (SQLException ex) {
            mostrarAlerta("Error", "No se pudo cargar la lista: " + ex.getMessage());
        }
    }

    private void configurarEventos() {
        vista.getFiltroEstado().setOnAction(e -> cargarDatos());
        vista.getFiltroModalidad().setOnAction(e -> cargarDatos());

        // ---- Nuevo: Lógica de habilitación para Asignar Asesor/Revisores ----
        vista.getTabla().getSelectionModel().selectedItemProperty().addListener((obs, oldSel, sel) -> {
            if (sel != null) {
                // Habilitar/deshabilitar controles normales (corregir/aceptar PDF)
                if (!sel.getEstado().equals("Aceptado")) {
                    vista.getCbEstado().setDisable(false);
                    vista.getBtnGuardar().setDisable(vista.getCbEstado().getValue() == null);
                }
                // Control de habilitación para el botón Asignar Asesor/Revisores
                boolean habilitarAsignar = false;
                if ("Aceptado".equals(sel.getEstado())) {
                    boolean tieneSolicitudRecibida = SolicitudResidenciaDatos.existeSolicitudRecibida(sel.getIdEstudiante());
                    habilitarAsignar = tieneSolicitudRecibida;
                }
                vista.getBtnAsignarAsesorRevisores().setDisable(!habilitarAsignar);
            } else {
                vista.getBtnAsignarAsesorRevisores().setDisable(true);
            }
        });

        // ---- Evento para abrir Wizard de Asignación ----
        vista.getBtnAsignarAsesorRevisores().setOnAction(e -> {
            EntregaAnteproyecto sel = vista.getTabla().getSelectionModel().getSelectedItem();
            if (sel == null) {
                mostrarAlerta("Atención", "Seleccione un anteproyecto.");
                return;
            }
            int idEntrega = sel.getIdEntrega();
            try{
                Integer idAnteproyecto = EntregaAnteproyectoDatos.obtenerIdAnteproyectoPorIdEntrega(idEntrega);
                if (idAnteproyecto != null) {
                    java.util.List<DocenteVista> docentesActivos = modelo.DocenteDatos.obtenerDocenteVistaSoloDocentesActivos();
                    WizardAsignacionAsesorRevisor wizard = new WizardAsignacionAsesorRevisor(docentesActivos, idAnteproyecto);
                    wizard.show();
                    // Si guardaste la asignación dentro del wizard puedes recargar la tabla aquí
                    cargarDatos();
                } else {
                    // No se encontró
                    mostrarAlerta("Error","No se encontro el id del anteproyecto");
                    return;
                }
                
            }
            catch(SQLException ex){mostrarAlerta("Error SQL","Quien sabe que paso jaja");}
        });


        vista.getBtnGuardar().setOnAction(e -> {
            EntregaAnteproyecto sel = vista.getTabla().getSelectionModel().getSelectedItem();
            if (sel == null) {
                mostrarAlerta("Atención", "Seleccione un anteproyecto.");
                return;
            }
            String estadoNuevo = vista.getCbEstado().getValue();
            if (estadoNuevo == null) {
                mostrarAlerta("Atención", "Seleccione un estado.");
                return;
            }
            String comentarios = null;
            if ("Corregir".equals(estadoNuevo)) {
                comentarios = vista.getTxtComentarios().getText();
                if (comentarios.trim().isEmpty()) {
                    mostrarAlerta("Atención", "Debe ingresar comentarios de corrección.");
                    return;
                }
            }
            try {
                if (EntregaAnteproyectoDatos.actualizarEstado(sel.getIdEntrega(), estadoNuevo, comentarios)) {
                    mostrarAlerta("Éxito", "Estado actualizado correctamente.");
                    cargarDatos();
                } else {
                    mostrarAlerta("Error", "No se pudo actualizar el estado.");
                }
            } catch (SQLException ex) {
                mostrarAlerta("Error", "Error al actualizar: " + ex.getMessage());
            }
        });

        vista.getBtnDescargar().setOnAction(e -> {
            EntregaAnteproyecto sel = vista.getTabla().getSelectionModel().getSelectedItem();
            if (sel == null) {
                mostrarAlerta("Atención", "Seleccione un anteproyecto.");
                return;
            }
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar PDF de Anteproyecto");
            fileChooser.setInitialFileName(sel.getIdEstudiante() + "_anteproyecto.pdf");
            File destino = fileChooser.showSaveDialog(null);
            if (destino != null) {
                try {
                    Files.copy(new File(sel.getRutaArchivo()).toPath(), destino.toPath());
                    mostrarAlerta("Éxito", "Archivo descargado correctamente.");
                } catch (IOException ex) {
                    mostrarAlerta("Error", "No se pudo copiar el archivo.");
                }
            }
        });
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
