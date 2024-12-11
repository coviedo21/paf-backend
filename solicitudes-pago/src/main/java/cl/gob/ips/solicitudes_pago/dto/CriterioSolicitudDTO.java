package cl.gob.ips.solicitudes_pago.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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
    private Date fechaDesde;
    private Date fechaHasta;
}