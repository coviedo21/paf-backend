package cl.gob.ips.solicitudes_pago.dto;

import java.util.Date;

import lombok.Data;

@Data
public class ResolucionDTO {
    private int idSolicitud;
    private int idEstado;
    private String vcDescripcion;
    private String vcApelable;
    private int autor;
    private Date dFechaAval;
    private Date dFechaApelacion;
    private String vcVigente;
    private Integer motivoRechazo;
    private String usuario;
}
