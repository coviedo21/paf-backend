package cl.gob.ips.solicitudes_pago.dao.daoImpl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import cl.gob.ips.solicitudes_pago.dao.CriterioSolicitudDAO;
import cl.gob.ips.solicitudes_pago.dto.CriterioSolicitudCausanteDTO;
import cl.gob.ips.solicitudes_pago.dto.CriterioSolicitudDTO;

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
    public List<CriterioSolicitudCausanteDTO> consultarCriterioCausante(Integer idCausanteSolicitud) {
        String sql = "SELECT * FROM paf.fn_ObtenerCriteriosCausante(?)";

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, new Object[]{idCausanteSolicitud});

        List<CriterioSolicitudCausanteDTO> criterioCausanteList = new ArrayList<>();
        for (Map<String, Object> row : results) {
            CriterioSolicitudCausanteDTO criterioCausante = new CriterioSolicitudCausanteDTO();
            if (row.get("iIdCriterioCausante") != null) criterioCausante.setIdCriterioSolicitudCausante((Integer) row.get("iIdCriterioCausante"));
            if (row.get("iIdCausanteSolicitud") != null) criterioCausante.setIdCausanteSolicitud((Integer) row.get("iIdCausanteSolicitud"));
            if (row.get("iIdCriterio") != null) criterioCausante.setIdCriterio((Integer) row.get("iIdCriterio"));
            if (row.get("vcCumple") != null) criterioCausante.setCumple((String) row.get("vcCumple"));
            if (row.get("vcArchivo") != null) criterioCausante.setArchivo((String) row.get("vcArchivo"));
            if (row.get("nombreCriterio") != null) criterioCausante.setNombreCriterio((String) row.get("nombreCriterio"));
            criterioCausanteList.add(criterioCausante);
        }

        return criterioCausanteList;
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
                        new SqlParameter("dFechaDesde", Types.DATE),
                        new SqlParameter("dFechaHasta", Types.DATE),
                        new SqlOutParameter("idCriterio", Types.INTEGER),
                        new SqlOutParameter("mensajeRespuesta", Types.VARCHAR)
                );

        MapSqlParameterSource inParams = new MapSqlParameterSource()
                .addValue("iIdSolicitud", criterioSolicitudDTO.getIdSolicitud())
                .addValue("iIdCriterio", criterioSolicitudDTO.getIdCriterio())
                .addValue("vcCumple", criterioSolicitudDTO.getCumple())
                .addValue("vcArchivo", criterioSolicitudDTO.getArchivo())
                .addValue("dFechaDesde", criterioSolicitudDTO.getArchivo())
                .addValue("dFechaHasta", criterioSolicitudDTO.getArchivo());

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

    @Override
    public String insertarCriterioCausante(CriterioSolicitudCausanteDTO criterioCausante) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName(esquema)
                .withProcedureName("SP_InsertarCriterioCausante")
                .declareParameters(
                        new SqlParameter("iIdCausanteSolicitud", Types.INTEGER),
                        new SqlParameter("iIdCriterio", Types.INTEGER),
                        new SqlParameter("vcCumple", Types.VARCHAR),
                        new SqlParameter("vcArchivo", Types.VARCHAR),
                        new SqlParameter("dFechaDesde", Types.DATE),
                        new SqlParameter("dFechaHasta", Types.DATE),
                        new SqlOutParameter("idCriterioCausante", Types.INTEGER),
                        new SqlOutParameter("mensajeRespuesta", Types.VARCHAR)
                );

        MapSqlParameterSource inParams = new MapSqlParameterSource()
                .addValue("iIdCausanteSolicitud", criterioCausante.getIdCausanteSolicitud())
                .addValue("iIdCriterio", criterioCausante.getIdCriterio())
                .addValue("vcCumple", criterioCausante.getCumple())
                .addValue("vcArchivo", criterioCausante.getArchivo())
                .addValue("dFechaDesde", criterioCausante.getFechaDesde())
                .addValue("dFechaHasta", criterioCausante.getFechaHasta());

        try {
            Map<String, Object> result = jdbcCall.execute(inParams);
            Integer idCriterio = (Integer) result.get("idCriterioCausante");
            String mensajeRespuesta = (String) result.get("mensajeRespuesta");
            return "ID Criterio: " + idCriterio + ", Mensaje: " + mensajeRespuesta;
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return "Error al insertar criterio para el causante";
        }
    }

    @Override
    public boolean actualizarCriterioSolicitud(CriterioSolicitudDTO criterioSolicitudDTO) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName(esquema)
                .withProcedureName("SP_ActualizarCriterioSolicitud")
                .declareParameters(
                        new SqlParameter("iidCriterioSolicitud", Types.INTEGER),
                        new SqlParameter("iIdSolicitud", Types.INTEGER),
                        new SqlParameter("iIdCriterio", Types.INTEGER),
                        new SqlParameter("vcCumple", Types.VARCHAR),
                        new SqlParameter("vcArchivo", Types.VARCHAR),
                        new SqlParameter("dFechaDesde", Types.DATE),
                        new SqlParameter("dFechaHasta", Types.DATE),
                        new SqlOutParameter("mensajeRespuesta", Types.VARCHAR)
                );

        MapSqlParameterSource inParams = new MapSqlParameterSource()
                .addValue("iidCriterioSolicitud", criterioSolicitudDTO.getIdCriterioSolicitud())
                .addValue("iIdSolicitud", criterioSolicitudDTO.getIdSolicitud())
                .addValue("iIdCriterio", criterioSolicitudDTO.getIdCriterio())
                .addValue("vcCumple", criterioSolicitudDTO.getCumple())
                .addValue("vcArchivo", criterioSolicitudDTO.getArchivo())
                .addValue("dFechaDesde", criterioSolicitudDTO.getFechaDesde())
                .addValue("dFechaHasta", criterioSolicitudDTO.getFechaHasta());

        try {
            Map<String, Object> result = jdbcCall.execute(inParams);
            String mensajeRespuesta = (String) result.get("mensajeRespuesta");
            return true;
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizarCriterioCausante(CriterioSolicitudCausanteDTO criterioCausante) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName(esquema)
                .withProcedureName("SP_ActualizarCriterioCausante")
                .declareParameters(
                        new SqlParameter("iidCriterioCausante", Types.INTEGER),
                        new SqlParameter("iIdCausanteSolicitud", Types.INTEGER),
                        new SqlParameter("iIdCriterio", Types.INTEGER),
                        new SqlParameter("vcCumple", Types.VARCHAR),
                        new SqlParameter("vcArchivo", Types.VARCHAR),
                        new SqlParameter("dFechaDesde", Types.DATE),
                        new SqlParameter("dFechaHasta", Types.DATE),
                        new SqlOutParameter("mensajeRespuesta", Types.VARCHAR)
                );

        MapSqlParameterSource inParams = new MapSqlParameterSource()
                .addValue("iidCriterioCausante", criterioCausante.getIdCriterioSolicitudCausante())
                .addValue("iIdCausanteSolicitud", criterioCausante.getIdCausanteSolicitud())
                .addValue("iIdCriterio", criterioCausante.getIdCriterio())
                .addValue("vcCumple", criterioCausante.getCumple())
                .addValue("vcArchivo", criterioCausante.getArchivo())
                .addValue("dFechaDesde", criterioCausante.getFechaDesde())
                .addValue("dFechaHasta", criterioCausante.getFechaHasta());

        try {
            Map<String, Object> result = jdbcCall.execute(inParams);
            String mensajeRespuesta = (String) result.get("mensajeRespuesta");
            return true;
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return false;
        }
    }

    @Override
    public CriterioSolicitudDTO obtenerCriteriosPorIdCriterio(Integer idCriterioSolicitud) {
        String sql = "SELECT * FROM paf.fn_ObtenerCriteriosSolicitudPorIdCriterio(?)";

        try {
            Map<String, Object> result = jdbcTemplate.queryForMap(sql, idCriterioSolicitud);

            CriterioSolicitudDTO criterioDTO = new CriterioSolicitudDTO();
            if (result.get("IdCriterioSolicitud") != null) criterioDTO.setIdCriterioSolicitud((Integer) result.get("IdCriterioSolicitud"));
            if (result.get("IdSolicitud") != null) criterioDTO.setIdSolicitud((Integer) result.get("IdSolicitud"));
            if (result.get("IdCriterio") != null) criterioDTO.setIdCriterio((Integer) result.get("IdCriterio"));
            if (result.get("Cumple") != null) criterioDTO.setCumple((String) result.get("Cumple"));
            if (result.get("Archivo") != null) criterioDTO.setArchivo((String) result.get("Archivo"));
            if (result.get("nombreCriterio") != null) criterioDTO.setNombreCriterio((String) result.get("nombreCriterio"));

            return criterioDTO;
        } catch (Exception e) {
            System.err.println("ERROR al obtener el criterio: " + e.getMessage());
            return null; // Devuelve null si no se encuentra el registro o ocurre un error
        }
    }

}