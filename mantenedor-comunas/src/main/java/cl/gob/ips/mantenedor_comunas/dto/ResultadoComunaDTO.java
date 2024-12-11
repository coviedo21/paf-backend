package cl.gob.ips.mantenedor_comunas.dto;

import lombok.Data;

@Data
public class ResultadoComunaDTO {
    private ListaComunaDTO[] listaComunas;
    private String mensaje;
}