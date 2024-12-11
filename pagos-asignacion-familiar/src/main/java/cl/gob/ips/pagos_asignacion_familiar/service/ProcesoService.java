package cl.gob.ips.pagos_asignacion_familiar.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cl.gob.ips.pagos_asignacion_familiar.dto.ProcesoDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.DatosProcesoPorTipoDTO;

@Service
public interface ProcesoService {
    public int crearProceso(ProcesoDTO insertarProcesoDTO);

    public List<ProcesoDTO> consultarProceso(Long idProceso);

    public List<ProcesoDTO> filtrarProcesos(String texto);

    List<DatosProcesoPorTipoDTO> obtenerDatosProcesoPorTipo(int idProceso);

    public boolean actualizarEstadoProceso(int idProceso, int idEstadoProceso);
}
