package cl.gob.ips.solicitudes_pago.dto;

import java.util.Date;

import lombok.Data;

@Data
public class ResponseDTO {
    private String idConsumidor;
    private String idRequerimiento;
    private int codigoRetorno;
    private String glosaRetorno;
    private Object resultado;
    private Date timestamp;
}
