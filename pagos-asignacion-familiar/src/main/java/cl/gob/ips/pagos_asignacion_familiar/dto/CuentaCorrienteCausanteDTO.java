package cl.gob.ips.pagos_asignacion_familiar.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class CuentaCorrienteCausanteDTO {
    private Long rutCausante;
    private Long rutBeneficiario;
    private Long periodo;
    private String nombre;
    private Long idBeneficio;
    private Double derecho;
    private Double pago;
    private Long diasReconocidos;
    private Integer tipoCausante;
    private Integer tipoBeneficio;
    private Timestamp fechaActualizacion;
    private Integer tipoBeneficiario;
    private Long idBenGenesys;
    private Integer causalExtincionId;
    private Timestamp fechaExtincion;
    private Timestamp fechaReconocimiento;
}