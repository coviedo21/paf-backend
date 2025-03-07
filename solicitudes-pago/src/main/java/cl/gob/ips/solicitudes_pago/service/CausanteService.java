package cl.gob.ips.solicitudes_pago.service;

import java.util.List;

import cl.gob.ips.solicitudes_pago.dto.CausanteCuentaCorrienteDTO;
import cl.gob.ips.solicitudes_pago.dto.CausanteDTO;
import cl.gob.ips.solicitudes_pago.dto.DetalleCausanteDTO;

public interface CausanteService {
    List<CausanteCuentaCorrienteDTO> obtenerDerechoCausantes(String rutCausante,String rutBeneficiario,String rutEmpleador, String periodoDesde, String periodoHasta, String tipoCausante);
    List<CausanteDTO> obtenerDetalleCausante(int rutBeneficiario);
    String insertarDetalleCausante(DetalleCausanteDTO causanteDTO);
    List<DetalleCausanteDTO> obtenerDetalleCausantePorId(int iIdCausanteSolicitud);
    DetalleCausanteDTO obtenerDetalleCausantePorIdDetalle(int iIdDetalleCausante);
    boolean actualizarDetalleCausante(DetalleCausanteDTO detalleCausanteDTO);
}
