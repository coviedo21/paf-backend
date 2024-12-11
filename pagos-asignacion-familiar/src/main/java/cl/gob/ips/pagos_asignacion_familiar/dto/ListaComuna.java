package cl.gob.ips.pagos_asignacion_familiar.dto;

import lombok.Data;

@Data
public class ListaComuna {
    private int codRegion;
    private int codProvincia;
    private int codComuna;
    private String nombreComuna;
}