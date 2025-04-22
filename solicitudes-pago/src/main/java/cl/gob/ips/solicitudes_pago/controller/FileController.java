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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPSClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import cl.gob.ips.solicitudes_pago.dto.DetalleCausanteDTO;
import cl.gob.ips.solicitudes_pago.dto.ResponseDTO;
import cl.gob.ips.solicitudes_pago.dto.SolicitudDTO;
import cl.gob.ips.solicitudes_pago.service.CausanteService;
import cl.gob.ips.solicitudes_pago.service.CriterioSolicitudService;
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
    private CausanteService causanteService;
    
    @Value("${app.connectionString}")
    private String connectionString;
    
    @Value("${app.fileShareName}")
    private String fileShareName;
    
    private final Map<String, String> estadoTareas = new ConcurrentHashMap<>();
    private final Map<String, String> mensajesTareas = new ConcurrentHashMap<>(); // Guarda la glosa de respuesta
    private final Map<String, Integer> totalSolicitudes = new ConcurrentHashMap<>();
    private final Map<String, Integer> solicitudesProcesadas = new ConcurrentHashMap<>();
    
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

         // ✅ Contar líneas antes de procesar
            try (InputStream originalInputStream = new FileInputStream(tempFile.toString());
                 BufferedInputStream bufferedInputStream = new BufferedInputStream(originalInputStream)) {

                String encoding = fileService.detectarCodificacion(bufferedInputStream);
                bufferedInputStream.reset();

                InputStream inputStream = encoding.equalsIgnoreCase("UTF-8")
                        ? bufferedInputStream
                        : fileService.convertirAUTF8(bufferedInputStream, encoding);

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                    long total = reader.lines()
                            .skip(1)
                            .filter(line -> !line.trim().isEmpty())
                            .count();
                    totalSolicitudes.put(taskId, (int) total);
                }
            } catch (Exception e) {
                totalSolicitudes.put(taskId, 0); // si falla el conteo
                System.err.println("Error contando líneas del archivo: " + e.getMessage());
            }

            solicitudesProcesadas.put(taskId, 0); // inicializar
            
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
                                int idCuentaBancaria = fileService.obtenerCuentaBancaria(Integer.valueOf(carga.getRutEmpleador()));
                                                                
                                if(idCuentaBancaria==0) {
                                	carga.setIdCuentaBancaria(null);
                                	carga.setFormaPago(4);
                                }
                                else{
                                	carga.setIdCuentaBancaria(idCuentaBancaria);
                                	carga.setFormaPago(8);
                                }
                                
                                listaSolicitudes.add(carga);
                            } catch (Exception e) {
                                // Ignoramos la línea errónea
                            }
                        }

                        
                     //	Guardar total para seguimiento
                        solicitudesProcesadas.put(taskId, 0); // Inicializar en 0
                     // Se inserta en la base de datos y guardamos la glosa
                     // ✅ Llamar al servicio con callback para progreso
                        respuesta = fileService.insertarSolicitudes(listaSolicitudes, periodo, procesados -> {
                            solicitudesProcesadas.put(taskId, procesados);
                        });
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
        else if ("procesando".equals(estado)) {
            int total = totalSolicitudes.getOrDefault(taskId, 0);
            int procesadas = solicitudesProcesadas.getOrDefault(taskId, 0);
            response.put("total", String.valueOf(total));
            response.put("procesadas", String.valueOf(procesadas));
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
            long total = Files.lines(tempFile, StandardCharsets.UTF_8)
            	    .skip(1) // para ignorar la cabecera
            	    .filter(line -> !line.trim().isEmpty()) // opcional, por si hay líneas vacías
            	    .count();
            totalSolicitudes.put(taskId, (int) total);
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
                                int idCuentaBancaria = fileService.obtenerCuentaBancaria(Integer.valueOf(carga.getRutEmpleador()));
                                
                                if(idCuentaBancaria==0) {
                                	carga.setIdCuentaBancaria(null);
                                	carga.setFormaPago(4);
                                }
                                else{
                                	carga.setIdCuentaBancaria(idCuentaBancaria);
                                	carga.setFormaPago(9);
                                }
                                
                                listaSolicitudes.add(carga);
                            } catch (Exception e) {
                                // Ignoramos la línea errónea
                            }
                        }

//                    	Guardar total para seguimiento
                        solicitudesProcesadas.put(taskId, 0); // Inicializar en 0
                     // Se inserta en la base de datos y guardamos la glosa
                     // ✅ Llamar al servicio con callback para progreso
                        respuesta = fileService.insertarSolicitudes(listaSolicitudes, periodo, procesados -> {
                            solicitudesProcesadas.put(taskId, procesados);
                        });
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
    public ResponseEntity<String> subirEvidenciaCausante(@RequestParam("file") MultipartFile file, int idDetalleCausante) {
        try {
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

            DetalleCausanteDTO detalleCausante = causanteService.obtenerDetalleCausantePorIdDetalle(idDetalleCausante);
            detalleCausante.setIdDetalleCausante(idDetalleCausante);
            detalleCausante.setArchivo(nombreRemoto);
            causanteService.actualizarDetalleCausante(detalleCausante);
            return ResponseEntity.ok("✅ Archivo subido correctamente: " + nombreRemoto);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error subiendo el archivo: " + e.getMessage());
        }
    }

    @GetMapping("/descargarEvidenciaSolicitud/{idCriterioSoliciud}")
    public ResponseEntity<byte[]> descargarEvidenciaSolicitud(@PathVariable int idCriterioSoliciud) {
    	    
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

    @GetMapping("/descargarEvidenciaCausante/{idDetalleCausante}")
    public ResponseEntity<byte[]> descargarEvidenciaCausante(@PathVariable int idDetalleCausante) {
    	    
        try {
            String rutaArchivo = causanteService.obtenerDetalleCausantePorIdDetalle(idDetalleCausante).getArchivo();
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

    @PostMapping("/subirEvidenciaFiniquitado")
    public ResponseEntity<String> subirEvidenciaFiniquitado(@RequestParam("file") MultipartFile file) {
        try {
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

            return ResponseEntity.ok(nombreRemoto);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error subiendo el archivo: " + e.getMessage());
        }
    }

    @GetMapping("/descargarEvidenciaFiniquitado/{idSolicitud}")
    public ResponseEntity<byte[]> descargarEvidenciaFiniquitado(@PathVariable int idSolicitud) {
    	    
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

}