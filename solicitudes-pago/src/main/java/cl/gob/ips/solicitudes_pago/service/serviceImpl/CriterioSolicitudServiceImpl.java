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
    public boolean validarCriteriosResolucion(Integer idSolicitud, boolean esArchivo, boolean esBotonValidar){
        listaCriterios.clear();
        listaCriteriosCausante.clear();
        //List<SolicitudDTO> listaSolicitud = solicitudPagoDAO.consultarSolicitudPago(idSolicitud);
        //SolicitudDTO solicitud = listaSolicitud.get(0);
        List<CausanteSolicitudDTO> listaCausantes = solicitudPagoDAO.obtenerCausantesPorSolicitud(idSolicitud);
        boolean solicitudAprobada = true;
        
        // 1) Validación de Rol Único Tributario Beneficiario
        agregarCriterioResolucion(idSolicitud,1,true,null,null,null);
        
        // 2) Validación de Comuna de Dirección de Empleador
        agregarCriterioResolucion(idSolicitud, 2, true, null,null,null);
        
        if(!esBotonValidar) {
	        for(CriterioSolicitudDTO criterio: listaCriterios){
	            criterioSolicitudDAO.insertarCriterioSolicitud(criterio);
	        }
        }
        System.out.println("Finalizó validación de Criterios de Resolución por Solicitud");

        for(CausanteSolicitudDTO causante: listaCausantes){
        	listaCriteriosCausante.clear();
        // 1) Validación de Rol Único Tributario Causante
            agregarCriterioCausante(causante.getIdCausanteSolicitud(),1,true,null,null,null);
           
            // 3) Validación de Período de Compensación
                agregarCriterioCausante(causante.getIdCausanteSolicitud(), 3, true, null,null,null);
              
            // 4) Validación de Duplicidad de Solicitud
                agregarCriterioCausante(causante.getIdCausanteSolicitud(), 4, true, null,null,null);
            
            // 5) Validación de Fechas Válidas
                agregarCriterioCausante(causante.getIdCausanteSolicitud(), 5, true, null,null,null); 
                
             // 7) Verificación de Relación Laboral Vigente
                //if(!esArchivo) {
	                if (verificarRelacionLaboralVigente(causante.getIdCausanteSolicitud())) {
	                    agregarCriterioCausante(causante.getIdCausanteSolicitud(), 7, true, null,null,null);
	                } else {
	                    solicitudAprobada = false;
	                    agregarCriterioCausante(causante.getIdCausanteSolicitud(), 7, false, null,null,null);
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
			        for(CriterioSolicitudCausanteDTO criterio: listaCriteriosCausante){
			            criterioSolicitudDAO.insertarCriterioCausante(criterio);
			        }
                }
        System.out.println("Finalizó validación de Criterios de Resolución por Causante");
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
    public boolean verificarRelacionLaboralVigente(int idCausanteSolicitud) {
    	
    	List<DetalleCausanteDTO> detalle = causanteService.obtenerDetalleCausantePorId(idCausanteSolicitud);
    	
    	int diasPago = 0;
    	int contadorAprobados = 0;
    	
    	boolean cumpleRelacionLaboral = true;
    	
    	if(detalle.size()==0) {
    		cumpleRelacionLaboral = false;
    	} 
    	
    	for(DetalleCausanteDTO detalleCausante: detalle) {
    		if(detalleCausante.getEstado()==1) {
    			contadorAprobados++;
    			int diasCotizaciones = causanteService.obtenerDiasCotizacion(detalleCausante.getRutBeneficiario(), detalleCausante.getRutEmpleador(), String.valueOf(detalleCausante.getPeriodo())); //Llamar a API que trae los dias trabajados
	 
    			Map<String, String> fechas = UtilServiceImpl.obtenerFechasDesdePeriodo(String.valueOf(detalleCausante.getPeriodo()));
    			System.out.println("Inicio: " + fechas.get("inicio")); // 2014-01-01
    			System.out.println("Fin: " + fechas.get("fin"));       // 2014-01-31
    			
	    		int diasLicenciasFiniquitos = licenciaFiniquitoService.obtenerDiasLicenciaFiniquito(detalleCausante.getRutBeneficiario(), fechas.get("inicio"), fechas.get("fin"));	
	    		    	
	    		int diasReconocimiento = detalleCausante.getDiasReconocimiento();
	    		
	    		if((diasCotizaciones+diasLicenciasFiniquitos)==0) {
	    			cumpleRelacionLaboral = false;
	    		}
	    		
	    		if(diasReconocimiento>(diasCotizaciones+diasLicenciasFiniquitos)) {
	    			diasPago = (diasCotizaciones+diasLicenciasFiniquitos);
	    		}
	    		else {
	    			diasPago = diasReconocimiento;
	    		}
	    		
	    		if(diasPago>25) {
	    			detalleCausante.setTotalPago(detalleCausante.getMontoMovimiento());
	    			detalleCausante.setDiasPago(diasPago);
	    		}
	    		else {
	    			BigDecimal dias = new BigDecimal(diasPago);
	    			BigDecimal divisor = new BigDecimal(30);
	    			// (monto / 30) * diasPago
	    			BigDecimal totalPago = detalleCausante.getMontoMovimiento()
	    			    .divide(divisor, 2, RoundingMode.HALF_UP)  // División con escala y redondeo
	    			    .multiply(dias);                           // Multiplicación
	
	    			detalleCausante.setTotalPago(totalPago);
	    			detalleCausante.setDiasPago(diasPago);
	    		}
	    		causanteService.actualizarDetalleCausante(detalleCausante);
    		}
    	}//Fin for
    	     	
    	if(contadorAprobados==0) {
    		cumpleRelacionLaboral = false;
    	}
        return cumpleRelacionLaboral;
    }
    
    public boolean verificarVigenciaCausante() {
        return false;
    }
    
    public boolean verificarPagoCuentaCorriente() {
        return false;
    }
    
    

    public boolean validarRut(String rut) {
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