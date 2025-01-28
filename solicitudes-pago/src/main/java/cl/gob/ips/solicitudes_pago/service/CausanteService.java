package cl.gob.ips.solicitudes_pago.service;

import java.util.List;

import cl.gob.ips.solicitudes_pago.dto.CausanteDTO;
import cl.gob.ips.solicitudes_pago.dto.DerechoCausanteDTO;

public interface CausanteService {
    List<DerechoCausanteDTO> obtenerDerechoCausantes(int rutBeneficiario,int periodoDesde, int periodoHasta, int tipoBeneficiario);
    List<CausanteDTO> obtenerDetalleCausante(int rutBeneficiario);
}
