/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package modelo;

import java.sql.Date;

public class PropuestaPropia {
    private int idPropuesta;
    private String idEstudiante;
    private Integer idAnteproyecto; // <-- Integer para permitir null
    private String rutaArchivo;
    private String estado;
    private String comentarios;
    private Date fechaEntrega;

    public PropuestaPropia() {}

    public PropuestaPropia(int idPropuesta, String idEstudiante, Integer idAnteproyecto, String rutaArchivo, String estado, String comentarios, Date fechaEntrega) {
        this.idPropuesta = idPropuesta;
        this.idEstudiante = idEstudiante;
        this.idAnteproyecto = idAnteproyecto;
        this.rutaArchivo = rutaArchivo;
        this.estado = estado;
        this.comentarios = comentarios;
        this.fechaEntrega = fechaEntrega;
    }

    public int getIdPropuesta() { return idPropuesta; }
    public void setIdPropuesta(int idPropuesta) { this.idPropuesta = idPropuesta; }

    public String getIdEstudiante() { return idEstudiante; }
    public void setIdEstudiante(String idEstudiante) { this.idEstudiante = idEstudiante; }

    public Integer getIdAnteproyecto() { return idAnteproyecto; }
    public void setIdAnteproyecto(Integer idAnteproyecto) { this.idAnteproyecto = idAnteproyecto; }

    public String getRutaArchivo() { return rutaArchivo; }
    public void setRutaArchivo(String rutaArchivo) { this.rutaArchivo = rutaArchivo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getComentarios() { return comentarios; }
    public void setComentarios(String comentarios) { this.comentarios = comentarios; }

    public Date getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(Date fechaEntrega) { this.fechaEntrega = fechaEntrega; }
}
