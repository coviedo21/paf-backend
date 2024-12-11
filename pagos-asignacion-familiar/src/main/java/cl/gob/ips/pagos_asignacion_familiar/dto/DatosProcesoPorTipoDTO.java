package cl.gob.ips.pagos_asignacion_familiar.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class DatosProcesoPorTipoDTO {
    private Integer tipo;
    private Integer cantidad;
    private BigDecimal porcentaje;
    private Integer pagosTotales;
    private Integer pagosAprobadosCompletamente;
    private BigDecimal porcentajeAprobadosCompletamente;
    private Integer pagosRechazados;
    private BigDecimal porcentajeRechazados;
}