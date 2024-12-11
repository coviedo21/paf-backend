package cl.gob.ips.solicitudes_pago.service;

import java.util.List;

import cl.gob.ips.solicitudes_pago.dto.CausanteDTO;

public interface CausanteService {
    List<CausanteDTO> obtenerDetalleCausante(int rutBeneficiario);
}
