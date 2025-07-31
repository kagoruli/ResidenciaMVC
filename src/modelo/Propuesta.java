/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package modelo;

import java.sql.Date;

public class Propuesta {
    private int idPropuesta;
    private String idEstudiante;
    private int idAnteproyecto;
    private String rutaArchivo;
    private String estado; // Pendiente, Aceptado, Corregir, Rechazado
    private String comentarios;
    private Date fechaEntrega; // Cambiado a java.sql.Date
    
    // Campos para mostrar en la tabla
    private String nombreEstudiante;
    private String tituloAnteproyecto;

    // Getters y setters
    public int getIdPropuesta() { return idPropuesta; }
    public void setIdPropuesta(int idPropuesta) { this.idPropuesta = idPropuesta; }
    
    public String getIdEstudiante() { return idEstudiante; }
    public void setIdEstudiante(String idEstudiante) { this.idEstudiante = idEstudiante; }
    
    public int getIdAnteproyecto() { return idAnteproyecto; }
    public void setIdAnteproyecto(int idAnteproyecto) { this.idAnteproyecto = idAnteproyecto; }
    
    public String getRutaArchivo() { return rutaArchivo; }
    public void setRutaArchivo(String rutaArchivo) { this.rutaArchivo = rutaArchivo; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public String getComentarios() { return comentarios; }
    public void setComentarios(String comentarios) { this.comentarios = comentarios; }
    
    public Date getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(Date fechaEntrega) { this.fechaEntrega = fechaEntrega; }
    
    public String getNombreEstudiante() { return nombreEstudiante; }
    public void setNombreEstudiante(String nombreEstudiante) { 
        this.nombreEstudiante = nombreEstudiante; 
    }
    
    public String getTituloAnteproyecto() { return tituloAnteproyecto; }
    public void setTituloAnteproyecto(String tituloAnteproyecto) { 
        this.tituloAnteproyecto = tituloAnteproyecto; 
    }
}