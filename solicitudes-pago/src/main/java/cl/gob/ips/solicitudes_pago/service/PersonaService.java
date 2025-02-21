package cl.gob.ips.solicitudes_pago.service;

import cl.gob.ips.solicitudes_pago.dto.DetallePersonaDTO;
import cl.gob.ips.solicitudes_pago.dto.PersonaDTO;

import java.util.List;

public interface PersonaService {
    DetallePersonaDTO obtenerPersona(int rutPersona);
    List<PersonaDTO> obtenerDetallePersona(int rut);
}