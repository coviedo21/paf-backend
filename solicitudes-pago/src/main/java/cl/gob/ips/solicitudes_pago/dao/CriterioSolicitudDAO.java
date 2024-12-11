package cl.gob.ips.solicitudes_pago.dao;

import java.util.List;

import cl.gob.ips.solicitudes_pago.dto.CriterioSolicitudCausanteDTO;
import cl.gob.ips.solicitudes_pago.dto.CriterioSolicitudDTO;

public interface CriterioSolicitudDAO {
    List<CriterioSolicitudDTO> consultarCriteriosSolicitud(Integer idSolicitud);
    String insertarCriterioSolicitud(CriterioSolicitudDTO criterios);
    String insertarCriterioCausante(CriterioSolicitudCausanteDTO criterios);
    boolean actualizarCriterioSolicitud(CriterioSolicitudDTO criterioSolicitudDTO);
    boolean actualizarCriterioCausante(CriterioSolicitudCausanteDTO criterioCausante);
    List<CriterioSolicitudCausanteDTO> consultarCriterioCausante(Integer idCausanteSolicitud);
    CriterioSolicitudDTO obtenerCriteriosPorIdCriterio(Integer idCriterioSolicitud);
}