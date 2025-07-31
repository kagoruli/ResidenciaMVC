/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

/**
 *
 * @author jafet
 */
import controlador.ControladorDocente;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.input.KeyEvent; // Importación añadida
import java.util.regex.Pattern;    // Importación añadida

public class FormularioDocente extends VBox {
    // Patrón para validar solo letras y espacios
    private static final Pattern LETTERS_ONLY = Pattern.compile("[\\p{L}\\s]*");
    
    public FormularioDocente() {
        super(20);
        getStyleClass().add("form-container");
        setPadding(new Insets(30));
        setAlignment(Pos.TOP_CENTER);

        Label titulo = new Label("AGREGAR DOCENTE");
        titulo.getStyleClass().add("form-title");

        GridPane formulario = new GridPane();
        formulario.getStyleClass().add("form-grid");
        formulario.setHgap(20);
        formulario.setVgap(15);
        formulario.setPadding(new Insets(20));

        int rowIndex = 0;

        formulario.add(createLabel("NOMBRE"), 0, rowIndex);
        TextField txtNombre = createTextField();
        // Filtro para solo letras y espacios
        txtNombre.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!LETTERS_ONLY.matcher(event.getCharacter()).matches()) {
                event.consume();
            }
        });
        formulario.add(txtNombre, 1, rowIndex++);

        formulario.add(createLabel("APELLIDO PATERNO"), 0, rowIndex);
        TextField txtPaterno = createTextField();
        // Filtro para solo letras y espacios
        txtPaterno.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!LETTERS_ONLY.matcher(event.getCharacter()).matches()) {
                event.consume();
            }
        });
        formulario.add(txtPaterno, 1, rowIndex++);

        formulario.add(createLabel("APELLIDO MATERNO"), 0, rowIndex);
        TextField txtMaterno = createTextField();
        // Filtro para solo letras y espacios
        txtMaterno.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!LETTERS_ONLY.matcher(event.getCharacter()).matches()) {
                event.consume();
            }
        });
        formulario.add(txtMaterno, 1, rowIndex++);

        formulario.add(createLabel("NÚMERO DE TARJETA"), 0, rowIndex);
        TextField txtTarjeta = createTextField();
        formulario.add(txtTarjeta, 1, rowIndex++);
        txtTarjeta.addEventFilter(KeyEvent.KEY_TYPED, e -> {
            // Solo permite letras y números
            if (!e.getCharacter().matches("[a-zA-Z0-9]")) {
                e.consume();
            }
            // Limita a 10 caracteres
            if (txtTarjeta.getText().length() >= 10) {
                e.consume();
            }
        });
        
        formulario.add(createLabel("CORREO ELECTRÓNICO"), 0, rowIndex);
        TextField txtEmail = createTextField();
        formulario.add(txtEmail, 1, rowIndex++);

        HBox botones = new HBox(20);
        botones.setAlignment(Pos.CENTER);

        Button btnAceptar = createButton("AGREGAR");
        Button btnLimpiar = createButton("LIMPIAR");
        botones.getChildren().addAll(btnAceptar, btnLimpiar);

        btnAceptar.setOnAction(e -> {
            String resultado = ControladorDocente.registrarDocente(
                txtNombre.getText(),
                txtPaterno.getText(),
                txtMaterno.getText(),
                txtEmail.getText(),
                txtTarjeta.getText()
            );
            if (resultado.startsWith("ÉXITO:")) {
                String contrasena = resultado.substring(6);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Docente agregado exitosamente",
                        "Usuario: " + txtTarjeta.getText() + "\nContraseña: " + contrasena);
                txtNombre.clear();
                txtPaterno.clear();
                txtMaterno.clear();
                txtTarjeta.clear();
                txtEmail.clear();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", resultado);
            }
        });

        btnLimpiar.setOnAction(e -> {
            txtNombre.clear();
            txtPaterno.clear();
            txtMaterno.clear();
            txtTarjeta.clear();
            txtEmail.clear();
        });

        getChildren().addAll(titulo, formulario, botones);
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("form-label");
        return label;
    }

    private TextField createTextField() {
        TextField textField = new TextField();
        textField.getStyleClass().add("form-textfield");
        return textField;
    }

    private Button createButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("form-button");
        btn.setPrefSize(150, 40);
        return btn;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
