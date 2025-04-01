package cl.gob.ips.proceso_pago.service;

import cl.gob.ips.proceso_pago.dto.CtactePafDTO;
import cl.gob.ips.proceso_pago.dto.ProcesoResponse;
import cl.gob.ips.proceso_pago.dto.ResultadoSP;

import java.util.List;

public interface CtaCtePafService {
    ProcesoResponse procesarYGuardarDatos(Integer idProcesoPago);
    List<CtactePafDTO> obtenerDatosProceso(Integer idProcesoPago);
    ResultadoSP ejecutarInsercionManual(CtactePafDTO registro);
}