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
    private Integer rutReteniente;
    private String dvReteniente;
    private String nombresReteniente;
    private String apellidoPaternoReteniente;
    private String apellidoMaternoReteniente;
    private String filePathPrevired;
    private String filePathEspecial;
    private int origen;
    private int tipoSolicitante;
    private String nombreTipoSolicitante;
    private String observaciones;
    private String conRetencionJudicial;
    private Integer idCuentaBancaria;
    private Integer telefono;
    private Integer idRegion;
    private String nombreRegion;
    private String finiquito;
    private Date fechaFiniquito;
    private Integer rutRepresentante;
    private String dvRepresentante;
    private Date fechaSolicitud;
    private Long folio;  // Usar Long para permitir valores nulos
    private Integer idEstado;
    private String estado;
    private String nombreOrigen;
    private List<CausanteSolicitudDTO> listaCausantes;
    private String cumpleCriterios;
    private String ciudadEmpleador;
    private String motivoRechazo;
    private BigDecimal montoHaber;
    private String esPortuario;
    private BigDecimal totalPago;
    private int codigoHaber;
    private int origenPago;
    private int corrProceso;
    private int corrHaber;
    private String usuarioCreacion;
    private String usuarioModificacion;
    private String finiquitoValidado;
    private String rutaMaestro;
}