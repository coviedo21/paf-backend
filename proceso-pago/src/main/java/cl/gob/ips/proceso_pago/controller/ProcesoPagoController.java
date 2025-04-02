package cl.gob.ips.proceso_pago.controller;

import cl.gob.ips.proceso_pago.dto.*;
import cl.gob.ips.proceso_pago.service.CtaCtePAFService;
import cl.gob.ips.proceso_pago.service.EmisionService;
import cl.gob.ips.proceso_pago.service.NominaPagoService;
import cl.gob.ips.proceso_pago.service.ProcesoService;
import com.azure.storage.file.share.ShareFileClient;
import com.azure.storage.file.share.ShareFileClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin("*")
@RequestMapping("/procesoPago")
public class ProcesoPagoController {

    @Autowired
    ProcesoService procesoService;

    @Autowired
    private EmisionService emisionService;

    @Autowired
    private NominaPagoService nominaPagoService;

    @Autowired
    CtaCtePAFService ctaCtePAFService;

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

        } 
        
        if(resultado==-1){
            responseDTO.setCodigoRetorno(-1);
            responseDTO.setGlosaRetorno("No se creó proceso de pago ya que no existen solicitudes pendientes por asignar.");
            Date currentDate = new Date();
            responseDTO.setTimestamp(currentDate);
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }else {
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

    @PostMapping("/procesarEmision")
    public ResponseEntity<ResponseDTO> procesarEmision(@RequestParam("file") MultipartFile file, @RequestParam("idProceso") Integer idProceso) {
        List<EmisionArchivoDTO> registros = new ArrayList<>();
        int[] posiciones = {2, 3, 13, 1, 1, 2, 1, 1, 3, 3, 4, 1, 1, 40, 8, 40, 8, 8, 1, 8, 1, 2, 1, 8, 1, 1, 2, 1, 3, 5, 7, 1, 3, 5, 7, 1, 3, 5, 7, 1, 3, 5, 7, 1, 3, 5, 7, 1, 3, 5, 7, 1, 3, 5, 7, 1, 3, 5, 7, 1, 3, 5, 7, 1, 3, 5, 7, 1, 3, 5, 7, 1, 3, 5, 7, 1, 3, 5, 7, 1, 3, 5, 7,1, 3, 5, 7, 7, 7, 7, 3, 4, 7, 1, 8, 7, 7, 7, 7, 7, 48, 15, 1, 1, 2, 2, 2, 2, 7, 8, 8, 10, 8};
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setTimestamp(new Date());

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
        //String periodoProceso = fechaInicio.getYear() + "/" + String.format("%02d", fechaInicio.getMonthValue());

                registro.setNroResol(valores[111]);
                registro.setFecResol(valores[112]);
                
                registros.add(registro);
                } //Fin While

                if(emisionService.validarSolicitudesEmitidas(registros,idProceso)){
                    try {
                        //String connectionString = System.getenv("AZURE_STORAGE_CONNECTION");
                    	String connectionString = "DefaultEndpointsProtocol=https;AccountName=almacenpagosafqa;AccountKey=+Hxoz3RIALz6dkerrOakHJcJ0T+U5Q/H0wdyS0dAM60S5afSBF/es8bLx78x7gDVQmUmE+WOoD40+AStsOXEHg==;EndpointSuffix=core.windows.net";
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

                        EmisionDTO emision = new EmisionDTO();
                        emision.setFechaEmision(new Date());  
                        emision.setIdProceso(idProceso);
                        emision.setRutaArchivo(nombreRemoto);
                        emisionService.insertarEmision(emision);
                        
                        boolean resultado = procesoService.actualizarEstadoProceso(idProceso, 2);
                        responseDTO.setCodigoRetorno(0);
                        responseDTO.setGlosaRetorno("Emisión procesada exitósamente.");
                        responseDTO.setTimestamp(new Date());

                        try {
                        	SpResponse responseCtaCte = ctaCtePAFService.copiaCteCtePAF(idProceso);
	                        if(responseCtaCte.getResultado()>0) {
	                        	//ctaCtePAFService.procesarYGuardarDatos(idProceso);
	                        }
                        }
                        catch(Exception e) {
                        	System.out.println("Hubo un error al insertar datos en cuenta corriente");
                        }
                        
                        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
                } catch (Exception e) {
                	responseDTO.setCodigoRetorno(-1);
                    responseDTO.setGlosaRetorno("Ocurrió un error al procesar la emisión: "+e.getMessage());
                    responseDTO.setTimestamp(new Date());

                    return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
                }
            }
            else {
            	responseDTO.setCodigoRetorno(-1);
                responseDTO.setGlosaRetorno("Solicitud procesada con errores.");
                responseDTO.setTimestamp(new Date());

                return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseDTO.setCodigoRetorno(-1);
            responseDTO.setGlosaRetorno("Ocurrió un error al leer archivo de emisión: "+e.getMessage());
            responseDTO.setTimestamp(new Date());

            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);

        }

    }

    @GetMapping("/obtenerEmisiones")
    public ResponseEntity<List<EmisionDTO>> obtenerEmisiones() {
        List<EmisionDTO> emisiones = emisionService.obtenerEmisiones();
        if (emisiones != null && !emisiones.isEmpty()) {
            return ResponseEntity.ok(emisiones);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("/insertarEmision")
    public ResponseEntity<ResponseDTO> insertarEmision(
            @RequestBody EmisionDTO emisionDTO) {

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setTimestamp(new Date());

        int resultado = emisionService.insertarEmision(emisionDTO);

        if (resultado>0){
            responseDTO.setCodigoRetorno(0);
            responseDTO.setGlosaRetorno("Emisión creada exitósamente!");
            responseDTO.setResultado(resultado);

            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        } 
        else {
            responseDTO.setCodigoRetorno(-1);
            responseDTO.setGlosaRetorno("No se creó el registro de emisión.");
            Date currentDate = new Date();
            responseDTO.setTimestamp(currentDate);
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/descargarEvidenciaEmision/{idEmision}")
    public ResponseEntity<byte[]> descargarEvidenciaEmision(@PathVariable int idEmision) {
        //String connectionString = System.getenv("AZURE_STORAGE_CONNECTION");
    	String connectionString = "DefaultEndpointsProtocol=https;AccountName=almacenpagosafqa;AccountKey=+Hxoz3RIALz6dkerrOakHJcJ0T+U5Q/H0wdyS0dAM60S5afSBF/es8bLx78x7gDVQmUmE+WOoD40+AStsOXEHg==;EndpointSuffix=core.windows.net";  
    	String fileShareName = "pagosafqa";

        try {
            String rutaArchivo = emisionService.obtenerEmision(idEmision).getRutaArchivo();
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

    @GetMapping(value = "/nomina-pago", produces = "text/csv")
    public void getNominaPagoCSV(@RequestParam("iIdProceso") int iIdProcesoIN,
                                 @RequestParam("fecPago") String fecPago,
                                  HttpServletResponse response) throws IOException {

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"nomina_pago.csv\"");

        nominaPagoService.generateCsvResponse(response, iIdProcesoIN, fecPago);
    }

    @PostMapping("/procesar/{idProceso}")
    public SpResponse copiaCteCtePAF(int iIdProcesoPago) {
        return ctaCtePAFService.copiaCteCtePAF(iIdProcesoPago);
    }
}