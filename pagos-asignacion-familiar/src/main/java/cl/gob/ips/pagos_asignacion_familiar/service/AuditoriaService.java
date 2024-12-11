package cl.gob.ips.pagos_asignacion_familiar.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cl.gob.ips.pagos_asignacion_familiar.dto.AuditoriaSolicitudDTO;
 
@Service
public interface AuditoriaService {
    List<AuditoriaSolicitudDTO> consultarAuditoriaSolicitud(Integer idSolicitud);
}
