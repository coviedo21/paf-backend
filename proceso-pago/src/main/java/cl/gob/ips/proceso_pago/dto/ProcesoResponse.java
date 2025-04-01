package cl.gob.ips.proceso_pago.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProcesoResponse {
    private int codigoResultado;
    private String mensaje;
}