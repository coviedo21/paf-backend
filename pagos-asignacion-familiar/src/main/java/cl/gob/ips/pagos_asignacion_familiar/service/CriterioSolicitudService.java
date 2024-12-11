package cl.gob.ips.pagos_asignacion_familiar.service;

import java.util.List;

import cl.gob.ips.pagos_asignacion_familiar.dto.CriterioSolicitudDTO;

public interface CriterioSolicitudService {
    List<CriterioSolicitudDTO> consultarCriteriosSolicitud(Integer idSolicitud);
    boolean validarCriteriosResolucion(Integer idSolicitud);
    boolean validarRolUnicoTributario(String rut);
}