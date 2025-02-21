package cl.gob.ips.solicitudes_pago.dao.daoImpl;

import cl.gob.ips.solicitudes_pago.dao.PersonaDAO;
import cl.gob.ips.solicitudes_pago.dto.PersonaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.List;
import java.util.Map;

@Repository
public class PersonaDAOImpl implements PersonaDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PersonaDAOImpl(@Qualifier("genesysPJdbc") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<PersonaDTO> obtenerDetallePersona(int rut) {
        StoredProcedure sp = new StoredProcedure(jdbcTemplate, "ADM_AF.SP_DETALLE_PERSONA") {
            {
                declareParameter(new SqlParameter("p_RUT", Types.NUMERIC));
                declareParameter(new SqlOutParameter("nombre_cursor", Types.REF_CURSOR));
                compile();
            }
        };

        Map<String, Object> params = Map.of("p_RUT", rut);

        Map<String, Object> results = sp.execute(params);

        return (List<PersonaDTO>) results.get("nombre_cursor");
    }


    private final RowMapper<PersonaDTO> personaRowMapper = (rs, rowNum) -> {
        PersonaDTO persona = new PersonaDTO();
        persona.setPrimerNombre(rs.getString("PRIMER_NOMBRE"));
        persona.setSegundoNombre(rs.getString("SEGUNDO_NOMBRE"));
        persona.setApellidoPaterno(rs.getString("APELLIDO_PATERNO"));
        persona.setApellidoMaterno(rs.getString("APELLIDO_MATERNO"));
        return persona;
    };
}