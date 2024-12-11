package cl.gob.ips.pagos_asignacion_familiar.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cl.gob.ips.pagos_asignacion_familiar.dto.ArchivoCuentaBancariaDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.ArchivoSolicitudDTO;

@Service
public interface FileService {
    public void insertarSolicitud(List<ArchivoSolicitudDTO> listaSolicitudes);
    public void insertarCuentasBancarias(List<ArchivoCuentaBancariaDTO> listaCuentasBancarias);
}
