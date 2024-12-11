package cl.gob.ips.proceso_pago.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.gob.ips.proceso_pago.dto.DatosProcesoPorTipoDTO;
import cl.gob.ips.proceso_pago.dto.ProcesoDTO;
import cl.gob.ips.proceso_pago.dto.ResponseDTO;
import cl.gob.ips.proceso_pago.service.ProcesoService;

@RestController
@CrossOrigin("*")
@RequestMapping("/procesoPago")
public class ProcesoPagoController {

    @Autowired
    ProcesoService procesoService;
    
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
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/filtrarProcesos")
    public ResponseEntity<List<ProcesoDTO>> filtrarProcesos(@RequestParam(value = "filtro", required = true) String filtro) {
        List<ProcesoDTO> listaProcesos = procesoService.filtrarProcesos(filtro);
        if (listaProcesos != null && !listaProcesos.isEmpty()) {
            return ResponseEntity.ok(listaProcesos);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/obtenerDatosProcesoPorTipo/{idProceso}")
    public ResponseEntity<List<DatosProcesoPorTipoDTO>> obtenerDatosProcesoPorTipo(@PathVariable("idProceso") int idProceso) {
        List<DatosProcesoPorTipoDTO> datosProceso = procesoService.obtenerDatosProcesoPorTipo(idProceso);
        if (datosProceso != null && !datosProceso.isEmpty()) {
            return ResponseEntity.ok(datosProceso);
        } else { 
            return ResponseEntity.noContent().build();
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
}