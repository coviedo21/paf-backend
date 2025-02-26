package cl.gob.ips.solicitudes_pago.controller;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPSClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ContentDisposition;
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

import com.azure.storage.file.share.ShareFileClient;
import com.azure.storage.file.share.ShareFileClientBuilder;

import cl.gob.ips.solicitudes_pago.dto.ArchivoResponseDTO;
import cl.gob.ips.solicitudes_pago.dto.ArchivoSolicitudDTO;
import cl.gob.ips.solicitudes_pago.dto.CriterioSolicitudCausanteDTO;
import cl.gob.ips.solicitudes_pago.dto.CriterioSolicitudDTO;
import cl.gob.ips.solicitudes_pago.dto.EmisionArchivoDTO;
import cl.gob.ips.solicitudes_pago.dto.ResponseDTO;
import cl.gob.ips.solicitudes_pago.dto.SolicitudDTO;
import cl.gob.ips.solicitudes_pago.service.CriterioSolicitudService;
import cl.gob.ips.solicitudes_pago.service.EmisionService;
import cl.gob.ips.solicitudes_pago.service.FileService;
import cl.gob.ips.solicitudes_pago.service.SolicitudPagoService;

@RestController
@CrossOrigin("*")
@RequestMapping("/archivos")
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private CriterioSolicitudService criterioSolicitudService;

    @Autowired
    private SolicitudPagoService solicitudPagoService;

    @Autowired
    private EmisionService emisionService;

    private final Map<String, String> estadoTareas = new ConcurrentHashMap<>();
    private final Map<String, String> mensajesTareas = new ConcurrentHashMap<>(); // Guarda la glosa de respuesta

    @PostMapping("/cargar-archivo-previred")
    public ResponseDTO cargarArchivoPrevired(@RequestParam("file") MultipartFile file,
                                             @RequestParam("origen") String origen,
                                             @RequestParam("periodo") String periodo) {
        ResponseDTO response = new ResponseDTO();
        String taskId = UUID.randomUUID().toString();
        estadoTareas.put(taskId, "procesando");

        try {
            Path tempFile = Files.createTempFile("previred_", ".csv");
            file.transferTo(tempFile.toFile());

            CompletableFuture.runAsync(() -> {
                List<ArchivoSolicitudDTO> listaSolicitudes = new ArrayList<>();
                ArchivoResponseDTO respuesta = new ArchivoResponseDTO(); // ✅ Definida correctamente

                try (InputStream originalInputStream = new FileInputStream(tempFile.toString());
                     BufferedInputStream bufferedInputStream = new BufferedInputStream(originalInputStream)) {

                    String encoding = fileService.detectarCodificacion(bufferedInputStream);
                    System.out.println("📌 Codificación detectada: " + encoding);

                    bufferedInputStream.reset();

                    InputStream inputStream = encoding.equalsIgnoreCase("UTF-8")
                            ? bufferedInputStream
                            : fileService.convertirAUTF8(bufferedInputStream, encoding);

                    try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                        String line;
                        boolean isFirstLine = true;

                        while ((line = br.readLine()) != null) {
                            if (isFirstLine) {
                                isFirstLine = false;
                                continue;
                            }
                            if (line.trim().isEmpty()) continue;

                            String[] fields = line.split(";");
                            ArchivoSolicitudDTO carga = new ArchivoSolicitudDTO();

                            try {
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
                            } catch (Exception e) {
                                // Ignoramos la línea errónea
                            }
                        }

                        // ✅ Insertamos en la base de datos y guardamos la glosa
                        respuesta = fileService.insertarSolicitudes(listaSolicitudes, periodo);
                        if (respuesta.getRegistrosFallidos() == 0) {
                            mensajesTareas.put(taskId, "Se leyeron " + respuesta.getRegistrosEnArchivo() + " solicitudes y se importaron " + respuesta.getRegistrosImportados() + " solicitudes.");
                        } else {
                            mensajesTareas.put(taskId, "Se leyeron " + respuesta.getRegistrosEnArchivo() + " solicitudes. Se importaron " + respuesta.getRegistrosImportados() + " solicitudes. Fallaron " + respuesta.getRegistrosFallidos() + " solicitudes. Revisar archivo de errores descargado.");
                        }
                        
                        estadoTareas.put(taskId, "completado");

                    }
                } catch (Exception e) {
                    estadoTareas.put(taskId, "error");
                    mensajesTareas.put(taskId, "Error durante el procesamiento.");
                }
            });

            response.setCodigoRetorno(0);
            response.setGlosaRetorno("El archivo se está procesando en segundo plano.");
            response.setResultado(Map.of("taskId", taskId));

        } catch (Exception e) {
            estadoTareas.put(taskId, "error");
            response.setCodigoRetorno(-1);
            response.setGlosaRetorno("Error al leer el archivo: " + e.getMessage());
        }

        return response;
    }

    @GetMapping("/estado/{taskId}")
    public ResponseEntity<Map<String, String>> obtenerEstado(@PathVariable String taskId) {
        String estado = estadoTareas.getOrDefault(taskId, "desconocido");
        Map<String, String> response = new HashMap<>();
        response.put("estado", estado);

        // ✅ Si la tarea está completada, devolver la glosa exacta de respuesta
        if ("completado".equals(estado) && mensajesTareas.containsKey(taskId)) {
            response.put("mensaje", mensajesTareas.get(taskId));
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/cargar-archivo-especiales")
    public ResponseDTO cargarArchivoEspeciales( @RequestParam("file") MultipartFile file,
            @RequestParam("origen") String origen, @RequestParam("periodo") String periodo) {
                ResponseDTO response = new ResponseDTO();
        String taskId = UUID.randomUUID().toString();
        estadoTareas.put(taskId, "procesando");

        try {
            Path tempFile = Files.createTempFile("previred_", ".csv");
            file.transferTo(tempFile.toFile());

            CompletableFuture.runAsync(() -> {
                List<ArchivoSolicitudDTO> listaSolicitudes = new ArrayList<>();
                ArchivoResponseDTO respuesta = new ArchivoResponseDTO(); // ✅ Definida correctamente

                try (InputStream originalInputStream = new FileInputStream(tempFile.toString());
                     BufferedInputStream bufferedInputStream = new BufferedInputStream(originalInputStream)) {

                    String encoding = fileService.detectarCodificacion(bufferedInputStream);
                    System.out.println("📌 Codificación detectada: " + encoding);

                    bufferedInputStream.reset();

                    InputStream inputStream = encoding.equalsIgnoreCase("UTF-8")
                            ? bufferedInputStream
                            : fileService.convertirAUTF8(bufferedInputStream, encoding);

                    try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                        String line;
                        boolean isFirstLine = true;

                        while ((line = br.readLine()) != null) {
                            if (isFirstLine) {
                                isFirstLine = false;
                                continue;
                            }
                            if (line.trim().isEmpty()) continue;

                            String[] fields = line.split(";");
                            ArchivoSolicitudDTO carga = new ArchivoSolicitudDTO();

                            try {
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
                            } catch (Exception e) {
                                // Ignoramos la línea errónea
                            }
                        }

                        // ✅ Insertamos en la base de datos y guardamos la glosa
                        respuesta = fileService.insertarSolicitudes(listaSolicitudes, periodo);
                        if (respuesta.getRegistrosFallidos() == 0) {
                            mensajesTareas.put(taskId, "Se leyeron " + respuesta.getRegistrosEnArchivo() + " solicitudes y se importaron " + respuesta.getRegistrosImportados() + " solicitudes.");
                        } else {
                            mensajesTareas.put(taskId, "Se leyeron " + respuesta.getRegistrosEnArchivo() + " solicitudes. Se importaron " + respuesta.getRegistrosImportados() + " solicitudes. Fallaron " + respuesta.getRegistrosFallidos() + " solicitudes. Revisar archivo de errores descargado.");
                        }
                        
                        estadoTareas.put(taskId, "completado");

                    }
                } catch (Exception e) {
                    estadoTareas.put(taskId, "error");
                    mensajesTareas.put(taskId, "Error durante el procesamiento.");
                }
            });

            response.setCodigoRetorno(0);
            response.setGlosaRetorno("El archivo se está procesando en segundo plano.");
            response.setResultado(Map.of("taskId", taskId));

        } catch (Exception e) {
            estadoTareas.put(taskId, "error");
            response.setCodigoRetorno(-1);
            response.setGlosaRetorno("Error al leer el archivo: " + e.getMessage());
        }

        return response;
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
        String carpetaArchivos = ""; // Carpeta donde se guardarán los archivos
        
        // Reemplazar '/' por '-' en el período
        periodo = periodo.replace("/", "-");

        // Concatenar el período al nombre del archivo
        String nombreArchivoErrores = "errores"+periodo+".txt";
        String rutaArchivo = nombreArchivoErrores; // Ruta completa del archivo

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

    @PostMapping("/subirEvidenciaSolicitud")
    public ResponseEntity<String> subirEvidenciaSolicitud(@RequestParam("file") MultipartFile file, int idCriterioSolicitud) {
        try {
            String connectionString = System.getenv("AZURE_STORAGE_CONNECTION");
            String fileShareName = "pagosafqa";
            String nombreRemoto = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            // Guardar temporalmente el archivo
            Path tempFile = Files.createTempFile("upload-", nombreRemoto);
            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            // Crear cliente para subir archivo a Azure
            ShareFileClient fileClient = new ShareFileClientBuilder()
                    .connectionString(connectionString)
                    .shareName(fileShareName)
                    .resourcePath(nombreRemoto)
                    .buildFileClient();

            fileClient.create(file.getSize());
            fileClient.uploadFromFile(tempFile.toString());


            CriterioSolicitudDTO criterio = criterioSolicitudService.obtenerCriteriosPorIdCriterio(idCriterioSolicitud);
            criterio.setArchivo(nombreRemoto);
            criterioSolicitudService.actualizarCriterioSolicitud(criterio);
            return ResponseEntity.ok("✅ Archivo subido correctamente: " + nombreRemoto);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error subiendo el archivo: " + e.getMessage());
        }
    }

    @PostMapping("/subirEvidenciaCausante")
    public ResponseEntity<String> subirEvidenciaCausante(@RequestParam("file") MultipartFile file, int idCriterioCausante) {
        try {
            String connectionString = System.getenv("AZURE_STORAGE_CONNECTION");
            String fileShareName = "pagosafqa";
            String nombreRemoto = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            // Guardar temporalmente el archivo
            Path tempFile = Files.createTempFile("upload-", nombreRemoto);
            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            // Crear cliente para subir archivo a Azure
            ShareFileClient fileClient = new ShareFileClientBuilder()
                    .connectionString(connectionString)
                    .shareName(fileShareName)
                    .resourcePath(nombreRemoto)
                    .buildFileClient();

            fileClient.create(file.getSize());
            fileClient.uploadFromFile(tempFile.toString());

            CriterioSolicitudCausanteDTO criterio = criterioSolicitudService.obtenerCriterioCausantePorIdCriterio(idCriterioCausante);
            criterio.setIdCriterioSolicitudCausante(idCriterioCausante);
            criterio.setArchivo(nombreRemoto);
            criterioSolicitudService.actualizarCriterioCausante(criterio);
            return ResponseEntity.ok("✅ Archivo subido correctamente: " + nombreRemoto);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error subiendo el archivo: " + e.getMessage());
        }
    }

    @GetMapping("/descargarEvidenciaSolicitud/{idCriterioSoliciud}")
    public ResponseEntity<byte[]> descargarEvidenciaSolicitud(@PathVariable int idCriterioSoliciud) {
        String connectionString = System.getenv("AZURE_STORAGE_CONNECTION");
        System.out.println("Conexión a Azure: " + connectionString);
            String fileShareName = "pagosafqa";
            
        try {
            String rutaArchivo = criterioSolicitudService.obtenerCriteriosPorIdCriterio(idCriterioSoliciud).getArchivo();
            ShareFileClient fileClient = new ShareFileClientBuilder()
                    .connectionString(connectionString)
                    .shareName(fileShareName)
                    .resourcePath(rutaArchivo)
                    .buildFileClient();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            fileClient.download(outputStream);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(ContentDisposition.attachment().filename(rutaArchivo).build());

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error al descargar archivo: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/descargarEvidenciaCausante/{idCriterioCausante}")
    public ResponseEntity<byte[]> descargarEvidenciaCausante(@PathVariable int idCriterioCausante) {
        String connectionString = System.getenv("AZURE_STORAGE_CONNECTION");
            String fileShareName = "pagosafqa";
            
        try {
            String rutaArchivo = criterioSolicitudService.obtenerCriterioCausantePorIdCriterio(idCriterioCausante).getArchivo();
            ShareFileClient fileClient = new ShareFileClientBuilder()
                    .connectionString(connectionString)
                    .shareName(fileShareName)
                    .resourcePath(rutaArchivo)
                    .buildFileClient();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            fileClient.download(outputStream);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(ContentDisposition.attachment().filename(rutaArchivo).build());

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error al descargar archivo: " + e.getMessage()).getBytes());
        }
    }

    @PostMapping("/procesarEmision")
    public List<EmisionArchivoDTO> procesarEmision(@RequestParam("file") MultipartFile file) {
        List<EmisionArchivoDTO> registros = new ArrayList<>();
        int[] posiciones = {2, 3, 13, 1, 1, 2, 1, 1, 3, 3, 4, 1, 1, 40, 8, 40, 8, 8, 1, 8, 1, 2, 1, 8, 1, 1, 2, 1, 3, 5, 7, 1, 3, 5, 7, 1, 3, 5, 7, 1, 3, 5, 7, 1, 3, 5, 7, 1, 3, 5, 7, 1, 3, 5, 7, 1, 3, 5, 7, 1, 3, 5, 7, 1, 3, 5, 7, 1, 3, 5, 7, 1, 3, 5, 7, 1, 3, 5, 7, 1, 3, 5, 7,1, 3, 5, 7, 7, 7, 7, 3, 4, 7, 1, 8, 7, 7, 7, 7, 7, 48, 15, 1, 1, 2, 2, 2, 2, 7, 8, 8, 10, 8};
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }
                
                EmisionArchivoDTO registro = new EmisionArchivoDTO();
                int inicio = 0;
                
                String[] valores = new String[posiciones.length];
                for (int i = 0; i < posiciones.length; i++) {
                    int fin = inicio + posiciones[i];
                    if (fin > linea.length()) {
                        valores[i] = "";
                    } else {
                        valores[i] = linea.substring(inicio, fin).trim();
                    }
                    inicio = fin;
                }
                
                registro.setCodInst(valores[0]);
                registro.setCodMuni(valores[1]);
                registro.setNis(valores[2]);
                registro.setDvNis(valores[3]);
                registro.setGrupPago(valores[4]);
                registro.setNroBenef(valores[5]);
                registro.setTipPens(valores[6]);
                registro.setNroReten(valores[7]);
                registro.setCodOrigLeg(valores[8]);
                registro.setCodSucINP(valores[9]);
                registro.setCilp(valores[10]);
                registro.setDvCilp(valores[11]);
                registro.setCodFormPago(valores[12]);
                registro.setNomBenef(valores[13]);
                registro.setFecVenceBenef(valores[14]);
                registro.setNomApodera(valores[15]);
                registro.setFecVigPoder(valores[16]);
                registro.setRutBenef(valores[17]);
                registro.setDvBenef(valores[18]);
                registro.setRutApodera(valores[19]);
                registro.setDvApodera(valores[20]);
                registro.setCantCargaFamSimple(valores[21]);
                registro.setCantCargaFamDuplo(valores[22]);
                registro.setFecProxPago(valores[23]);
                registro.setIndicaFecProxPago(valores[24]);
                registro.setFranjaHoraria(valores[25]);
                registro.setCantOcurre(valores[26]);
                
                registro.setHDtipoRegistro1(valores[27]);
registro.setHDcodigoHD1(valores[28]);
registro.setHDatributo1(valores[29]);
registro.setHDmonto1(valores[30]);

registro.setHDtipoRegistro2(valores[31]);
registro.setHDcodigoHD2(valores[32]);
registro.setHDatributo2(valores[33]);
registro.setHDmonto2(valores[34]);

registro.setHDtipoRegistro3(valores[35]);
registro.setHDcodigoHD3(valores[36]);
registro.setHDatributo3(valores[37]);
registro.setHDmonto3(valores[38]);

registro.setHDtipoRegistro4(valores[39]);
registro.setHDcodigoHD4(valores[40]);
registro.setHDatributo4(valores[41]);
registro.setHDmonto4(valores[42]);

registro.setHDtipoRegistro5(valores[43]);
registro.setHDcodigoHD5(valores[44]);
registro.setHDatributo5(valores[45]);
registro.setHDmonto5(valores[46]);

registro.setHDtipoRegistro6(valores[47]);
registro.setHDcodigoHD6(valores[48]);
registro.setHDatributo6(valores[49]);
registro.setHDmonto6(valores[50]);

registro.setHDtipoRegistro7(valores[51]);
registro.setHDcodigoHD7(valores[52]);
registro.setHDatributo7(valores[53]);
registro.setHDmonto7(valores[54]);

registro.setHDtipoRegistro8(valores[55]);
registro.setHDcodigoHD8(valores[56]);
registro.setHDatributo8(valores[57]);
registro.setHDmonto8(valores[58]);

registro.setHDtipoRegistro9(valores[59]);
registro.setHDcodigoHD9(valores[60]);
registro.setHDatributo9(valores[61]);
registro.setHDmonto9(valores[62]);

registro.setHDtipoRegistro10(valores[63]);
registro.setHDcodigoHD10(valores[64]);
registro.setHDatributo10(valores[65]);
registro.setHDmonto10(valores[66]);

registro.setHDtipoRegistro11(valores[67]);
registro.setHDcodigoHD11(valores[68]);
registro.setHDatributo11(valores[69]);
registro.setHDmonto11(valores[70]);

registro.setHDtipoRegistro12(valores[71]);
registro.setHDcodigoHD12(valores[72]);
registro.setHDatributo12(valores[73]);
registro.setHDmonto12(valores[74]);

registro.setHDtipoRegistro13(valores[75]);
registro.setHDcodigoHD13(valores[76]);
registro.setHDatributo13(valores[77]);
registro.setHDmonto13(valores[78]);

registro.setHDtipoRegistro14(valores[79]);
registro.setHDcodigoHD14(valores[80]);
registro.setHDatributo14(valores[81]);
registro.setHDmonto14(valores[82]);

registro.setHDtipoRegistro15(valores[83]);
registro.setHDcodigoHD15(valores[84]);
registro.setHDatributo15(valores[85]);
registro.setHDmonto15(valores[86]);
                
                registro.setTotalHaberes(valores[87]);
                registro.setTotalDescuentos(valores[88]);
                registro.setLiquidoaPagar(valores[89]);
                registro.setCodBanco(valores[90]);
                registro.setCodPlazaPago(valores[91]);
                registro.setNroDocto(valores[92]);
                registro.setDvNroDocto(valores[93]);
                registro.setFecPago(valores[94]);
                registro.setMontoAfectoImpos(valores[95]);
                registro.setAporteSaludExento(valores[96]);
                registro.setAporteSaludTributa(valores[97]);
                registro.setMontoOtrosDsctosExen(valores[98]);
                registro.setMontoAfectoImpstoRen(valores[99]);
                registro.setDireccion(valores[100]);
                registro.setCiudad(valores[101]);
                registro.setIndMsjeGnral(valores[102]);
                registro.setIndMsjeSelect(valores[103]);
                registro.setMScodigo1(valores[104]);
                registro.setMScodigo2(valores[105]);
                registro.setMScodigo3(valores[106]);
                registro.setMScodigo4(valores[107]);
                registro.setMontoPensActual(valores[108]);
                registro.setFecIniPeriodo(valores[109]);
                registro.setFecFinPago(valores[110]);
                // Convertir a LocalDate
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate fechaInicio = LocalDate.parse(registro.getFecIniPeriodo(), formatter);

        // Obtener el año y mes en formato "yyyy/MM"
        String periodoInicio = fechaInicio.getYear() + "/" + String.format("%02d", fechaInicio.getMonthValue());

                registro.setNroResol(valores[111]);
                registro.setFecResol(valores[112]);
                
                registros.add(registro);
                } //Fin While

                if(emisionService.validarSolicitudesEmitidas(registros)){
                    try {
                        String connectionString = System.getenv("AZURE_STORAGE_CONNECTION");
                        String fileShareName = "pagosafqa";
                        String nombreRemoto = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            
                        // Guardar temporalmente el archivo
                        Path tempFile = Files.createTempFile("upload-", nombreRemoto);
                        Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
            
                        // Crear cliente para subir archivo a Azure
                        ShareFileClient fileClient = new ShareFileClientBuilder()
                                .connectionString(connectionString)
                                .shareName(fileShareName)
                                .resourcePath(nombreRemoto)
                                .buildFileClient();
            
                        fileClient.create(file.getSize());
                        fileClient.uploadFromFile(tempFile.toString());

                        /*TO DO 
                            Crear DTO para la tabla Emision y crear nuevo registro en esta tabla
                            Asignando en el campo nombreArchivo el nombreRemoto.
                        */
                } catch (Exception e) {
                    return registros;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return registros;
    }


    @PostMapping("/subirEvidenciaFiniquitado")
    public ResponseEntity<String> subirEvidenciaFiniquitado(@RequestParam("file") MultipartFile file, int idSolicitud) {
        try {
            String connectionString = System.getenv("AZURE_STORAGE_CONNECTION");
            String fileShareName = "pagosafqa";
            String nombreRemoto = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            // Guardar temporalmente el archivo
            Path tempFile = Files.createTempFile("upload-", nombreRemoto);
            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            // Crear cliente para subir archivo a Azure
            ShareFileClient fileClient = new ShareFileClientBuilder()
                    .connectionString(connectionString)
                    .shareName(fileShareName)
                    .resourcePath(nombreRemoto)
                    .buildFileClient();

            fileClient.create(file.getSize());
            fileClient.uploadFromFile(tempFile.toString());

            SolicitudDTO solicitud = solicitudPagoService.consultarSolicitudPago(idSolicitud).get(0);
            solicitud.setFiniquito(nombreRemoto);
            solicitudPagoService.actualizarSolicitudPago(solicitud);
            return ResponseEntity.ok("✅ Archivo subido correctamente: " + nombreRemoto);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error subiendo el archivo: " + e.getMessage());
        }
    }

    @GetMapping("/descargarEvidenciaFiniquitado/{idSolicitud}")
    public ResponseEntity<byte[]> descargarEvidenciaFiniquitado(@PathVariable int idSolicitud) {
        String connectionString = System.getenv("AZURE_STORAGE_CONNECTION");
            String fileShareName = "pagosafqa";
            
        try {
            String rutaArchivo = solicitudPagoService.consultarSolicitudPago(idSolicitud).get(0).getFiniquito();
            ShareFileClient fileClient = new ShareFileClientBuilder()
                    .connectionString(connectionString)
                    .shareName(fileShareName)
                    .resourcePath(rutaArchivo)
                    .buildFileClient();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            fileClient.download(outputStream);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(ContentDisposition.attachment().filename(rutaArchivo).build());

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error al descargar archivo: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/descargarEvidenciaEmision/{idEmision}")
    public ResponseEntity<byte[]> descargarEvidenciaEmisoin(@PathVariable int idEmision) {
        /*String connectionString = System.getenv("AZURE_STORAGE_CONNECTION");
            String fileShareName = "pagosafqa";
            
        try {
            String rutaArchivo = emisionService.obtenerEmision(idEmision).getArhivo();
            ShareFileClient fileClient = new ShareFileClientBuilder()
                    .connectionString(connectionString)
                    .shareName(fileShareName)
                    .resourcePath(rutaArchivo)
                    .buildFileClient();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            fileClient.download(outputStream);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(ContentDisposition.attachment().filename(rutaArchivo).build());

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error al descargar archivo: " + e.getMessage()).getBytes());
        }*/
        return null;
    }
}