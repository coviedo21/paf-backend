package cl.gob.ips.solicitudes_pago.service.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import cl.gob.ips.solicitudes_pago.dao.SolicitudPagoDAO;
import cl.gob.ips.solicitudes_pago.dto.CausanteSolicitudDTO;
import cl.gob.ips.solicitudes_pago.dto.CriterioSolicitudCausanteDTO;
import cl.gob.ips.solicitudes_pago.dto.CriterioSolicitudDTO;
import cl.gob.ips.solicitudes_pago.dto.EstadosSolicitudDTO;
import cl.gob.ips.solicitudes_pago.dto.MotivoRechazoDTO;
import cl.gob.ips.solicitudes_pago.dto.OrigenArchivoDTO;
import cl.gob.ips.solicitudes_pago.dto.RechazoSolicitudDTO;
import cl.gob.ips.solicitudes_pago.dto.ResolucionDTO;
import cl.gob.ips.solicitudes_pago.dto.ResponseDTO;
import cl.gob.ips.solicitudes_pago.dto.SolicitudDTO;
import cl.gob.ips.solicitudes_pago.dto.SolicitudProcesoDTO;
import cl.gob.ips.solicitudes_pago.dto.TipoSolicitanteDTO;
import cl.gob.ips.solicitudes_pago.service.CriterioSolicitudService;
import cl.gob.ips.solicitudes_pago.service.EmailService;
import cl.gob.ips.solicitudes_pago.service.SolicitudPagoService;

@Service
public class SolicitudPagoServiceImpl implements SolicitudPagoService {

    @Autowired
    private SolicitudPagoDAO solicitudPagoDAO;

    @Autowired
    private CriterioSolicitudService criterioSolicitudService;

    @Autowired
    private EmailService emailService;

    @Value("${solicitud.dias.antiguedad}")
    private int diasAntiguedad;  // Inyecta el valor del archivo de propiedades
    
    @Override
    public ResponseDTO insertarSolicitudPago(SolicitudDTO solicitudPago,boolean esArchivo) {
        ResponseDTO response = new ResponseDTO();
        response = solicitudPagoDAO.insertarSolicitudPago(solicitudPago,esArchivo);
        boolean enviar = false;

        if((int) response.getResultado()>0){
               enviar = criterioSolicitudService.validarCriteriosResolucion((int) response.getResultado(), esArchivo,false, solicitudPago.getEsPortuario());
            
               if(enviar){
                SolicitudDTO actualizarSolicitud = new SolicitudDTO();
                actualizarSolicitud.setIdSolicitud((int) response.getResultado());
                actualizarSolicitud.setCumpleCriterios("S");
                actualizarSolicitudPago(actualizarSolicitud);
                
                ResolucionDTO resolucion = new ResolucionDTO();
                resolucion.setIdSolicitud((int) response.getResultado());
                resolucion.setAutor(solicitudPago.getIdUsuario());
                resolucion.setIdEstado(2);
                resolucion.setVcDescripcion("Solicitud cumple criterios y es enviada automáticamente.");
                resolucion.setMotivoRechazo(null);
                resolucion.setUsuario("Sistema");
                insertarResolucion(resolucion);
                try {
                    if(!esArchivo){
                    	response.setGlosaRetorno("Solicitud Nº "+(int) response.getResultado()+" creada exitósamente. Puede ser asignada a un proceso de pago ya que cumple con todos los criterios de aceptación.");
                        emailService.enviarCorreo(solicitudPago.getEmail(),"Solicitud N°"+(int) response.getResultado()+" enviada","Su solicitud N° "+(int) response.getResultado()+" cumple con todos los criterios de aceptación por lo que ha sido enviada para su resolución.");    
                    }
                } catch (Exception e) {
                    // Captura cualquier excepción relacionada con el envío del correo y loguea el error
                    System.err.println("Error enviando correo para la solicitud " + (int) response.getResultado() + ": " + e.getMessage());
                }
            }
            else{
                try {
                    SolicitudDTO actualizarSolicitud = new SolicitudDTO();
                    actualizarSolicitud.setIdSolicitud((int) response.getResultado());
                    actualizarSolicitud.setCumpleCriterios("N");
                    actualizarSolicitudPago(actualizarSolicitud);
                    response.setGlosaRetorno("Solicitud Nº "+(int) response.getResultado()+" creada. No cumple con todos los criterios de aceptación.");
                    if(!esArchivo){
                    	emailService.enviarCorreo(solicitudPago.getEmail(),"Solicitud N°"+(int) response.getResultado()+" recibida con error(es)","Su solicitud N° "+(int) response.getResultado()+" NO cumple con todos los criterios de aceptación. A partir de este momento cuenta con "+diasAntiguedad+" días para subsanarla. Deberá adjuntar la documentación necesaria, de lo contrario será enviada automáticamente para su resolución");
                    }
                } catch (Exception e) {
                    // Captura cualquier excepción relacionada con el envío del correo y loguea el error
                    System.err.println("Error enviando correo para la solicitud " + (int) response.getResultado() + ": " + e.getMessage());
                }
            }
            return response;
        }
        return response;
    }

    @Override
    public List<SolicitudDTO> consultarSolicitudPago(Integer idSolicitud) {
        return solicitudPagoDAO.consultarSolicitudPago(idSolicitud);
    }

    @Override
    public boolean actualizarSolicitudPago(SolicitudDTO solicitudPago){
        if(solicitudPagoDAO.actualizarSolicitudPago(solicitudPago)){
            /*boolean enviar = criterioSolicitudService.validarCriteriosResolucion(solicitudPago.getIdSolicitud());
            if(enviar){
                //Hacer algo
            }
            else{
                //Hacer algo
            }*/
            return true;
        }
        return false;
    }

    @Override
    public List<SolicitudDTO> filtrarSolicitudesPago(String texto){
        return solicitudPagoDAO.filtrarSolicitudesPago(texto);    
    }

    public List<SolicitudProcesoDTO> obtenerSolicitudesPorProceso(int idProceso, int tipoSolicitante, Integer idEstado) {
        return solicitudPagoDAO.obtenerSolicitudesPorProceso(idProceso, tipoSolicitante, idEstado);
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

    public List<EstadosSolicitudDTO> obtenerEstadosSolicitud(){
        return solicitudPagoDAO.obtenerEstadosSolicitud();
    }

    public List<CausanteSolicitudDTO> obtenerCausantesPorSolicitud(int idSolicitud){
        return solicitudPagoDAO.obtenerCausantesPorSolicitud(idSolicitud);
    }

    public boolean eliminarCausanteSolicitud(int idCausanteSolicitud){
        return solicitudPagoDAO.eliminarCausanteSolicitud(idCausanteSolicitud);
    }

    public boolean actualizarCausanteSolicitud(CausanteSolicitudDTO causanteSolicitud){
        return solicitudPagoDAO.actualizarCausanteSolicitud(causanteSolicitud);
    }

    public List<SolicitudDTO> obtenerSolicitudesPorEstado(int idEstado){
        return solicitudPagoDAO.obtenerSolicitudesPorEstado(idEstado);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public List<SolicitudDTO> enviarSolicitudesAntiguas(){
        List<SolicitudDTO> solicitudesPago = new ArrayList<>();
        
        try {
        solicitudesPago = solicitudPagoDAO.obtenerSolicitudesAntiguas(diasAntiguedad);
        System.out.println("Se encontraron "+solicitudesPago.size()+" solicitudes con "+diasAntiguedad+" días de antigüedad");
        
            for(SolicitudDTO solicitud : solicitudesPago){
                ResolucionDTO resolucion = new ResolucionDTO();
                resolucion.setIdSolicitud(solicitud.getIdSolicitud());
                resolucion.setAutor(solicitud.getIdUsuario());
                resolucion.setIdEstado(7);
                resolucion.setVcDescripcion("Termina plazo para subsanar, solicitud no es enviada y debe ser revisada");
                resolucion.setMotivoRechazo(null);
                resolucion.setUsuario("Sistema");
                insertarResolucion(resolucion);
                try {
                    emailService.enviarCorreo(solicitud.getEmail(),"Solicitud "+solicitud.getIdSolicitud()+" enviada","Terminó plazo de "+diasAntiguedad+" días para subsanar su solicitud N° "+solicitud.getIdSolicitud()+". Será revisada.");    
                } catch (Exception e) {
                    // Captura cualquier excepción relacionada con el envío del correo y loguea el error
                    System.err.println("Error enviando correo para la solicitud " + solicitud.getIdSolicitud() + ": " + e.getMessage());
                }    
            }

        } catch (Exception e) {
            // Captura cualquier otra excepción que ocurra en el proceso general
            System.err.println("Error en el proceso de enviar solicitudes antiguas: " + e.getMessage());
        }
        return solicitudesPago;
    }

    public List<SolicitudDTO> obtenerSolicitudesPorBeneficiario(Integer rutBeneficiario){
        return solicitudPagoDAO.obtenerSolicitudesPorBeneficiario(rutBeneficiario);
    }
    
    public List<SolicitudDTO> obtenerSolicitudesPorEmpleador(Integer rutEmpleador){
        return solicitudPagoDAO.obtenerSolicitudesPorEmpleador(rutEmpleador);
    }

    public List<SolicitudDTO> obtenerSolicitudesPorOrigen(Integer origen, Integer proceso, Integer estado){
        return solicitudPagoDAO.obtenerSolicitudesPorOrigen(origen,proceso,estado);
    }

    public List<MotivoRechazoDTO> obtenerMotivosRechazo(){
        return solicitudPagoDAO.obtenerMotivosRechazo();
    }

    

    public boolean rechazarSolicitud(RechazoSolicitudDTO rechazoSolicitudDTO){
        SolicitudDTO solicitud = solicitudPagoDAO.consultarSolicitudPago(rechazoSolicitudDTO.getIdSolicitud()).get(0);
        MotivoRechazoDTO rechazo = solicitudPagoDAO.obtenerMotivoRechazoPorId(rechazoSolicitudDTO.getIdMotivoRechazo());
        ResolucionDTO resolucion = new ResolucionDTO();
        resolucion.setIdSolicitud(rechazoSolicitudDTO.getIdSolicitud());
        resolucion.setAutor(1);
        resolucion.setIdEstado(4);
        resolucion.setVcDescripcion("Se rechaza solicitud.");
        resolucion.setMotivoRechazo(rechazoSolicitudDTO.getIdMotivoRechazo());
        resolucion.setUsuario(rechazoSolicitudDTO.getUsuario());
        insertarResolucion(resolucion);
        try {
            emailService.enviarCorreo(solicitud.getEmail(),"Solicitud "+solicitud.getIdSolicitud()+" rechazada.","Su solicitud N° "+solicitud.getIdSolicitud()+" ha sido rechazada. Motivo de Rechazo:  "+rechazo.getNombre());    
            return true;
        } catch (Exception e) {
            // Captura cualquier excepción relacionada con el envío del correo y loguea el error
            System.err.println("Error enviando correo para la solicitud " + solicitud.getIdSolicitud() + ": " + e.getMessage());
        }
        return false;
    }
}