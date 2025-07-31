/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package controlador;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import modelo.FormatoOficial;
import modelo.FormatoOficialDAO;
import vista.VistaFormatosOficialesJefatura;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.sql.Date;
import java.util.List;
import javafx.scene.control.ChoiceDialog;

public class ControladorFormatosOficialesJefatura {
    private final VistaFormatosOficialesJefatura vista;
    private final ObservableList<FormatoOficial> formatos;

    private final String carpetaBase = "archivos/formatos/";

    public ControladorFormatosOficialesJefatura(VistaFormatosOficialesJefatura vista) {
        this.vista = vista;
        this.formatos = FXCollections.observableArrayList();
        vista.setFormatos(formatos);
        cargarFormatos();
        configurarAcciones();
    }

    private void cargarFormatos() {
        try {
            List<FormatoOficial> lista = FormatoOficialDAO.obtenerTodos();
            formatos.setAll(lista);
        } catch (Exception ex) {
            mostrarAlerta("Error", "No se pudieron cargar los formatos: " + ex.getMessage());
        }
    }

    private void configurarAcciones() {
        vista.getBtnSubirFormato().setOnAction(e -> subirFormato());
        vista.getBtnDescargarFormato().setOnAction(e -> descargarFormato());
    }

    private void subirFormato() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecciona PDF del Formato");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        File archivo = fileChooser.showOpenDialog(getVentana());
        if (archivo == null) return;

        // Diálogo de tipo de formato (mostrar en mayúsculas pero guardar en minúsculas)
        ChoiceDialog<String> dialog = new ChoiceDialog<>("ANTEPROYECTO", "ANTEPROYECTO", "SOLICITUD_RESIDENCIA");
        dialog.setTitle("Tipo de Formato");
        dialog.setHeaderText("Selecciona el tipo de formato que estás subiendo:");
        dialog.setContentText("Tipo:");
        String tipoSeleccionado = dialog.showAndWait().orElse(null);
        if (tipoSeleccionado == null) return;

        // Forzar minúsculas para el tipo
        String tipo = tipoSeleccionado.toLowerCase(); // <-- ¡Aquí está la magia!

        // Guardar archivo en carpeta estructurada y en minúsculas
        try {
            Files.createDirectories(Paths.get(carpetaBase));
            String nombreArchivo = tipo + ".pdf"; // siempre en minúsculas
            Path destino = Paths.get(carpetaBase, nombreArchivo);
            Files.copy(archivo.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);

            // Registrar en base de datos (guardar tipo y ruta en minúsculas)
            FormatoOficial formato = new FormatoOficial(0, tipo, destino.toString(), new Date(System.currentTimeMillis()));
            // Si ya existe un formato de este tipo, actualizarlo en vez de insertar
            FormatoOficial existente = FormatoOficialDAO.obtenerPorTipo(tipo);
            if (existente == null) {
                FormatoOficialDAO.insertarFormato(formato);
            } else {
                // Actualiza la ruta y fecha (debería ser un update, pero depende de tu implementación DAO)
                FormatoOficialDAO.insertarFormato(formato); // Aquí puedes cambiarlo a update si tienes ese método
            }
            mostrarAlerta("Éxito", "Formato subido correctamente.");
            cargarFormatos();
        } catch (IOException ex) {
            mostrarAlerta("Error", "No se pudo guardar el archivo: " + ex.getMessage());
        } catch (Exception ex) {
            mostrarAlerta("Error", "No se pudo registrar el formato: " + ex.getMessage());
        }
    }


    private void descargarFormato() {
        FormatoOficial formato = vista.getTablaFormatos().getSelectionModel().getSelectedItem();
        if (formato == null) {
            mostrarAlerta("Atención", "Selecciona un formato para descargar.");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Formato PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        fileChooser.setInitialFileName(formato.getTipo() + ".pdf");
        File destino = fileChooser.showSaveDialog(getVentana());
        if (destino == null) return;
        try {
            Files.copy(Paths.get(formato.getRutaPDF()), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
            mostrarAlerta("Éxito", "Archivo descargado en: " + destino.getAbsolutePath());
        } catch (Exception ex) {
            mostrarAlerta("Error", "No se pudo descargar el PDF: " + ex.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private Window getVentana() {
        return vista.getScene().getWindow();
    }
}
