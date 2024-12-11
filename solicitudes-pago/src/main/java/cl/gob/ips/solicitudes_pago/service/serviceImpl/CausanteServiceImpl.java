package cl.gob.ips.solicitudes_pago.service.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.gob.ips.solicitudes_pago.dao.CausanteDAO;
import cl.gob.ips.solicitudes_pago.dto.CausanteDTO;
import cl.gob.ips.solicitudes_pago.service.CausanteService;

@Service
public class CausanteServiceImpl implements CausanteService{
    @Autowired
    private CausanteDAO causanteDAO;

    public List<CausanteDTO> obtenerDetalleCausante(int rutBeneficiario){
        return causanteDAO.obtenerDetalleCausante(rutBeneficiario);
    }
}
