package cl.gob.ips.solicitudes_pago.dao;

import cl.gob.ips.solicitudes_pago.dto.LicenciaFiniquitoDTO;
import cl.gob.ips.solicitudes_pago.dto.LicenciaFiniquitoInputDTO;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public interface LicenciaFiniquitoDAO {
    List<LicenciaFiniquitoDTO> obtenerLicenciaFiniquito(int rutBeneficiario, String fechaInicio, String fechaFin);
    HashMap<String, String> agregarLicenciaFiniquito(LicenciaFiniquitoInputDTO licenciaFiniquito);
}
