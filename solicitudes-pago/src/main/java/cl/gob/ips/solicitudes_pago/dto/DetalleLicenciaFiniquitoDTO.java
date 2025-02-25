package cl.gob.ips.solicitudes_pago.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetalleLicenciaFiniquitoDTO {

    private int iIdDetalleLicenFiniq;
    private int idLicFin;
    private Date dPeriodo;
    private int iMonto;
}
