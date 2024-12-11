package cl.gob.ips.solicitudes_pago.service;

import java.util.List;

import cl.gob.ips.solicitudes_pago.dto.OrigenDTO;

public interface UtilService {
    List<OrigenDTO> obtenerOrigenes();
}