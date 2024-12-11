package cl.gob.ips.mantenedor_comunas.dto;

import lombok.Data;

@Data
public class ComunaMantenedorDTO {
    private Integer idCoincidencia;
    private String comunaIngresada;
    private Integer idComunaOriginal;
    private String nombreComunaOriginal;
    private String esCoincidencia;
    private String fechaInsercion;
}