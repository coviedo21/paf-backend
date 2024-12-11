package cl.gob.ips.pagos_asignacion_familiar.service;

import org.springframework.stereotype.Service;
import cl.gob.ips.pagos_asignacion_familiar.dto.ComunaDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.ListaComuna;
import cl.gob.ips.pagos_asignacion_familiar.dto.RegionDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.ResultadoRegionDTO;

@Service
public interface ComunaService {

    public RegionDTO obtenerRegiones();
    public ComunaDTO obtenerComunas(int idRegion);
    public ListaComuna validarComuna(int idRegion, String comuna);
    public ResultadoRegionDTO validarRegion(String region);
}