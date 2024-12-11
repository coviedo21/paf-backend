package cl.gob.ips.mantenedor_comunas.service;

import java.util.List;
import cl.gob.ips.mantenedor_comunas.dto.ComunaDTO;
import cl.gob.ips.mantenedor_comunas.dto.ComunaMantenedorDTO;
import cl.gob.ips.mantenedor_comunas.dto.ListaComunaDTO;
import cl.gob.ips.mantenedor_comunas.dto.RegionDTO;
import cl.gob.ips.mantenedor_comunas.dto.ResultadoRegionDTO;
public interface ComunaService {

    public RegionDTO obtenerRegiones();
    public ComunaDTO obtenerComunas(int idRegion);
    public ListaComunaDTO validarComuna(int idRegion, String comuna);
    public ResultadoRegionDTO validarRegion(String region);
    ComunaMantenedorDTO obtenerComunaPorIdCoincidencia(Integer iIdCoincidencia);
    int actualizarComunaMantenedor(ComunaMantenedorDTO comuna);
    int eliminarComunaPorIdCoincidencia(Integer iIdCoincidencia);
    List<ComunaMantenedorDTO> obtenerRegistrosPorComunaOriginal(Integer iIdComunaOriginal);
    int insertarComunaMantenedor(ComunaMantenedorDTO comuna);
}