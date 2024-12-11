package cl.gob.ips.solicitudes_pago.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudDTO {
    private int idSolicitud;
    private int idProceso;
    private int idUsuario;
    private int idTipoCausante;
    private int idComuna;
    private String nombreComuna;
    private Integer idInstitucion;  // Usar Integer para permitir valores nulos
    private Integer idHaber;
    private int idFormaPago;
    private String periodo;
    private String code;
    private Integer rutEmpleador;
    private String dvEmpleador;
    private String razonSocialEmpleador;
    private String direccion;
    private String email;
    private Integer rutBeneficiario;
    private String dvBeneficiario;
    private String nombresBeneficiario;
    private String apellidoPaternoBeneficiario;
    private String apellidoMaternoBeneficiario;
    private Integer rutCausante;
    private String dvCausante;
    private String nombresCausante;
    private String apellidoPaternoCausante;
    private String apellidoMaternoCausante;
    private Integer rutReteniente;
    private String dvReteniente;
    private String nombresReteniente;
    private String apellidoPaternoReteniente;
    private String apellidoMaternoReteniente;
    private Date inicioCompensacion;
    private Date finCompensacion;
    private String filePathPrevired;
    private String filePathEspecial;
    private int origen;
    private int tipoSolicitante;
    private String nombreTipoSolicitante;
    private int subTipoSolicitante;
    private String observaciones;
    private BigDecimal montoReconocimientoDerecho;
    private BigDecimal montoReconocimientoEfectivo;
    private String conRetencionJudicial;
    private String invalida;
    private String emitida;
    private Integer idCuentaBancaria;
    private Integer telefono;
    private Integer idRegion;
    private String finiquito;
    private Date fechaFiniquito;
    private Integer rutRepresentante;
    private String dvRepresentante;
    private Date fechaSolicitud;
    private Long folio;  // Usar Long para permitir valores nulos
    private String estado;
    private String nombreOrigen;
    private List<CausanteSolicitudDTO> listaCausantes;
    private String cumpleCriterios;
}