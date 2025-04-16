package cl.gob.ips.proceso_pago.service;

import java.util.List;

import cl.gob.ips.proceso_pago.dto.EmisionArchivoDTO;
import cl.gob.ips.proceso_pago.dto.EmisionDTO;
import cl.gob.ips.proceso_pago.dto.PagoRetencionDTO;
import cl.gob.ips.proceso_pago.dto.ProcesoDTO;

public interface EmisionService {
    Boolean validarSolicitudesEmitidas(List<EmisionArchivoDTO> emision, int idProceso);
    List<EmisionDTO> obtenerEmisiones();
    int insertarEmision(EmisionDTO emision);
    ProcesoDTO obtenerProcesoPorPeriodo(String periodo);
    EmisionDTO obtenerEmision(int idEmision);
    boolean insertarPagoRetencion(PagoRetencionDTO dto);
}