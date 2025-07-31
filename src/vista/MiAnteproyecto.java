/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package vista;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import modelo.*;
import java.io.File;

public class MiAnteproyecto extends VBox {
    private final String usuarioLogueado;
    private VBox contenedorDetalles;
    private VBox contenedorMensaje;

    // Controles anteproyecto
    private VBox contenedorBotonesAnteproyecto;
    private Button btnDescargarFormatoAnteproyecto;
    private Button btnSubirPDFAnteproyecto;
    private Label lblEstadoAnteproyecto;
    private Label lblComentariosAnteproyecto;
    private TextArea txtComentariosAnteproyecto;

    // Controles solicitud de residencia
    private VBox contenedorBotonesSolicitud;
    private Button btnDescargarFormatoSolicitud;
    private Button btnSubirPDFSolicitud;
    private Button btnDescargarSolicitudPDF;
    private Label lblEstadoSolicitud;

    // Datos del proyecto
    private Label lblTitulo;
    private Label lblEmpresa;
    private Label lblCupo;
    private Label lblTipo;
    private Label lblDescripcion;

    public MiAnteproyecto(String usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
        inicializarUI();
        cargarDatos();
    }

    private void inicializarUI() {
        setSpacing(20);
        setPadding(new Insets(20));
        setAlignment(Pos.TOP_CENTER);

        // Mensaje cuando no hay proyecto
        contenedorMensaje = new VBox();
        contenedorMensaje.setAlignment(Pos.CENTER);
        contenedorMensaje.setSpacing(16);
        contenedorMensaje.setPadding(new Insets(32));
        Label labelMsg = new Label("Aún no se ha seleccionado ningún Proyecto.");
        labelMsg.setStyle("-fx-font-size: 18px; -fx-text-fill: #555;");
        contenedorMensaje.getChildren().add(labelMsg);
        contenedorMensaje.setVisible(false);

        // Contenedor detalles
        contenedorDetalles = new VBox(20);
        contenedorDetalles.setAlignment(Pos.TOP_CENTER);

        Label titulo = new Label("MI PROYECTO");
        titulo.getStyleClass().add("form-title");

        lblTitulo = new Label();
        lblEmpresa = new Label();
        lblCupo = new Label();
        lblTipo = new Label();
        lblDescripcion = new Label();
        lblDescripcion.setWrapText(true);

        GridPane gridDetalles = new GridPane();
        gridDetalles.setHgap(10);
        gridDetalles.setVgap(10);
        gridDetalles.addRow(0, new Label("Título:"), lblTitulo);
        gridDetalles.addRow(1, new Label("Empresa:"), lblEmpresa);
        gridDetalles.addRow(2, new Label("Cupo disponible:"), lblCupo);
        gridDetalles.addRow(3, new Label("Tipo:"), lblTipo);
        gridDetalles.addRow(4, new Label("Descripción:"), lblDescripcion);

        // ---------- ANTEPROYECTO -----------
        btnDescargarFormatoAnteproyecto = new Button("Descargar formato oficial PDF Anteproyecto");
        btnDescargarFormatoAnteproyecto.setPrefWidth(280);

        btnSubirPDFAnteproyecto = new Button("Subir PDF Anteproyecto");
        btnSubirPDFAnteproyecto.setPrefWidth(250);

        lblEstadoAnteproyecto = new Label();
        lblEstadoAnteproyecto.setStyle("-fx-font-weight: bold;");
        lblEstadoAnteproyecto.setVisible(false);

        lblComentariosAnteproyecto = new Label("Comentarios de la Jefatura:");
        lblComentariosAnteproyecto.setVisible(false);

        txtComentariosAnteproyecto = new TextArea();
        txtComentariosAnteproyecto.setEditable(false);
        txtComentariosAnteproyecto.setWrapText(true);
        txtComentariosAnteproyecto.setPrefRowCount(2);
        txtComentariosAnteproyecto.setMaxWidth(400);
        txtComentariosAnteproyecto.setVisible(false);

        contenedorBotonesAnteproyecto = new VBox(10, btnDescargarFormatoAnteproyecto, btnSubirPDFAnteproyecto);
        contenedorBotonesAnteproyecto.setAlignment(Pos.CENTER);

        // ---------- SOLICITUD DE RESIDENCIA -----------
        btnDescargarFormatoSolicitud = new Button("Descargar formato oficial Solicitud Residencia");
        btnDescargarFormatoSolicitud.setPrefWidth(280);

        btnSubirPDFSolicitud = new Button("Subir Solicitud de Residencia (PDF)");
        btnSubirPDFSolicitud.setPrefWidth(250);

        btnDescargarSolicitudPDF = new Button("Descargar PDF enviado");
        btnDescargarSolicitudPDF.setPrefWidth(250);

        lblEstadoSolicitud = new Label();
        lblEstadoSolicitud.setStyle("-fx-font-weight: bold;");

        contenedorBotonesSolicitud = new VBox(10, btnDescargarFormatoSolicitud, btnSubirPDFSolicitud, btnDescargarSolicitudPDF, lblEstadoSolicitud);
        contenedorBotonesSolicitud.setAlignment(Pos.CENTER);

        contenedorDetalles.getChildren().addAll(titulo, gridDetalles, contenedorBotonesAnteproyecto, lblEstadoAnteproyecto, lblComentariosAnteproyecto, txtComentariosAnteproyecto, contenedorBotonesSolicitud);

        // Inicialmente sólo uno visible
        contenedorDetalles.setVisible(false);
        contenedorMensaje.setVisible(false);

        getChildren().addAll(contenedorDetalles, contenedorMensaje);
    }

    public void cargarDatos() {
        try {
            // Checar si el estudiante tiene anteproyecto asignado
            int idAnte = EstudianteDatos.obtenerIdAnteproyecto(usuarioLogueado);

            if (idAnte == 0) {
                contenedorDetalles.setVisible(false);
                contenedorMensaje.setVisible(true);
                return;
            }
            // Si sí tiene, mostramos todos los detalles del proyecto
            AnteproyectoBanco ante = AnteproyectoDatos.obtenerAnteproyectoPorId(idAnte);

            if (ante == null) {
                contenedorDetalles.setVisible(false);
                contenedorMensaje.setVisible(true);
                ((Label)contenedorMensaje.getChildren().get(0)).setText("Error: Proyecto asignado no encontrado.");
                return;
            }
            lblTitulo.setText(ante.getTitulo());
            lblEmpresa.setText(ante.getEmpresa());
            lblCupo.setText(String.valueOf(ante.getCupoDisponible()));
            lblTipo.setText(ante.getTipo());
            lblDescripcion.setText(ante.getDescripcion());

            // --------- ANTEPROYECTO ------------
            EntregaAnteproyecto ea = EntregaAnteproyectoDatos.obtenerPorEstudiante(usuarioLogueado);

            String estadoAnteproyecto = ea != null ? ea.getEstado() : null;
            // Control de visibilidad de bloques
            contenedorBotonesAnteproyecto.setVisible(estadoAnteproyecto == null || !estadoAnteproyecto.equalsIgnoreCase("Aceptado"));
            lblEstadoAnteproyecto.setVisible(estadoAnteproyecto != null && !estadoAnteproyecto.equalsIgnoreCase("Aceptado"));
            lblComentariosAnteproyecto.setVisible(estadoAnteproyecto != null && estadoAnteproyecto.equals("Corregir"));
            txtComentariosAnteproyecto.setVisible(estadoAnteproyecto != null && estadoAnteproyecto.equals("Corregir"));

            // Estado y comentarios
            if (ea == null) {
                btnSubirPDFAnteproyecto.setText("Subir PDF Anteproyecto");
                btnSubirPDFAnteproyecto.setDisable(false);
                lblEstadoAnteproyecto.setText("");
                lblComentariosAnteproyecto.setVisible(false);
                txtComentariosAnteproyecto.setVisible(false);
            } else {
                lblEstadoAnteproyecto.setText("Estado de anteproyecto: " + ea.getEstado());
                if (ea.getEstado().equals("Pendiente")) {
                    btnSubirPDFAnteproyecto.setText("Subir PDF Anteproyecto");
                    btnSubirPDFAnteproyecto.setDisable(true);
                } else if (ea.getEstado().equals("Corregir")) {
                    btnSubirPDFAnteproyecto.setText("Reenviar PDF Anteproyecto");
                    btnSubirPDFAnteproyecto.setDisable(false);
                    txtComentariosAnteproyecto.setText(ea.getComentarios() == null ? "" : ea.getComentarios());
                } else if (ea.getEstado().equals("Aceptado")) {
                    btnSubirPDFAnteproyecto.setVisible(false);
                    btnDescargarFormatoAnteproyecto.setVisible(false);
                }
            }

            // ----------- SOLICITUD DE RESIDENCIA -------------
            // Solo visible cuando anteproyecto está aceptado
            contenedorBotonesSolicitud.setVisible(estadoAnteproyecto != null && estadoAnteproyecto.equalsIgnoreCase("Aceptado"));

            // Cargar estado y controles de la solicitud
            SolicitudResidencia solicitud = SolicitudResidenciaDatos.obtenerPorEstudiante(usuarioLogueado);
            if (solicitud == null) {
                btnSubirPDFSolicitud.setDisable(false);
                btnDescargarSolicitudPDF.setDisable(true);
                lblEstadoSolicitud.setText("No has enviado solicitud de residencia.");
            } else {
                btnSubirPDFSolicitud.setDisable(true);
                btnDescargarSolicitudPDF.setDisable(false);
                lblEstadoSolicitud.setText("Estado de la solicitud: " + solicitud.getEstatus());
            }

            contenedorDetalles.setVisible(true);
            contenedorMensaje.setVisible(false);

        } catch (Exception ex) {
            contenedorDetalles.setVisible(false);
            contenedorMensaje.setVisible(true);
            ((Label)contenedorMensaje.getChildren().get(0)).setText("Error al cargar los datos: " + ex.getMessage());
        }
    }

    // --- Getters para el controlador ---
    public Button getBtnSubirPDFAnteproyecto() { return btnSubirPDFAnteproyecto; }
    public Button getBtnDescargarFormatoAnteproyecto() { return btnDescargarFormatoAnteproyecto; }

    public Button getBtnSubirPDFSolicitud() { return btnSubirPDFSolicitud; }
    public Button getBtnDescargarFormatoSolicitud() { return btnDescargarFormatoSolicitud; }
    public Button getBtnDescargarSolicitudPDF() { return btnDescargarSolicitudPDF; }

    public void actualizarVista() { cargarDatos(); }
}
