package cl.gob.ips.pagos_asignacion_familiar.dao;

import java.util.List;

import org.springframework.stereotype.Component;

import cl.gob.ips.pagos_asignacion_familiar.dto.ProcesoDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.ProcesoTipoSolicitanteDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.DatosProcesoPorTipoDTO;

@Component
public interface ProcesoDAO {
    int insertarProceso(ProcesoDTO insertarProcesoDTO);
    List<ProcesoDTO> consultarProceso(Long idProceso);
    List<ProcesoDTO> filtrarProcesos(String texto);
    List<DatosProcesoPorTipoDTO> obtenerDatosProcesoPorTipo(int idProceso);
    boolean actualizarEstadoProceso(int idProceso, int idEstadoProceso);
    void insertarProcesoTipoSolicitante(int idProceso, List<Integer> listaTipoSolicitante);
}