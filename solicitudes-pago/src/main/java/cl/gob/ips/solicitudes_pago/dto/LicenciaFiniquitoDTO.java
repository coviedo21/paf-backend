package cl.gob.ips.solicitudes_pago.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LicenciaFiniquitoDTO {
    private int idLicFin;
    private int tipo;
    private String nroLicencia;
    private int rutBeneficiario;
    private String dvBeneficiario;
    private int rutEmpleado;
    private String dvEmpleado;
    private Date fechaInicio;
    private Date fechaFin;
    private int monto;
}
