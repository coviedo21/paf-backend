package cl.gob.ips.solicitudes_pago.dto;

import lombok.Data;

@Data
public class ListaComunaDTO {
    private int codRegion;
    private int codProvincia;
    private int codComuna;
    private String nombreComuna;
}