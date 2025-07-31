/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

/**
 *
 * @author jafet
 */

import javafx.scene.control.*;
import javafx.scene.layout.*;
import controlador.ControladorAnteproyecto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.StageStyle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import javafx.scene.control.TextFormatter;

public class FormularioAnteproyecto extends VBox {

    private TextField tituloField;
    private TextField cupoField;
    private ComboBox<String> tipoCombo;
    private TextArea descripcionArea;
    private ComboBox<String> empresaCombo;
    private Button btnGuardar;
    private Button btnLimpiar;
    
    private ControladorAnteproyecto controlador;
    private boolean empresasRegistradas = false;  // Nuevo estado para controlar empresas

    public FormularioAnteproyecto() {
        super();
        controlador = new ControladorAnteproyecto(this);
        
        // Primero verificamos si hay empresas registradas
        verificarEmpresasRegistradas();
        
        // Solo creamos la UI si hay empresas
        if (empresasRegistradas) {
            crearUI();
            aplicarEstilos();
        } else {
            mostrarErrorNoEmpresas();
        }
    }

    // Nuevo método para verificar si hay empresas registradas
    private void verificarEmpresasRegistradas() {
    List<String> empresas = controlador.obtenerEmpresas();
    empresasRegistradas = !empresas.isEmpty();
}

    // Nuevo método para mostrar error cuando no hay empresas
    private void mostrarErrorNoEmpresas() {
        VBox contenedorError = new VBox(20);
        contenedorError.setAlignment(Pos.CENTER);
        contenedorError.setPadding(new Insets(50));
        
        Label titulo = new Label("No se puede registrar Proyectos");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titulo.getStyleClass().add("form-title");
        
        Label mensaje = new Label("No hay empresas registradas. Debe registrar al menos una empresa primero.");
        mensaje.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        mensaje.setWrapText(true);
        mensaje.setAlignment(Pos.CENTER);
        mensaje.setMaxWidth(600);
        
        
        
        contenedorError.getChildren().addAll(titulo, mensaje);
        this.getChildren().add(contenedorError);
    }

    private void crearUI() {
        setSpacing(20);
        setPadding(new Insets(20));

        // Título del formulario
        Label titulo = new Label("Registrar Nuevo Proyecto");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titulo.getStyleClass().add("form-title");

        // Contenedor de campos
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        grid.getStyleClass().add("form-grid");

        tituloField = new TextField();
        tituloField.setPromptText("Ingrese el título del Proyecto");
        aplicarFiltroTexto(tituloField, "[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*"); // Letras, números y algunos símbolos
        
        cupoField = new TextField();
        cupoField.setPromptText("Ingrese el cupo máximo de estudiantes");
        aplicarFiltroNumerico(cupoField); // Solo números enteros
        
        tipoCombo = new ComboBox<>();
        tipoCombo.setPromptText("Seleccione el tipo");
        ObservableList<String> tipos = FXCollections.observableArrayList(
            "Presencial", "Remota", "Hibrido"
        );
        tipoCombo.setItems(tipos);
        
        descripcionArea = new TextArea();
        descripcionArea.setPromptText("Describa el Proyecto");
        descripcionArea.setWrapText(true);
        descripcionArea.setPrefRowCount(5);
        
        empresaCombo = new ComboBox<>();
        empresaCombo.setPromptText("Seleccione la empresa");

        // Agregar campos al grid
        grid.add(createLabel("TÍTULO:"), 0, 0);
        grid.add(tituloField, 1, 0);
        GridPane.setHgrow(tituloField, Priority.ALWAYS);
        
        grid.add(createLabel("CUPO MÁXIMO:"), 0, 1);
        grid.add(cupoField, 1, 1);
        GridPane.setHgrow(cupoField, Priority.ALWAYS);
        
        grid.add(createLabel("TIPO:"), 0, 2);
        grid.add(tipoCombo, 1, 2);
        GridPane.setHgrow(tipoCombo, Priority.ALWAYS);
        
        grid.add(createLabel("DESCRIPCIÓN:"), 0, 3);
        grid.add(descripcionArea, 1, 3);
        GridPane.setHgrow(descripcionArea, Priority.ALWAYS);
        GridPane.setVgrow(descripcionArea, Priority.ALWAYS);
        
        grid.add(createLabel("EMPRESA:"), 0, 4);
        grid.add(empresaCombo, 1, 4);
        GridPane.setHgrow(empresaCombo, Priority.ALWAYS);

        // Contenedor de botones
        HBox contenedorBotones = new HBox(20);
        contenedorBotones.setAlignment(Pos.CENTER);
        contenedorBotones.setPadding(new Insets(20, 0, 0, 0));
        
        btnGuardar = new Button("AGREGAR");
        btnGuardar.getStyleClass().add("form-button");
        btnGuardar.setPrefWidth(180);
        
        btnLimpiar = new Button("LIMPIAR");
        btnLimpiar.getStyleClass().add("form-button");
        btnLimpiar.setPrefWidth(180);
        
        contenedorBotones.getChildren().addAll(btnLimpiar, btnGuardar);

        // Agregar componentes al formulario
        this.getChildren().addAll(titulo, grid, contenedorBotones);
        
        // Configurar eventos
        configurarEventos();
        
        // Cargar empresas desde la base de datos
        cargarEmpresas();
    }

    // Métodos nuevos para aplicar filtros de validación
    private void aplicarFiltroTexto(TextField textField, String regex) {
        Pattern pattern = Pattern.compile(regex);
        UnaryOperator<TextFormatter.Change> filtro = change -> {
            if (pattern.matcher(change.getControlNewText()).matches()) {
                return change;
            }
            return null;
        };
        textField.setTextFormatter(new TextFormatter<>(filtro));
    }

    private void aplicarFiltroNumerico(TextField textField) {
        Pattern pattern = Pattern.compile("\\d*"); // Solo dígitos
        UnaryOperator<TextFormatter.Change> filtro = change -> {
            if (pattern.matcher(change.getControlNewText()).matches()) {
                return change;
            }
            return null;
        };
        textField.setTextFormatter(new TextFormatter<>(filtro));
    }
    
    // Método para cargar empresas desde la base de datos
    private void cargarEmpresas() {
    List<String> empresas = controlador.obtenerEmpresas();
    if (empresas.isEmpty()) {
        mostrarError("Error", "No hay empresas registradas");
        btnGuardar.setDisable(true);
    } else {
        empresaCombo.setItems(FXCollections.observableArrayList(empresas));
    }
}


    // Método para configurar eventos
    private void configurarEventos() {
        btnLimpiar.setOnAction(e -> {
            tituloField.clear();
            cupoField.clear();
            tipoCombo.getSelectionModel().clearSelection();
            descripcionArea.clear();
            empresaCombo.getSelectionModel().clearSelection();
        });

        btnGuardar.setOnAction(e -> {
            if (validarCampos()) {
                controlador.registrarAnteproyecto(
                    tituloField.getText(),
                    cupoField.getText(),
                    tipoCombo.getValue(),
                    descripcionArea.getText(),
                    empresaCombo.getValue()
                );
            }
        });
    }

    // Método de validación
    private boolean validarCampos() {
        if (tituloField.getText().isEmpty()) {
            mostrarError("Título requerido", "Debe ingresar el título del Proyecto");
            tituloField.requestFocus();
            return false;
        }

        if (cupoField.getText().isEmpty()) {
            mostrarError("Cupo requerido", "Debe ingresar el cupo máximo de estudiantes");
            cupoField.requestFocus();
            return false;
        } else {
            try {
                int cupo = Integer.parseInt(cupoField.getText());
                if (cupo <= 0) {
                    mostrarError("Cupo inválido", "El cupo debe ser un número mayor a cero");
                    cupoField.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                mostrarError("Cupo inválido", "El cupo debe ser un número entero");
                cupoField.requestFocus();
                return false;
            }
        }

        if (tipoCombo.getValue() == null) {
            mostrarError("Tipo requerido", "Debe seleccionar el tipo de Proyecto");
            tipoCombo.requestFocus();
            return false;
        }

        if (descripcionArea.getText().isEmpty()) {
            mostrarError("Descripción requerida", "Debe ingresar una descripción del Pproyecto");
            descripcionArea.requestFocus();
            return false;
        }

        if (empresaCombo.getValue() == null) {
            mostrarError("Empresa requerida", "Debe seleccionar una empresa asociada");
            empresaCombo.requestFocus();
            return false;
        }

        return true;
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("form-label");
        return label;
    }
    
    // Método para mostrar errores con el nuevo estilo
    public void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        
        // Personalizar el diálogo para que coincida con la imagen
        alert.initStyle(StageStyle.UTILITY);
        alert.getDialogPane().getStyleClass().add("error-dialog");
        
        // Configurar el botón
        ButtonType aceptarButton = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(aceptarButton);
        
        // Estilizar el botón
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/vista/style.css").toExternalForm());
        dialogPane.getStyleClass().add("error-dialog");
        
        // Mostrar la alerta
        alert.showAndWait();
    }

    // Método para mostrar confirmación con el nuevo estilo
    public void mostrarConfirmacion() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registro exitoso");
        alert.setHeaderText(null);
        alert.setContentText("Proyecto registrado correctamente");
        
        // Personalizar el diálogo
        alert.initStyle(StageStyle.UTILITY);
        
        // Configurar el botón
        ButtonType aceptarButton = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(aceptarButton);
        
        // Estilizar el botón
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/vista/style.css").toExternalForm());
        dialogPane.getStyleClass().add("success-dialog");
        
        // Mostrar la alerta
        alert.showAndWait();

        // Limpiar después de guardar
        btnLimpiar.fire();
    }
    
    
    private void aplicarEstilos() {
        this.getStyleClass().add("form-container");
    }

    // Getters para los campos
    public TextField getTituloField() {
        return tituloField;
    }

    public TextField getCupoField() {
        return cupoField;
    }

    public ComboBox<String> getTipoCombo() {
        return tipoCombo;
    }

    public TextArea getDescripcionArea() {
        return descripcionArea;
    }

    public ComboBox<String> getEmpresaCombo() {
        return empresaCombo;
    }

    public Button getBtnGuardar() {
        return btnGuardar;
    }

    public Button getBtnLimpiar() {
        return btnLimpiar;
    }
}



