package cl.gob.ips.solicitudes_pago.service.serviceImpl;

import cl.gob.ips.solicitudes_pago.dao.LicenciaFiniquitoDAO;
import cl.gob.ips.solicitudes_pago.dto.LicenciaFiniquitoDTO;
import cl.gob.ips.solicitudes_pago.dto.LicenciaFiniquitoInputDTO;
import cl.gob.ips.solicitudes_pago.service.LicenciaFiniquitoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LicenciaFiniquitoServiceImpl implements LicenciaFiniquitoService {

    @Autowired
    private LicenciaFiniquitoDAO licenciaFiniquitoDAO;

    @Override
    public LicenciaFiniquitoDTO obtenerLicenciaFiniquito(int rut) {
        return licenciaFiniquitoDAO.obtenerLicenciaFiniquito(rut);
    }

    @Override
    public boolean agregarLicenciaFiniquito(LicenciaFiniquitoInputDTO licenciaFiniquito) {
        int response = licenciaFiniquitoDAO.agregarLicenciaFiniquito(licenciaFiniquito);

        return response == 1;
    }
}
