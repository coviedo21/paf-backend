package cl.gob.ips.proceso_pago.service;

import java.util.List;
import cl.gob.ips.proceso_pago.dto.ProcesoDTO;
import cl.gob.ips.proceso_pago.dto.DatosProcesoPorTipoDTO;

public interface ProcesoService {
    public int crearProceso(ProcesoDTO insertarProcesoDTO);

    public List<ProcesoDTO> consultarProceso(Long idProceso);

    public List<ProcesoDTO> filtrarProcesos(String texto);

    List<DatosProcesoPorTipoDTO> obtenerDatosProcesoPorTipo(int idProceso);

    public boolean actualizarEstadoProceso(int idProceso, int idEstadoProceso);
}
