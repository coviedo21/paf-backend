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

@Repository
public class CausanteDAOImpl implements CausanteDAO{
    
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CausanteDAOImpl(@Qualifier("genesysPJdbc") JdbcTemplate jdbcTemplate) {
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
}
