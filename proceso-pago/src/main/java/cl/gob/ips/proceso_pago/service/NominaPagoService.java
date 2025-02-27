package cl.gob.ips.proceso_pago.service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface NominaPagoService {
    void generateCsvResponse(HttpServletResponse response, int iIdProcesoIN, String fecPago) throws IOException;
}
