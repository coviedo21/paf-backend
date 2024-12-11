package cl.gob.ips.pagos_asignacion_familiar.dao.daoImpl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;

import cl.gob.ips.pagos_asignacion_familiar.dao.CuentaCorrienteDAO;
import cl.gob.ips.pagos_asignacion_familiar.dto.CuentaCorrienteCausanteDTO;

@Component
public class CuentaCorrienteDAOImpl implements CuentaCorrienteDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CuentaCorrienteDAOImpl(@Qualifier("genesysPJdbc") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<CuentaCorrienteCausanteDTO> obtenerCuentaCorrienteCausantes(int rutBeneficiario) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("SP_OBTENERCTACTECAUSANTE")
                .declareParameters(
                        new SqlParameter("p_rutBeneficiario", Types.NUMERIC),
                        new SqlOutParameter("p_cursor", Types.REF_CURSOR));

        MapSqlParameterSource inParams = new MapSqlParameterSource()
                .addValue("p_rutBeneficiario", rutBeneficiario);

        Map<String, Object> result;
        try {
            result = jdbcCall.execute(inParams);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return null;
        }

        List<CuentaCorrienteCausanteDTO> cuentaCorrienteCausanteList = new ArrayList<>();
        List<Map<String, Object>> rows = (List<Map<String, Object>>) result.get("p_cursor");

        for (Map<String, Object> row : rows) {
            CuentaCorrienteCausanteDTO dto = new CuentaCorrienteCausanteDTO();
            dto.setRutCausante(((Number) row.get("RUT_CAUSANTE")).longValue());
            dto.setRutBeneficiario(((Number) row.get("RUT_BENEFICIARIO")).longValue());
            dto.setPeriodo(((Number) row.get("PERIODO")).longValue());
            dto.setNombre((String) row.get("NOMBRE"));
            dto.setIdBeneficio(((Number) row.get("ID_BENEFICIO")).longValue());
            dto.setDerecho(((Number) row.get("DERECHO")).doubleValue());
            dto.setPago(((Number) row.get("PAGO")).doubleValue());
            dto.setDiasReconocidos(((Number) row.get("DIAS_RECONOCIDOS")).longValue());
            dto.setTipoCausante(((Number) row.get("TIPO_CAUSANTE")).intValue());
            dto.setTipoBeneficio(((Number) row.get("TIPO_BENEFICIO")).intValue());
            dto.setFechaActualizacion((Timestamp) row.get("FECHA_ACTUALIZACION"));
            dto.setTipoBeneficiario(((Number) row.get("TIPO_BENEFICIARIO")).intValue());
            dto.setIdBenGenesys(((Number) row.get("ID_BEN_GENESYS")).longValue());
            dto.setCausalExtincionId(((Number) row.get("CAUSAL_EXTINCION_ID")).intValue());
            dto.setFechaExtincion((Timestamp) row.get("FECHA_EXTINCION"));
            dto.setFechaReconocimiento((Timestamp) row.get("FECHA_RECONOCIMIENTO"));
            cuentaCorrienteCausanteList.add(dto);
        }

        return cuentaCorrienteCausanteList;
    }
}
