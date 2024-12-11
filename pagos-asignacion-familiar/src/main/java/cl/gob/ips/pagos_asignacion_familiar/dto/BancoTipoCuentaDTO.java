package cl.gob.ips.pagos_asignacion_familiar.dto;

import lombok.Data;

@Data
public class BancoTipoCuentaDTO {
    private int idBanco;
    private String nombreBanco;
    private int idTipoCuenta;
    private String nombreTipoCuenta;
}