package cl.gob.ips.solicitudes_pago.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class CausanteCuentaCorrienteDTO {
    private int rutCausante;
    private String dvRutCausante;
    private String nombreCausante;
    private LocalDate fechaInicioPeriodo;
    private LocalDate fechaFinPeriodo;
    private BigDecimal montoPagar;
    private String periodosAprobados;
    private List<DerechoCausanteDTO> detalle;
}
