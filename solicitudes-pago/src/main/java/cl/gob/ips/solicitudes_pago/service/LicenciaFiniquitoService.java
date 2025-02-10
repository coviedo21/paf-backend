package cl.gob.ips.solicitudes_pago.service;

import cl.gob.ips.solicitudes_pago.dto.LicenciaFiniquitoDTO;
import cl.gob.ips.solicitudes_pago.dto.LicenciaFiniquitoInputDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface LicenciaFiniquitoService {
    List<LicenciaFiniquitoDTO> obtenerLicenciaFiniquito(int rutBeneficiario, String nroLicencia, String fechaInicio, String fechaFin);
    HashMap<String, String> agregarLicenciaFiniquito(LicenciaFiniquitoInputDTO licenciaFiniquito);
}
