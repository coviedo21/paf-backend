package cl.gob.ips.cuenta_bancaria.dao.daoImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cl.gob.ips.cuenta_bancaria.dao.CuentaBancariaDAO;
import cl.gob.ips.cuenta_bancaria.dao.FileDAO;
import cl.gob.ips.cuenta_bancaria.dto.CuentaBancariaDTO;

@Repository
public class FileDAOImpl implements FileDAO{
    
    @Autowired
    private CuentaBancariaDAO cuentaBancariaDAO;
    
    public void insertarCuentaBancaria(CuentaBancariaDTO cuentaBancaria){
        cuentaBancariaDAO.insertarCuentaBancaria(cuentaBancaria,true);
    }
}