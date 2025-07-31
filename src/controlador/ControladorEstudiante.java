/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

/**
 *
 * @author jafet
 */
import modelo.ConexionDB;
import modelo.Utilidades;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import modelo.EnviadorCorreo;

public class ControladorEstudiante {

    public static String registrarEstudiante(
            String nombre, String paterno, String materno,
            String correo, String numeroControl) {

        // Validación básica
        if (nombre.isEmpty() || paterno.isEmpty() || materno.isEmpty() ||
                correo.isEmpty() || numeroControl.isEmpty()) {
            return "Por favor complete todos los campos.";
        }

        // Validar que nombre y apellidos solo tengan letras (permitiendo tildes y ñ)
        if (!nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            return "El nombre solo puede contener letras";
        }

        if (!paterno.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            return "El apellido paterno solo puede contener letras";
        }

        if (!materno.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            return "El apellido materno solo puede contener letras";
        }

        // Validar matrícula: letras y números, sin espacios ni símbolos
        if (!numeroControl.matches("^[a-zA-Z0-9]+$")) {
            return "El número de control solo puede contener letras y números";
        }

        // Validar que los dos primeros dígitos (si existen) sean máximo 25
        if (numeroControl.length() >= 2) {
            String primerosDos = numeroControl.substring(0, 2);
            try {
                int prefijo = Integer.parseInt(primerosDos);
                if (prefijo > 25) {
                    return "El número de control no puede comenzar con un número mayor a 25";
                }
            } catch (NumberFormatException e) {
                return "El número de control debe comenzar con dos números válidos";
            }
        }
        
        if (!correo.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            return "Por favor ingrese un correo electrónico válido.";
        }

        String contrasenaSimple = String.format("%06d", new Random().nextInt(999999));
        String contrasenaHash = Utilidades.hashSHA256(contrasenaSimple);

        Connection conn = null;
        try {
            conn = ConexionDB.conectar();
            if (conn == null) throw new SQLException("No se pudo conectar a la base de datos.");
            conn.setAutoCommit(false); // Inicia transacción

            // 1. Obtener id_rol del rol estudiante
            int idRolEstudiante = -1;
            String queryRol = "SELECT id_rol FROM rol WHERE LOWER(nombre_rol) = 'estudiante'";
            try (PreparedStatement psRol = conn.prepareStatement(queryRol);
                 ResultSet rsRol = psRol.executeQuery()) {
                if (rsRol.next()) {
                    idRolEstudiante = rsRol.getInt("id_rol");
                } else {
                    throw new SQLException("No existe el rol 'estudiante' en la base de datos.");
                }
            }

            // 2. Insertar en usuario y obtener id_usuario
            String queryUsuario = "INSERT INTO usuario (nombre_usuario, contrasena_usuario, id_rol) VALUES (?, ?, ?) RETURNING id_usuario";
            int idUsuario;
            try (PreparedStatement psUsuario = conn.prepareStatement(queryUsuario)) {
                psUsuario.setString(1, numeroControl);
                psUsuario.setString(2, contrasenaHash);
                psUsuario.setInt(3, idRolEstudiante);
                try (ResultSet rsUsuario = psUsuario.executeQuery()) {
                    if (rsUsuario.next()) {
                        idUsuario = rsUsuario.getInt("id_usuario");
                    } else {
                        throw new SQLException("No se pudo insertar el usuario.");
                    }
                }
            }

            // 3. Insertar en persona y obtener id_persona
            String queryPersona = "INSERT INTO persona (nombre_persona, apellido_paterno_persona, apellido_materno_persona, correo_persona, id_usuario) VALUES (?, ?, ?, ?, ?) RETURNING id_persona";
            int idPersona;
            try (PreparedStatement psPersona = conn.prepareStatement(queryPersona)) {
                psPersona.setString(1, nombre);
                psPersona.setString(2, paterno);
                psPersona.setString(3, materno);
                psPersona.setString(4, correo);
                psPersona.setInt(5, idUsuario);
                try (ResultSet rsPersona = psPersona.executeQuery()) {
                    if (rsPersona.next()) {
                        idPersona = rsPersona.getInt("id_persona");
                    } else {
                        throw new SQLException("No se pudo insertar la persona.");
                    }
                }
            }

            // 4. Insertar en estudiante
            String queryEstudiante = "INSERT INTO estudiante (numero_control, id_persona) VALUES (?, ?)";
            try (PreparedStatement psEstudiante = conn.prepareStatement(queryEstudiante)) {
                psEstudiante.setString(1, numeroControl);
                psEstudiante.setInt(2, idPersona);
                psEstudiante.executeUpdate();
            }

            conn.commit(); // Si todo fue bien, confirma la transacción

            // ----- ENVÍA EL CORREO AQUÍ -----
            // (Puedes manejar excepciones del correo por separado si quieres)
            try {
                EnviadorCorreo.enviarCorreoBienvenida(correo, numeroControl, contrasenaSimple);
            } catch (Exception exCorreo) {
                System.err.println("No se pudo enviar el correo al estudiante: " + exCorreo.getMessage());
                // Si quieres, puedes retornar advertencia pero dejar el registro exitoso
            }

            return "ÉXITO:" + contrasenaSimple;

        } catch (SQLException ex) {
            // Reversión de la transacción si hubo error
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException e) { /* Ignora */ }
            }
            String msg = ex.getMessage().toLowerCase();

            // Detectar violaciones de clave única y personalizar mensajes
            if (msg.contains("usuario_nombre_usuario_key") || (msg.contains("llave duplicada") && msg.contains("nombre_usuario"))) {
                return "Ya hay un estudiante vinculado a ese número de control.";
            } else if (msg.contains("correo_persona_key") || (msg.contains("llave duplicada") && msg.contains("correo_persona"))) {
                return "Ya existe un estudiante con ese correo electrónico.";
            } else if (msg.contains("numero_control") || (msg.contains("llave duplicada") && msg.contains("numero_control"))) {
                return "Ya hay un usuario vinculado a ese número de control.";
            } else {
                return "Error de base de datos: " + ex.getMessage();
            }
        } finally {
            // Restaurar autocommit y cerrar conexión
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { /* Ignora */ }
            }
        }
    }
    
    public static String editarEstudiante(
    String numeroControlOriginal,
    String nuevoNumeroControl,
    String nombre, String paterno, String materno, String correo) {

    // Validaciones aquí (igual que en registrarEstudiante)...
    if (nombre.isEmpty() || paterno.isEmpty() || materno.isEmpty() ||
        correo.isEmpty() || nuevoNumeroControl.isEmpty()) {
        return "Por favor complete todos los campos.";
    }

    if (!nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
        return "El nombre solo puede contener letras";
    }
    if (!paterno.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
        return "El apellido paterno solo puede contener letras";
    }
    if (!materno.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
        return "El apellido materno solo puede contener letras";
    }
    if (!nuevoNumeroControl.matches("^[a-zA-Z0-9]+$")) {
        return "El número de control solo puede contener letras y números";
    }
    if (nuevoNumeroControl.length() >= 2) {
        String primerosDos = nuevoNumeroControl.substring(0, 2);
        try {
            int prefijo = Integer.parseInt(primerosDos);
            if (prefijo > 25) {
                return "La matrícula no puede comenzar con un número mayor a 25";
            }
        } catch (NumberFormatException e) {
            return "La matrícula debe comenzar con dos números válidos";
        }
    }
    if (!correo.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
        return "Por favor ingrese un correo electrónico válido.";
    }

    // Llama al DAO
    int resultado = modelo.EstudianteDatos.actualizarEstudianteCompleto(
            numeroControlOriginal, nuevoNumeroControl, nombre, paterno, materno, correo);

    if (resultado == 1) return "ÉXITO:";
    else if (resultado == -1) return "El ingresado correo ya está en uso.";
    else if (resultado == -2) return "Ya hay un estudiante vinculado a ese número de control.";
    else return "Error al actualizar el estudiante.";
}

}