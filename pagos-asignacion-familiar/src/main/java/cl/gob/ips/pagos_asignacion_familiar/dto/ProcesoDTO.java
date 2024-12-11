package cl.gob.ips.pagos_asignacion_familiar.dto;
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
    private String mesProceso;
    private int anoProceso;
    private int idUsuario;
    private String nombreUsuario;
    private int pagosTotales;
    private int aprobados;
    private int rechazados;
    private int periodo;
    private int idEstado;
    private Date fechaCreacion;
    private Date fechaPago;
    private List<Integer> listaTipoSolicitante;
}