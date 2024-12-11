package cl.gob.ips.pagos_asignacion_familiar.dao;

import java.util.List;

import org.springframework.stereotype.Component;

import cl.gob.ips.pagos_asignacion_familiar.dto.ArchivoCuentaBancariaDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.ArchivoSolicitudDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.CuentaBancariaDTO;

@Component
public interface FileDAO {
    public void insertarSolicitud(ArchivoSolicitudDTO solicitud);
    public void insertarCuentaBancaria(CuentaBancariaDTO cuentaBancaria);
}