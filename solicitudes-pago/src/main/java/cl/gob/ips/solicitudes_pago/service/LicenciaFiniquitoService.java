package cl.gob.ips.solicitudes_pago.service;

import cl.gob.ips.solicitudes_pago.dto.LicenciaFiniquitoDTO;
import cl.gob.ips.solicitudes_pago.dto.LicenciaFiniquitoInputDTO;

import java.util.HashMap;
import java.util.List;

public interface LicenciaFiniquitoService {
    List<LicenciaFiniquitoDTO> obtenerLicenciaFiniquito(int rutBeneficiario, String fechaInicio, String fechaFin);
    HashMap<String, String> agregarLicenciaFiniquito(LicenciaFiniquitoInputDTO licenciaFiniquito);
}
