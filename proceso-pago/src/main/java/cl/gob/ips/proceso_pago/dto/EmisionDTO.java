package cl.gob.ips.proceso_pago.dto;

import java.util.Date;

import lombok.Data;

@Data
public class EmisionDTO {
    private int idEmision;
    private Date fechaEmision;
    private int idProceso;
    private String rutaArchivo;
}
