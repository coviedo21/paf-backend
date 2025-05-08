package cl.gob.ips.proceso_pago.service.serviceImpl;

import cl.gob.ips.proceso_pago.dao.CtaCtePAFDAO;
import cl.gob.ips.proceso_pago.dto.CtaCteDTO;
import cl.gob.ips.proceso_pago.dto.SpResponse;
import cl.gob.ips.proceso_pago.service.CtaCtePAFService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CtaCtePAFServiceImpl implements CtaCtePAFService {

	private static final Logger logger = LoggerFactory.getLogger(CtaCtePAFServiceImpl.class);
	
    @Autowired
    private CtaCtePAFDAO ctaCtePAFDAO;

    @Transactional
    public SpResponse procesarDatosCtaCtePAF(int IdProceso) {
        return ctaCtePAFDAO.copyDataToTBLCTACTEPAF(IdProceso);
    }

    @Override
    public SpResponse procesarDatosCtaCte(int IdProceso) {
        SpResponse response = new SpResponse();
        logger.error("1 - iniciando proceso");
        List<CtaCteDTO> datosList = ctaCtePAFDAO.selectTBLCTACTEPAF(IdProceso);
        logger.error("2 - Se obtuvieron registros de paf");
            response = ctaCtePAFDAO.insertarTBLCTACTEPAF(datosList);

            if (response.getResultado() == 1) {
                ctaCtePAFDAO.actualizarEstadoTBLCTACTEPAF(IdProceso);
            } else {
                response.setResultado(0);
                response.setMensaje("Error al insertar datos en TBLCTACTEPAF : " + response.getMensaje());
            }
        ctaCtePAFDAO.eliminarRegistrosTBLCTACTEPAF(IdProceso);
        return response;
    }
}