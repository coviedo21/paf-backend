package cl.gob.ips.proceso_pago.service.serviceImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import cl.gob.ips.proceso_pago.dao.ProcesoDAO;
import cl.gob.ips.proceso_pago.dto.ProcesoDTO;
import cl.gob.ips.proceso_pago.dto.DatosProcesoPorTipoDTO;
import cl.gob.ips.proceso_pago.service.ProcesoService;

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

    //@Scheduled(cron = "0 0 0 * * ?")
    @Scheduled(cron = "0 49 14 * * ?")
    public List<ProcesoDTO> iniciarProceso() {
        List<ProcesoDTO> listaProcesos = new ArrayList<>();
        System.out.println("Inicia Daemon");
        try {
            listaProcesos = procesoDAO.consultarProceso(null);
            //System.out.println("Se encontró " + solicitudesPago.size() + " solicitudes con " + diasAntiguedad + " días de antigüedad");

            for (ProcesoDTO proceso : listaProcesos) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                
                try{
                // Formateamos ambas fechas a solo año-mes-día
                System.out.println("Proceso "+proceso.getIdProceso()+" fecha "+proceso.getFechaEjecucion());
                String fechaFormateada = sdf.format(proceso.getFechaEjecucion());
                String fechaActualFormateada = sdf.format(new Date());
                
                // Comparamos si la fecha es menor o igual a la fecha actual
                if(fechaFormateada.compareTo(fechaActualFormateada) <= 0){
                    if(proceso.getIdEstado()==3){
                        //Se cumplió la fecha, se debe ejecutar el proceso
                        System.out.println("A ejecutar el proceso "+proceso.getIdProceso()+", tiene fecha "+proceso.getFechaEjecucion());
                    }
                }
            }catch(Exception e){
                System.err.println("Error con fechas");    
            }
                
            }

        } catch (Exception e) {
            // Captura cualquier otra excepción que ocurra en el proceso general
            System.err.println("Error al obtener procesos");
        }
        return listaProcesos;
    }
    
}