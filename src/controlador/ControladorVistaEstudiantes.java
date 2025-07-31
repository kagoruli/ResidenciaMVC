/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

/**
 *
 * @author jafet
 */
import javafx.collections.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import modelo.EstudianteDatos;
import modelo.EstudianteVista;
import vista.VistaEstudiantes;

public class ControladorVistaEstudiantes {
    private final VistaEstudiantes vista;
    private ObservableList<EstudianteVista> lista;

    public ControladorVistaEstudiantes(VistaEstudiantes vista) {
        this.vista = vista;
        inicializarEventos();
        cargarTabla(null);
        actualizarBotones();
    }

    private void inicializarEventos() {
        vista.getBtnRefrescar().setOnAction(e -> cargarTabla(null));
        vista.getFiltro().setOnAction(e -> {
            String val = vista.getFiltro().getValue();
            cargarTabla(val.equals("TODOS") ? null : val);
        });
        vista.getBtnBuscar().setOnAction(e -> {
            String num = vista.getTxtBuscar().getText().trim();
            if (num.isEmpty()) {
                mostrarAlerta("Ingrese número de control para buscar.");
                return;
            }
            EstudianteVista ev = EstudianteDatos.buscarEstudianteVista(num);
            ObservableList<EstudianteVista> listaBusqueda = FXCollections.observableArrayList();
            if (ev != null) listaBusqueda.add(ev);
            else {
                mostrarAlerta("No se encontró ningún estudiante con el número de control: " + num);
                return;
            }
            vista.getTable().setItems(listaBusqueda);
            actualizarBotones();
        });

        vista.getBtnDarBaja().setOnAction(e -> cambiarEstatusEstudiante());
        vista.getBtnEditar().setOnAction(e -> {
            EstudianteVista sel = vista.getTable().getSelectionModel().getSelectedItem();
            if (sel == null) {
                mostrarAlerta("Selecciona un estudiante de la tabla.");
                return;
            }
            new vista.FormularioEditarEstudiante(
                sel.getNumeroControl(),
                () -> {
                    String val = vista.getFiltro().getValue();
                    cargarTabla(val.equals("TODOS") ? null : val);
                }
            ).show();
        });

        // Listener de selección de tabla para habilitar/deshabilitar botones según el estatus
        vista.getTable().getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSel, newSel) -> actualizarBotones()
        );
    }

    private void actualizarBotones() {
        EstudianteVista estudiante = vista.getTable().getSelectionModel().getSelectedItem();
        if (estudiante == null) {
            vista.getBtnEditar().setDisable(true);
            vista.getBtnDarBaja().setDisable(true);
            vista.getBtnDarBaja().setText("Dar de Baja");
            return;
        }

        boolean esActivo = "ACTIVO".equalsIgnoreCase(estudiante.getEstado());
        vista.getBtnEditar().setDisable(!esActivo);
        vista.getBtnDarBaja().setDisable(false);

        if (esActivo) {
            vista.getBtnDarBaja().setText("Dar de Baja");
        } else {
            vista.getBtnDarBaja().setText("Dar de Alta");
        }
    }

    private void cargarTabla(String filtro) {
        lista = FXCollections.observableArrayList(EstudianteDatos.obtenerEstudiantesVista(filtro));
        vista.getTable().setItems(lista);
        vista.getTable().getSelectionModel().clearSelection();
        actualizarBotones();
    }

    private void cambiarEstatusEstudiante() {
        EstudianteVista sel = vista.getTable().getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarAlerta("Selecciona un estudiante de la tabla.");
            return;
        }

        boolean esActivo = "ACTIVO".equalsIgnoreCase(sel.getEstado());
        String nuevoEstado = esActivo ? "BAJA" : "ACTIVO";
        String accion = esActivo ? "dar de baja" : "dar de alta";
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar " + accion);
        confirm.setHeaderText(null);
        confirm.setContentText("¿Estás seguro que deseas " + accion + " a este estudiante?");
        ButtonType btnSi = new ButtonType("Sí", ButtonBar.ButtonData.YES);
        ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.NO);
        confirm.getButtonTypes().setAll(btnSi, btnNo);

        confirm.showAndWait().ifPresent(resp -> {
            if (resp == btnSi) {
                boolean ok = EstudianteDatos.cambiarEstadoEstudiante(sel.getNumeroControl(), nuevoEstado);
                if (ok) {
                    sel.setEstado(nuevoEstado);
                    vista.getTable().refresh();
                    mostrarAlerta(esActivo ? "Estudiante dado de baja exitosamente." : "Estudiante dado de alta exitosamente.");
                    actualizarBotones();
                } else {
                    mostrarAlerta("Error al cambiar el estado del estudiante.");
                }
            }
        });
    }

    private void mostrarAlerta(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
