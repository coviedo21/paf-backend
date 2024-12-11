package cl.gob.ips.cuenta_bancaria.service.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.gob.ips.cuenta_bancaria.dao.CuentaBancariaDAO;
import cl.gob.ips.cuenta_bancaria.dto.BancoDTO;
import cl.gob.ips.cuenta_bancaria.dto.BancoTipoCuentaDTO;
import cl.gob.ips.cuenta_bancaria.dto.CuentaBancariaDTO;
import cl.gob.ips.cuenta_bancaria.dto.FormaPagoDTO;
import cl.gob.ips.cuenta_bancaria.service.CuentaBancariaService;

import java.util.List;

@Service
public class CuentaBancariaServiceImpl implements CuentaBancariaService {

    @Autowired
    private CuentaBancariaDAO cuentaBancariaDAO;

    @Override
    public List<CuentaBancariaDTO> obtenerCuentasBancariasPorRut(int rutTitular) {
        return cuentaBancariaDAO.obtenerCuentasBancariasPorRut(rutTitular);
    }

    public int insertarCuentaBancaria(CuentaBancariaDTO cuentaBancaria) {
        return cuentaBancariaDAO.insertarCuentaBancaria(cuentaBancaria,false);
    }

    public List<BancoDTO> obtenerBancos(){
        return cuentaBancariaDAO.obtenerBancos();
    }

    public List<BancoTipoCuentaDTO> obtenerBancoTipoCuenta(int idBanco){
        return cuentaBancariaDAO.obtenerBancoTipoCuenta(idBanco);
    }

    public List<FormaPagoDTO> obtenerFormasPago(int idSistema){
        return cuentaBancariaDAO.obtenerFormasPago(idSistema);
    }

    public List<CuentaBancariaDTO> obtenerCuentasBancariasPorID(int idCuenta){
        return cuentaBancariaDAO.obtenerCuentasBancariasPorID(idCuenta);
    }
}