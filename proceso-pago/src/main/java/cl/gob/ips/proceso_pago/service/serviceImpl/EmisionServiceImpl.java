package cl.gob.ips.proceso_pago.service.serviceImpl;

import java.math.BigDecimal;
import java.util.List;

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

        // Obtener las solicitudes asociadas al proceso
        List<SolicitudDTO> solicitudes = procesoDAO.obtenerSolicitudesPorProceso(idProceso);

        for (SolicitudDTO solicitud : solicitudes) {
            try {
                // Buscar si la solicitud está en la lista de emisiones
                boolean solicitudEncontrada = emision.stream()
                    .peek(archivo -> {
                        // Eliminar ceros a la izquierda del RUT Beneficiario
                        String rutArchivo = archivo.getRutBenef().replaceFirst("^0+(?!$)", ""); 
                        System.out.println("Comparando RUT Beneficiario: " + solicitud.getRutBeneficiario() + " con " + rutArchivo);
                        System.out.println("Comparando Haber: " + solicitud.getMontoHaber() + " con " + archivo.getTotalHaberes());
                    })
                    .anyMatch(archivo -> {
                        // Eliminar ceros a la izquierda del RUT Beneficiario antes de comparar
                        String rutArchivo = archivo.getRutBenef().replaceFirst("^0+(?!$)", "");
                        return solicitud.getRutBeneficiario() != null &&
                            solicitud.getRutBeneficiario().toString().equals(rutArchivo) &&
                            solicitud.getMontoHaber() != null &&
                            solicitud.getMontoHaber().compareTo(new BigDecimal(archivo.getTotalHaberes())) == 0;
                    });

                // Crear resolución
                ResolucionDTO resolucion = new ResolucionDTO();
                resolucion.setIIdSolicitud(solicitud.getIdSolicitud());
                resolucion.setIAutor(solicitud.getIdUsuario());
                resolucion.setVcDescripcion(solicitudEncontrada ? "Solicitud aprobada para pago." : "Solicitud rechazada para pago.");
                resolucion.setIMotivoRechazo(null);
                resolucion.setIIdEstado(solicitudEncontrada ? 3 : 5);

                System.out.println(solicitudEncontrada
                    ? "Solicitud encontrada en emisiones, generando resolución con estado 3."
                    : "Solicitud NO encontrada en emisiones, generando resolución con estado 5.");

                // Llamar a la API externa para insertar la resolución
                String url = baseUrl + "/insertarResolucion";

                // Enviar el JSON usando postForObject y recibir la respuesta en ResponseDTO
                ResponseDTO response = restTemplate.postForObject(url, resolucion, ResponseDTO.class);

                // Validar respuesta de la API
                if (response != null && response.getCodigoRetorno() == 0) {
                    System.out.println("Resolución insertada correctamente en la API.");
                } else {
                    System.out.println("Error al insertar resolución en la API: " + (response != null ? response.getGlosaRetorno() : "Respuesta nula"));
                }

            } catch (Exception e) {
                System.out.println("Error al procesar la solicitud con Rut Beneficiario: " + solicitud.getRutBeneficiario() + " - " + e.getMessage());
                return false;
            }
        }

        return emisionDAO.validarSolicitudesEmitidas(emision);
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
}
