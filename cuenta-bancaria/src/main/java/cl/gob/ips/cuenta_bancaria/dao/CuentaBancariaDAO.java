package cl.gob.ips.cuenta_bancaria.dao;

import java.util.List;

import cl.gob.ips.cuenta_bancaria.dto.BancoDTO;
import cl.gob.ips.cuenta_bancaria.dto.BancoTipoCuentaDTO;
import cl.gob.ips.cuenta_bancaria.dto.CuentaBancariaDTO;
import cl.gob.ips.cuenta_bancaria.dto.FormaPagoDTO;

public interface CuentaBancariaDAO {
    List<CuentaBancariaDTO> obtenerCuentasBancariasPorRut(int rutTitular);
    int insertarCuentaBancaria(CuentaBancariaDTO cuentaBancaria, boolean esArchivo);
    List<BancoDTO> obtenerBancos();
    List<BancoTipoCuentaDTO> obtenerBancoTipoCuenta(int idBanco);
    List<FormaPagoDTO> obtenerFormasPago(int idSistema);
    List<CuentaBancariaDTO> obtenerCuentasBancariasPorID(int idCuenta);
}