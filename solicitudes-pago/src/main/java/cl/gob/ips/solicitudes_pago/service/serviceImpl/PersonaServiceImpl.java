package cl.gob.ips.solicitudes_pago.service.serviceImpl;

import cl.gob.ips.solicitudes_pago.dao.DetallePersonaDAO;
import cl.gob.ips.solicitudes_pago.dao.PersonaDAO;
import cl.gob.ips.solicitudes_pago.dto.DetallePersonaDTO;
import cl.gob.ips.solicitudes_pago.dto.PersonaDTO;
import cl.gob.ips.solicitudes_pago.service.PersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PersonaServiceImpl implements PersonaService {

    @Autowired
    private DetallePersonaDAO detallePersonaDAO;

    @Autowired
    private PersonaDAO personaDAO;

    @Override
    public DetallePersonaDTO obtenerPersona(int rutPersona) {
        return detallePersonaDAO.obtenerDetallePersona(rutPersona);
    }

    public List<PersonaDTO> obtenerDetallePersona(int rut) {
        try {
            return personaDAO.obtenerDetallePersona(rut);
        } catch (Exception e) {
            if (e.getCause() instanceof SQLException) {
                SQLException sqlException = (SQLException) e.getCause();
                if (sqlException.getErrorCode() == 20001) { // Código de error ORA-20001
                    return new ArrayList<>(); // Retorna lista vacía
                }
            }
            throw e; // Re-lanza otras excepciones
        }
    }
}