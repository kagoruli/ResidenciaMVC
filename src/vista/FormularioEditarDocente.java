/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.DocenteDatos;
import modelo.DocenteVista;
import controlador.ControladorDocente;
import java.util.regex.Pattern;


/**
 *
 * @author jafet
 */
public class FormularioEditarDocente extends Stage {
    private static final Pattern LETTERS_ONLY = Pattern.compile("[\\p{L}\\s]*");
    private String numeroTarjetaOriginal;
    
    public FormularioEditarDocente(String numeroTarjeta, Runnable onActualizar) {
        this.numeroTarjetaOriginal = numeroTarjeta;
        
        setTitle("Editar Docente");
        setResizable(false);
        initModality(Modality.APPLICATION_MODAL);

        VBox root = new VBox(16);
        root.setPadding(new Insets(32));
        root.setAlignment(Pos.TOP_CENTER);

        Label lblTitulo = new Label("EDITAR DOCENTE");
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

        TextField txtTarjeta = createTextField();
        txtTarjeta.setEditable(true);
        formulario.add(createLabel("NÚMERO DE TARJETA"), 0, row);
        formulario.add(txtTarjeta, 1, row++);
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
        
        
        TextField txtCorreo = createTextField();
        formulario.add(createLabel("CORREO ELECTRÓNICO"), 0, row);
        formulario.add(txtCorreo, 1, row++);

        // Cargar datos
        DocenteVista docente = DocenteDatos.buscarDocenteVista(numeroTarjeta);
        if (docente != null) {
            txtNombre.setText(docente.getNombre());
            txtPaterno.setText(docente.getApellidoPaterno());
            txtMaterno.setText(docente.getApellidoMaterno());
            txtTarjeta.setText(docente.getNumeroTarjeta());
            txtCorreo.setText(docente.getCorreo());
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
            String control = txtTarjeta.getText().trim();

            String resultado = ControladorDocente.editarDocente(
                numeroTarjetaOriginal,
                txtTarjeta.getText().trim(),
                txtNombre.getText().trim(),
                txtPaterno.getText().trim(),
                txtMaterno.getText().trim(),
                txtCorreo.getText().trim()
            );

            if (resultado.startsWith("ÉXITO:")) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Actualización exitosa", "El docente fue actualizado correctamente.");
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
