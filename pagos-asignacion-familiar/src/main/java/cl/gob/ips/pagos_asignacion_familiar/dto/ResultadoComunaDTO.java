package cl.gob.ips.pagos_asignacion_familiar.dto;

import lombok.Data;

@Data
public class ResultadoComunaDTO {
    private ListaComuna[] listaComunas;
    private String mensaje;
}