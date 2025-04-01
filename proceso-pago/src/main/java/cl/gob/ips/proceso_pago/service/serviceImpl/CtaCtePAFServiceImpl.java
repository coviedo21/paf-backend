package cl.gob.ips.proceso_pago.service.serviceImpl;

import cl.gob.ips.proceso_pago.dao.CtaCtePAFDAO;
import cl.gob.ips.proceso_pago.dto.CtaCtePAFDTO;
import cl.gob.ips.proceso_pago.dto.SpResponse;
import cl.gob.ips.proceso_pago.service.CtaCtePAFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CtaCtePAFServiceImpl implements CtaCtePAFService {

    private final CtaCtePAFDAO ctaCteDAO;

    @Autowired
    public CtaCtePAFServiceImpl(CtaCtePAFDAO ctaCteDAO) {
        this.ctaCteDAO = ctaCteDAO;
    }

    @Transactional
    public SpResponse copiaCteCtePAF(int iIdProcesoPago) {
        // Lógica transaccional: Si hay error, se hace rollback
        return ctaCteDAO.procesarYInsertar(iIdProcesoPago);
    }
}