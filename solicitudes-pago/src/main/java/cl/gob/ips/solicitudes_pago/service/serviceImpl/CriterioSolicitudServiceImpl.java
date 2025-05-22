package cl.gob.ips.solicitudes_pago.service.serviceImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import cl.gob.ips.solicitudes_pago.dao.CriterioSolicitudDAO;
import cl.gob.ips.solicitudes_pago.dao.SolicitudPagoDAO;
import cl.gob.ips.solicitudes_pago.dto.CausanteSolicitudDTO;
import cl.gob.ips.solicitudes_pago.dto.CriterioSolicitudCausanteDTO;
import cl.gob.ips.solicitudes_pago.dto.CriterioSolicitudDTO;
import cl.gob.ips.solicitudes_pago.dto.DetalleCausanteDTO;
import cl.gob.ips.solicitudes_pago.dto.ResponseDTO;
import cl.gob.ips.solicitudes_pago.dto.RetencionJudicialDTO;
import cl.gob.ips.solicitudes_pago.dto.SolicitudDTO;
import cl.gob.ips.solicitudes_pago.service.CausanteService;
import cl.gob.ips.solicitudes_pago.service.CriterioSolicitudService;
import cl.gob.ips.solicitudes_pago.service.FileService;
import cl.gob.ips.solicitudes_pago.service.LicenciaFiniquitoService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CriterioSolicitudServiceImpl implements CriterioSolicitudService {

	private static final Logger logger = LoggerFactory.getLogger(CriterioSolicitudServiceImpl.class);
	
    private final RestTemplate restTemplate;

    @Autowired
    private CriterioSolicitudDAO criterioSolicitudDAO;

    @Autowired
    private SolicitudPagoDAO solicitudPagoDAO;
    
    @Autowired
    private CausanteService causanteService;
    
    @Autowired
    private LicenciaFiniquitoService licenciaFiniquitoService;
    

    List<CriterioSolicitudDTO> listaCriterios = new ArrayList<CriterioSolicitudDTO>();
    List<CriterioSolicitudCausanteDTO> listaCriteriosCausante = new ArrayList<CriterioSolicitudCausanteDTO>();

    @Override
    public List<CriterioSolicitudDTO> consultarCriteriosSolicitud(Integer idSolicitud) {
        return criterioSolicitudDAO.consultarCriteriosSolicitud(idSolicitud);
    }

    @Override
    public boolean validarCriteriosResolucion(Integer idSolicitud, boolean esArchivo, boolean esBotonValidar, String esPortuario){
    	long tiempoInicio = System.currentTimeMillis();
    	listaCriterios.clear();
        listaCriteriosCausante.clear();
        //List<SolicitudDTO> listaSolicitud = solicitudPagoDAO.consultarSolicitudPago(idSolicitud);
        //SolicitudDTO solicitud = listaSolicitud.get(0);
        List<CausanteSolicitudDTO> listaCausantes = solicitudPagoDAO.obtenerCausantesPorSolicitud(idSolicitud);
        boolean solicitudAprobada = true;
        
        if(!esBotonValidar) {
        // 1) Validación de Rol Único Tributario Beneficiario
        agregarCriterioResolucion(idSolicitud,1,true,null,null,null);
        
        // 2) Validación de Comuna de Dirección de Empleador
        agregarCriterioResolucion(idSolicitud, 2, true, null,null,null);
        }
        
        if(!esBotonValidar) {
	        for(CriterioSolicitudDTO criterio: listaCriterios){
	            criterioSolicitudDAO.insertarCriterioSolicitud(criterio);
	        }
        }
        System.out.println("Finalizó validación de Criterios de Resolución por Solicitud");

        for(CausanteSolicitudDTO causante: listaCausantes){
        	listaCriteriosCausante.clear();
        	if(!esBotonValidar) {
        // 1) Validación de Rol Único Tributario Causante
            agregarCriterioCausante(causante.getIdCausanteSolicitud(),1,true,null,null,null);
           
            // 3) Validación de Período de Compensación
                agregarCriterioCausante(causante.getIdCausanteSolicitud(), 3, true, null,null,null);
              
            // 4) Validación de Duplicidad de Solicitud
                agregarCriterioCausante(causante.getIdCausanteSolicitud(), 4, true, null,null,null);
            
            // 5) Validación de Fechas Válidas
                agregarCriterioCausante(causante.getIdCausanteSolicitud(), 5, true, null,null,null); 
        	}
        	
             // 7) Verificación de Relación Laboral Vigente
                //if(!esArchivo) {
	                if (verificarRelacionLaboralVigente(causante.getIdCausanteSolicitud(),esPortuario)) {
	                	if(!esBotonValidar) {
	                		agregarCriterioCausante(causante.getIdCausanteSolicitud(), 7, true, null,null,null);
	                	}
	                    if(esBotonValidar) {
	                    	List<CriterioSolicitudCausanteDTO> criterios = consultarCriteriosCausante(causante.getIdCausanteSolicitud());
	                    	for(CriterioSolicitudCausanteDTO criterio: criterios) {
	                    		if(criterio.getIdCriterio()==7) {
	                    			criterio.setCumple("S");
	                    			actualizarCriterioCausante(criterio);
	                    		}
	                    	}
	                    	
	                    }
	                    
	                } else {
	                    if(esArchivo) {
	                    	solicitudAprobada = true;
	                    	agregarCriterioCausante(causante.getIdCausanteSolicitud(), 7, true, null,null,null);
	                    }
	                    else {
	                    	solicitudAprobada = false;
	                    	agregarCriterioCausante(causante.getIdCausanteSolicitud(), 7, false, null,null,null);
	                    }
	                }
                /*}
                else {
                	agregarCriterioCausante(causante.getIdCausanteSolicitud(), 7, true, null,null,null);
                }*/
 
                // 8) Verificación de Vigencia del Causante
                agregarCriterioCausante(causante.getIdCausanteSolicitud(), 8, true, null,null,null);
            
            // 10) Verificación de Retenciones Judiciales
                agregarCriterioCausante(causante.getIdCausanteSolicitud(), 10, true, null,null,null);
            
                if(!esArchivo) {
                
                }
                if(!esBotonValidar) { 
                	long tiempoInicioInsertarCriterio = System.currentTimeMillis();
                    
                	int i = 1;
                	criterioSolicitudDAO.insertarCriteriosCausanteMasivo(listaCriteriosCausante);
                	/*for (CriterioSolicitudCausanteDTO criterio : listaCriteriosCausante) {
                	    long inicio = System.currentTimeMillis();

                	    criterioSolicitudDAO.insertarCriterioCausante(criterio);

                	    long fin = System.currentTimeMillis();
                	    System.out.println("Insert #" + i + " tomó " + (fin - inicio) + " ms");
                	    i++;
                	}*/
			        long tiempoFinInsertarCriterio = System.currentTimeMillis();
			        logger.error("Tiempo Fin de Insertar Criterios: "+(tiempoInicioInsertarCriterio-tiempoFinInsertarCriterio));
                }
        System.out.println("Finalizó validación de Criterios de Resolución por Causante");
        long tiempoFin = System.currentTimeMillis();
        logger.error("Tiempo Fin de los Criterios: "+(tiempoInicio-tiempoFin));
        }
        return solicitudAprobada;
    }

    public void agregarCriterioResolucion(Integer idSolicitud, Integer idCriterio, boolean cumple, String archivo, Date fechaDesde, Date fechaHasta){
        CriterioSolicitudDTO criterio = new CriterioSolicitudDTO();
        criterio.setIdSolicitud(idSolicitud);
        criterio.setIdCriterio(idCriterio);
        if(cumple){
            criterio.setCumple("S");
        }
        else{
            criterio.setCumple("N");
        }
        criterio.setArchivo(archivo);
        listaCriterios.add(criterio);
    }

    public void agregarCriterioCausante(Integer idCausanteSolicitud, Integer idCriterio, boolean cumple, String archivo, Date fechaDesde,Date fechaHasta){
        CriterioSolicitudCausanteDTO criterio = new CriterioSolicitudCausanteDTO();
        criterio.setIdCausanteSolicitud(idCausanteSolicitud);
        criterio.setIdCriterio(idCriterio);
        if(cumple){
            criterio.setCumple("S");
        }
        else{
            criterio.setCumple("N");
        }
        criterio.setArchivo(archivo);
        listaCriteriosCausante.add(criterio);
    }

    @Override
    public boolean validarRolUnicoTributario(String rut){
        
        return validarRut(rut);
    }

    public boolean validarComunaDireccionEmpleador(int idComuna) {
        if(idComuna>0){
            return true;
        }
        else{
            return false;
        }
    }
    
    public boolean validarPeriodoCompensacion() {
        return false;
    }
    
    public boolean validarDuplicidadSolicitud() {
        return false;
    }
    
    public boolean validarFechasValidas() {
        return false;
    }
    
    public boolean relacionLaboralVigenteDurantePeriodoCompensacion() {
        return false;
    }
    
    @Override
    public boolean verificarRelacionLaboralVigente(int idCausanteSolicitud, String esPortuario) {
    	
    	List<DetalleCausanteDTO> detalle = causanteService.obtenerDetalleCausantePorId(idCausanteSolicitud);
    	
    	int diasPago = 0;
    	int contadorAprobados = 0;
    	int tramo = 0;
    	int valorDiario = 0;
    	int diasPorPagar = 0;
    	int diasTrabajados = 0;
    	
    	boolean cumpleRelacionLaboral = true;
    	
    	if(detalle.size()==0) {
    		cumpleRelacionLaboral = false;
    	} 
    	
    	for(DetalleCausanteDTO detalleCausante: detalle) {
    		if(detalleCausante.getEstado()==1 || detalleCausante.getEstado()==6) {
    			tramo = detalleCausante.getValorTramo30();
    			valorDiario = (int) Math.round((double) tramo / 30);
    			diasPorPagar = detalleCausante.getDiasReconocimiento() - detalleCausante.getDiasTrabajados();
    			diasTrabajados = detalleCausante.getDiasTrabajados();
    			
    			if(diasTrabajados == 0) {
    				cumpleRelacionLaboral = false;
    			}
    			contadorAprobados++;
    			long tiempoInicioObtenerDiasTrabajados = System.currentTimeMillis();
    	   
    			//int diasCotizaciones = causanteService.obtenerDiasCotizacion(detalleCausante.getRutBeneficiario(), detalleCausante.getRutEmpleador(), String.valueOf(detalleCausante.getPeriodo())); //Llamar a API que trae los dias trabajados
    			long tiempoFinObtenerDiasTrabajados = System.currentTimeMillis();
    	        logger.error("Obtener dias trabajados demoró: "+(tiempoInicioObtenerDiasTrabajados-tiempoFinObtenerDiasTrabajados));
    			Map<String, String> fechas = UtilServiceImpl.obtenerFechasDesdePeriodo(String.valueOf(detalleCausante.getPeriodo()));
    			System.out.println("Inicio: " + fechas.get("inicio")); // 2014-01-01
    			System.out.println("Fin: " + fechas.get("fin"));       // 2014-01-31
    			long tiempoInicioObtenerLicencias = System.currentTimeMillis();
    	        
	    		int diasLicencias = licenciaFiniquitoService.obtenerDiasLicenciaFiniquito(detalleCausante.getRutBeneficiario(), fechas.get("inicio"), fechas.get("fin"));	
	    		if(diasLicencias>0) {
	    			diasPago = diasLicencias;
	    		}
	    		
	    		long tiempoFinObtenerLicencias = System.currentTimeMillis();
	            logger.error("Obtener licencias demoró: "+(tiempoInicioObtenerLicencias-tiempoFinObtenerLicencias));    	
	    		
	            long tiempoInicioCalculo = System.currentTimeMillis();
	    		int diasReconocimiento = detalleCausante.getDiasReconocimiento();
	    		
	    		
	    		/*if((diasCotizaciones+diasLicenciasFiniquitos)==0) {
	    			cumpleRelacionLaboral = false;
	    		}*/
	    		
	    		if(esPortuario.equalsIgnoreCase("S")) {
	    			if(diasTrabajados>0) {
	    				detalleCausante.setTotalPago(detalleCausante.getMontoMovimiento());
	    			}
	    			else {
	    				detalleCausante.setTotalPago(BigDecimal.ZERO);
	    			}
	    		}	
	    		else {
	    			if(diasReconocimiento == diasPorPagar) {
	    				detalleCausante.setTotalPago(new BigDecimal(valorDiario*diasPorPagar));
	    			}
	    			else {
	    				if(diasLicencias>diasPorPagar) {
	    					detalleCausante.setTotalPago(new BigDecimal(valorDiario*diasPorPagar));
	    				}
	    				else{
	    					detalleCausante.setTotalPago(new BigDecimal(valorDiario*diasLicencias));
	    				}
	    			}	
	    		}	
	    			
	    			
	    		if(diasTrabajados==0) {
    				detalleCausante.setEstado(6);
    			}
	    		else {
	    			if(detalleCausante.getEstado()==6) {
	    				detalleCausante.setEstado(1);
	    			}
	    		}
	    		
	    		/*if(diasPago>=25) { //dias por pagar
	    			detalleCausante.setTotalPago(detalleCausante.getMontoMovimiento());
	    			detalleCausante.setDiasPago(diasPago);
	    		}
	    		else {
	    			BigDecimal dias = new BigDecimal(diasPago);
	    			BigDecimal divisor = new BigDecimal(diasReconocimiento);
	    			// (monto / 30) * diasPago
	    			BigDecimal totalPago = detalleCausante.getMontoMovimiento()
	    			    .divide(divisor, 2, RoundingMode.HALF_UP)  // División con escala y redondeo
	    			    .multiply(dias);                           // Multiplicación
	
	    			detalleCausante.setTotalPago(totalPago);
	    			detalleCausante.setDiasPago(diasPago);
	    			
	    		}*/
	    		long tiempoFinCalculo = System.currentTimeMillis();
	            logger.error("Obtener calculo demoró: "+(tiempoInicioCalculo-tiempoFinCalculo));
	    		
	            long tiempoInicioActualizarDetalleCausante = System.currentTimeMillis();
	            causanteService.actualizarDetalleCausante(detalleCausante);
	    		long tiempoFinActualizarDetalleCausante = System.currentTimeMillis();
	            logger.error("Actualizar detalle causante demoró: "+(tiempoInicioActualizarDetalleCausante-tiempoFinActualizarDetalleCausante));
    		}
    	}//Fin for
    	     	
    	if(contadorAprobados==0) {
    		cumpleRelacionLaboral = false;
    	}
    	System.out.println("Se terminó de validar si hay relacion laboral");
        return cumpleRelacionLaboral;
    }
    
    public boolean verificarVigenciaCausante() {
        return false;
    }
    
    public boolean verificarPagoCuentaCorriente() {
        return false;
    }
    
    

    public boolean validarRut(String rut) {
    	long tiempoInicioValidarRut = System.currentTimeMillis();
        
        // Limpiar el RUT, eliminando puntos, guiones y espacios
        rut = rut.replace(".", "").replace("-", "").trim();

        // Validar longitud mínima
        if (rut.length() < 2) {
            return false;
        }

        // Obtener el dígito verificador
        char dv = rut.charAt(rut.length() - 1);

        // Obtener el número del RUT
        String rutNumber = rut.substring(0, rut.length() - 1);

        // Validar que el RUT contenga solo números
        if (!rutNumber.matches("\\d+")) {
            return false;
        }

        // Convertir el número del RUT a entero
        int rutInt = Integer.parseInt(rutNumber);

        // Calcular el dígito verificador esperado
        char dvEsperado = calcularDigitoVerificador(rutInt);

        // Comparar el dígito verificador esperado con el proporcionado
        long tiempoFinValidarRut = System.currentTimeMillis();
        logger.error("validar rut demoró: "+(tiempoInicioValidarRut-tiempoFinValidarRut));
        return dv == dvEsperado;
    }

    private char calcularDigitoVerificador(int rut) {
        int m = 0, s = 1;
        while (rut != 0) {
            s = (s + rut % 10 * (9 - m++ % 6)) % 11;
            rut /= 10;
        }
        return (char) (s != 0 ? s + 47 : 75);
    }

    
    public boolean actualizarCriterioSolicitud(CriterioSolicitudDTO criterioSolicitudDTO){
        return criterioSolicitudDAO.actualizarCriterioSolicitud(criterioSolicitudDTO);
    }

    public boolean actualizarCriterioCausante(CriterioSolicitudCausanteDTO criterioCausante){
        return  criterioSolicitudDAO.actualizarCriterioCausante(criterioCausante);   
    }

    public List<CriterioSolicitudCausanteDTO> consultarCriteriosCausante(Integer idCausanteSolicitud){
        return criterioSolicitudDAO.consultarCriterioCausante(idCausanteSolicitud);
    }

    public CriterioSolicitudDTO obtenerCriteriosPorIdCriterio(Integer idCriterioSolicitud){
        return criterioSolicitudDAO.obtenerCriteriosPorIdCriterio(idCriterioSolicitud);
    }
 
    public CriterioSolicitudCausanteDTO obtenerCriterioCausantePorIdCriterio(Integer idCriterioCausante){
        return criterioSolicitudDAO.obtenerCriterioCausantePorIdCriterio(idCriterioCausante);
    }

    public boolean obtenerRelacionLaboral(Integer rutBeneficiario){
        return criterioSolicitudDAO.obtenerRelacionLaboral(rutBeneficiario);
    }
    
    public BigDecimal obtenerMontoDescuento(Integer rutBeneficiario){
        return criterioSolicitudDAO.obtenerMontoDescuento(rutBeneficiario);
    }
}