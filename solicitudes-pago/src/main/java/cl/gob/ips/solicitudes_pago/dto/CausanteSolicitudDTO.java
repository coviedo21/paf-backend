package cl.gob.ips.solicitudes_pago.dto;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class CausanteSolicitudDTO {
    private int iIdCausanteSolicitud;
    private int iIdSolicitud;
    private int rutBeneficiario;
    private String vcDvBeneficiario;
    private int rutCausante;
    private String vcDvCausante;
    private String vcNombreCausante;
    private Date fechaInicioRango;
    private Date fechaFinRango;
    private String vcPeriodosAprobados;
    private BigDecimal totalPagar;
}