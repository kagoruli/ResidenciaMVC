/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

/**
 *
 * @author luis
 */

import java.io.File;
import java.sql.SQLException;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import modelo.Anteproyecto;
import modelo.AnteproyectoBanco;
import modelo.AnteproyectoDatos;
import modelo.EstudianteDatos;
import modelo.Propuesta;
import modelo.PropuestaDatos;
import vista.BancoAnteproyectos;
import vista.DetallesAnteproyectoDialog;
import vista.MiAnteproyecto;
import vista.PantallaPrincipalEstudiante;

public class ControladorBancoAnteproyectos {
    private BancoAnteproyectos vista;
    private AnteproyectoBanco anteproyectoSeleccionado;
    private File archivoPropuesta;
    private PantallaPrincipalEstudiante pantallaPrincipal;

    public ControladorBancoAnteproyectos(BancoAnteproyectos vista, PantallaPrincipalEstudiante pantallaPrincipal) {
        this.vista = vista;
        this.pantallaPrincipal = pantallaPrincipal;
        configurarEventos();
        cargarDatosIniciales();
    }

    private void configurarEventos() {
        // Siempre inicia desactivado
        vista.getBtnSeleccionar().setDisable(true);

        // Listener para la selección de la tabla
        vista.getTablaAnteproyectos().getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    anteproyectoSeleccionado = newSelection;
                    // Activa/desactiva según el cupo disponible
                    if (anteproyectoSeleccionado.getCupoDisponible() > 0) {
                        vista.getBtnSeleccionar().setDisable(false);
                    } else {
                        vista.getBtnSeleccionar().setDisable(true);
                    }
                } else {
                    anteproyectoSeleccionado = null;
                    vista.getBtnSeleccionar().setDisable(true);
                }
            }
        );
        
        vista.getTablaAnteproyectos().setRowFactory(tv -> {
            TableRow<AnteproyectoBanco> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    AnteproyectoBanco anteproyecto = row.getItem();
                    mostrarDetallesAnteproyecto(anteproyecto);
                }
            });
            return row;
        });
        
        
        
        vista.getBtnSeleccionar().setOnAction(e -> {
            if (anteproyectoSeleccionado == null) {
                mostrarAlerta("Selección requerida", "Por favor seleccione un anteproyecto de la lista");
                return;
            }
            
            // Nueva validación aquí (protege contra carreras de datos)
            if (anteproyectoSeleccionado.getCupoDisponible() <= 0) {
                mostrarAlerta("Cupo agotado", "Este proyecto ya no tiene cupos disponibles");
                vista.getBtnSeleccionar().setDisable(true); // desactiva por si acaso
                return;
            }
            
            // Confirmación
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar selección");
            confirmacion.setHeaderText(null);
            confirmacion.setContentText("¿Estás seguro de seleccionar este anteproyecto?");
            
            if (confirmacion.showAndWait().get() == ButtonType.OK) {
                try {
                    // Vincular estudiante con anteproyecto
                    if (EstudianteDatos.actualizarAnteproyectoEstudiante(
                        vista.getUsuarioLogueado(), 
                        anteproyectoSeleccionado.getIdAnteproyecto())) {
                        
                        // Actualizar cupo disponible SOLO si cupo es mayor a 0 (doble protección)
                        if (anteproyectoSeleccionado.getCupoDisponible() > 0) {
                            AnteproyectoDatos.actualizarCupoDisponible(
                                anteproyectoSeleccionado.getIdAnteproyecto());
                        }
                        
                        mostrarAlerta("Éxito", "Se ha seleccionado el anteproyecto correctamente");
                        
                        // Redirigir a Mi Anteproyecto
                        pantallaPrincipal.mostrarMiAnteproyecto();
                    } else {
                        mostrarAlerta("Error", "No se pudo seleccionar el anteproyecto");
                    }
                } catch (SQLException ex) {
                    mostrarAlerta("Error", "No se pudo seleccionar el anteproyecto: " + ex.getMessage());
                    System.out.println(ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        
        //vista.getBtnPropia().setOnAction(e -> subirPropuestaPropia());
        
        vista.getChkFiltrarCupo().selectedProperty().addListener((obs, oldVal, newVal) -> {
            cargarAnteproyectos();
        });
    }

    private void mostrarDetallesAnteproyecto(AnteproyectoBanco anteproyecto) {
            DetallesAnteproyectoDialog dialog = new DetallesAnteproyectoDialog(anteproyecto, null);
            dialog.showAndWait();
        }
    
    private void subirPropuestaPropia() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Subir propuesta propia en PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        File archivo = fileChooser.showOpenDialog(new Stage());

        if (archivo != null) {
            mostrarAlerta("Éxito", "Propuesta de anteproyecto enviada, espere la confirmación de la jefatura");
            //pantallaPrincipal.mostrarMiAnteproyecto();
        }
    }

    private void cargarDatosIniciales() {
        cargarAnteproyectos();
        verificarSeleccionPrevia();
    }
    
    private void cargarAnteproyectos() {
        try {
            boolean soloConCupo = vista.getChkFiltrarCupo().isSelected();
            vista.getTablaAnteproyectos().setItems(AnteproyectoDatos.obtenerAnteproyectos(soloConCupo));
        } catch (SQLException ex) {
            mostrarAlerta("Error", "No se pudieron cargar los anteproyectos: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
   private void verificarSeleccionPrevia() {
    try {
        if (EstudianteDatos.tieneAnteproyectoAsignado(vista.getUsuarioLogueado())) {
            // Mostrar solo el mensaje grande y ocultar todo lo demás
            vista.mostrarSoloMensaje(true);
        } else {
            vista.mostrarSoloMensaje(false);
        }
    } catch (SQLException ex) {
        vista.mostrarSoloMensaje(false);
        System.err.println("Error al verificar selección: " + ex.getMessage());
    }
}
    
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
