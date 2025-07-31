/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

/**
 *
 * @author jafet
 */

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.EstudianteDatos;
import modelo.EstudianteVista;
import controlador.ControladorEstudiante;
import java.util.regex.Pattern;

public class FormularioEditarEstudiante extends Stage {
    private static final Pattern LETTERS_ONLY = Pattern.compile("[\\p{L}\\s]*");
    private String numeroControlOriginal;
    
    public FormularioEditarEstudiante(String numeroControl, Runnable onActualizar) {
        this.numeroControlOriginal = numeroControl;
        
        setTitle("Editar Estudiante");
        setResizable(false);
        initModality(Modality.APPLICATION_MODAL);

        VBox root = new VBox(16);
        root.setPadding(new Insets(32));
        root.setAlignment(Pos.TOP_CENTER);

        Label lblTitulo = new Label("EDITAR ESTUDIANTE");
        lblTitulo.getStyleClass().add("form-title");

        GridPane formulario = new GridPane();
        formulario.getStyleClass().add("form-grid");
        formulario.setHgap(20);
        formulario.setVgap(15);
        formulario.setPadding(new Insets(20));

        int row = 0;

        TextField txtNombre = createTextField();
        formulario.add(createLabel("NOMBRE"), 0, row);
        formulario.add(txtNombre, 1, row++);
        txtNombre.addEventFilter(KeyEvent.KEY_TYPED, e -> {
            if (!LETTERS_ONLY.matcher(e.getCharacter()).matches()) e.consume();
        });

        TextField txtPaterno = createTextField();
        formulario.add(createLabel("APELLIDO PATERNO"), 0, row);
        formulario.add(txtPaterno, 1, row++);
        txtPaterno.addEventFilter(KeyEvent.KEY_TYPED, e -> {
            if (!LETTERS_ONLY.matcher(e.getCharacter()).matches()) e.consume();
        });

        TextField txtMaterno = createTextField();
        formulario.add(createLabel("APELLIDO MATERNO"), 0, row);
        formulario.add(txtMaterno, 1, row++);
        txtMaterno.addEventFilter(KeyEvent.KEY_TYPED, e -> {
            if (!LETTERS_ONLY.matcher(e.getCharacter()).matches()) e.consume();
        });

        TextField txtControl = createTextField();
        txtControl.setEditable(true);
        formulario.add(createLabel("NÚMERO DE CONTROL"), 0, row);
        formulario.add(txtControl, 1, row++);
        txtControl.addEventFilter(KeyEvent.KEY_TYPED, e -> {
            // Solo permite letras y números
            if (!e.getCharacter().matches("[a-zA-Z0-9]")) {
                e.consume();
            }
            // Limita a 10 caracteres
            if (txtControl.getText().length() >= 10) {
                e.consume();
            }
        });
        
        
        TextField txtCorreo = createTextField();
        formulario.add(createLabel("CORREO ELECTRÓNICO"), 0, row);
        formulario.add(txtCorreo, 1, row++);

        // Cargar datos
        EstudianteVista estudiante = EstudianteDatos.buscarEstudianteVista(numeroControl);
        if (estudiante != null) {
            txtNombre.setText(estudiante.getNombre());
            txtPaterno.setText(estudiante.getApellidoPaterno());
            txtMaterno.setText(estudiante.getApellidoMaterno());
            txtControl.setText(estudiante.getNumeroControl());
            txtCorreo.setText(estudiante.getCorreo());
        }

        Button btnActualizar = createButton("ACTUALIZAR");
        Button btnCancelar = createButton("CANCELAR");

        HBox botones = new HBox(24, btnActualizar, btnCancelar);
        botones.setAlignment(Pos.CENTER);

        root.getChildren().addAll(lblTitulo, formulario, botones);

        // === Eventos ===
        btnActualizar.setOnAction(e -> {
            String nombre = txtNombre.getText().trim();
            String paterno = txtPaterno.getText().trim();
            String materno = txtMaterno.getText().trim();
            String correo = txtCorreo.getText().trim();
            String control = txtControl.getText().trim();

            String resultado = ControladorEstudiante.editarEstudiante(
                numeroControlOriginal,
                txtControl.getText().trim(),
                txtNombre.getText().trim(),
                txtPaterno.getText().trim(),
                txtMaterno.getText().trim(),
                txtCorreo.getText().trim()
            );

            if (resultado.startsWith("ÉXITO:")) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Actualización exitosa", "El estudiante fue actualizado correctamente.");
                close();
                if (onActualizar != null) onActualizar.run(); // Refresca la lista en la ventana principal si se pasa ese callback
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error al actualizar", resultado);
            }
        });

        btnCancelar.setOnAction(e -> close());

        Scene scene = new Scene(root, 410, 410);
        scene.getStylesheets().add(getClass().getResource("/vista/style.css").toExternalForm());
        setScene(scene);
    }
    
    
    
    private Label createLabel(String txt) {
        Label lbl = new Label(txt);
        lbl.getStyleClass().add("form-label");
        return lbl;
    }

    private TextField createTextField() {
        TextField txt = new TextField();
        txt.getStyleClass().add("form-textfield");
        return txt;
    }

    private Button createButton(String txt) {
        Button btn = new Button(txt);
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

