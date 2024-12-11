package cl.gob.ips.pagos_asignacion_familiar.service.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.gob.ips.pagos_asignacion_familiar.dao.ProcesoDAO;
import cl.gob.ips.pagos_asignacion_familiar.dto.ProcesoDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.DatosProcesoPorTipoDTO;
import cl.gob.ips.pagos_asignacion_familiar.service.ProcesoService;

@Service
public class ProcesoServiceImpl implements ProcesoService {
    @Autowired
    private ProcesoDAO procesoDAO;

    @Override
    public int crearProceso(ProcesoDTO insertarProcesoDTO) {
        return procesoDAO.insertarProceso(insertarProcesoDTO);
    }

    @Override
    public List<ProcesoDTO> consultarProceso(Long idProceso) {
        return procesoDAO.consultarProceso(idProceso);
    }

    @Override
    public List<ProcesoDTO> filtrarProcesos(String texto) {
        return procesoDAO.filtrarProcesos(texto);
    }

    @Override
    public List<DatosProcesoPorTipoDTO> obtenerDatosProcesoPorTipo(int idProceso){
        return procesoDAO.obtenerDatosProcesoPorTipo(idProceso);
    }

    @Override
    public boolean actualizarEstadoProceso(int idProceso, int idEstadoProceso){
        return procesoDAO.actualizarEstadoProceso(idProceso, idEstadoProceso);
    }
}