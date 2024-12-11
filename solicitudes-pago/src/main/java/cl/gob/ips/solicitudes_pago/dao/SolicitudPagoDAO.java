package cl.gob.ips.solicitudes_pago.dao;

import java.util.List;

import cl.gob.ips.solicitudes_pago.dto.CausanteSolicitudDTO;
import cl.gob.ips.solicitudes_pago.dto.ResolucionDTO;
import cl.gob.ips.solicitudes_pago.dto.SolicitudDTO;
import cl.gob.ips.solicitudes_pago.dto.TipoSolicitanteDTO;

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
    boolean actualizarCausanteSolicitud(CausanteSolicitudDTO causanteSolicitud);
    List<SolicitudDTO> obtenerSolicitudesPorEstado(int idEstado);
    List<SolicitudDTO> obtenerSolicitudesAntiguas(int dias);
    List<SolicitudDTO> obtenerSolicitudesPorBeneficiario(Integer rutBeneficiario);
    List<SolicitudDTO> obtenerSolicitudesPorEmpleador(Integer rutEmpleador);
    List<SolicitudDTO> obtenerSolicitudesPorOrigen(Integer origen, Integer proceso, Integer estado);
}