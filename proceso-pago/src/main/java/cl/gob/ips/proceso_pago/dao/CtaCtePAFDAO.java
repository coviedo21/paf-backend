package cl.gob.ips.proceso_pago.dao;

import cl.gob.ips.proceso_pago.dto.*;

import java.util.List;
import java.util.Map;

public interface CtaCtePAFDAO {
//    List<CtaCtePAFDTO> obtenerDatosDesdeFuncion(int iIdProcesoPago);
//    SpResponse insertarConSP(CtaCtePAFDTO datos);
    SpResponse procesarYInsertar(int iIdProcesoPago);
//    SpResponse insertarDesdeVistaConSP();
}