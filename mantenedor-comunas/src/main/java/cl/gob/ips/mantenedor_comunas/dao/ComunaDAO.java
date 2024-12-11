package cl.gob.ips.mantenedor_comunas.dao;

import java.util.List;

import cl.gob.ips.mantenedor_comunas.dto.ComunaMantenedorDTO;
import cl.gob.ips.mantenedor_comunas.dto.ListaComunaDTO;
import cl.gob.ips.mantenedor_comunas.dto.ResultadoRegionDTO;

public interface ComunaDAO {
    ListaComunaDTO obtenerIdComunaPorNombre(String nombreComuna);
    int insertarComunaMantenedor(ComunaMantenedorDTO comuna);
    ResultadoRegionDTO obtenerIdRegionPorNombre(String nombreRegion);
    int insertarRegionMantenedor(String regionIngresada, Integer idRegionOriginal, String nombreComunaOriginal, String esCoincidencia);
    ComunaMantenedorDTO obtenerComunaPorIdCoincidencia(Integer iIdCoincidencia);
    int actualizarComunaMantenedor(ComunaMantenedorDTO comuna);
    int eliminarComunaPorIdCoincidencia(Integer iIdCoincidencia);
    List<ComunaMantenedorDTO> obtenerRegistrosPorComunaOriginal(Integer iIdComunaOriginal);
}