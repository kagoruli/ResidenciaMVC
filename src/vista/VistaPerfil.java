/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

/**
 *
 * @author jafet
 */
/**
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import modelo.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class VistaPerfil extends VBox {

    public VistaPerfil(String usuarioLogueado) {
        super(20);
        setPadding(new Insets(40));
        setAlignment(Pos.TOP_CENTER);
        setSpacing(20);
        getStyleClass().add("form-container");

        // Consultar datos desde la BD
        String nombre = "";
        String paterno = "";
        String materno = "";
        String correo = "";
        String rol = "";
        String icono = "estudiante";

        String sql = "SELECT p.nombre_persona, p.apellido_paterno_persona, p.apellido_materno_persona, " +
                "p.correo_persona, r.nombre_rol " +
                "FROM persona p " +
                "JOIN usuario u ON p.id_usuario = u.id_usuario " +
                "JOIN rol r ON u.id_rol = r.id_rol " +
                "WHERE u.nombre_usuario = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuarioLogueado);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                nombre = rs.getString("nombre_persona");
                paterno = rs.getString("apellido_paterno_persona");
                materno = rs.getString("apellido_materno_persona");
                correo = rs.getString("correo_persona");
                rol = rs.getString("nombre_rol");
                icono = rol.equalsIgnoreCase("estudiante") ? "estudiante" : "docente";
            }
        } catch (Exception ex) {
            System.err.println("Error obteniendo datos de perfil: " + ex.getMessage());
        }

        // Icono grande
        ImageView iconoGrande = loadIcon(icono, 120);

        // Labels
        Label lblTitulo = new Label("Mi perfil");
        lblTitulo.getStyleClass().add("form-title");

        Label lblNombre = new Label("Nombre completo: " + nombre + " " + paterno + " " + materno);
        Label lblUsuario = new Label((icono.equals("estudiante") ? "Número de control" : "Número de Tarjeta") + ": " + usuarioLogueado);
        Label lblCorreo = new Label("Correo electrónico: " + correo);
        Label lblRol = new Label("Rol: " + rol);

        lblNombre.getStyleClass().add("form-label");
        lblUsuario.getStyleClass().add("form-label");
        lblCorreo.getStyleClass().add("form-label");
        lblRol.getStyleClass().add("form-label");

        VBox datos = new VBox(14, lblNombre, lblUsuario, lblCorreo, lblRol);
        datos.setAlignment(Pos.CENTER_LEFT);

        getChildren().addAll(iconoGrande, lblTitulo, datos);
    }

    private ImageView loadIcon(String iconName, double size) {
        try (var is = getClass().getResourceAsStream("/vista/icons/" + iconName + ".png")) {
            if (is != null) {
                Image iconImage = new Image(is);
                ImageView icon = new ImageView(iconImage);
                icon.setFitHeight(size);
                icon.setPreserveRatio(true);
                return icon;
            }
            System.err.println("Icono de perfil no encontrado: " + iconName);
            return null;
        } catch (Exception e) {
            System.err.println("Error cargando icono: " + e.getMessage());
            return null;
        }
    }
}*/
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import modelo.Perfil;

public class VistaPerfil extends VBox {
    public VistaPerfil(Perfil perfil) {
        super(20);
        setPadding(new Insets(40));
        setAlignment(Pos.TOP_CENTER);
        setSpacing(20);
        getStyleClass().add("form-container");

        ImageView iconoGrande = loadIcon(perfil.getIcono(), 120);

        Label lblTitulo = new Label("Mi perfil");
        lblTitulo.getStyleClass().add("form-title");

        Label lblNombre = new Label("Nombre completo: " + perfil.getNombreCompleto());
        Label lblUsuario = new Label((perfil.getIcono().equals("estudiante") ? "Número de control" : "Número de Tarjeta") + ": " + perfil.getUsuario());
        Label lblCorreo = new Label("Correo electrónico: " + perfil.getCorreo());
        Label lblRol = new Label("Rol: " + perfil.getRol());

        lblNombre.getStyleClass().add("form-label");
        lblUsuario.getStyleClass().add("form-label");
        lblCorreo.getStyleClass().add("form-label");
        lblRol.getStyleClass().add("form-label");

        VBox datos = new VBox(14, lblNombre, lblUsuario, lblCorreo, lblRol);
        datos.setAlignment(Pos.CENTER_LEFT);

        getChildren().addAll(iconoGrande, lblTitulo, datos);
    }

    private ImageView loadIcon(String iconName, double size) {
        try (var is = getClass().getResourceAsStream("/vista/icons/" + iconName + ".png")) {
            if (is != null) {
                Image iconImage = new Image(is);
                ImageView icon = new ImageView(iconImage);
                icon.setFitHeight(size);
                icon.setPreserveRatio(true);
                return icon;
            }
            System.err.println("Icono de perfil no encontrado: " + iconName);
            return null;
        } catch (Exception e) {
            System.err.println("Error cargando icono: " + e.getMessage());
            return null;
        }
    }
}
