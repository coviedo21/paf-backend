package cl.gob.ips.solicitudes_pago.service.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
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
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom("no-reply@ips.gob.cl");
        mensaje.setTo(destinatario);
        mensaje.setSubject(asunto);
        mensaje.setText(cuerpo);

        javaMailSender.send(mensaje);
    }
}