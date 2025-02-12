package cl.gob.ips.solicitudes_pago.dao.daoImpl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import cl.gob.ips.solicitudes_pago.dao.LicenciaFiniquitoDAO;
import cl.gob.ips.solicitudes_pago.dto.LicenciaFiniquitoDTO;
import cl.gob.ips.solicitudes_pago.dto.LicenciaFiniquitoInputDTO;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Repository
public class LicenciaFiniquitoDAOImpl implements LicenciaFiniquitoDAO {

    @Value("${spring.datasource.schema}")
    private String esquema;

    private final JdbcTemplate jdbcTemplate;

    public LicenciaFiniquitoDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<LicenciaFiniquitoDTO> obtenerLicenciaFiniquito(int rutBeneficiario, String nroLicencia, String fechaInicio, String fechaFin) {
        try {
            String sql = "SELECT * FROM " + esquema + ".fn_ObtenerLicenciaFiniquito(?, ?, ?, ?)";

            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, new Object[]{rutBeneficiario == 0 ? null : rutBeneficiario, nroLicencia, fechaInicio, fechaFin});

            List<LicenciaFiniquitoDTO> licenciaFiniquitoDTOS = new ArrayList<>();
            for (Map<String, Object> row : results) {
                LicenciaFiniquitoDTO licenciaFiniquitoDTO = new LicenciaFiniquitoDTO();
                if (row.get("idLicFin") != null) licenciaFiniquitoDTO.setIdLicFin((Integer) row.get("idLicFin"));
                if (row.get("iTipo") != null) licenciaFiniquitoDTO.setTipo((Integer) row.get("iTipo"));
                if (row.get("vNroLicencia") != null) licenciaFiniquitoDTO.setNroLicencia((String) row.get("vNroLicencia"));
                if (row.get("iRutBeneficiario") != null) licenciaFiniquitoDTO.setRutBeneficiario((Integer) row.get("iRutBeneficiario"));
                if (row.get("vDvBeneficiario") != null) licenciaFiniquitoDTO.setDvBeneficiario((String) row.get("vDvBeneficiario"));
                if (row.get("iRutEmpleado") != null) licenciaFiniquitoDTO.setRutEmpleado((Integer) row.get("iRutEmpleado"));
                if (row.get("vDvEmpleado") != null) licenciaFiniquitoDTO.setDvEmpleado((String) row.get("vDvEmpleado"));
                if (row.get("dFechaInicio") != null) licenciaFiniquitoDTO.setFechaInicio((Date) row.get("dFechaInicio"));
                if (row.get("dFechaFin") != null) licenciaFiniquitoDTO.setFechaFin((Date) row.get("dFechaFin"));
                if (row.get("iMonto") != null) licenciaFiniquitoDTO.setMonto((Integer) row.get("iMonto"));
                licenciaFiniquitoDTOS.add(licenciaFiniquitoDTO);
            }
            return licenciaFiniquitoDTOS;
        } catch (EmptyResultDataAccessException ex) {
            log.error(ex.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public HashMap<String, String> agregarLicenciaFiniquito(LicenciaFiniquitoInputDTO licenciaFiniquito) {

        HashMap<String, String> respuesta = new HashMap<>();

        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withSchemaName(esquema)
                    .withProcedureName("sp_InsertarLicenFiniq")
                    .declareParameters(
                            new SqlParameter("iTipo", Types.INTEGER),
                            new SqlParameter("vNroLicencia", Types.VARCHAR),
                            new SqlParameter("iRutBeneficiario", Types.INTEGER),
                            new SqlParameter("vDvBeneficiario", Types.VARCHAR),
                            new SqlParameter("iRutEmpleado", Types.INTEGER),
                            new SqlParameter("vDvEmpleado", Types.VARCHAR),
                            new SqlParameter("dFechaInicio", Types.DATE),
                            new SqlParameter("dFechaFin", Types.DATE),
                            new SqlParameter("iMonto", Types.INTEGER),
                            new SqlOutParameter("idLicFin", Types.INTEGER)
                    );

            MapSqlParameterSource inParams = new MapSqlParameterSource()
                    .addValue("iTipo", licenciaFiniquito.getTipo())
                    .addValue("vNroLicencia", licenciaFiniquito.getNroLicencia())
                    .addValue("iRutBeneficiario", licenciaFiniquito.getRutBeneficiario())
                    .addValue("vDvBeneficiario", licenciaFiniquito.getDvBeneficiario())
                    .addValue("iRutEmpleado", licenciaFiniquito.getRutEmpleado())
                    .addValue("vDvEmpleado", licenciaFiniquito.getDvEmpleado())
                    .addValue("dFechaInicio", licenciaFiniquito.getFechaInicio())
                    .addValue("dFechaFin", licenciaFiniquito.getFechaFin())
                    .addValue("iMonto", licenciaFiniquito.getMonto());

            Map<String, Object> result = jdbcCall.execute(inParams);

            respuesta.put("Estado", "OK");
            respuesta.put("Mensaje", "Se a creado el registro con el id: " + result.get("idLicFin"));

        } catch (DataIntegrityViolationException ex) {
            log.error(ex.getMessage());
            respuesta.put("Estado", "NOK");
            respuesta.put("Mensaje", "Error al insertar la licencia o finiquito: " + ex.getLocalizedMessage());
        } catch (UncategorizedSQLException ex) {
            log.error(ex.getMessage());
            respuesta.put("Estado", "NOK");
            respuesta.put("Mensaje", "Error al insertar la licencia o finiquito: " + ex.getSQLException());
        }
        return respuesta;
    }

    public static class LicenciaFiniquitoRowMapper implements RowMapper<LicenciaFiniquitoDTO> {
        @Override
        public LicenciaFiniquitoDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            LicenciaFiniquitoDTO licenciaFiniquitoDTO = new LicenciaFiniquitoDTO();

            licenciaFiniquitoDTO.setIdLicFin(rs.getInt("idLicFin"));
            licenciaFiniquitoDTO.setTipo(rs.getInt("iTipo"));
            licenciaFiniquitoDTO.setNroLicencia(rs.getString("vNroLicencia"));
            licenciaFiniquitoDTO.setRutBeneficiario(rs.getInt("iRutBeneficiario"));
            licenciaFiniquitoDTO.setDvBeneficiario(rs.getString("vDvBeneficiario"));
            licenciaFiniquitoDTO.setRutEmpleado(rs.getInt("iRutEmpleado"));
            licenciaFiniquitoDTO.setDvEmpleado(rs.getString("vDvEmpleado"));
            licenciaFiniquitoDTO.setFechaInicio(rs.getDate("dFechaInicio"));
            licenciaFiniquitoDTO.setFechaFin(rs.getDate("dFechaFin"));

            return licenciaFiniquitoDTO;
        }
    }
}