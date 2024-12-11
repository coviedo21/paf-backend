package cl.gob.ips.pagos_asignacion_familiar.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import cl.gob.ips.pagos_asignacion_familiar.dto.CuentaCorrienteCausanteDTO;

@Repository
public interface CuentaCorrienteDAO {
    List<CuentaCorrienteCausanteDTO> obtenerCuentaCorrienteCausantes(int rutBeneficiario);
}
