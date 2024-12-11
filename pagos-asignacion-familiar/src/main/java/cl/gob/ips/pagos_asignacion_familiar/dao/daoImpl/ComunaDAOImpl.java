package cl.gob.ips.pagos_asignacion_familiar.dao.daoImpl;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import cl.gob.ips.pagos_asignacion_familiar.dao.ComunaDAO;
import cl.gob.ips.pagos_asignacion_familiar.dto.ListaComuna;
import cl.gob.ips.pagos_asignacion_familiar.dto.RegionDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.ResponseDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.ResultadoRegionDTO;

@Repository
public class ComunaDAOImpl implements ComunaDAO{
    private final JdbcTemplate jdbcTemplate;
    

    @Autowired
    public ComunaDAOImpl(@Qualifier("pafJdbc") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ListaComuna obtenerIdComunaPorNombre(String nombreComuna){
        String sql = "SELECT idComuna,vcNombre FROM paf.fn_ObtenerComunaPorNombre(?)";
        ListaComuna comuna = new ListaComuna();
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, new Object[]{nombreComuna});
        //List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, inParams);

        if (!results.isEmpty()) {
            Map<String, Object> u = results.get(0); // Solo tomamos el primer registro
            if (u.get("idComuna") != null) {
                comuna.setCodComuna((Integer) u.get("idComuna"));
                comuna.setNombreComuna((String) u.get("vcNombre"));
                return comuna;
            }
        }

        return null;
    }

    public int insertarComunaMantenedor(String comunaIngresada, Integer idComunaOriginal, String nombreComunaOriginal, String esCoincidencia) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("paf")  // Asumiendo que el esquema es 'paf', cámbialo si es necesario
                .withProcedureName("SP_InsertarComunaMantenedor")
                .declareParameters(
                        new SqlParameter("ComunaIngresada", Types.VARCHAR),
                        new SqlParameter("IdComunaOriginal", Types.INTEGER),
                        new SqlParameter("NombreComunaOriginal", Types.VARCHAR),
                        new SqlParameter("EsCoincidencia", Types.CHAR),
                        new SqlOutParameter("ReturnIdComunaOriginal", Types.INTEGER)
                );

        MapSqlParameterSource inParams = new MapSqlParameterSource()
                .addValue("ComunaIngresada", comunaIngresada)
                .addValue("IdComunaOriginal", idComunaOriginal)
                .addValue("NombreComunaOriginal", nombreComunaOriginal)
                .addValue("EsCoincidencia", esCoincidencia);

        Map<String, Object> result = jdbcCall.execute(inParams);
        return result.get("ReturnIdComunaOriginal")!=null?(Integer) result.get("ReturnIdComunaOriginal"):0;
    }

    public ResultadoRegionDTO obtenerIdRegionPorNombre(String nombreRegion){
        String sql = "SELECT idRegion,vcNombre FROM paf.fn_ObtenerRegionPorNombre(?)";
        ResultadoRegionDTO region = new ResultadoRegionDTO();
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, new Object[]{nombreRegion});
        //List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, inParams);

        if (!results.isEmpty()) {
            Map<String, Object> u = results.get(0); // Solo tomamos el primer registro
            if (u.get("idRegion") != null) {
                region.setCodigoRegion((Integer) u.get("idRegion"));
                region.setNombreRegion((String) u.get("vcNombre"));
                return region;
            }
        }

        return null;
    }

    public int insertarRegionMantenedor(String regionIngresada, Integer idRegionOriginal, String nombreRegionOriginal, String esCoincidencia) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("paf")  // Asumiendo que el esquema es 'paf', cámbialo si es necesario
                .withProcedureName("SP_InsertarRegionMantenedor")
                .declareParameters(
                        new SqlParameter("RegionIngresada", Types.VARCHAR),
                        new SqlParameter("IdRegionOriginal", Types.INTEGER),
                        new SqlParameter("NombreRegionOriginal", Types.VARCHAR),
                        new SqlParameter("EsCoincidencia", Types.CHAR),
                        new SqlOutParameter("ReturnIdRegionOriginal", Types.INTEGER)
                );

        MapSqlParameterSource inParams = new MapSqlParameterSource()
                .addValue("RegionIngresada", regionIngresada)
                .addValue("IdRegionOriginal", idRegionOriginal)
                .addValue("NombreRegionOriginal", nombreRegionOriginal)
                .addValue("EsCoincidencia", esCoincidencia);

        Map<String, Object> result = jdbcCall.execute(inParams);
        return result.get("ReturnIdRegionOriginal")!=null?(Integer) result.get("ReturnIdRegionOriginal"):0;
    }
}