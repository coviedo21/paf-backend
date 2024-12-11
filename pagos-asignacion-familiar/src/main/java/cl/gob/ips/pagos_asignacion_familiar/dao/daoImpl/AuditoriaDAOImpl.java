package cl.gob.ips.pagos_asignacion_familiar.dao.daoImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import cl.gob.ips.pagos_asignacion_familiar.dao.AuditoriaDAO;
import cl.gob.ips.pagos_asignacion_familiar.dto.AuditoriaSolicitudDTO;

@Repository
public class AuditoriaDAOImpl implements AuditoriaDAO{
    
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AuditoriaDAOImpl(@Qualifier("pafJdbc") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<AuditoriaSolicitudDTO> consultarAuditoriaSolicitud(Integer idSolicitud) {
        String sql = "SELECT * FROM paf.fn_ObtenerAuditoriaSolicitud(?)";

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, new Object[]{idSolicitud});

        //List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, inParams);

        List<AuditoriaSolicitudDTO> auditorias = new ArrayList<>();
        for (Map<String, Object> u : results) {
            AuditoriaSolicitudDTO auditoriaDTO = new AuditoriaSolicitudDTO();
            if (u.get("iIdAuditoriaSolicitud") != null) auditoriaDTO.setIIdAuditoriaSolicitud((Integer) u.get("iIdAuditoriaSolicitud"));
            if (u.get("iIdProceso") != null) auditoriaDTO.setIIdProceso((Integer) u.get("iIdProceso"));
            if (u.get("iIdSolicitud") != null) auditoriaDTO.setIIdSolicitud((Integer) u.get("iIdSolicitud"));
            if (u.get("iIdAutoreditor") != null) auditoriaDTO.setIIdAutoreditor((Integer) u.get("iIdAutoreditor"));
            if (u.get("vcObservaciones") != null) auditoriaDTO.setVcObservaciones((String) u.get("vcObservaciones"));
            if (u.get("dFechaInicio") != null) auditoriaDTO.setDFechaInicio((Date) u.get("dFechaInicio"));
            if (u.get("dFechaFin") != null) auditoriaDTO.setDFechaFin((Date) u.get("dFechaFin"));
            if (u.get("iCantidadRegistros") != null) auditoriaDTO.setICantidadRegistros((Integer) u.get("iCantidadRegistros"));
            if (u.get("iTipoActualizacion") != null) auditoriaDTO.setITipoActualizacion((Integer) u.get("iTipoActualizacion"));
            auditorias.add(auditoriaDTO);
        }

        return auditorias;
    }
}