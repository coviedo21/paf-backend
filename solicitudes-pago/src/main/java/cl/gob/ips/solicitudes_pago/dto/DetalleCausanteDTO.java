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
    private int iNis;
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
    private int idFormaPago;
    private int idBanco;
    private String numeroCuenta;
    private String archivo;
}
