package cl.gob.ips.proceso_pago.dao.daoImpl;

import cl.gob.ips.proceso_pago.dao.CtaCtePAFDAO;
import cl.gob.ips.proceso_pago.dto.CtaCteDTO;
import cl.gob.ips.proceso_pago.dto.SpResponse;
import cl.gob.ips.proceso_pago.mapper.CtaCtePAFMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CtaCtePAFDAOImpl implements CtaCtePAFDAO {

    private static final Logger log = LoggerFactory.getLogger(CtaCtePAFDAOImpl.class);
    private final JdbcTemplate jdbcTemplate;
    private final JdbcTemplate ctaCteJdbc;

    @Autowired
    public CtaCtePAFDAOImpl(@Qualifier("pafJdbc") JdbcTemplate jdbcTemplate,
                            @Qualifier("ctaCteJdbc") JdbcTemplate ctaCteJdbc) {
        this.jdbcTemplate = jdbcTemplate;
        this.ctaCteJdbc = ctaCteJdbc;
    }

    public List<CtaCteDTO> obtenerDatosCtaCtePAF(int IdProceso) {
        log.info("********************* obtenerDatosCtaCtePAF *********************");
        String sql = "SELECT * FROM paf.fn_ObtenerDatosCCPAF(?)";
        return jdbcTemplate.query(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, IdProceso);
            return ps;
        }, new CtaCtePAFMapper());
    }

    public SpResponse insertTBLCTACTEPAF(List<CtaCteDTO> dtoPAFList) {
        log.info("********************* insertTBLCTACTEPAF *********************");
        SpResponse response = new SpResponse();
        for (CtaCteDTO dtoPAF : dtoPAFList) {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("SP_InsertarTBLCTACTEPAF")
                    .withSchemaName("paf")
                    .declareParameters(
                            new SqlParameter("p_nMesRemuneracion", Types.DECIMAL),
                            new SqlParameter("p_nRUTCausante", Types.DECIMAL),
                            new SqlParameter("p_cDVCausante", Types.CHAR),
                            new SqlParameter("p_nRUTBeneficiario", Types.DECIMAL),
                            new SqlParameter("p_nDVRUTBeneficiario", Types.CHAR),
                            new SqlParameter("p_nIDBeneficio", Types.DECIMAL),
                            new SqlParameter("p_nMontoBeneficio", Types.DECIMAL),
                            new SqlParameter("p_nRUTEmpleador", Types.DECIMAL),
                            new SqlParameter("p_nDVRUTEmpleador", Types.CHAR),
                            new SqlParameter("p_nIDTipoBeneficio", Types.DECIMAL),
                            new SqlParameter("p_nIDTipoBeneficiario", Types.DECIMAL),
                            new SqlParameter("p_nTipoCausanteID", Types.DECIMAL),
                            new SqlParameter("p_nNumDiasAsigFam", Types.DECIMAL),
                            new SqlParameter("p_nCodTramo", Types.DECIMAL),
                            new SqlParameter("p_nMontoDocumento", Types.DECIMAL),
                            new SqlParameter("p_vcNumeroDocumento", Types.VARCHAR),
                            new SqlParameter("p_dFechaEmisionDocumento", Types.DATE),
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
                            new SqlParameter("p_nIDProceso", Types.INTEGER),
                            new SqlOutParameter("p_nResultado", Types.INTEGER),
                            new SqlOutParameter("p_vcMensaje", Types.VARCHAR)
                    );

            Map<String, Object> params = new HashMap<>();
            params.put("p_nMesRemuneracion", dtoPAF.getNMesRemuneracion());
            params.put("p_nRUTCausante", dtoPAF.getNRUTCausante());
            params.put("p_cDVCausante", dtoPAF.getCdvCausante());
            params.put("p_nRUTBeneficiario", dtoPAF.getNRUTBeneficiario());
            params.put("p_nDVRUTBeneficiario", dtoPAF.getNdvRUTBeneficiario());
            params.put("p_nIDBeneficio", dtoPAF.getNIDBeneficio());
            params.put("p_nMontoBeneficio", dtoPAF.getNMontoBeneficio());
            params.put("p_nRUTEmpleador", dtoPAF.getNRUTEmpleador());
            params.put("p_nDVRUTEmpleador", dtoPAF.getNdvRUTEmpleador());
            params.put("p_nIDTipoBeneficio", dtoPAF.getNIDTipoBeneficio());
            params.put("p_nIDTipoBeneficiario", dtoPAF.getNIDTipoBeneficiario());
            params.put("p_nTipoCausanteID", dtoPAF.getNTipoCausanteID());
            params.put("p_nNumDiasAsigFam", dtoPAF.getNNumDiasAsigFam());
            params.put("p_nCodTramo", dtoPAF.getNCodTramo());
            params.put("p_nMontoDocumento", dtoPAF.getNMontoDocumento());
            params.put("p_vcNumeroDocumento", dtoPAF.getVcNumeroDocumento());
            params.put("p_dFechaEmisionDocumento", dtoPAF.getDFechaEmisionDocumento());
            params.put("p_nCodigoBanco", dtoPAF.getNCodigoBanco());
            params.put("p_nCodigoInstitucion", dtoPAF.getNCodigoInstitucion());
            params.put("p_vcOrigen", dtoPAF.getVcOrigen());
            params.put("p_nTipoEmision", dtoPAF.getNTipoEmision());
            params.put("p_nTipoDeclaracion", dtoPAF.getNTipoDeclaracion());
            params.put("p_vcNumeroSerie", dtoPAF.getVcNumeroSerie());
            params.put("p_vcNumeroPlanilla", dtoPAF.getVcNumeroPlanilla());
            params.put("p_nCodigoCausalReliquidacion", dtoPAF.getNCodigoCausalReliquidacion());
            params.put("p_nCodigoTipoEgreso", dtoPAF.getNCodigoTipoEgreso());
            params.put("p_nTipoMovimientoID", dtoPAF.getNTipoMovimientoID());
            params.put("p_vcUsuarioCreacion", dtoPAF.getVcUsuarioCreacion());
            params.put("p_dFechaCreacion", dtoPAF.getDFechaCreacion());
            params.put("p_dFechaActualizacion", dtoPAF.getDFechaActualizacion());
            params.put("p_vcUsuarioActualizacion", dtoPAF.getVcUsuarioActualizacion());
            params.put("p_nIDProceso", dtoPAF.getNidProceso());

            try {
                Map<String, Object> result = jdbcCall.execute(params);
                response.setResultado((Integer) result.get("p_nResultado"));
                response.setMensaje((String) result.get("p_vcMensaje"));
            } catch (DataAccessException e) {
                response.setResultado(0);
                response.setMensaje(e.getMostSpecificCause().getMessage());
                break;
            }
        }
        return response;
    }

    public List<CtaCteDTO> selectTBLCTACTEPAF(int nidProceso) {
        log.info("********************* obtenerDatosTBLCTACTEPAF *********************");
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("SP_ObtenerDatosTBLCTACTEPAF")
                .withSchemaName("paf")
                .returningResultSet("result", new CtaCtePAFMapper());

        Map<String, Object> params = new HashMap<>();
        params.put("p_nIDProceso", nidProceso);

        Map<String, Object> result = jdbcCall.execute(params);
        return (List<CtaCteDTO>) result.get("result");
    }

    //ADM_CTACTE
    public SpResponse insertarTBLCTACTEPAF(List<CtaCteDTO> dtoPAFList) {
        log.info("********************* insertarDatosTBLCTACTEPAF *********************");
        SpResponse response = new SpResponse();
        for (CtaCteDTO dtoPAF : dtoPAFList) {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(ctaCteJdbc)
                    .withProcedureName("SP_INSERTTBLCTACTEPAF")
//                    .withSchemaName("ADM_CTACTE")
                    .declareParameters(
                            new SqlParameter("p_NMESREMUNERACION", Types.NUMERIC),
                            new SqlParameter("p_NRUTCAUSANTE", Types.NUMERIC),
                            new SqlParameter("p_CDVCAUSANTE", Types.VARCHAR),
                            new SqlParameter("p_NRUTBENEFICIARIO", Types.NUMERIC),
                            new SqlParameter("p_NDVRUTBENEFICIARIO", Types.VARCHAR),
                            new SqlParameter("p_NIDBENEFICIO", Types.NUMERIC),
                            new SqlParameter("p_NMONTOBENEFICIO", Types.NUMERIC),
                            new SqlParameter("p_NRUTEMPLEADOR", Types.NUMERIC),
                            new SqlParameter("p_NDVRUTEMPLEADOR", Types.VARCHAR),
                            new SqlParameter("p_NIDTIPOBENEFICIO", Types.NUMERIC),
                            new SqlParameter("p_NIDTIPOBENEFICIARIO", Types.NUMERIC),
                            new SqlParameter("p_NTIPOCAUSANTEID", Types.NUMERIC),
                            new SqlParameter("p_NNUMDIASASIGFAM", Types.NUMERIC),
                            new SqlParameter("p_NCODTRAMO", Types.NUMERIC),
                            new SqlParameter("p_NMONTODOCUMENTO", Types.NUMERIC),
                            new SqlParameter("p_VCNUMERODOCUMENTO", Types.VARCHAR),
                            new SqlParameter("p_DFECHAEMISIONDOCUMENTO", Types.DATE),
                            new SqlParameter("p_NCODIGOBANCO", Types.NUMERIC),
                            new SqlParameter("p_NCODIGOINSTITUCION", Types.NUMERIC),
                            new SqlParameter("p_VCORIGEN", Types.VARCHAR),
                            new SqlParameter("p_NTIPOEMISION", Types.NUMERIC),
                            new SqlParameter("p_NTIPODECLARACION", Types.NUMERIC),
                            new SqlParameter("p_VCNUMEROSERIE", Types.VARCHAR),
                            new SqlParameter("p_VCNUMEROPLANILLA", Types.VARCHAR),
                            new SqlParameter("p_NCODIGOCAUSALRELIQUIDACION", Types.NUMERIC),
                            new SqlParameter("p_NCODIGOTIPOEGRESO", Types.NUMERIC),
                            new SqlParameter("p_NTIPOMOVIMIENTOID", Types.NUMERIC),
                            new SqlParameter("p_VCUSUARIOCREACION", Types.VARCHAR),
                            new SqlParameter("p_DFECHACREACION", Types.DATE),
                            new SqlParameter("p_DFECHAACTUALIZACION", Types.DATE),
                            new SqlParameter("p_VCUSUARIOACTUALIZACION", Types.VARCHAR),
                            new SqlOutParameter("p_nResultado", Types.NUMERIC),
                            new SqlOutParameter("p_vcMensaje", Types.VARCHAR)
                    );

            Map<String, Object> params = new HashMap<>();
            params.put("p_NMESREMUNERACION", dtoPAF.getNMesRemuneracion());
            params.put("p_NRUTCAUSANTE", dtoPAF.getNRUTCausante());
            params.put("p_CDVCAUSANTE", dtoPAF.getCdvCausante());
            params.put("p_NRUTBENEFICIARIO", dtoPAF.getNRUTBeneficiario());
            params.put("p_NDVRUTBENEFICIARIO", dtoPAF.getNdvRUTBeneficiario());
            params.put("p_NIDBENEFICIO", dtoPAF.getNIDBeneficio());
            params.put("p_NMONTOBENEFICIO", dtoPAF.getNMontoBeneficio());
            params.put("p_NRUTEMPLEADOR", dtoPAF.getNRUTEmpleador());
            params.put("p_NDVRUTEMPLEADOR", dtoPAF.getNdvRUTEmpleador());
            params.put("p_NIDTIPOBENEFICIO", dtoPAF.getNIDTipoBeneficio());
            params.put("p_NIDTIPOBENEFICIARIO", dtoPAF.getNIDTipoBeneficiario());
            params.put("p_NTIPOCAUSANTEID", dtoPAF.getNTipoCausanteID());
            params.put("p_NNUMDIASASIGFAM", dtoPAF.getNNumDiasAsigFam());
            params.put("p_NCODTRAMO", dtoPAF.getNCodTramo());
            params.put("p_NMONTODOCUMENTO", dtoPAF.getNMontoDocumento());
            params.put("p_VCNUMERODOCUMENTO", dtoPAF.getVcNumeroDocumento());
            params.put("p_DFECHAEMISIONDOCUMENTO", dtoPAF.getDFechaEmisionDocumento());
            params.put("p_NCODIGOBANCO", dtoPAF.getNCodigoBanco());
            params.put("p_NCODIGOINSTITUCION", dtoPAF.getNCodigoInstitucion());
            params.put("p_VCORIGEN", dtoPAF.getVcOrigen());
            params.put("p_NTIPOEMISION", dtoPAF.getNTipoEmision());
            params.put("p_NTIPODECLARACION", dtoPAF.getNTipoDeclaracion());
            params.put("p_VCNUMEROSERIE", dtoPAF.getVcNumeroSerie());
            params.put("p_VCNUMEROPLANILLA", dtoPAF.getVcNumeroPlanilla());
            params.put("p_NCODIGOCAUSALRELIQUIDACION", dtoPAF.getNCodigoCausalReliquidacion());
            params.put("p_NCODIGOTIPOEGRESO", dtoPAF.getNCodigoTipoEgreso());
            params.put("p_NTIPOMOVIMIENTOID", dtoPAF.getNTipoMovimientoID());
            params.put("p_VCUSUARIOCREACION", dtoPAF.getVcUsuarioCreacion());
            params.put("p_DFECHACREACION", dtoPAF.getDFechaCreacion());
            params.put("p_DFECHAACTUALIZACION", dtoPAF.getDFechaActualizacion());
            params.put("p_VCUSUARIOACTUALIZACION", dtoPAF.getVcUsuarioActualizacion());

            try {
                Map<String, Object> result = jdbcCall.execute(params);
                response.setResultado((Integer) result.get("p_nResultado"));
                response.setMensaje((String) result.get("p_vcMensaje"));
            } catch (DataAccessException e) {
                response.setResultado(0);
                response.setMensaje(e.getMostSpecificCause().getMessage());
                break;
            }
            actualizarEstadoTBLCTACTEPAF(dtoPAF.getNidProceso());
        }
        return response;
    }

    public SpResponse eliminarRegistrosTBLCTACTEPAF(int nidProceso) {
        log.info("********************* eliminarRegistrosTBLCTACTEPAF *********************");
        SpResponse response = new SpResponse();
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("SP_DeleteTBLCTACTEPAF")
                .withSchemaName("paf")
                .declareParameters(
                        new SqlParameter("p_nIDProceso", Types.INTEGER),
                        new SqlParameter("p_vcEstado", Types.VARCHAR),
                        new SqlOutParameter("p_nResultado", Types.INTEGER),
                        new SqlOutParameter("p_vcMensaje", Types.VARCHAR)
                );

        Map<String, Object> params = new HashMap<>();
        params.put("p_nIDProceso", nidProceso);
        params.put("p_vcEstado", "COPIADO");

        try {
            Map<String, Object> result = jdbcCall.execute(params);
            response.setResultado((Integer) result.get("p_nResultado"));
            response.setMensaje((String) result.get("p_vcMensaje"));
        } catch (DataAccessException e) {
            response.setResultado(0);
            response.setMensaje(e.getMostSpecificCause().getMessage());
        }
        return response;
    }

    public SpResponse actualizarEstadoTBLCTACTEPAF(int nidProceso) {
        log.info("********************* actualizarEstadoTBLCTACTEPAF *********************");
        SpResponse response = new SpResponse();
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("SP_UpdateEstadoTBLCTACTEPAF")
                .withSchemaName("paf")
                .declareParameters(
                        new SqlParameter("p_nIDProceso", Types.INTEGER),
                        new SqlOutParameter("p_nResultado", Types.INTEGER),
                        new SqlOutParameter("p_vcMensaje", Types.VARCHAR)
                );

        Map<String, Object> params = new HashMap<>();
        params.put("p_nIDProceso", nidProceso);

        try {
            Map<String, Object> result = jdbcCall.execute(params);
            response.setResultado((Integer) result.get("p_nResultado"));
            response.setMensaje((String) result.get("p_vcMensaje"));
        } catch (DataAccessException e) {
            response.setResultado(0);
            response.setMensaje(e.getMostSpecificCause().getMessage());
        }
        return response;
    }
}