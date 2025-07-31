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
import vista.VistaEmpresas;
import java.util.List;
import javafx.scene.layout.*;
import javafx.stage.*;
import vista.DetallesEmpresaDialog;

public class ControladorVistaEmpresas {
    private final VistaEmpresas vista;
    private ObservableList<Empresa> lista;

    public ControladorVistaEmpresas(VistaEmpresas vista) {
        this.vista = vista;
        cargarFiltros();
        inicializarEventos();
        cargarTabla();
    }

    private void cargarFiltros() {
        vista.getFiltroEstatus().getItems().clear();
        vista.getFiltroEstatus().getItems().addAll("TODOS", "ACTIVO", "BAJA");
        vista.getFiltroEstatus().setValue("TODOS");
    }

    private void inicializarEventos() {
        vista.getFiltroEstatus().setOnAction(e -> cargarTabla());
        vista.getBtnBuscar().setOnAction(e -> buscarPorRFC());
        vista.getBtnRefrescar().setOnAction(e -> cargarTabla());

        vista.getTable().getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> actualizarBotones());
        vista.getBtnEditar().setOnAction(e -> mostrarEdicion());
        vista.getBtnEstatus().setOnAction(e -> cambiarEstatusEmpresa());

        // Doble clic para mostrar detalles
        vista.getTable().setRowFactory(tv -> {
            TableRow<Empresa> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    mostrarDetalles(row.getItem());
                }
            });
            return row;
        });
    }

    private void cargarTabla() {
        String estatus = vista.getFiltroEstatus().getValue();
        lista = FXCollections.observableArrayList(EmpresaDatos.listarEmpresasPorEstatus(estatus));
        vista.getTable().setItems(lista);
        vista.getTable().getSelectionModel().clearSelection();
        actualizarBotones();
    }

    private void buscarPorRFC() {
        String rfc = vista.getTxtBuscarRFC().getText().trim();
        if (rfc.isEmpty()) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Campo vacío");
            alerta.setHeaderText(null);
            alerta.setContentText("Ingrese un RFC para buscar.");
            alerta.showAndWait();
            return;
        }
        lista = FXCollections.observableArrayList(EmpresaDatos.buscarEmpresasPorRFC(rfc));
        vista.getTable().setItems(lista);
        vista.getTable().getSelectionModel().clearSelection();
        actualizarBotones();
    }

    private void actualizarBotones() {
        Empresa emp = vista.getTable().getSelectionModel().getSelectedItem();
        if (emp == null) {
            vista.getBtnEditar().setDisable(true);
            vista.getBtnEstatus().setDisable(true);
            vista.getBtnEstatus().setText("Dar de baja");
            return;
        }
        boolean activa = "ACTIVO".equals(emp.getEstatus());
        vista.getBtnEditar().setDisable(!activa); // Solo activo permite edición
        vista.getBtnEstatus().setDisable(false);
        vista.getBtnEstatus().setText(activa ? "Dar de baja" : "Dar de alta");
    }

    private void mostrarEdicion() {
        Empresa seleccionada = vista.getTable().getSelectionModel().getSelectedItem();
        if (seleccionada == null) return;

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Editar Empresa");

        VBox root = new VBox(15);
        root.setPadding(new javafx.geometry.Insets(20));
        root.setAlignment(javafx.geometry.Pos.CENTER);

        // --- Nombre: solo letras, máximo 50 ---
        TextField txtNombre = new TextField(seleccionada.getNombre());
        txtNombre.setTextFormatter(new TextFormatter<String>(change -> {
            if (change.getControlNewText().length() > 50) return null;
            if (!change.getText().matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*")) return null;
            return change;
        }));

        // --- RFC: letras y números, 12-13 caracteres ---
        TextField txtRFC = new TextField(seleccionada.getRfc());
        txtRFC.setTextFormatter(new TextFormatter<String>(change -> {
            String newText = change.getControlNewText();
            if (newText.length() > 13) return null;
            if (!newText.matches("[a-zA-Z0-9]*")) return null;
            return change;
        }));

        // --- Representante: solo letras, máximo 50 ---
        TextField txtRepresentante = new TextField(seleccionada.getRepresentante());
        txtRepresentante.setTextFormatter(new TextFormatter<String>(change -> {
            if (change.getControlNewText().length() > 50) return null;
            if (!change.getText().matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*")) return null;
            return change;
        }));

        // --- Teléfono: solo números, exactamente 10 dígitos ---
        TextField txtTelefono = new TextField(seleccionada.getTelefono());
        txtTelefono.setTextFormatter(new TextFormatter<String>(change -> {
            String newText = change.getControlNewText();
            if (newText.length() > 10) return null;
            if (!newText.matches("\\d*")) return null;
            return change;
        }));

        // --- Descripción: hasta 300 caracteres (puedes ajustar) ---
        TextArea txtDescripcion = new TextArea(seleccionada.getDescripcion());
        txtDescripcion.setPrefRowCount(3);
        txtDescripcion.setTextFormatter(new TextFormatter<String>(change -> {
            if (change.getControlNewText().length() > 300) return null;
            return change;
        }));

        // ... el resto del código igual
        Button btnGuardar = new Button("Guardar cambios");
        btnGuardar.getStyleClass().add("small-form-button");
        btnGuardar.setDefaultButton(true);

        Label lblError = new Label();
        lblError.setStyle("-fx-text-fill: red;");

        btnGuardar.setOnAction(e -> {
            try {
                String nombre = txtNombre.getText().trim();
                String nuevoRFC = txtRFC.getText().trim();
                String representante = txtRepresentante.getText().trim();
                String telefono = txtTelefono.getText().trim();
                String descripcion = txtDescripcion.getText().trim();

                // Validaciones
                if (nombre.isEmpty() || nuevoRFC.isEmpty() || representante.isEmpty() || telefono.isEmpty() || descripcion.isEmpty()) {
                    lblError.setText("Todos los campos son obligatorios.");
                    return;
                }
                if (nuevoRFC.length() < 12 || nuevoRFC.length() > 13) {
                    lblError.setText("El RFC debe tener entre 12 y 13 caracteres.");
                    return;
                }
                if (telefono.length() != 10) {
                    lblError.setText("El teléfono debe tener 10 dígitos.");
                    return;
                }
                if (!nuevoRFC.equals(seleccionada.getRfc()) && EmpresaDatos.existeRFCExcepto(nuevoRFC, seleccionada.getRfc())) {
                    lblError.setText("Ya existe una empresa vinculada a ese RFC.");
                    return;
                }
                Empresa empresaEditada = new Empresa(nombre, nuevoRFC, representante, telefono, descripcion);
                boolean ok = EmpresaDatos.actualizarEmpresa(empresaEditada, seleccionada.getRfc());
                if (ok) {
                    dialog.close();
                    cargarTabla();
                } else {
                    lblError.setText("No se pudo guardar la empresa.");
                }
            } catch (Exception ex) {
                lblError.setText("Error: " + ex.getMessage());
            }
        });

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.addRow(0, new Label("Nombre:"), txtNombre);
        grid.addRow(1, new Label("RFC:"), txtRFC);
        grid.addRow(2, new Label("Representante:"), txtRepresentante);
        grid.addRow(3, new Label("Teléfono:"), txtTelefono);
        grid.addRow(4, new Label("Descripción:"), txtDescripcion);

        root.getChildren().addAll(grid, btnGuardar, lblError);

        dialog.setScene(new javafx.scene.Scene(root));
        dialog.showAndWait();
    }


    private void cambiarEstatusEmpresa() {
        Empresa seleccionada = vista.getTable().getSelectionModel().getSelectedItem();
        if (seleccionada == null) return;

        boolean esActivo = "ACTIVO".equals(seleccionada.getEstatus());
        String nuevoEstatus = esActivo ? "BAJA" : "ACTIVO";
        String accion = esActivo ? "dar de baja" : "dar de alta";

        // Validar si puede darse de baja (cuando está ACTIVO)
        if (esActivo) {
            List<Integer> idsProyectos = AnteproyectoDatos.obtenerIdsProyectosPorEmpresa(seleccionada.getRfc());
            if (!idsProyectos.isEmpty() && AnteproyectoDatos.algunProyectoConEstudiantes(idsProyectos)) {
                Alert err = new Alert(Alert.AlertType.ERROR,
                    "No es posible dar de baja a la empresa ya que al menos uno de sus proyectos está vinculado a un estudiante.",
                    ButtonType.OK);
                err.setHeaderText(null);
                err.showAndWait();
                return;
            }
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
            "¿Seguro que deseas " + accion + " esta empresa?",
            ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirmación");
        alert.setHeaderText(null);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            boolean ok = EmpresaDatos.cambiarEstatusEmpresa(seleccionada.getRfc(), nuevoEstatus);
            if (ok) {
                cargarTabla();
            } else {
                Alert err = new Alert(Alert.AlertType.ERROR, "No se pudo cambiar el estatus.", ButtonType.OK);
                err.showAndWait();
            }
        }
    }

    // Detalles de empresa
    private void mostrarDetalles(Empresa empresa) {
        List<String> proyectos = AnteproyectoDatos.obtenerTitulosProyectosPorEmpresa(empresa.getRfc());
        DetallesEmpresaDialog dialog = new DetallesEmpresaDialog(empresa, proyectos);
        dialog.showAndWait();
    }
}
