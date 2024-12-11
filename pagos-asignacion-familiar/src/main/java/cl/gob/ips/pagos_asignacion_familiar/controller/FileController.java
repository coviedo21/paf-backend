package cl.gob.ips.pagos_asignacion_familiar.controller;

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

import cl.gob.ips.pagos_asignacion_familiar.dto.ArchivoCuentaBancariaDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.ArchivoSolicitudDTO;
import cl.gob.ips.pagos_asignacion_familiar.service.FileService;

@RestController
@CrossOrigin("*")
@RequestMapping("/archivos")
public class FileController {
    @Autowired
    private FileService fileService;

    @PostMapping("/cargar-archivo")
    public List<ArchivoSolicitudDTO> parseFile( @RequestParam("file") MultipartFile file,
            @RequestParam("origen") String origen) {
    List<ArchivoSolicitudDTO> listaSolicitudes = new ArrayList<>();
    /*String region [15,"valparíso"?¡];
    comuna[15,176,"vina del mar"]
    comuna[15,"177","valparaíso"]*/
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
                ArchivoSolicitudDTO carga = new ArchivoSolicitudDTO();
                carga.setFolio(fields[0]);
                carga.setFechaHora(fields[1]);
                carga.setRutEmpleador(fields[2]);
                carga.setDvEmpleador(fields[3]);
                carga.setRazonSocialEmpleador(fields[4]);
                carga.setDireccionEmpleador(fields[5]);
                carga.setEmailEmpleador(fields[6]);
                carga.setComunaEmpleador(fields[7]);
                carga.setCiudadEmpleador(fields[8]);
                carga.setRegionEmpleador(fields[9]);
                carga.setRutTrabajador(fields[10]);
                carga.setDvTrabajador(fields[11]);
                carga.setApellidoPaternoTrabajador(fields[12]);
                carga.setApellidoMaternoTrabajador(fields[13]);
                carga.setNombresTrabajador(fields[14]);
                carga.setRutCargaFamiliar(fields[15]);
                carga.setDvCargaFamiliar(fields[16]);
                carga.setApellidoPaternoCarga(fields[17]);
                carga.setApellidoMaternoCarga(fields[18]);
                carga.setNombresCarga(fields[19]);
                carga.setTipoCarga(fields[20]);
                carga.setFechaInicioCompensacion(fields[21]);
                carga.setFechaFinCompensacion(fields[22]);
                carga.setEstadoCarga(fields.length > 23 && !fields[23].trim().isEmpty() ? fields[23].trim() : null);
                carga.setOrigen(origen);
                
                listaSolicitudes.add(carga);
                
            }
            fileService.insertarSolicitud(listaSolicitudes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaSolicitudes;
    }

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
