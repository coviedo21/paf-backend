package cl.gob.ips.pagos_asignacion_familiar.dao.daoImpl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import cl.gob.ips.pagos_asignacion_familiar.dao.CriterioSolicitudDAO;
import cl.gob.ips.pagos_asignacion_familiar.dto.CriterioSolicitudDTO;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class CriterioSolicitudDAOImpl implements CriterioSolicitudDAO {

    private final JdbcTemplate jdbcTemplate;

    @Value("${spring.datasource.schema}")
    private String esquema;

    @Autowired
    public CriterioSolicitudDAOImpl(@Qualifier("pafJdbc") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<CriterioSolicitudDTO> consultarCriteriosSolicitud(Integer idSolicitud) {
        String sql = "SELECT * FROM paf.fn_ObtenerCriteriosSolicitud(?)";

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, new Object[]{idSolicitud});

        //MapSqlParameterSource inParams = new MapSqlParameterSource()
        //    .addValue("idSolicitud", idSolicitud);

        //List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, inParams);

        List<CriterioSolicitudDTO> criterios = new ArrayList<>();
        for (Map<String, Object> u : results) {
            CriterioSolicitudDTO criterioDTO = new CriterioSolicitudDTO();
            if (u.get("IdCriterioSolicitud") != null) criterioDTO.setIdCriterioSolicitud((Integer) u.get("IdCriterioSolicitud"));
            if (u.get("IdSolicitud") != null) criterioDTO.setIdSolicitud((Integer) u.get("IdSolicitud"));
            if (u.get("IdCriterio") != null) criterioDTO.setIdCriterio((Integer) u.get("IdCriterio"));
            if (u.get("Cumple") != null) criterioDTO.setCumple((String) u.get("Cumple"));
            if (u.get("Archivo") != null) criterioDTO.setArchivo((String) u.get("Archivo"));
            if (u.get("nombreCriterio") != null) criterioDTO.setNombreCriterio((String) u.get("nombreCriterio"));
            criterios.add(criterioDTO);
        }

        return criterios;
    }

    @Override
    public String insertarCriterioSolicitud(CriterioSolicitudDTO criterioSolicitudDTO) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName(esquema)
                .withProcedureName("SP_InsertarCriterioSolicitud")
                .declareParameters(
                        new SqlParameter("iIdSolicitud", Types.INTEGER),
                        new SqlParameter("iIdCriterio", Types.INTEGER),
                        new SqlParameter("vcCumple", Types.VARCHAR),
                        new SqlParameter("vcArchivo", Types.VARCHAR),
                        new SqlOutParameter("idCriterio", Types.INTEGER),
                        new SqlOutParameter("mensajeRespuesta", Types.VARCHAR)
                );

        MapSqlParameterSource inParams = new MapSqlParameterSource()
                .addValue("iIdSolicitud", criterioSolicitudDTO.getIdSolicitud())
                .addValue("iIdCriterio", criterioSolicitudDTO.getIdCriterio())
                .addValue("vcCumple", criterioSolicitudDTO.getCumple())
                .addValue("vcArchivo", criterioSolicitudDTO.getArchivo());

        try {
            Map<String, Object> result = jdbcCall.execute(inParams);
            Integer idCriterio = (Integer) result.get("idCriterio");
            String mensajeRespuesta = (String) result.get("mensajeRespuesta");
            return "ID Criterio: " + idCriterio + ", Mensaje: " + mensajeRespuesta;
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return "Error al insertar criterio de solicitud";
        }
    }
}