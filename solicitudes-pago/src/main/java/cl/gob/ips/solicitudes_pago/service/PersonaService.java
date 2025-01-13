package cl.gob.ips.solicitudes_pago.service;

import cl.gob.ips.solicitudes_pago.dto.DetallePersonaDTO;

public interface PersonaService {
    DetallePersonaDTO obtenerPersona(int rutPersona);
}