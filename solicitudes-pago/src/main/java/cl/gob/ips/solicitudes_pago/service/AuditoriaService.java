package cl.gob.ips.solicitudes_pago.service;

import java.util.List;

import cl.gob.ips.solicitudes_pago.dto.AuditoriaSolicitudDTO;

 
public interface AuditoriaService {
    List<AuditoriaSolicitudDTO> consultarAuditoriaSolicitud(Integer idSolicitud);
}