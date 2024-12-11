package cl.gob.ips.cuenta_bancaria.service.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.gob.ips.cuenta_bancaria.dao.FileDAO;
import cl.gob.ips.cuenta_bancaria.dto.ArchivoCuentaBancariaDTO;
import cl.gob.ips.cuenta_bancaria.dto.CuentaBancariaDTO;
import cl.gob.ips.cuenta_bancaria.service.FileService;

@Service
public class FileServiceImpl implements FileService {
    @Autowired
    private FileDAO fileDAO;

    public void insertarCuentasBancarias(List<ArchivoCuentaBancariaDTO> listaCuentasBancarias){
        for (ArchivoCuentaBancariaDTO archivo : listaCuentasBancarias) {
            CuentaBancariaDTO cuentaBancaria = new CuentaBancariaDTO();
            cuentaBancaria.setRutTitular(Integer.parseInt(archivo.getRutTitular()));
            cuentaBancaria.setDvTitular(archivo.getDvTitular());
            cuentaBancaria.setNumeroCuenta(archivo.getNumeroCuenta());
            cuentaBancaria.setCodigoBanco(Integer.parseInt(archivo.getCodigoBanco()));
            cuentaBancaria.setTipoCuenta(Integer.parseInt(archivo.getTipoCuenta()));
            cuentaBancaria.setEstadoCuenta(Integer.parseInt(archivo.getEstadoCuenta()));
            cuentaBancaria.setOrigen(5);
            fileDAO.insertarCuentaBancaria(cuentaBancaria);
        }
    }
}