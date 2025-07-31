/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package vista;

import controlador.ControladorRevisarPropuestasJefatura;
import controlador.ControladorRevisionAnteproyectos;
import controlador.ControladorSolicitudesResidenciaJefatura;
import controlador.ControladorVistaEmpresas;
import controlador.ControladorVistaEstudiantes;
import controlador.ControladorVistaProyectos;
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

public class PantallaPrincipal {
    private Stage stage;
    private VBox subMenuDocentes;
    private VBox subMenuEstudiantes;
    private VBox subMenuAnteproyectos;
    private StackPane contenidoCentral;
    private final String usuarioLogueado;

    // Nuevas variables para controlar animaciones
    private final ScaleTransition scaleDocentes = new ScaleTransition();
    private final ScaleTransition scaleEstudiantes = new ScaleTransition();
    private final ScaleTransition scaleAnteproyectos = new ScaleTransition();
    private final FadeTransition fadeDocentes = new FadeTransition();
    private final FadeTransition fadeEstudiantes = new FadeTransition();
    private final FadeTransition fadeAnteproyectos = new FadeTransition();

    public PantallaPrincipal(String usuarioLogueado) {
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

        Scene scene = new Scene(root, 1100, 900);
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
        Button btnDocentes = createMenuButton("Docentes", "docentes");
        Button btnEstudiantes = createMenuButton("Estudiantes", "estudiantes");
        Button btnAnteproyectos = createMenuButton("Proyectos", "anteproyectos");
        Button btnMiPerfil = createMenuButton("Mi perfil", "perfil");
        Button btnCerrarSesion = createMenuButton("Cerrar Sesión", "cerrar_sesion");

        btnInicio.setOnAction(e -> mostrarBienvenida());

        subMenuDocentes = createDocentesSubMenu();
        subMenuEstudiantes = createEstudiantesSubMenu();
        subMenuAnteproyectos = createAnteproyectosSubMenu();

        // Configurar animaciones para Docentes
        configurarAnimacion(subMenuDocentes, scaleDocentes, fadeDocentes);
        configurarAnimacion(subMenuEstudiantes, scaleEstudiantes, fadeEstudiantes);
        configurarAnimacion(subMenuAnteproyectos, scaleAnteproyectos, fadeAnteproyectos);

        btnDocentes.setOnAction(e -> toggleSubMenu(subMenuDocentes, scaleDocentes, fadeDocentes));
        btnEstudiantes.setOnAction(e -> toggleSubMenu(subMenuEstudiantes, scaleEstudiantes, fadeEstudiantes));
        btnAnteproyectos.setOnAction(e -> toggleSubMenu(subMenuAnteproyectos, scaleAnteproyectos, fadeAnteproyectos));
        btnMiPerfil.setOnAction(e -> mostrarPerfil());
        btnCerrarSesion.setOnAction(e -> cerrarSesion());

        menuLateral.getChildren().addAll(
                btnInicio,
                btnDocentes,
                subMenuDocentes,
                btnEstudiantes,
                subMenuEstudiantes,
                btnAnteproyectos,
                subMenuAnteproyectos,
                btnMiPerfil,
                new Separator(),
                btnCerrarSesion
        );

        return menuLateral;
    }

    private void configurarAnimacion(VBox subMenu, ScaleTransition scale, FadeTransition fade) {
        // Animación de escala
        scale.setNode(subMenu);
        scale.setDuration(Duration.millis(300));
        scale.setFromY(0);
        scale.setToY(1);
        
        // Animación de desvanecimiento
        fade.setNode(subMenu);
        fade.setDuration(Duration.millis(200));
        fade.setFromValue(0);
        fade.setToValue(1);
        
        // Configuración inicial del submenú
        subMenu.setVisible(false);
        subMenu.setManaged(false);
        subMenu.setScaleY(0);
        subMenu.setOpacity(0);
    }
    
    private VBox createDocentesSubMenu() {
        VBox subMenu = createSubMenu(
            new String[]{"Agregar Docente", "agregar_docente"},
            new String[]{"Ver Docentes", "visualizar_docentes"}
        );
        
        // Configurar acción para Agregar Docente
        ((Button) subMenu.getChildren().get(0)).setOnAction(e -> mostrarFormularioDocente());
        ((Button) subMenu.getChildren().get(1)).setOnAction(e -> mostrarFormularioVistaDocente());
        
        return subMenu;
    }

    private VBox createEstudiantesSubMenu() {
    VBox subMenu = createSubMenu(
        new String[]{"Agregar Estudiante", "agregar_estudiante"},
        new String[]{"Ver Estudiantes", "visualizar_estudiantes"}
    );
    
    // Configurar acciones
    ((Button) subMenu.getChildren().get(0)).setOnAction(e -> mostrarFormularioEstudiante());
    ((Button) subMenu.getChildren().get(1)).setOnAction(e -> mostrarFormularioVistaEstudiante());
    
    return subMenu;
    }

    
    private VBox createAnteproyectosSubMenu() {
        // Nuevo submenú con dos opciones
        VBox subMenu = createSubMenu(
            new String[]{"Agregar Empresa", "agregar_empresa"},
            new String[]{"Ver Empresas", "ver_empresa"},
            new String[]{"Agregar Proyecto", "agregar_anteproyecto"},
            new String[]{"Ver Proyectos", "ver_proyecto"},
            new String[]{"Revisar Anteproyectos", "revisar_anteproyecto"},
            new String[]{"Revisar Propuestas", "revisar_propuesta"},
            new String[]{"Formatos Oficiales", "formato_oficial"},
            new String[]{"Solicitudes", "solicitud_residencia"}
        );
        
        // Configurar acciones para los nuevos botones
        ((Button) subMenu.getChildren().get(0)).setOnAction(e -> mostrarFormularioEmpresa());
        ((Button) subMenu.getChildren().get(1)).setOnAction(e -> mostrarVistaEmpresas());     //cambia mostrarFormularioEmpresa por la nueva vista para ver las empresas
        ((Button) subMenu.getChildren().get(2)).setOnAction(e -> mostrarFormularioAnteproyecto());
        ((Button) subMenu.getChildren().get(3)).setOnAction(e -> mostrarVistaProyectos());     //cambia mostrarFormularioEmpresa por la nueva vista para ver los proyectos
        ((Button) subMenu.getChildren().get(4)).setOnAction(e -> mostrarRevisionAnteproyectos());
        ((Button) subMenu.getChildren().get(5)).setOnAction(e -> mostrarRevisarPropuestas());  //cambia mostrarFormularioEmpresa por la nueva vista para revisar las propuestas
        ((Button) subMenu.getChildren().get(6)).setOnAction(e -> mostrarVistaFormatosOficiales());
        ((Button) subMenu.getChildren().get(7)).setOnAction(e -> mostrarVistaSolicitudesResidencia());
        
        return subMenu;
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

            ImageView icon = loadIcon(opcion[1], 30);
            if (icon != null) {
                btn.setGraphic(icon);
                btn.setContentDisplay(ContentDisplay.LEFT);
                btn.setGraphicTextGap(8);
            }

            subMenu.getChildren().add(btn);
        }

        return subMenu;
    }

    private void mostrarFormularioDocente() {
        FormularioDocente formulario = new FormularioDocente();

        FadeTransition ft = new FadeTransition(Duration.millis(500), formulario);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();

        contenidoCentral.getChildren().setAll(formulario);
    }

    private void mostrarFormularioVistaDocente() {
        VistaDocentes formulario = new VistaDocentes();

        FadeTransition ft = new FadeTransition(Duration.millis(500), formulario);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
        contenidoCentral.getChildren().setAll(formulario);
    }
    
    private void mostrarFormularioEstudiante() {
        FormularioEstudiante formulario = new FormularioEstudiante();

        FadeTransition ft = new FadeTransition(Duration.millis(500), formulario);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        contenidoCentral.getChildren().setAll(formulario);
    }
    
    private void mostrarFormularioVistaEstudiante() {
        VistaEstudiantes formulario = new VistaEstudiantes();

        FadeTransition ft = new FadeTransition(Duration.millis(500), formulario);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
        contenidoCentral.getChildren().setAll(formulario);
    }
    
    // NUEVOS MÉTODOS PARA MOSTRAR FORMULARIOS
    private void mostrarFormularioEmpresa() {
        FormularioEmpresa formulario = new FormularioEmpresa();

        FadeTransition ft = new FadeTransition(Duration.millis(500), formulario);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();

        contenidoCentral.getChildren().setAll(formulario);
    }

    private void mostrarFormularioAnteproyecto() {
        FormularioAnteproyecto formulario = new FormularioAnteproyecto();

        FadeTransition ft = new FadeTransition(Duration.millis(500), formulario);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        contenidoCentral.getChildren().setAll(formulario);
    }
    
    private void mostrarRevisionAnteproyectos() {
        RevisionAnteproyectos revision = new RevisionAnteproyectos();
        new ControladorRevisionAnteproyectos(revision);
        
        FadeTransition ft = new FadeTransition(Duration.millis(500), revision);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
        
        contenidoCentral.getChildren().setAll(revision);
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
    
    private void mostrarVistaProyectos() {
        VistaProyectos vista = new VistaProyectos();
        new ControladorVistaProyectos(vista);
        FadeTransition ft = new FadeTransition(Duration.millis(500), vista);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        contenidoCentral.getChildren().setAll(vista);
    }
    
    private void mostrarVistaEmpresas() {
        VistaEmpresas vista = new VistaEmpresas();
        new ControladorVistaEmpresas(vista);
        FadeTransition ft = new FadeTransition(Duration.millis(500), vista);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        contenidoCentral.getChildren().setAll(vista);
    }

    private void toggleSubMenu(VBox subMenu, ScaleTransition scale, FadeTransition fade) {
        boolean visible = subMenu.isVisible();
        
        // Cerrar todos los submenús
        if (subMenuDocentes != subMenu) {
            cerrarSubMenu(subMenuDocentes, scaleDocentes, fadeDocentes);
        }
        if (subMenuEstudiantes != subMenu) {
            cerrarSubMenu(subMenuEstudiantes, scaleEstudiantes, fadeEstudiantes);
        }
        if (subMenuAnteproyectos != subMenu) {
            cerrarSubMenu(subMenuAnteproyectos, scaleAnteproyectos, fadeAnteproyectos);
        }

        // Abrir/cerrar el submenú actual
        if (!visible) {
            abrirSubMenu(subMenu, scale, fade);
        } else {
            cerrarSubMenu(subMenu, scale, fade);
        }
    }
    
    private void abrirSubMenu(VBox subMenu, ScaleTransition scale, FadeTransition fade) {
        subMenu.setVisible(true);
        subMenu.setManaged(true);
        
        ParallelTransition abrir = new ParallelTransition();
        abrir.getChildren().addAll(scale, fade);
        abrir.play();
    }

    private void cerrarSubMenu(VBox subMenu, ScaleTransition scale, FadeTransition fade) {
        if (!subMenu.isVisible()) return;

        ScaleTransition scaleCierre = new ScaleTransition(Duration.millis(200), subMenu);
        scaleCierre.setFromY(1);
        scaleCierre.setToY(0);
        
        FadeTransition fadeCierre = new FadeTransition(Duration.millis(150), subMenu);
        fadeCierre.setFromValue(1);
        fadeCierre.setToValue(0);
        
        ParallelTransition cerrar = new ParallelTransition(scaleCierre, fadeCierre);
        cerrar.setOnFinished(e -> {
            subMenu.setVisible(false);
            subMenu.setManaged(false);
        });
        cerrar.play();
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
    
    private void mostrarRevisarPropuestas() {
        // Crea la vista
        RevisarPropuestasJefatura vista = new RevisarPropuestasJefatura();
        // Crea el controlador, asociando la vista
        new ControladorRevisarPropuestasJefatura(vista);

        // Efecto de transición (opcional, igual que el resto)
        FadeTransition ft = new FadeTransition(Duration.millis(500), vista);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();

        // Cambia el contenido central
        contenidoCentral.getChildren().setAll(vista);
    }
    
    private void mostrarVistaFormatosOficiales() {
        vista.VistaFormatosOficialesJefatura vistaFormatos = new vista.VistaFormatosOficialesJefatura();
        new controlador.ControladorFormatosOficialesJefatura(vistaFormatos);

        javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(javafx.util.Duration.millis(500), vistaFormatos);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();

        contenidoCentral.getChildren().setAll(vistaFormatos);
    }

    private void mostrarVistaSolicitudesResidencia() {
        VistaSolicitudesResidenciaJefatura vista = new VistaSolicitudesResidenciaJefatura();
        new ControladorSolicitudesResidenciaJefatura(vista);

        FadeTransition ft = new FadeTransition(Duration.millis(500), vista);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        contenidoCentral.getChildren().setAll(vista);
    }
    
    private void cerrarSesion() {
        stage.close();
        new InicioSesion().start(new Stage());
    }
}