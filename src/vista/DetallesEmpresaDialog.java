/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

/**
 *
 * @author jafet
 */
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.List;
import modelo.Empresa;

public class DetallesEmpresaDialog extends Dialog<Void> {
    public DetallesEmpresaDialog(Empresa empresa, List<String> proyectos) {
        setTitle("Detalles de la Empresa");
        setHeaderText("Información completa de la empresa");

        ButtonType cerrarButton = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(cerrarButton);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setMaxWidth(Double.MAX_VALUE);

        Label lblNombre = new Label("Nombre:");
        lblNombre.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        Label lblNombreValor = new Label(empresa.getNombre());

        Label lblRFC = new Label("RFC:");
        lblRFC.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        Label lblRFCValor = new Label(empresa.getRfc());

        Label lblRepresentante = new Label("Representante:");
        lblRepresentante.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        Label lblRepresentanteValor = new Label(empresa.getRepresentante());

        Label lblTelefono = new Label("Teléfono:");
        lblTelefono.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        Label lblTelefonoValor = new Label(empresa.getTelefono());

        Label lblDescripcion = new Label("Descripción:");
        lblDescripcion.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        TextArea txtDescripcion = new TextArea(empresa.getDescripcion());
        txtDescripcion.setEditable(false);
        txtDescripcion.setWrapText(true);
        txtDescripcion.setPrefRowCount(3);
        txtDescripcion.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(txtDescripcion, Priority.ALWAYS);

        grid.add(lblNombre, 0, 0);        grid.add(lblNombreValor, 1, 0);
        grid.add(lblRFC, 0, 1);           grid.add(lblRFCValor, 1, 1);
        grid.add(lblRepresentante, 0, 2); grid.add(lblRepresentanteValor, 1, 2);
        grid.add(lblTelefono, 0, 3);      grid.add(lblTelefonoValor, 1, 3);
        grid.add(lblDescripcion, 0, 4);   grid.add(txtDescripcion, 1, 4);

        // Proyectos vinculados
        Label lblProyectos = new Label("Proyectos vinculados:");
        lblProyectos.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        grid.add(lblProyectos, 0, 5);

        if (proyectos == null || proyectos.isEmpty()) {
            Label sinProyectos = new Label("No hay proyectos vinculados a esta empresa.");
            grid.add(sinProyectos, 1, 5);
        } else {
            // Área con scroll para muchos proyectos
            ListView<String> listaProyectos = new ListView<>();
            listaProyectos.getItems().addAll(proyectos);
            listaProyectos.setMaxHeight(130);
            listaProyectos.setPrefWidth(250);
            grid.add(listaProyectos, 1, 5);
        }

        getDialogPane().setContent(grid);
    }
}

