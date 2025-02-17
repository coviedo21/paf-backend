package cl.gob.ips.solicitudes_pago.service;

import java.time.LocalDate;
import java.util.List;

import cl.gob.ips.solicitudes_pago.dto.OrigenDTO;

public interface UtilService {
    List<OrigenDTO> obtenerOrigenes(int tipo);
    boolean esFechaValida(LocalDate fechaInicio, LocalDate fechaSolicitud);
}