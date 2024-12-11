package cl.gob.ips.pagos_asignacion_familiar.service.serviceImpl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.gob.ips.pagos_asignacion_familiar.dao.FileDAO;
import cl.gob.ips.pagos_asignacion_familiar.dto.ArchivoCuentaBancariaDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.ArchivoSolicitudDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.CuentaBancariaDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.ListaComuna;
import cl.gob.ips.pagos_asignacion_familiar.dto.ResultadoRegionDTO;
import cl.gob.ips.pagos_asignacion_familiar.service.ComunaService;
import cl.gob.ips.pagos_asignacion_familiar.service.CriterioSolicitudService;
import cl.gob.ips.pagos_asignacion_familiar.service.FileService;

@Service
public class FileServiceImpl implements FileService {
    @Autowired
    private FileDAO fileDAO;
    
    @Autowired
    private ComunaService comunaService;

    @Autowired
    private CriterioSolicitudService criterioSolicitudService;

    public void insertarSolicitud(List<ArchivoSolicitudDTO> listaSolicitudes) {
        Map<String, ListaComuna> comunaCache = new HashMap<>();
        Map<String, ResultadoRegionDTO> regionCache = new HashMap<>();
        String nombreArchivoErrores = "errores.txt"; // Nombre del archivo donde se guardarán las líneas con errores

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nombreArchivoErrores))) {
            // Escribir la cabecera del archivo de errores
            String cabecera = "Folio;Fecha - hora Declaración de cargas familiares;RUT Empleador;Digito Verificador Empleador;" +
                            "Razón Social Empleador;Dirección Empleador;Email Empleador;Comuna Empleador;Ciudad Empleador;" +
                            "Región Empleador;RUT Trabajador;Dv Trabajador;Apellido Paterno Trabajador;Apellidos Materno Trabajador;" +
                            "Nombres Trabajador;RUT Carga Familiar;Dígito Verificador Carga familiar;Apellido Paterno Carga;" +
                            "Apellidos Materno Carga;Nombres Carga;Tipo Carga;Fecha Inicio Compensación;Fecha Fin Compensación;Estado de la Carga";
            bw.write(cabecera);
            bw.newLine(); // Saltar a la siguiente línea después de la cabecera

            for (ArchivoSolicitudDTO archivo : listaSolicitudes) {
                String regionKey = archivo.getRegionEmpleador().trim().toUpperCase();
                ResultadoRegionDTO region = null;

                if (regionCache.containsKey(regionKey)) {
                    System.out.println("La región ya fue validada: " + regionKey);
                    region = regionCache.get(regionKey);
                } else {
                    // Validar la región si no está en la caché
                    region = comunaService.validarRegion(archivo.getRegionEmpleador());
                    if (region != null) {
                        regionCache.put(regionKey, region);
                    } else {
                        // Manejo de casos donde no se encontró la región
                        archivo.setRegionEmpleador("**ERROR**" + archivo.getRegionEmpleador());
                    }
                }

                if (region != null) {
                    String comunaKey = region.getCodigoRegion() + "|" + archivo.getComunaEmpleador().trim().toUpperCase();

                    ListaComuna comuna = null;
                    // Verificar si la comuna ya ha sido validada previamente
                    if (comunaCache.containsKey(comunaKey)) {
                        comuna = comunaCache.get(comunaKey);
                    } else {
                        // Validar la comuna si no está en la caché
                        comuna = comunaService.validarComuna(region.getCodigoRegion(), archivo.getComunaEmpleador());
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
                fileDAO.insertarSolicitud(archivo);
            }
        } catch (IOException e) {
            e.printStackTrace();
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
           archivo.getRegionEmpleador() + ";" +
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

    public void insertarCuentasBancarias(List<ArchivoCuentaBancariaDTO> listaCuentasBancarias){
        for (ArchivoCuentaBancariaDTO archivo : listaCuentasBancarias) {
            CuentaBancariaDTO cuentaBancaria = new CuentaBancariaDTO();
            cuentaBancaria.setRutTitular(Integer.parseInt(archivo.getRutTitular()));
            cuentaBancaria.setDvTitular(archivo.getDvTitular());
            cuentaBancaria.setNumeroCuenta(archivo.getNumeroCuenta());
            cuentaBancaria.setCodigoBanco(Integer.parseInt(archivo.getCodigoBanco()));
            cuentaBancaria.setTipoCuenta(Integer.parseInt(archivo.getTipoCuenta()));
            cuentaBancaria.setEstadoCuenta(Integer.parseInt(archivo.getEstadoCuenta()));
            cuentaBancaria.setOrigen(5);
            fileDAO.insertarCuentaBancaria(cuentaBancaria);
        }
    }
}