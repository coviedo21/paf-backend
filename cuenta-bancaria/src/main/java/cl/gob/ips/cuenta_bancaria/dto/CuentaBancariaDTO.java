package cl.gob.ips.cuenta_bancaria.dto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuentaBancariaDTO {
    private int idCuentaBancaria;
    private String numeroCuenta;
    private int rutTitular;
    private String dvTitular;
    private int codigoBanco;
    private String nombreBanco;
    private int tipoCuenta;
    private String nombreTipoCuenta;
    private int estadoCuenta;
    private int origen;
}