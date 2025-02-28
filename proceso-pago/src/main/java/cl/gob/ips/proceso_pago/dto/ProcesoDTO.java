package cl.gob.ips.proceso_pago.dto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcesoDTO {
    private int idProceso;
    private int idUsuario;
    private String nombreUsuario;
    private BigDecimal pagosTotales;
    private BigDecimal aprobados;
    private BigDecimal rechazados;
    private int idEstado;
    private String nombreEstado;
    private Date fechaCreacion;
    private Date fechaEjecucion;
    private String periodo;
    private String nombresTiposSolicitantes;
    private List<Integer> listaTipoSolicitante;
}