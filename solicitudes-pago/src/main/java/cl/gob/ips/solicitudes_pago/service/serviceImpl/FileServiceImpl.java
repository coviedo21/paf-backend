package cl.gob.ips.solicitudes_pago.service.serviceImpl;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.azure.storage.file.share.ShareDirectoryClient;
import com.azure.storage.file.share.ShareFileClient;
import com.azure.storage.file.share.ShareFileClientBuilder;

import cl.gob.ips.solicitudes_pago.dao.FileDAO;
import cl.gob.ips.solicitudes_pago.dto.ArchivoResponseDTO;
import cl.gob.ips.solicitudes_pago.dto.ArchivoSolicitudDTO;
import cl.gob.ips.solicitudes_pago.dto.ListaComunaDTO;
import cl.gob.ips.solicitudes_pago.dto.ResultadoRegionDTO;
import cl.gob.ips.solicitudes_pago.service.CriterioSolicitudService;
import cl.gob.ips.solicitudes_pago.service.FileService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final RestTemplate restTemplate;

    @Autowired
    private FileDAO fileDAO;
    
    //@Autowired
    //private ComunaService comunaService;

    @Autowired
    private CriterioSolicitudService criterioSolicitudService;

    @Value("${app.base.url}")
    private String baseUrl;    
    
    public ArchivoResponseDTO insertarSolicitudes(List<ArchivoSolicitudDTO> listaSolicitudes, String periodo) {
        Map<String, ListaComunaDTO> comunaCache = new HashMap<>();
        Map<String, ResultadoRegionDTO> regionCache = new HashMap<>();
        periodo = periodo.replace("/", "-");
        String nombreArchivoErrores = "errores"+periodo+".txt"; // Nombre del archivo donde se guardarán las líneas con errores
        int contadorRegistros = 0;
        int contadorExitos = 0;
        int contadorErrores = 0;
        ArchivoResponseDTO respuesta = new ArchivoResponseDTO();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nombreArchivoErrores))) {
            // Escribir la cabecera del archivo de errores
            /*String cabecera = "Folio;Fecha - hora Declaración de cargas familiares;RUT Empleador;Digito Verificador Empleador;" +
                            "Razón Social Empleador;Dirección Empleador;Email Empleador;Comuna Empleador;Ciudad Empleador;" +
                            "Región Empleador;RUT Trabajador;Dv Trabajador;Apellido Paterno Trabajador;Apellidos Materno Trabajador;" +
                            "Nombres Trabajador;RUT Carga Familiar;Dígito Verificador Carga familiar;Apellido Paterno Carga;" +
                            "Apellidos Materno Carga;Nombres Carga;Tipo Carga;Fecha Inicio Compensación;Fecha Fin Compensación;Estado de la Carga";
            bw.write(cabecera);*/
            //bw.newLine(); // Saltar a la siguiente línea después de la cabecera

            for (ArchivoSolicitudDTO archivo : listaSolicitudes) {
                contadorRegistros++;
                String regionKey = archivo.getNombreRegion().trim().toUpperCase();
                ResultadoRegionDTO region = null;

                if (regionCache.containsKey(regionKey)) {
                    System.out.println("La región ya fue validada: " + regionKey);
                    region = regionCache.get(regionKey);
                } else {
                    // Validar la región si no está en la caché
                    //String url = String.format("https://pagosafback-dev.azurewebsites.net/pagos-asignacion-familiar-v1/pagos/crear-solicitud");
                    String url = String.format(baseUrl+"/validarRegion/"+archivo.getNombreRegion());
                    region = restTemplate.getForObject(url, ResultadoRegionDTO.class);
                    //region = comunaService.validarRegion(archivo.getRegionEmpleador());
                    if (region != null) {
                        regionCache.put(regionKey, region);
                    } else {
                        // Manejo de casos donde no se encontró la región
                        archivo.setNombreRegion("**ERROR**" + archivo.getNombreRegion());
                    }
                }

                if (region != null) {
                    archivo.setIdRegion(region.getCodigoRegion());
                    archivo.setNombreRegion(region.getNombreRegion());
                    String comunaKey = region.getCodigoRegion() + "|" + archivo.getComunaEmpleador().trim().toUpperCase();

                    ListaComunaDTO comuna = null;
                    // Verificar si la comuna ya ha sido validada previamente
                    if (comunaCache.containsKey(comunaKey)) {
                        comuna = comunaCache.get(comunaKey);
                    } else {
                        // Validar la comuna si no está en la caché
                        String url = String.format(baseUrl+"/validarComuna/"+region.getCodigoRegion()+"/"+archivo.getComunaEmpleador());
                        comuna = restTemplate.getForObject(url, ListaComunaDTO.class);
                        //comuna = comunaService.validarComuna(region.getCodigoRegion(), archivo.getComunaEmpleador());
                        if (comuna != null) {
                            // Guardar la comuna validada en la caché
                            comunaCache.put(comunaKey, comuna);
                        } else {
                            // Manejo de casos donde no se encontró la comuna
                            archivo.setComunaEmpleador("**ERROR**" + archivo.getComunaEmpleador());
                        }
                    }

                    if (comuna != null) {
                        archivo.setIdComuna(comuna.getCodComuna());
                        archivo.setNombreComuna(comuna.getNombreComuna());
                    } else {
                        // Escribir la línea con error en comuna
                        bw.write(formatearLineaError(archivo));
                        bw.newLine();
                        continue; // Saltar al siguiente archivo
                    }
                } else {
                    // Si no se encontró la región, escribir la línea con error en región
                    bw.write(formatearLineaError(archivo));
                    bw.newLine();
                    continue; // Saltar al siguiente archivo
                }

                // Validar RUT Empleador
                if (!criterioSolicitudService.validarRolUnicoTributario(archivo.getRutEmpleador()+archivo.getDvEmpleador())) {
                    archivo.setRutEmpleador("**ERROR**" + archivo.getRutEmpleador());
                    //archivo.setDvEmpleador("**ERROR**" + archivo.getDvEmpleador());
                    bw.write(formatearLineaError(archivo));
                    bw.newLine();
                    continue; // Saltar al siguiente archivo
                }

                //Validar RUT Trabajador
                if (!criterioSolicitudService.validarRolUnicoTributario(archivo.getRutTrabajador()+archivo.getDvTrabajador())) {
                    archivo.setRutTrabajador("**ERROR**" + archivo.getRutTrabajador());
                    //archivo.setDvEmpleador("**ERROR**" + archivo.getDvEmpleador());
                    bw.write(formatearLineaError(archivo));
                    bw.newLine();
                    continue; // Saltar al siguiente archivo
                }

                //Validar RUT Carga Familiar
                if (!criterioSolicitudService.validarRolUnicoTributario(archivo.getRutCargaFamiliar()+archivo.getDvCargaFamiliar())) {
                    archivo.setRutCargaFamiliar("**ERROR**" + archivo.getRutCargaFamiliar());
                    //archivo.setDvEmpleador("**ERROR**" + archivo.getDvEmpleador());
                    bw.write(formatearLineaError(archivo));
                    bw.newLine();
                    continue; // Saltar al siguiente archivo
                }

                // Finalmente, insertar la solicitud si todo está correcto
                boolean insertado = fileDAO.insertarSolicitud(archivo);
                if(insertado){
                    contadorExitos++;
                }
                else{
                    bw.write("**ERROR** Solicitud ya existe. Rut Beneficiario: "+archivo.getRutTrabajador()+"-"+archivo.getDvTrabajador()+", Rut Causante: "+archivo.getRutCargaFamiliar()+"-"+archivo.getDvCargaFamiliar()+" Periodo: "+archivo.getPeriodo());
                }
            }
            contadorErrores = contadorRegistros-contadorExitos;
            respuesta.setRegistrosEnArchivo(contadorRegistros);
            respuesta.setRegistrosImportados(contadorExitos);
            respuesta.setRegistrosFallidos(contadorErrores);
            return respuesta;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Método para formatear la línea con el error
    private String formatearLineaError(ArchivoSolicitudDTO archivo) {
        return archivo.getFolio() + ";" +
            archivo.getFechaHora() + ";" +
            archivo.getRutEmpleador() + ";" +
            archivo.getDvEmpleador() + ";" +
            archivo.getRazonSocialEmpleador() + ";" +
            archivo.getDireccionEmpleador() + ";" +
            archivo.getEmailEmpleador() + ";" +
            archivo.getComunaEmpleador() + ";" +
            archivo.getCiudadEmpleador() + ";" +
            archivo.getNombreRegion() + ";" +
            archivo.getRutTrabajador() + ";" +
            archivo.getDvTrabajador() + ";" +
            archivo.getApellidoPaternoTrabajador() + ";" +
            archivo.getApellidoMaternoTrabajador() + ";" +
            archivo.getNombresTrabajador() + ";" +
            archivo.getRutCargaFamiliar() + ";" +
            archivo.getDvCargaFamiliar() + ";" +
            archivo.getApellidoPaternoCarga() + ";" +
            archivo.getApellidoMaternoCarga() + ";" +
            archivo.getNombresCarga() + ";" +
            archivo.getTipoCarga() + ";" +
            archivo.getFechaInicioCompensacion() + ";" +
            archivo.getFechaFinCompensacion() + ";" +
            archivo.getEstadoCarga();
    }

    public Boolean uploadFileAzure(String connectStr, String shareName,
            String dirName, String fileName, byte[] file) {
        try {
            System.out.println("==°°==°°==°°== DENTRO DE UPLOADFILEAZURE ==°°==°°==°°==");
            ShareDirectoryClient dirClient = new ShareFileClientBuilder()
                    .connectionString(connectStr)
                    .shareName(shareName)
                    .resourcePath(dirName)
                    .buildDirectoryClient();
                    
                    System.out.println("==°°==°°==°°== FILENAME ==°°==°°==°°==");
                    System.out.println(fileName);
                    System.out.println("==°°==°°==°°== FILE ==°°==°°==°°==");
                    System.out.println(file);
            ShareFileClient fileClient = dirClient.getFileClient(fileName);

            fileClient.create(file.length);

            InputStream inputStream = new ByteArrayInputStream(file);

            fileClient.uploadRange(inputStream, file.length);

            return true;
        } catch (Exception e) {
            System.out.println("uploadFile exception: " + e.getMessage());
            return false;
        }
    }

    @Override
    public byte[] downloadFile(String connectStr, String shareName, String dirName, String fileName) {
        try {
            ShareDirectoryClient dirClient = new ShareFileClientBuilder()
                .connectionString(connectStr)
                .shareName(shareName)
                .resourcePath(dirName)
                .buildDirectoryClient();

            ShareFileClient fileClient = dirClient.getFileClient(fileName);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            fileClient.download(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            System.out.println("downloadFile exception: " + e.getMessage());
            return null;
        }
    }
}