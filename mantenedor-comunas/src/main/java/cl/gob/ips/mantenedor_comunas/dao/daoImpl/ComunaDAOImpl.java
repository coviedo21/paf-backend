package cl.gob.ips.mantenedor_comunas.dao.daoImpl;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import cl.gob.ips.mantenedor_comunas.dao.ComunaDAO;
import cl.gob.ips.mantenedor_comunas.dto.ComunaMantenedorDTO;
import cl.gob.ips.mantenedor_comunas.dto.ListaComunaDTO;
import cl.gob.ips.mantenedor_comunas.dto.ResultadoRegionDTO;

@Repository
public class ComunaDAOImpl implements ComunaDAO{
    private final JdbcTemplate jdbcTemplate;
    

    @Autowired
    public ComunaDAOImpl(@Qualifier("pafJdbc") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ListaComunaDTO obtenerIdComunaPorNombre(String nombreComuna){
        String sql = "SELECT idComuna,vcNombre FROM paf.fn_ObtenerComunaPorNombre(?)";
        ListaComunaDTO comuna = new ListaComunaDTO();
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

    public int insertarComunaMantenedor(ComunaMantenedorDTO comuna) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withSchemaName("paf")  // Asumiendo que el esquema es 'paf', cámbialo si es necesario
                    .withProcedureName("SP_InsertarComunaMantenedor")
                    .declareParameters(
                            new SqlParameter("ComunaIngresada", Types.VARCHAR),
                            new SqlParameter("IdComunaOriginal", Types.INTEGER),
                            new SqlParameter("NombreComunaOriginal", Types.VARCHAR),
                            new SqlParameter("EsCoincidencia", Types.CHAR),
                            new SqlOutParameter("ReturnIdComunaMantenedor", Types.INTEGER)
                    );

            MapSqlParameterSource inParams = new MapSqlParameterSource()
                    .addValue("ComunaIngresada", comuna.getComunaIngresada())
                    .addValue("IdComunaOriginal", comuna.getIdComunaOriginal())
                    .addValue("NombreComunaOriginal", comuna.getNombreComunaOriginal())
                    .addValue("EsCoincidencia", comuna.getEsCoincidencia());

            Map<String, Object> result = jdbcCall.execute(inParams);
            return result.get("ReturnIdComunaMantenedor") != null ? (Integer) result.get("ReturnIdComunaMantenedor") : 0;

        } catch (DataAccessException e) {
            // Manejo específico de excepciones relacionadas con la base de datos
            System.err.println("Error al ejecutar el procedimiento almacenado: " + e.getMessage());
            return 0; // Puedes retornar un valor específico o manejar el error de otra manera
        } catch (Exception e) {
            // Manejo de cualquier otra excepción
            System.err.println("Error inesperado: " + e.getMessage());
            return 0;
        }
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

    // Método parComunaMantenedorDTOa obtener comunas basado en iIdCoincidencia
    public ComunaMantenedorDTO obtenerComunaPorIdCoincidencia(Integer iIdCoincidencia) {
        String sql = "SELECT iIdCoincidencia, vcComunaIngresada, iIdComunaOriginal, vcNombreComunaOriginal, cEsCoincidencia " +
                     "FROM paf.fn_ObtenerComunaMantenedor(?)";
        ComunaMantenedorDTO comuna = new ComunaMantenedorDTO();
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, new Object[]{iIdCoincidencia});

        if (!results.isEmpty()) {
            Map<String, Object> u = results.get(0); // Solo tomamos el primer registro
            if (u.get("iIdCoincidencia") != null) {
                comuna.setIdCoincidencia((Integer) u.get("iIdCoincidencia"));
                comuna.setComunaIngresada((String) u.get("vcComunaIngresada"));
                comuna.setIdComunaOriginal((Integer) u.get("iIdComunaOriginal"));
                comuna.setNombreComunaOriginal((String) u.get("vcNombreComunaOriginal"));
                comuna.setEsCoincidencia((String) u.get("cEsCoincidencia"));
                return comuna;
            }
        }

        return null;
    }

    // Método para actualizar registros en la tabla ComunaMantenedor
    public int actualizarComunaMantenedor(ComunaMantenedorDTO comuna) {
        String sql = "EXEC paf.SP_ActualizarComunaMantenedor ?, ?, ?, ?, ?";
        return jdbcTemplate.update(sql, 
            comuna.getIdCoincidencia(),
            comuna.getComunaIngresada(),
            comuna.getIdComunaOriginal(),
            comuna.getNombreComunaOriginal(),
            comuna.getEsCoincidencia()
        );
    }

    // Método para eliminar registros de la tabla ComunaMantenedor
    public int eliminarComunaPorIdCoincidencia(Integer iIdCoincidencia) {
        String sql = "EXEC paf.SP_EliminarComunaMantenedor ?";
        return jdbcTemplate.update(sql, iIdCoincidencia);
    }

    // Método para obtener registros por iIdComunaOriginal
    public List<ComunaMantenedorDTO> obtenerRegistrosPorComunaOriginal(Integer iIdComunaOriginal) {
        String sql = "SELECT iIdCoincidencia, vcComunaIngresada, iIdComunaOriginal, vcNombreComunaOriginal, cEsCoincidencia, dFechaInsercion " +
                    "FROM paf.fn_ObtenerRegistrosPorComunaOriginal(?)";
        
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, new Object[]{iIdComunaOriginal});
        
        List<ComunaMantenedorDTO> comunas = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (!results.isEmpty()) {
            for (Map<String, Object> row : results) {
                ComunaMantenedorDTO comuna = new ComunaMantenedorDTO();
                comuna.setIdCoincidencia((Integer) row.get("iIdCoincidencia"));
                comuna.setComunaIngresada((String) row.get("vcComunaIngresada"));
                comuna.setIdComunaOriginal((Integer) row.get("iIdComunaOriginal"));
                comuna.setNombreComunaOriginal((String) row.get("vcNombreComunaOriginal"));
                comuna.setEsCoincidencia((String) row.get("cEsCoincidencia"));
                Date fechaInsercion = (Date) row.get("dFechaInsercion");
                if (fechaInsercion != null) {
                    comuna.setFechaInsercion(dateFormat.format(fechaInsercion));
                } else {
                    comuna.setFechaInsercion(null);  // En caso de que sea null
                }
                
                comunas.add(comuna);
            }
        }
        
        return comunas;
    }

}