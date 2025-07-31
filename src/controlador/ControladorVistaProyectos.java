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
import javafx.scene.control.*;
import modelo.*;
import vista.VistaProyectos;
import vista.DetallesAnteproyectoDialog;
import java.util.List;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ControladorVistaProyectos {
    private final VistaProyectos vista;
    private ObservableList<Anteproyecto> lista;

    public ControladorVistaProyectos(VistaProyectos vista) {
        this.vista = vista;
        cargarEmpresasEnFiltro();
        inicializarEventos();
        cargarTabla();
    }

    // Carga el filtro de empresas (solo una vez al inicio)
    private void cargarEmpresasEnFiltro() {
        List<Empresa> empresas = AnteproyectoDatos.listarEmpresas();
        ComboBox<String> cb = vista.getFiltroEmpresa();
        cb.getItems().clear();
        cb.getItems().add("TODAS");
        for (Empresa emp : empresas) {
            cb.getItems().add(emp.getNombre() + " (" + emp.getRfc() + ")");
        }
        cb.setValue("TODAS");
    }

    private void inicializarEventos() {
        vista.getFiltroEstatus().setOnAction(e -> cargarTabla());
        vista.getFiltroEmpresa().setOnAction(e -> cargarTabla());
        vista.getBtnRefrescar().setOnAction(e -> cargarTabla());

        vista.getTable().getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> actualizarBotones());
        
        vista.getBtnEditar().setOnAction(e -> mostrarEdicion());

        vista.getBtnEstatus().setOnAction(e -> cambiarEstatusProyecto());

        // Agregar listener de doble clic para mostrar detalles del proyecto
        vista.getTable().setRowFactory(tv -> {
            TableRow<Anteproyecto> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Anteproyecto ant = row.getItem();
                    mostrarDetallesAnteproyecto(ant);
                }
            });
            return row;
        });
    }

    private void cargarTabla() {
        String estatus = vista.getFiltroEstatus().getValue();
        String empresa = vista.getFiltroEmpresa().getValue();
        String rfcEmpresa = null;

        if (empresa != null && !empresa.equals("TODAS")) {
            int idx1 = empresa.lastIndexOf('(');
            int idx2 = empresa.lastIndexOf(')');
            if (idx1 != -1 && idx2 != -1 && idx2 > idx1) {
                rfcEmpresa = empresa.substring(idx1 + 1, idx2).trim();
            }
        }
        lista = FXCollections.observableArrayList(AnteproyectoDatos.listarAnteproyectos(estatus, rfcEmpresa));
        vista.getTable().setItems(lista);
        vista.getTable().getSelectionModel().clearSelection();
        actualizarBotones();
    }

    private void actualizarBotones() {
        Anteproyecto ant = vista.getTable().getSelectionModel().getSelectedItem();
        if (ant == null) {
            vista.getBtnEditar().setDisable(true);
            vista.getBtnEstatus().setDisable(true);
            vista.getBtnEstatus().setText("Dar de baja");
            return;
        }
        boolean activo = "ACTIVO".equals(ant.getEstatus());
        vista.getBtnEditar().setDisable(!activo);
        vista.getBtnEstatus().setDisable(false);
        vista.getBtnEstatus().setText(activo ? "Dar de baja" : "Dar de alta");
    }

    private void mostrarEdicion() {
        Anteproyecto seleccionado = vista.getTable().getSelectionModel().getSelectedItem();
        if (seleccionado == null) return;

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Editar Anteproyecto");

        VBox root = new VBox(15);
        root.setPadding(new javafx.geometry.Insets(20));
        root.setAlignment(javafx.geometry.Pos.CENTER);

        // -------- Título: Máximo 50 caracteres --------
        TextField txtTitulo = new TextField(seleccionado.getTitulo());
        txtTitulo.setTextFormatter(new TextFormatter<String>(change -> {
            if (change.getControlNewText().length() > 50) return null;
            return change;
        }));

        // -------- Cupo: Solo números, máximo 4 dígitos --------
        TextField txtCupo = new TextField(String.valueOf(seleccionado.getCupo()));
        txtCupo.setTextFormatter(new TextFormatter<String>(change -> {
            String newText = change.getControlNewText();
            if (newText.length() > 4) return null;
            if (!newText.matches("\\d*")) return null;
            return change;
        }));

        ComboBox<String> cbTipo = new ComboBox<>();
        cbTipo.getItems().addAll("Presencial", "Remota", "Hibrido");
        cbTipo.setValue(seleccionado.getTipo());

        TextArea txtDescripcion = new TextArea(seleccionado.getDescripcion());
        txtDescripcion.setPrefRowCount(4);

        ComboBox<String> cbEmpresa = new ComboBox<>();
        List<Empresa> empresas = AnteproyectoDatos.listarEmpresas();
        String seleccionEmpresa = null;
        for (Empresa emp : empresas) {
            String display = emp.getNombre() + " (" + emp.getRfc() + ")";
            cbEmpresa.getItems().add(display);
            if (emp.getRfc().equals(seleccionado.getRfcEmpresa())) {
                seleccionEmpresa = display;
            }
        }
        cbEmpresa.setValue(seleccionEmpresa);

        Button btnGuardar = new Button("Guardar cambios");
        btnGuardar.getStyleClass().add("small-form-button");
        btnGuardar.setDefaultButton(true);

        Label lblError = new Label();
        lblError.setStyle("-fx-text-fill: red;");

        btnGuardar.setOnAction(e -> {
            try {
                String nuevoTitulo = txtTitulo.getText().trim();
                String cupoStr = txtCupo.getText().trim();
                if (nuevoTitulo.isEmpty() || cbTipo.getValue() == null || cbEmpresa.getValue() == null || cupoStr.isEmpty()) {
                    lblError.setText("Todos los campos son obligatorios.");
                    return;
                }
                int nuevoCupo = Integer.parseInt(cupoStr);

                String nuevoTipo = cbTipo.getValue();
                String nuevaDescripcion = txtDescripcion.getText().trim();

                String empresaSel = cbEmpresa.getValue();
                String nuevoRfcEmpresa = null;
                if (empresaSel != null) {
                    int idx1 = empresaSel.lastIndexOf('(');
                    int idx2 = empresaSel.lastIndexOf(')');
                    if (idx1 != -1 && idx2 != -1 && idx2 > idx1) {
                        nuevoRfcEmpresa = empresaSel.substring(idx1 + 1, idx2).trim();
                    }
                }

                seleccionado.setTitulo(nuevoTitulo);
                seleccionado.setCupo(nuevoCupo);
                seleccionado.setTipo(nuevoTipo);
                seleccionado.setDescripcion(nuevaDescripcion);
                seleccionado.setRfcEmpresa(nuevoRfcEmpresa);

                boolean ok = AnteproyectoDatos.actualizarAnteproyecto(seleccionado);
                if (ok) {
                    dialog.close();
                    cargarTabla();
                } else {
                    lblError.setText("Error al guardar los cambios.");
                }
            } catch (Exception ex) {
                lblError.setText("Datos inválidos: " + ex.getMessage());
            }
        });

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);

        grid.addRow(0, new Label("Título:"), txtTitulo);
        grid.addRow(1, new Label("Cupo:"), txtCupo);
        grid.addRow(2, new Label("Tipo:"), cbTipo);
        grid.addRow(3, new Label("Descripción:"), txtDescripcion);
        grid.addRow(4, new Label("Empresa:"), cbEmpresa);

        root.getChildren().addAll(grid, btnGuardar, lblError);

        dialog.setScene(new javafx.scene.Scene(root));
        dialog.showAndWait();
    }

    private void cambiarEstatusProyecto() {
        Anteproyecto seleccionado = vista.getTable().getSelectionModel().getSelectedItem();
        if (seleccionado == null) return;

        // Nueva lógica: no permitir baja si hay estudiantes asignados
        if ("ACTIVO".equals(seleccionado.getEstatus())) {
            int numEstudiantes = AnteproyectoDatos.contarEstudiantesAsignados(seleccionado.getIdAnteproyecto());
            if (numEstudiantes > 0) {
                Alert err = new Alert(Alert.AlertType.ERROR, 
                    "No es posible dar de baja este proyecto porque hay estudiantes asignados.", 
                    ButtonType.OK);
                err.setHeaderText(null);
                err.showAndWait();
                return;
            }
        }

        String nuevoEstatus = seleccionado.getEstatus().equals("ACTIVO") ? "BAJA" : "ACTIVO";
        String accion = nuevoEstatus.equals("BAJA") ? "dar de baja" : "dar de alta";
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Seguro que deseas " + accion + " este proyecto?",
                ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirmación");
        alert.setHeaderText(null);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            boolean ok = AnteproyectoDatos.cambiarEstatus(seleccionado.getIdAnteproyecto(), nuevoEstatus);
            if (ok) {
                cargarTabla();
            } else {
                Alert err = new Alert(Alert.AlertType.ERROR, "No se pudo cambiar el estatus.", ButtonType.OK);
                err.showAndWait();
            }
        }
    }

    // ------- NUEVO: Método para mostrar los detalles del proyecto con alumnos -------
    private void mostrarDetallesAnteproyecto(Anteproyecto ant) {
    try {
        // 1. Obtener el anteproyecto completo (con cupo disponible correcto)
        AnteproyectoBanco anteproyectoBanco = AnteproyectoDatos.obtenerAnteproyectoPorId(ant.getIdAnteproyecto());

        // 2. Obtener la lista de alumnos asignados
        List<String[]> alumnos = AnteproyectoDatos.obtenerEstudiantesAsignados(ant.getIdAnteproyecto());

        // 3. Mostrar el diálogo con toda la info correcta
        DetallesAnteproyectoDialog dialog = new DetallesAnteproyectoDialog(anteproyectoBanco, alumnos);
        dialog.showAndWait();
    } catch (Exception ex) {
        ex.printStackTrace();
        Alert err = new Alert(Alert.AlertType.ERROR, "No se pudieron mostrar los detalles.", ButtonType.OK);
        err.showAndWait();
    }
}
}
