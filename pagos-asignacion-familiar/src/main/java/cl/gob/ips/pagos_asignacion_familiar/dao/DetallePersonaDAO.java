package cl.gob.ips.pagos_asignacion_familiar.dao;

import org.springframework.stereotype.Repository;

import cl.gob.ips.pagos_asignacion_familiar.dto.DetallePersonaDTO;

@Repository
public interface DetallePersonaDAO {
    DetallePersonaDTO obtenerDetallePersona(int rutBeneficiario);
}
