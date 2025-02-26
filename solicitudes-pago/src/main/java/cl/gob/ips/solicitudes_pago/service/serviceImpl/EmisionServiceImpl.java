package cl.gob.ips.solicitudes_pago.service.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import cl.gob.ips.solicitudes_pago.dto.EmisionArchivoDTO;
import cl.gob.ips.solicitudes_pago.service.EmisionService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmisionServiceImpl implements EmisionService {
    
    @Override
    public Boolean validarSolicitudesEmitidas(List<EmisionArchivoDTO> emision){
        return true;
    }

    /*public List<EmisionDTO> obtenerEmisiones(){
        return null;
    }

    public EmisionDTO obtenerEmision(int idEmision){
        return null;
    }*/
}
