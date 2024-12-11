package cl.gob.ips.solicitudes_pago.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;


@Configuration
public class EmailConfig {
    
    // private static final String SMTP_HOST = "smtp.office365.com";
    // private static final int SMTP_PORT = 587;
    // // private static final String USERNAME = "ngilc.srv@ips.gob.cl";
    // // private static final String PASSWORD = "Ips.2023\"!";
    // private static final String USERNAME = "cgaetej.srv@ips.gob.cl";
    // private static final String PASSWORD = "ips_2023";

    private static final String SMTP_HOST = "email-smtp.us-east-1.amazonaws.com";
    private static final int SMTP_PORT = 587;
    private static final String USERNAME = "AKIAYIIH2KSXOCSSXEM4";
    private static final String PASSWORD = "BLlnDoXAjesQBvUp8zLO555N9X2S519KZ6F0aU8J/mLf";

    /*private static final String SMTP_HOST = "localhost";
    private static final int SMTP_PORT = 1025; // Puerto por defecto de Mailpit*/

    /*@Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(SMTP_HOST);
        mailSender.setPort(SMTP_PORT);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "false");  // Mailpit no requiere autenticación
        props.put("mail.smtp.starttls.enable", "false");  // Desactiva TLS ya que es localhost
        props.put("mail.debug", "true");  // Activar el modo de depuración

        return mailSender;
    }*/

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(SMTP_HOST);
        mailSender.setPort(SMTP_PORT);
        mailSender.setUsername(USERNAME);
        mailSender.setPassword(PASSWORD);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "false");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }
}