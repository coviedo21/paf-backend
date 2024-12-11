package cl.gob.ips.pagos_asignacion_familiar.dao;

import java.util.List;

import cl.gob.ips.pagos_asignacion_familiar.dto.CausanteSolicitudDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.ResolucionDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.SolicitudDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.TipoSolicitanteDTO;

public interface SolicitudPagoDAO {
    int insertarSolicitudPago(SolicitudDTO solicitudPago);
    List<SolicitudDTO> consultarSolicitudPago(Integer idSolicitud);
    boolean actualizarSolicitudPago(SolicitudDTO solicitudPago);
    List<SolicitudDTO> filtrarSolicitudesPago(String texto);
    List<SolicitudDTO> obtenerSolicitudesPorProceso(int idProceso, int tipoSolicitante);
    boolean actualizarIdProceso(int iIdSolicitud, int nuevoIdProceso);
    int insertarResolucion(ResolucionDTO resolucionDTO);
    List<TipoSolicitanteDTO> obtenerTiposSolicitante();
    List<CausanteSolicitudDTO> obtenerCausantesPorSolicitud(int idSolicitud);
    boolean eliminarCausanteSolicitud(int idCausanteSolicitud);
}