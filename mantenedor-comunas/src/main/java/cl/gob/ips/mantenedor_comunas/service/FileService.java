package cl.gob.ips.mantenedor_comunas.service;

import java.util.List;

import cl.gob.ips.mantenedor_comunas.dto.ArchivoSolicitudDTO;

public interface FileService {
    public void insertarComuna(List<ArchivoSolicitudDTO> listaComunas);
}