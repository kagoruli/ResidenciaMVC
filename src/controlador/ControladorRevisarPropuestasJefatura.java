/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import modelo.PropuestaPropia;
import modelo.PropuestaPropiaDAO;
import vista.RevisarPropuestasJefatura;
import vista.FormularioRegistroPropuesta;
import java.io.File;
import java.sql.SQLException;
import java.util.List;

public class ControladorRevisarPropuestasJefatura {
    private final RevisarPropuestasJefatura vista;
    private ObservableList<PropuestaPropia> propuestas;

    public ControladorRevisarPropuestasJefatura(RevisarPropuestasJefatura vista) {
        this.vista = vista;
        cargarPropuestas();
        configurarEventos();
        setControlesActivos(false, false); // Inicialmente todo desactivado
    }

    private void cargarPropuestas() {
        try {
            List<PropuestaPropia> lista = PropuestaPropiaDAO.obtenerTodas();
            propuestas = FXCollections.observableArrayList(lista);
            vista.getTablaPropuestas().setItems(propuestas);
        } catch (SQLException ex) {
            mostrarAlerta("Error", "No se pudieron cargar las propuestas: " + ex.getMessage());
        }
    }

    private void configurarEventos() {
        // Desactiva todo al inicio (excepto tabla)
        setControlesActivos(false, false);

        vista.getTablaPropuestas().getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                vista.getCmbEstado().setValue(selected.getEstado());
                vista.getTxtComentarios().setText(selected.getComentarios());

                boolean esAceptado = selected.getEstado().equalsIgnoreCase("Aceptado");
                setControlesActivos(true, !esAceptado);

                // Botón descargar PDF solo si hay archivo
                vista.getBtnDescargarPDF().setDisable(selected.getRutaArchivo() == null || selected.getRutaArchivo().isEmpty());
            } else {
                setControlesActivos(false, false);
            }
        });

        vista.getBtnDescargarPDF().setOnAction(e -> descargarPDF());
        vista.getBtnCambiarEstado().setOnAction(e -> cambiarEstadoYComentario());
    }

    // Control de activación/desactivación de controles
    private void setControlesActivos(boolean habilitar, boolean editable) {
        vista.getBtnDescargarPDF().setDisable(!habilitar); // Habilita solo si hay selección
        vista.getBtnCambiarEstado().setDisable(!editable); // Solo si editable (no aceptado)
        vista.getCmbEstado().setDisable(!editable);
        vista.getTxtComentarios().setDisable(!editable);
    }

    private void descargarPDF() {
        PropuestaPropia seleccionada = vista.getTablaPropuestas().getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Atención", "Selecciona una propuesta de la tabla.");
            return;
        }
        String ruta = seleccionada.getRutaArchivo();
        if (ruta == null || ruta.isEmpty()) {
            mostrarAlerta("Error", "La propuesta no tiene PDF asociado.");
            return;
        }
        try {
            File archivoOriginal = new File(ruta);
            if (!archivoOriginal.exists()) {
                mostrarAlerta("Error", "No se encontró el archivo PDF.");
                return;
            }
            // Dialogo de guardar como
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Guardar PDF como...");
            fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
            fileChooser.setInitialFileName(archivoOriginal.getName());
            File destino = fileChooser.showSaveDialog(vista.getScene().getWindow());
            if (destino != null) {
                // Copiar el archivo
                java.nio.file.Files.copy(archivoOriginal.toPath(), destino.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                mostrarAlerta("Éxito", "Archivo guardado correctamente en:\n" + destino.getAbsolutePath());
                // Si quieres, puedes abrirlo después de guardar:
                java.awt.Desktop.getDesktop().open(destino);
            }
        } catch (Exception ex) {
            mostrarAlerta("Error", "No se pudo guardar el PDF: " + ex.getMessage());
        }
    }


    private void cambiarEstadoYComentario() {
        PropuestaPropia seleccionada = vista.getTablaPropuestas().getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Atención", "Selecciona una propuesta para actualizar.");
            return;
        }
        String nuevoEstado = vista.getCmbEstado().getValue();
        String comentarios = vista.getTxtComentarios().getText();
        if (nuevoEstado == null) {
            mostrarAlerta("Atención", "Selecciona un nuevo estado.");
            return;
        }
        try {
            boolean ok = PropuestaPropiaDAO.actualizarEstadoYComentarios(seleccionada.getIdPropuesta(), nuevoEstado, comentarios);
            if (ok) {
                seleccionada.setEstado(nuevoEstado);
                seleccionada.setComentarios(comentarios);
                vista.getTablaPropuestas().refresh();
                mostrarAlerta("Éxito", "Estado/comentarios actualizados.");

                // --- Lógica clave: si es aceptado y el estudiante NO tiene anteproyecto asignado
                if (nuevoEstado.equalsIgnoreCase("Aceptado")) {
                    int idAnteproyecto = modelo.EstudianteDatos.obtenerIdAnteproyecto(seleccionada.getIdEstudiante());
                    if (idAnteproyecto == 0) {
                        abrirFormularioRegistro(seleccionada);
                    }
                }
                cargarPropuestas(); // Siempre refresca

                // Al refrescar, volver a desactivar controles hasta nueva selección
                setControlesActivos(false, false);

            } else {
                mostrarAlerta("Error", "No se pudo actualizar la propuesta.");
            }
        } catch (SQLException ex) {
            mostrarAlerta("Error", "Error al actualizar: " + ex.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void abrirFormularioRegistro(PropuestaPropia propuesta) {
        FormularioRegistroPropuesta dialog = new FormularioRegistroPropuesta(
            propuesta.getIdEstudiante(),
            "", // RFC sugerido (si tienes)
            "", // Nombre empresa sugerido
            "", // Título sugerido
            "", // Tipo sugerido
            ""  // Descripción sugerida
        );
        dialog.showAndWait();
        cargarPropuestas();
    }
}
