package cl.gob.ips.proceso_pago.service.serviceImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import cl.gob.ips.proceso_pago.dao.EmisionDAO;
import cl.gob.ips.proceso_pago.dao.ProcesoDAO;
import cl.gob.ips.proceso_pago.dto.EmisionArchivoDTO;
import cl.gob.ips.proceso_pago.dto.EmisionDTO;
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

    @Override
    public Boolean validarSolicitudesEmitidas(List<EmisionArchivoDTO> emision, int idProceso) {
        if (emision == null || emision.isEmpty()) {
            System.out.println("Lista de emisiones vacía o nula.");
            return false;
        }

        // Obtener las solicitudes asociadas al proceso y agruparlas por RutBeneficiario
        List<SolicitudDTO> solicitudes = procesoDAO.obtenerSolicitudesPorProceso(idProceso);
        
        // Agrupar las solicitudes por RutBeneficiario y sumar sus montos
        Map<String, BigDecimal> solicitudesAgrupadas = solicitudes.stream()
            .filter(s -> s.getRutBeneficiario() != null && s.getMontoHaber() != null) // Filtrar solicitudes válidas
            .collect(Collectors.groupingBy(
                s -> s.getRutBeneficiario().toString(), // Agrupar por RutBeneficiario
                Collectors.reducing(BigDecimal.ZERO, SolicitudDTO::getMontoHaber, BigDecimal::add) // Sumar montos
            ));

        boolean errorEncontrado = false;
        
        // Recorrer las solicitudes agrupadas por RutBeneficiario
        for (Map.Entry<String, BigDecimal> entry : solicitudesAgrupadas.entrySet()) {
            String rut = entry.getKey();
            BigDecimal montoSolicitudes = entry.getValue();
            
            // Buscar si existe un registro en el archivo con el mismo RutBeneficiario
            EmisionArchivoDTO archivoEncontrado = emision.stream()
                .filter(e -> e.getRutBenef().replaceFirst("^0+(?!$)", "").equals(rut))
                .findFirst()
                .orElse(null);
            
            boolean solicitudEncontrada = archivoEncontrado != null && montoSolicitudes.compareTo(new BigDecimal(archivoEncontrado.getTotalHaberes())) == 0;
            
            // Obtener el idSolicitud de una de las solicitudes del grupo
            Integer idSolicitud = solicitudes.stream()
                .filter(s -> s.getRutBeneficiario().toString().equals(rut))
                .map(SolicitudDTO::getIdSolicitud)
                .findFirst()
                .orElse(null);
            
            // Crear la resolución correspondiente
            try {
	            ResolucionDTO resolucion = new ResolucionDTO();
	            resolucion.setIIdSolicitud(idSolicitud);
	            resolucion.setIAutor(1); // Asignar el autor (ajustar si es necesario)
	            resolucion.setVcDescripcion(solicitudEncontrada ? "Solicitud aprobada para pago." : "Solicitud rechazada para pago.");
	            resolucion.setIMotivoRechazo(null);
	            resolucion.setIIdEstado(solicitudEncontrada ? 3 : 5);
	
	            // Mensaje de depuración sobre la validación
	            System.out.println(solicitudEncontrada
	                ? "Solicitud encontrada en emisiones, generando resolución con estado 3."
	                : "Solicitud NO encontrada en emisiones, generando resolución con estado 5.");
	
	            // Llamar a la API externa para insertar la resolución
	            String url = baseUrl + "/insertarResolucion";
	            ResponseDTO response = restTemplate.postForObject(url, resolucion, ResponseDTO.class);
	
	            // Validar la respuesta de la API
	            if (response == null || response.getCodigoRetorno() != 0) {
	                System.out.println("Error al insertar resolución en la API: " + (response != null ? response.getGlosaRetorno() : "Respuesta nula"));
	                errorEncontrado = true;
            }
            }catch(Exception e) {
            	System.out.println("Error al insertar resolucion");
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
}
