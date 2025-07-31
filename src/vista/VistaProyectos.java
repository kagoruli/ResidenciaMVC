/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

/**
 *
 * @author jafet
 */


import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.animation.FadeTransition;
import modelo.Anteproyecto;
import modelo.Empresa;
import java.util.List;

public class VistaProyectos extends VBox {

    private final TableView<Anteproyecto> table;
    private final ComboBox<String> filtroEstatus, filtroEmpresa;
    private final Button btnEditar, btnEstatus, btnRefrescar;

    public VistaProyectos() {
        setSpacing(20);
        setPadding(new Insets(30));
        setAlignment(Pos.TOP_CENTER);
        getStyleClass().add("content-container");

        // TÍTULO
        Label lblTitulo = new Label("Lista de Proyectos");
        lblTitulo.setFont(new Font("Segoe UI", 30));
        lblTitulo.getStyleClass().add("welcome-title");

        // FILTROS
        filtroEstatus = new ComboBox<>();
        filtroEstatus.getItems().addAll("TODOS", "ACTIVO", "BAJA");
        filtroEstatus.setValue("ACTIVO");
        filtroEstatus.getStyleClass().add("filter-combobox");

        filtroEmpresa = new ComboBox<>();
        filtroEmpresa.getItems().add("TODAS");
        // Las empresas las cargará el controlador por separación MVC
        filtroEmpresa.setValue("TODAS");
        filtroEmpresa.getStyleClass().add("filter-combobox");

        btnRefrescar = new Button("Refrescar");
        btnRefrescar.getStyleClass().add("small-form-button");

        HBox barraFiltros = new HBox(10, new Label("Estatus:"), filtroEstatus, new Label("Empresa:"), filtroEmpresa, btnRefrescar);
        barraFiltros.setAlignment(Pos.CENTER_LEFT);

        // TABLA
        table = new TableView<>();
        TableColumn<Anteproyecto, String> colTitulo = new TableColumn<>("Título");
        colTitulo.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTitulo()));
        TableColumn<Anteproyecto, String> colCupo = new TableColumn<>("Cupo");
        colCupo.setCellValueFactory(data -> new ReadOnlyStringWrapper(String.valueOf(data.getValue().getCupo())));
        TableColumn<Anteproyecto, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTipo()));
        TableColumn<Anteproyecto, String> colEmpresa = new TableColumn<>("Empresa");
        colEmpresa.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getNombreEmpresa()));
        TableColumn<Anteproyecto, String> colEstatus = new TableColumn<>("Estatus");
        colEstatus.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getEstatus()));

        table.getColumns().addAll(colTitulo, colCupo, colTipo, colEmpresa, colEstatus);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400);
        VBox.setVgrow(table, Priority.ALWAYS);

        // BOTONES
        btnEditar = new Button("Editar");
        btnEditar.getStyleClass().add("small-form-button");
        btnEditar.setDisable(true);

        btnEstatus = new Button("Dar de baja");
        btnEstatus.getStyleClass().add("small-form-button");
        btnEstatus.setDisable(true);

        HBox barraBotones = new HBox(10, btnEditar, btnEstatus);
        barraBotones.setAlignment(Pos.CENTER_RIGHT);

        getChildren().addAll(lblTitulo, barraFiltros, table, barraBotones);

        // ANIMACIÓN
        FadeTransition ft = new FadeTransition(Duration.millis(500), this);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        // El controlador se instancia en la clase main o donde muestres esta vista
        // Ejemplo: new ControladorVistaProyectos(this);
    }

    // Getters para el controlador
    public TableView<Anteproyecto> getTable() { return table; }
    public ComboBox<String> getFiltroEstatus() { return filtroEstatus; }
    public ComboBox<String> getFiltroEmpresa() { return filtroEmpresa; }
    public Button getBtnEditar() { return btnEditar; }
    public Button getBtnEstatus() { return btnEstatus; }
    public Button getBtnRefrescar() { return btnRefrescar; }
}

