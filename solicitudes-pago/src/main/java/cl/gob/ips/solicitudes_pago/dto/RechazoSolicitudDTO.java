package cl.gob.ips.solicitudes_pago.dto;

import lombok.Data;

@Data
public class RechazoSolicitudDTO {
    private int idSolicitud;
    private int idMotivoRechazo;
}
