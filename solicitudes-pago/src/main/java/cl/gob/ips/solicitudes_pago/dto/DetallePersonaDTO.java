package cl.gob.ips.solicitudes_pago.dto;

import lombok.Data;

@Data
public class DetallePersonaDTO {
    private String rutBeneficiario;
    private String rutCausante;
    private String nombresBeneficiario;
    private String apaternoBeneficiario;
    private String amaternoBeneficiario;
    private String nombresCausante;
    private String apaternoCausante;
    private String amaternoCausante;
    private String tipoBeneficiario;
    private String tipoCausante;
}
