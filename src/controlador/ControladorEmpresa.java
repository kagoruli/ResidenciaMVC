/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

/**
 *
 * @author jafet
 */
import modelo.Empresa;
import modelo.EmpresaDatos;
import javafx.scene.control.Alert;
import vista.FormularioEmpresa;

public class ControladorEmpresa {

    private FormularioEmpresa vista;

    public ControladorEmpresa(FormularioEmpresa vista) {
        this.vista = vista;
    }

    public void registrarEmpresa(String nombre, String rfc, String representante, String telefono, String descripcion) {

        // Validar si ya existe el RFC antes de intentar registrar
        if (EmpresaDatos.existeRFC(rfc)) {
            vista.mostrarError("RFC duplicado", "Ya existe una empresa vinculada a ese RFC.");
            return;
        }
        
        Empresa empresa = new Empresa(nombre, rfc, representante, telefono, descripcion);

        try {
            boolean exito = EmpresaDatos.insertarEmpresa(empresa);
            if (exito) {
                vista.mostrarConfirmacion();
            } else {
                vista.mostrarError("Error de base de datos", "No se pudo registrar la empresa.");
            }
        } catch (Exception e) {
            vista.mostrarError("Error de base de datos", e.getMessage());
        }
    }
}
