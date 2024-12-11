package cl.gob.ips.solicitudes_pago.dao;

import java.util.List;

import cl.gob.ips.solicitudes_pago.dto.AuditoriaSolicitudDTO;


public interface AuditoriaDAO {
    List<AuditoriaSolicitudDTO> consultarAuditoriaSolicitud(Integer idSolicitud);
}
