package cl.gob.ips.cuenta_bancaria.dao.daoImpl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import cl.gob.ips.cuenta_bancaria.dao.CuentaBancariaDAO;
import cl.gob.ips.cuenta_bancaria.dto.BancoDTO;
import cl.gob.ips.cuenta_bancaria.dto.BancoTipoCuentaDTO;
import cl.gob.ips.cuenta_bancaria.dto.CuentaBancariaDTO;
import cl.gob.ips.cuenta_bancaria.dto.FormaPagoDTO;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class CuentaBancariaDAOImpl implements CuentaBancariaDAO {

    private final JdbcTemplate jdbcTemplate;

    @Value("${spring.datasource.schema}")
    private String esquema;

    @Autowired
    public CuentaBancariaDAOImpl(@Qualifier("pafJdbc") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int insertarCuentaBancaria(CuentaBancariaDTO cuentaBancaria, boolean esArchivo) {
        int tipoCuenta = 0;
        if(esArchivo){
            tipoCuenta = obtenerIdTipoCuentaPorCodigo(cuentaBancaria.getTipoCuenta());
        }
        else{
            tipoCuenta = cuentaBancaria.getTipoCuenta();
        }

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName(esquema)
                .withProcedureName("SP_InsertarCuentaBancaria")
                .declareParameters(
                        new SqlParameter("vcNumeroCuenta", Types.VARCHAR),
                        new SqlParameter("iRutTitular", Types.INTEGER),
                        new SqlParameter("vcDvTitular", Types.VARCHAR),
                        new SqlParameter("iIdCodigoBanco", Types.INTEGER),
                        new SqlParameter("iIdTipoCuenta", Types.INTEGER),
                        new SqlParameter("iIdEstadoCuenta", Types.INTEGER),
                        new SqlParameter("iIdOrigen", Types.INTEGER),
                        new SqlOutParameter("idCuenta", Types.INTEGER),
                        new SqlOutParameter("mensajeRespuesta", Types.VARCHAR),  // Parámetero de salida para el mensaje
                new SqlOutParameter("codigoRespuesta", Types.INTEGER)    // Parámetero de salida para el código
                );

        MapSqlParameterSource inParams = new MapSqlParameterSource()
                .addValue("vcNumeroCuenta", cuentaBancaria.getNumeroCuenta())
                .addValue("iRutTitular", cuentaBancaria.getRutTitular())
                .addValue("vcDvTitular", cuentaBancaria.getDvTitular())
                .addValue("iIdCodigoBanco", cuentaBancaria.getCodigoBanco())
                .addValue("iIdTipoCuenta", tipoCuenta)
                .addValue("iIdEstadoCuenta", cuentaBancaria.getEstadoCuenta())
                .addValue("iIdOrigen", cuentaBancaria.getOrigen());

        try {
            Map<String, Object> result = jdbcCall.execute(inParams);
            Integer codigoRespuesta = (Integer) result.get("codigoRespuesta");
            String mensajeRespuesta = (String) result.get("mensajeRespuesta");
            System.out.println("Este es el mensaje respuesta: "+mensajeRespuesta);
            if (codigoRespuesta != null && codigoRespuesta == -1) {
                throw new RuntimeException(mensajeRespuesta);
            }

            // Si no hubo error, retornar el idCuenta
            return (Integer) result.get("idCuenta");
        } catch (Exception e) {
            throw new RuntimeException("Error al insertar cuenta bancaria: " + e.getMessage(), e);
        }
    }

    @Override
    public List<CuentaBancariaDTO> obtenerCuentasBancariasPorRut(int rutTitular) {
        String sql = "SELECT * FROM paf.fn_ObtenerCuentasBancariasPorRut(?)";

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, new Object[]{rutTitular});
        
        List<CuentaBancariaDTO> cuentas = new ArrayList<>();
        for (Map<String, Object> u : results) {
            CuentaBancariaDTO cuentaDTO = new CuentaBancariaDTO();
            if (u.get("IdCuentaBancaria") != null) cuentaDTO.setIdCuentaBancaria((Integer) u.get("IdCuentaBancaria"));
            if (u.get("NumeroCuenta") != null) cuentaDTO.setNumeroCuenta((String) u.get("NumeroCuenta"));
            if (u.get("RutTitular") != null) cuentaDTO.setRutTitular((Integer) u.get("RutTitular"));
            if (u.get("DvTitular") != null) cuentaDTO.setDvTitular((String) u.get("DvTitular"));
            if (u.get("CodigoBanco") != null) cuentaDTO.setCodigoBanco((Integer) u.get("CodigoBanco"));
            if (u.get("nombreBanco") != null) cuentaDTO.setNombreBanco((String) u.get("nombreBanco"));
            if (u.get("TipoCuenta") != null) cuentaDTO.setTipoCuenta((Integer) u.get("TipoCuenta"));
            if (u.get("nombreTipoCuenta") != null) cuentaDTO.setNombreTipoCuenta((String) u.get("nombreTipoCuenta"));
            if (u.get("EstadoCuenta") != null) cuentaDTO.setEstadoCuenta((Integer) u.get("EstadoCuenta"));
            if (u.get("Origen") != null) cuentaDTO.setOrigen((Integer) u.get("Origen"));
            cuentas.add(cuentaDTO);
        }

        return cuentas;
    }

    @Override
    public List<CuentaBancariaDTO> obtenerCuentasBancariasPorID(int idCuenta) {
        String sql = "SELECT * FROM paf.fn_ObtenerCuentasBancariasPorID(?)";

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, new Object[]{idCuenta});
        
        List<CuentaBancariaDTO> cuentas = new ArrayList<>();
        for (Map<String, Object> u : results) {
            CuentaBancariaDTO cuentaDTO = new CuentaBancariaDTO();
            if (u.get("IdCuentaBancaria") != null) cuentaDTO.setIdCuentaBancaria((Integer) u.get("IdCuentaBancaria"));
            if (u.get("NumeroCuenta") != null) cuentaDTO.setNumeroCuenta((String) u.get("NumeroCuenta"));
            if (u.get("RutTitular") != null) cuentaDTO.setRutTitular((Integer) u.get("RutTitular"));
            if (u.get("DvTitular") != null) cuentaDTO.setDvTitular((String) u.get("DvTitular"));
            if (u.get("CodigoBanco") != null) cuentaDTO.setCodigoBanco((Integer) u.get("CodigoBanco"));
            if (u.get("TipoCuenta") != null) cuentaDTO.setTipoCuenta((Integer) u.get("TipoCuenta"));
            if (u.get("EstadoCuenta") != null) cuentaDTO.setEstadoCuenta((Integer) u.get("EstadoCuenta"));
            if (u.get("Origen") != null) cuentaDTO.setOrigen((Integer) u.get("Origen"));
            cuentas.add(cuentaDTO);
        }

        return cuentas;
    }

    @Override
    public List<BancoDTO> obtenerBancos() {
        String sql = "SELECT iIdBanco, iCodigoBanco, vcNombreBanco FROM paf.vw_Bancos";

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);

        List<BancoDTO> bancos = new ArrayList<>();
        for (Map<String, Object> row : results) {
            BancoDTO bancoDTO = new BancoDTO();
            bancoDTO.setIdBanco((Integer) row.get("iIdBanco"));
            bancoDTO.setCodigoBanco((Integer) row.get("iCodigoBanco"));
            bancoDTO.setNombreBanco((String) row.get("vcNombreBanco"));
            bancos.add(bancoDTO);
        }

        return bancos;
    }

    @Override
public List<BancoTipoCuentaDTO> obtenerBancoTipoCuenta(int idBanco) {
    String sql = "SELECT * FROM paf.fn_ObtenerBancoTipoCuenta(?)";

    List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, idBanco);

    List<BancoTipoCuentaDTO> bancoTipoCuentas = new ArrayList<>();
    for (Map<String, Object> row : results) {
        BancoTipoCuentaDTO dto = new BancoTipoCuentaDTO();
        dto.setIdBanco((Integer) row.get("iIdBanco"));
        dto.setNombreBanco((String) row.get("vcNombreBanco"));
        dto.setIdTipoCuenta((Integer) row.get("iIdTipoCuenta"));
        dto.setNombreTipoCuenta((String) row.get("vcNombreTipoCuenta"));
        bancoTipoCuentas.add(dto);
    }

    return bancoTipoCuentas;
}

@Override
public List<FormaPagoDTO> obtenerFormasPago(int idSistema) {
    String sql = "SELECT * FROM paf.fn_ObtenerFormasPagoPorSistema(?)";

    List<Map<String, Object>> results = jdbcTemplate.queryForList(sql,idSistema);

    List<FormaPagoDTO> formasPago = new ArrayList<>();
    for (Map<String, Object> row : results) {
        FormaPagoDTO dto = new FormaPagoDTO();
        dto.setIdFormaPago((Integer) row.get("iIdFormaPago"));
        dto.setNombre((String) row.get("vcNombre"));
        formasPago.add(dto);
    }

    return formasPago;
}

public Integer obtenerIdTipoCuentaPorCodigo(int codigo) {
    String sql = "SELECT paf.fn_ObtenerIdTipoCuentaPorCodigo(?) AS IdTipoCuenta";

    Map<String, Object> result = jdbcTemplate.queryForMap(sql, codigo);
    
    return (Integer) result.get("IdTipoCuenta");
}


}