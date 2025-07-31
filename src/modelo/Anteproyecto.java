/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package modelo;

public class Anteproyecto {
    private int idAnteproyecto;
    private String titulo;
    private int cupo;
    private String tipo;
    private String descripcion;
    private String rfcEmpresa;       // Relación con la empresa
    private String nombreEmpresa;    // Para mostrar en la vista
    private String estatus;
    private boolean esPropuesta;     // NUEVO campo para marcar propuesta propia
    private int cupoDisponible;
    // Constructor principal SIN el campo esPropuesta (compatibilidad hacia atrás)
    public Anteproyecto(int idAnteproyecto, String titulo, int cupo, String tipo, String descripcion, String rfcEmpresa, String nombreEmpresa, String estatus) {
        this.idAnteproyecto = idAnteproyecto;
        this.titulo = titulo;
        this.cupo = cupo;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.rfcEmpresa = rfcEmpresa;
        this.nombreEmpresa = nombreEmpresa;
        this.estatus = estatus;
        this.esPropuesta = false; // Por defecto no es propuesta
    }

    // Constructor con el campo esPropuesta
    public Anteproyecto(int idAnteproyecto, String titulo, int cupo, String tipo, String descripcion, String rfcEmpresa, String nombreEmpresa, String estatus, boolean esPropuesta) {
        this.idAnteproyecto = idAnteproyecto;
        this.titulo = titulo;
        this.cupo = cupo;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.rfcEmpresa = rfcEmpresa;
        this.nombreEmpresa = nombreEmpresa;
        this.estatus = estatus;
        this.esPropuesta = esPropuesta;
    }
    
    public Anteproyecto(int idAnteproyecto, String titulo, int cupo, String tipo, String descripcion, String rfcEmpresa, String nombreEmpresa, String estatus, int cupoDisponible) {
        this.idAnteproyecto = idAnteproyecto;
        this.titulo = titulo;
        this.cupo = cupo;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.rfcEmpresa = rfcEmpresa;
        this.nombreEmpresa = nombreEmpresa;
        this.estatus = estatus;
        this.cupoDisponible = cupoDisponible;
    }
    
    // Getters y Setters

    public int getIdAnteproyecto() {
        return idAnteproyecto;
    }

    public void setIdAnteproyecto(int idAnteproyecto) {
        this.idAnteproyecto = idAnteproyecto;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getCupo() {
        return cupo;
    }

    public void setCupo(int cupo) {
        this.cupo = cupo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getRfcEmpresa() {
        return rfcEmpresa;
    }

    public void setRfcEmpresa(String rfcEmpresa) {
        this.rfcEmpresa = rfcEmpresa;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getEstatus() { 
        return estatus; 
    }

    public void setEstatus(String estatus) { 
        this.estatus = estatus; 
    }

    // NUEVO Getter y Setter para esPropuesta
    public boolean isEsPropuesta() {
        return esPropuesta;
    }

    public void setEsPropuesta(boolean esPropuesta) {
        this.esPropuesta = esPropuesta;
    }

    public int getCupoDisponible() {
        return cupoDisponible;
    }

    public void setCupoDisponible(int cupoDisponible) {
        this.cupoDisponible = cupoDisponible;
    }

    // Constructor vacío si lo necesitas
    public Anteproyecto() {}
}
