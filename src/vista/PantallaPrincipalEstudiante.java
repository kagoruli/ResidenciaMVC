/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

/**
 *
 * @author jafet
 */

import controlador.ControladorBancoAnteproyectos;
import controlador.ControladorMiAnteproyecto;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import modelo.Perfil;
import modelo.PerfilDatos;

public class PantallaPrincipalEstudiante {
    private Stage stage;
    private StackPane contenidoCentral;
    private final String usuarioLogueado;
    private VBox subMenuAnteproyecto;
    
    // Transiciones para animaciones
    private ScaleTransition scaleAnteproyecto;
    private FadeTransition fadeAnteproyecto;

    public PantallaPrincipalEstudiante(String usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
    }

    public void show() {
        stage = new Stage();
        stage.setTitle("Sistema de Residencias Profesionales");

        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");

        HBox header = createHeader();
        root.setTop(header);

        VBox menuLateral = createMenuLateral();
        root.setLeft(menuLateral);

        contenidoCentral = new StackPane();
        contenidoCentral.getStyleClass().add("content-container");
        mostrarBienvenida();
        root.setCenter(contenidoCentral);

        Scene scene = new Scene(root, 1100, 825);
        scene.getStylesheets().add(getClass().getResource("/vista/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.getStyleClass().add("header-bar");
        header.setPadding(new Insets(10));
        header.setAlignment(Pos.CENTER_LEFT);

        ImageView logoTecNM = loadImage("/vista/img/logo_tecnm.png", 50);
        ImageView logoITO = loadImage("/vista/img/logo_ito.png", 50);

        Label tituloSistema = new Label("JAFOS ENTERPRISE");
        tituloSistema.getStyleClass().add("system-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        if (logoTecNM != null && logoITO != null) {
            header.getChildren().addAll(logoTecNM, tituloSistema, spacer, logoITO);
        } else {
            header.getChildren().addAll(tituloSistema, spacer);
        }

        return header;
    }

    private ImageView loadImage(String path, double height) {
        try (var is = getClass().getResourceAsStream(path)) {
            if (is != null) {
                Image image = new Image(is);
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(height);
                imageView.setPreserveRatio(true);
                return imageView;
            } else {
                System.err.println("Imagen no encontrada: " + path);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error cargando imagen: " + e.getMessage());
            return null;
        }
    }

    private VBox createMenuLateral() {
        VBox menuLateral = new VBox(10);
        menuLateral.getStyleClass().add("menu-lateral");
        menuLateral.setPrefWidth(275);
        menuLateral.setPadding(new Insets(20, 10, 20, 10));

        Button btnInicio = createMenuButton("Inicio", "inicio");
        Button btnMiPerfil = createMenuButton("Mi perfil", "perfil");
        Button btnCerrarSesion = createMenuButton("Cerrar Sesión", "cerrar_sesion");
        Button btnAnteproyecto = createMenuButton("Proyecto", "anteproyectos");
        
        subMenuAnteproyecto = createAnteproyectoSubMenu();
        
        // Configurar animaciones para el submenú
        configurarAnimacionSubMenu(subMenuAnteproyecto);
        
        btnInicio.setOnAction(e -> mostrarBienvenida());
        btnMiPerfil.setOnAction(e -> mostrarPerfil());
        btnAnteproyecto.setOnAction(e -> toggleSubMenu(subMenuAnteproyecto));
        btnCerrarSesion.setOnAction(e -> cerrarSesion());

        menuLateral.getChildren().addAll(
                btnInicio,
                btnAnteproyecto,
                subMenuAnteproyecto,
                btnMiPerfil,
                new Separator(),
                btnCerrarSesion
        );

        return menuLateral;
    }
    
    private void configurarAnimacionSubMenu(VBox subMenu) {
        // Configurar estado inicial
        subMenu.setVisible(false);
        subMenu.setManaged(false);
        subMenu.setScaleY(0);
        subMenu.setOpacity(0);
        
        // Crear transiciones
        scaleAnteproyecto = new ScaleTransition(Duration.millis(300), subMenu);
        fadeAnteproyecto = new FadeTransition(Duration.millis(200), subMenu);
    }

    private Button createMenuButton(String texto, String iconName) {
        Button btn = new Button(texto);
        btn.getStyleClass().add("menu-button");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);

        ImageView icon = loadIcon(iconName, 30);
        if (icon != null) {
            btn.setGraphic(icon);
            btn.setContentDisplay(ContentDisplay.LEFT);
            btn.setGraphicTextGap(10);
        }

        return btn;
    }

    private ImageView loadIcon(String iconName, double size) {
        try (var is = getClass().getResourceAsStream("/vista/icons/" + iconName + ".png")) {
            if (is != null) {
                Image iconImage = new Image(is);
                ImageView icon = new ImageView(iconImage);
                icon.setFitHeight(size);
                icon.setPreserveRatio(true);
                return icon;
            }
            System.err.println("Icono no encontrado: " + iconName);
            return null;
        } catch (Exception e) {
            System.err.println("Error cargando icono: " + e.getMessage());
            return null;
        }
    }

    private void mostrarPerfil() {
        Perfil perfil = PerfilDatos.obtenerPerfilPorUsuario(usuarioLogueado);
        VistaPerfil vistaPerfil = new VistaPerfil(perfil);

        FadeTransition ft = new FadeTransition(Duration.millis(500), vistaPerfil);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        contenidoCentral.getChildren().setAll(vistaPerfil);
    }

    private void mostrarBienvenida() {
        BorderPane contenidoPrincipal = new BorderPane();
        contenidoPrincipal.setPadding(new Insets(30));

        Label titulo = new Label("Bienvenido");
        titulo.getStyleClass().add("welcome-title");
        BorderPane.setAlignment(titulo, Pos.TOP_LEFT);
        BorderPane.setMargin(titulo, new Insets(0, 0, 20, 30));

        StackPane tituloContainer = new StackPane(titulo);
        tituloContainer.setAlignment(Pos.TOP_LEFT);

        contenidoPrincipal.setTop(tituloContainer);

        ImageView marcaAgua = loadImage("/vista/img/logo_empresa.png", 400);
        if (marcaAgua != null) {
            marcaAgua.setOpacity(0.15);
            marcaAgua.setBlendMode(BlendMode.MULTIPLY);
            StackPane.setAlignment(marcaAgua, Pos.CENTER);
            contenidoPrincipal.setCenter(marcaAgua);
        }

        FadeTransition ft = new FadeTransition(Duration.millis(1000), contenidoPrincipal);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        contenidoCentral.getChildren().setAll(contenidoPrincipal);
    }
    
    private VBox createAnteproyectoSubMenu() {
        // Nuevo submenú con dos opciones
        VBox subMenu = createSubMenu(
            new String[]{"Banco de Proyectos", "banco_proyecto"},
            new String[]{"Propuesta Propia", "propuesta_propia"},
            new String[]{"Mi Proyecto", "mi_proyecto"}
        );
        
        // Configurar acciones para los nuevos botones
        ((Button) subMenu.getChildren().get(0)).setOnAction(e -> mostrarBancoAnteproyectos());
        ((Button) subMenu.getChildren().get(1)).setOnAction(e -> mostrarPropuestaPropia());    //cambia mostrarMiAnteproyecto a la vista para adjuntar una propuesta propia
        ((Button) subMenu.getChildren().get(2)).setOnAction(e -> mostrarMiAnteproyecto());
        
        return subMenu;
    }
     
    private VBox createSubMenu(String[]... opciones) {
        VBox subMenu = new VBox(5);
        subMenu.getStyleClass().add("submenu");
        subMenu.setVisible(false);
        subMenu.setManaged(false);

        for (String[] opcion : opciones) {
            Button btn = new Button(opcion[0]);
            btn.getStyleClass().add("submenu-button");
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setAlignment(Pos.CENTER_LEFT);
            btn.setPadding(new Insets(5, 5, 5, 30));

            ImageView icon = loadIcon(opcion[1], 28);
            if (icon != null) {
                btn.setGraphic(icon);
                btn.setContentDisplay(ContentDisplay.LEFT);
                btn.setGraphicTextGap(8);
            }

            subMenu.getChildren().add(btn);
        }

        return subMenu;
    }
    
    private void toggleSubMenu(VBox subMenu) {
        boolean visible = subMenu.isVisible();
        
        if (!visible) {
            abrirSubMenu(subMenu);
        } else {
            cerrarSubMenu(subMenu);
        }
    }
    
    private void abrirSubMenu(VBox subMenu) {
        subMenu.setVisible(true);
        subMenu.setManaged(true);
        
        // Configurar animación de apertura
        scaleAnteproyecto.setFromY(0);
        scaleAnteproyecto.setToY(1);
        
        fadeAnteproyecto.setFromValue(0);
        fadeAnteproyecto.setToValue(1);
        
        // Ejecutar ambas animaciones en paralelo
        ParallelTransition abrir = new ParallelTransition(scaleAnteproyecto, fadeAnteproyecto);
        abrir.play();
    }
    
    private void cerrarSubMenu(VBox subMenu) {
        if (!subMenu.isVisible()) return;

        // Crear nuevas transiciones para el cierre
        ScaleTransition scaleCierre = new ScaleTransition(Duration.millis(200), subMenu);
        scaleCierre.setFromY(1);
        scaleCierre.setToY(0);
        
        FadeTransition fadeCierre = new FadeTransition(Duration.millis(150), subMenu);
        fadeCierre.setFromValue(1);
        fadeCierre.setToValue(0);
        
        // Ejecutar animaciones y ocultar al finalizar
        ParallelTransition cerrar = new ParallelTransition(scaleCierre, fadeCierre);
        cerrar.setOnFinished(e -> {
            subMenu.setVisible(false);
            subMenu.setManaged(false);
        });
        cerrar.play();
    }
    
    private void mostrarBancoAnteproyectos() {
        BancoAnteproyectos banco = new BancoAnteproyectos(usuarioLogueado);
        new ControladorBancoAnteproyectos(banco, this); // Pasamos referencia a esta pantalla principal
        
        FadeTransition ft = new FadeTransition(Duration.millis(500), banco);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        contenidoCentral.getChildren().setAll(banco);
    }
    
    /**public void mostrarMiAnteproyecto() {
        MiAnteproyecto vistaMiAnteproyecto = new MiAnteproyecto(usuarioLogueado);
        new ControladorMiAnteproyecto(vistaMiAnteproyecto, stage, usuarioLogueado);
        
        FadeTransition ft = new FadeTransition(Duration.millis(500), vistaMiAnteproyecto);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        contenidoCentral.getChildren().setAll(vistaMiAnteproyecto);
    }*/
    public void mostrarMiAnteproyecto() {
    MiAnteproyecto vista = new MiAnteproyecto(usuarioLogueado);
    new ControladorMiAnteproyecto(vista, (Stage) contenidoCentral.getScene().getWindow(), usuarioLogueado);
    contenidoCentral.getChildren().setAll(vista);
}

    public void mostrarPropuestaPropia() {
        // Si tu flujo requiere el idAnteproyecto, pásalo; si no, usa 0 o el valor correspondiente
        VistaPropuestaPropia vistaPropuestaPropia = new VistaPropuestaPropia(usuarioLogueado, 0); 
        // Si necesitas un controlador asociado, créalo aquí (como en MiAnteproyecto), si no, puedes omitirlo

        FadeTransition ft = new FadeTransition(Duration.millis(500), vistaPropuestaPropia);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        contenidoCentral.getChildren().setAll(vistaPropuestaPropia);
    }

    
    private void cerrarSesion() {
        stage.close();
        new InicioSesion().start(new Stage());
    }
}