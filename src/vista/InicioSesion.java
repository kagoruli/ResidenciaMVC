/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

/**
 *
 * @author jafet
 */

import controlador.ControladorInicioSesion;
import controlador.ControladorRecuperacion;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.stage.Modality;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;

public class InicioSesion extends Application {

    // Contadores y bloqueo
    private int intentosFallidos = 0;
    private boolean bloqueado = false;

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");

        // Header
        HBox header = new HBox(40);
        header.getStyleClass().add("header-bar");
        ImageView logoTecNM = new ImageView(new Image(getClass().getResourceAsStream("/vista/img/logo_tecnm.png")));
        logoTecNM.setFitHeight(60);
        logoTecNM.setPreserveRatio(true);

        ImageView logoITO = new ImageView(new Image(getClass().getResourceAsStream("/vista/img/logo_ito.png")));
        logoITO.setFitHeight(60);
        logoITO.setPreserveRatio(true);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(logoTecNM, spacer, logoITO);
        root.setTop(header);

        // Formulario de login
        VBox loginBox = new VBox(20);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setMaxWidth(400);
        loginBox.getStyleClass().add("login-card");

        Label title = new Label("Residencia Profesional");
        title.getStyleClass().add("title");

        TextField matricula = new TextField();
        matricula.setPromptText("Número de Tarjeta o Número de Control");
        matricula.getStyleClass().add("input-field");
        
        matricula.addEventFilter(KeyEvent.KEY_TYPED, e -> {
            // Solo permite letras y números
            if (!e.getCharacter().matches("[a-zA-Z0-9]")) {
                e.consume();
            }
            // Limita a 10 caracteres
            if (matricula.getText().length() >= 10) {
                e.consume();
            }
        });
        
        PasswordField password = new PasswordField();
        password.setPromptText("Contraseña");
        password.getStyleClass().add("input-field");
        
        password.addEventFilter(KeyEvent.KEY_TYPED, e -> {
            // Limita a 30 caracteres
            if (password.getText().length() >= 30) {
                e.consume();
            }
        });
        
        Button loginBtn = new Button("Ingresar");
        loginBtn.getStyleClass().add("login-button");

        // Link de recuperación
        Hyperlink linkOlvidaste = new Hyperlink("¿Olvidaste tu usuario o contraseña?");
        linkOlvidaste.setOnAction(e -> mostrarDialogoRecuperacion());

        loginBtn.setOnAction(e -> {
    if (bloqueado) {
        mostrarAlerta(Alert.AlertType.WARNING, "Bloqueado",
                "Demasiados intentos fallidos. Espere unos segundos antes de intentar de nuevo.");
        return;
    }

    String inputMatricula = matricula.getText().trim();
    String inputPassword = password.getText().trim();

    if (inputMatricula.isEmpty() || inputPassword.isEmpty()) {
        mostrarAlerta(Alert.AlertType.WARNING, "Campos Vacíos", "Por favor completa todos los campos.");
        return;
    }

    // Verifica las credenciales y obtiene el rol/estado
    String rol = ControladorInicioSesion.validarCredenciales(inputMatricula, inputPassword);

    // NUEVO: Mensajes claros según estado
    if ("ESTADO_BAJA".equals(rol)) {
        mostrarAlerta(Alert.AlertType.ERROR, "Acceso Denegado", "El usuario está dado de baja.");
        return;
    }
    if ("ESTADO_FINALIZADO".equals(rol)) {
        mostrarAlerta(Alert.AlertType.INFORMATION, "Acceso Denegado", "El usuario ya ha finalizado el proceso de Residencia Profesional.");
        return;
    }

    if (rol != null) {
        // Si es un estudiante, abre la pantalla correspondiente
        if (rol.equalsIgnoreCase("estudiante")) {
            new PantallaPrincipalEstudiante(inputMatricula).show();
        } else if (rol.equalsIgnoreCase("docente")) {
            new PantallaPrincipalDocente(inputMatricula).show();
        } else if (rol.equalsIgnoreCase("jefatura de vinculación y proyectos")) {
            new PantallaPrincipal(inputMatricula).show();
        }
        else {
            mostrarAlerta(Alert.AlertType.ERROR, "Rol no válido", "Este rol no está permitido para el acceso.");
        }
        // Cierra la ventana de inicio de sesión
        stage.close();
    } else {
        intentosFallidos++;
        mostrarAlerta(Alert.AlertType.ERROR, "Acceso Denegado",
                "Matrícula o contraseña incorrectos. Intente nuevamente.");

        if (intentosFallidos >= 5) {
            bloquearLogin(loginBtn);
        }
    }
});

        loginBox.getChildren().addAll(title, matricula, password, loginBtn, linkOlvidaste);
        VBox centerWrap = new VBox(loginBox);
        centerWrap.setAlignment(Pos.CENTER);
        centerWrap.setPadding(new Insets(30));
        root.setCenter(centerWrap);

        FadeTransition ft = new FadeTransition(Duration.millis(800), loginBox);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        Scene scene = new Scene(root, 850, 550);
        scene.getStylesheets().add(getClass().getResource("/vista/style.css").toExternalForm());

        stage.setTitle("Iniciar Sesión - Residencia Profesional");
        stage.setScene(scene);
        stage.show();
    }

    private void bloquearLogin(Button loginBtn) {
        bloqueado = true;
        loginBtn.setDisable(true);

        // Crear ventana modal con cuenta regresiva
        Stage bloqueoStage = new Stage();
        bloqueoStage.initModality(Modality.APPLICATION_MODAL);
        bloqueoStage.setTitle("Demasiados intentos");

        VBox bloqueoBox = new VBox(18);
        bloqueoBox.setAlignment(Pos.CENTER);
        bloqueoBox.setPadding(new Insets(25));

        Label mensaje = new Label();
        mensaje.setFont(new Font(16));
        mensaje.setTextFill(Color.DARKRED);

        bloqueoBox.getChildren().add(mensaje);

        Scene scene = new Scene(bloqueoBox, 370, 120);
        bloqueoStage.setScene(scene);

        // Timeline para cuenta regresiva
        final int[] seconds = {30};
        mensaje.setText("Se ha excedido el número de intentos.\nEspere " + seconds[0] + " segundos.");

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
            seconds[0]--;
            if (seconds[0] > 0) {
                mensaje.setText("Se ha excedido el número de intentos.\nEspere " + seconds[0] + " segundos.");
            } else {
                bloqueoStage.close();
                bloqueado = false;
                intentosFallidos = 0;
                loginBtn.setDisable(false);
            }
        }));
        timeline.setCycleCount(30);
        timeline.play();

        bloqueoStage.showAndWait();
    }

    private void mostrarDialogoRecuperacion() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Recuperar usuario o contraseña");
        dialog.setHeaderText("Ingresa tu correo electrónico registrado:");
        dialog.setContentText("Correo:");

        dialog.showAndWait().ifPresent(correo -> {
            if (correo.trim().isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Campo vacío", "Debes ingresar un correo electrónico.");
                return;
            }
            String mensaje = ControladorRecuperacion.recuperarUsuarioYContrasena(correo.trim());
            mostrarAlerta(Alert.AlertType.INFORMATION, "Recuperación", mensaje);
        });
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

