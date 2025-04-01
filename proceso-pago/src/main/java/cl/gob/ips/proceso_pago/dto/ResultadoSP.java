package cl.gob.ips.proceso_pago.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResultadoSP {
    private int resultado;
    private String mensaje;
}