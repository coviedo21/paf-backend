package cl.gob.ips.proceso_pago.service.serviceImpl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import cl.gob.ips.proceso_pago.dao.EmisionDAO;
import cl.gob.ips.proceso_pago.dao.ProcesoDAO;
import cl.gob.ips.proceso_pago.dto.CausanteSolicitudDTO;
import cl.gob.ips.proceso_pago.dto.DetalleCausanteDTO;
import cl.gob.ips.proceso_pago.dto.EmisionArchivoDTO;
import cl.gob.ips.proceso_pago.dto.EmisionDTO;
import cl.gob.ips.proceso_pago.dto.PagoRetencionDTO;
import cl.gob.ips.proceso_pago.dto.ProcesoDTO;
import cl.gob.ips.proceso_pago.dto.ResolucionDTO;
import cl.gob.ips.proceso_pago.dto.ResponseDTO;
import cl.gob.ips.proceso_pago.dto.SolicitudDTO;
import cl.gob.ips.proceso_pago.service.EmisionService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmisionServiceImpl implements EmisionService {
    private final RestTemplate restTemplate;

    @Autowired
    private ProcesoDAO procesoDAO;

    @Autowired
    private EmisionDAO emisionDAO;
    
    @Value("${app.base.url}")
    private String baseUrl; 

    @Value("${app.base.urlRetencionPagos}")
    private String baseUrlRetencionPagos; 

    @Override
    public Boolean validarSolicitudesEmitidas(List<EmisionArchivoDTO> emision, int idProceso) {
        if (emision == null || emision.isEmpty()) {
            System.out.println("Lista de emisiones vacía o nula.");
            return false;
        }

        // 1️ Obtener los detalles del proceso de pago
        List<DetalleCausanteDTO> detalles = procesoDAO.obtenerDetallesPorProcesoPago(idProceso);

        // 2️ Agrupar los detalles por rutBeneficiarioPago y sumar los montos de totalPago
        detalles.stream()
        .filter(d -> d != null && d.getTotalPago() == null)
        .forEach(d -> System.out.println("TotalPago null para RUT: " + d.getRutBeneficiarioPago()));
        
        Map<Integer, BigDecimal> detallesAgrupados = detalles.stream()
            .collect(Collectors.groupingBy(
                DetalleCausanteDTO::getRutBeneficiarioPago, // No es necesario verificar null, es un int
                Collectors.reducing(BigDecimal.ZERO, DetalleCausanteDTO::getTotalPago, BigDecimal::add)
            ));

        boolean errorEncontrado = false;

        // 3️ Recorrer los detalles agrupados por rutBeneficiarioPago
        for (Map.Entry<Integer, BigDecimal> entry : detallesAgrupados.entrySet()) {
            int rut = entry.getKey(); // `rutBeneficiarioPago` ya es int, no puede ser null
            BigDecimal montoTotalPago = entry.getValue();

            // Buscar si existe un registro en el archivo con el mismo RutBeneficiarioPago
            EmisionArchivoDTO archivoEncontrado = emision.stream()
                .filter(e -> Integer.parseInt(e.getRutBenef().replaceFirst("^0+(?!$)", "")) == rut)
                .findFirst()
                .orElse(null);

            boolean montosCoinciden = archivoEncontrado != null 
            	    && montoTotalPago.compareTo(new BigDecimal(archivoEncontrado.getTotalHaberes())) == 0;

            // 4️ Obtener todos los detalles del grupo y actualizar con los valores del archivo
            List<DetalleCausanteDTO> detallesDelGrupo = detalles.stream()
                .filter(d -> d.getRutBeneficiarioPago() == rut) // Comparación directa sin equals()
                .collect(Collectors.toList());

            for (DetalleCausanteDTO detalle : detallesDelGrupo) {
                if (archivoEncontrado != null) {
                	detalle.setRutNis(String.valueOf(Long.parseLong(archivoEncontrado.getNis())));
                    detalle.setDvNis(archivoEncontrado.getDvNis());
                    detalle.setNumeroDocumento(Integer.parseInt(archivoEncontrado.getNroDocto()));
                    detalle.setDvDocumento(archivoEncontrado.getDvNroDocto());
                    
                    SimpleDateFormat formato = new SimpleDateFormat("yyyyMMdd");
                    try {
                        Date fecha = formato.parse(archivoEncontrado.getFecPago());
                        System.out.println("Fecha convertida: " + fecha);
                        detalle.setFechaPago(fecha);
                    } catch (ParseException e) {
                        System.out.println("Error al convertir la fecha.");
                    }
                    
                }
                
                
                detalle.setEstado(montosCoinciden ? 4 : 5); // 4 si coincide, 5 si no

                // Llamar a la API para actualizar detalle causante
                String urlActualizar = baseUrl + "/actualizarDetalleCausante";
                restTemplate.postForObject(urlActualizar, detalle, Boolean.class);
                System.out.println("Se actualizó el detalle");
                
                if(detalle.getRutBeneficiarioPago()==detalle.getRutReteniente()) {
                	if(montosCoinciden) {
                		try {
	                		PagoRetencionDTO pagoRetencion = new PagoRetencionDTO();
	                		pagoRetencion.setEstadoPago(1);
	                		pagoRetencion.setFechaPago(detalle.getFechaPago());
	                		pagoRetencion.setIdRetencion(detalle.getIdRetencion());
	                		pagoRetencion.setMontoPagado(detalle.getTotalPago());
	                		pagoRetencion.setTipoPago(5);
	                		insertarPagoRetencion(pagoRetencion);
                		}
                		catch(Exception e) {
                			System.out.println("Error al insertar pago en Retención Judicial");
                		}
                	//Llamar a API de Retencion Judicial para reflejar el pago
                	}	
                }
                
            }
        }

        // 5️ Obtener todas las solicitudes del proceso
        List<SolicitudDTO> solicitudes = procesoDAO.obtenerSolicitudesPorProceso(idProceso);

        // 6️ Todas las solicitudes del proceso cambian de estado a 3
        for (SolicitudDTO solicitud : solicitudes) {
            try {
                // Crear resolución con estado 3
                ResolucionDTO resolucion = new ResolucionDTO();
                resolucion.setIdSolicitud(solicitud.getIdSolicitud());
                resolucion.setAutor(1); // Asignar el autor (ajustar si es necesario)
                resolucion.setVcDescripcion("Solicitud procesada para pago.");
                resolucion.setIdEstado(3);

                // Llamar a la API externa para insertar la resolución
                String url = baseUrl + "/insertarResolucion";
                restTemplate.postForObject(url, resolucion, ResponseDTO.class);

            } catch (Exception e) {
                System.out.println("Error al generar resolución para solicitud ID: " + solicitud.getIdSolicitud());
                e.printStackTrace();
                errorEncontrado = true;
            }
        }

        // Retornar true solo si no hubo errores y la validación de emisiones fue exitosa
        return !errorEncontrado && emisionDAO.validarSolicitudesEmitidas(emision);
    }



    @Override
    public List<EmisionDTO> obtenerEmisiones(){
        return emisionDAO.obtenerEmisiones();
    }

    @Override
    public int insertarEmision(EmisionDTO emision){
        return emisionDAO.insertarEmision(emision);
    }

    @Override
    public ProcesoDTO obtenerProcesoPorPeriodo(String periodo){
        return emisionDAO.obtenerProcesoPorPeriodo(periodo);
    }
    
    @Override
    public EmisionDTO obtenerEmision(int idEmision) {
    	return emisionDAO.obtenerEmision(idEmision);
    }
    
    @Override
    public boolean insertarPagoRetencion(PagoRetencionDTO dto) {
        String url = baseUrlRetencionPagos + "/insertar-pago-retencion";
        System.out.println("Insertando pago en: " + url);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<PagoRetencionDTO> request = new HttpEntity<>(dto, headers);

        try {
            ResponseEntity<ResponseDTO> response = restTemplate.postForEntity(url, request, ResponseDTO.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                System.out.println("Respuesta: " + response.getBody().getGlosaRetorno());
                return true;
            }

            System.err.println("No se insertó el pago. Código: " + response.getStatusCode());
            return false;

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.err.println("Error al insertar pago: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return false;
        } catch (Exception e) {
            System.err.println("Error inesperado al insertar pago: " + e.getMessage());
            return false;
        }
    }


}
