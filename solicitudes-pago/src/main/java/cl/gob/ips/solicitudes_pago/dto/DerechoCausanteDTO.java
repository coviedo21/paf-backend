package cl.gob.ips.solicitudes_pago.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class DerechoCausanteDTO {
    //private Integer numeroCuentaCorriente;
    private Integer rutBeneficiario;
    //private String dvBeneficiario;
    private Integer periodo;
    private Integer tipoMovimiento;
    private String entradaSalida;
    private String fechaMovimiento;
    private BigDecimal montoMovimiento;
    //private Integer rutCausante;
    //private String dvCausante;
    private Integer tipoBeneficiario;
    private BigDecimal rentaPromedio;
    private Integer codigoTramo;
    private Integer diasReconocimiento;
}
