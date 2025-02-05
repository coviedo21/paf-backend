package cl.gob.ips.solicitudes_pago.service;

import cl.gob.ips.solicitudes_pago.dto.LicenciaFiniquitoDTO;
import cl.gob.ips.solicitudes_pago.dto.LicenciaFiniquitoInputDTO;

public interface LicenciaFiniquitoService {
    LicenciaFiniquitoDTO obtenerLicenciaFiniquito(int rut);
    boolean agregarLicenciaFiniquito(LicenciaFiniquitoInputDTO licenciaFiniquito);
}
