package cl.gob.ips.solicitudes_pago.service;

import java.util.List;

import cl.gob.ips.solicitudes_pago.dto.CriterioSolicitudCausanteDTO;
import cl.gob.ips.solicitudes_pago.dto.CriterioSolicitudDTO;

public interface CriterioSolicitudService {
    List<CriterioSolicitudDTO> consultarCriteriosSolicitud(Integer idSolicitud);
    boolean validarCriteriosResolucion(Integer idSolicitud);
    boolean validarRolUnicoTributario(String rut);
    boolean actualizarCriterioSolicitud(CriterioSolicitudDTO criterioSolicitudDTO);
    boolean actualizarCriterioCausante(CriterioSolicitudCausanteDTO criterioCausante);
    List<CriterioSolicitudCausanteDTO> consultarCriteriosCausante(Integer idCausanteSolicitud);
    public CriterioSolicitudDTO obtenerCriteriosPorIdCriterio(Integer idCriterioSolicitud);
}