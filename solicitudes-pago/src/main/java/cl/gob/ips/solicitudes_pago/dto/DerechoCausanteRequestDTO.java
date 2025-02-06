package cl.gob.ips.solicitudes_pago.dto;

import lombok.Data;

@Data
public class DerechoCausanteRequestDTO {
    private String rutCausante;
    private String rutBeneficiario;
    private String periodoDesde;
    private String periodoHasta;
    private String tipoCausante;
}
