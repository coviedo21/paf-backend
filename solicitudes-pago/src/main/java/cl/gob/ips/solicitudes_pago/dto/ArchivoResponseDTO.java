package cl.gob.ips.solicitudes_pago.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ArchivoResponseDTO {
    private int registrosEnArchivo;
    private int registrosImportados;
    private int registrosFallidos;
}
