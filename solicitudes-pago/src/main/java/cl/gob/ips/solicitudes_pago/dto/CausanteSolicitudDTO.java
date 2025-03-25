package cl.gob.ips.solicitudes_pago.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class CausanteSolicitudDTO {
    private int idCausanteSolicitud;
    private int iIdSolicitud;
    private int rutBeneficiario;
    private String vcDvBeneficiario;
    private int rutCausante;
    private String vcDvCausante;
    private String vcNombreCausante;
    private LocalDate fechaInicioRango;
    private LocalDate fechaFinRango;
    private String vcPeriodosAprobados;
    private BigDecimal totalReconocimiento;
    private BigDecimal totalPago;
    private List<DetalleCausanteDTO> detalle;
}