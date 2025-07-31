/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package vista;

import controlador.ControladorPropuestaPropia;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import modelo.PropuestaPropia;
import java.io.File;

public class VistaPropuestaPropia extends VBox {
    private final Label lblEstado;
    private final Label lblComentarios;
    private final Button btnSubir;
    private final Button btnEnviar;
    private final Label lblArchivo;
    private final ControladorPropuestaPropia controlador;
    private File archivoPDF;
    private String idEstudiante;
    private Integer idAnteproyecto; // <-- Cambia a Integer

    public VistaPropuestaPropia(String idEstudiante, int idAnteproyecto) {
        this.idEstudiante = idEstudiante;
        // Si te pasan 0, pon null para que viaje como null
        this.idAnteproyecto = (idAnteproyecto == 0) ? null : idAnteproyecto;
        this.controlador = new ControladorPropuestaPropia(this);

        setSpacing(20);
        setPadding(new Insets(40));
        setAlignment(Pos.TOP_CENTER);
        getStyleClass().add("form-container");

        Label titulo = new Label("Propuesta Propia");
        titulo.getStyleClass().add("form-title");

        lblEstado = new Label();
        lblComentarios = new Label();
        lblArchivo = new Label("No se ha seleccionado ningún archivo PDF.");

        btnSubir = new Button("Subir propuesta");
        btnEnviar = new Button("Enviar propuesta");
        btnEnviar.setDisable(true);

        getChildren().addAll(titulo, lblEstado, lblComentarios, lblArchivo, btnSubir, btnEnviar);

        cargarEstado();

        btnSubir.setOnAction(e -> seleccionarArchivo());
        btnEnviar.setOnAction(e -> enviarPropuesta());
    }

    public String getUsuarioLogueado() {
        return idEstudiante;
    }

    private void cargarEstado() {
        try {
            PropuestaPropia propuesta = controlador.obtenerPropuesta(idEstudiante);
            if (propuesta != null) {
                lblEstado.setText("Estado actual: " + propuesta.getEstado());
                if (propuesta.getComentarios() != null && !propuesta.getComentarios().isEmpty()) {
                    lblComentarios.setText("Comentarios: " + propuesta.getComentarios());
                } else {
                    lblComentarios.setText("");
                }
                btnSubir.setDisable(!controlador.puedeSubirPropuesta(propuesta));
                btnEnviar.setDisable(true);
                if (!controlador.puedeSubirPropuesta(propuesta)) {
                    btnSubir.setText("Ya enviaste una propuesta");
                }
            } else {
                lblEstado.setText("No has enviado ninguna propuesta propia.");
                lblComentarios.setText("");
                btnSubir.setDisable(false);
                btnSubir.setText("Subir propuesta");
            }
        } catch (Exception ex) {
            lblEstado.setText("Error al cargar estado de la propuesta.");
            btnSubir.setDisable(true);
            btnEnviar.setDisable(true);
        }
    }

    private void seleccionarArchivo() {
        Stage stage = (Stage) getScene().getWindow();
        File file = controlador.seleccionarArchivo(stage);
        if (file != null) {
            archivoPDF = file;
            lblArchivo.setText("Archivo seleccionado: " + archivoPDF.getName());
            btnEnviar.setDisable(false);
        }
    }

    private void enviarPropuesta() {
        try {
            if (archivoPDF == null) return;
            boolean ok = controlador.registrarPropuesta(idEstudiante, (idAnteproyecto == null ? 0 : idAnteproyecto), archivoPDF);
            if (ok) {
                mostrarAlerta("Éxito", "Propuesta enviada correctamente.", Alert.AlertType.INFORMATION);
                btnEnviar.setDisable(true);
                cargarEstado();
            } else {
                mostrarAlerta("Error", "No se pudo enviar la propuesta.", Alert.AlertType.ERROR);
            }
        } catch (Exception ex) {
            mostrarAlerta("Error", "Ocurrió un error: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void mostrarSoloMensaje(boolean mostrar) {
        getChildren().removeIf(node -> node.getStyleClass().contains("mensaje-grande"));

        for (javafx.scene.Node node : getChildren()) {
            node.setVisible(!mostrar);
            node.setManaged(!mostrar);
        }
        if (mostrar) {
            Label lblMensaje = new Label("Ya has seleccionado un Proyecto, por lo que no podrás seleccionar otro");
            lblMensaje.getStyleClass().add("mensaje-grande");
            getChildren().add(lblMensaje);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
