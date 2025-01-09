package cl.gob.ips.solicitudes_pago.dto;

import lombok.Data;

@Data
public class MotivoRechazoDTO {
    private Integer idMotivoRechazo;
    private String nombre;
    private Boolean vigente;
}