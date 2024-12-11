package cl.gob.ips.solicitudes_pago.dto;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class InsertarRetencionJudicialDTO {
    private long estadoPago;
    private Date fechaPago;
    private long idRetencion;
    private BigDecimal montoPagado;
    private long tipoPago;
}