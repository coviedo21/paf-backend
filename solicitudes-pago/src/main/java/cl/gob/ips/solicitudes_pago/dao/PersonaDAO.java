package cl.gob.ips.solicitudes_pago.dao;

import cl.gob.ips.solicitudes_pago.dto.PersonaDTO;

import java.util.List;

public interface PersonaDAO {
    List<PersonaDTO> obtenerDetallePersona(int rut);
}
