package cl.gob.ips.proceso_pago.service.serviceImpl;

import cl.gob.ips.proceso_pago.dao.CtactePafDAO;
import cl.gob.ips.proceso_pago.dto.CtactePafDTO;
import cl.gob.ips.proceso_pago.dto.ProcesoResponse;
import cl.gob.ips.proceso_pago.dto.ResultadoSP;
import cl.gob.ips.proceso_pago.service.CtaCtePafService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CtactePafServiceImpl implements CtaCtePafService {

    private static final Logger log = LoggerFactory.getLogger(CtactePafServiceImpl.class);
    @Autowired
    CtactePafDAO ctactePafDAO;

    @Override
    public ProcesoResponse procesarYGuardarDatos(Integer idProcesoPago) {
        // Validación básica
        if (idProcesoPago == null || idProcesoPago <= 0) {
            return new ProcesoResponse(0, "ID de proceso inválido");
        }

        try {
            Map<String, Object> resultado = ctactePafDAO.procesarYGuardar(idProcesoPago);
            return new ProcesoResponse(
                    ((Number) resultado.get("p_nResultado")).intValue(),
                    (String) resultado.get("p_vcMensaje")
            );
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ProcesoResponse(0, "Error en el proceso: " + e.getMessage());
        }
    }

    @Override
    public List<CtactePafDTO> obtenerDatosProceso(Integer idProcesoPago) {
        if (idProcesoPago == null) {
            throw new IllegalArgumentException("ID de proceso no puede ser nulo");
        }
        return ctactePafDAO.obtenerDatosDesdeFuncion(idProcesoPago);
    }

    @Override
    public ResultadoSP ejecutarInsercionManual(CtactePafDTO registro) {
        // Validar datos obligatorios
        if (registro.getNPagoPAFID() == null || registro.getNMesRemuneracion() == null) {
            throw new IllegalArgumentException("Campos obligatorios faltantes");
        }

        Map<String, Object> resultado = ctactePafDAO.insertarRegistro(registro);
        return new ResultadoSP(
                ((Number) resultado.get("p_nResultado")).intValue(),
                (String) resultado.get("p_vcMensaje")
        );
    }
}
