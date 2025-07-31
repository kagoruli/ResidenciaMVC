/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package vista;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.DocenteVista;
import modelo.AsignacionAsesorRevisorDatos;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ListChangeListener;

public class WizardAsignacionAsesorRevisor extends Stage {

    private int fase = 1;
    private final VBox root = new VBox();
    private ListView<DocenteVista> listaAsesores;
    private Button btnSiguiente1;
    private ListView<DocenteVista> listaRevisores;
    private Button btnAtras2;
    private Button btnSiguiente2;
    private Label lblAsesor;
    private ListView<DocenteVista> listaRevisoresSeleccionados;
    private Button btnAtras3;
    private Button btnGuardar;
    private final ObservableList<DocenteVista> docentesActivos;
    private DocenteVista asesorSeleccionado;
    private ObservableList<DocenteVista> revisoresSeleccionados = FXCollections.observableArrayList();
    private final int idAnteproyecto;

    public WizardAsignacionAsesorRevisor(List<DocenteVista> docentes, int idAnteproyecto) {
        setTitle("Asignar Asesor y Revisores");
        initModality(Modality.APPLICATION_MODAL);
        setResizable(false);

        this.docentesActivos = FXCollections.observableArrayList(docentes);
        this.idAnteproyecto = idAnteproyecto;

        Scene scene = new Scene(root, 420, 330);
        setScene(scene);

        mostrarFase1();
    }

    private void mostrarFase1() {
        listaAsesores = new ListView<>(docentesActivos);
        listaAsesores.setPrefHeight(220);
        listaAsesores.setPlaceholder(new Label("No hay docentes activos"));
        listaAsesores.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        btnSiguiente1 = new Button("Siguiente");
        btnSiguiente1.setDisable(true);

        VBox fase1 = new VBox(10,
                new Label("Selecciona al Docente que será Asesor del Anteproyecto:"),
                listaAsesores,
                btnSiguiente1
        );
        fase1.setPadding(new Insets(20));
        fase1.setPrefWidth(420);

        root.getChildren().setAll(fase1);

        listaAsesores.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, sel) -> {
            btnSiguiente1.setDisable(sel == null);
        });

        btnSiguiente1.setOnAction(e -> {
            asesorSeleccionado = listaAsesores.getSelectionModel().getSelectedItem();
            mostrarFase2();
        });
    }

    private void mostrarFase2() {
        listaRevisores = new ListView<>();
        listaRevisores.setPrefHeight(220);
        listaRevisores.setPlaceholder(new Label("No hay docentes activos disponibles"));
        listaRevisores.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        btnAtras2 = new Button("Atrás");
        btnSiguiente2 = new Button("Siguiente");
        btnSiguiente2.setDisable(true);

        ObservableList<DocenteVista> revisores = FXCollections.observableArrayList(docentesActivos);
        revisores.remove(asesorSeleccionado);
        listaRevisores.setItems(revisores);
        listaRevisores.getSelectionModel().clearSelection();

        VBox fase2 = new VBox(10,
                new Label("Selecciona de 1 a 5 Docentes Revisores (no puede estar el asesor):"),
                listaRevisores,
                new HBox(10, btnAtras2, btnSiguiente2)
        );
        fase2.setPadding(new Insets(20));

        root.getChildren().setAll(fase2);

        listaRevisores.getSelectionModel().getSelectedItems().addListener(
                (ListChangeListener.Change<? extends DocenteVista> c) -> {
                    ObservableList<DocenteVista> selected = listaRevisores.getSelectionModel().getSelectedItems();
                    btnSiguiente2.setDisable(selected.size() < 1 || selected.size() > 5);
                });

        btnAtras2.setOnAction(e -> mostrarFase1());
        btnSiguiente2.setOnAction(e -> {
            revisoresSeleccionados = FXCollections.observableArrayList(listaRevisores.getSelectionModel().getSelectedItems());
            mostrarFase3();
        });
    }

    private void mostrarFase3() {
        lblAsesor = new Label();
        listaRevisoresSeleccionados = new ListView<>();
        listaRevisoresSeleccionados.setPrefHeight(120);
        btnAtras3 = new Button("Atrás");
        btnGuardar = new Button("Guardar");

        lblAsesor.setText(asesorSeleccionado.getNombre() + " " + asesorSeleccionado.getApellidoPaterno()
                + " (" + asesorSeleccionado.getNumeroTarjeta() + ")");
        listaRevisoresSeleccionados.setItems(revisoresSeleccionados);

        VBox fase3 = new VBox(10,
                new Label("Confirmación de Asignación"),
                new Label("Asesor Asignado:"),
                lblAsesor,
                new Label("Revisores Asignados:"),
                listaRevisoresSeleccionados,
                new HBox(10, btnAtras3, btnGuardar)
        );
        fase3.setPadding(new Insets(20));

        root.getChildren().setAll(fase3);

        btnAtras3.setOnAction(e -> mostrarFase2());
        
        // Configuración directa del evento de guardado
        btnGuardar.setOnAction(e -> guardarAsignacion());
    }

    private void guardarAsignacion() {
        System.out.println("GuardarAsignacion() ejecutado desde Wizard");
        
        if (asesorSeleccionado == null) {
            mostrarAlerta("Atención", "Debes seleccionar un asesor.");
            return;
        }
        if (revisoresSeleccionados == null || revisoresSeleccionados.isEmpty()) {
            mostrarAlerta("Atención", "Debes seleccionar al menos un revisor.");
            return;
        }

        try {
            // Convertir lista de revisores a matrículas
            List<String> matriculasRevisores = new ArrayList<>();
            for (DocenteVista revisor : revisoresSeleccionados) {
                matriculasRevisores.add(revisor.getNumeroTarjeta());
            }

            // Insertar en la base de datos
            boolean ok = AsignacionAsesorRevisorDatos.insertarAsignacion(
                    idAnteproyecto,
                    asesorSeleccionado.getNumeroTarjeta(),
                    matriculasRevisores
            );
            
            if (ok) {
                mostrarAlerta("Asignación exitosa", "¡Asignación de Asesor/Revisores guardada exitosamente!");
                this.close();
            } else {
                mostrarAlerta("Error", "No se pudo guardar la asignación. Intenta de nuevo.");
            }
        } catch (SQLException ex) {
            mostrarAlerta("Error", "Error en la BD: " + ex.getMessage());
            ex.printStackTrace();
        } catch (Exception ex) {
            mostrarAlerta("Error inesperado", "Ocurrió un error inesperado: " + ex.getMessage());
            ex.printStackTrace();
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