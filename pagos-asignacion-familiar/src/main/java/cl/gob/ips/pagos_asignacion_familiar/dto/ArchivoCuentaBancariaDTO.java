package cl.gob.ips.pagos_asignacion_familiar.dto;

import lombok.Data;

@Data
public class ArchivoCuentaBancariaDTO {
    private String numeroCuenta;
    private String rutTitular;
    private String dvTitular;
    private String codigoBanco;
    private String tipoCuenta;
    private String estadoCuenta;
}