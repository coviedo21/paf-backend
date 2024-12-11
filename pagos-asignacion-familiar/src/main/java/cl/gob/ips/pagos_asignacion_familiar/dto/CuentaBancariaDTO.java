package cl.gob.ips.pagos_asignacion_familiar.dto;
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
    private int tipoCuenta;
    private int estadoCuenta;
    private int origen;
}