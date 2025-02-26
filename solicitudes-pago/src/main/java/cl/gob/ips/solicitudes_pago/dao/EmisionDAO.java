package cl.gob.ips.solicitudes_pago.dao;

import java.util.List;

import cl.gob.ips.solicitudes_pago.dto.EmisionArchivoDTO;
import cl.gob.ips.solicitudes_pago.dto.EmisionDTO;

public interface EmisionDAO {
    Boolean validarSolicitudesEmitidas(List<EmisionArchivoDTO> emision);
    List<EmisionDTO> obtenerEmisiones();
    int insertarEmision(EmisionDTO emision);
}