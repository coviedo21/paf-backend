package cl.gob.ips.pagos_asignacion_familiar.dao;

import cl.gob.ips.pagos_asignacion_familiar.dto.ListaComuna;
import cl.gob.ips.pagos_asignacion_familiar.dto.ResultadoRegionDTO;

public interface ComunaDAO {
    public ListaComuna obtenerIdComunaPorNombre(String nombreComuna);
    public int insertarComunaMantenedor(String comunaIngresada, Integer idComunaOriginal, String nombreComunaOriginal, String esCoincidencia);
    public ResultadoRegionDTO obtenerIdRegionPorNombre(String nombreRegion);
    public int insertarRegionMantenedor(String regionIngresada, Integer idRegionOriginal, String nombreComunaOriginal, String esCoincidencia);
}