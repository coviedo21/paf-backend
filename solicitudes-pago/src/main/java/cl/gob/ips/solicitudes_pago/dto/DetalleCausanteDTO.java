package cl.gob.ips.solicitudes_pago.dto;

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
    private BigDecimal diferencia;
    //private BigDecimal diferenciaDerecho;
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
    private String rutNis;
    private String dvNis;
    private int numeroDocumento;
    private String dvDocumento;
    private Date fechaPago;
    private int idRetencion;
    private int rutReteniente;
    private String dvReteniente;
    private String nombresReteniente;
    private String apellidoPaternoReteniente;
    private String apellidoMaternoReteniente;
    private int idFormaPagoRetenedor;
    private String glosaFormaPagoRetenedor;
    private int idBancoRetenedor;
    private String glosaBancoRetenedor;
    private int idTipoCuentaRetenedor;
    private String glosaTipoCuentaRetenedor;
    private String numeroCuentaRetenedor;
    private String archivo;
    private int idProcesoPago;
    private int rutBeneficiarioPago;
    private String dvBeneficiarioPago;
    private int idFormaPagoFinal;
    private String glosaFormaPagoFinal;
    private int idBancoFinal;
    private String glosaBancoFinal;
    private int idTipoCuentaFinal;
    private String glosaTipoCuentaFinal;
    private String numeroCuentaFinal;
    private int diasPago;
    private int tipoSolicitante;
    private BigDecimal totalPago;
    private int valorTramo30;
}
