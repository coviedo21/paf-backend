package cl.gob.ips.solicitudes_pago.service.serviceImpl;

import cl.gob.ips.solicitudes_pago.dao.DetallePersonaDAO;
import cl.gob.ips.solicitudes_pago.dto.DetallePersonaDTO;
import cl.gob.ips.solicitudes_pago.service.PersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonaServiceImpl implements PersonaService {

    @Autowired
    private DetallePersonaDAO detallePersonaDAO;

    @Override
    public DetallePersonaDTO obtenerPersona(int rutPersona) {
        return detallePersonaDAO.obtenerDetallePersona(rutPersona);
    }
}