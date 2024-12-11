package cl.gob.ips.proceso_pago.dao;

import java.util.List;
import cl.gob.ips.proceso_pago.dto.ProcesoDTO;
import cl.gob.ips.proceso_pago.dto.DatosProcesoPorTipoDTO;
public interface ProcesoDAO {
    int insertarProceso(ProcesoDTO insertarProcesoDTO);
    List<ProcesoDTO> consultarProceso(Long idProceso);
    List<ProcesoDTO> filtrarProcesos(String texto);
    List<DatosProcesoPorTipoDTO> obtenerDatosProcesoPorTipo(int idProceso);
    boolean actualizarEstadoProceso(int idProceso, int idEstadoProceso);
    void insertarProcesoTipoSolicitante(int idProceso, List<Integer> listaTipoSolicitante);
}