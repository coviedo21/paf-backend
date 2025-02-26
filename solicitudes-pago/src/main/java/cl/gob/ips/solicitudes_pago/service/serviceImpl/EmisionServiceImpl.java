package cl.gob.ips.solicitudes_pago.service.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.gob.ips.solicitudes_pago.dao.EmisionDAO;
import cl.gob.ips.solicitudes_pago.dao.FileDAO;
import cl.gob.ips.solicitudes_pago.dto.EmisionArchivoDTO;
import cl.gob.ips.solicitudes_pago.dto.EmisionDTO;
import cl.gob.ips.solicitudes_pago.service.EmisionService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmisionServiceImpl implements EmisionService {
    
    @Autowired
    private EmisionDAO emisionDAO;

    @Override
    public Boolean validarSolicitudesEmitidas(List<EmisionArchivoDTO> emision){
        return emisionDAO.validarSolicitudesEmitidas(emision);
    }

    @Override
    public List<EmisionDTO> obtenerEmisiones(){
        return emisionDAO.obtenerEmisiones();
    }

    @Override
    public int insertarEmision(EmisionDTO emision){
        return emisionDAO.insertarEmision(emision);
    }
}
