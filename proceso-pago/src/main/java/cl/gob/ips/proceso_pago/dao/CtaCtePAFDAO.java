package cl.gob.ips.proceso_pago.dao;

import cl.gob.ips.proceso_pago.dto.CtaCteDTO;
import cl.gob.ips.proceso_pago.dto.SpResponse;

import java.util.List;

public interface CtaCtePAFDAO {
    SpResponse copyDataToTBLCTACTEPAF(int iIdProcesoPago);
    List<CtaCteDTO> selectTBLCTACTEPAF(int nidProceso);
    SpResponse insertarTBLCTACTEPAF(List<CtaCteDTO> dtoPAFList);
    SpResponse actualizarEstadoTBLCTACTEPAF(int nidProceso);
    SpResponse eliminarRegistrosTBLCTACTEPAF(int nidProceso);
}