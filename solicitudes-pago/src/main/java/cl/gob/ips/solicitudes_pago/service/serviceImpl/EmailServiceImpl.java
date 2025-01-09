package cl.gob.ips.solicitudes_pago.service.serviceImpl;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import cl.gob.ips.solicitudes_pago.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;

     @Autowired
    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void enviarCorreo(String destinatario, String asunto, String cuerpo) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom("no-reply@ips.gob.cl");
            mensaje.setTo(destinatario);
            mensaje.setSubject(asunto);
            mensaje.setText(cuerpo);

            javaMailSender.send(mensaje);
        } catch (MailException e) {
            System.err.println("Error al enviar el correo: " + e.getMessage());
        } catch (Exception e) {
            // Captura la excepción y continúa la ejecución
            System.err.println("Error al enviar el correo: " + e.getMessage());
            e.printStackTrace(); // Opcional: muestra la traza del error para debugging
        }
    }
}