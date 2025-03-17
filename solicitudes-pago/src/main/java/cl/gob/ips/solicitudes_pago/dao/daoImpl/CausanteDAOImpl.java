package cl.gob.ips.solicitudes_pago.dao.daoImpl;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import cl.gob.ips.solicitudes_pago.dao.CausanteDAO;
import cl.gob.ips.solicitudes_pago.dto.CausanteDTO;
import cl.gob.ips.solicitudes_pago.dto.DetalleCausanteDTO;

@Repository
public class CausanteDAOImpl implements CausanteDAO{
    
    private final JdbcTemplate jdbcTemplate;
    private final JdbcTemplate genesysPJdbc;
    
    @Value("${spring.datasource.schema}")
    private String esquema;

    @Autowired
    public CausanteDAOImpl(@Qualifier("pafJdbc") JdbcTemplate jdbcTemplate,@Qualifier("genesysPJdbc") JdbcTemplate genesysPJdbc) {
        this.jdbcTemplate = jdbcTemplate;
        this.genesysPJdbc = genesysPJdbc;
    }

    @Override
    public List<CausanteDTO> obtenerDetalleCausante(int rutBeneficiario) {

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(genesysPJdbc)
                .withProcedureName("SP_DETALLECAUSANTES")
                .declareParameters(
                        new SqlOutParameter("p_cursor", Types.REF_CURSOR),
                        new SqlParameter("RUT_BENEFICIARIO", Types.INTEGER));

        SqlParameterSource inParams = new MapSqlParameterSource()
                .addValue("RUT_BENEFICIARIO", rutBeneficiario);

        Map<String, Object> result = jdbcCall.execute(inParams);

        List<Map<String, Object>> results = (List<Map<String, Object>>) result.get("p_cursor");

        List<CausanteDTO> listaCausantes = new ArrayList<CausanteDTO>();

        // Formato de salida
        DateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
        String fechaFinal = "";

        if (results.size() > 0) {

            results.forEach(u -> {

                CausanteDTO causanteDTO = new CausanteDTO();

                if (u.get("NOMBRE_BENEFICIARIO") != null)
                    causanteDTO.setNombreBeneficiario((String) u.get("NOMBRE_BENEFICIARIO"));

                if (u.get("RUT_BENEFICIARIO") != null)
                    causanteDTO.setRutBeneficiario((String) u.get("RUT_BENEFICIARIO"));

                if (u.get("NOMBRE_CAUSANTE") != null)
                    causanteDTO.setNombreCausante((String) u.get("NOMBRE_CAUSANTE"));

                if (u.get("RUT_CAUSANTE") != null)
                    causanteDTO.setRutCausante((String) u.get("RUT_CAUSANTE"));

                if (u.get("TIPO_CAUSANTE") != null)
                    causanteDTO.setTipoCausante((String) u.get("TIPO_CAUSANTE"));

                if (u.get("ID_TIPO_CAUSANTE") != null)
                    causanteDTO.setIdTipoCausante((BigDecimal) u.get("ID_TIPO_CAUSANTE"));

                    if (u.get("NACIMIENTO_BENEFICIARIO") != null)
                    causanteDTO.setFechaNacimientoBeneficiario(formatoFecha.format((Date) u.get("NACIMIENTO_BENEFICIARIO")));

                    if (u.get("NACIMIENTO_CAUSANTE") != null)
                    causanteDTO.setFechaNacimientoCausante(formatoFecha.format((Date) u.get("NACIMIENTO_CAUSANTE")));
                
                if(calcularEdad((Date) u.get("NACIMIENTO_CAUSANTE"))>=18){
                    causanteDTO.setCausanteMayorDeEdad(true);
                }    
                listaCausantes.add(causanteDTO);
            });

            return listaCausantes;
        } else {
            return null;
        }

    }

    public static int calcularEdad(Date fechaNacimiento) {
        // Crear una instancia de Calendar y establecerla en la fecha actual
        Calendar calendarActual = Calendar.getInstance();
        calendarActual.setTime(new Date());

        // Crear una instancia de Calendar y establecerla en la fecha de nacimiento
        Calendar calendarNacimiento = Calendar.getInstance();
        calendarNacimiento.setTime(fechaNacimiento);

        // Calcular la diferencia de años entre la fecha actual y la fecha de nacimiento
        int edad = calendarActual.get(Calendar.YEAR) - calendarNacimiento.get(Calendar.YEAR);

        // Comprobar si todavía no se ha cumplido el cumpleaños de este año
        if (calendarNacimiento.get(Calendar.MONTH) > calendarActual.get(Calendar.MONTH) ||
                (calendarNacimiento.get(Calendar.MONTH) == calendarActual.get(Calendar.MONTH) &&
                        calendarNacimiento.get(Calendar.DAY_OF_MONTH) > calendarActual.get(Calendar.DAY_OF_MONTH))) {
            edad--;
        }

        // Devolver la edad calculada
        return edad;
    }

    @Override
    public String insertarDetalleCausante(DetalleCausanteDTO causanteDTO) {

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName(esquema)
                .withProcedureName("SP_InsertarDetalleCausante");

        SqlParameterSource inParams = new MapSqlParameterSource()
                .addValue("iIdCausanteSolicitud", causanteDTO.getIdCausanteSolicitud())
                .addValue("iRutBeneficiario", causanteDTO.getRutBeneficiario())
                .addValue("vcDvBeneficiario", causanteDTO.getDvBeneficiario())
                .addValue("iRutCausante", causanteDTO.getRutCausante())
                .addValue("vcDvCausante", causanteDTO.getDvCausante())
                .addValue("iPeriodo", causanteDTO.getPeriodo())
                .addValue("iTipoMovimiento", causanteDTO.getTipoMovimiento())
                .addValue("dFechaMovimiento", causanteDTO.getFechaMovimiento())
                .addValue("vcEntradaSalida", causanteDTO.getEntradaSalida())
                .addValue("nMontoMovimiento", causanteDTO.getMontoMovimiento())
                .addValue("iTipoCausante", causanteDTO.getTipoCausante())
                .addValue("iIdBeneficio", causanteDTO.getIdBeneficio())
                .addValue("nRentaPromedio", causanteDTO.getRentaPromedio())
                .addValue("iDiasReconocimiento", causanteDTO.getDiasReconocimiento())
                .addValue("iCodigoTramo", causanteDTO.getCodigoTramo())
                .addValue("dFechaFinVigencia", causanteDTO.getFechaFinVigencia())
                .addValue("dFechaInicioVigencia", causanteDTO.getFechaInicioVigencia())
                .addValue("iEstado", causanteDTO.getEstado())
                .addValue("iRutEmpleador", causanteDTO.getRutEmpleador())
                .addValue("vcDvEmpleador", causanteDTO.getDvEmpleador())
                .addValue("iNis", causanteDTO.getRutNis())
                .addValue("vcDvNis", causanteDTO.getDvNis())
                .addValue("iNumeroDocumento", causanteDTO.getNumeroDocumento())
                .addValue("vcDvDocumento", causanteDTO.getDvDocumento())
                .addValue("dFechaPago", causanteDTO.getFechaPago())
                .addValue("iIdRetencion", causanteDTO.getIdRetencion())
                .addValue("iRutReteniente", causanteDTO.getRutReteniente())
                .addValue("vcDvReteniente", causanteDTO.getDvReteniente())
                .addValue("vcNombresReteniente", causanteDTO.getNombresReteniente())
                .addValue("vcApellidoPaternoReteniente", causanteDTO.getApellidoPaternoReteniente())
                .addValue("vcApellidoMaternoReteniente", causanteDTO.getApellidoMaternoReteniente())
                .addValue("iIdFormaPagoFinal", causanteDTO.getIdFormaPagoFinal())
                .addValue("iIdBancoFinal", causanteDTO.getIdBancoFinal())
                .addValue("iIdTipoCuentaFinal", causanteDTO.getIdTipoCuentaFinal())
                .addValue("vcNumeroCuentaFinal", causanteDTO.getNumeroCuentaFinal())
                .addValue("vcArchivo", causanteDTO.getArchivo())
                .addValue("iDiasPago", causanteDTO.getDiasPago())
                .addValue("mensajeRespuesta", Types.VARCHAR);

        Map<String, Object> result = jdbcCall.execute(inParams);
        return (String) result.get("mensajeRespuesta");
    }


    @Override
    public List<DetalleCausanteDTO> obtenerDetalleCausantePorId(int iIdCausanteSolicitud) {
        String sql = "SELECT * FROM paf.fn_ObtenerDetalleCausante(?)";

        try {
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, new Object[]{iIdCausanteSolicitud});
            List<DetalleCausanteDTO> detallesCausante = new ArrayList<>();

            for (Map<String, Object> row : results) {
                DetalleCausanteDTO detalleDTO = new DetalleCausanteDTO();

                if (row.get("iIdDetalleCausante") != null) 
                    detalleDTO.setIdDetalleCausante((Integer) row.get("iIdDetalleCausante"));

                if (row.get("iIdCausanteSolicitud") != null) 
                    detalleDTO.setIdCausanteSolicitud((Integer) row.get("iIdCausanteSolicitud"));

                if (row.get("iRutCausante") != null) 
                    detalleDTO.setRutCausante((Integer) row.get("iRutCausante"));

                if (row.get("vcDvCausante") != null) 
                    detalleDTO.setDvCausante((String) row.get("vcDvCausante"));

                if (row.get("iPeriodo") != null) 
                    detalleDTO.setPeriodo((Integer) row.get("iPeriodo"));

                if (row.get("iTipoMovimiento") != null) 
                    detalleDTO.setTipoMovimiento((Integer) row.get("iTipoMovimiento"));

                if (row.get("dFechaMovimiento") != null) 
                    detalleDTO.setFechaMovimiento((Date) row.get("dFechaMovimiento"));

                if (row.get("vcEntradaSalida") != null) 
                    detalleDTO.setEntradaSalida((String) row.get("vcEntradaSalida"));

                if (row.get("nMontoMovimiento") != null) 
                    detalleDTO.setMontoMovimiento((BigDecimal) row.get("nMontoMovimiento"));

                if (row.get("iTipoCausante") != null) 
                    detalleDTO.setTipoCausante((Integer) row.get("iTipoCausante"));

                if (row.get("iIdBeneficio") != null) 
                    detalleDTO.setIdBeneficio((Integer) row.get("iIdBeneficio"));

                if (row.get("nRentaPromedio") != null) 
                    detalleDTO.setRentaPromedio((BigDecimal) row.get("nRentaPromedio"));

                if (row.get("iDiasReconocimiento") != null) 
                    detalleDTO.setDiasReconocimiento((Integer) row.get("iDiasReconocimiento"));

                if (row.get("iCodigoTramo") != null) 
                    detalleDTO.setCodigoTramo((Integer) row.get("iCodigoTramo"));

                if (row.get("dFechaFinVigencia") != null) 
                    detalleDTO.setFechaFinVigencia((Date) row.get("dFechaFinVigencia"));

                if (row.get("dFechaInicioVigencia") != null) 
                    detalleDTO.setFechaInicioVigencia((Date) row.get("dFechaInicioVigencia"));

                if (row.get("iRutEmpleador") != null) 
                    detalleDTO.setRutEmpleador((Integer) row.get("iRutEmpleador"));    

                if (row.get("vcDvEmpleador") != null) 
                    detalleDTO.setDvEmpleador((String) row.get("vcDvEmpleador"));

                if (row.get("iNis") != null) 
                    detalleDTO.setRutNis((Integer) row.get("iNis"));

                if (row.get("vcDvNis") != null) 
                    detalleDTO.setDvNis((String) row.get("vcDvNis"));

                if (row.get("iNumeroDocumento") != null) 
                    detalleDTO.setNumeroDocumento((Integer) row.get("iNumeroDocumento"));

                if (row.get("vcDvDocumento") != null) 
                    detalleDTO.setDvDocumento((String) row.get("vcDvDocumento"));

                if (row.get("dFechaPago") != null) 
                    detalleDTO.setFechaPago((Date) row.get("dFechaPago"));

                if (row.get("iIdRetencion") != null) 
                    detalleDTO.setIdRetencion((Integer) row.get("iIdRetencion"));

                if (row.get("iRutReteniente") != null) 
                    detalleDTO.setRutReteniente((Integer) row.get("iRutReteniente"));

                if (row.get("vcDvReteniente") != null) 
                    detalleDTO.setDvReteniente((String) row.get("vcDvReteniente"));

                if (row.get("vcNombresReteniente") != null) 
                    detalleDTO.setNombresReteniente((String) row.get("vcNombresReteniente"));

                if (row.get("vcApellidoPaternoReteniente") != null) 
                    detalleDTO.setApellidoPaternoReteniente((String) row.get("vcApellidoPaternoReteniente"));

                if (row.get("vcApellidoMaternoReteniente") != null) 
                    detalleDTO.setApellidoMaternoReteniente((String) row.get("vcApellidoMaternoReteniente"));

                if (row.get("iFormaPagoFinal") != null) 
                    detalleDTO.setIdFormaPagoFinal((Integer) row.get("iFormaPagoFinal"));

                if (row.get("iBancoFinal") != null) 
                    detalleDTO.setIdBancoFinal((Integer) row.get("iBancoFinal"));
                
                if (row.get("iTipoCuentaFinal") != null) 
                    detalleDTO.setIdTipoCuentaFinal((Integer) row.get("iTipoCuentaFinal"));

                if (row.get("vcNumeroCuentaFinal") != null) 
                    detalleDTO.setNumeroCuentaFinal((String) row.get("vcNumeroCuentaFinal"));

                if (row.get("vcArchivo") != null) 
                    detalleDTO.setArchivo((String) row.get("vcArchivo"));
                
                if (row.get("vcNombreEstado") != null) 
                    detalleDTO.setNombreEstado((String) row.get("vcNombreEstado"));
                
                if (row.get("iDiasPago") != null) 
                    detalleDTO.setDiasPago((Integer) row.get("iDiasPago"));
                
                if (row.get("iRutBeneficiarioPago") != null) 
                    detalleDTO.setRutBeneficiarioPago((Integer) row.get("iRutBeneficiarioPago"));
                
                if (row.get("vcDvBeneficiarioPago") != null) 
                    detalleDTO.setDvBeneficiarioPago((String) row.get("vcDvBeneficiarioPago"));

                detallesCausante.add(detalleDTO);
            }

            return detallesCausante;

        } catch (Exception e) {
            System.err.println("Error al ejecutar la consulta: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public DetalleCausanteDTO obtenerDetalleCausantePorIdDetalle(int iIdDetalleCausante) {
        String sql = "SELECT * FROM paf.fn_ObtenerDetalleCausantePorId(?)";

        try {
            Map<String, Object> row = jdbcTemplate.queryForMap(sql, iIdDetalleCausante);
            DetalleCausanteDTO detalleDTO = new DetalleCausanteDTO();

            if (row.get("iIdDetalleCausante") != null) 
                detalleDTO.setIdDetalleCausante((Integer) row.get("iIdDetalleCausante"));

            if (row.get("iIdCausanteSolicitud") != null) 
                detalleDTO.setIdCausanteSolicitud((Integer) row.get("iIdCausanteSolicitud"));

            if (row.get("iRutCausante") != null) 
                detalleDTO.setRutCausante((Integer) row.get("iRutCausante"));

            if (row.get("vcDvCausante") != null) 
                detalleDTO.setDvCausante((String) row.get("vcDvCausante"));

            if (row.get("iPeriodo") != null) 
                detalleDTO.setPeriodo((Integer) row.get("iPeriodo"));

            if (row.get("iTipoMovimiento") != null) 
                detalleDTO.setTipoMovimiento((Integer) row.get("iTipoMovimiento"));

            if (row.get("dFechaMovimiento") != null) 
                detalleDTO.setFechaMovimiento((Date) row.get("dFechaMovimiento"));

            if (row.get("vcEntradaSalida") != null) 
                detalleDTO.setEntradaSalida((String) row.get("vcEntradaSalida"));

            if (row.get("nMontoMovimiento") != null) 
                detalleDTO.setMontoMovimiento((BigDecimal) row.get("nMontoMovimiento"));

            if (row.get("iTipoCausante") != null) 
                detalleDTO.setTipoCausante((Integer) row.get("iTipoCausante"));

            if (row.get("iIdBeneficio") != null) 
                detalleDTO.setIdBeneficio((Integer) row.get("iIdBeneficio"));

            if (row.get("nRentaPromedio") != null) 
                detalleDTO.setRentaPromedio((BigDecimal) row.get("nRentaPromedio"));

            if (row.get("iDiasReconocimiento") != null) 
                detalleDTO.setDiasReconocimiento((Integer) row.get("iDiasReconocimiento"));

            if (row.get("iCodigoTramo") != null) 
                detalleDTO.setCodigoTramo((Integer) row.get("iCodigoTramo"));

            if (row.get("dFechaFinVigencia") != null) 
                detalleDTO.setFechaFinVigencia((Date) row.get("dFechaFinVigencia"));

            if (row.get("dFechaInicioVigencia") != null) 
                detalleDTO.setFechaInicioVigencia((Date) row.get("dFechaInicioVigencia"));

            if (row.get("iRutEmpleador") != null) 
                detalleDTO.setRutEmpleador((Integer) row.get("iRutEmpleador"));    

            if (row.get("vcDvEmpleador") != null) 
                detalleDTO.setDvEmpleador((String) row.get("vcDvEmpleador"));

            if (row.get("iNis") != null) 
                detalleDTO.setRutNis((Integer) row.get("iNis"));

            if (row.get("vcDvNis") != null) 
                detalleDTO.setDvNis((String) row.get("vcDvNis"));

            if (row.get("iNumeroDocumento") != null) 
                detalleDTO.setNumeroDocumento((Integer) row.get("iNumeroDocumento"));

            if (row.get("vcDvDocumento") != null) 
                detalleDTO.setDvDocumento((String) row.get("vcDvDocumento"));

            if (row.get("dFechaPago") != null) 
                detalleDTO.setFechaPago((Date) row.get("dFechaPago"));

            if (row.get("iIdRetencion") != null) 
                detalleDTO.setIdRetencion((Integer) row.get("iIdRetencion"));

            if (row.get("iRutReteniente") != null) 
                detalleDTO.setRutReteniente((Integer) row.get("iRutReteniente"));

            if (row.get("vcDvReteniente") != null) 
                detalleDTO.setDvReteniente((String) row.get("vcDvReteniente"));

            if (row.get("vcNombresReteniente") != null) 
                detalleDTO.setNombresReteniente((String) row.get("vcNombresReteniente"));

            if (row.get("vcApellidoPaternoReteniente") != null) 
                detalleDTO.setApellidoPaternoReteniente((String) row.get("vcApellidoPaternoReteniente"));

            if (row.get("vcApellidoMaternoReteniente") != null) 
                detalleDTO.setApellidoMaternoReteniente((String) row.get("vcApellidoMaternoReteniente"));

            if (row.get("iFormaPagoFinal") != null) 
                detalleDTO.setIdFormaPagoFinal((Integer) row.get("iFormaPagoFinal"));

            if (row.get("iBancoFinal") != null) 
                detalleDTO.setIdBancoFinal((Integer) row.get("iBancoFinal"));
            
            if (row.get("iTipoCuentaFinal") != null) 
                detalleDTO.setIdTipoCuentaFinal((Integer) row.get("iTipoCuentaFinal"));

            if (row.get("vcNumeroCuentaFinal") != null) 
                detalleDTO.setNumeroCuentaFinal((String) row.get("vcNumeroCuentaFinal"));

            if (row.get("vcArchivo") != null) 
                detalleDTO.setArchivo((String) row.get("vcArchivo"));

            if (row.get("vcNombreEstado") != null) 
                detalleDTO.setNombreEstado((String) row.get("vcNombreEstado"));

            if (row.get("iDiasPago") != null) 
                detalleDTO.setDiasPago((Integer) row.get("iDiasPago"));
            
            if (row.get("iRutBeneficiarioPago") != null) 
                detalleDTO.setRutBeneficiarioPago((Integer) row.get("iRutBeneficiarioPago"));
            
            if (row.get("vcDvBeneficiarioPago") != null) 
                detalleDTO.setDvBeneficiarioPago((String) row.get("vcDvBeneficiarioPago"));
            
            return detalleDTO;

        } catch (Exception e) {
            System.err.println("Error al ejecutar la consulta: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean actualizarDetalleCausante(DetalleCausanteDTO detalleCausanteDTO) {

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName(esquema)
                .withProcedureName("SP_ActualizarDetalleCausante");

        SqlParameterSource inParams = new MapSqlParameterSource()
                .addValue("iIdDetalleCausante", detalleCausanteDTO.getIdDetalleCausante())
                .addValue("iIdCausanteSolicitud", detalleCausanteDTO.getIdCausanteSolicitud(), Types.INTEGER)
                .addValue("iRutCausante", detalleCausanteDTO.getRutCausante(), Types.INTEGER)
                .addValue("vcDvCausante", detalleCausanteDTO.getDvCausante(), Types.VARCHAR)
                .addValue("iPeriodo", detalleCausanteDTO.getPeriodo(), Types.INTEGER)
                .addValue("iTipoMovimiento", detalleCausanteDTO.getTipoMovimiento(), Types.INTEGER)
                .addValue("dFechaMovimiento", detalleCausanteDTO.getFechaMovimiento(), Types.DATE)
                .addValue("vcEntradaSalida", detalleCausanteDTO.getEntradaSalida(), Types.CHAR)
                .addValue("nMontoMovimiento", detalleCausanteDTO.getMontoMovimiento(), Types.DECIMAL)
                .addValue("iTipoCausante", detalleCausanteDTO.getTipoCausante(), Types.INTEGER)
                .addValue("iIdBeneficio", detalleCausanteDTO.getIdBeneficio(), Types.INTEGER)
                .addValue("nRentaPromedio", detalleCausanteDTO.getRentaPromedio(), Types.DECIMAL)
                .addValue("iDiasReconocimiento", detalleCausanteDTO.getDiasReconocimiento(), Types.INTEGER)
                .addValue("iCodigoTramo", detalleCausanteDTO.getCodigoTramo(), Types.INTEGER)
                .addValue("dFechaFinVigencia", detalleCausanteDTO.getFechaFinVigencia(), Types.DATE)
                .addValue("dFechaInicioVigencia", detalleCausanteDTO.getFechaInicioVigencia(), Types.DATE)
                .addValue("iRutEmpleador", detalleCausanteDTO.getRutEmpleador(), Types.INTEGER)
                .addValue("vcDvEmpleador", detalleCausanteDTO.getDvEmpleador(), Types.VARCHAR)
                .addValue("iNis", detalleCausanteDTO.getRutNis(), Types.INTEGER)
                .addValue("vcDvNis", detalleCausanteDTO.getDvNis(), Types.VARCHAR)
                .addValue("iNumeroDocumento", detalleCausanteDTO.getNumeroDocumento(), Types.INTEGER)
                .addValue("vcDvNumeroDocumento", detalleCausanteDTO.getDvDocumento(), Types.VARCHAR)
                .addValue("dFechaPago", detalleCausanteDTO.getFechaPago(), Types.DATE)
                .addValue("iIdRetencion", detalleCausanteDTO.getIdRetencion(), Types.INTEGER)
                .addValue("iRutReteniente", detalleCausanteDTO.getRutReteniente(), Types.INTEGER)
                .addValue("vcDvReteniente", detalleCausanteDTO.getDvReteniente(), Types.VARCHAR)
                .addValue("vcNombresReteniente", detalleCausanteDTO.getNombresReteniente(), Types.VARCHAR)
                .addValue("vcApellidoPaternoReteniente", detalleCausanteDTO.getApellidoPaternoReteniente(), Types.VARCHAR)
                .addValue("vcApellidoMaternoReteniente", detalleCausanteDTO.getApellidoMaternoReteniente(), Types.VARCHAR)
                .addValue("iIdFormaPagoFinal", detalleCausanteDTO.getIdFormaPagoFinal(), Types.INTEGER)
                .addValue("iIdBancoFinal", detalleCausanteDTO.getIdBancoFinal(), Types.INTEGER)
                .addValue("iIdTipoCuentaFinal", detalleCausanteDTO.getIdTipoCuentaFinal())
                .addValue("vcNumeroCuentaFinal", detalleCausanteDTO.getNumeroCuentaFinal(), Types.VARCHAR)
                .addValue("vcArchivo", detalleCausanteDTO.getArchivo(), Types.VARCHAR)
                .addValue("mensajeRespuesta", Types.VARCHAR);

        Map<String, Object> result = jdbcCall.execute(inParams);
        String mensajeRespuesta = (String) result.get("mensajeRespuesta");

        return "Actualización correcta".equalsIgnoreCase(mensajeRespuesta);
    }

}