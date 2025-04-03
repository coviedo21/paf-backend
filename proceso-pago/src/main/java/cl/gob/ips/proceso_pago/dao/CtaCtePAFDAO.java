package cl.gob.ips.proceso_pago.dao;

import cl.gob.ips.proceso_pago.dto.CtaCteDTO;
import cl.gob.ips.proceso_pago.dto.SpResponse;

import java.util.List;

public interface CtaCtePAFDAO {
    List<CtaCteDTO> obtenerDatosCtaCtePAF(int IdProceso);
    SpResponse insertTBLCTACTEPAF(List<CtaCteDTO> dtoPAFList);
    List<CtaCteDTO> selectTBLCTACTEPAF(int nidProceso);
    SpResponse insertarTBLCTACTEPAF(List<CtaCteDTO> dtoPAFList);
    SpResponse eliminarRegistrosTBLCTACTEPAF(int nidProceso);
}