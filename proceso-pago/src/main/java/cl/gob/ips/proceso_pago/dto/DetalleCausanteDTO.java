package cl.gob.ips.proceso_pago.dto;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class DetalleCausanteDTO {

    private int idDetalleCausante;
    private int idCausanteSolicitud;
    private int rutCausante;
    private String dvCausante;
    private int rutBeneficiario;
    private String dvBeneficiario;
    private int periodo;
    private int tipoMovimiento;
    private Date fechaMovimiento;
    private String entradaSalida;
    private BigDecimal montoMovimiento;
    private int tipoCausante;
    private int idBeneficio;
    private BigDecimal rentaPromedio;
    private int diasReconocimiento;
    private int codigoTramo;
    private Date fechaFinVigencia;
    private Date fechaInicioVigencia;
    private int estado;
    private String nombreEstado;
    private int rutEmpleador;
    private String dvEmpleador;
}
