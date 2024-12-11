package cl.gob.ips.pagos_asignacion_familiar.dto;

import lombok.Data;

@Data
public class ComunaDTO {
    private String codigoRetorno;
    private String glosaRetorno;
    private String timestamp;
    private ResultadoComunaDTO resultado;
}