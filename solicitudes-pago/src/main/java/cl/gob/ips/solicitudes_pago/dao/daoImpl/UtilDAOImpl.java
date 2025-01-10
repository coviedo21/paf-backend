package cl.gob.ips.solicitudes_pago.dao.daoImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cl.gob.ips.solicitudes_pago.dao.UtilDAO;
import cl.gob.ips.solicitudes_pago.dto.OrigenDTO;

@Repository
public class UtilDAOImpl implements UtilDAO{
private final JdbcTemplate jdbcTemplate;

    @Value("${spring.datasource.schema}")
    private String esquema;

    @Autowired
    public UtilDAOImpl(@Qualifier("pafJdbc") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<OrigenDTO> obtenerOrigenes(int iTipo) {
        String sql = "SELECT * FROM paf.fn_ObtenerOrigenes(?)";
        
        // Ejecuta la consulta pasando el parámetro
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, iTipo);
        
        List<OrigenDTO> origenes = new ArrayList<>();
        
        for (Map<String, Object> row : results) {
            OrigenDTO origenDTO = new OrigenDTO();
            
            if (row.get("iIdOrigen") != null) origenDTO.setIdOrigen((Integer) row.get("iIdOrigen"));
            if (row.get("vcNombre") != null) origenDTO.setNombre((String) row.get("vcNombre"));
            if (row.get("iEstadoOrigen") != null) origenDTO.setEstadoOrigen((Integer) row.get("iEstadoOrigen"));
            
            origenes.add(origenDTO);
        }
        
        return origenes;
    }
}
