package cl.gob.ips.cuenta_bancaria.dao;


import cl.gob.ips.cuenta_bancaria.dto.CuentaBancariaDTO;
public interface FileDAO {
    public void insertarCuentaBancaria(CuentaBancariaDTO cuentaBancaria);
}