package cl.gob.ips.pagos_asignacion_familiar.dto;

import java.util.Date;

import lombok.Data;

@Data
public class ResolucionDTO {
    private int iIdSolicitud;
    private int iIdEstado;
    private String vcDescripcion;
    private String vcApelable;
    private int iAutor;
    private Date dFechaAval;
    private Date dFechaApelacion;
    private String vcVigente;
}
