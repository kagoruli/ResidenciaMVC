/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

/**
 *
 * @author luis
 */


import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import modelo.AnteproyectoBanco;

public class BancoAnteproyectos extends VBox {
    private final String usuarioLogueado;
    private TableView<AnteproyectoBanco> tablaAnteproyectos;
    private Button btnSeleccionar;
    // private Button btnPropia;  // Eliminado
    private CheckBox chkFiltrarCupo;
    private VBox contenidoPrincipal;
    private Label lblMensaje;

    public BancoAnteproyectos(String usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
        inicializarUI();
    }

    private void inicializarUI() {
        setSpacing(20);
        setPadding(new Insets(20));
        setAlignment(Pos.TOP_CENTER);

        // Mensaje para cuando ya tiene anteproyecto
        lblMensaje = new Label("Ya has seleccionado un Proyecto, por lo que no podrás seleccionar otro");
        lblMensaje.getStyleClass().add("mensaje-grande");
        lblMensaje.setVisible(false);

        // Contenedor principal (tabla, botones, etc.)
        contenidoPrincipal = new VBox(20);
        contenidoPrincipal.setAlignment(Pos.TOP_CENTER);

        // Título
        Label titulo = new Label("BANCO DE PROYECTOS");
        titulo.getStyleClass().add("form-title");

        // Tabla de anteproyectos
        tablaAnteproyectos = new TableView<>();
        tablaAnteproyectos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablaAnteproyectos.setMaxHeight(300);

        // Columnas
        TableColumn<AnteproyectoBanco, String> colTitulo = new TableColumn<>("Título");
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colTitulo.setMinWidth(150);

        TableColumn<AnteproyectoBanco, String> colEmpresa = new TableColumn<>("Empresa");
        colEmpresa.setCellValueFactory(new PropertyValueFactory<>("empresa"));
        colEmpresa.setMinWidth(120);

        TableColumn<AnteproyectoBanco, Integer> colCupo = new TableColumn<>("Cupo Disponible");
        colCupo.setCellValueFactory(new PropertyValueFactory<>("cupoDisponible"));
        colCupo.setMinWidth(80);

        TableColumn<AnteproyectoBanco, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colTipo.setMinWidth(80);

        tablaAnteproyectos.getColumns().addAll(colTitulo, colEmpresa, colCupo, colTipo);

        // Filtro de cupo
        chkFiltrarCupo = new CheckBox("Mostrar solo proyectos con cupo disponible");
        chkFiltrarCupo.setSelected(true);

        // Solo un botón: Seleccionar Proyecto, ahora con estilo "form-button"
        btnSeleccionar = new Button("Seleccionar Proyecto");
        btnSeleccionar.getStyleClass().add("form-button-ancho");

        HBox contenedorBotones = new HBox(10, btnSeleccionar);
        contenedorBotones.setAlignment(Pos.CENTER);

        // Agregar componentes al contenedor principal
        contenidoPrincipal.getChildren().addAll(titulo, tablaAnteproyectos, chkFiltrarCupo, contenedorBotones);

        // Agregar al layout principal
        getChildren().addAll(contenidoPrincipal, lblMensaje);

        // cuando se corriga esto lo reactivas jajajas
        chkFiltrarCupo.setDisable(true);
        chkFiltrarCupo.setVisible(false);
    }

    public void mostrarSoloMensaje(boolean mostrar) {
        contenidoPrincipal.setVisible(!mostrar);
        contenidoPrincipal.setManaged(!mostrar);
        lblMensaje.setVisible(mostrar);
        lblMensaje.setManaged(mostrar);
    }

    // Getters
    public Button getBtnSeleccionar() { return btnSeleccionar; }
    // public Button getBtnPropia() { return btnPropia; } // Eliminado getter
    public TableView<AnteproyectoBanco> getTablaAnteproyectos() { return tablaAnteproyectos; }
    public CheckBox getChkFiltrarCupo() { return chkFiltrarCupo; }
    public String getUsuarioLogueado() { return usuarioLogueado; }
}
