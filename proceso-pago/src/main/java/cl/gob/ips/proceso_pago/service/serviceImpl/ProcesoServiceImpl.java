package cl.gob.ips.proceso_pago.service.serviceImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import cl.gob.ips.proceso_pago.dao.ProcesoDAO;
import cl.gob.ips.proceso_pago.dto.ProcesoDTO;
import cl.gob.ips.proceso_pago.dto.ResponseDTO;
import cl.gob.ips.proceso_pago.dto.RetencionJudicialDTO;
import cl.gob.ips.proceso_pago.dto.DatosProcesoPorTipoDTO;
import cl.gob.ips.proceso_pago.dto.DetalleCausanteDTO;
import cl.gob.ips.proceso_pago.service.ProcesoService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProcesoServiceImpl implements ProcesoService {
	private final RestTemplate restTemplate;
	
	@Autowired
    private ProcesoDAO procesoDAO;

    @Value("${app.base.url}")
    private String baseUrl; 

    @Value("${app.base.urlRetencion}")
    private String baseUrlRetencion; 

    @Override
    public int crearProceso(ProcesoDTO insertarProcesoDTO) {
    	int idProceso = procesoDAO.insertarProceso(insertarProcesoDTO);
         
        if(idProceso>0) {
        	List<DetalleCausanteDTO> listaDetalleCausante = procesoDAO.obtenerDetallesPorProcesoPago(idProceso);
         	
            for(DetalleCausanteDTO detalle: listaDetalleCausante) {
            	if(detalle.getTipoSolicitante()!=2) {
	            	RetencionJudicialDTO retencion = obtenerRetencionJudicial(detalle.getRutCausante(),detalle.getRutBeneficiario(), detalle.getPeriodo());
	            	if(retencion!=null) {
		            	detalle.setIdRetencion(retencion.getIdRetencion());
		            	detalle.setRutReteniente(retencion.getRutRetenedor());
		            	detalle.setDvReteniente(retencion.getDvRetenedor());
		            	detalle.setNombresReteniente(retencion.getNombreRetenedor());
		            	detalle.setApellidoMaternoReteniente(retencion.getApellidoMaternoRetenedor());
		            	detalle.setApellidoPaternoReteniente(retencion.getApellidoPaternoRetenedor());
		            	detalle.setIdFormaPagoRetenedor(retencion.getIdFormaPago());
		            	detalle.setIdBancoRetenedor(retencion.getCodBanco());
		            	detalle.setIdTipoCuentaRetenedor(retencion.getCodTipoCuenta());
		            	detalle.setNumeroCuentaRetenedor(retencion.getNumeroCuenta());
		            	detalle.setRutBeneficiarioPago(retencion.getRutRetenedor());
		            	detalle.setDvBeneficiarioPago(retencion.getDvRetenedor());
		            	
		            	RetencionJudicialDTO ultimaRetencion = obtenerRetencionJudicialUltima(detalle.getRutCausante(),detalle.getRutBeneficiario(), detalle.getPeriodo());
		            	if(ultimaRetencion!=null) {
			            	detalle.setIdFormaPagoFinal(ultimaRetencion.getIdFormaPago());
			            	detalle.setIdBancoFinal(ultimaRetencion.getCodBanco());
			            	detalle.setIdTipoCuentaFinal(ultimaRetencion.getCodTipoCuenta());
			            	detalle.setNumeroCuentaFinal(ultimaRetencion.getNumeroCuenta());
		            	}
		            	// Llamar a la API para actualizar detalle causante
	                    String urlActualizar = baseUrl + "/actualizarDetalleCausante";
	                    restTemplate.postForObject(urlActualizar, detalle, Boolean.class);
		           	}
            	}
            }
            return idProceso;
        }
        return idProceso;
    }
    
    public RetencionJudicialDTO obtenerRetencionJudicial(int rutCausante, int rutBeneficiario, int periodo) {
        String url = String.format(
            baseUrlRetencion + "/obtener-detalle-causante/%s/%s/%s",
            rutCausante, rutBeneficiario, periodo
        );

        System.out.println("Esta es la URL: " + url);

        RestTemplate restTemplate = new RestTemplate();
        ResponseDTO response = null;

        try {
            response = restTemplate.getForObject(url, ResponseDTO.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                System.err.println("La API retornó 404: No se encontraron retenciones para el causante en este periodo " + periodo + ".");
                return null;
            } else {
                System.err.println("Error al llamar a la API: " + e.getMessage());
                throw e;
            }
        }

        if (response != null && response.getResultado() != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.convertValue(response.getResultado(), RetencionJudicialDTO.class);
            } catch (IllegalArgumentException e) {
                System.err.println("Error al convertir resultado a RetencionJudicialDTO: " + e.getMessage());
            }
        }

        return null;
    }


    public RetencionJudicialDTO obtenerRetencionJudicialUltima(int rutCausante, int rutBeneficiario, int periodo) {
        String url = String.format(
            baseUrlRetencion + "/obtener-detalle-causante-retenedor/%s/%s",
            rutCausante, rutBeneficiario
        );

        System.out.println("Esta es la URL de la segunda API: " + url);

        RestTemplate restTemplate = new RestTemplate();
        ResponseDTO response = null;

        try {
            response = restTemplate.getForObject(url, ResponseDTO.class);
        } catch (HttpClientErrorException e) {
            /*if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                System.err.println("Segunda API retornó 404: No se encontraron datos adicionales para el causante en este periodo " + periodo + ".");
                return null;
            } else {
                System.err.println("Error al llamar a la segunda API: " + e.getMessage());
                return null;
            }*/
        	return null;
        }

        if (response != null && response.getResultado() != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.convertValue(response.getResultado(), RetencionJudicialDTO.class);
            } catch (IllegalArgumentException e) {
                System.err.println("Error al convertir resultado a RetencionJudicialDTO: " + e.getMessage());
            }
        }

        return null;
    }

    @Override
    public List<ProcesoDTO> consultarProceso(Long idProceso) {
        return procesoDAO.consultarProceso(idProceso);
    }

    @Override
    public List<ProcesoDTO> filtrarProcesos(String texto) {
        return procesoDAO.filtrarProcesos(texto);
    }

    @Override
    public List<DatosProcesoPorTipoDTO> obtenerDatosProcesoPorTipo(int idProceso){
        return procesoDAO.obtenerDatosProcesoPorTipo(idProceso);
    }

    @Override
    public boolean actualizarEstadoProceso(int idProceso, int idEstadoProceso){
        return procesoDAO.actualizarEstadoProceso(idProceso, idEstadoProceso);
    }

    //@Scheduled(cron = "0 0 0 * * ?")
    /*@Scheduled(cron = "0 49 14 * * ?")
    public List<ProcesoDTO> iniciarProceso() {
        List<ProcesoDTO> listaProcesos = new ArrayList<>();
        System.out.println("Inicia Daemon");
        try {
            listaProcesos = procesoDAO.consultarProceso(null);
            //System.out.println("Se encontró " + solicitudesPago.size() + " solicitudes con " + diasAntiguedad + " días de antigüedad");

            for (ProcesoDTO proceso : listaProcesos) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                
                try{
                // Formateamos ambas fechas a solo año-mes-día
                System.out.println("Proceso "+proceso.getIdProceso()+" fecha "+proceso.getFechaEjecucion());
                String fechaFormateada = sdf.format(proceso.getFechaEjecucion());
                String fechaActualFormateada = sdf.format(new Date());
                
                // Comparamos si la fecha es menor o igual a la fecha actual
                if(fechaFormateada.compareTo(fechaActualFormateada) <= 0){
                    if(proceso.getIdEstado()==3){
                        //Se cumplió la fecha, se debe ejecutar el proceso
                        System.out.println("A ejecutar el proceso "+proceso.getIdProceso()+", tiene fecha "+proceso.getFechaEjecucion());
                    }
                }
            }catch(Exception e){
                System.err.println("Error con fechas");    
            }
                
            }

        } catch (Exception e) {
            // Captura cualquier otra excepción que ocurra en el proceso general
            System.err.println("Error al obtener procesos");
        }
        return listaProcesos;
    }*/
    
}