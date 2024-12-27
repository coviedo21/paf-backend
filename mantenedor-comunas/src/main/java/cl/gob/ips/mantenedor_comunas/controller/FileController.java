package cl.gob.ips.mantenedor_comunas.controller;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import cl.gob.ips.mantenedor_comunas.dto.ArchivoSolicitudDTO;
import cl.gob.ips.mantenedor_comunas.service.FileService;

@RestController
@CrossOrigin("*")
@RequestMapping("/archivos")
public class FileController {
    @Autowired
    private FileService fileService;

    @PostMapping("/cargar-comunas")
    public List<ArchivoSolicitudDTO> parseFile( @RequestParam("file") MultipartFile file) {
    List<ArchivoSolicitudDTO> listaComunas = new ArrayList<>();
    
    try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            //Pasar a UTF-8
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                 // Ignorar líneas en blanco
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] fields = line.split(";");
                ArchivoSolicitudDTO carga = new ArchivoSolicitudDTO();
                carga.setComunaIngresada(fields[0]);
                carga.setIdComunaOriginal(fields[1].trim());
                carga.setNombreComunaOriginal(fields[2]);
                listaComunas.add(carga);
                
            }
            fileService.insertarComuna(listaComunas);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaComunas;
    }

}