package cl.gob.ips.mantenedor_comunas.controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import cl.gob.ips.mantenedor_comunas.dto.ComunaDTO;
import cl.gob.ips.mantenedor_comunas.dto.ComunaMantenedorDTO;
import cl.gob.ips.mantenedor_comunas.dto.ListaComunaDTO;
import cl.gob.ips.mantenedor_comunas.dto.RegionDTO;
import cl.gob.ips.mantenedor_comunas.dto.ResultadoRegionDTO;
import cl.gob.ips.mantenedor_comunas.service.ComunaService;

@RestController
@CrossOrigin("*")
@RequestMapping("/mantenedor")
public class MantenedorController {
    @Autowired
    private ComunaService comunaService;
    
    @GetMapping("/obtenerRegiones")
    public ResponseEntity<RegionDTO> obtenerRegiones() {
        RegionDTO region = comunaService.obtenerRegiones();
        if (region != null) {
            return ResponseEntity.ok(region);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/obtenerComunas/{idRegion}")
    public ResponseEntity<ComunaDTO> obtenerComunas(@PathVariable("idRegion") int idRegion) {
        ComunaDTO comuna = comunaService.obtenerComunas(idRegion);
        if (comuna != null) {
            return ResponseEntity.ok(comuna);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/validarComuna/{idRegion}/{nombreComuna}")
    public ResponseEntity<ListaComunaDTO> obtenerComunas(@PathVariable("idRegion") int idRegion, 
                                                @PathVariable("nombreComuna") String nombreComuna) {
        ListaComunaDTO comuna = comunaService.validarComuna(idRegion, nombreComuna);
        if (comuna != null) {
            return ResponseEntity.ok(comuna);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/validarRegion/{nombreRegion}")
    public ResponseEntity<ResultadoRegionDTO> validarRegion(@PathVariable("nombreRegion") String nombreRegion) {
        ResultadoRegionDTO region = comunaService.validarRegion(nombreRegion);
        if (region != null) {
            return ResponseEntity.ok(region);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/obtenerComunasPorId/{iIdCoincidencia}")
    public ResponseEntity<ComunaMantenedorDTO> obtenerComunaPorIdCoincidencia(@PathVariable("iIdCoincidencia") Integer iIdCoincidencia) {
        ComunaMantenedorDTO comuna = comunaService.obtenerComunaPorIdCoincidencia(iIdCoincidencia);
        if (comuna != null) {
            return ResponseEntity.ok(comuna);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("/insertarComuna")
    public ResponseEntity<Map<String, Object>> insertarComunaMantenedor(@RequestBody ComunaMantenedorDTO comuna) {
        int result = comunaService.insertarComunaMantenedor(comuna);
        Map<String, Object> response = new HashMap<>();
        if (result > 0) {
            response.put("message", "Comuna insertada correctamente.");
            response.put("success", true);
            response.put("comuna", comuna); 
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "No se pudo insertar la comuna.");
            response.put("success", false);
            return ResponseEntity.badRequest().body(response);
        }
    }


    @PutMapping("/actualizarComuna")
    public ResponseEntity<Map<String, String>> actualizarComunaMantenedor(@RequestBody ComunaMantenedorDTO comuna) {
        int result = comunaService.actualizarComunaMantenedor(comuna);
        Map<String, String> response = new HashMap<>();
        if (result > 0) {
            response.put("mensaje", "Comuna actualizada correctamente.");
            return ResponseEntity.ok(response);
        } else {
            response.put("mensaje", "No se pudo actualizar la comuna.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/eliminarComuna/{iIdCoincidencia}")
    public ResponseEntity<Map<String, String>> eliminarComunaPorIdCoincidencia(@PathVariable("iIdCoincidencia") Integer iIdCoincidencia) {
        int result = comunaService.eliminarComunaPorIdCoincidencia(iIdCoincidencia);
        Map<String, String> response = new HashMap<>();
        if (result > 0) {
            response.put("message", "Comuna eliminada correctamente.");
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } else {
            response.put("message", "No se pudo eliminar la comuna.");
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        }
    }

    @GetMapping("/obtenerRegistrosPorComunaOriginal/{iIdComunaOriginal}")
    public ResponseEntity<List<ComunaMantenedorDTO>> obtenerRegistrosPorComunaOriginal(@PathVariable("iIdComunaOriginal") Integer iIdComunaOriginal) {
        List<ComunaMantenedorDTO> registros = comunaService.obtenerRegistrosPorComunaOriginal(iIdComunaOriginal);
        if (registros != null) {
            return ResponseEntity.ok(registros);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

}