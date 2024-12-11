package cl.gob.ips.solicitudes_pago.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CausanteDTO {
    private String nombreBeneficiario;
    private String rutBeneficiario;
    private String nombreCausante;
    private String rutCausante;
    private String tipoCausante;
    private BigDecimal idTipoCausante;
    private String fechaNacimientoBeneficiario;
    private String fechaNacimientoCausante;
    private boolean causanteMayorDeEdad;
}