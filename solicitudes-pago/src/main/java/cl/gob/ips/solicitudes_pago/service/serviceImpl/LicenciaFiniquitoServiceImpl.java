package cl.gob.ips.solicitudes_pago.service.serviceImpl;

import cl.gob.ips.solicitudes_pago.dao.LicenciaFiniquitoDAO;
import cl.gob.ips.solicitudes_pago.dto.LicenciaFiniquitoDTO;
import cl.gob.ips.solicitudes_pago.dto.LicenciaFiniquitoInputDTO;
import cl.gob.ips.solicitudes_pago.service.LicenciaFiniquitoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class LicenciaFiniquitoServiceImpl implements LicenciaFiniquitoService {

    @Autowired
    private LicenciaFiniquitoDAO licenciaFiniquitoDAO;


    public List<LicenciaFiniquitoDTO> obtenerLicenciaFiniquito(int rutBeneficiario, String nroLicencia, String fechaInicio, String fechaFin) {
        return licenciaFiniquitoDAO.obtenerLicenciaFiniquito(rutBeneficiario, nroLicencia, fechaInicio, fechaFin);
    }

    @Override
    public HashMap<String, String> agregarLicenciaFiniquito(LicenciaFiniquitoInputDTO licenciaFiniquito) {
        return licenciaFiniquitoDAO.agregarLicenciaFiniquito(licenciaFiniquito);
    }
}
