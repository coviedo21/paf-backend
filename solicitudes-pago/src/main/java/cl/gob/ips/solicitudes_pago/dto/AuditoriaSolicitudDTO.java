package cl.gob.ips.solicitudes_pago.dto;

import java.util.Date;

import lombok.Data;

@Data
public class AuditoriaSolicitudDTO {
    private int iIdAuditoriaSolicitud;
    private int iIdProceso;
    private int iIdSolicitud;
    private int iIdAutoreditor;
    private String vcObservaciones;
    private Date dFechaInicio;
    private Date dFechaFin;
    private int iCantidadRegistros;
    private int iTipoActualizacion;
}
