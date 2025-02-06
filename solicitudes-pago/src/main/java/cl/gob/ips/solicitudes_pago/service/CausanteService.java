package cl.gob.ips.solicitudes_pago.service;

import java.util.List;

import cl.gob.ips.solicitudes_pago.dto.CausanteDTO;
import cl.gob.ips.solicitudes_pago.dto.DerechoCausanteDTO;
import cl.gob.ips.solicitudes_pago.dto.DetalleCausanteDTO;

public interface CausanteService {
    List<DerechoCausanteDTO> obtenerDerechoCausantes(String rutCausante,String rutBeneficiario,String periodoDesde, String periodoHasta, String tipoCausante);
    List<CausanteDTO> obtenerDetalleCausante(int rutBeneficiario);
    String insertarDetalleCausante(DetalleCausanteDTO causanteDTO);
    List<DetalleCausanteDTO> obtenerDetalleCausantePorId(int iIdCausanteSolicitud);
}
