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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import modelo.FormatoOficial;

public class VistaFormatosOficialesJefatura extends VBox {
    private final TableView<FormatoOficial> tablaFormatos;
    private final Button btnSubirFormato;
    private final Button btnDescargarFormato;

    public VistaFormatosOficialesJefatura() {
        setSpacing(25);
        setPadding(new Insets(32, 32, 32, 32));
        setAlignment(Pos.TOP_CENTER);
        getStyleClass().add("form-container");

        Label titulo = new Label("Formatos Oficiales");
        titulo.getStyleClass().add("form-title");

        tablaFormatos = new TableView<>();
        tablaFormatos.setPrefHeight(300);
        tablaFormatos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<FormatoOficial, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colTipo.setMinWidth(150);

        TableColumn<FormatoOficial, String> colFecha = new TableColumn<>("Fecha de Subida");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaSubida"));
        colFecha.setMinWidth(160);

        tablaFormatos.getColumns().addAll(colTipo, colFecha);

        btnSubirFormato = new Button("Subir PDF Formato");
        btnSubirFormato.getStyleClass().add("form-button-ancho");
        btnDescargarFormato = new Button("Descargar Formato");
        btnDescargarFormato.getStyleClass().add("form-button-ancho");
        btnDescargarFormato.setDisable(true);

        HBox botones = new HBox(15, btnSubirFormato, btnDescargarFormato);
        botones.setAlignment(Pos.CENTER);

        getChildren().addAll(titulo, tablaFormatos, botones);

        // Habilitar botón de descarga sólo si hay selección
        tablaFormatos.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            btnDescargarFormato.setDisable(newSel == null);
        });
    }

    // Métodos de acceso para el controlador
    public TableView<FormatoOficial> getTablaFormatos() { return tablaFormatos; }
    public Button getBtnSubirFormato() { return btnSubirFormato; }
    public Button getBtnDescargarFormato() { return btnDescargarFormato; }

    // Permite cargar los datos desde el controlador
    public void setFormatos(ObservableList<FormatoOficial> formatos) {
        tablaFormatos.setItems(formatos);
    }
}
