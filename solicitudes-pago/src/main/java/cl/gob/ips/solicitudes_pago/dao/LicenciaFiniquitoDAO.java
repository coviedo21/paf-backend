package cl.gob.ips.solicitudes_pago.dao;

import cl.gob.ips.solicitudes_pago.dto.LicenciaFiniquitoDTO;
import org.springframework.stereotype.Repository;

@Repository
public interface LicenciaFiniquitoDAO {
    LicenciaFiniquitoDTO obtenerLicenciaFiniquito(int rut);
}
