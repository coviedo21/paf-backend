package cl.gob.ips.solicitudes_pago.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class CausanteCuentaCorrienteDTO {
    private int rutCausante;
    private String dvRutCausante;
    private String nombreCausante;
    private String rangoFechas;
    private BigDecimal montoPagar;
    private String periodosAprobados;
    private List<DerechoCausanteDTO> detalle;
}
