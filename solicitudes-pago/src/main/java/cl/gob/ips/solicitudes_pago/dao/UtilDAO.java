package cl.gob.ips.solicitudes_pago.dao;

import java.util.List;

import cl.gob.ips.solicitudes_pago.dto.OrigenDTO;

public interface UtilDAO {
    List<OrigenDTO> obtenerOrigenes(int tipo);
}