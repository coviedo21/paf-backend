package cl.gob.ips.mantenedor_comunas.dto;

import lombok.Data;

@Data
public class ListaComunaDTO {
    private int codRegion;
    private int codProvincia;
    private int codComuna;
    private String nombreComuna;
}