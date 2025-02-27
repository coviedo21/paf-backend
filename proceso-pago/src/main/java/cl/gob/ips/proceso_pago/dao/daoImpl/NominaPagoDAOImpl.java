package cl.gob.ips.proceso_pago.dao.daoImpl;

import cl.gob.ips.proceso_pago.dao.NominaPagoDAO;
import cl.gob.ips.proceso_pago.dto.NominaPagoDTO;
import cl.gob.ips.proceso_pago.mapper.NominaPagoRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class NominaPagoDAOImpl implements NominaPagoDAO {

    private final JdbcTemplate jdbcTemplate;
    private final NominaPagoRowMapper rowMapper;

    @Value("${spring.datasource.schema}")
    private String esquema;

    @Autowired
    public NominaPagoDAOImpl(@Qualifier("pafJdbc") JdbcTemplate jdbcTemplate, NominaPagoRowMapper rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }

    public List<NominaPagoDTO> ejecutarProcedimientoAlmacenado(int iIdProcesoIN, String fecPago) {
        return jdbcTemplate.query(connection -> {
                    PreparedStatement ps = connection.prepareStatement("{call " + esquema + ".SP_NominaPagosData(?, ?)}");
                    ps.setInt(1, iIdProcesoIN);
                    ps.setDate(2, Date.valueOf(fecPago));
                    return ps;
                },
                rowMapper
        );
    }
}
