package cl.gob.ips.proceso_pago.dao.daoImpl;

import cl.gob.ips.proceso_pago.dao.CtaCtePAFDAO;
import cl.gob.ips.proceso_pago.dto.CtaCtePAFDTO;
import cl.gob.ips.proceso_pago.dto.SpResponse;
import cl.gob.ips.proceso_pago.mapper.CtaCtePAFMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final CtaCtePAFMapper rowMapper = new CtaCtePAFMapper();

    public CtaCtePAFDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CtaCtePAFDTO> obtenerDatosCtaCtePAF(int IdProceso) {
        log.info("********************* obtenerDatosCtaCtePAF *********************");
        String sql = "SELECT * FROM paf.fn_ObtenerDatosCCPAF(?)";
        return jdbcTemplate.query(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, IdProceso);
            return ps;
        }, new CtaCtePAFMapper());
    }

    public SpResponse insertarDatosCtaCtePAF(List<CtaCtePAFDTO> datosList) {
        log.info("********************* insertarDatosCtaCtePAF *********************");
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("SP_InsertarCtaCtePAF")
                .withSchemaName("paf")
                .declareParameters(
                        // Parámetros obligatorios
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
                        // Parámetros de salida
                        new SqlOutParameter("p_nResultado", Types.INTEGER),
                        new SqlOutParameter("p_vcMensaje", Types.VARCHAR)
                );

        SpResponse finalResponse = new SpResponse(1, "Todos los registros se insertaron correctamente");

        for (CtaCtePAFDTO dto : datosList) {
            Map<String, Object> params = new HashMap<>();
            // Parámetros obligatorios
            params.put("p_nMesRemuneracion", dto.getNMesRemuneracion());
//            params.put("p_nMesRemuneracion", 112023); // TODO: Solo para prueba, borrar despues
            params.put("p_nRUTCausante", dto.getNRUTCausante());
            params.put("p_cDVCausante", dto.getCdvCausante());
            params.put("p_nRUTBeneficiario", dto.getNRUTBeneficiario());
            params.put("p_nDVRUTBeneficiario", dto.getNdvRUTBeneficiario());
            params.put("p_nIDBeneficio", dto.getNIDBeneficio());
            params.put("p_nMontoBeneficio", dto.getNMontoBeneficio());
            // Parámetros opcionales
            params.put("p_nRUTEmpleador", dto.getNRUTEmpleador());
            params.put("p_nDVRUTEmpleador", dto.getNdvRUTEmpleador());
            params.put("p_nIDTipoBeneficio", dto.getNIDTipoBeneficio());
            params.put("p_nIDTipoBeneficiario", dto.getNIDTipoBeneficiario());
            params.put("p_nTipoCausanteID", dto.getNTipoCausanteID());
            params.put("p_nNumDiasAsigFam", dto.getNNumDiasAsigFam());
            params.put("p_nCodTramo", dto.getNCodTramo());
            params.put("p_nMontoDocumento", dto.getNMontoDocumento());
            params.put("p_vcNumeroDocumento", dto.getVcNumeroDocumento());
            params.put("p_dFechaEmisionDocumento", dto.getDFechaEmisionDocumento());
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
                    finalResponse.setMensaje(mensaje);
                    return finalResponse; // Detiene el proceso si hay error
                }
            } catch (DataAccessException e) {
                finalResponse.setResultado(0);
                finalResponse.setMensaje(e.getMostSpecificCause().getMessage());
                return finalResponse;
            }
        }
        return finalResponse;
    }
}