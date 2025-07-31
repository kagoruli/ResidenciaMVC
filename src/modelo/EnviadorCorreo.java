/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author jafet
 */
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EnviadorCorreo {

    private static final String emailFrom = "jafosgamer@gmail.com"; // Tu correo Gmail
    private static final String passwordFrom = "lkccpxzkpnxbejds"; // Contraseña de aplicación

    // --- Correo de bienvenida (registro) ---
    public static void enviarCorreoBienvenida(String destinatario, String usuario, String contrasena) {
        String subject = "¡Bienvenido al Sistema de Residencias Profesionales!";
        String content = "<h2>¡Registro exitoso!</h2>"
                + "<p><b>Usuario:</b> " + usuario + "<br>"
                + "<b>Contraseña:</b> " + contrasena + "</p>";
        enviar(destinatario, subject, content);
    }

    // --- Correo de recuperación ---
    public static void enviarCorreoRecuperacion(String destinatario, String usuario, String contrasena) {
        String subject = "Recuperación de usuario y contraseña";
        String content = "<h2>Solicitud de recuperación de acceso</h2>"
                + "<p>Recientemente se solicitó recuperar tu acceso al sistema.</p>"
                + "<p><b>Usuario:</b> " + usuario + "<br>"
                + "<b>Nueva contraseña:</b> " + contrasena + "</p>";
        enviar(destinatario, subject, content);
    }

    // --- Método interno para enviar correo ---
    private static void enviar(String destinatario, String subject, String content) {
        Properties mProperties = new Properties();
        mProperties.put("mail.smtp.host", "smtp.gmail.com");
        mProperties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        mProperties.setProperty("mail.smtp.starttls.enable", "true");
        mProperties.setProperty("mail.smtp.port", "587");
        mProperties.setProperty("mail.smtp.user", emailFrom);
        mProperties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
        mProperties.setProperty("mail.smtp.auth", "true");

        Session mSession = Session.getDefaultInstance(mProperties);

        try {
            MimeMessage mCorreo = new MimeMessage(mSession);
            mCorreo.setFrom(new InternetAddress(emailFrom));
            mCorreo.setRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
            mCorreo.setSubject(subject);
            mCorreo.setContent(content, "text/html; charset=UTF-8");

            Transport mTransport = mSession.getTransport("smtp");
            mTransport.connect(emailFrom, passwordFrom);
            mTransport.sendMessage(mCorreo, mCorreo.getRecipients(Message.RecipientType.TO));
            mTransport.close();

            System.out.println("Correo enviado exitosamente a " + destinatario);

        } catch (MessagingException e) {
            System.err.println("Error al enviar correo: " + e.getMessage());
        }
    }
}

