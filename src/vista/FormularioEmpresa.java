/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

/**
 *
 * @author jafet
 */

import controlador.ControladorEmpresa;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class FormularioEmpresa extends VBox {

    private TextField nombreField;
    private TextField rfcField;
    private TextField representanteField;
    private TextField telefonoField;
    private TextArea descripcionArea;
    private Button btnGuardar;
    private Button btnLimpiar;
    
    private ControladorEmpresa controlador;

    public FormularioEmpresa() {
        super();
        controlador = new ControladorEmpresa(this);
        crearUI();
        aplicarEstilos();
    }

    private void crearUI() {
        setSpacing(20);
        setPadding(new Insets(20));

        // Título del formulario con estilo mejorado
        Label titulo = new Label("Registrar Nueva Empresa");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titulo.getStyleClass().add("form-title");

        // Contenedor de campos con espaciado consistente
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        grid.getStyleClass().add("form-grid");

        // Campos del formulario con validación
        nombreField = new TextField();
        nombreField.setPromptText("Ej: Tech Solutions");
        aplicarFiltroTexto(nombreField, "[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*"); // Solo letras y espacios
        
        rfcField = new TextField();
        rfcField.setPromptText("Ej: Tech Solutions");
        aplicarFiltroTexto(rfcField, "^[\\p{L}\\p{N}]{0,13}$");
        //aplicarFiltroTexto(rfcField, "^[\\p{L}\\p{N}]*$"); // Solo letras y numeros
        
        representanteField = new TextField();
        representanteField.setPromptText("Ej: María García");
        aplicarFiltroTexto(representanteField, "[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*"); // Solo letras y espacios
        
        telefonoField = new TextField();
        telefonoField.setPromptText("Ej: 0987654321");
        aplicarFiltroTelefono(telefonoField); // Solo números (máx. 10 dígitos)
        
        descripcionArea = new TextArea();
        descripcionArea.setPromptText("Describa las actividades principales de la empresa...");
        descripcionArea.setWrapText(true);
        descripcionArea.setPrefRowCount(5);

        // Agregar campos al grid con alineación consistente
        grid.add(createLabel("NOMBRE:"), 0, 0);
        grid.add(nombreField, 1, 0);
        GridPane.setHgrow(nombreField, Priority.ALWAYS);
        
        grid.add(createLabel("RFC:"), 0, 1);
        grid.add(rfcField, 1, 1);
        GridPane.setHgrow(rfcField, Priority.ALWAYS);
        
        grid.add(createLabel("REPRESENTANTE:"), 0, 2);
        grid.add(representanteField, 1, 2);
        GridPane.setHgrow(representanteField, Priority.ALWAYS);
        
        grid.add(createLabel("TELÉFONO:"), 0, 3);
        grid.add(telefonoField, 1, 3);
        GridPane.setHgrow(telefonoField, Priority.ALWAYS);
        
        grid.add(createLabel("DESCRIPCIÓN:"), 0, 4);
        grid.add(descripcionArea, 1, 4);
        GridPane.setHgrow(descripcionArea, Priority.ALWAYS);
        GridPane.setVgrow(descripcionArea, Priority.ALWAYS);

        // Contenedor de botones con espaciado consistente
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
        
        // Configurar acciones
        configurarEventos();
    }
    
    // Método para aplicar filtro de solo letras
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
    
    // Método para aplicar filtro de teléfono (solo números, máximo 10 dígitos)
    private void aplicarFiltroTelefono(TextField textField) {
        UnaryOperator<TextFormatter.Change> filtro = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,10}")) { // Solo dígitos con máximo 10
                return change;
            }
            return null;
        };
        textField.setTextFormatter(new TextFormatter<>(filtro));
    }
    
    // Método auxiliar para crear etiquetas consistentes
    private Label createLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("form-label");
        return label;
    }

    private void configurarEventos() {
        // Acción para limpiar el formulario
        btnLimpiar.setOnAction(e -> {
            nombreField.clear();
            rfcField.clear();
            representanteField.clear();
            telefonoField.clear();
            descripcionArea.clear();
        });
        
        // Validación de campos antes de guardar
        btnGuardar.setOnAction(e -> {
            if (validarCampos()) {
                controlador.registrarEmpresa(
                    nombreField.getText(),
                    rfcField.getText(),
                    representanteField.getText(),
                    telefonoField.getText(),
                    descripcionArea.getText()
                );
            }
        });
    }

    private boolean validarCampos() {
        if (nombreField.getText().isEmpty() && rfcField.getText().isEmpty() && representanteField.getText().isEmpty() && telefonoField.getText().isEmpty() && descripcionArea.getText().isEmpty()) {
            mostrarError("Campos vacíos", "Todos los campos son obligatorios.");
            return false;
        }
        if (nombreField.getText().isEmpty()) {
            mostrarError("Nombre requerido", "Debe ingresar el nombre de la empresa");
            nombreField.requestFocus();
            return false;
        }
        
        if (rfcField.getText().isEmpty()) {
            mostrarError("RFC requerido", "Debe ingresar el RFC de la empresa");
            rfcField.requestFocus();
            return false;
        }
        
        if (rfcField.getText().length() > 13 || rfcField.getText().length() < 12) {
            mostrarError("RFC inválido", "El RFC debe tener entre 12 y 13 caracteres alfanuméricos");
            rfcField.requestFocus();
            return false;
        }


        if (representanteField.getText().isEmpty()) {
            mostrarError("Representante requerido", "Debe ingresar el nombre del representante");
            representanteField.requestFocus();
            return false;
        }

        if (telefonoField.getText().isEmpty()) {
            mostrarError("Teléfono requerido", "Debe ingresar un número de teléfono");
            telefonoField.requestFocus();
            return false;
        }
        
        // Validación adicional para teléfono (exactamente 10 dígitos)
        if (telefonoField.getText().length() != 10) {
            mostrarError("Teléfono inválido", "El teléfono debe tener 10 dígitos");
            telefonoField.requestFocus();
            return false;
        }

        if (descripcionArea.getText().isEmpty()) {
            mostrarError("Descripción requerida", "Debe ingresar una descripción de la empresa");
            descripcionArea.requestFocus();
            return false;
        }

        return true;
    }

    public void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de validación");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void mostrarConfirmacion() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registro exitoso");
        alert.setHeaderText("Empresa registrada correctamente");
        alert.setContentText("La información se ha guardado en la base de datos.");
        alert.showAndWait();

        btnLimpiar.fire();
    }

    private void aplicarEstilos() {
        this.getStyleClass().add("form-container");
    }

    // Getters para los campos
    public TextField getNombreField() {
        return nombreField;
    }
    
    public TextField getRFCField() {
        return rfcField;
    }

    public TextField getRepresentanteField() {
        return representanteField;
    }

    public TextField getTelefonoField() {
        return telefonoField;
    }

    public TextArea getDescripcionArea() {
        return descripcionArea;
    }

    public Button getBtnGuardar() {
        return btnGuardar;
    }

    public Button getBtnLimpiar() {
        return btnLimpiar;
    }
}

