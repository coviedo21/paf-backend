package cl.gob.ips.proceso_pago.mapper;

import cl.gob.ips.proceso_pago.dto.CtaCtePAFDTO;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CtaCtePAFMapper implements RowMapper<CtaCtePAFDTO> {

    @Override
    public CtaCtePAFDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        CtaCtePAFDTO dto = new CtaCtePAFDTO();

        // Mapeo explícito con manejo de NULLs y nombres exactos de columnas
        dto.setNMesRemuneracion(getBigDecimalOrNull(rs, "NMESREMUNERACION"));
        dto.setNRUTCausante(getBigDecimalOrNull(rs, "NRUTCAUSANTE"));
        dto.setCdvCausante(rs.getString("CDVCAUSANTE"));
        dto.setNRUTBeneficiario(getBigDecimalOrNull(rs, "NRUTBENEFICIARIO"));
        dto.setNdvRUTBeneficiario(rs.getString("NDVRUTBENEFICIARIO"));
        dto.setNRUTEmpleador(getBigDecimalOrNull(rs, "NRUTEMPLEADOR"));
        dto.setNdvRUTEmpleador(rs.getString("NDVRUTEMPLEADOR"));
        dto.setNIDBeneficio(getBigDecimalOrNull(rs, "NIDBENEFICIO"));
        dto.setNIDTipoBeneficio(getBigDecimalOrNull(rs, "NIDTIPOBENEFICIO"));
        dto.setNIDTipoBeneficiario(getBigDecimalOrNull(rs, "NIDTIPOBENEFICIARIO"));
        dto.setNTipoCausanteID(getBigDecimalOrNull(rs, "NTIPOCAUSANTEID"));
        dto.setNNumDiasAsigFam(getBigDecimalOrNull(rs, "NNUMDIASASIGFAM"));
        dto.setNCodTramo(getBigDecimalOrNull(rs, "NCODTRAMO"));
        dto.setNMontoBeneficio(getBigDecimalOrNull(rs, "NMONTOBENEFICIO"));
        dto.setNMontoDocumento(getBigDecimalOrNull(rs, "NMONTODOCUMENTO"));
        dto.setVcNumeroDocumento(rs.getString("VCNUMERODOCUMENTO"));
        dto.setDFechaEmisionDocumento(rs.getDate("DFECHAEMISIONDOCUMENTO"));
        dto.setNCodigoBanco(getBigDecimalOrNull(rs, "NCODIGOBANCO"));
        dto.setNCodigoInstitucion(getBigDecimalOrNull(rs, "NCODIGOINSTITUCION"));
        dto.setVcOrigen(rs.getString("VCORIGEN"));
        dto.setNTipoEmision(getBigDecimalOrNull(rs, "NTIPOEMISION"));
        dto.setNTipoDeclaracion(getBigDecimalOrNull(rs, "NTIPODECLARACION"));
        dto.setVcNumeroSerie(rs.getString("VCNUMEROSERIE"));
        dto.setVcNumeroPlanilla(rs.getString("VCNUMEROPLANILLA"));
        dto.setNCodigoCausalReliquidacion(getBigDecimalOrNull(rs, "NCODIGOCAUSALRELIQUIDACION"));
        dto.setNCodigoTipoEgreso(getBigDecimalOrNull(rs, "NCODIGOTIPOEGRESO"));
        dto.setNTipoMovimientoID(getBigDecimalOrNull(rs, "NTIPOMOVIMIENTOID"));
        dto.setVcUsuarioCreacion(rs.getString("VCUSUARIOCREACION"));
        dto.setDFechaCreacion(rs.getDate("DFECHACREACION"));
        dto.setDFechaActualizacion(rs.getDate("DFECHAACTUALIZACION"));
        dto.setVcUsuarioActualizacion(rs.getString("VCUSUARIOACTUALIZACION"));

        return dto;
    }

    // Método auxiliar para manejar NULLs en BigDecimal
    private BigDecimal getBigDecimalOrNull(ResultSet rs, String column) throws SQLException {
        BigDecimal value = rs.getBigDecimal(column);
        return rs.wasNull() ? null : value;
    }
}