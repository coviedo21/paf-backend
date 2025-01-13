package cl.gob.ips.solicitudes_pago.dao;

import cl.gob.ips.solicitudes_pago.dto.DetallePersonaDTO;
import org.springframework.stereotype.Repository;

@Repository
public interface DetallePersonaDAO {
    DetallePersonaDTO obtenerDetallePersona(int rutBeneficiario);
}
