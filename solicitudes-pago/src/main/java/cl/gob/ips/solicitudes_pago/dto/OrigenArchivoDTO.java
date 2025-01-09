package cl.gob.ips.solicitudes_pago.dto;

import lombok.Data;

@Data
public class OrigenArchivoDTO {
    private int idOrigen;
    private String nombre;
    private int estadoOrigen;
}