package cl.gob.ips.proceso_pago.dao.daoImpl;

import cl.gob.ips.proceso_pago.dao.CtaCtePAFDAO;
import cl.gob.ips.proceso_pago.dto.CtaCteDTO;
import cl.gob.ips.proceso_pago.dto.SpResponse;
import cl.gob.ips.proceso_pago.mapper.CtaCtePAFMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CtaCtePAFDAOImpl implements CtaCtePAFDAO {

    @Value("${spring.datasource.schema}")
    private String schema;

    private static final Logger log = LoggerFactory.getLogger(CtaCtePAFDAOImpl.class);
    private final JdbcTemplate jdbcTemplate;
    private final JdbcTemplate ctaCteJdbc;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    public CtaCtePAFDAOImpl(@Qualifier("pafJdbc") JdbcTemplate jdbcTemplate,
                            @Qualifier("ctaCteJdbc") JdbcTemplate ctaCteJdbc) {
        this.jdbcTemplate = jdbcTemplate;
        this.ctaCteJdbc = ctaCteJdbc;
    }

    public List<CtaCteDTO> selectTBLCTACTEPAF(int nidProceso) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("SP_ObtenerDatosTBLCTACTEPAF")
                .withSchemaName(schema)
                .returningResultSet("result", new CtaCtePAFMapper());

        Map<String, Object> params = new HashMap<>();
        params.put("p_nIDProceso", nidProceso);

        Map<String, Object> result = jdbcCall.execute(params);
        return (List<CtaCteDTO>) result.get("result");
    }

    //ADM_CTACTE
    public SpResponse insertarTBLCTACTEPAF(List<CtaCteDTO> dtoPAFList) {
        SpResponse response = new SpResponse();

        // Verificar si hay datos para procesar
        if (dtoPAFList == null || dtoPAFList.isEmpty()) {
            response.setResultado(0);
            response.setMensaje("No hay datos que procesar");
            return response;
        }

        // Utilizar TransactionTemplate para manejar la transacción
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

        try {
            return transactionTemplate.execute(status -> {
                // Preparar el procedimiento almacenado una sola vez
                SimpleJdbcCall jdbcCall = new SimpleJdbcCall(ctaCteJdbc)
                        .withProcedureName("SP_INSERTTBLCTACTEPAF")
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

                int totalRegistros = 0;
                int procesados = 0;
                String mensajeError = null;

                // Procesar cada registro dentro de la misma transacción
                for (CtaCteDTO dtoPAF : dtoPAFList) {
                    totalRegistros++;
                    Map<String, Object> params = new HashMap<>();
                    params.put("p_NPAGOPAFID", null);
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
                        int resultadoSp = ((BigDecimal) result.get("p_nResultado")).intValue();

                        if (resultadoSp != 1) {
                            mensajeError = (String) result.get("p_vcMensaje");
                            // Marcar la transacción para rollback
                            status.setRollbackOnly();
                            break;
                        }

                        procesados++;
                    } catch (DataAccessException e) {
                        log.error("Error al insertar en TBLCTACTEPAF: {}", e.getMostSpecificCause().getMessage());
                        mensajeError = e.getMostSpecificCause().getMessage();
                        // Marcar la transacción para rollback
                        status.setRollbackOnly();
                        break;
                    }
                }

                // Preparar respuesta final
                SpResponse resultado = new SpResponse();
                if (status.isRollbackOnly() || procesados != totalRegistros) {
                    resultado.setResultado(0);
                    resultado.setMensaje(mensajeError != null ? mensajeError :
                            "No se procesaron todos los registros. Procesados: " + procesados +
                                    " de " + totalRegistros);
                } else {
                    resultado.setResultado(1);
                    resultado.setMensaje("Se procesaron exitosamente " + procesados + " registros");
                }

                return resultado;
            });
        } catch (TransactionException e) {
            log.error("Error en la transacción: {}", e.getMessage());
            response.setResultado(0);
            response.setMensaje("Error en la transacción: " + e.getMessage());
            return response;
        }
    }

    public SpResponse eliminarRegistrosTBLCTACTEPAF(int nidProceso) {
        SpResponse response = new SpResponse();
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("SP_DeleteTBLCTACTEPAF")
                .withSchemaName(schema)
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
        SpResponse response = new SpResponse();
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("SP_UpdateEstadoTBLCTACTEPAF")
                .withSchemaName(schema)
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

    public SpResponse copyDataToTBLCTACTEPAF(int iIdProcesoPago) {
        SpResponse response = new SpResponse();
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("SP_CopyDataToTBLCTACTEPAF")
                .withSchemaName(schema)
                .declareParameters(
                        new SqlParameter("iIdProcesoPago", Types.INTEGER),
                        new SqlOutParameter("NoDataCopied", Types.BIT)
                );

        Map<String, Object> params = new HashMap<>();
        params.put("iIdProcesoPago", iIdProcesoPago);

        try {
            Map<String, Object> result = jdbcCall.execute(params);
            boolean noDataCopied = (Boolean) result.get("NoDataCopied");

            if (noDataCopied) {
                response.setResultado(0);
                response.setMensaje("No data to copy.");
            } else {
                response.setResultado(1);
                response.setMensaje("Data copied successfully.");
            }
        } catch (DataAccessException e) {
            response.setResultado(0);
            response.setMensaje(e.getMostSpecificCause().getMessage());
        }
        return response;
    }
}