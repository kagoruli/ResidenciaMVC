/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package vista;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import modelo.Empresa;
import modelo.Anteproyecto;
import modelo.EmpresaDatos;
import modelo.AnteproyectoDatos;
import modelo.EstudianteDatos;

public class FormularioRegistroPropuesta extends Stage {
    private boolean empresaExistente = false;
    
    public FormularioRegistroPropuesta(String idEstudiante, String rfcSugerido, String nombreSugerido, 
                                      String tituloSugerido, String tipoSugerido, String descripcionSugerida) {
        setTitle("Registrar Empresa y Proyecto de Propuesta Propia");

        VBox mainContainer = new VBox(15);
        mainContainer.getStyleClass().add("form-container");
        mainContainer.setPadding(new Insets(25));
        mainContainer.setMaxWidth(800);

        // Título del formulario
        Label lblTitulo = new Label("Registrar Empresa y Proyecto");
        lblTitulo.getStyleClass().add("form-title");
        
        // --- Empresa
        GridPane grid = new GridPane();
        grid.setVgap(12);
        grid.setHgap(15);
        grid.setPadding(new Insets(15));
        grid.getStyleClass().add("form-grid");

        // Campos de empresa
        Label lblEmpresa = new Label("Datos de la Empresa");
        lblEmpresa.getStyleClass().add("form-label");
        lblEmpresa.setStyle("-fx-font-size: 16px; -fx-padding: 0 0 10px 0;");
        grid.add(lblEmpresa, 0, 0, 2, 1);

        TextField txtNombreEmpresa = new TextField(nombreSugerido != null ? nombreSugerido : "");
        txtNombreEmpresa.getStyleClass().add("form-textfield");
        TextField txtRFC = new TextField(rfcSugerido != null ? rfcSugerido : "");
        txtRFC.getStyleClass().add("form-textfield");
        TextField txtRepresentante = new TextField();
        txtRepresentante.getStyleClass().add("form-textfield");
        TextField txtTelefono = new TextField();
        txtTelefono.getStyleClass().add("form-textfield");
        TextArea txtDescripcionEmpresa = new TextArea();
        txtDescripcionEmpresa.getStyleClass().add("form-textfield");
        txtDescripcionEmpresa.setPrefHeight(80);

        // --- Proyecto
        Label lblProyecto = new Label("Datos del Proyecto");
        lblProyecto.getStyleClass().add("form-label");
        lblProyecto.setStyle("-fx-font-size: 16px; -fx-padding: 20px 0 10px 0;");
        // CORRECCIÓN: REMOVER ESTA LÍNEA DUPLICADA
        // grid.add(lblProyecto, 0, 7, 2, 1); // <-- ESTA ES LA LÍNEA QUE CAUSA EL ERROR

        TextField txtTitulo = new TextField(tituloSugerido != null ? tituloSugerido : "");
        txtTitulo.getStyleClass().add("form-textfield");
        Spinner<Integer> spnCupo = new Spinner<>(1, 30, 1);
        spnCupo.getStyleClass().add("form-textfield");
        ComboBox<String> cmbTipo = new ComboBox<>();
        cmbTipo.getStyleClass().add("form-combobox");
        cmbTipo.getItems().addAll("Presencial", "Hibrido", "Remota");
        cmbTipo.setValue(tipoSugerido != null ? tipoSugerido : "Presencial");
        TextArea txtDescripcionProyecto = new TextArea(descripcionSugerida != null ? descripcionSugerida : "");
        txtDescripcionProyecto.getStyleClass().add("form-textfield");
        txtDescripcionProyecto.setPrefHeight(80);

        // Botón
        Button btnGuardar = new Button("Guardar y Vincular Proyecto");
        btnGuardar.getStyleClass().add("form-button-ancho");
        btnGuardar.setMaxWidth(Double.MAX_VALUE);

        // Mensaje de bloqueo
        Label lblMensaje = new Label();
        lblMensaje.getStyleClass().add("mensaje-bloqueo");
        lblMensaje.setVisible(false);

        // Aumentar ancho de columnas para evitar etiquetas truncadas
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(30);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(70);
        grid.getColumnConstraints().addAll(col1, col2);

        int f = 1;
        grid.add(new Label("Nombre:"), 0, f); 
        grid.add(txtNombreEmpresa, 1, f++);
        grid.add(new Label("RFC:"), 0, f); 
        grid.add(txtRFC, 1, f++);
        grid.add(new Label("Representante:"), 0, f); 
        grid.add(txtRepresentante, 1, f++);
        grid.add(new Label("Teléfono:"), 0, f); 
        grid.add(txtTelefono, 1, f++);
        grid.add(new Label("Descripción:"), 0, f); 
        grid.add(txtDescripcionEmpresa, 1, f++);

        // Separador visual entre secciones
        Separator separator = new Separator();
        separator.setPadding(new Insets(10, 0, 10, 0));
        grid.add(separator, 0, f++, 2, 1);

        // Continuamos con proyecto (SOLO AQUÍ SE AGREGA lblProyecto)
        grid.add(lblProyecto, 0, f++, 2, 1); // <-- ÚNICA ADICIÓN
        grid.add(new Label("Título:"), 0, f); 
        grid.add(txtTitulo, 1, f++);
        grid.add(new Label("Cupo:"), 0, f); 
        grid.add(spnCupo, 1, f++);
        grid.add(new Label("Tipo:"), 0, f); 
        grid.add(cmbTipo, 1, f++);
        grid.add(new Label("Descripción:"), 0, f); 
        grid.add(txtDescripcionProyecto, 1, f++);
        grid.add(btnGuardar, 0, f, 2, 1);
        grid.add(lblMensaje, 0, ++f, 2, 1);

        // Aplicar restricciones de entrada
        aplicarRestriccionesEntrada(txtNombreEmpresa, txtRFC, txtRepresentante, txtTelefono);

        // Búsqueda automática de RFC con detección de borrado
        txtRFC.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() >= 12) { 
                buscarEmpresaPorRFC(newVal.trim(), txtNombreEmpresa, 
                                   txtRepresentante, txtTelefono, txtDescripcionEmpresa,
                                   lblMensaje);
            } else if (newVal.isEmpty() || newVal.length() < 12) {
                // Si se borra el RFC o es muy corto, habilitar campos
                txtNombreEmpresa.setDisable(false);
                txtRepresentante.setDisable(false);
                txtTelefono.setDisable(false);
                txtDescripcionEmpresa.setDisable(false);
                lblMensaje.setVisible(false);
                empresaExistente = false;
            }
        });

        mainContainer.getChildren().addAll(lblTitulo, grid);
        Scene scene = new Scene(mainContainer);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        setScene(scene);

        btnGuardar.setOnAction(e -> {
            try {
                // Validación adicional
                if (!validarCampos(txtNombreEmpresa, txtRFC, txtRepresentante, txtTelefono, txtTitulo)) {
                    return;
                }
                
                // 1. Empresa
                Empresa emp = new Empresa(txtNombreEmpresa.getText(), txtRFC.getText(),
                        txtRepresentante.getText(), txtTelefono.getText(), txtDescripcionEmpresa.getText());
                if (!EmpresaDatos.existeRFC(emp.getRfc())) {
                    EmpresaDatos.insertarEmpresa(emp);
                }
                // 2. Proyecto
                Anteproyecto ante = new Anteproyecto(0, txtTitulo.getText(), spnCupo.getValue(), cmbTipo.getValue(),
                        txtDescripcionProyecto.getText(), txtRFC.getText(), txtNombreEmpresa.getText(), "ACTIVO", spnCupo.getValue());
                int idAnte = AnteproyectoDatos.insertarRetornarID(ante, true);
                // 3. Vincular al estudiante
                EstudianteDatos.actualizarAnteproyectoEstudiante(idEstudiante, idAnte);

                new Alert(Alert.AlertType.INFORMATION, "¡Registro exitoso!\nEl estudiante puede seleccionar su propuesta propia.", ButtonType.OK).showAndWait();
                close();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Error al registrar: " + ex.getMessage(), ButtonType.OK).showAndWait();
                ex.printStackTrace();
            }
        });
    }

    private void aplicarRestriccionesEntrada(TextField nombre, TextField rfc, TextField representante, TextField telefono) {
        // Solo letras y espacios para nombre y representante
        nombre.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*")) {
                nombre.setText(oldVal);
            }
        });
        
        representante.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*")) {
                representante.setText(oldVal);
            }
        });
        
        // Solo números para teléfono (máximo 10 dígitos)
        telefono.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*") || newVal.length() > 10) {
                telefono.setText(oldVal);
            }
        });
        
        // Solo alfanumérico para RFC (máximo 13 caracteres)
        rfc.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("[a-zA-Z0-9]*") || newVal.length() > 13) {
                rfc.setText(oldVal);
            }
        });
    }

    private void buscarEmpresaPorRFC(String rfc, TextField nombre, TextField representante, 
                                    TextField telefono, TextArea descripcion, Label mensaje) {
        new Thread(() -> {
            Empresa empresa = EmpresaDatos.obtenerPorRFC(rfc);
            Platform.runLater(() -> {
                if (empresa != null) {
                    nombre.setText(empresa.getNombre());
                    representante.setText(empresa.getRepresentante());
                    telefono.setText(empresa.getTelefono());
                    descripcion.setText(empresa.getDescripcion());
                    
                    nombre.setDisable(true);
                    representante.setDisable(true);
                    telefono.setDisable(true);
                    descripcion.setDisable(true);
                    
                    mensaje.setText("Empresa encontrada. Campos bloqueados para edición.");
                    mensaje.setVisible(true);
                    empresaExistente = true;
                } else {
                    nombre.setDisable(false);
                    representante.setDisable(false);
                    telefono.setDisable(false);
                    descripcion.setDisable(false);
                    
                    if (empresaExistente) {
                        nombre.clear();
                        representante.clear();
                        telefono.clear();
                        descripcion.clear();
                        empresaExistente = false;
                    }
                    
                    mensaje.setVisible(false);
                }
            });
        }).start();
    }

    private boolean validarCampos(TextField nombre, TextField rfc, TextField representante, 
                                TextField telefono, TextField titulo) {
        if (nombre.getText().isEmpty() || rfc.getText().isEmpty() || 
            representante.getText().isEmpty() || telefono.getText().isEmpty() || 
            titulo.getText().isEmpty()) {
            
            new Alert(Alert.AlertType.WARNING, "Todos los campos obligatorios deben estar llenos", ButtonType.OK).show();
            return false;
        }
        
        if (telefono.getText().length() < 10) {
            new Alert(Alert.AlertType.WARNING, "El teléfono debe tener 10 dígitos", ButtonType.OK).show();
            return false;
        }
        
        if (rfc.getText().length() < 12) {
            new Alert(Alert.AlertType.WARNING, "El RFC debe tener al menos 12 caracteres", ButtonType.OK).show();
            return false;
        }
        
        return true;
    }
}