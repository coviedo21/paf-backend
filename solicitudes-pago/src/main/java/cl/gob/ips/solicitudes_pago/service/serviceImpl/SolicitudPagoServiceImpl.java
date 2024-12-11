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
import cl.gob.ips.solicitudes_pago.dto.ResolucionDTO;
import cl.gob.ips.solicitudes_pago.dto.SolicitudDTO;
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
    public int insertarSolicitudPago(SolicitudDTO solicitudPago) {
        /*List<CuentaCorrienteCausanteDTO> listaCuentasCausante = cuentaCorrienteDAO.obtenerCuentaCorrienteCausantes(solicitudPago.getRutBeneficiario());
        for(CuentaCorrienteCausanteDTO cuentaCausante:listaCuentasCausante){
            System.out.println("Causante: "+cuentaCausante.getRutCausante()+" "+cuentaCausante.getNombre());    
        }*/
        int idSolicitud = solicitudPagoDAO.insertarSolicitudPago(solicitudPago);
        if(idSolicitud>0){
            /*Validar Criterios de Solicitud*/
            boolean enviar = criterioSolicitudService.validarCriteriosResolucion(idSolicitud);
            if(enviar){
                SolicitudDTO actualizarSolicitud = new SolicitudDTO();
                actualizarSolicitud.setIdSolicitud(idSolicitud);
                actualizarSolicitud.setCumpleCriterios("S");
                actualizarSolicitudPago(actualizarSolicitud);
                /*
                //cambiar estado de la solicitud a 'Enviada'
                ResolucionDTO resolucion = new ResolucionDTO();
                resolucion.setIIdSolicitud(idSolicitud);
                resolucion.setIIdEstado(2);
                resolucion.setIAutor(solicitudPago.getIdUsuario());
                resolucion.setVcDescripcion("Solicitud cumple con todos los criterios de resolución.");
                solicitudPagoDAO.insertarResolucion(resolucion);*/
                try {
                    emailService.enviarCorreo(solicitudPago.getEmail(),"Solicitud N°"+idSolicitud+" enviada","Su solicitud N° "+idSolicitud+" cumple con todos los criterios de aceptación por lo que ha sido enviada para su resolución.");    
                } catch (Exception e) {
                    // Captura cualquier excepción relacionada con el envío del correo y loguea el error
                    System.err.println("Error enviando correo para la solicitud " + idSolicitud + ": " + e.getMessage());
                }
            }
            else{
                try {
                    SolicitudDTO actualizarSolicitud = new SolicitudDTO();
                    actualizarSolicitud.setIdSolicitud(idSolicitud);
                    actualizarSolicitud.setCumpleCriterios("N");
                    actualizarSolicitudPago(actualizarSolicitud);
                    emailService.enviarCorreo(solicitudPago.getEmail(),"Solicitud N°"+idSolicitud+" recibida con error(es)","Su solicitud N° "+idSolicitud+" NO cumple con todos los criterios de aceptación. A partir de este momento cuenta con "+diasAntiguedad+" días para subsanarla. Deberá adjuntar la documentación necesaria, de lo contrario será enviada automáticamente para su resolución");    
                } catch (Exception e) {
                    // Captura cualquier excepción relacionada con el envío del correo y loguea el error
                    System.err.println("Error enviando correo para la solicitud " + idSolicitud + ": " + e.getMessage());
                }
            }
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

    public boolean actualizarCausanteSolicitud(CausanteSolicitudDTO causanteSolicitud){
        return solicitudPagoDAO.actualizarCausanteSolicitud(causanteSolicitud);
    }

    public List<SolicitudDTO> obtenerSolicitudesPorEstado(int idEstado){
        return solicitudPagoDAO.obtenerSolicitudesPorEstado(idEstado);
    }

    @Scheduled(cron = "0 * * * * ?")
    public List<SolicitudDTO> enviarSolicitudesAntiguas(){
        List<SolicitudDTO> solicitudesPago = new ArrayList<>();
        
        try {
        solicitudesPago = solicitudPagoDAO.obtenerSolicitudesAntiguas(diasAntiguedad);
        System.out.println("Se encontraron "+solicitudesPago.size()+" solicitudes con "+diasAntiguedad+" días de antigüedad");
        
            for(SolicitudDTO solicitud : solicitudesPago){
                ResolucionDTO resolucion = new ResolucionDTO();
                resolucion.setIIdSolicitud(solicitud.getIdSolicitud());
                resolucion.setIAutor(solicitud.getIdUsuario());
                resolucion.setIIdEstado(2);
                resolucion.setVcDescripcion("Termina plazo para subsanar, solicitud enviada automáticamente");
                insertarResolucion(resolucion);
                try {
                    emailService.enviarCorreo(solicitud.getEmail(),"Solicitud "+solicitud.getIdSolicitud()+" enviada","Terminó plazo de "+diasAntiguedad+" días para subsanar su solicitud N° "+solicitud.getIdSolicitud()+". Ha sido enviada automáticamente para su resolución.");    
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

}