package cl.gob.ips.solicitudes_pago.dto;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LicenciaFiniquitoInputDTO {
    @NotNull
    private String tipo;
    private String nroLicencia;
    @NotNull
    private int rutBeneficiario;
    @NotNull
    private String dvBeneficiario;
    @NotNull
    private int rutEmpleado;
    @NotNull
    private String dvEmpleado;
    @NotNull
    private Date fechaInicio;
    @NotNull
    private Date fechaFin;
    @NotNull
    private int monto;
}
