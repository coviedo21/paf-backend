package cl.gob.ips.solicitudes_pago.controller;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPSClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
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
import org.springframework.web.multipart.MultipartFile;

import cl.gob.ips.solicitudes_pago.dto.ArchivoCriterioDTO;
import cl.gob.ips.solicitudes_pago.dto.AuditoriaSolicitudDTO;
import cl.gob.ips.solicitudes_pago.dto.CausanteDTO;
import cl.gob.ips.solicitudes_pago.dto.CausanteSolicitudDTO;
import cl.gob.ips.solicitudes_pago.dto.CriterioSolicitudCausanteDTO;
import cl.gob.ips.solicitudes_pago.dto.CriterioSolicitudDTO;
import cl.gob.ips.solicitudes_pago.dto.OrigenDTO;
import cl.gob.ips.solicitudes_pago.dto.ResolucionDTO;
import cl.gob.ips.solicitudes_pago.dto.ResponseDTO;
import cl.gob.ips.solicitudes_pago.dto.SolicitudDTO;
import cl.gob.ips.solicitudes_pago.dto.TipoSolicitanteDTO;
import cl.gob.ips.solicitudes_pago.service.AuditoriaService;
import cl.gob.ips.solicitudes_pago.service.CausanteService;
import cl.gob.ips.solicitudes_pago.service.CriterioSolicitudService;
import cl.gob.ips.solicitudes_pago.service.FileService;
import cl.gob.ips.solicitudes_pago.service.SolicitudPagoService;
import cl.gob.ips.solicitudes_pago.service.UtilService;
import org.springframework.http.MediaType;

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
    private FileService fileService;

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

        int resultado = solicitudPagoService.insertarSolicitudPago(solicitudPago);
        if (resultado>0) {
            responseDTO.setCodigoRetorno(0);
            responseDTO.setGlosaRetorno("Solicitud de pago insertada correctamente!");
            responseDTO.setResultado(resultado);

            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        } else {
            responseDTO.setCodigoRetorno(-1);
            responseDTO.setGlosaRetorno("No se creó la solicitud de pago.");
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

    @GetMapping("/obtenerCausantes/{rutBeneficiario}")
    public ResponseEntity<List<CausanteDTO>> obtenerCausantes(@PathVariable("rutBeneficiario") Integer rutBeneficiario) {
        List<CausanteDTO> causantes = causanteService.obtenerDetalleCausante(rutBeneficiario);
        if (causantes != null && !causantes.isEmpty()) {
            return ResponseEntity.ok(causantes);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/obtenerOrigenes")
    public ResponseEntity<List<OrigenDTO>> obtenerOrigenes() {
        List<OrigenDTO> origenes = utilService.obtenerOrigenes();
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
        
    @PostMapping("/subirArchivo")
    public ResponseEntity<String> subirArchivo(
        @RequestParam("file") MultipartFile file,        
        @RequestParam("idCriterio") String idCriterio
            ) {
        try {
            if (file != null && !file.isEmpty()) {
                String connectStr = "DefaultEndpointsProtocol=https;AccountName=almacenretenjudicialdev;AccountKey=U0RDkRH+LmOKofGzwuwf39HmZMSzRqBOTPX2i0IiXda3t5Dcx6V0RxMjKYhkZznshvMQ/g5wpPh5+ASt99vMiQ==";
                String shareName = "retenjudicialdev";
                String dirName = "";

                String nombreArchivo = file.getOriginalFilename();
                String ext = (nombreArchivo != null && nombreArchivo.contains("."))
                        ? nombreArchivo.substring(nombreArchivo.lastIndexOf("."))
                        : "";

                // Generar un nombre único
                String nombreUnico = UUID.randomUUID().toString() + ext;

                // Subir el archivo
                byte[] fileBytes = file.getBytes();
                boolean success = fileService.uploadFileAzure(connectStr, shareName, dirName, nombreUnico, fileBytes);
                int idCriterioInt = Integer.parseInt(idCriterio);
                if (success) {
                    CriterioSolicitudDTO criterio = new CriterioSolicitudDTO();
                    criterio = criterioSolicitudService.obtenerCriteriosPorIdCriterio(idCriterioInt);
                    criterio.setArchivo(nombreUnico);
                    // Actualizar el archivo asociado en la base de datos
                    boolean resultado = criterioSolicitudService.actualizarCriterioSolicitud(criterio);
                    if (resultado) {
                        return ResponseEntity.ok("Archivo subido y asociado correctamente.");
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error al asociar el archivo con el criterio ID " + idCriterio);
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error al subir el archivo.");
                }
            } else {
                return ResponseEntity.badRequest().body("Archivo vacío o no proporcionado.");
            }
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al subir el archivo.");
        }
    }


}