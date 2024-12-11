package cl.gob.ips.mantenedor_comunas.dto;

import lombok.Data;

@Data
public class ComunaDTO {
    private String codigoRetorno;
    private String glosaRetorno;
    private String timestamp;
    private ResultadoComunaDTO resultado;
}