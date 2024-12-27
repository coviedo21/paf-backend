package cl.gob.ips.mantenedor_comunas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArchivoSolicitudDTO {
    private String comunaIngresada;
    private String idComunaOriginal;
    private String nombreComunaOriginal;
}
