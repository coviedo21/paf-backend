package cl.gob.ips.solicitudes_pago.controller;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPSClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
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

import cl.gob.ips.solicitudes_pago.dto.ArchivoResponseDTO;
import cl.gob.ips.solicitudes_pago.dto.ArchivoSolicitudDTO;
import cl.gob.ips.solicitudes_pago.service.FileService;

@RestController
@CrossOrigin("*")
@RequestMapping("/archivos")
public class FileController {
    @Autowired
    private FileService fileService;

    @PostMapping("/cargar-archivo-previred")
    public ArchivoResponseDTO cargarArchivoPrevired( @RequestParam("file") MultipartFile file,
            @RequestParam("origen") String origen, @RequestParam("periodo") String periodo) {
    List<ArchivoSolicitudDTO> listaSolicitudes = new ArrayList<>();
    ArchivoResponseDTO respuesta = new ArchivoResponseDTO();
    /*String region [15,"valparíso"?¡];
    comuna[15,176,"vina del mar"]
    comuna[15,"177","valparaíso"]*/

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
                carga.setFolio(fields[0]);
                carga.setFechaHora(fields[1]);
                carga.setRutEmpleador(fields[2]);
                carga.setDvEmpleador(fields[3]);
                carga.setRazonSocialEmpleador(fields[4]);
                carga.setDireccionEmpleador(fields[5]);
                carga.setEmailEmpleador(fields[6]);
                carga.setComunaEmpleador(fields[7]);
                carga.setCiudadEmpleador(fields[8]);
                carga.setNombreRegion(fields[9]);
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
                carga.setPeriodo(periodo);
                
                listaSolicitudes.add(carga);
                
            }
            respuesta = fileService.insertarSolicitudes(listaSolicitudes,periodo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return respuesta;
    }

    @PostMapping("/cargar-archivo-especiales")
    public List<ArchivoSolicitudDTO> cargarArchivoEspeciales( @RequestParam("file") MultipartFile file,
            @RequestParam("origen") String origen, @RequestParam("periodo") String periodo) {
    List<ArchivoSolicitudDTO> listaSolicitudes = new ArrayList<>();
    /*String region [15,"valparíso"?¡];
    comuna[15,176,"vina del mar"]
    comuna[15,"177","valparaíso"]*/
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
                carga.setFolio(fields[0]);
                carga.setFechaHora(fields[1]);
                carga.setRutEmpleador(fields[2]);
                carga.setDvEmpleador(fields[3]);
                carga.setRazonSocialEmpleador(fields[4]);
                carga.setDireccionEmpleador(fields[5]);
                carga.setEmailEmpleador(fields[6]);
                carga.setComunaEmpleador(fields[7]);
                carga.setCiudadEmpleador(fields[8]);
                carga.setNombreRegion(fields[9]);
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
            fileService.insertarSolicitudes(listaSolicitudes,periodo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaSolicitudes;
    }

    @GetMapping("/getFileAzure/{nombreArchivo}")
    public ResponseEntity<byte[]> getFileAzure(@PathVariable String nombreArchivo, @PathVariable String fileName) {      
        return null;
    }

    @GetMapping("/descargarArchivo")
    public ResponseEntity<byte[]> descargarArchivo(@RequestParam("nombreArchivo") String nombreArchivo) {
        String servidor = System.getenv("paf_ftp_server");
        String usuario = System.getenv("paf_ftp_user");
        String password = System.getenv("paf_ftp_password");
        int puerto = 21;
        
        FTPSClient ftpClient = new FTPSClient();

        try {
            // Conexión al servidor FTPS
            ftpClient.connect(servidor, puerto);
            ftpClient.login(usuario, password);
            ftpClient.execPBSZ(0); // Configurar el búfer de protección
            ftpClient.execPROT("P"); // Proteger la conexión de datos
            ftpClient.enterLocalPassiveMode(); // Usar modo pasivo para evitar problemas de red
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE); // Manejo seguro de datos binarios

            // Descargar el archivo
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            boolean archivoDescargado = ftpClient.retrieveFile("site/wwwroot/" + nombreArchivo, outputStream);

            if (!archivoDescargado) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // Preparar la respuesta con el archivo
            byte[] archivoBytes = outputStream.toByteArray();
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + nombreArchivo)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(archivoBytes);
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } finally {
            // Cerrar la conexión FTPS
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (Exception ex) {
                System.err.println("Error al cerrar la conexión FTPS: " + ex.getMessage());
            }
        }
    }

    @GetMapping("/descargarErrores")
    public ResponseEntity<Resource> descargarErrores(@RequestParam String periodo) {
        String carpetaArchivos = "/site/wwwroot/webapps/archivos"; // Carpeta donde se guardarán los archivos
        
        // Reemplazar '/' por '-' en el período
        periodo = periodo.replace("/", "-");

        // Concatenar el período al nombre del archivo
        String nombreArchivoErrores = "errores"+periodo+".txt";
        String rutaArchivo = carpetaArchivos + "/" + nombreArchivoErrores; // Ruta completa del archivo

        // Crear la carpeta 'archivos' si no existe
        File carpeta = new File(carpetaArchivos);
        if (!carpeta.exists()) {
            carpeta.mkdirs(); // Crear la carpeta y subcarpetas si no existen
        }

        File archivoErrores = new File(rutaArchivo);

        if (!archivoErrores.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null); // Manejar el caso donde el archivo no existe
        }

        try {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(archivoErrores));

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nombreArchivoErrores);
            headers.add(HttpHeaders.CONTENT_TYPE, "text/plain");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(archivoErrores.length())
                    .body(resource);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}