package cl.gob.ips.cuenta_bancaria.service;

import java.util.List;

import cl.gob.ips.cuenta_bancaria.dto.BancoDTO;
import cl.gob.ips.cuenta_bancaria.dto.BancoTipoCuentaDTO;
import cl.gob.ips.cuenta_bancaria.dto.CuentaBancariaDTO;
import cl.gob.ips.cuenta_bancaria.dto.FormaPagoDTO;

public interface CuentaBancariaService {
    List<CuentaBancariaDTO> obtenerCuentasBancariasPorRut(int rutTitular);
    int insertarCuentaBancaria(CuentaBancariaDTO cuentaBancaria);
    List<BancoDTO> obtenerBancos();
    List<BancoTipoCuentaDTO> obtenerBancoTipoCuenta(int idBanco);
    List<FormaPagoDTO> obtenerFormasPago(int idSistema);
    List<CuentaBancariaDTO> obtenerCuentasBancariasPorID(int idCuenta);
}