package cl.gob.ips.mantenedor_comunas.service.serviceImpl;

import java.util.List;

import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import cl.gob.ips.mantenedor_comunas.dao.ComunaDAO;
import cl.gob.ips.mantenedor_comunas.dto.ComunaDTO;
import cl.gob.ips.mantenedor_comunas.dto.ComunaMantenedorDTO;
import cl.gob.ips.mantenedor_comunas.dto.ListaComunaDTO;
import cl.gob.ips.mantenedor_comunas.dto.RegionDTO;
import cl.gob.ips.mantenedor_comunas.dto.ResponseDTO;
import cl.gob.ips.mantenedor_comunas.dto.ResultadoComunaDTO;
import cl.gob.ips.mantenedor_comunas.dto.ResultadoRegionDTO;
import cl.gob.ips.mantenedor_comunas.service.ComunaService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComunaServiceImpl implements ComunaService {
    private final RestTemplate restTemplate;
    private static final double SIMILARITY_THRESHOLD = 0.80;
    private final ComunaDAO comunaDAO;

    @Override
    public RegionDTO obtenerRegiones(){
        RegionDTO regionDTO = new RegionDTO();
        String url = String.format("https://prod.ips.apim.junar.com/stjc/v1/consulta/listarregiones.json/?auth_key=VWLc2gcWVwvB23iTZy9TQLQ0EJfVN4oCEsJnMMBf");
        regionDTO = restTemplate.getForObject(url, RegionDTO.class);
        if(regionDTO.getResultado()!=null){
            return regionDTO;
        }
        return null;
    }    

    @Override
    public ComunaDTO obtenerComunas(int idRegion){
        ComunaDTO comunaDTO = new ComunaDTO();
        String url = String.format("https://prod.ips.apim.junar.com/stjc/v1/consulta/listarcomunas/"+idRegion+".json/?auth_key=VWLc2gcWVwvB23iTZy9TQLQ0EJfVN4oCEsJnMMBf");
        comunaDTO = restTemplate.getForObject(url, ComunaDTO.class);
        if(comunaDTO.getResultado()!=null){
            return comunaDTO;
        }
        return null;
    }

    @Override
    public ListaComunaDTO validarComuna(int codigoRegion, String comuna) {
        Integer idCoincidencia = 0;
        ListaComunaDTO comunaEncontrada = new ListaComunaDTO();
        //Se busca la comuna en la tabla mantenedor de comuna
        comunaEncontrada = comunaDAO.obtenerIdComunaPorNombre(comuna);

        //Si no se consigue la comuna en tabla mantendor, se debe validar que exista en Junar
        if(comunaEncontrada==null){ 
            //RegionDTO regionDTO = obtenerRegiones();
            boolean coincidenciaEncontrada = false;
            
            //LevenshteinDistance levenshtein = new LevenshteinDistance();
            //double desiredSimilarity = 0.60;  // Similitud deseada del 60%

                    //Obtenemos la lista de comunas de Junar
                    ComunaDTO comunaDTO = obtenerComunas(codigoRegion);

                    
                    if (comunaDTO != null && comunaDTO.getResultado() != null) {
                        //Validamos comuna por comuna si es igual a la ingresada en el archivo
                        for (ListaComunaDTO comunaExistente : comunaDTO.getResultado().getListaComunas()) {
                            String comunaIngresada = comuna.trim().toUpperCase();
                            String comunaBD = comunaExistente.getNombreComuna().trim().toUpperCase();

                            // Calcular el umbral en función de la longitud de la cadena
                            //int maxLength = Math.max(comunaIngresada.length(), comunaBD.length());
                            //int threshold = (int) (maxLength * (1 - desiredSimilarity));

                            // Calcula la distancia de Levenshtein
                            //int distance = levenshtein.apply(comunaIngresada, comunaBD);
                            if(comunaIngresada.equalsIgnoreCase(comunaBD)){
                            //if (distance == 0) {
                                comunaEncontrada = new ListaComunaDTO();
                                comunaEncontrada.setCodComuna(comunaExistente.getCodComuna());
                                comunaEncontrada.setNombreComuna(comunaExistente.getNombreComuna());
                                return comunaEncontrada;  // Son exactamente iguales, retorno el codigo de la comuna original
                            }
                            /*if (distance <= threshold) {
                                System.out.println("Comuna Encontrada: " + comunaExistente.getNombreComuna());
                                coincidenciaEncontrada = true;
                                idCoincidencia = comunaDAO.insertarComunaMantenedor(
                                        comuna, 
                                        comunaExistente.getCodComuna(), 
                                        comunaExistente.getNombreComuna(), 
                                        "S"
                                );
                                if(idCoincidencia>0){
                                    comunaEncontrada = new ListaComunaDTO();
                                    comunaEncontrada.setCodComuna(comunaExistente.getCodComuna());
                                    comunaEncontrada.setNombreComuna(comunaExistente.getNombreComuna());
                                }
                                //break;
                            }*/
                        }
                    }
                
            

            if (!coincidenciaEncontrada) {
                /*idCoincidencia = comunaDAO.insertarComunaMantenedor(
                        comuna, 
                        null, 
                        null, 
                        "N"
                );
                comunaEncontrada = new ListaComuna();
                comunaEncontrada.setCodComuna(0);
                comunaEncontrada.setNombreComuna(null);*/
                ///EN ARCHIVO DE ERRORES COLOCAR ESTA LINEA
            }
        }
        return comunaEncontrada;
    }

    @Override
    public ResultadoRegionDTO validarRegion(String region) {
        Integer idCoincidencia = 0;
        ResultadoRegionDTO regionEncontrada = new ResultadoRegionDTO();
        
        // Se busca la región en la tabla mantenedor de región
        regionEncontrada = comunaDAO.obtenerIdRegionPorNombre(region);

        if (regionEncontrada == null) { // Si no se consigue la región en tabla mantenedor, se debe validar que exista en Junar
            RegionDTO regionDTO = obtenerRegiones();
            boolean coincidenciaEncontrada = false;

            LevenshteinDistance levenshtein = new LevenshteinDistance();
            double desiredSimilarity = 0.80;  // Similitud deseada del 60%

            if (regionDTO != null && regionDTO.getResultado() != null) {
                for (ResultadoRegionDTO regionExistente : regionDTO.getResultado()) {
                    String regionIngresada = region.trim().toUpperCase();
                    String regionBD = regionExistente.getNombreRegion().trim().toUpperCase();

                    // Calcular el umbral en función de la longitud de la cadena
                    int maxLength = Math.max(regionIngresada.length(), regionBD.length());
                    int threshold = (int) (maxLength * (1 - desiredSimilarity));

                    // Calcula la distancia de Levenshtein
                    int distance = levenshtein.apply(regionIngresada, regionBD);

                    if (distance == 0) {
                        regionEncontrada = new ResultadoRegionDTO();
                        regionEncontrada.setCodigoRegion(regionExistente.getCodigoRegion());
                        regionEncontrada.setNombreRegion(regionExistente.getNombreRegion());
                        return regionEncontrada;  // Son exactamente iguales, retorno el código de la región original
                    }

                    if (distance <= threshold) {
                        System.out.println("Región Encontrada: " + regionExistente.getNombreRegion());
                        coincidenciaEncontrada = true;
                        idCoincidencia = comunaDAO.insertarRegionMantenedor(
                                region,
                                regionExistente.getCodigoRegion(),
                                regionExistente.getNombreRegion(),
                                "S"
                        );
                        if (idCoincidencia > 0) {
                            regionEncontrada = new ResultadoRegionDTO();
                            regionEncontrada.setCodigoRegion(regionExistente.getCodigoRegion());
                            regionEncontrada.setNombreRegion(regionExistente.getNombreRegion());
                        }
                        break;
                    }
                }
            }

            if (!coincidenciaEncontrada) {
            /* idCoincidencia = comunaDAO.insertarRegionMantenedor(
                        region,
                        null,
                        null,
                        "N"
                );
                regionEncontrada = new ResultadoRegionDTO();
                regionEncontrada.setCodigoRegion(0);
                regionEncontrada.setNombreRegion(null);*/
                //Asignar esta linea en archivo de error
            }
        }
        return regionEncontrada;
    }

    public ComunaMantenedorDTO obtenerComunaPorIdCoincidencia(Integer iIdCoincidencia){
        return comunaDAO.obtenerComunaPorIdCoincidencia(iIdCoincidencia);
    }
        
    public int actualizarComunaMantenedor(ComunaMantenedorDTO comuna){
        return comunaDAO.actualizarComunaMantenedor(comuna);
    }
        
    public int eliminarComunaPorIdCoincidencia(Integer iIdCoincidencia){
        return comunaDAO.eliminarComunaPorIdCoincidencia(iIdCoincidencia);
    }

    public List<ComunaMantenedorDTO> obtenerRegistrosPorComunaOriginal(Integer iIdComunaOriginal){
        return comunaDAO.obtenerRegistrosPorComunaOriginal(iIdComunaOriginal);
    }

    public int insertarComunaMantenedor(ComunaMantenedorDTO comuna){
        return comunaDAO.insertarComunaMantenedor(comuna);
    }
}