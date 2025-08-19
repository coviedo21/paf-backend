package cl.gob.ips.solicitudes_pago.service.serviceImpl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ByteArrayResource;

import cl.gob.ips.solicitudes_pago.dto.CausanteSolicitudDTO;
import cl.gob.ips.solicitudes_pago.dto.SolicitudDTO;
import cl.gob.ips.solicitudes_pago.service.EmailService;
import cl.gob.ips.solicitudes_pago.service.PDFService;
import cl.gob.ips.solicitudes_pago.service.SolicitudPagoService;

@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    
     @Autowired
    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }
     	
     @Autowired
  	PDFService pdfService;

     @Override
     public void enviarCorreo(String destinatario, String asunto, String cuerpoHtml, boolean adjuntarPdf, int idSolicitud) {
         try {
             MimeMessage mensaje = javaMailSender.createMimeMessage();
             MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

             helper.setFrom("no-reply@ips.gob.cl");
             helper.setTo(destinatario);
             helper.setSubject(asunto);
             helper.setText(cuerpoHtml, true); // true = HTML

             if (adjuntarPdf) {
                 byte[] pdf = pdfService.generarSolicitudAsignacionFamiliar(idSolicitud);
                 String nombreArchivo = "solicitud_" + idSolicitud + ".pdf";
                 helper.addAttachment(nombreArchivo, new ByteArrayResource(pdf));
             }

             javaMailSender.send(mensaje);
         } catch (MailException e) {
             System.err.println("Error al enviar el correo: " + e.getMessage());
         } catch (Exception e) {
             System.err.println("Error al enviar el correo: " + e.getMessage());
             e.printStackTrace();
         }
     }
    
}