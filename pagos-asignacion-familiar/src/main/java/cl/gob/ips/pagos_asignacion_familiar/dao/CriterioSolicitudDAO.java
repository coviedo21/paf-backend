package cl.gob.ips.pagos_asignacion_familiar.dao;

import java.util.List;

import cl.gob.ips.pagos_asignacion_familiar.dto.CriterioSolicitudDTO;

public interface CriterioSolicitudDAO {
    List<CriterioSolicitudDTO> consultarCriteriosSolicitud(Integer idSolicitud);
    String insertarCriterioSolicitud(CriterioSolicitudDTO criterios);
}