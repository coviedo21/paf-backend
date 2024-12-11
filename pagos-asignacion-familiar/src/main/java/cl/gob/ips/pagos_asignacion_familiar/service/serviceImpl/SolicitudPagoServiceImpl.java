package cl.gob.ips.pagos_asignacion_familiar.service.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.gob.ips.pagos_asignacion_familiar.dao.SolicitudPagoDAO;
import cl.gob.ips.pagos_asignacion_familiar.dto.CausanteSolicitudDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.ResolucionDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.SolicitudDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.TipoSolicitanteDTO;
import cl.gob.ips.pagos_asignacion_familiar.service.CriterioSolicitudService;
import cl.gob.ips.pagos_asignacion_familiar.service.SolicitudPagoService;

@Service
public class SolicitudPagoServiceImpl implements SolicitudPagoService {

    @Autowired
    private SolicitudPagoDAO solicitudPagoDAO;

    @Autowired
    private CriterioSolicitudService criterioSolicitudService;

    @Override
    public int insertarSolicitudPago(SolicitudDTO solicitudPago) {
        /*List<CuentaCorrienteCausanteDTO> listaCuentasCausante = cuentaCorrienteDAO.obtenerCuentaCorrienteCausantes(solicitudPago.getRutBeneficiario());
        for(CuentaCorrienteCausanteDTO cuentaCausante:listaCuentasCausante){
            System.out.println("Causante: "+cuentaCausante.getRutCausante()+" "+cuentaCausante.getNombre());    
        }*/
        int idSolicitud = solicitudPagoDAO.insertarSolicitudPago(solicitudPago);
        if(idSolicitud>0){
            /*Validar Criterios de Solicitud*/
            /*boolean enviar = criterioSolicitudService.validarCriteriosResolucion(idSolicitud);
            if(enviar){
                //cambiar estado de la solicitud a 'Enviada'
            }
            else{
                //Enviar correo correo para pedir documentos y comenzar contador de días limite para en envío de los documentos
            }*/
            return idSolicitud;
        }
        return 0;
    }

    @Override
    public List<SolicitudDTO> consultarSolicitudPago(Integer idSolicitud) {
        return solicitudPagoDAO.consultarSolicitudPago(idSolicitud);
    }

    @Override
    public boolean actualizarSolicitudPago(SolicitudDTO solicitudPago){
        if(solicitudPagoDAO.actualizarSolicitudPago(solicitudPago)){
            boolean enviar = criterioSolicitudService.validarCriteriosResolucion(solicitudPago.getIdSolicitud());
            if(enviar){
                //Hacer algo
            }
            else{
                //Hacer algo
            }
            return true;
        }
        return false;
    }

    @Override
    public List<SolicitudDTO> filtrarSolicitudesPago(String texto){
        return solicitudPagoDAO.filtrarSolicitudesPago(texto);    
    }

    public List<SolicitudDTO> obtenerSolicitudesPorProceso(int idProceso, int tipoSolicitante) {
        return solicitudPagoDAO.obtenerSolicitudesPorProceso(idProceso, tipoSolicitante);
    }

    public boolean actualizarIdProceso(int iIdSolicitud, int nuevoIdProceso){
        return solicitudPagoDAO.actualizarIdProceso(iIdSolicitud, nuevoIdProceso);
    }

    public int insertarResolucion(ResolucionDTO resolucionDTO){
        return solicitudPagoDAO.insertarResolucion(resolucionDTO);
    }

    public List<TipoSolicitanteDTO> obtenerTiposSolicitante(){
        return solicitudPagoDAO.obtenerTiposSolicitante();
    }

    public List<CausanteSolicitudDTO> obtenerCausantesPorSolicitud(int idSolicitud){
        return solicitudPagoDAO.obtenerCausantesPorSolicitud(idSolicitud);
    }

    public boolean eliminarCausanteSolicitud(int idCausanteSolicitud){
        return solicitudPagoDAO.eliminarCausanteSolicitud(idCausanteSolicitud);
    }
}