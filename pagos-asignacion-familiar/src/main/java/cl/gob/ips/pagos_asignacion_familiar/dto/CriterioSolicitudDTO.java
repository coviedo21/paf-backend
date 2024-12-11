package cl.gob.ips.pagos_asignacion_familiar.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CriterioSolicitudDTO {
    private int idCriterioSolicitud;
    private int idSolicitud;
    private int idCriterio;
    private String cumple;
    private String archivo;
    private String nombreCriterio;
}