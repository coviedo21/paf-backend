package cl.gob.ips.solicitudes_pago.service.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import cl.gob.ips.solicitudes_pago.dao.CriterioSolicitudDAO;
import cl.gob.ips.solicitudes_pago.dao.SolicitudPagoDAO;
import cl.gob.ips.solicitudes_pago.dto.CausanteSolicitudDTO;
import cl.gob.ips.solicitudes_pago.dto.CriterioSolicitudCausanteDTO;
import cl.gob.ips.solicitudes_pago.dto.CriterioSolicitudDTO;
import cl.gob.ips.solicitudes_pago.dto.ResponseDTO;
import cl.gob.ips.solicitudes_pago.dto.RetencionJudicialDTO;
import cl.gob.ips.solicitudes_pago.dto.SolicitudDTO;
import cl.gob.ips.solicitudes_pago.service.CriterioSolicitudService;
import cl.gob.ips.solicitudes_pago.service.FileService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CriterioSolicitudServiceImpl implements CriterioSolicitudService {

    private final RestTemplate restTemplate;

    @Autowired
    private CriterioSolicitudDAO criterioSolicitudDAO;

    @Autowired
    private SolicitudPagoDAO solicitudPagoDAO;

    List<CriterioSolicitudDTO> listaCriterios = new ArrayList<CriterioSolicitudDTO>();
    List<CriterioSolicitudCausanteDTO> listaCriteriosCausante = new ArrayList<CriterioSolicitudCausanteDTO>();

    @Override
    public List<CriterioSolicitudDTO> consultarCriteriosSolicitud(Integer idSolicitud) {
        return criterioSolicitudDAO.consultarCriteriosSolicitud(idSolicitud);
    }

    @Override
    public boolean validarCriteriosResolucion(Integer idSolicitud){
        listaCriterios.clear();
        listaCriteriosCausante.clear();
        List<SolicitudDTO> listaSolicitud = solicitudPagoDAO.consultarSolicitudPago(idSolicitud);
        SolicitudDTO solicitud = listaSolicitud.get(0);
        List<CausanteSolicitudDTO> listaCausantes = solicitudPagoDAO.obtenerCausantesPorSolicitud(idSolicitud);
        boolean solicitudAprobada = true;
        // 1) Validación de Rol Único Tributario Beneficiario
        if(validarRolUnicoTributario(solicitud.getRutBeneficiario().toString()+solicitud.getDvBeneficiario())){
                agregarCriterioResolucion(idSolicitud,1,true,null,null,null);
        }
        else{
            solicitudAprobada = false;
            agregarCriterioResolucion(idSolicitud, 1, false, null,null,null);
        }

        // 2) Validación de Comuna de Dirección de Empleador
        if (validarComunaDireccionEmpleador(solicitud.getIdComuna())) {
            agregarCriterioResolucion(idSolicitud, 2, true, null,null,null);
        } else {
            solicitudAprobada = false;
            agregarCriterioResolucion(idSolicitud, 2, false, null,null,null);
        }

        // 6) Relación Laboral Vigente Durante el Período de Compensación
        if (relacionLaboralVigenteDurantePeriodoCompensacion()) {
            agregarCriterioResolucion(idSolicitud, 6, true, null,null,null);
        } else {
            solicitudAprobada = false;
            agregarCriterioResolucion(idSolicitud, 6, false, null,null,null);
        }

        // 7) Verificación de Relación Laboral Vigente
        if (verificarRelacionLaboralVigente()) {
            agregarCriterioResolucion(idSolicitud, 7, true, null,null,null);
        } else {
            solicitudAprobada = false;
            agregarCriterioResolucion(idSolicitud, 7, false, null,null,null);
        }

        // 9) Verificación de Pago en Cuenta Corriente
        if (verificarPagoCuentaCorriente()) {
            agregarCriterioResolucion(idSolicitud, 9, true, null,null,null);
        } else {
            solicitudAprobada = false;
            agregarCriterioResolucion(idSolicitud, 9, false, null,null,null);
        }

        for(CriterioSolicitudDTO criterio: listaCriterios){
            criterioSolicitudDAO.insertarCriterioSolicitud(criterio);
        }
        System.out.println("Finalizó validación de Criterios de Resolución por Solicitud");

        for(CausanteSolicitudDTO causante: listaCausantes){
        // 1) Validación de Rol Único Tributario Causante
            if(validarRolUnicoTributario(String.valueOf(causante.getRutCausante())+causante.getVcDvCausante())){
                agregarCriterioCausante(causante.getIIdCausanteSolicitud(),1,true,null,null,null);
            }
            else{
                solicitudAprobada = false;
                agregarCriterioCausante(causante.getIIdCausanteSolicitud(), 1, false, null,null,null);
            }

            // 3) Validación de Período de Compensación
            if (validarPeriodoCompensacion()) {
                agregarCriterioCausante(causante.getIIdCausanteSolicitud(), 3, true, null,null,null);
            } else {
                solicitudAprobada = false;
                agregarCriterioCausante(causante.getIIdCausanteSolicitud(), 3, false, null,null,null);
            }    
            
            // 4) Validación de Duplicidad de Solicitud
            if (validarDuplicidadSolicitud()) {
                agregarCriterioCausante(causante.getIIdCausanteSolicitud(), 4, true, null,null,null);
            } else {
                solicitudAprobada = false;
                agregarCriterioCausante(causante.getIIdCausanteSolicitud(), 4, false, null,null,null);
            }

            // 5) Validación de Fechas Válidas
            if (validarFechasValidas()) {
                agregarCriterioCausante(causante.getIIdCausanteSolicitud(), 5, true, null,null,null);
            } else {
                solicitudAprobada = false;
                agregarCriterioCausante(causante.getIIdCausanteSolicitud(), 5, false, null,null,null);
            }     

            // 8) Verificación de Vigencia del Causante
            if (verificarVigenciaCausante()) {
                agregarCriterioCausante(causante.getIIdCausanteSolicitud(), 8, true, null,null,null);
            } else {
                solicitudAprobada = false;
                agregarCriterioCausante(causante.getIIdCausanteSolicitud(), 8, false, null,null,null);
            }

            // 10) Verificación de Retenciones Judiciales
            if (verificarRetencionesJudiciales(String.valueOf(causante.getRutCausante()))) {
                agregarCriterioCausante(causante.getIIdCausanteSolicitud(), 10, true, null,null,null);
            } else {
                solicitudAprobada = false;
                agregarCriterioCausante(causante.getIIdCausanteSolicitud(), 10, false, null,null,null);
            }

            
        for(CriterioSolicitudCausanteDTO criterio: listaCriteriosCausante){
            criterioSolicitudDAO.insertarCriterioCausante(criterio);
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
    
    public boolean verificarRelacionLaboralVigente() {
        return false;
    }
    
    public boolean verificarVigenciaCausante() {
        return false;
    }
    
    public boolean verificarPagoCuentaCorriente() {
        return false;
    }
    
    public boolean verificarRetencionesJudiciales(String rut) {
        // Usamos un Map para solo enviar rutCausante
        Map<String, String> payload = new HashMap<>();
        payload.put("rutCausante", rut);
        String url = String.format("https://retencionjudicialback-dev.azurewebsites.net/retencion-judicial-filtro-ms-v1/filtro/filtrar-retenciones-judiciales");
        ResponseDTO response = restTemplate.postForObject(url, payload, ResponseDTO.class);
        
        // getResultado() devuelve una lista, hacemos el casting
        if (response.getResultado() instanceof List) {
            List<?> resultado = (List<?>) response.getResultado();
            // Verificamos si el tamaño de la lista es mayor a 0
            if (!resultado.isEmpty()) {
                return true;
            }
        }
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
    
}