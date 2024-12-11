package cl.gob.ips.cuenta_bancaria.dto;

import lombok.Data;

@Data
public class BancoTipoCuentaDTO {
    private int idBanco;
    private String nombreBanco;
    private int idTipoCuenta;
    private String nombreTipoCuenta;
}