/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package modelo;

public class AsignacionAsesorRevisor {
    private int idAsignacion;
    private int idAnteproyecto;
    private String matriculaAsesor;     // Docente que es asesor
    private String[] matriculasRevisores; // Arreglo de hasta 5 revisores

    public AsignacionAsesorRevisor() {}

    public int getIdAsignacion() { return idAsignacion; }
    public void setIdAsignacion(int idAsignacion) { this.idAsignacion = idAsignacion; }

    public int getIdAnteproyecto() { return idAnteproyecto; }
    public void setIdAnteproyecto(int idAnteproyecto) { this.idAnteproyecto = idAnteproyecto; }

    public String getMatriculaAsesor() { return matriculaAsesor; }
    public void setMatriculaAsesor(String matriculaAsesor) { this.matriculaAsesor = matriculaAsesor; }

    public String[] getMatriculasRevisores() { return matriculasRevisores; }
    public void setMatriculasRevisores(String[] matriculasRevisores) { this.matriculasRevisores = matriculasRevisores; }
}

