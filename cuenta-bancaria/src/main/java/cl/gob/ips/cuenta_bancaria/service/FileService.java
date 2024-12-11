package cl.gob.ips.cuenta_bancaria.service;

import java.util.List;
import cl.gob.ips.cuenta_bancaria.dto.ArchivoCuentaBancariaDTO;
public interface FileService {
    public void insertarCuentasBancarias(List<ArchivoCuentaBancariaDTO> listaCuentasBancarias);
}