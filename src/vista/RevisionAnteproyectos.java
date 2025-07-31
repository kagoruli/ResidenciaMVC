/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package vista;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import modelo.EntregaAnteproyecto;

public class RevisionAnteproyectos extends VBox {
    private final TableView<EntregaAnteproyecto> tabla;
    private final ComboBox<String> filtroEstado;
    private final ComboBox<String> filtroModalidad;
    private final ComboBox<String> cbEstado;
    private final TextArea txtComentarios;
    private final Button btnGuardar;
    private final Button btnDescargar;
    private final Button btnAsignarAsesorRevisores; // Nuevo botón

    public RevisionAnteproyectos() {
        setSpacing(20);
        setPadding(new Insets(20));
        getStyleClass().add("form-container");

        Label titulo = new Label("REVISIÓN DE ANTEPROYECTOS");
        titulo.getStyleClass().add("form-title");

        // Filtros
        filtroEstado = new ComboBox<>();
        filtroEstado.getItems().addAll("Todos", "Pendiente", "Corregir", "Aceptado");
        filtroEstado.setValue("Todos");

        filtroModalidad = new ComboBox<>();
        filtroModalidad.getItems().addAll("Todos", "Banco de Anteproyectos", "Propuesta Propia");
        filtroModalidad.setValue("Todos");

        HBox filtros = new HBox(20, new Label("Estado:"), filtroEstado, new Label("Modalidad:"), filtroModalidad);
        filtros.setPadding(new Insets(0,0,10,0));

        // Tabla
        tabla = new TableView<>();
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabla.setPrefHeight(340);

        TableColumn<EntregaAnteproyecto, String> colEstudiante = new TableColumn<>("Estudiante");
        colEstudiante.setCellValueFactory(new PropertyValueFactory<>("idEstudiante"));

        TableColumn<EntregaAnteproyecto, String> colProyecto = new TableColumn<>("Proyecto");
        colProyecto.setCellValueFactory(new PropertyValueFactory<>("tituloProyecto"));

        TableColumn<EntregaAnteproyecto, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        TableColumn<EntregaAnteproyecto, String> colFecha = new TableColumn<>("Fecha Entrega");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaEntrega"));

        TableColumn<EntregaAnteproyecto, String> colModalidad = new TableColumn<>("Modalidad");
        colModalidad.setCellValueFactory(new PropertyValueFactory<>("modalidad"));

        tabla.getColumns().addAll(colEstudiante, colProyecto, colEstado, colFecha, colModalidad);

        // Panel de revisión
        GridPane panelRevision = new GridPane();
        panelRevision.setHgap(10);
        panelRevision.setVgap(10);
        panelRevision.setPadding(new Insets(10));
        panelRevision.getStyleClass().add("form-grid");

        cbEstado = new ComboBox<>();
        cbEstado.getItems().addAll("Aceptado", "Corregir");
        cbEstado.setPrefWidth(150);

        txtComentarios = new TextArea();
        txtComentarios.setPrefRowCount(3);
        txtComentarios.setPromptText("Comentarios de corrección...");
        txtComentarios.setVisible(false);

        Label lblEstado = new Label("Estado:");
        Label lblComentarios = new Label("Comentarios:");
        lblComentarios.setVisible(false);

        btnGuardar = new Button("Guardar");
        btnGuardar.setDisable(true);

        btnDescargar = new Button("Descargar PDF");
        btnDescargar.setDisable(true);

        btnAsignarAsesorRevisores = new Button("Asignar Asesor/Revisores"); // Nuevo botón
        btnAsignarAsesorRevisores.setDisable(true);

        panelRevision.add(lblEstado, 0, 0);
        panelRevision.add(cbEstado, 1, 0);
        panelRevision.add(lblComentarios, 0, 1);
        panelRevision.add(txtComentarios, 1, 1);
        panelRevision.add(btnGuardar, 0, 2);
        panelRevision.add(btnDescargar, 1, 2);
        panelRevision.add(btnAsignarAsesorRevisores, 2, 2); // Agregamos el botón en la UI

        GridPane.setColumnSpan(txtComentarios, 2);

        getChildren().addAll(titulo, filtros, tabla, panelRevision);

        // Eventos UI (igual que antes)
        tabla.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, sel) -> {
            if (sel == null) {
                cbEstado.setDisable(true);
                cbEstado.setValue(null);
                btnGuardar.setDisable(true);
                btnDescargar.setDisable(true);
                btnAsignarAsesorRevisores.setDisable(true);
                lblComentarios.setVisible(false);
                txtComentarios.setVisible(false);
                txtComentarios.clear();
            } else {
                cbEstado.setDisable(false);
                cbEstado.setValue(sel.getEstado().equals("Pendiente") ? null : sel.getEstado());
                btnDescargar.setDisable(false);

                // Habilitar botón de asignar solo si el estado es Aceptado (el controlador revisa la solicitud)
                btnAsignarAsesorRevisores.setDisable(!"Aceptado".equals(sel.getEstado()));

                if (sel.getEstado().equals("Aceptado")) {
                    cbEstado.setDisable(true);
                    btnGuardar.setDisable(true);
                    lblComentarios.setVisible(false);
                    txtComentarios.setVisible(false);
                } else {
                    btnGuardar.setDisable(cbEstado.getValue() == null);
                }
                if ("Corregir".equals(cbEstado.getValue())) {
                    lblComentarios.setVisible(true);
                    txtComentarios.setVisible(true);
                } else {
                    lblComentarios.setVisible(false);
                    txtComentarios.setVisible(false);
                }
            }
        });

        cbEstado.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || tabla.getSelectionModel().getSelectedItem() == null) {
                btnGuardar.setDisable(true);
                lblComentarios.setVisible(false);
                txtComentarios.setVisible(false);
            } else {
                if (newVal.equals("Corregir")) {
                    lblComentarios.setVisible(true);
                    txtComentarios.setVisible(true);
                    btnGuardar.setDisable(txtComentarios.getText().trim().isEmpty());
                } else {
                    lblComentarios.setVisible(false);
                    txtComentarios.setVisible(false);
                    btnGuardar.setDisable(false);
                }
            }
        });

        txtComentarios.textProperty().addListener((obs, oldVal, newVal) -> {
            if ("Corregir".equals(cbEstado.getValue())) {
                btnGuardar.setDisable(newVal.trim().isEmpty());
            }
        });
    }

    // Getters para controlador
    public TableView<EntregaAnteproyecto> getTabla() { return tabla; }
    public ComboBox<String> getFiltroEstado() { return filtroEstado; }
    public ComboBox<String> getFiltroModalidad() { return filtroModalidad; }
    public ComboBox<String> getCbEstado() { return cbEstado; }
    public TextArea getTxtComentarios() { return txtComentarios; }
    public Button getBtnGuardar() { return btnGuardar; }
    public Button getBtnDescargar() { return btnDescargar; }
    public Button getBtnAsignarAsesorRevisores() { return btnAsignarAsesorRevisores; } // Nuevo getter

    public void cargarDatos(ObservableList<EntregaAnteproyecto> lista) {
        tabla.setItems(lista);
    }
}
