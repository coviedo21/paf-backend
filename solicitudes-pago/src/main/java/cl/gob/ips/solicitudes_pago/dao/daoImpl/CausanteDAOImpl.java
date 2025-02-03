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

    @Autowired
    public CausanteDAOImpl(@Qualifier("pafJdbc") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<CausanteDTO> obtenerDetalleCausante(int rutBeneficiario) {

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
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
                .withProcedureName("SP_InsertarDetalleCausante");

        SqlParameterSource inParams = new MapSqlParameterSource()
                .addValue("iIdCausanteSolicitud", causanteDTO.getIdCausanteSolicitud())
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
                .addValue("vcTramo", causanteDTO.getTramo())
                .addValue("iDiasReconocimiento", causanteDTO.getDiasReconocimiento())
                .addValue("iCodigoTramo", causanteDTO.getCodigoTramo())
                .addValue("dFechaFinVigencia", causanteDTO.getFechaFinVigencia())
                .addValue("dFechaInicioVigencia", causanteDTO.getFechaInicioVigencia())
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

                if (row.get("vcTramo") != null) 
                    detalleDTO.setTramo((String) row.get("vcTramo"));

                if (row.get("iDiasReconocimiento") != null) 
                    detalleDTO.setDiasReconocimiento((Integer) row.get("iDiasReconocimiento"));

                if (row.get("iCodigoTramo") != null) 
                    detalleDTO.setCodigoTramo((Integer) row.get("iCodigoTramo"));

                if (row.get("dFechaFinVigencia") != null) 
                    detalleDTO.setFechaFinVigencia((Date) row.get("dFechaFinVigencia"));

                if (row.get("dFechaInicioVigencia") != null) 
                    detalleDTO.setFechaInicioVigencia((Date) row.get("dFechaInicioVigencia"));

                detallesCausante.add(detalleDTO);
            }

            return detallesCausante;

        } catch (Exception e) {
            System.err.println("Error al ejecutar la consulta: " + e.getMessage());
            return new ArrayList<>();
        }
    }


}
