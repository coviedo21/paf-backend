package cl.gob.ips.proceso_pago.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class DatosProcesoPorTipoDTO {
    private Integer tipo;
    private String nombreTipoSolicitante;
    private Integer cantidad;
    private BigDecimal porcentaje;
    private Integer pagosTotales;
    private Integer pagosAprobados;
    private Integer pagosRechazados;
}