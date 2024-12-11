package cl.gob.ips.cuenta_bancaria.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cl.gob.ips.cuenta_bancaria.dto.ArchivoCuentaBancariaDTO;
import cl.gob.ips.cuenta_bancaria.service.FileService;

@RestController
@CrossOrigin("*")
@RequestMapping("/archivos")
public class FileController {
    @Autowired
    private FileService fileService;

    @PostMapping("/cargar-cuentas-bancarias")
    public List<ArchivoCuentaBancariaDTO> parseFile( @RequestParam("file") MultipartFile file) {
    List<ArchivoCuentaBancariaDTO> listaCuentasBancarias = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), "Windows-1252"))) {

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
                ArchivoCuentaBancariaDTO carga = new ArchivoCuentaBancariaDTO();
                carga.setRutTitular(fields[0]);
                carga.setDvTitular(fields[1]);
                carga.setNumeroCuenta(fields[2]);
                carga.setCodigoBanco(fields[3]);
                carga.setTipoCuenta(fields[4]);
                carga.setEstadoCuenta(fields[5]);
                
                listaCuentasBancarias.add(carga);
                
            }
            fileService.insertarCuentasBancarias(listaCuentasBancarias);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaCuentasBancarias;
    }
}