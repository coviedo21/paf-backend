package cl.gob.ips.proceso_pago.dto;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class PagoRetencionDTO {
	 private int estadoPago;
	 private Date fechaPago;
	 private int idRetencion;
	 private BigDecimal montoPagado;
	 private int tipoPago;
}
