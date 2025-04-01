package cl.gob.ips.proceso_pago.dao.daoImpl;

import cl.gob.ips.proceso_pago.dao.CtaCtePAFDAO;
import cl.gob.ips.proceso_pago.dto.CtaCtePAFDTO;
import cl.gob.ips.proceso_pago.dto.SpResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CtaCtePAFDAOImpl implements CtaCtePAFDAO {

    private final JdbcTemplate jdbcTemplate;

    public CtaCtePAFDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<CtaCtePAFDTO> ROW_MAPPER = (rs, rowNum) -> {
        CtaCtePAFDTO dto = new CtaCtePAFDTO();
        dto.setNPagoPAFID(rs.getBigDecimal("NPAGOPAFID"));
        dto.setNMesRemuneracion(rs.getBigDecimal("NMESREMUNERACION"));
        dto.setNRUTCausante(rs.getBigDecimal("NRUTCAUSANTE"));
        dto.setCdvCausante(rs.getString("CDVCAUSANTE"));
        dto.setNRUTBeneficiario(rs.getBigDecimal("NRUTBENEFICIARIO"));
        dto.setNdvRUTBeneficiario(rs.getString("NDVRUTBENEFICIARIO"));
        dto.setNRUTEmpleador(rs.getBigDecimal("NRUTEMPLEADOR"));
        dto.setNdvRUTEmpleador(rs.getString("NDVRUTEMPLEADOR"));
        dto.setNIDBeneficio(rs.getBigDecimal("NIDBENEFICIO"));
        dto.setNIDTipoBeneficio(rs.getBigDecimal("NIDTIPOBENEFICIO"));
        dto.setNIDTipoBeneficiario(rs.getBigDecimal("NIDTIPOBENEFICIARIO"));
        dto.setNTipoCausanteID(rs.getBigDecimal("NTIPOCAUSANTEID"));
        dto.setNNumDiasAsigFam(rs.getBigDecimal("NNUMDIASASIGFAM"));
        dto.setNCodTramo(rs.getBigDecimal("NCODTRAMO"));
        dto.setNMontoBeneficio(rs.getBigDecimal("NMONTOBENEFICIO"));
        dto.setNMontoDocumento(rs.getBigDecimal("NMONTODOCUMENTO"));
        dto.setVcNumeroDocumento(rs.getString("VCNUMERODOCUMENTO"));
        dto.setNFechaEmisionDocumento(rs.getBigDecimal("NFECHAEMISIONDOCUMENTO"));
        dto.setNCodigoBanco(rs.getBigDecimal("NCODIGOBANCO"));
        dto.setNCodigoInstitucion(rs.getBigDecimal("NCODIGOINSTITUCION"));
        dto.setVcOrigen(rs.getString("VCORIGEN"));
        dto.setNTipoEmision(rs.getBigDecimal("NTIPOEMISION"));
        dto.setNTipoDeclaracion(rs.getBigDecimal("NTIPODECLARACION"));
        dto.setVcNumeroSerie(rs.getString("VCNUMEROSERIE"));
        dto.setVcNumeroPlanilla(rs.getString("VCNUMEROPLANILLA"));
        dto.setNCodigoCausalReliquidacion(rs.getBigDecimal("NCODIGOCAUSALRELIQUIDACION"));
        dto.setNCodigoTipoEgreso(rs.getBigDecimal("NCODIGOTIPOEGRESO"));
        dto.setNTipoMovimientoID(rs.getBigDecimal("NTIPOMOVIMIENTOID"));
        dto.setVcUsuarioCreacion(rs.getString("VCUSUARIOCREACION"));
        dto.setDFechaCreacion(rs.getDate("DFECHACREACION"));
        dto.setDFechaActualizacion(rs.getDate("DFECHAACTUALIZACION"));
        dto.setVcUsuarioActualizacion(rs.getString("VCUSUARIOACTUALIZACION"));
        return dto;
    };

    public List<CtaCtePAFDTO> obtenerDatosDesdeFuncion(int iIdProcesoPago) {
        String sql = "SELECT * FROM paf.fn_ObtenerDatosCCPAF(?)";
        return jdbcTemplate.query(sql, ROW_MAPPER, iIdProcesoPago);
    }

    private SpResponse insertarConSP(CtaCtePAFDTO datos) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("SP_InsertarCtaCtePAF")
                .declareParameters(
                        // Parámetros obligatorios
                        new SqlParameter("p_nPagoPAFID", Types.DECIMAL),
                        new SqlParameter("p_nMesRemuneracion", Types.DECIMAL),
                        new SqlParameter("p_nRUTCausante", Types.DECIMAL),
                        new SqlParameter("p_cDVCausante", Types.CHAR),
                        new SqlParameter("p_nRUTBeneficiario", Types.DECIMAL),
                        new SqlParameter("p_nDVRUTBeneficiario", Types.CHAR),
                        new SqlParameter("p_nIDBeneficio", Types.DECIMAL),
                        new SqlParameter("p_nMontoBeneficio", Types.DECIMAL),
                        // Parámetros opcionales
                        new SqlParameter("p_nRUTEmpleador", Types.DECIMAL),
                        new SqlParameter("p_nDVRUTEmpleador", Types.CHAR),
                        new SqlParameter("p_nIDTipoBeneficio", Types.DECIMAL),
                        new SqlParameter("p_nIDTipoBeneficiario", Types.DECIMAL),
                        new SqlParameter("p_nTipoCausanteID", Types.DECIMAL),
                        new SqlParameter("p_nNumDiasAsigFam", Types.DECIMAL),
                        new SqlParameter("p_nCodTramo", Types.DECIMAL),
                        new SqlParameter("p_nMontoDocumento", Types.DECIMAL),
                        new SqlParameter("p_vcNumeroDocumento", Types.VARCHAR),
                        new SqlParameter("p_nFechaEmisionDocumento", Types.DECIMAL),
                        new SqlParameter("p_nCodigoBanco", Types.DECIMAL),
                        new SqlParameter("p_nCodigoInstitucion", Types.DECIMAL),
                        new SqlParameter("p_vcOrigen", Types.CHAR),
                        new SqlParameter("p_nTipoEmision", Types.DECIMAL),
                        new SqlParameter("p_nTipoDeclaracion", Types.DECIMAL),
                        new SqlParameter("p_vcNumeroSerie", Types.DECIMAL),
                        new SqlParameter("p_vcNumeroPlanilla", Types.DECIMAL),
                        new SqlParameter("p_nCodigoCausalReliquidacion", Types.DECIMAL),
                        new SqlParameter("p_nCodigoTipoEgreso", Types.DECIMAL),
                        new SqlParameter("p_nTipoMovimientoID", Types.DECIMAL),
                        new SqlParameter("p_vcUsuarioCreacion", Types.VARCHAR),
                        new SqlParameter("p_dFechaCreacion", Types.DATE),
                        new SqlParameter("p_dFechaActualizacion", Types.DATE),
                        new SqlParameter("p_vcUsuarioActualizacion", Types.VARCHAR),
                        // Parámetros de salida
                        new SqlOutParameter("p_nResultado", Types.INTEGER),
                        new SqlOutParameter("p_vcMensaje", Types.VARCHAR)
                );

        Map<String, Object> params = new HashMap<>();
        params.put("p_nPagoPAFID", datos.getNPagoPAFID());
        params.put("p_nMesRemuneracion", datos.getNMesRemuneracion());
        params.put("p_nRUTCausante", datos.getNRUTCausante());
        params.put("p_cDVCausante", datos.getCdvCausante());
        params.put("p_nRUTBeneficiario", datos.getNRUTBeneficiario());
        params.put("p_nDVRUTBeneficiario", datos.getNdvRUTBeneficiario());
        params.put("p_nIDBeneficio", datos.getNIDBeneficio());
        params.put("p_nMontoBeneficio", datos.getNMontoBeneficio());
        // Parámetros opcionales
        params.put("p_nRUTEmpleador", datos.getNRUTEmpleador());
        params.put("p_nDVRUTEmpleador", datos.getNdvRUTEmpleador());
        params.put("p_nIDTipoBeneficio", datos.getNIDTipoBeneficio());
        params.put("p_nIDTipoBeneficiario", datos.getNIDTipoBeneficiario());
        params.put("p_nTipoCausanteID", datos.getNTipoCausanteID());
        params.put("p_nNumDiasAsigFam", datos.getNNumDiasAsigFam());
        params.put("p_nCodTramo", datos.getNCodTramo());
        params.put("p_nMontoDocumento", datos.getNMontoDocumento());
        params.put("p_vcNumeroDocumento", datos.getVcNumeroDocumento());
        params.put("p_nFechaEmisionDocumento", datos.getNFechaEmisionDocumento());
        params.put("p_nCodigoBanco", datos.getNCodigoBanco());
        params.put("p_nCodigoInstitucion", datos.getNCodigoInstitucion());
        params.put("p_vcOrigen", datos.getVcOrigen());
        params.put("p_nTipoEmision", datos.getNTipoEmision());
        params.put("p_nTipoDeclaracion", datos.getNTipoDeclaracion());
        params.put("p_vcNumeroSerie", datos.getVcNumeroSerie());
        params.put("p_vcNumeroPlanilla", datos.getVcNumeroPlanilla());
        params.put("p_nCodigoCausalReliquidacion", datos.getNCodigoCausalReliquidacion());
        params.put("p_nCodigoTipoEgreso", datos.getNCodigoTipoEgreso());
        params.put("p_nTipoMovimientoID", datos.getNTipoMovimientoID());
        params.put("p_vcUsuarioCreacion", datos.getVcUsuarioCreacion());
        params.put("p_dFechaCreacion", datos.getDFechaCreacion());
        params.put("p_dFechaActualizacion", datos.getDFechaActualizacion());
        params.put("p_vcUsuarioActualizacion", datos.getVcUsuarioActualizacion());

        try {
            Map<String, Object> out = jdbcCall.execute(params);
            return new SpResponse(
                    (int) out.get("p_nResultado"),
                    (String) out.get("p_vcMensaje")
            );
        } catch (DataAccessException e) {
            return new SpResponse(0, "Error al ejecutar SP: " + e.getMostSpecificCause().getMessage());
        }
    }

    public SpResponse procesarYInsertar(int iIdProcesoPago) { // --> PAF
        List<CtaCtePAFDTO> datos = obtenerDatosDesdeFuncion(iIdProcesoPago);
        if (datos.isEmpty()) {
            return new SpResponse(0, "No hay datos para insertar");
        }
        return insertarConSP(datos.get(0));
    }

    public SpResponse insertarDesdeVistaConSP() { // --> CC
        List<CtaCtePAFDTO> datosVista = jdbcTemplate.query(
                "SELECT * FROM paf.VW_CTACTEPAF", ROW_MAPPER);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("SP_INSERTAR_TBLCTACTEPAF")
                .declareParameters(
                        // Parámetros Obligatorios (NOT NULL)
                        new SqlParameter("p_nPagoPAFID", Types.DECIMAL),
                        new SqlParameter("p_nMesRemuneracion", Types.DECIMAL),
                        new SqlParameter("p_nRUTCausante", Types.DECIMAL),
                        new SqlParameter("p_cDVCausante", Types.CHAR),
                        new SqlParameter("p_nRUTBeneficiario", Types.DECIMAL),
                        new SqlParameter("p_nDVRUTBeneficiario", Types.CHAR),
                        new SqlParameter("p_nIDBeneficio", Types.DECIMAL),
                        new SqlParameter("p_nMontoBeneficio", Types.DECIMAL),
                        // Parámetros Opcionales (NULL)
                        new SqlParameter("p_nRUTEmpleador", Types.DECIMAL),
                        new SqlParameter("p_nDVRUTEmpleador", Types.CHAR),
                        new SqlParameter("p_nIDTipoBeneficio", Types.DECIMAL),
                        new SqlParameter("p_nIDTipoBeneficiario", Types.DECIMAL),
                        new SqlParameter("p_nTipoCausanteID", Types.DECIMAL),
                        new SqlParameter("p_nNumDiasAsigFam", Types.DECIMAL),
                        new SqlParameter("p_nCodTramo", Types.DECIMAL),
                        new SqlParameter("p_nMontoDocumento", Types.DECIMAL),
                        new SqlParameter("p_vcNumeroDocumento", Types.VARCHAR),
                        new SqlParameter("p_nFechaEmisionDocumento", Types.DECIMAL),
                        new SqlParameter("p_nCodigoBanco", Types.DECIMAL),
                        new SqlParameter("p_nCodigoInstitucion", Types.DECIMAL),
                        new SqlParameter("p_vcOrigen", Types.CHAR),
                        new SqlParameter("p_nTipoEmision", Types.DECIMAL),
                        new SqlParameter("p_nTipoDeclaracion", Types.DECIMAL),
                        new SqlParameter("p_vcNumeroSerie", Types.VARCHAR),
                        new SqlParameter("p_vcNumeroPlanilla", Types.VARCHAR),
                        new SqlParameter("p_nCodigoCausalReliquidacion", Types.DECIMAL),
                        new SqlParameter("p_nCodigoTipoEgreso", Types.DECIMAL),
                        new SqlParameter("p_nTipoMovimientoID", Types.DECIMAL),
                        new SqlParameter("p_vcUsuarioCreacion", Types.VARCHAR),
                        new SqlParameter("p_dFechaCreacion", Types.DATE),
                        new SqlParameter("p_dFechaActualizacion", Types.DATE),
                        new SqlParameter("p_vcUsuarioActualizacion", Types.VARCHAR),
                        // Parámetros de Salida
                        new SqlOutParameter("p_nResultado", Types.INTEGER),
                        new SqlOutParameter("p_vcMensaje", Types.VARCHAR)
                );

        SpResponse finalResponse = new SpResponse(1, "Todos los registros se insertaron correctamente");

        for (CtaCtePAFDTO dto : datosVista) {
            Map<String, Object> params = new HashMap<>();
            // Parámetros Obligatorios
            params.put("p_nPagoPAFID", dto.getNPagoPAFID());
            params.put("p_nMesRemuneracion", dto.getNMesRemuneracion());
            params.put("p_nRUTCausante", dto.getNRUTCausante());
            params.put("p_cDVCausante", dto.getCdvCausante());
            params.put("p_nRUTBeneficiario", dto.getNRUTBeneficiario());
            params.put("p_nDVRUTBeneficiario", dto.getNdvRUTBeneficiario());
            params.put("p_nIDBeneficio", dto.getNIDBeneficio());
            params.put("p_nMontoBeneficio", dto.getNMontoBeneficio());
            // Parámetros Opcionales
            params.put("p_nRUTEmpleador", dto.getNRUTEmpleador());
            params.put("p_nDVRUTEmpleador", dto.getNdvRUTEmpleador());
            params.put("p_nIDTipoBeneficio", dto.getNIDTipoBeneficio());
            params.put("p_nIDTipoBeneficiario", dto.getNIDTipoBeneficiario());
            params.put("p_nTipoCausanteID", dto.getNTipoCausanteID());
            params.put("p_nNumDiasAsigFam", dto.getNNumDiasAsigFam());
            params.put("p_nCodTramo", dto.getNCodTramo());
            params.put("p_nMontoDocumento", dto.getNMontoDocumento());
            params.put("p_vcNumeroDocumento", dto.getVcNumeroDocumento());
            params.put("p_nFechaEmisionDocumento", dto.getNFechaEmisionDocumento());
            params.put("p_nCodigoBanco", dto.getNCodigoBanco());
            params.put("p_nCodigoInstitucion", dto.getNCodigoInstitucion());
            params.put("p_vcOrigen", dto.getVcOrigen());
            params.put("p_nTipoEmision", dto.getNTipoEmision());
            params.put("p_nTipoDeclaracion", dto.getNTipoDeclaracion());
            params.put("p_vcNumeroSerie", dto.getVcNumeroSerie());
            params.put("p_vcNumeroPlanilla", dto.getVcNumeroPlanilla());
            params.put("p_nCodigoCausalReliquidacion", dto.getNCodigoCausalReliquidacion());
            params.put("p_nCodigoTipoEgreso", dto.getNCodigoTipoEgreso());
            params.put("p_nTipoMovimientoID", dto.getNTipoMovimientoID());
            params.put("p_vcUsuarioCreacion", dto.getVcUsuarioCreacion());
            params.put("p_dFechaCreacion", dto.getDFechaCreacion());
            params.put("p_dFechaActualizacion", dto.getDFechaActualizacion());
            params.put("p_vcUsuarioActualizacion", dto.getVcUsuarioActualizacion());

            try {
                Map<String, Object> result = jdbcCall.execute(params);
                int resultado = (int) result.get("p_nResultado");
                String mensaje = (String) result.get("p_vcMensaje");

                if (resultado == 0) {
                    finalResponse.setResultado(0);
                    finalResponse.setMensaje("Error en el registro ID " + dto.getNPagoPAFID() + ": " + mensaje);
                    return finalResponse; // Detiene el proceso si hay error
                }
            } catch (DataAccessException e) {
                finalResponse.setResultado(0);
                finalResponse.setMensaje("Error crítico al insertar registro ID " + dto.getNPagoPAFID() + ": " + e.getMostSpecificCause().getMessage());
                return finalResponse;
            }
        }
        return finalResponse;
    }
}