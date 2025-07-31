/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.animation.FadeTransition;
import javafx.scene.input.KeyEvent;
import modelo.DocenteVista;

/**
 *
 * @author jafet
 */
public class VistaDocentes extends VBox {
    private final TableView<DocenteVista> table;
    private final ComboBox<String> filtro;
    private final TextField txtBuscar;
    private final Button btnBuscar, btnEditar, btnDarBaja, btnRefrescar;

    public VistaDocentes() {
        setSpacing(20);
        setPadding(new Insets(30));
        setAlignment(Pos.TOP_CENTER);
        getStyleClass().add("content-container");

        Label lblTitulo = new Label("Lista de Docentes");
        lblTitulo.setFont(new Font("Segoe UI", 30));
        lblTitulo.getStyleClass().add("welcome-title");

        filtro = new ComboBox<>();
        filtro.getItems().addAll("TODOS", "ACTIVO", "BAJA");
        filtro.setValue("TODOS");
        filtro.getStyleClass().add("filter-combobox");
        
        txtBuscar = new TextField();
        txtBuscar.setPromptText("Número de tarjeta...");
        txtBuscar.addEventFilter(KeyEvent.KEY_TYPED, e -> {
            // Solo permite letras y números
            if (!e.getCharacter().matches("[a-zA-Z0-9]")) {
                e.consume();
            }
            // Limita a 10 caracteres
            if (txtBuscar.getText().length() >= 10) {
                e.consume();
            }
        });        
        
        btnBuscar = new Button("Buscar");
        btnEditar = new Button("Editar");
        btnDarBaja = new Button("Dar de Baja");
        btnRefrescar = new Button("Refrescar");

        btnBuscar.getStyleClass().add("small-form-button");
        btnEditar.getStyleClass().add("small-form-button");
        btnDarBaja.getStyleClass().add("small-form-button");
        btnRefrescar.getStyleClass().add("small-form-button");
        
        // Tabla
        table = new TableView<>();
        TableColumn<DocenteVista, String> colNum = new TableColumn<>("No. Tarjeta");
        colNum.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getNumeroTarjeta()));
        TableColumn<DocenteVista, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getNombre()));
        TableColumn<DocenteVista, String> colPat = new TableColumn<>("Apellido Paterno");
        colPat.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getApellidoPaterno()));
        TableColumn<DocenteVista, String> colMat = new TableColumn<>("Apellido Materno");
        colMat.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getApellidoMaterno()));
        TableColumn<DocenteVista, String> colCorreo = new TableColumn<>("Correo");
        colCorreo.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getCorreo()));
        TableColumn<DocenteVista, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getEstado()));

        table.getColumns().addAll(colNum, colNombre, colPat, colMat, colCorreo, colEstado);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400);
        VBox.setVgrow(table, Priority.ALWAYS);

        HBox barraFiltros = new HBox(10, new Label("Filtrar:"), filtro, new Label("Buscar:"), txtBuscar, btnBuscar, btnRefrescar);
        barraFiltros.setAlignment(Pos.CENTER_LEFT);

        HBox barraBotones = new HBox(10, btnEditar, btnDarBaja);
        barraBotones.setAlignment(Pos.CENTER_RIGHT);

        getChildren().addAll(lblTitulo, barraFiltros, table, barraBotones);

        // ANIMACIÓN: Fade-in cuando se muestra la vista
        FadeTransition ft = new FadeTransition(Duration.millis(500), this);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
        
        new controlador.ControladorVistaDocentes(this);
    }

    // Getters (para el controlador)
    public TableView<DocenteVista> getTable() { return table; }
    public ComboBox<String> getFiltro() { return filtro; }
    public TextField getTxtBuscar() { return txtBuscar; }
    public Button getBtnBuscar() { return btnBuscar; }
    public Button getBtnEditar() { return btnEditar; }
    public Button getBtnDarBaja() { return btnDarBaja; }
    public Button getBtnRefrescar() { return btnRefrescar; }
}
