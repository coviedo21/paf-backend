package cl.gob.ips.solicitudes_pago.controller;

import cl.gob.ips.solicitudes_pago.dto.*;
import cl.gob.ips.solicitudes_pago.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequestMapping("/solicitudPago")
public class SolicitudesPagosController {

    
    @Autowired
    private CriterioSolicitudService criterioSolicitudService;

    @Autowired
    private SolicitudPagoService solicitudPagoService;

    @Autowired
    private AuditoriaService auditoriaService;

    @Autowired
    private CausanteService causanteService;
 
    @Autowired
    private UtilService utilService;

    @Autowired
    private PersonaService personaService;

    @GetMapping("/obtenerCriterio/{id}")
    public ResponseEntity<List<CriterioSolicitudDTO>> consultarCriterio(@PathVariable("id") Integer id) {
        List<CriterioSolicitudDTO> criterios = criterioSolicitudService.consultarCriteriosSolicitud(id);
        if (criterios != null && !criterios.isEmpty()) {
            return ResponseEntity.ok(criterios);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/obtenerCriteriosCausante/{idCausanteSolicitud}")
    public ResponseEntity<List<CriterioSolicitudCausanteDTO>> consultarCriteriosCausante(@PathVariable("idCausanteSolicitud") Integer idCausanteSolicitud) {
        List<CriterioSolicitudCausanteDTO> criterios = criterioSolicitudService.consultarCriteriosCausante(idCausanteSolicitud);
        if (criterios != null && !criterios.isEmpty()) {
            return ResponseEntity.ok(criterios);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("/crear-solicitud")
    public ResponseEntity<ResponseDTO> insertarSolicitudPago(@RequestBody SolicitudDTO solicitudPago) {
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setTimestamp(new Date());

        if(solicitudPago.getListaCausantes().isEmpty()){
            responseDTO.setCodigoRetorno(-1);
            responseDTO.setGlosaRetorno("Debe ingresar al menos un causante");
            responseDTO.setTimestamp(new Date());

            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
        responseDTO = solicitudPagoService.insertarSolicitudPago(solicitudPago,false);
        if ((int) responseDTO.getResultado()>0) {
            //responseDTO.setCodigoRetorno(0);
            responseDTO.setGlosaRetorno("Solicitud de pago insertada correctamente!");
            //responseDTO.setResultado(resultado);

            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        } else {
            responseDTO.setCodigoRetorno(-1);
            
            responseDTO.setGlosaRetorno(responseDTO.getGlosaRetorno());
        
            
            responseDTO.setTimestamp(new Date());

            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/obtenerSolicitud")
    public ResponseEntity<List<SolicitudDTO>> consultarSolicitudesPago(@RequestParam(value = "id", required = false) Integer id) {
        List<SolicitudDTO> solicitudesPago = solicitudPagoService.consultarSolicitudPago(id);
        if (solicitudesPago != null && !solicitudesPago.isEmpty()) {
            return ResponseEntity.ok(solicitudesPago);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/obtenerSolicitudPorId/{id}")
    public ResponseEntity<List<SolicitudDTO>> consultarSolicitudesPago(@PathVariable("id") int id) {
        List<SolicitudDTO> solicitudesPago = solicitudPagoService.consultarSolicitudPago(id);
        if (solicitudesPago != null && !solicitudesPago.isEmpty()) {
            return ResponseEntity.ok(solicitudesPago);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/filtrarSolicitudes")
    public ResponseEntity<List<SolicitudDTO>> filtrarSolicitudesPago(@RequestParam(value = "filtro", required = true) String filtro) {
        List<SolicitudDTO> solicitudesPago = solicitudPagoService.filtrarSolicitudesPago(filtro);
        if (solicitudesPago != null && !solicitudesPago.isEmpty()) {
            return ResponseEntity.ok(solicitudesPago);
        } else {
            return ResponseEntity.noContent().build();
        }
    }
    
    @GetMapping("/obtenerAuditoriaSolicitud")
    public ResponseEntity<List<AuditoriaSolicitudDTO>> consultarAuditoriaSolicitud(@RequestParam(value = "idSolicitud", required = false) Integer idSolicitud) {
        List<AuditoriaSolicitudDTO> auditoriaSolicitud = auditoriaService.consultarAuditoriaSolicitud(idSolicitud);
        if (auditoriaSolicitud != null && !auditoriaSolicitud.isEmpty()) {
            return ResponseEntity.ok(auditoriaSolicitud);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PutMapping("/actualizarSolicitud")
    public ResponseEntity<ResponseDTO> actualizarSolicitudPago(@RequestBody SolicitudDTO solicitudPago) {
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setTimestamp(new Date());

        boolean resultado = solicitudPagoService.actualizarSolicitudPago(solicitudPago);
        if (resultado) {
            responseDTO.setCodigoRetorno(0);
            responseDTO.setGlosaRetorno("Solicitud de pago actualizada correctamente!");
            responseDTO.setResultado(resultado);

            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        } else {
            responseDTO.setCodigoRetorno(-1);
            responseDTO.setGlosaRetorno("No se actualizó la solicitud.");
            responseDTO.setTimestamp(new Date());

            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/obtenerSolicitudesPorProceso/{idProceso}/{idTipoSolicitante}")
    public ResponseEntity<List<SolicitudDTO>> obtenerSolicitudesPorProceso(@PathVariable("idProceso") int idProceso,@PathVariable("idTipoSolicitante") int idTipoSolicitante) {
        List<SolicitudDTO> solicitudes = solicitudPagoService.obtenerSolicitudesPorProceso(idProceso, idTipoSolicitante);
        if (solicitudes != null && !solicitudes.isEmpty()) {
            return ResponseEntity.ok(solicitudes);
        } else {
            return ResponseEntity.noContent().build();
        }
    }
    
    @PostMapping("/actualizarIdProceso")
    public ResponseEntity<ResponseDTO> actualizarIdProceso(@PathVariable("idSolicitud") int idSolicitud,@PathVariable("idProceso") int idProceso) {
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setTimestamp(new Date());

        boolean resultado = solicitudPagoService.actualizarIdProceso(idSolicitud,idProceso);
        if (resultado) {
            responseDTO.setCodigoRetorno(0);
            responseDTO.setGlosaRetorno("Solicitud asignada a proceso correctamente!");
            responseDTO.setResultado(resultado);

            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        } else {
            responseDTO.setCodigoRetorno(-1);
            responseDTO.setGlosaRetorno("No se actualizó la solicitud.");
            responseDTO.setTimestamp(new Date());

            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/insertarResolucion")
    public ResponseEntity<ResponseDTO> insertarResolucion(@RequestBody ResolucionDTO resolucionDTO) {
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setTimestamp(new Date());

        int resultado = solicitudPagoService.insertarResolucion(resolucionDTO);
        if (resultado>0) {
            responseDTO.setCodigoRetorno(0);
            responseDTO.setGlosaRetorno("Resolución insertada correctamente!");
            responseDTO.setResultado(resultado);

            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        } else {
            responseDTO.setCodigoRetorno(-1);
            responseDTO.setGlosaRetorno("No se insertó la resolución.");
            responseDTO.setTimestamp(new Date());

            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/obtenerTiposSolicitante")
    public ResponseEntity<List<TipoSolicitanteDTO>> obtenerTiposSolicitante() {
        List<TipoSolicitanteDTO> tiposSolicitante = solicitudPagoService.obtenerTiposSolicitante();
        if (tiposSolicitante != null && !tiposSolicitante.isEmpty()) {
            return ResponseEntity.ok(tiposSolicitante);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/obtenerCausantesPorSolicitud/{idSolicitud}")
    public ResponseEntity<List<CausanteSolicitudDTO>> obtenerCausantesPorSolicitud(@PathVariable("idSolicitud") int idSolicitud) {
        List<CausanteSolicitudDTO> causante = solicitudPagoService.obtenerCausantesPorSolicitud(idSolicitud);
        if (causante != null && !causante.isEmpty()) {
            return ResponseEntity.ok(causante);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @DeleteMapping("/eliminarCausanteSolicitud/{idCausanteSolicitud}")
    public ResponseEntity<Map<String, String>> eliminarCausante(@PathVariable("idCausanteSolicitud") int idCausanteSolicitud) {
        boolean resultado = solicitudPagoService.eliminarCausanteSolicitud(idCausanteSolicitud);

        Map<String, String> response = new HashMap<>();
        if (resultado) {
            response.put("message", "Causante eliminado correctamente.");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Error al eliminar el causante.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PutMapping("/actualizar-causante")
    public ResponseEntity<String> actualizarCausanteSolicitud(@RequestBody CausanteSolicitudDTO causanteSolicitud) {
        boolean actualizado = solicitudPagoService.actualizarCausanteSolicitud(causanteSolicitud);

        if (actualizado) {
            return ResponseEntity.ok("Causante actualizado correctamente.");
        } else {
            return ResponseEntity.status(500).body("Error al actualizar el causante.");
        }
    }

    @GetMapping("/obtenerSolicitudesPorEstado/{idEstado}")
    public ResponseEntity<List<SolicitudDTO>> obtenerSolicitudesPorEstado(@PathVariable("idEstado") int idEstado) {
        List<SolicitudDTO> solicitudes = solicitudPagoService.obtenerSolicitudesPorEstado(idEstado);
        if (solicitudes != null && !solicitudes.isEmpty()) {
            return ResponseEntity.ok(solicitudes);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/obtenerSolicitudesPorBeneficiario/{rutBeneficiario}")
    public ResponseEntity<List<SolicitudDTO>> obtenerSolicitudesPorBeneficiario(@PathVariable("rutBeneficiario") Integer rutBeneficiario) {
        List<SolicitudDTO> solicitudes = solicitudPagoService.obtenerSolicitudesPorBeneficiario(rutBeneficiario);
        if (solicitudes != null && !solicitudes.isEmpty()) {
            return ResponseEntity.ok(solicitudes);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/obtenerSolicitudesPorEmpleador/{rutEmpleador}")
    public ResponseEntity<List<SolicitudDTO>> obtenerSolicitudesPorEmpleador(@PathVariable("rutEmpleador") Integer rutEmpleador) {
        List<SolicitudDTO> solicitudes = solicitudPagoService.obtenerSolicitudesPorEmpleador(rutEmpleador);
        if (solicitudes != null && !solicitudes.isEmpty()) {
            return ResponseEntity.ok(solicitudes);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/obtenerSolicitudesPorOrigen")
    public ResponseEntity<List<SolicitudDTO>> obtenerSolicitudesPorOrigen(
            @RequestParam(value = "origen", required = false) Integer origen,
            @RequestParam(value = "proceso", required = false) Integer proceso,
            @RequestParam(value = "estado", required = false) Integer estado) {

        List<SolicitudDTO> solicitudes = solicitudPagoService.obtenerSolicitudesPorOrigen(origen, proceso, estado);
        if (solicitudes != null && !solicitudes.isEmpty()) {
            return ResponseEntity.ok(solicitudes);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    
    @GetMapping("/obtenerDerechoCausantes/{rutBeneficiario}/{periodoDesde}/{periodoHasta}/{tipoBeneficiario}")
    public ResponseEntity<List<DerechoCausanteDTO>> obtenerDerechoCausantes(@PathVariable("rutBeneficiario") Integer rutBeneficiario,@PathVariable("periodoDesde") Integer periodoDesde,@PathVariable("periodoHasta") Integer periodoHasta,@PathVariable("tipoBeneficiario") Integer tipoBeneficiario) {
        List<DerechoCausanteDTO> derechoCausantes = causanteService.obtenerDerechoCausantes(rutBeneficiario,periodoDesde,periodoHasta,tipoBeneficiario);
        if (derechoCausantes != null && !derechoCausantes.isEmpty()) {
            return ResponseEntity.ok(derechoCausantes);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/obtenerCausantes/{rutBeneficiario}")
    public ResponseEntity<List<CausanteDTO>> obtenerCausantes(@PathVariable("rutBeneficiario") Integer rutBeneficiario) {
        List<CausanteDTO> causantes = causanteService.obtenerDetalleCausante(rutBeneficiario);
        if (causantes != null && !causantes.isEmpty()) {
            return ResponseEntity.ok(causantes);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/obtenerOrigenes/{tipo}")
    public ResponseEntity<List<OrigenDTO>> obtenerOrigenes(@PathVariable("tipo") Integer tipo) {
        List<OrigenDTO> origenes = utilService.obtenerOrigenes(tipo);
        if (origenes != null && !origenes.isEmpty()) {
            return ResponseEntity.ok(origenes);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PutMapping("/actualizarCriterio")
    public ResponseEntity<String> actualizarCriterio(@RequestBody CriterioSolicitudDTO criterioSolicitudDTO) {
        try {
            // Llama al servicio para actualizar el criterio
            boolean resultado = criterioSolicitudService.actualizarCriterioSolicitud(criterioSolicitudDTO);

            if (resultado) {
                return ResponseEntity.ok("Criterio actualizado correctamente.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error al actualizar el criterio con ID " + criterioSolicitudDTO.getIdCriterioSolicitud());
            }
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar el criterio.");
        }
    }

    @PutMapping("/actualizarCriterioCausante")
    public ResponseEntity<String> actualizarCriterioCausante(
        @RequestPart("criterioCausante") CriterioSolicitudCausanteDTO criterioCausanteDTO,
        @RequestParam(value = "file", required = false) MultipartFile file) {
    return null;
    }
        
    @GetMapping("/obtenerMotivosRechazo")
    public ResponseEntity<List<MotivoRechazoDTO>> obtenerMotivosRechazo() {
        List<MotivoRechazoDTO> motivosRechazo = solicitudPagoService.obtenerMotivosRechazo();
        if (motivosRechazo != null && !motivosRechazo.isEmpty()) {
            return ResponseEntity.ok(motivosRechazo);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/detallePersona/{rut}")
    public ResponseEntity<DetallePersonaDTO> obtenerDetallePersona(@PathVariable("rut") int rut) {

        DetallePersonaDTO detallePersona = personaService.obtenerPersona(rut);

        if (detallePersona != null) {
            return ResponseEntity.ok(detallePersona);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("/rechazarSolicitud")
    public ResponseEntity<String> rechazarSolicitud(@RequestBody RechazoSolicitudDTO rechazoSolicitudDTO) {
        boolean rechazo = solicitudPagoService.rechazarSolicitud(rechazoSolicitudDTO);
        if (rechazo) {
            // Caso exitoso: solicitud rechazada
            return ResponseEntity.status(HttpStatus.OK).body("Solicitud rechazada exitosamente.");
        } else {
            // Caso de error: No se pudo rechazar
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se pudo rechazar la solicitud. Motivo: <explicar_el_motivo>");
        }
    }

    @GetMapping("/obtenerDetalleCausantePorId/{idCausanteSolicitud}")
    public ResponseEntity<List<DetalleCausanteDTO>> obtenerDetalleCausantePorId(@PathVariable("idCausanteSolicitud") Integer idCausanteSolicitud) {
        List<DetalleCausanteDTO> detalleCausante = causanteService.obtenerDetalleCausantePorId(idCausanteSolicitud);
        if (detalleCausante != null && !detalleCausante.isEmpty()) {
            return ResponseEntity.ok(detalleCausante);
        } else {
            return ResponseEntity.noContent().build();
        }
    }
}