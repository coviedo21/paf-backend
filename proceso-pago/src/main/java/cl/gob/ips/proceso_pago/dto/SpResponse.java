package cl.gob.ips.proceso_pago.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpResponse {
    private int resultado;
    private String mensaje;
}
