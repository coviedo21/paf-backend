package cl.gob.ips.solicitudes_pago.dao;

import cl.gob.ips.solicitudes_pago.dto.ArchivoSolicitudDTO;
public interface FileDAO {
    public void insertarSolicitud(ArchivoSolicitudDTO solicitud);
}