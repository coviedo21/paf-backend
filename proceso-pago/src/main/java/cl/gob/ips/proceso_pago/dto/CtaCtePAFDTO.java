package cl.gob.ips.proceso_pago.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CtaCtePAFDTO {
//    private BigDecimal nPagoPAFID;
    private BigDecimal nMesRemuneracion;
    private BigDecimal nRUTCausante;
    private String cdvCausante;
    private BigDecimal nRUTBeneficiario;
    private String ndvRUTBeneficiario;
    private BigDecimal nRUTEmpleador;
    private String ndvRUTEmpleador;
    private BigDecimal nIDBeneficio;
    private BigDecimal nIDTipoBeneficio;
    private BigDecimal nIDTipoBeneficiario;
    private BigDecimal nTipoCausanteID;
    private BigDecimal nNumDiasAsigFam;
    private BigDecimal nCodTramo;
    private BigDecimal nMontoBeneficio;
    private BigDecimal nMontoDocumento;
    private String vcNumeroDocumento;
    private Date dFechaEmisionDocumento;
    private BigDecimal nCodigoBanco;
    private BigDecimal nCodigoInstitucion;
    private String vcOrigen;
    private BigDecimal nTipoEmision;
    private BigDecimal nTipoDeclaracion;
    private String vcNumeroSerie;
    private String vcNumeroPlanilla;
    private BigDecimal nCodigoCausalReliquidacion;
    private BigDecimal nCodigoTipoEgreso;
    private BigDecimal nTipoMovimientoID;
    private String vcUsuarioCreacion;
    private Date dFechaCreacion;
    private Date dFechaActualizacion;
    private String vcUsuarioActualizacion;
}