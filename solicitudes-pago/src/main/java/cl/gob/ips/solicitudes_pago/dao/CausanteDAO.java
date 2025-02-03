package cl.gob.ips.solicitudes_pago.dao;

import java.util.List;

import cl.gob.ips.solicitudes_pago.dto.CausanteDTO;
import cl.gob.ips.solicitudes_pago.dto.DetalleCausanteDTO;

public interface CausanteDAO {
    List<CausanteDTO> obtenerDetalleCausante(int rutBeneficiario);
    String insertarDetalleCausante(DetalleCausanteDTO causanteDTO);
    List<DetalleCausanteDTO> obtenerDetalleCausantePorId(int iIdCausanteSolicitud);
}
