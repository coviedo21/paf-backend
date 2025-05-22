package cl.gob.ips.solicitudes_pago.dao.daoImpl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.support.SqlValue;
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

import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import com.microsoft.sqlserver.jdbc.SQLServerPreparedStatement;

import cl.gob.ips.solicitudes_pago.dao.CausanteDAO;
import cl.gob.ips.solicitudes_pago.dto.CausanteDTO;
import cl.gob.ips.solicitudes_pago.dto.DetalleCausanteDTO;

@Repository
public class CausanteDAOImpl implements CausanteDAO{
    
    private final JdbcTemplate jdbcTemplate;
    private final JdbcTemplate genesysPJdbc;
    private final JdbcTemplate coredJdbc;
    
    @Value("${spring.datasource.schema}")
    private String esquema;

    @Autowired
    public CausanteDAOImpl(@Qualifier("pafJdbc") JdbcTemplate jdbcTemplate,@Qualifier("genesysPJdbc") JdbcTemplate genesysPJdbc,@Qualifier("coredJdbc") JdbcTemplate coredJdbc) {
        this.jdbcTemplate = jdbcTemplate;
        this.genesysPJdbc = genesysPJdbc;
        this.coredJdbc = coredJdbc;
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
    public String insertarDetalleCausanteMasivo(List<DetalleCausanteDTO> lista) {
        try {
            SQLServerDataTable tvp = new SQLServerDataTable();

            tvp.addColumnMetadata("iIdCausanteSolicitud", Types.INTEGER);
            tvp.addColumnMetadata("iRutBeneficiario", Types.INTEGER);
            tvp.addColumnMetadata("vcDvBeneficiario", Types.VARCHAR);
            tvp.addColumnMetadata("iRutCausante", Types.INTEGER);
            tvp.addColumnMetadata("vcDvCausante", Types.VARCHAR);
            tvp.addColumnMetadata("iPeriodo", Types.INTEGER);
            tvp.addColumnMetadata("iTipoMovimiento", Types.INTEGER);
            tvp.addColumnMetadata("dFechaMovimiento", Types.DATE);
            tvp.addColumnMetadata("vcEntradaSalida", Types.CHAR);
            tvp.addColumnMetadata("nMontoMovimiento", Types.DECIMAL);
            tvp.addColumnMetadata("iTipoCausante", Types.INTEGER);
            tvp.addColumnMetadata("iIdBeneficio", Types.INTEGER);
            tvp.addColumnMetadata("nRentaPromedio", Types.DECIMAL);
            tvp.addColumnMetadata("iDiasReconocimiento", Types.INTEGER);
            tvp.addColumnMetadata("iCodigoTramo", Types.INTEGER);
            tvp.addColumnMetadata("dFechaFinVigencia", Types.DATE);
            tvp.addColumnMetadata("dFechaInicioVigencia", Types.DATE);
            tvp.addColumnMetadata("iEstado", Types.INTEGER);
            tvp.addColumnMetadata("iRutEmpleador", Types.INTEGER);
            tvp.addColumnMetadata("vcDvEmpleador", Types.VARCHAR);
            tvp.addColumnMetadata("iNis", Types.VARCHAR);
            tvp.addColumnMetadata("vcDvNis", Types.VARCHAR);
            tvp.addColumnMetadata("iNumeroDocumento", Types.INTEGER);
            tvp.addColumnMetadata("vcDvNumeroDocumento", Types.VARCHAR);
            tvp.addColumnMetadata("dFechaPago", Types.DATE);
            tvp.addColumnMetadata("iIdRetencion", Types.INTEGER);
            tvp.addColumnMetadata("iRutReteniente", Types.INTEGER);
            tvp.addColumnMetadata("vcDvReteniente", Types.VARCHAR);
            tvp.addColumnMetadata("vcNombresReteniente", Types.VARCHAR);
            tvp.addColumnMetadata("vcApellidoPaternoReteniente", Types.VARCHAR);
            tvp.addColumnMetadata("vcApellidoMaternoReteniente", Types.VARCHAR);
            tvp.addColumnMetadata("iFormaPagoFinal", Types.INTEGER);
            tvp.addColumnMetadata("iBancoFinal", Types.INTEGER);
            tvp.addColumnMetadata("vcNumeroCuentaFinal", Types.VARCHAR);
            tvp.addColumnMetadata("iTipoCuentaFinal", Types.INTEGER);
            tvp.addColumnMetadata("vcArchivo", Types.VARCHAR);
            tvp.addColumnMetadata("iDiasPago", Types.INTEGER);

            for (DetalleCausanteDTO d : lista) {
            	System.out.println("iIdCausanteSolicitud: " + d.getIdCausanteSolicitud());
            	System.out.println("iRutBeneficiario: " + d.getRutBeneficiario());
            	System.out.println("vcDvBeneficiario: " + d.getDvBeneficiario());
            	System.out.println("iRutCausante: " + d.getRutCausante());
            	System.out.println("vcDvCausante: " + d.getDvCausante());
            	System.out.println("iPeriodo: " + d.getPeriodo());
            	System.out.println("iTipoMovimiento: " + d.getTipoMovimiento());
            	System.out.println("dFechaMovimiento: " + d.getFechaMovimiento());
            	System.out.println("vcEntradaSalida: " + d.getEntradaSalida());
            	System.out.println("nMontoMovimiento (diferencia): " + d.getDiferencia());
            	System.out.println("iTipoCausante: " + d.getTipoCausante());
            	System.out.println("iIdBeneficio: " + d.getIdBeneficio());
            	System.out.println("nRentaPromedio: " + d.getRentaPromedio());
            	System.out.println("iDiasReconocimiento: " + d.getDiasReconocimiento());
            	System.out.println("iCodigoTramo: " + d.getCodigoTramo());
            	System.out.println("dFechaFinVigencia: " + d.getFechaFinVigencia());
            	System.out.println("dFechaInicioVigencia: " + d.getFechaInicioVigencia());
            	System.out.println("iEstado: " + d.getEstado());
            	System.out.println("iRutEmpleador: " + d.getRutEmpleador());
            	System.out.println("vcDvEmpleador: " + d.getDvEmpleador());
            	System.out.println("iNis: " + d.getRutNis());
            	System.out.println("vcDvNis: " + d.getDvNis());
            	System.out.println("iNumeroDocumento: " + d.getNumeroDocumento());
            	System.out.println("vcDvNumeroDocumento: " + d.getDvDocumento());
            	System.out.println("dFechaPago: " + d.getFechaPago());
            	System.out.println("iIdRetencion: " + d.getIdRetencion());
            	System.out.println("iRutReteniente: " + d.getRutReteniente());
            	System.out.println("vcDvReteniente: " + d.getDvReteniente());
            	System.out.println("vcNombresReteniente: " + d.getNombresReteniente());
            	System.out.println("vcApellidoPaternoReteniente: " + d.getApellidoPaternoReteniente());
            	System.out.println("vcApellidoMaternoReteniente: " + d.getApellidoMaternoReteniente());
            	System.out.println("iFormaPagoFinal: " + d.getIdFormaPagoFinal());
            	System.out.println("iBancoFinal: " + d.getIdBancoFinal());
            	System.out.println("vcNumeroCuentaFinal: " + d.getNumeroCuentaFinal());
            	System.out.println("iTipoCuentaFinal: " + d.getIdTipoCuentaFinal());
            	System.out.println("vcArchivo: " + d.getArchivo());
            	System.out.println("iDiasPago: " + d.getDiasPago());

            	
                tvp.addRow(
                    d.getIdCausanteSolicitud(),
                    d.getRutBeneficiario(),
                    d.getDvBeneficiario(),
                    d.getRutCausante(),
                    d.getDvCausante(),
                    d.getPeriodo(),
                    d.getTipoMovimiento(),
                    d.getFechaMovimiento(),
                    d.getEntradaSalida(),
                    d.getDiferencia(),
                    d.getTipoCausante(),
                    d.getIdBeneficio(),
                    d.getRentaPromedio(),
                    d.getDiasReconocimiento(),
                    d.getCodigoTramo(),
                    d.getFechaFinVigencia(),
                    d.getFechaInicioVigencia(),
                    d.getEstado(),
                    d.getRutEmpleador(),
                    d.getDvEmpleador(),
                    d.getRutNis(),
                    d.getDvNis(),
                    d.getNumeroDocumento(),
                    d.getDvDocumento(), // Asegúrate que este sea vcDvNumeroDocumento en la DTO
                    d.getFechaPago(),
                    d.getIdRetencion(),
                    d.getRutReteniente(),
                    d.getDvReteniente(),
                    d.getNombresReteniente(),
                    d.getApellidoPaternoReteniente(),
                    d.getApellidoMaternoReteniente(),
                    d.getIdFormaPagoFinal(), // ← iFormaPagoFinal
                    d.getIdBancoFinal(),
                    d.getNumeroCuentaFinal(),
                    d.getIdTipoCuentaFinal(),
                    d.getArchivo(),
                    d.getDiasPago()
                );
            }

            SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName(esquema)
                .withProcedureName("SP_InsertarDetalleCausante_Masivo")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                    new SqlParameter("detalles", Types.STRUCT),
                    new SqlOutParameter("mensajeRespuesta", Types.VARCHAR)
                );

            MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("detalles", new SqlValue() {
                    @Override
                    public void setValue(PreparedStatement ps, int paramIndex) throws SQLException {
                        ((SQLServerPreparedStatement) ps).setStructured(paramIndex, "paf.TVP_DetalleCausanteV3", tvp);
                    }

                    @Override
                    public void cleanup() {}
                });

            Map<String, Object> result = call.execute(param);
            return (String) result.get("mensajeRespuesta");

        } catch (Exception e) {
            System.err.println("Error al insertar detalle causante masivo: " + e.getMessage());
            return "Error al insertar detalle causante masivo: " + e.getMessage();
        }
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
                .addValue("nMontoMovimiento", causanteDTO.getDiferencia())
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
        String sql = "SELECT * FROM " + esquema + ".fn_ObtenerDetalleCausante(?)";

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
                    detalleDTO.setRutNis((String) row.get("iNis"));

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

                if (row.get("iIdFormaPagoRetenedor") != null) 
                    detalleDTO.setIdFormaPagoRetenedor((Integer) row.get("iIdFormaPagoRetenedor"));

                if (row.get("iBancoRetenedor") != null) 
                    detalleDTO.setIdBancoRetenedor((Integer) row.get("iBancoRetenedor"));
                
                if (row.get("iTipoCuentaRetenedor") != null) 
                    detalleDTO.setIdTipoCuentaRetenedor((Integer) row.get("iTipoCuentaRetenedor"));

                if (row.get("vcNumeroCuentaRetenedor") != null) 
                    detalleDTO.setNumeroCuentaRetenedor((String) row.get("vcNumeroCuentaRetenedor"));

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
                
                if (row.get("estado") != null) 
                    detalleDTO.setEstado((Integer) row.get("estado"));
                
                if (row.get("iDiasPago") != null) 
                    detalleDTO.setDiasPago((Integer) row.get("iDiasPago"));
                
                if (row.get("iRutBeneficiarioPago") != null) 
                    detalleDTO.setRutBeneficiarioPago((Integer) row.get("iRutBeneficiarioPago"));
                
                if (row.get("vcDvBeneficiarioPago") != null) 
                    detalleDTO.setDvBeneficiarioPago((String) row.get("vcDvBeneficiarioPago"));
                
                if (row.get("nTotalPago") != null) 
                    detalleDTO.setTotalPago((BigDecimal) row.get("nTotalPago"));
                
                if (row.get("nombreFormaPagoRetenedor") != null) 
                    detalleDTO.setGlosaFormaPagoRetenedor((String) row.get("nombreFormaPagoRetenedor"));
                
                if (row.get("nombreFormaPagoFinal") != null) 
                    detalleDTO.setGlosaFormaPagoFinal((String) row.get("nombreFormaPagoFinal"));
                
                if (row.get("nombreBancoRetenedor") != null) 
                    detalleDTO.setGlosaBancoRetenedor((String) row.get("nombreBancoRetenedor"));
                
                if (row.get("nombreBancoFinal") != null) 
                    detalleDTO.setGlosaBancoFinal((String) row.get("nombreBancoFinal"));
                
                if (row.get("nombreTipoCuentaRetenedor") != null) 
                    detalleDTO.setGlosaTipoCuentaRetenedor((String) row.get("nombreTipoCuentaRetenedor"));
                
                if (row.get("nombreTipoCuentaFinal") != null) 
                    detalleDTO.setGlosaTipoCuentaFinal((String) row.get("nombreTipoCuentaFinal"));
                
                if (row.get("idProcesoPago") != null) 
                    detalleDTO.setIdProcesoPago((Integer) row.get("idProcesoPago"));
                
                if (row.get("rutBeneficiario") != null) 
                    detalleDTO.setRutBeneficiario((Integer) row.get("rutBeneficiario"));

                if (row.get("dvBeneficiario") != null) 
                    detalleDTO.setDvBeneficiario((String) row.get("dvBeneficiario"));

                if (row.get("valorTramo") != null) 
                    detalleDTO.setValorTramo30((Integer) row.get("valorTramo"));
                
                if (row.get("diasTrabajados") != null) 
                    detalleDTO.setDiasTrabajados((Integer) row.get("diasTrabajados"));
                
                if (row.get("diasPagados") != null) 
                    detalleDTO.setDiasPagados((Integer) row.get("diasPagados"));

                detalleDTO.setDiasPorPagar((detalleDTO.getDiasReconocimiento()-detalleDTO.getDiasTrabajados())>0?detalleDTO.getDiasReconocimiento()-detalleDTO.getDiasTrabajados():0);
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
        String sql = "SELECT * FROM " + esquema + ".fn_ObtenerDetalleCausantePorId(?)";

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
                detalleDTO.setRutNis((String) row.get("iNis"));

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

            if (row.get("iIdFormaPagoRetenedor") != null) 
                detalleDTO.setIdFormaPagoRetenedor((Integer) row.get("iIdFormaPagoRetenedor"));

            if (row.get("iBancoRetenedor") != null) 
                detalleDTO.setIdBancoRetenedor((Integer) row.get("iBancoRetenedor"));
            
            if (row.get("iTipoCuentaRetenedor") != null) 
                detalleDTO.setIdTipoCuentaRetenedor((Integer) row.get("iTipoCuentaRetenedor"));

            if (row.get("vcNumeroCuentaRetenedor") != null) 
                detalleDTO.setNumeroCuentaRetenedor((String) row.get("vcNumeroCuentaRetenedor"));

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
            
            if (row.get("nTotalPago") != null) 
                detalleDTO.setTotalPago((BigDecimal) row.get("nTotalPago"));
            
            if (row.get("nombreFormaPagoRetenedor") != null) 
                detalleDTO.setGlosaFormaPagoRetenedor((String) row.get("nombreFormaPagoRetenedor"));
            
            if (row.get("nombreFormaPagoFinal") != null) 
                detalleDTO.setGlosaFormaPagoFinal((String) row.get("nombreFormaPagoFinal"));
            
            if (row.get("nombreBancoRetenedor") != null) 
                detalleDTO.setGlosaBancoRetenedor((String) row.get("nombreBancoRetenedor"));
            
            if (row.get("nombreBancoFinal") != null) 
                detalleDTO.setGlosaBancoFinal((String) row.get("nombreBancoFinal"));
            
            if (row.get("nombreTipoCuentaRetenedor") != null) 
                detalleDTO.setGlosaTipoCuentaRetenedor((String) row.get("nombreTipoCuentaRetenedor"));
            
            if (row.get("nombreTipoCuentaFinal") != null) 
                detalleDTO.setGlosaTipoCuentaFinal((String) row.get("nombreTipoCuentaFinal"));

            if (row.get("idProcesoPago") != null) 
                detalleDTO.setIdProcesoPago((Integer) row.get("idProcesoPago"));
            
            if (row.get("estado") != null) 
                detalleDTO.setEstado((Integer) row.get("estado"));
            
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
                .addValue("iNis", detalleCausanteDTO.getRutNis(), Types.VARCHAR)
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
                .addValue("iIdFormaPagoRetenedor", detalleCausanteDTO.getIdFormaPagoRetenedor(), Types.INTEGER)
                .addValue("iIdBancoRetenedor", detalleCausanteDTO.getIdBancoRetenedor(), Types.INTEGER)
                .addValue("iIdTipoCuentaRetenedor", detalleCausanteDTO.getIdTipoCuentaRetenedor())
                .addValue("vcNumeroCuentaRetenedor", detalleCausanteDTO.getNumeroCuentaRetenedor(), Types.VARCHAR)
                .addValue("iIdFormaPagoFinal", detalleCausanteDTO.getIdFormaPagoFinal(), Types.INTEGER)
                .addValue("iIdBancoFinal", detalleCausanteDTO.getIdBancoFinal(), Types.INTEGER)
                .addValue("iIdTipoCuentaFinal", detalleCausanteDTO.getIdTipoCuentaFinal())
                .addValue("vcNumeroCuentaFinal", detalleCausanteDTO.getNumeroCuentaFinal(), Types.VARCHAR)
                .addValue("vcArchivo", detalleCausanteDTO.getArchivo(), Types.VARCHAR)
                .addValue("iEstado", detalleCausanteDTO.getEstado(), Types.VARCHAR)
                .addValue("iDiasPago", detalleCausanteDTO.getDiasPago(), Types.INTEGER)
                .addValue("nTotalPago", detalleCausanteDTO.getTotalPago(), Types.DECIMAL)
                .addValue("mensajeRespuesta", Types.VARCHAR);

        Map<String, Object> result = jdbcCall.execute(inParams);
        String mensajeRespuesta = (String) result.get("mensajeRespuesta");

        return "Actualización correcta".equalsIgnoreCase(mensajeRespuesta);
    }
    
    @Override
    public int obtenerDiasCotizacion(int rutBeneficiario, int rutEmpleador, String periodo) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(coredJdbc)
                .withProcedureName("PRC_WS_DIAS_TRABAJADOS")
                .withSchemaName("INPAPP")
                .declareParameters(
                    new SqlParameter("P_RUT_TR", Types.INTEGER),
                    new SqlParameter("P_RUT_EM", Types.INTEGER),
                    new SqlParameter("P_PERIODO", Types.INTEGER),
                    new SqlOutParameter("P_DIAS_TRABAJADOS", Types.INTEGER),
                    new SqlOutParameter("RESPUESTA", Types.INTEGER),
                    new SqlOutParameter("MENSAJE", Types.VARCHAR),
                    new SqlOutParameter("CODERROR", Types.INTEGER),
                    new SqlOutParameter("DETALLEERROR", Types.VARCHAR),
                    new SqlOutParameter("FECHAERROR", Types.VARCHAR)
                );

        int periodoInt = Integer.parseInt(periodo); // "202403" -> 202403

        SqlParameterSource inParams = new MapSqlParameterSource()
                .addValue("P_RUT_TR", rutBeneficiario)
                .addValue("P_RUT_EM", rutEmpleador)
                .addValue("P_PERIODO", periodoInt);

        Map<String, Object> out = jdbcCall.execute(inParams);

        // Validar respuesta
        Integer respuesta = (Integer) out.get("RESPUESTA");
        if (respuesta != null && respuesta == 1) {
            return (Integer) out.get("P_DIAS_TRABAJADOS");
        } else {
            // Podrías logear el error o lanzar una excepción si lo deseas
            return 0;
        }
    }

}