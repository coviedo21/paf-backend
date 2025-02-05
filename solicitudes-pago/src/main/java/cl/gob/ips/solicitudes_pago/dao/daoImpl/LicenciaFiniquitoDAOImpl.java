package cl.gob.ips.solicitudes_pago.dao.daoImpl;

import cl.gob.ips.solicitudes_pago.dao.LicenciaFiniquitoDAO;
import cl.gob.ips.solicitudes_pago.dto.LicenciaFiniquitoDTO;
import cl.gob.ips.solicitudes_pago.dto.LicenciaFiniquitoInputDTO;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class LicenciaFiniquitoDAOImpl implements LicenciaFiniquitoDAO {

    private final JdbcTemplate jdbcTemplate;

    public LicenciaFiniquitoDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public LicenciaFiniquitoDTO obtenerLicenciaFiniquito(int rut) {

        LicenciaFiniquitoDTO licenciaFiniquitoDTO;

        try {
            String sql =
                    "SELECT * " +
                    "FROM paf.tblLicenFiniq lf " +
                    "WHERE lf.iRutBeneficiario = ?";

            licenciaFiniquitoDTO = jdbcTemplate.queryForObject(sql, new LicensiaFiniquitoRowMapper(), rut);

            return licenciaFiniquitoDTO;
        } catch (EmptyResultDataAccessException ex) { //TODO: Ver implementacion con Optional o opcion mas elegante
            return null;
        }
    }

    @Override
    public int agregarLicenciaFiniquito(LicenciaFiniquitoInputDTO licenciaFiniquito) {
        String sql =
                "INSERT INTO dbPagosAF.paf.tblLicenFiniq (idLicFin, vTipoInformacion, iNroLicenciaFiniquito, iRutBeneficiario, vDvBneficiario, iRutEmpleado, vDvEmpleado, dFechaInicio, dFechaFin) " +
                "VALUES(?,?,?,?,?,?,?,?,?)";
        return jdbcTemplate.update(
                sql,
                licenciaFiniquito.getTipoInformacion(),
                licenciaFiniquito.getNroLicenciaFiniquito(),
                licenciaFiniquito.getRutBeneficiario(),
                licenciaFiniquito.getDvBneficiario(),
                licenciaFiniquito.getRutEmpleado(),
                licenciaFiniquito.getDvEmpleado(),
                licenciaFiniquito.getDvBneficiario(),
                licenciaFiniquito.getFechaInicio(),
                licenciaFiniquito.getFechaFin()
        );
    }

    public static class LicensiaFiniquitoRowMapper implements RowMapper<LicenciaFiniquitoDTO> {
        @Override
        public LicenciaFiniquitoDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            LicenciaFiniquitoDTO licenciaFiniquitoDTO = new LicenciaFiniquitoDTO();

            licenciaFiniquitoDTO.setIdLicFin(rs.getInt("idLicFin"));
            licenciaFiniquitoDTO.setTipoInformacion(rs.getString("vTipoInformacion"));
            licenciaFiniquitoDTO.setNroLicenciaFiniquito(rs.getInt("iNroLicenciaFiniquito"));
            licenciaFiniquitoDTO.setRutBeneficiario(rs.getInt("iRutBeneficiario"));
            licenciaFiniquitoDTO.setDvBneficiario(rs.getString("vDvBneficiario"));
            licenciaFiniquitoDTO.setRutEmpleado(rs.getInt("iRutEmpleado"));
            licenciaFiniquitoDTO.setDvEmpleado(rs.getString("vDvEmpleado"));
            licenciaFiniquitoDTO.setFechaInicio(rs.getDate("dFechaInicio"));
            licenciaFiniquitoDTO.setFechaFin(rs.getDate("dFechaFin"));

            return licenciaFiniquitoDTO;
        }
    }
}