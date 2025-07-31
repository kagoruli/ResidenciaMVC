/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author jafet
 */
public class Estudiante {
    private String numeroControl;
    private int idPersona;
    private String estado; // ACTIVO, FINALIZADO, BAJA

    public Estudiante() {}

    public Estudiante(String numeroControl, int idPersona, String estado) {
        this.numeroControl = numeroControl;
        this.idPersona = idPersona;
        this.estado = estado;
    }

    // Getters y Setters
    public String getNumeroControl() { return numeroControl; }
    public void setNumeroControl(String numeroControl) { this.numeroControl = numeroControl; }

    public int getIdPersona() { return idPersona; }
    public void setIdPersona(int idPersona) { this.idPersona = idPersona; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado;}
}
