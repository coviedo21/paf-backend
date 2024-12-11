package cl.gob.ips.pagos_asignacion_familiar.service.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import cl.gob.ips.pagos_asignacion_familiar.dao.CriterioSolicitudDAO;
import cl.gob.ips.pagos_asignacion_familiar.dao.SolicitudPagoDAO;
import cl.gob.ips.pagos_asignacion_familiar.dto.CriterioSolicitudDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.ResponseDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.RetencionJudicialDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.SolicitudDTO;
import cl.gob.ips.pagos_asignacion_familiar.service.CriterioSolicitudService;
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

    @Override
    public List<CriterioSolicitudDTO> consultarCriteriosSolicitud(Integer idSolicitud) {
        return criterioSolicitudDAO.consultarCriteriosSolicitud(idSolicitud);
    }

    @Override
    public boolean validarCriteriosResolucion(Integer idSolicitud){
        List<SolicitudDTO> listaSolicitud = solicitudPagoDAO.consultarSolicitudPago(idSolicitud);
        SolicitudDTO solicitud = listaSolicitud.get(0);
        
        // 1) Validación de Rol Único Tributario
        if(validarRolUnicoTributario(solicitud.getRutBeneficiario().toString()+solicitud.getDvBeneficiario())){
            agregarCriterioResolucion(idSolicitud,1,true,null);
        }
        else{
            agregarCriterioResolucion(idSolicitud, 1, false, null);
        }

        // 2) Validación de Comuna de Dirección de Empleador
        if (validarComunaDireccionEmpleador(solicitud.getIdComuna())) {
            agregarCriterioResolucion(idSolicitud, 2, true, null);
        } else {
            agregarCriterioResolucion(idSolicitud, 2, false, null);
        }

        // 3) Validación de Período de Compensación
        if (validarPeriodoCompensacion()) {
            agregarCriterioResolucion(idSolicitud, 3, true, null);
        } else {
            agregarCriterioResolucion(idSolicitud, 3, false, null);
        }

        // 4) Validación de Duplicidad de Solicitud
        if (validarDuplicidadSolicitud()) {
            agregarCriterioResolucion(idSolicitud, 4, true, null);
        } else {
            agregarCriterioResolucion(idSolicitud, 4, false, null);
        }

        // 5) Validación de Fechas Válidas
        if (validarFechasValidas()) {
            agregarCriterioResolucion(idSolicitud, 5, true, null);
        } else {
            agregarCriterioResolucion(idSolicitud, 5, false, null);
        }

        // 6) Relación Laboral Vigente Durante el Período de Compensación
        if (relacionLaboralVigenteDurantePeriodoCompensacion()) {
            agregarCriterioResolucion(idSolicitud, 6, true, null);
        } else {
            agregarCriterioResolucion(idSolicitud, 6, false, null);
        }

        // 7) Verificación de Relación Laboral Vigente
        if (verificarRelacionLaboralVigente()) {
            agregarCriterioResolucion(idSolicitud, 7, true, null);
        } else {
            agregarCriterioResolucion(idSolicitud, 7, false, null);
        }

        // 8) Verificación de Vigencia del Causante
        if (verificarVigenciaCausante()) {
            agregarCriterioResolucion(idSolicitud, 8, true, null);
        } else {
            agregarCriterioResolucion(idSolicitud, 8, false, null);
        }

        // 9) Verificación de Pago en Cuenta Corriente
        if (verificarPagoCuentaCorriente()) {
            agregarCriterioResolucion(idSolicitud, 9, true, null);
        } else {
            agregarCriterioResolucion(idSolicitud, 9, false, null);
        }

        // 10) Verificación de Retenciones Judiciales
        if (verificarRetencionesJudiciales()) {
            agregarCriterioResolucion(idSolicitud, 10, true, null);
        } else {
            agregarCriterioResolucion(idSolicitud, 10, false, null);
        }

        for(CriterioSolicitudDTO criterio: listaCriterios){
            criterioSolicitudDAO.insertarCriterioSolicitud(criterio);
        }
        System.out.println("Finalizó validación de Criterios de Resolución");
        return true;
    }

    public void agregarCriterioResolucion(Integer idSolicitud, Integer idCriterio, boolean cumple, String archivo){
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
        return true;
    }
    
    public boolean validarDuplicidadSolicitud() {
        return true;
    }
    
    public boolean validarFechasValidas() {
        return false;
    }
    
    public boolean relacionLaboralVigenteDurantePeriodoCompensacion() {
        return true;
    }
    
    public boolean verificarRelacionLaboralVigente() {
        return true;
    }
    
    public boolean verificarVigenciaCausante() {
        return true;
    }
    
    public boolean verificarPagoCuentaCorriente() {
        return true;
    }
    
    public boolean verificarRetencionesJudiciales() {
        /*RetencionJudicialDTO retencionJudicialDTO = new RetencionJudicialDTO();
        String url = String.format("https://retencionjudicialback-dev.azurewebsites.net/retencion-judicial-filtro-ms-v1/filtro/filtrar-retenciones-judiciales");
        ResponseDTO response = restTemplate.postForObject(url, retencionJudicialDTO, ResponseDTO.class);
        if(response.getResultado()!=null){
            return true;
        }*/
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
    
}