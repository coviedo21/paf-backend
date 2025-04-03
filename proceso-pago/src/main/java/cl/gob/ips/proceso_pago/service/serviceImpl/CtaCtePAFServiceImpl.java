package cl.gob.ips.proceso_pago.service.serviceImpl;

import cl.gob.ips.proceso_pago.dao.CtaCtePAFDAO;
import cl.gob.ips.proceso_pago.dto.CtaCteDTO;
import cl.gob.ips.proceso_pago.dto.SpResponse;
import cl.gob.ips.proceso_pago.service.CtaCtePAFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CtaCtePAFServiceImpl implements CtaCtePAFService {

    @Autowired
    private CtaCtePAFDAO ctaCtePAFDAO;

    @Transactional
    public SpResponse procesarDatosCtaCtePAF(int IdProceso) {
        // Obtener datos desde la capa DAO
        List<CtaCteDTO> datosList = ctaCtePAFDAO.obtenerDatosCtaCtePAF(IdProceso);
        // Insertar los datos obtenidos
        return ctaCtePAFDAO.insertTBLCTACTEPAF(datosList);
    }

    @Override
    public SpResponse procesarDatosCtaCte(int IdProceso) {
        SpResponse response = new SpResponse();

        List<CtaCteDTO> datosList = ctaCtePAFDAO.selectTBLCTACTEPAF(IdProceso);
        response = ctaCtePAFDAO.insertarTBLCTACTEPAF(datosList);
        ctaCtePAFDAO.eliminarRegistrosTBLCTACTEPAF(IdProceso);
        return response;
    }
}