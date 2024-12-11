package cl.gob.ips.solicitudes_pago.dao;

import java.util.List;

import cl.gob.ips.solicitudes_pago.dto.CausanteDTO;

public interface CausanteDAO {
    List<CausanteDTO> obtenerDetalleCausante(int rutBeneficiario);
}
