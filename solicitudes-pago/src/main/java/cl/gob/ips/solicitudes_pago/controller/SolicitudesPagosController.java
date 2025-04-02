package cl.gob.ips.solicitudes_pago.controller;

import cl.gob.ips.solicitudes_pago.dao.PersonaDAO;
import cl.gob.ips.solicitudes_pago.dto.*;
import cl.gob.ips.solicitudes_pago.service.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
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

    @Autowired
    private LicenciaFiniquitoService licenciaFiniquitoService;

    @Autowired
    private PersonaDAO personaDAO;
    
    private final Map<String, Map<String, Object>> tareas = new ConcurrentHashMap<>();

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

        // Validaciones originales (no se toca nada)
        if(solicitudPago.getListaCausantes().isEmpty()){
            responseDTO.setCodigoRetorno(-1);
            responseDTO.setGlosaRetorno("Debe ingresar al menos un causante");
            responseDTO.setTimestamp(new Date());

            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
        else {
            for (CausanteSolicitudDTO causante : solicitudPago.getListaCausantes()) {
                if (causante.getDetalle() == null || causante.getDetalle().isEmpty()) {
                    responseDTO.setCodigoRetorno(-1);
                    responseDTO.setGlosaRetorno("Error: Uno o más causantes no tienen detalle asociado.");
                    responseDTO.setTimestamp(new Date());
                    return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
                }
            }
        }

        LocalDate fechaComparacion = LocalDate.now(ZoneId.of("UTC"));

        for (CausanteSolicitudDTO causante : solicitudPago.getListaCausantes()) {
            if (causante.getFechaInicioRango() == null) {
                responseDTO.setCodigoRetorno(-1);
                responseDTO.setGlosaRetorno("Error: Uno o más causantes tienen una fecha de inicio de rango nula.");
                return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
            }

            if(solicitudPago.getTipoSolicitante() != 1) {
                if (!utilService.esFechaValida(causante.getFechaInicioRango(), fechaComparacion)) {
                    responseDTO.setCodigoRetorno(-1);
                    responseDTO.setGlosaRetorno("Error: Uno o más causantes tienen una fecha de inicio de rango inválida o mayor a 5 años.");
                    return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
                }
            }
        }

        // 🔹 Aquí empieza la modificación mínima para ejecutar en segundo plano

        String taskId = UUID.randomUUID().toString();
        Map<String, Object> datosTarea = new ConcurrentHashMap<>();
        datosTarea.put("estado", "procesando");
        tareas.put(taskId, datosTarea);

        CompletableFuture.runAsync(() -> {
            try {
                ResponseDTO resultado = solicitudPagoService.insertarSolicitudPago(solicitudPago, false);

                if ((int) resultado.getResultado() > 0) {
                    datosTarea.put("estado", "completado");
                    datosTarea.put("idSolicitud", resultado.getResultado());
                    datosTarea.put("mensaje", resultado.getGlosaRetorno());
                } else {
                    datosTarea.put("estado", "error");
                    datosTarea.put("mensaje", resultado.getGlosaRetorno());
                }
            } catch (Exception e) {
                datosTarea.put("estado", "error");
                datosTarea.put("mensaje", e.getMessage());
                e.printStackTrace();
            }
        });

        // 🔹 Fin de modificación. Respuesta inmediata con taskId
        ResponseDTO respuesta = new ResponseDTO();
        respuesta.setCodigoRetorno(0);
        respuesta.setGlosaRetorno("Solicitud en proceso. Puede consultar el estado con el taskId.");
        respuesta.setResultado(Map.of("taskId", taskId));
        respuesta.setTimestamp(new Date());

        return new ResponseEntity<>(respuesta, HttpStatus.ACCEPTED);
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
    public ResponseEntity<List<SolicitudProcesoDTO>> obtenerSolicitudesPorProceso(
            @PathVariable("idProceso") int idProceso,
            @PathVariable("idTipoSolicitante") int idTipoSolicitante,
            @RequestParam(value = "idEstado", required = false) Integer idEstado) {
    
        List<SolicitudProcesoDTO> solicitudes = solicitudPagoService.obtenerSolicitudesPorProceso(idProceso, idTipoSolicitante, idEstado);
    
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

    
@PostMapping("/obtenerDerechoCausantes")
public ResponseEntity<?> obtenerDerechoCausantes(@RequestBody DerechoCausanteRequestDTO request) {
        
// Obtener la fecha actual
LocalDate fechaActual = LocalDate.now();

// Formateador para obtener el período en formato "yyyyMM"
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");

// Calcular el período final (año y mes de la fecha actual)
String periodoFinal = fechaActual.format(formatter);

// Calcular el período de inicio (restando 5 años)
String periodoInicio = fechaActual.minusYears(5).format(formatter);

// Imprimir resultados
System.out.println("Período Final: " + periodoFinal); // Ejemplo: "202402"
System.out.println("Período Inicio: " + periodoInicio); // Ejemplo: "201902"

List<CausanteCuentaCorrienteDTO> derechoCausantes = new ArrayList<>();
        try {
        	if (request.getPeriodoDesde() != null && !request.getPeriodoDesde().isBlank() &&
        		    request.getPeriodoHasta() != null && !request.getPeriodoHasta().isBlank()) {
                derechoCausantes = causanteService.obtenerDerechoCausantes(
                    request.getRutCausante(), request.getRutBeneficiario(), request.getRutEmpleador(), request.getPeriodoDesde(), request.getPeriodoHasta(), request.getTipoCausante());    
            }
            else{
                derechoCausantes = causanteService.obtenerDerechoCausantes(
                    request.getRutCausante(), request.getRutBeneficiario(), request.getRutEmpleador(), periodoInicio, periodoFinal, request.getTipoCausante());
            }    
            
            if (derechoCausantes != null && !derechoCausantes.isEmpty()) {
                return ResponseEntity.ok(derechoCausantes);
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No se han encontrado causantes con periodos aprobados menores a 5 años.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error en los parámetros: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor: " + e.getMessage());
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

        try {
            DetallePersonaDTO detallePersona = personaService.obtenerPersona(rut);

            if (detallePersona != null) {
                return ResponseEntity.ok(detallePersona);
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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

    @GetMapping("/licenciaFiniquito")
    public ResponseEntity<List<LicenciaFiniquitoDTO>> obtenerLicenciaFiniquito(@Param("rutBeneficiario") Integer rutBeneficiario,
                                                                               @Param("nroLicencia") String nroLicencia,
                                                                               @Param("fechaInicio") String fechaInicio,
                                                                               @Param("fechaFin") String fechaFin) {
        try {
            List<LicenciaFiniquitoDTO> licenciaFiniquitoDTOS = licenciaFiniquitoService.obtenerLicenciaFiniquito(rutBeneficiario != null ? rutBeneficiario : 0, nroLicencia, fechaInicio, fechaFin);

            if (null != licenciaFiniquitoDTOS) {
                return ResponseEntity.ok(licenciaFiniquitoDTOS);
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/DetallelicenciaFiniquito/{idLicFin}")
    public ResponseEntity<List<DetalleLicenciaFiniquitoDTO>> obtenerDetalleLicenciaFiniquito(@PathVariable("idLicFin") Integer idLicFin) {
        try {
            List<DetalleLicenciaFiniquitoDTO> detalleLicenciaFiniquitoDTOs = licenciaFiniquitoService.obtenerDetalleLicenciaFiniquito(idLicFin);

            if (detalleLicenciaFiniquitoDTOs != null) {
                return ResponseEntity.ok(detalleLicenciaFiniquitoDTOs);
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping("/licenciaFiniquito")
    public ResponseEntity<ResponseDTO> insertarLicenciaFiniquito(@RequestBody LicenciaFiniquitoInputDTO licenciaFiniquito) {
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setTimestamp(new Date());
        try {
            HashMap<String, String> map = licenciaFiniquitoService.agregarLicenciaFiniquito(licenciaFiniquito);

            if (map.get("Estado").equals("OK")) {
                responseDTO.setCodigoRetorno(0);
                responseDTO.setGlosaRetorno(map.get("Mensaje"));
                return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
            } else {
                responseDTO.setCodigoRetorno(-1);
                responseDTO.setGlosaRetorno(map.get("Mensaje"));
                return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("ERROR: {}", e.getMessage());
            responseDTO.setCodigoRetorno(-1);
            responseDTO.setGlosaRetorno(e.getMessage());
            responseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/obtenerDetalleCausantePorId/{idCausanteSolicitud}")
    public ResponseEntity<List<DetalleCausanteDTO>> obtenerDetalleCausantePorId (@PathVariable("idCausanteSolicitud") Integer idCausanteSolicitud) {
        List<DetalleCausanteDTO> detalleCausante = causanteService.obtenerDetalleCausantePorId(idCausanteSolicitud);
        if (detalleCausante != null && !detalleCausante.isEmpty()) {
            return ResponseEntity.ok(detalleCausante);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/persona/{rut}")
    public ResponseEntity<List<PersonaDTO>> obtenerPersona(@PathVariable int rut) {
        try {
            List<PersonaDTO>  personaDTOS = personaService.obtenerDetallePersona(rut);

            if (personaDTOS.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(personaDTOS);
//                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.ok(personaDTOS);
            }
        } catch (Exception e) {
            log.error("ERROR: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/obtenerEstadosSolicitud")
    public ResponseEntity<List<EstadosSolicitudDTO>> obtenerEstadosSolicitud() {
        List<EstadosSolicitudDTO> estadosSolicitud = solicitudPagoService.obtenerEstadosSolicitud();
        if (estadosSolicitud != null && !estadosSolicitud.isEmpty()) {
            return ResponseEntity.ok(estadosSolicitud);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/validarSolicitud/{idSolicitud}/{usuario}")
    public ResponseEntity<Map<String, Object>> validarSolicitud(@PathVariable("idSolicitud") Integer idSolicitud,@PathVariable("usuario") String usuario) {
        String taskId = UUID.randomUUID().toString();

        Map<String, Object> datosTarea = new ConcurrentHashMap<>();
        datosTarea.put("estado", "procesando");
        tareas.put(taskId, datosTarea);

        CompletableFuture.runAsync(() -> {
            try {
            	String esPortuario = solicitudPagoService.consultarSolicitudPago(idSolicitud).get(0).getEsPortuario();
                boolean validacionCriterios = criterioSolicitudService.validarCriteriosResolucion(idSolicitud, false, true,esPortuario);

                if (validacionCriterios) {
                    ResolucionDTO resolucion = new ResolucionDTO();
                    resolucion.setIdSolicitud(idSolicitud);
                    resolucion.setAutor(1);
                    resolucion.setIdEstado(2);
                    resolucion.setVcDescripcion("Se valida solicitud.");
                    resolucion.setMotivoRechazo(null);
                    resolucion.setUsuario(usuario);

                    int resolucionResponse = solicitudPagoService.insertarResolucion(resolucion);
                    if (resolucionResponse > 0) {
                        datosTarea.put("estado", "completado");
                        datosTarea.put("mensaje", "Se validó la solicitud de pago. Puede ser asignada a un proceso de pago.");
                    } else {
                        datosTarea.put("estado", "error");
                        datosTarea.put("mensaje", "Error al insertar resolución.");
                    }
                } else {
                    datosTarea.put("estado", "rechazada");
                    datosTarea.put("mensaje", "La solicitud no cumple con todos los criterios de aceptación.");
                }
            } catch (Exception e) {
                datosTarea.put("estado", "error");
                datosTarea.put("mensaje", "Error en validación: " + e.getMessage());
                e.printStackTrace();
            }
        });

        return ResponseEntity.accepted().body(Map.of(
                "taskId", taskId,
                "mensaje", "La validación de la solicitud se está procesando."
        ));
    }

    
    @PostMapping("/actualizarDetalleCausante")
    public ResponseEntity<Boolean> actualizarDetalleCausante(@RequestBody DetalleCausanteDTO detalleCausanteDTO) {
        Boolean resultado = causanteService.actualizarDetalleCausante(detalleCausanteDTO);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/obtenerDiasLicenciaFiniquito")
    public int obtenerDiasLicenciaFiniquito(@RequestParam int rutBeneficiario,
                                            @RequestParam String fechaInicio,
                                            @RequestParam String fechaFin) {
        return licenciaFiniquitoService.obtenerDiasLicenciaFiniquito(rutBeneficiario, fechaInicio, fechaFin);
    }
    
    @GetMapping("/obtenerDiasCotizacion")
    public ResponseEntity<Integer> obtenerDiasCotizacion(
            @RequestParam int rutBeneficiario,
            @RequestParam int rutEmpleador,
            @RequestParam String periodo) {

        int dias = causanteService.obtenerDiasCotizacion(rutBeneficiario, rutEmpleador, periodo);
        return ResponseEntity.ok(dias);
    }
    
    @GetMapping("/verificar-relacion-laboral/{idCausanteSolicitud}")
    public ResponseEntity<Boolean> verificarRelacionLaboralVigente(@PathVariable int idCausanteSolicitud) {
        boolean vigente = criterioSolicitudService.verificarRelacionLaboralVigente(idCausanteSolicitud,"N");
        return ResponseEntity.ok(vigente);
    }

    @GetMapping("/obtenerTareaSolicitud/{taskId}")
    public ResponseEntity<Map<String, Object>> obtenerIdSolicitud(@PathVariable String taskId) {
        Map<String, Object> datos = tareas.get(taskId);

        if (datos == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "estado", "desconocido",
                "mensaje", "No existe ninguna tarea con ese taskId"
            ));
        }

        return ResponseEntity.ok(datos);
    }

    @GetMapping("/obtenerTareaValidacion/{taskId}")
    public ResponseEntity<Map<String, Object>> obtenerResultadoValidacion(@PathVariable String taskId) {
        Map<String, Object> datos = tareas.get(taskId);

        if (datos == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "estado", "desconocido",
                    "mensaje", "No existe ninguna validación con ese taskId"
            ));
        }

        return ResponseEntity.ok(datos);
    }

}