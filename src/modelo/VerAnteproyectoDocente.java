/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class VerAnteproyectoDocente {
    private final StringProperty idAnteproyecto;
    private final StringProperty titulo;
    private final StringProperty modalidad;
    private final StringProperty rol;

    public VerAnteproyectoDocente(String idAnteproyecto, String titulo, String modalidad, String rol) {
        this.idAnteproyecto = new SimpleStringProperty(idAnteproyecto);
        this.titulo = new SimpleStringProperty(titulo);
        this.modalidad = new SimpleStringProperty(modalidad);
        this.rol = new SimpleStringProperty(rol);
    }

    public StringProperty idAnteproyectoProperty() { return idAnteproyecto; }
    public StringProperty tituloProperty() { return titulo; }
    public StringProperty modalidadProperty() { return modalidad; }
    public StringProperty rolProperty() { return rol; }

    public String getIdAnteproyecto() { return idAnteproyecto.get(); }
    public String getTitulo() { return titulo.get(); }
    public String getModalidad() { return modalidad.get(); }
    public String getRol() { return rol.get(); }
}
