package cl.gob.ips.solicitudes_pago.service;

public interface EmailService{
    void enviarCorreo(String destinatario, String asunto, String cuerpo);
}