package cl.gob.ips.proceso_pago.dao;

import cl.gob.ips.proceso_pago.dto.NominaPagoDTO;

import java.util.List;

public interface NominaPagoDAO {
    List<NominaPagoDTO> ejecutarProcedimientoAlmacenado(int iIdProcesoIN, String fecPago);
}

