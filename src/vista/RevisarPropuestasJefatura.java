/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package vista;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import modelo.PropuestaPropia;

public class RevisarPropuestasJefatura extends VBox {
    private TableView<PropuestaPropia> tablaPropuestas;
    private Button btnDescargarPDF;
    private Button btnCambiarEstado;
    private TextArea txtComentarios;
    private ComboBox<String> cmbEstado;
    private Label lblInfo;

    public RevisarPropuestasJefatura() {
        setSpacing(20);
        setPadding(new Insets(30));
        setAlignment(Pos.TOP_CENTER);
        getStyleClass().add("form-container");

        Label titulo = new Label("Revisar Propuestas Propias");
        titulo.getStyleClass().add("form-title");

        tablaPropuestas = new TableView<>();
        tablaPropuestas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<PropuestaPropia, String> colEstudiante = new TableColumn<>("Estudiante");
        colEstudiante.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getIdEstudiante()));
        colEstudiante.setMinWidth(100);

        TableColumn<PropuestaPropia, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEstado()));
        colEstado.setMinWidth(80);

        TableColumn<PropuestaPropia, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFechaEntrega().toString()));
        colFecha.setMinWidth(100);

        tablaPropuestas.getColumns().addAll(colEstudiante, colEstado, colFecha);

        btnDescargarPDF = new Button("Descargar PDF");
        btnCambiarEstado = new Button("Guardar Cambios");

        cmbEstado = new ComboBox<>();
        cmbEstado.getItems().addAll("Pendiente", "Aceptado", "Corregir", "Rechazado");
        cmbEstado.setPrefWidth(150);

        txtComentarios = new TextArea();
        txtComentarios.setPromptText("Escribe tus comentarios aquí...");
        txtComentarios.setPrefRowCount(3);

        HBox filaBotones = new HBox(15, btnDescargarPDF, new Label("Nuevo estado:"), cmbEstado, btnCambiarEstado);
        filaBotones.setAlignment(Pos.CENTER);

        lblInfo = new Label();

        getChildren().addAll(titulo, tablaPropuestas, filaBotones, txtComentarios, lblInfo);
    }

    // Getters para controlador
    public TableView<PropuestaPropia> getTablaPropuestas() { return tablaPropuestas; }
    public Button getBtnDescargarPDF() { return btnDescargarPDF; }
    public Button getBtnCambiarEstado() { return btnCambiarEstado; }
    public TextArea getTxtComentarios() { return txtComentarios; }
    public ComboBox<String> getCmbEstado() { return cmbEstado; }
    public Label getLblInfo() { return lblInfo; }
}
