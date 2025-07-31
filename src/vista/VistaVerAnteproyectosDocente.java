/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import modelo.VerAnteproyectoDocente;

public class VistaVerAnteproyectosDocente extends VBox {
    private final ComboBox<String> filtroRol;
    private final TableView<VerAnteproyectoDocente> tabla;

    public VistaVerAnteproyectosDocente(String matriculaDocente) {
        setSpacing(20);
        setPadding(new Insets(25));
        setAlignment(Pos.TOP_CENTER);

        Label titulo = new Label("Anteproyectos asignados");
        titulo.getStyleClass().add("form-title");

        filtroRol = new ComboBox<>();
        filtroRol.getItems().addAll("Todos", "Asesor", "Revisor");
        filtroRol.setValue("Todos");

        HBox panelFiltros = new HBox(18, new Label("Filtrar por:"), filtroRol);
        panelFiltros.setAlignment(Pos.CENTER_LEFT);

        tabla = new TableView<>();
        tabla.setPrefHeight(350);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<VerAnteproyectoDocente, String> colIdAnte = new TableColumn<>("ID Proyecto");
        colIdAnte.setCellValueFactory(c -> c.getValue().idAnteproyectoProperty());

        TableColumn<VerAnteproyectoDocente, String> colTitulo = new TableColumn<>("Título");
        colTitulo.setCellValueFactory(c -> c.getValue().tituloProperty());

        TableColumn<VerAnteproyectoDocente, String> colModalidad = new TableColumn<>("Modalidad");
        colModalidad.setCellValueFactory(c -> c.getValue().modalidadProperty());

        TableColumn<VerAnteproyectoDocente, String> colRol = new TableColumn<>("Rol");
        colRol.setCellValueFactory(c -> c.getValue().rolProperty());

        tabla.getColumns().addAll(colIdAnte, colTitulo, colModalidad, colRol);

        getChildren().addAll(titulo, panelFiltros, tabla);

        // Lógica de carga
        cargarDatos(matriculaDocente);

        filtroRol.setOnAction(e -> cargarDatos(matriculaDocente));
    }

    private void cargarDatos(String matriculaDocente) {
        String filtro = filtroRol.getValue();
        ObservableList<VerAnteproyectoDocente> lista = modelo.VerAnteproyectoDocenteDatos.obtenerProyectosPorDocente(matriculaDocente, filtro);
        tabla.setItems(lista);
    }
}
