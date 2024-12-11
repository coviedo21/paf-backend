package cl.gob.ips.solicitudes_pago.service.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.gob.ips.solicitudes_pago.dao.AuditoriaDAO;
import cl.gob.ips.solicitudes_pago.dto.AuditoriaSolicitudDTO;
import cl.gob.ips.solicitudes_pago.service.AuditoriaService;

@Service
public class AuditoriaServiceImpl implements AuditoriaService{

   @Autowired
    private AuditoriaDAO auditoriaDAO;

    @Override
    public List<AuditoriaSolicitudDTO> consultarAuditoriaSolicitud(Integer idSolicitud){
        return auditoriaDAO.consultarAuditoriaSolicitud(idSolicitud);
    }
}