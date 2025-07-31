/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package vista;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import modelo.SolicitudResidencia;

public class VistaSolicitudesResidenciaJefatura extends VBox {
    private TableView<SolicitudResidencia> tablaSolicitudes;
    private ObservableList<SolicitudResidencia> listaSolicitudes;
    private Button btnRecibir;
    private Button btnDescargarPDF;

    public VistaSolicitudesResidenciaJefatura() {
        setSpacing(18);
        setPadding(new Insets(32));
        setAlignment(Pos.TOP_CENTER);

        Label titulo = new Label("Solicitudes de Residencia Enviadas");
        titulo.getStyleClass().add("form-title");

        tablaSolicitudes = new TableView<>();
        tablaSolicitudes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablaSolicitudes.setPrefHeight(350);

        TableColumn<SolicitudResidencia, Number> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getIdSolicitud()));

        TableColumn<SolicitudResidencia, String> colEstudiante = new TableColumn<>("No. Control");
        colEstudiante.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getIdEstudiante()));

        TableColumn<SolicitudResidencia, String> colRuta = new TableColumn<>("PDF");
        colRuta.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getRutaPdf()));

        TableColumn<SolicitudResidencia, String> colEstatus = new TableColumn<>("Estatus");
        colEstatus.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEstatus()));

        TableColumn<SolicitudResidencia, String> colFecha = new TableColumn<>("Fecha Envío");
        colFecha.setCellValueFactory(data -> {
            java.sql.Date fecha = data.getValue().getFechaEnvio();
            String val = (fecha != null) ? fecha.toString() : "";
            return new javafx.beans.property.SimpleStringProperty(val);
        });

        tablaSolicitudes.getColumns().addAll(colId, colEstudiante, colRuta, colEstatus, colFecha);

        listaSolicitudes = FXCollections.observableArrayList();
        tablaSolicitudes.setItems(listaSolicitudes);

        btnRecibir = new Button("Recibir");
        btnRecibir.setDisable(true);
        btnRecibir.setPrefWidth(160);

        btnDescargarPDF = new Button("Descargar PDF");
        btnDescargarPDF.setDisable(true);
        btnDescargarPDF.setPrefWidth(160);

        HBox contBotones = new HBox(18, btnRecibir, btnDescargarPDF);
        contBotones.setAlignment(Pos.CENTER);

        getChildren().addAll(titulo, tablaSolicitudes, contBotones);

        // Selección en tabla
        tablaSolicitudes.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            btnRecibir.setDisable(newSel == null || (newSel != null && "RECIBIDO".equalsIgnoreCase(newSel.getEstatus())));
            btnDescargarPDF.setDisable(newSel == null);
        });
    }

    public TableView<SolicitudResidencia> getTablaSolicitudes() { return tablaSolicitudes; }
    public ObservableList<SolicitudResidencia> getListaSolicitudes() { return listaSolicitudes; }
    public Button getBtnRecibir() { return btnRecibir; }
    public Button getBtnDescargarPDF() { return btnDescargarPDF; }
}
