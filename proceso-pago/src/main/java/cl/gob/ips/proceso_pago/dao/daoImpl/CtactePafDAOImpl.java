package cl.gob.ips.proceso_pago.dao.daoImpl;

import cl.gob.ips.proceso_pago.dao.CtactePafDAO;
import cl.gob.ips.proceso_pago.dto.CtactePafDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CtactePafDAOImpl implements CtactePafDAO {

    private final JdbcTemplate jdbcTemplate;

    // @TODO: Implementar conexion a Oracle para insertarRegistro
    public CtactePafDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 1. Metodo para obtener datos desde la función
    @Override
    public List<CtactePafDTO> obtenerDatosDesdeFuncion(Integer idProcesoPago) {
        String sql = "SELECT * FROM paf.fn_ObtenerDatosCCPAF(?)";

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> mapRowToDto(rs),
                idProcesoPago
        );
    }

    private CtactePafDTO mapRowToDto(ResultSet rs) throws SQLException {
        CtactePafDTO dto = new CtactePafDTO();
        // Mapeo completo de todos los campos
        dto.setNPagoPAFID(rs.getInt("NPAGOPAFID"));  // Asumiendo que la función devuelve este campo
        dto.setNMesRemuneracion(rs.getInt("NMESREMUNERACION"));
        dto.setNRutCausante(rs.getInt("NRUTCAUSANTE"));
        dto.setCDvCausante(rs.getString("CDVCAUSANTE"));
        dto.setNRutBeneficiario(rs.getInt("NRUTBENEFICIARIO"));
        dto.setNDvRutBeneficiario(rs.getString("NDVRUTBENEFICIARIO"));
        dto.setNRutEmpleador(rs.getInt("NRUTEMPLEADOR"));
        dto.setNDvRutEmpleador(rs.getString("NDVRUTEMPLEADOR"));
        dto.setNIdBeneficio(rs.getInt("NIDBENEFICIO"));
        dto.setNIdTipoBeneficio(rs.getInt("NIDTIPOBENEFICIO"));
        dto.setNIdTipoBeneficiario(rs.getInt("NIDTIPOBENEFICIARIO"));
        dto.setNTipoCausanteID(rs.getInt("NTIPOCAUSANTEID"));
        dto.setNNumDiasAsigFam(rs.getInt("NNUMDIASASIGFAM"));
        dto.setNCodTramo(rs.getInt("NCODTRAMO"));
        dto.setNMontoBeneficio(rs.getBigDecimal("NMONTOBENEFICIO"));
        dto.setNMontoDocumento(rs.getBigDecimal("NMONTODOCUMENTO"));
        dto.setVcNumeroDocumento(rs.getString("VCNUMERODOCUMENTO"));
        dto.setNFechaEmisionDocumento(rs.getInt("NFECHAEMISIONDOCUMENTO"));
        dto.setNCodigoBanco(rs.getInt("NCODIGOBANCO"));
        dto.setNCodigoInstitucion(rs.getInt("NCODIGOINSTITUCION"));
        dto.setVcOrigen(rs.getString("VCORIGEN"));
        dto.setNTipoEmision(rs.getInt("NTIPOEMISION"));
        dto.setNTipoDeclaracion(rs.getInt("NTIPODECLARACION"));
        dto.setVcNumeroSerie(rs.getString("VCNUMEROSERIE"));
        dto.setVcNumeroPlanilla(rs.getString("VCNUMEROPLANILLA"));
        dto.setNCodigoCausalReliquidacion(rs.getInt("NCODIGOCAUSALRELIQUIDACION"));
        dto.setNCodigoTipoEgreso(rs.getInt("NCODIGOTIPOEGRESO"));
        dto.setNTipoMovimientoID(rs.getInt("NTIPOMOVIMIENTOID"));
        dto.setVcUsuarioCreacion(rs.getString("VCUSUARIOCREACION"));
        dto.setDFechaCreacion(rs.getObject("DFECHACREACION", LocalDateTime.class));
        dto.setDFechaActualizacion(rs.getObject("DFECHAACTUALIZACION", LocalDateTime.class));
        dto.setVcUsuarioActualizacion(rs.getString("VCUSUARIOACTUALIZACION"));

        return dto;
    }

    // 2. Metodo para insertar usando el stored procedure
    public Map<String, Object> insertarRegistro(CtactePafDTO registro) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("SP_INSERTAR_TBLCTACTEPAF")
                .declareParameters(
                        // Parámetros de entrada
                        new SqlParameter("p_nPagoPAFID", Types.NUMERIC),
                        new SqlParameter("p_nMesRemuneracion", Types.NUMERIC),
                        new SqlParameter("p_nRUTCausante", Types.NUMERIC),
                        new SqlParameter("p_cDVCausante", Types.VARCHAR),
                        new SqlParameter("p_nRUTBeneficiario", Types.NUMERIC),
                        new SqlParameter("p_nDVRUTBeneficiario", Types.VARCHAR),
                        new SqlParameter("p_nIDBeneficio", Types.NUMERIC),
                        new SqlParameter("p_nMontoBeneficio", Types.NUMERIC),
                        // Parámetros opcionales
                        new SqlParameter("p_nRUTEmpleador", Types.NUMERIC),
                        new SqlParameter("p_nDVRUTEmpleador", Types.VARCHAR),
                        new SqlParameter("p_nIDTipoBeneficio", Types.NUMERIC),
                        new SqlParameter("p_nIDTipoBeneficiario", Types.NUMERIC),
                        new SqlParameter("p_nTipoCausanteID", Types.NUMERIC),
                        new SqlParameter("p_nNumDiasAsigFam", Types.NUMERIC),
                        new SqlParameter("p_nCodTramo", Types.NUMERIC),
                        new SqlParameter("p_nMontoDocumento", Types.NUMERIC),
                        new SqlParameter("p_vcNumeroDocumento", Types.VARCHAR),
                        new SqlParameter("p_nFechaEmisionDocumento", Types.NUMERIC),
                        new SqlParameter("p_nCodigoBanco", Types.NUMERIC),
                        new SqlParameter("p_nCodigoInstitucion", Types.NUMERIC),
                        new SqlParameter("p_vcOrigen", Types.VARCHAR),
                        new SqlParameter("p_nTipoEmision", Types.NUMERIC),
                        new SqlParameter("p_nTipoDeclaracion", Types.NUMERIC),
                        new SqlParameter("p_vcNumeroSerie", Types.VARCHAR),
                        new SqlParameter("p_vcNumeroPlanilla", Types.VARCHAR),
                        new SqlParameter("p_nCodigoCausalReliquidacion", Types.NUMERIC),
                        new SqlParameter("p_nCodigoTipoEgreso", Types.NUMERIC),
                        new SqlParameter("p_nTipoMovimientoID", Types.NUMERIC),
                        new SqlParameter("p_vcUsuarioCreacion", Types.VARCHAR),
                        new SqlParameter("p_dFechaCreacion", Types.TIMESTAMP),
                        new SqlParameter("p_dFechaActualizacion", Types.TIMESTAMP),
                        new SqlParameter("p_vcUsuarioActualizacion", Types.VARCHAR),
                        // Parámetros de salida
                        new SqlOutParameter("p_nResultado", Types.NUMERIC),
                        new SqlOutParameter("p_vcMensaje", Types.VARCHAR)
                );

        Map<String, Object> params = new HashMap<>();
        // Parámetros obligatorios
        params.put("p_nPagoPAFID", registro.getNPagoPAFID());
        params.put("p_nMesRemuneracion", registro.getNMesRemuneracion());
        params.put("p_nRUTCausante", registro.getNRutCausante());
        params.put("p_cDVCausante", registro.getCDvCausante());
        params.put("p_nRUTBeneficiario", registro.getNRutBeneficiario());
        params.put("p_nDVRUTBeneficiario", registro.getNDvRutBeneficiario());
        params.put("p_nIDBeneficio", registro.getNIdBeneficio());
        params.put("p_nMontoBeneficio", registro.getNMontoBeneficio());
        // Parámetros opcionales (usan getters que pueden retornar null)
        params.put("p_nRUTEmpleador", registro.getNRutEmpleador());
        params.put("p_nDVRUTEmpleador", registro.getNDvRutEmpleador());
        params.put("p_nIDTipoBeneficio", registro.getNIdTipoBeneficio());
        params.put("p_nIDTipoBeneficiario", registro.getNIdTipoBeneficiario());
        params.put("p_nTipoCausanteID", registro.getNTipoCausanteID());
        params.put("p_nNumDiasAsigFam", registro.getNNumDiasAsigFam());
        params.put("p_nCodTramo", registro.getNCodTramo());
        params.put("p_nMontoDocumento", registro.getNMontoDocumento());
        params.put("p_vcNumeroDocumento", registro.getVcNumeroDocumento());
        params.put("p_nFechaEmisionDocumento", registro.getNFechaEmisionDocumento());
        params.put("p_nCodigoBanco", registro.getNCodigoBanco());
        params.put("p_nCodigoInstitucion", registro.getNCodigoInstitucion());
        params.put("p_vcOrigen", registro.getVcOrigen());
        params.put("p_nTipoEmision", registro.getNTipoEmision());
        params.put("p_nTipoDeclaracion", registro.getNTipoDeclaracion());
        params.put("p_vcNumeroSerie", registro.getVcNumeroSerie());
        params.put("p_vcNumeroPlanilla", registro.getVcNumeroPlanilla());
        params.put("p_nCodigoCausalReliquidacion", registro.getNCodigoCausalReliquidacion());
        params.put("p_nCodigoTipoEgreso", registro.getNCodigoTipoEgreso());
        params.put("p_nTipoMovimientoID", registro.getNTipoMovimientoID());
        params.put("p_vcUsuarioCreacion", registro.getVcUsuarioCreacion());
        params.put("p_dFechaCreacion", registro.getDFechaCreacion());
        params.put("p_dFechaActualizacion", registro.getDFechaActualizacion());
        params.put("p_vcUsuarioActualizacion", registro.getVcUsuarioActualizacion());

        return jdbcCall.execute(params);
    }

    // 3. Metodo que coordina ambos procesos
    @Override
    public Map<String, Object> procesarYGuardar(Integer idProcesoPago) {
        List<CtactePafDTO> datos = obtenerDatosDesdeFuncion(idProcesoPago);
        Map<String, Object> resultado = new HashMap<>();

        if (!datos.isEmpty()) {
            CtactePafDTO registro = datos.get(0); // Asume un solo registro relevante
            resultado = insertarRegistro(registro);
        } else {
            resultado.put("p_nResultado", 0);
            resultado.put("p_vcMensaje", "No se encontraron datos para el proceso: " + idProcesoPago);
        }
        return resultado;
    }
}