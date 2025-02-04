package cl.gob.ips.solicitudes_pago.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LicenciaFiniquitoDTO {
    private int idLicFin;
    private String tipoInformacion;
    private int nroLicenciaFiniquito;
    private int rutBeneficiario;
    private String dvBneficiario;
    private int rutEmpleado;
    private String dvEmpleado;
    private Date fechaInicio;
    private Date fechaFin;
}
