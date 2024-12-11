package cl.gob.ips.solicitudes_pago.dto;

import java.util.Date;

import lombok.Data;

@Data
public class RetencionJudicialDTO {
    private long idRetencion;
    private long rutBeneficiario;
    private String rutBeneficiarioString;
    private String dvBeneficiario;
    private String rutCausante;
    private String rutCausanteString;
    private long rutRetenedor;
    private String rutRetenedorString;
    private long estadoRetencion;
    private long formaPago;
    private long cuentaBancaria;
    private Date fechaIngresoRetencion;
    private Date fechaInicio;
    private Date fechaFin;
    private String declaracionJurada;
    private String nombreFormaPago;
    private String nombreEstadoRetencion;
    private String tipoCausante;
}
