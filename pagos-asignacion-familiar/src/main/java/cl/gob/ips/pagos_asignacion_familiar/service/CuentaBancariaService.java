package cl.gob.ips.pagos_asignacion_familiar.service;

import java.util.List;

import cl.gob.ips.pagos_asignacion_familiar.dto.BancoDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.BancoTipoCuentaDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.CuentaBancariaDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.FormaPagoDTO;

public interface CuentaBancariaService {
    List<CuentaBancariaDTO> obtenerCuentasBancariasPorRut(int rutTitular);
    int insertarCuentaBancaria(CuentaBancariaDTO cuentaBancaria);
    List<BancoDTO> obtenerBancos();
    List<BancoTipoCuentaDTO> obtenerBancoTipoCuenta(int idBanco);
    List<FormaPagoDTO> obtenerFormasPago(int idSistema);
    List<CuentaBancariaDTO> obtenerCuentasBancariasPorID(int idCuenta);
}