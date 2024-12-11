package cl.gob.ips.solicitudes_pago.service.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.gob.ips.solicitudes_pago.dao.UtilDAO;
import cl.gob.ips.solicitudes_pago.dto.OrigenDTO;
import cl.gob.ips.solicitudes_pago.service.UtilService;

@Service
public class UtilServiceImpl implements UtilService{
    @Autowired
    private UtilDAO utilDAO;

    public List<OrigenDTO> obtenerOrigenes(){
        return utilDAO.obtenerOrigenes();    
    }
}
