package cl.gob.ips.pagos_asignacion_familiar.dto;

import java.time.OffsetDateTime;

import lombok.Data;

@Data
public class RegionDTO {
    private String codigoRetorno;
    private String glosaRetorno;
    private OffsetDateTime timestamp;
    private ResultadoRegionDTO[] resultado;
}