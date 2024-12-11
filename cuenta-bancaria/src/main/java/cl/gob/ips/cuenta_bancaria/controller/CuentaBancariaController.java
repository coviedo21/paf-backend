package cl.gob.ips.cuenta_bancaria.controller;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.gob.ips.cuenta_bancaria.dto.BancoDTO;
import cl.gob.ips.cuenta_bancaria.dto.BancoTipoCuentaDTO;
import cl.gob.ips.cuenta_bancaria.dto.CuentaBancariaDTO;
import cl.gob.ips.cuenta_bancaria.dto.FormaPagoDTO;
import cl.gob.ips.cuenta_bancaria.dto.ResponseDTO;
import cl.gob.ips.cuenta_bancaria.service.CuentaBancariaService;

@RestController
@CrossOrigin("*")
@RequestMapping("/cuentaBancaria")
public class CuentaBancariaController {

    @Autowired
    private CuentaBancariaService cuentaBancariaService;

    @PostMapping("/insertarCuenta")
    public ResponseEntity<ResponseDTO> insertarCuentaBancaria(@RequestBody CuentaBancariaDTO cuentaBancaria) {
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setTimestamp(new Date());
        
        try {
            // Llamada al servicio para insertar la cuenta bancaria
            int resultado = cuentaBancariaService.insertarCuentaBancaria(cuentaBancaria);
            
            // Si la inserción fue exitosa
            if (resultado > 0) {
                responseDTO.setCodigoRetorno(0);
                responseDTO.setGlosaRetorno("Cuenta creada exitosamente!");
                responseDTO.setResultado(resultado);
                return new ResponseEntity<>(responseDTO, HttpStatus.OK);
            } else {
                // Si no se creó la cuenta, pero no hay excepción
                responseDTO.setCodigoRetorno(-1);
                responseDTO.setGlosaRetorno("No se creó la cuenta bancaria.");
                return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
            }
        } catch (RuntimeException e) {
            // Captura de la excepción cuando se produce un error (como número de cuenta repetido)
            responseDTO.setCodigoRetorno(-1);
            responseDTO.setGlosaRetorno(e.getMessage());  // El mensaje de la excepción contiene el error
            responseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Captura de cualquier otro error que ocurra
            responseDTO.setCodigoRetorno(-1);
            responseDTO.setGlosaRetorno("Error al crear la cuenta bancaria: " + e.getMessage());
            responseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/obtenerCuentas/{rutTitular}")
    public ResponseEntity<List<CuentaBancariaDTO>> obtenerCuentasBancariasPorRut(@PathVariable("rutTitular") int rutTitular) {
        List<CuentaBancariaDTO> cuentas = cuentaBancariaService.obtenerCuentasBancariasPorRut(rutTitular);
        if (cuentas != null && !cuentas.isEmpty()) {
            return ResponseEntity.ok(cuentas);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/obtenerBancos")
    public ResponseEntity<List<BancoDTO>> obtenerBancos() {
        List<BancoDTO> bancos = cuentaBancariaService.obtenerBancos();
        if (bancos != null && !bancos.isEmpty()) {
            return ResponseEntity.ok(bancos);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/obtenerTipoCuentas/{idBanco}")
    public ResponseEntity<List<BancoTipoCuentaDTO>> obtenerTipoCuentas(@PathVariable("idBanco") int idBanco) {
        List<BancoTipoCuentaDTO> tipoCuentas = cuentaBancariaService.obtenerBancoTipoCuenta(idBanco);
        if (tipoCuentas != null && !tipoCuentas.isEmpty()) {
            return ResponseEntity.ok(tipoCuentas);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/obtenerFormasPago/{idSistema}")
    public ResponseEntity<List<FormaPagoDTO>> obtenerFormasPago(@PathVariable("idSistema") int idSistema) {
        List<FormaPagoDTO> formaPago = cuentaBancariaService.obtenerFormasPago(idSistema);
        if (formaPago != null && !formaPago.isEmpty()) {
            return ResponseEntity.ok(formaPago);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/obtenerCuentasPorID/{idCuenta}")
    public ResponseEntity<List<CuentaBancariaDTO>> obtenerCuentasBancariasPorID(@PathVariable("idCuenta") int idCuenta) {
        List<CuentaBancariaDTO> cuentas = cuentaBancariaService.obtenerCuentasBancariasPorID(idCuenta);
        if (cuentas != null && !cuentas.isEmpty()) {
            return ResponseEntity.ok(cuentas);
        } else {
            return ResponseEntity.noContent().build();
        }
    }
}