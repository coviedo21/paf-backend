package cl.gob.ips.pagos_asignacion_familiar.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import cl.gob.ips.pagos_asignacion_familiar.dto.AuditoriaSolicitudDTO;

@Repository
public interface AuditoriaDAO {
    List<AuditoriaSolicitudDTO> consultarAuditoriaSolicitud(Integer idSolicitud);
}
