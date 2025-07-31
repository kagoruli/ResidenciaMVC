/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

/**
 *
 * @author jafet
 */
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import modelo.Empresa;

public class VistaEmpresas extends VBox {

    private final TableView<Empresa> table;
    private final ComboBox<String> filtroEstatus;
    private final TextField txtBuscarRFC;
    private final Button btnBuscar;
    private final Button btnRefrescar;
    private final Button btnEditar;
    private final Button btnEstatus;

    public VistaEmpresas() {
        setSpacing(20);
        setPadding(new Insets(30));
        setAlignment(Pos.TOP_CENTER);
        getStyleClass().add("content-container");

        // TÍTULO grande azul
        Label lblTitulo = new Label("Lista de Empresas");
        lblTitulo.setFont(new Font("Segoe UI", 30));
        lblTitulo.getStyleClass().add("welcome-title");

        // FILTROS: Estatus y RFC
        filtroEstatus = new ComboBox<>();
        filtroEstatus.getItems().addAll("TODOS", "ACTIVO", "BAJA");
        filtroEstatus.setValue("ACTIVO");
        filtroEstatus.getStyleClass().add("filter-combobox");

        txtBuscarRFC = new TextField();
        txtBuscarRFC.setPromptText("RFC de empresa...");
        txtBuscarRFC.setPrefWidth(180);

        btnBuscar = new Button("Buscar");
        btnBuscar.getStyleClass().add("small-form-button");

        btnRefrescar = new Button("Refrescar");
        btnRefrescar.getStyleClass().add("small-form-button");

        HBox barraFiltros = new HBox(10,
                new Label("Filtrar:"), filtroEstatus,
                new Label("Buscar:"), txtBuscarRFC, btnBuscar, btnRefrescar
        );
        barraFiltros.setAlignment(Pos.CENTER_LEFT);

        // TABLA
        table = new TableView<>();
        TableColumn<Empresa, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Empresa, String> colRepresentante = new TableColumn<>("Representante");
        colRepresentante.setCellValueFactory(new PropertyValueFactory<>("representante"));

        TableColumn<Empresa, String> colTelefono = new TableColumn<>("Teléfono");
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));

        TableColumn<Empresa, String> colRFC = new TableColumn<>("RFC");
        colRFC.setCellValueFactory(new PropertyValueFactory<>("rfc"));

        TableColumn<Empresa, String> colEstatus = new TableColumn<>("Estatus");
        colEstatus.setCellValueFactory(new PropertyValueFactory<>("estatus"));

        table.getColumns().addAll(colNombre, colRepresentante, colTelefono, colRFC, colEstatus);
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

        // Layout principal
        getChildren().addAll(lblTitulo, barraFiltros, table, barraBotones);

        // Eventos para activar/desactivar botones según selección
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> actualizarBotones());

        // Doble clic para ver detalles
        table.setRowFactory(tv -> {
            TableRow<Empresa> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    Empresa empresa = row.getItem();
                    // Aquí debes llamar al método del controlador para mostrar el detalle
                    // controlador.mostrarDetalleEmpresa(empresa);
                }
            });
            return row;
        });

        // Los eventos de botones (editar, estatus, buscar, refrescar, filtro) los maneja el controlador
        // Ejemplo (descomentar cuando tengas el controlador):
        // btnEditar.setOnAction(e -> controlador.editarEmpresaSeleccionada());
        // btnEstatus.setOnAction(e -> controlador.cambiarEstatusEmpresa());
        // btnBuscar.setOnAction(e -> controlador.buscarPorRFC(txtBuscarRFC.getText()));
        // btnRefrescar.setOnAction(e -> controlador.refrescarLista());
        // filtroEstatus.setOnAction(e -> controlador.cargarEmpresasConFiltro());
    }

    // Métodos para conectar con el controlador

    public TableView<Empresa> getTable() { return table; }
    public ComboBox<String> getFiltroEstatus() { return filtroEstatus; }
    public TextField getTxtBuscarRFC() { return txtBuscarRFC; }
    public Button getBtnBuscar() { return btnBuscar; }
    public Button getBtnRefrescar() { return btnRefrescar; }
    public Button getBtnEditar() { return btnEditar; }
    public Button getBtnEstatus() { return btnEstatus; }

    // Permite al controlador poblar la tabla
    public void setEmpresas(ObservableList<Empresa> lista) {
        table.setItems(lista);
        table.getSelectionModel().clearSelection();
        actualizarBotones();
    }

    private void actualizarBotones() {
        Empresa emp = table.getSelectionModel().getSelectedItem();
        if (emp == null) {
            btnEditar.setDisable(true);
            btnEstatus.setDisable(true);
            btnEstatus.setText("Dar de baja");
            return;
        }
        boolean activa = "ACTIVO".equalsIgnoreCase(emp.getEstatus());
        btnEditar.setDisable(false);
        btnEstatus.setDisable(false);
        btnEstatus.setText(activa ? "Dar de baja" : "Dar de alta");
    }
}
