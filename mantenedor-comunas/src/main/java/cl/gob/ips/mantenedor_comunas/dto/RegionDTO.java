package cl.gob.ips.mantenedor_comunas.dto;

import java.time.OffsetDateTime;

import lombok.Data;

@Data
public class RegionDTO {
    private String codigoRetorno;
    private String glosaRetorno;
    private OffsetDateTime timestamp;
    private ResultadoRegionDTO[] resultado;
}