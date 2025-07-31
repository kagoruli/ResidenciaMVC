/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package modelo;

import java.sql.Date;

public class FormatoOficial {
    private int idFormato;
    private String tipo;          // Ejemplo: "ANTEPROYECTO", "SOLICITUD_RESIDENCIA"
    private String rutaPDF;
    private Date fechaSubida;

    public FormatoOficial() {}

    public FormatoOficial(int idFormato, String tipo, String rutaPDF, Date fechaSubida) {
        this.idFormato = idFormato;
        this.tipo = tipo;
        this.rutaPDF = rutaPDF;
        this.fechaSubida = fechaSubida;
    }

    // Getters y setters
    public int getIdFormato() { return idFormato; }
    public void setIdFormato(int idFormato) { this.idFormato = idFormato; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getRutaPDF() { return rutaPDF; }
    public void setRutaPDF(String rutaPDF) { this.rutaPDF = rutaPDF; }
    public Date getFechaSubida() { return fechaSubida; }
    public void setFechaSubida(Date fechaSubida) { this.fechaSubida = fechaSubida; }
}
