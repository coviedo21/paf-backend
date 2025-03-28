package cl.gob.ips.solicitudes_pago.service;

import java.math.BigDecimal;
import java.util.List;

import cl.gob.ips.solicitudes_pago.dto.CausanteSolicitudDTO;
import cl.gob.ips.solicitudes_pago.dto.CriterioSolicitudCausanteDTO;
import cl.gob.ips.solicitudes_pago.dto.CriterioSolicitudDTO;

public interface CriterioSolicitudService {
    List<CriterioSolicitudDTO> consultarCriteriosSolicitud(Integer idSolicitud);
    boolean validarCriteriosResolucion(Integer idSolicitud, boolean esArchivo, boolean esBotonValidar, String esPortuario);
    boolean validarRolUnicoTributario(String rut);
    boolean actualizarCriterioSolicitud(CriterioSolicitudDTO criterioSolicitudDTO);
    boolean actualizarCriterioCausante(CriterioSolicitudCausanteDTO criterioCausante);
    List<CriterioSolicitudCausanteDTO> consultarCriteriosCausante(Integer idCausanteSolicitud);
    CriterioSolicitudDTO obtenerCriteriosPorIdCriterio(Integer idCriterioSolicitud);
    CriterioSolicitudCausanteDTO obtenerCriterioCausantePorIdCriterio(Integer idCriterioCausante);
    boolean obtenerRelacionLaboral(Integer rutBeneficiario);
    BigDecimal obtenerMontoDescuento(Integer rutBeneficiario);
    boolean verificarRelacionLaboralVigente(int idCausanteSolicitud, String esPortuario);
}