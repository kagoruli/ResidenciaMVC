/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.Perfil;
import modelo.PerfilDatos;
import vista.VistaPerfil;

/**
 *
 * @author jafet
 */
public class ControladorPerfil {
    public VistaPerfil crearVistaPerfil(String usuarioLogueado) {
        Perfil perfil = PerfilDatos.obtenerPerfilPorUsuario(usuarioLogueado);
        return new VistaPerfil(perfil);
    }
}
