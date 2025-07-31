/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

/**
 *
 * @author luis
 */
import java.util.List;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import modelo.AnteproyectoBanco;

public class DetallesAnteproyectoDialog extends Dialog<Void> {

    /**
     * 
     * @param anteproyecto   El proyecto a mostrar
     * @param alumnos        Lista de alumnos (String[0]=Num. control, String[1]=Nombre) o null para NO mostrar campo de alumnos
     *                       Si es una lista vacía, mostrará el mensaje "No hay ningún estudiante vinculado..."
     */
    public DetallesAnteproyectoDialog(AnteproyectoBanco anteproyecto, List<String[]> alumnos) {
        setTitle("Detalles del Proyecto");
        setHeaderText("Información completa del anteproyecto");

        ButtonType cerrarButton = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(cerrarButton);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setMaxWidth(Double.MAX_VALUE);

        Label lblTitulo = new Label("Título:");
        Label lblTituloValor = new Label(anteproyecto.getTitulo());
        Label lblEmpresa = new Label("Empresa:");
        Label lblEmpresaValor = new Label(anteproyecto.getEmpresa());
        Label lblCupo = new Label("Cupo Disponible:");
        Label lblCupoValor = new Label(String.valueOf(anteproyecto.getCupoDisponible()));
        Label lblTipo = new Label("Tipo:");
        Label lblTipoValor = new Label(anteproyecto.getTipo());
        Label lblDescripcion = new Label("Descripción:");
        TextArea txtDescripcion = new TextArea(anteproyecto.getDescripcion());
        txtDescripcion.setEditable(false);
        txtDescripcion.setWrapText(true);
        txtDescripcion.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(txtDescripcion, Priority.ALWAYS);

        // Ajustar altura dinámica según número de líneas de la descripción
        String descripcion = anteproyecto.getDescripcion() != null ? anteproyecto.getDescripcion() : "";
        int descLines = descripcion.split("\r\n|\r|\n").length;
        int descRows = Math.max(2, Math.min(descLines, 12));
        txtDescripcion.setPrefRowCount(descRows);

        grid.add(lblTitulo, 0, 0);      grid.add(lblTituloValor, 1, 0);
        grid.add(lblEmpresa, 0, 1);     grid.add(lblEmpresaValor, 1, 1);
        grid.add(lblCupo, 0, 2);        grid.add(lblCupoValor, 1, 2);
        grid.add(lblTipo, 0, 3);        grid.add(lblTipoValor, 1, 3);
        grid.add(lblDescripcion, 0, 4); grid.add(txtDescripcion, 1, 4);

        // SOLO agrega el apartado de alumnos si la lista no es null (jefatura)
        if (alumnos != null) {
            Label lblAlumnos = new Label("Estudiantes asignados:");
            grid.add(lblAlumnos, 0, 5);

            if (alumnos.isEmpty()) {
                Label sinAlumnos = new Label("No hay ningún estudiante vinculado a este proyecto aún.");
                grid.add(sinAlumnos, 1, 5);
            } else {
                // Arma un TextArea con los alumnos (uno por línea)
                StringBuilder sb = new StringBuilder();
                for (String[] alumno : alumnos) {
                    sb.append(alumno[0]).append(" - ").append(alumno[1]).append("\n");
                }
                TextArea areaAlumnos = new TextArea(sb.toString());
                areaAlumnos.setEditable(false);
                areaAlumnos.setWrapText(false); // Que solo haga scroll horizontal si hay nombres enormes

                // Ajuste dinámico: mínimo 3 filas, máximo 8 filas visibles
                int alumnoRows = Math.max(2, Math.min(alumnos.size(), 8));
                areaAlumnos.setPrefRowCount(alumnoRows);

                // Fijar altura mínima y máxima
                areaAlumnos.setMinHeight(60);
                areaAlumnos.setMaxHeight(200); // Ajusta esto si quieres más/menos altura máxima

                areaAlumnos.setMaxWidth(Double.MAX_VALUE);
                GridPane.setHgrow(areaAlumnos, Priority.ALWAYS);

                // Permitir scroll vertical (por defecto el TextArea ya lo hace si hay muchas líneas)
                grid.add(areaAlumnos, 1, 5);
            }
        }

        getDialogPane().setContent(grid);
    }
}
