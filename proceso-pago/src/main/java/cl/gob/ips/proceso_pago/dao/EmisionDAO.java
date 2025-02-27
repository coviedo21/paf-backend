package cl.gob.ips.proceso_pago.dao;

import java.util.List;

import cl.gob.ips.proceso_pago.dto.EmisionArchivoDTO;
import cl.gob.ips.proceso_pago.dto.EmisionDTO;
import cl.gob.ips.proceso_pago.dto.ProcesoDTO;

public interface EmisionDAO {
    Boolean validarSolicitudesEmitidas(List<EmisionArchivoDTO> emision);
    List<EmisionDTO> obtenerEmisiones();
    int insertarEmision(EmisionDTO emision);
    ProcesoDTO obtenerProcesoPorPeriodo(String periodo);
}