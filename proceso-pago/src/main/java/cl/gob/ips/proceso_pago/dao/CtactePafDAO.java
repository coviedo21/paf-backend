package cl.gob.ips.proceso_pago.dao;

import cl.gob.ips.proceso_pago.dto.*;

import java.util.List;
import java.util.Map;

public interface CtactePafDAO {
    List<CtactePafDTO> obtenerDatosDesdeFuncion(Integer idProcesoPago);
    Map<String, Object> insertarRegistro(CtactePafDTO registro);
    Map<String, Object> procesarYGuardar(Integer idProcesoPago);
}