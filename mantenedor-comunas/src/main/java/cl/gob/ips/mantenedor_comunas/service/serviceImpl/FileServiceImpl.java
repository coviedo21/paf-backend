package cl.gob.ips.mantenedor_comunas.service.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.gob.ips.mantenedor_comunas.dao.ComunaDAO;
import cl.gob.ips.mantenedor_comunas.dto.ArchivoSolicitudDTO;
import cl.gob.ips.mantenedor_comunas.dto.ComunaMantenedorDTO;
import cl.gob.ips.mantenedor_comunas.service.FileService;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private ComunaDAO fileDAO;
    
    public void insertarComuna(List<ArchivoSolicitudDTO> listaComunas){
        
            for (ArchivoSolicitudDTO archivo : listaComunas) {
                ComunaMantenedorDTO comuna = new ComunaMantenedorDTO();
                comuna.setComunaIngresada(archivo.getComunaIngresada());
                comuna.setIdComunaOriginal(Integer.valueOf(archivo.getIdComunaOriginal()));
                comuna.setNombreComunaOriginal(archivo.getNombreComunaOriginal());
                fileDAO.insertarComunaMantenedor(comuna);                
            }
    }

}