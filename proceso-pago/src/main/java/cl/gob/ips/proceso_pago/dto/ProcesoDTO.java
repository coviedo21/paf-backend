package cl.gob.ips.proceso_pago.dto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcesoDTO {
    private int idProceso;
    private int idUsuario;
    private String nombreUsuario;
    private int pagosTotales;
    private int aprobados;
    private int rechazados;
    private int idEstado;
    private String nombreEstado;
    private Date fechaCreacion;
    private Date fechaEjecucion;
    private String periodo;
    private List<Integer> listaTipoSolicitante;
}