package cl.gob.ips.pagos_asignacion_familiar.service.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.gob.ips.pagos_asignacion_familiar.dao.AuditoriaDAO;
import cl.gob.ips.pagos_asignacion_familiar.dto.AuditoriaSolicitudDTO;
import cl.gob.ips.pagos_asignacion_familiar.service.AuditoriaService;

@Service
public class AuditoriaServiceImpl implements AuditoriaService{

   @Autowired
    private AuditoriaDAO auditoriaDAO;

    @Override
    public List<AuditoriaSolicitudDTO> consultarAuditoriaSolicitud(Integer idSolicitud){
        return auditoriaDAO.consultarAuditoriaSolicitud(idSolicitud);
    }
}
