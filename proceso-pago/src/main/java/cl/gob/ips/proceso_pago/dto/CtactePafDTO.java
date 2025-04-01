package cl.gob.ips.proceso_pago.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CtactePafDTO {
    private Integer nPagoPAFID;
    private Integer nMesRemuneracion;
    private Integer nRutCausante;
    private String cDvCausante;
    private Integer nRutBeneficiario;
    private String nDvRutBeneficiario;
    private Integer nRutEmpleador;
    private String nDvRutEmpleador;
    private Integer nIdBeneficio;
    private Integer nIdTipoBeneficio;
    private Integer nIdTipoBeneficiario;
    private Integer nTipoCausanteID;
    private Integer nNumDiasAsigFam;
    private Integer nCodTramo;
    private BigDecimal nMontoBeneficio;
    private BigDecimal nMontoDocumento;
    private String vcNumeroDocumento;
    private Integer nFechaEmisionDocumento;
    private Integer nCodigoBanco;
    private Integer nCodigoInstitucion;
    private String vcOrigen;
    private Integer nTipoEmision;
    private Integer nTipoDeclaracion;
    private String vcNumeroSerie;
    private String vcNumeroPlanilla;
    private Integer nCodigoCausalReliquidacion;
    private Integer nCodigoTipoEgreso;
    private Integer nTipoMovimientoID;
    private String vcUsuarioCreacion;
    private LocalDateTime dFechaCreacion;
    private LocalDateTime dFechaActualizacion;
    private String vcUsuarioActualizacion;
}