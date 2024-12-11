package cl.gob.ips.pagos_asignacion_familiar.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.gob.ips.pagos_asignacion_familiar.dto.AuditoriaSolicitudDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.BancoDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.BancoTipoCuentaDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.CausanteSolicitudDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.ComunaDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.CriterioSolicitudDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.CuentaBancariaDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.FormaPagoDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.ProcesoDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.RegionDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.ResolucionDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.DatosProcesoPorTipoDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.ResponseDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.SolicitudDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.TipoSolicitanteDTO;
import cl.gob.ips.pagos_asignacion_familiar.service.AuditoriaService;
import cl.gob.ips.pagos_asignacion_familiar.service.ComunaService;
import cl.gob.ips.pagos_asignacion_familiar.service.CriterioSolicitudService;
import cl.gob.ips.pagos_asignacion_familiar.service.CuentaBancariaService;
import cl.gob.ips.pagos_asignacion_familiar.service.ProcesoService;
import cl.gob.ips.pagos_asignacion_familiar.service.SolicitudPagoService;

@RestController
@CrossOrigin("*")
@RequestMapping("/pagos")
public class PagosAsignacionFamiliarController {

    @Autowired
    ProcesoService procesoService;

    @Autowired
    private CriterioSolicitudService criterioSolicitudService;

    @Autowired
    private CuentaBancariaService cuentaBancariaService;

    @Autowired
    private SolicitudPagoService solicitudPagoService;

    @Autowired
    private AuditoriaService auditoriaService;

    @Autowired
    private ComunaService comunaService;

    @PostMapping("/crear-proceso")
    public ResponseEntity<ResponseDTO> crearProceso(
            @RequestBody ProcesoDTO insertarProcesoDTO) {

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setTimestamp(new Date());

        int resultado = procesoService
                .crearProceso(insertarProcesoDTO);

        if (resultado>0){
            ProcesoDTO proceso = new ProcesoDTO();
            responseDTO.setCodigoRetorno(0);
            responseDTO.setGlosaRetorno("Proceso creado exitósamente!");
            responseDTO.setResultado(resultado);

            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        } else {
            responseDTO.setCodigoRetorno(-1);
            responseDTO.setGlosaRetorno("No se creó el proceso.");
            Date currentDate = new Date();
            responseDTO.setTimestamp(currentDate);
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/obtenerProceso")
    public ResponseEntity<List<ProcesoDTO>> consultarProceso(@RequestParam(value = "id", required = false) Long id) {
        List<ProcesoDTO> procesos = procesoService.consultarProceso(id);
        if (procesos != null && !procesos.isEmpty()) {
            return ResponseEntity.ok(procesos);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/obtenerCriterio/{id}")
    public ResponseEntity<List<CriterioSolicitudDTO>> consultarCriterio(@PathVariable("id") Integer id) {
        List<CriterioSolicitudDTO> criterios = criterioSolicitudService.consultarCriteriosSolicitud(id);
        if (criterios != null && !criterios.isEmpty()) {
            return ResponseEntity.ok(criterios);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/insertarCuenta")
public ResponseEntity<ResponseDTO> insertarCuentaBancaria(@RequestBody CuentaBancariaDTO cuentaBancaria) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.setTimestamp(new Date());
    
    try {
        // Llamada al servicio para insertar la cuenta bancaria
        int resultado = cuentaBancariaService.insertarCuentaBancaria(cuentaBancaria);
        
        // Si la inserción fue exitosa
        if (resultado > 0) {
            responseDTO.setCodigoRetorno(0);
            responseDTO.setGlosaRetorno("Cuenta creada exitosamente!");
            responseDTO.setResultado(resultado);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } else {
            // Si no se creó la cuenta, pero no hay excepción
            responseDTO.setCodigoRetorno(-1);
            responseDTO.setGlosaRetorno("No se creó la cuenta bancaria.");
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
    } catch (RuntimeException e) {
        // Captura de la excepción cuando se produce un error (como número de cuenta repetido)
        responseDTO.setCodigoRetorno(-1);
        responseDTO.setGlosaRetorno(e.getMessage());  // El mensaje de la excepción contiene el error
        responseDTO.setTimestamp(new Date());
        return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
        // Captura de cualquier otro error que ocurra
        responseDTO.setCodigoRetorno(-1);
        responseDTO.setGlosaRetorno("Error al crear la cuenta bancaria: " + e.getMessage());
        responseDTO.setTimestamp(new Date());
        return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}


    @GetMapping("/obtenerCuentas/{rutTitular}")
    public ResponseEntity<List<CuentaBancariaDTO>> obtenerCuentasBancariasPorRut(@PathVariable("rutTitular") int rutTitular) {
        List<CuentaBancariaDTO> cuentas = cuentaBancariaService.obtenerCuentasBancariasPorRut(rutTitular);
        if (cuentas != null && !cuentas.isEmpty()) {
            return ResponseEntity.ok(cuentas);
        } else {
            return ResponseEntity.notFound().build();
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
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/obtenerSolicitudPorId/{id}")
    public ResponseEntity<List<SolicitudDTO>> consultarSolicitudesPago(@PathVariable("id") int id) {
        List<SolicitudDTO> solicitudesPago = solicitudPagoService.consultarSolicitudPago(id);
        if (solicitudesPago != null && !solicitudesPago.isEmpty()) {
            return ResponseEntity.ok(solicitudesPago);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/filtrarSolicitudes")
    public ResponseEntity<List<SolicitudDTO>> filtrarSolicitudesPago(@RequestParam(value = "filtro", required = true) String filtro) {
        List<SolicitudDTO> solicitudesPago = solicitudPagoService.filtrarSolicitudesPago(filtro);
        if (solicitudesPago != null && !solicitudesPago.isEmpty()) {
            return ResponseEntity.ok(solicitudesPago);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/filtrarProcesos")
    public ResponseEntity<List<ProcesoDTO>> filtrarProcesos(@RequestParam(value = "filtro", required = true) String filtro) {
        List<ProcesoDTO> listaProcesos = procesoService.filtrarProcesos(filtro);
        if (listaProcesos != null && !listaProcesos.isEmpty()) {
            return ResponseEntity.ok(listaProcesos);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/obtenerAuditoriaSolicitud")
    public ResponseEntity<List<AuditoriaSolicitudDTO>> consultarAuditoriaSolicitud(@RequestParam(value = "idSolicitud", required = false) Integer idSolicitud) {
        List<AuditoriaSolicitudDTO> auditoriaSolicitud = auditoriaService.consultarAuditoriaSolicitud(idSolicitud);
        if (auditoriaSolicitud != null && !auditoriaSolicitud.isEmpty()) {
            return ResponseEntity.ok(auditoriaSolicitud);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/actualizarSolicitud")
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

    @GetMapping("/obtenerBancos")
    public ResponseEntity<List<BancoDTO>> obtenerBancos() {
        List<BancoDTO> bancos = cuentaBancariaService.obtenerBancos();
        if (bancos != null && !bancos.isEmpty()) {
            return ResponseEntity.ok(bancos);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/obtenerTipoCuentas/{idBanco}")
    public ResponseEntity<List<BancoTipoCuentaDTO>> obtenerTipoCuentas(@PathVariable("idBanco") int idBanco) {
        List<BancoTipoCuentaDTO> tipoCuentas = cuentaBancariaService.obtenerBancoTipoCuenta(idBanco);
        if (tipoCuentas != null && !tipoCuentas.isEmpty()) {
            return ResponseEntity.ok(tipoCuentas);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/obtenerFormasPago/{idSistema}")
    public ResponseEntity<List<FormaPagoDTO>> obtenerFormasPago(@PathVariable("idSistema") int idSistema) {
        List<FormaPagoDTO> formaPago = cuentaBancariaService.obtenerFormasPago(idSistema);
        if (formaPago != null && !formaPago.isEmpty()) {
            return ResponseEntity.ok(formaPago);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/obtenerCuentasPorID/{idCuenta}")
    public ResponseEntity<List<CuentaBancariaDTO>> obtenerCuentasBancariasPorID(@PathVariable("idCuenta") int idCuenta) {
        List<CuentaBancariaDTO> cuentas = cuentaBancariaService.obtenerCuentasBancariasPorID(idCuenta);
        if (cuentas != null && !cuentas.isEmpty()) {
            return ResponseEntity.ok(cuentas);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/obtenerDatosProcesoPorTipo/{idProceso}")
    public ResponseEntity<List<DatosProcesoPorTipoDTO>> obtenerDatosProcesoPorTipo(@PathVariable("idProceso") int idProceso) {
        List<DatosProcesoPorTipoDTO> datosProceso = procesoService.obtenerDatosProcesoPorTipo(idProceso);
        if (datosProceso != null && !datosProceso.isEmpty()) {
            return ResponseEntity.ok(datosProceso);
        } else { 
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/obtenerSolicitudesPorProceso/{idProceso}/{idTipoSolicitante}")
    public ResponseEntity<List<SolicitudDTO>> obtenerSolicitudesPorProceso(@PathVariable("idProceso") int idProceso,@PathVariable("idTipoSolicitante") int idTipoSolicitante) {
        List<SolicitudDTO> solicitudes = solicitudPagoService.obtenerSolicitudesPorProceso(idProceso, idTipoSolicitante);
        if (solicitudes != null && !solicitudes.isEmpty()) {
            return ResponseEntity.ok(solicitudes);
        } else {
            return ResponseEntity.notFound().build();
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
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/actualizarEstadoProceso/{idProceso}/{idEstadoProceso}")
    public ResponseEntity<ResponseDTO> actualizarEstadoProceso(
        @PathVariable("idProceso") int idProceso,
        @PathVariable("idEstadoProceso") int idEstadoProceso) {

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setTimestamp(new Date());

        boolean resultado = procesoService.actualizarEstadoProceso(idProceso, idEstadoProceso);
        if (resultado) {
            responseDTO.setCodigoRetorno(0);
            responseDTO.setGlosaRetorno("Estado del proceso actualizado correctamente!");
            responseDTO.setResultado(resultado);

            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        } else {
            responseDTO.setCodigoRetorno(-1);
            responseDTO.setGlosaRetorno("No se pudo actualizar el estado del proceso.");
            responseDTO.setTimestamp(new Date());

            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/obtenerRegiones")
    public ResponseEntity<RegionDTO> obtenerRegiones() {
        RegionDTO region = comunaService.obtenerRegiones();
        if (region != null) {
            return ResponseEntity.ok(region);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/obtenerComunas/{idRegion}")
    public ResponseEntity<ComunaDTO> obtenerComunas(@PathVariable("idRegion") int idRegion) {
        ComunaDTO comuna = comunaService.obtenerComunas(idRegion);
        if (comuna != null) {
            return ResponseEntity.ok(comuna);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/obtenerCausantesPorSolicitud/{idSolicitud}")
    public ResponseEntity<List<CausanteSolicitudDTO>> obtenerCausantesPorSolicitud(@PathVariable("idSolicitud") int idSolicitud) {
        List<CausanteSolicitudDTO> causante = solicitudPagoService.obtenerCausantesPorSolicitud(idSolicitud);
        if (causante != null && !causante.isEmpty()) {
            return ResponseEntity.ok(causante);
        } else {
            return ResponseEntity.notFound().build();
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

}