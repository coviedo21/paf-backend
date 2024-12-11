package cl.gob.ips.pagos_asignacion_familiar.dao.daoImpl;

import java.sql.Types;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import cl.gob.ips.pagos_asignacion_familiar.dao.DetallePersonaDAO;
import cl.gob.ips.pagos_asignacion_familiar.dto.DetallePersonaDTO;

@Repository
public class DetallePersonaDAOImpl implements DetallePersonaDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DetallePersonaDAOImpl(@Qualifier("genesysPJdbc") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public DetallePersonaDTO obtenerDetallePersona(int rutBeneficiario) {

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("SP_DETALLEPERSONAS")
                .declareParameters(
                        new SqlParameter("rutBeneficiario", Types.NUMERIC),
                        new SqlOutParameter("RUT_BENEFICIARIO", Types.VARCHAR),
                        new SqlOutParameter("RUT_CAUSANTE", Types.VARCHAR),
                        new SqlOutParameter("NOMBRES_BENEFICIARIO", Types.VARCHAR),
                        new SqlOutParameter("APATERNO_BENEFICIARIO", Types.VARCHAR),
                        new SqlOutParameter("AMATERNO_BENEFICIARIO", Types.VARCHAR),
                        new SqlOutParameter("NOMBRES_CAUSANTE", Types.VARCHAR),
                        new SqlOutParameter("APATERNO_CAUSANTE", Types.VARCHAR),
                        new SqlOutParameter("AMATERNO_CAUSANTE", Types.VARCHAR),
                        new SqlOutParameter("TIPO_BENEFICIARIO", Types.VARCHAR),
                        new SqlOutParameter("TIPO_CAUSANTE", Types.VARCHAR));

        MapSqlParameterSource inParams = new MapSqlParameterSource();
        inParams.addValue("rutBeneficiario", rutBeneficiario);

        Map<String, Object> result = null;

        try {
            result = jdbcCall.execute(inParams);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }

        DetallePersonaDTO detallePersonasDTO = new DetallePersonaDTO();

        if (result.size() > 0) {
            if (result.get("RUT_BENEFICIARIO") != null)
                detallePersonasDTO.setRutBeneficiario((String) result.get("RUT_BENEFICIARIO"));

            if (result.get("RUT_CAUSANTE") != null)
                detallePersonasDTO.setRutCausante((String) result.get("RUT_CAUSANTE"));

            if (result.get("NOMBRES_BENEFICIARIO") != null)
                detallePersonasDTO.setNombresBeneficiario((String) result.get("NOMBRES_BENEFICIARIO"));

            if (result.get("APATERNO_BENEFICIARIO") != null)
                detallePersonasDTO.setApaternoBeneficiario((String) result.get("APATERNO_BENEFICIARIO"));

            if (result.get("AMATERNO_BENEFICIARIO") != null)
                detallePersonasDTO.setAmaternoBeneficiario((String) result.get("AMATERNO_BENEFICIARIO"));

            if (result.get("NOMBRES_CAUSANTE") != null)
                detallePersonasDTO.setNombresCausante((String) result.get("NOMBRES_CAUSANTE"));

            if (result.get("APATERNO_CAUSANTE") != null)
                detallePersonasDTO.setApaternoCausante((String) result.get("APATERNO_CAUSANTE"));

            if (result.get("AMATERNO_CAUSANTE") != null)
                detallePersonasDTO.setAmaternoCausante((String) result.get("AMATERNO_CAUSANTE"));

            if (result.get("TIPO_BENEFICIARIO") != null)
                detallePersonasDTO.setTipoBeneficiario((String) result.get("TIPO_BENEFICIARIO"));

            if (result.get("TIPO_CAUSANTE") != null)
                detallePersonasDTO.setTipoCausante((String) result.get("TIPO_CAUSANTE"));

            return detallePersonasDTO;

        } else {
            return null;
        }
    }
}