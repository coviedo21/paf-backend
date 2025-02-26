package cl.gob.ips.solicitudes_pago.service;

import java.util.List;

import cl.gob.ips.solicitudes_pago.dto.EmisionArchivoDTO;

public interface EmisionService {
    public Boolean validarSolicitudesEmitidas(List<EmisionArchivoDTO> emision);
}