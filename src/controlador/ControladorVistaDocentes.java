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
import modelo.DocenteDatos;
import modelo.DocenteVista;
import vista.VistaDocentes;

public class ControladorVistaDocentes {
    private final VistaDocentes vista;
    private ObservableList<DocenteVista> lista;

    public ControladorVistaDocentes(VistaDocentes vista) {
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
                mostrarAlerta("Ingrese número de tarjeta para buscar.");
                return;
            }
            DocenteVista ev = DocenteDatos.buscarDocenteVista(num);
            ObservableList<DocenteVista> listaBusqueda = FXCollections.observableArrayList();
            if (ev != null) listaBusqueda.add(ev);
            else {
                mostrarAlerta("No se encontró ningún docente con el número de tarjeta: " + num);
                return;
            }
            vista.getTable().setItems(listaBusqueda);
            actualizarBotones();
        });

        vista.getBtnDarBaja().setOnAction(e -> cambiarEstatusDocente());
        vista.getBtnEditar().setOnAction(e -> {
            DocenteVista sel = vista.getTable().getSelectionModel().getSelectedItem();
            if (sel == null) {
                mostrarAlerta("Selecciona un docente de la tabla.");
                return;
            }
            new vista.FormularioEditarDocente(
                sel.getNumeroTarjeta(),
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
        DocenteVista docente = vista.getTable().getSelectionModel().getSelectedItem();
        if (docente == null) {
            vista.getBtnEditar().setDisable(true);
            vista.getBtnDarBaja().setDisable(true);
            vista.getBtnDarBaja().setText("Dar de Baja");
            return;
        }

        boolean esActivo = "ACTIVO".equalsIgnoreCase(docente.getEstado());
        vista.getBtnEditar().setDisable(!esActivo);
        vista.getBtnDarBaja().setDisable(false);

        if (esActivo) {
            vista.getBtnDarBaja().setText("Dar de Baja");
        } else {
            vista.getBtnDarBaja().setText("Dar de Alta");
        }
    }

    private void cargarTabla(String filtro) {
        lista = FXCollections.observableArrayList(DocenteDatos.obtenerDocenteVista(filtro));
        vista.getTable().setItems(lista);
        vista.getTable().getSelectionModel().clearSelection();
        actualizarBotones();
    }

    private void cambiarEstatusDocente() {
        DocenteVista sel = vista.getTable().getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarAlerta("Selecciona un docente de la tabla.");
            return;
        }

        boolean esActivo = "ACTIVO".equalsIgnoreCase(sel.getEstado());
        String nuevoEstado = esActivo ? "BAJA" : "ACTIVO";
        String accion = esActivo ? "dar de baja" : "dar de alta";
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar " + accion);
        confirm.setHeaderText(null);
        confirm.setContentText("¿Estás seguro que deseas " + accion + " a este docente?");
        ButtonType btnSi = new ButtonType("Sí", ButtonBar.ButtonData.YES);
        ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.NO);
        confirm.getButtonTypes().setAll(btnSi, btnNo);

        confirm.showAndWait().ifPresent(resp -> {
            if (resp == btnSi) {
                boolean ok = DocenteDatos.cambiarEstadoDocente(sel.getNumeroTarjeta(), nuevoEstado);
                if (ok) {
                    sel.setEstado(nuevoEstado);
                    vista.getTable().refresh();
                    mostrarAlerta(esActivo ? "Docente dado de baja exitosamente." : "Docente dado de alta exitosamente.");
                    actualizarBotones();
                } else {
                    mostrarAlerta("Error al cambiar el estado del docente.");
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
