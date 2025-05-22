package cl.gob.ips.solicitudes_pago.dto;

import java.util.Date;

import lombok.Data;

@Data
public class CuentaCorrienteResponseDTO {
    private int codigoRetorno;
    private String glosaRetorno;
    private CuentaCorrienteListaDTO resultado;
    private Date timestamp;
}
