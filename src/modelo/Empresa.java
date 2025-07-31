/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author jafet
 */
public class Empresa {
    private String nombre;
    private String representante;
    private String telefono;
    private String descripcion;
    private String rfc;
    private String estatus;

    public Empresa(String nombre, String rfc, String representante, String telefono, String descripcion) {
        this.nombre = nombre;
        this.rfc=rfc;
        this.representante = representante;
        this.telefono = telefono;
        this.descripcion = descripcion;
    }

    
    // Agrega un constructor con estatus también
    public Empresa(String nombre, String rfc, String representante, String telefono, String descripcion, String estatus) {
        this.nombre = nombre;
        this.rfc = rfc;
        this.representante = representante;
        this.telefono = telefono;
        this.descripcion = descripcion;
        this.estatus = estatus;
    }

    public Empresa() {}


    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRepresentante() {
        return representante;
    }

    public void setRepresentante(String representante) {
        this.representante = representante;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }
}


