package cl.gob.ips.proceso_pago.dao.daoImpl;

import java.sql.Types;
import java.util.ArrayList;
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

import cl.gob.ips.proceso_pago.dao.EmisionDAO;
import cl.gob.ips.proceso_pago.dto.EmisionArchivoDTO;
import cl.gob.ips.proceso_pago.dto.EmisionDTO;
import cl.gob.ips.proceso_pago.dto.ProcesoDTO;

@Repository
public class EmisionDAOImpl implements EmisionDAO{
private final JdbcTemplate jdbcTemplate;

    @Value("${spring.datasource.schema}")
    private String esquema;

    @Autowired
    public EmisionDAOImpl(@Qualifier("pafJdbc") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Boolean validarSolicitudesEmitidas(List<EmisionArchivoDTO> emision){
        return true;
    }

   @Override
    public List<EmisionDTO> obtenerEmisiones() {
        String sql = "SELECT * FROM paf.fn_ObtenerEmisiones()"; // Llamada a la función

        try {
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);

            List<EmisionDTO> emisiones = new ArrayList<>();
            for (Map<String, Object> row : results) {
                EmisionDTO emisionDTO = new EmisionDTO();
                if (row.get("iIdEmision") != null) 
                    emisionDTO.setIdEmision((Integer) row.get("iIdEmision"));
                if (row.get("dFecha") != null) 
                    emisionDTO.setFechaEmision(new java.util.Date(((java.sql.Date) row.get("dFecha")).getTime()));
                if (row.get("iIdProceso") != null) 
                    emisionDTO.setIdProceso((Integer) row.get("iIdProceso"));
                if (row.get("vcRutaArchivo") != null) 
                    emisionDTO.setRutaArchivo((String) row.get("vcRutaArchivo"));

                emisiones.add(emisionDTO);
            }

            return emisiones;
        } catch (Exception e) {
            System.out.println("Error al obtener las emisiones: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public int insertarEmision(EmisionDTO emision) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("paf")
                .withProcedureName("SP_InsertarEmision")
                .declareParameters(
                        new SqlParameter("dFecha", Types.DATE),
                        new SqlParameter("iIdProceso", Types.INTEGER),
                        new SqlParameter("vcRutaArchivo", Types.VARCHAR),
                        new SqlOutParameter("idCriterioCausante", Types.INTEGER),
                        new SqlOutParameter("mensajeRespuesta", Types.VARCHAR)
                );

        SqlParameterSource inParams = new MapSqlParameterSource()
                .addValue("dFecha", emision.getFechaEmision())
                .addValue("iIdProceso", emision.getIdProceso())
                .addValue("vcRutaArchivo", emision.getRutaArchivo())
                .addValue("mensajeRespuesta", Types.VARCHAR)
                .addValue("iIdEmision", Types.INTEGER);

        Map<String, Object> result = jdbcCall.execute(inParams);
        Integer idEmision = (Integer) result.get("iIdEmision");
        System.out.println((String) result.get("mensajeRespuesta"));
        return idEmision;
    }

    @Override
    public ProcesoDTO obtenerProcesoPorPeriodo(String periodo) {
        String sql = "SELECT * FROM paf.fn_ObtenerProcesoPorPeriodo(?)"; // Llamada a la función con parámetro

        try {
            Map<String, Object> row = jdbcTemplate.queryForMap(sql, new Object[]{periodo});
            
            ProcesoDTO procesoDTO = new ProcesoDTO();
            if (row.get("IdProceso") != null) 
                procesoDTO.setIdProceso((Integer) row.get("IdProceso"));
            if (row.get("IdUsuario") != null) 
                procesoDTO.setIdUsuario((Integer) row.get("IdUsuario"));
            if (row.get("nombreUsuario") != null) 
                procesoDTO.setNombreUsuario((String) row.get("nombreUsuario"));
            if (row.get("PagosTotales") != null) 
                procesoDTO.setPagosTotales((Integer) row.get("PagosTotales"));
            if (row.get("Aprobados") != null) 
                procesoDTO.setAprobados((Integer) row.get("Aprobados"));
            if (row.get("Rechazados") != null) 
                procesoDTO.setRechazados((Integer) row.get("Rechazados"));
            if (row.get("IdEstado") != null) 
                procesoDTO.setIdEstado((Integer) row.get("IdEstado"));
            if (row.get("FechaCreacion") != null) 
                procesoDTO.setFechaCreacion(new java.util.Date(((java.sql.Date) row.get("FechaCreacion")).getTime()));
            if (row.get("nombreEstado") != null) 
                procesoDTO.setNombreEstado((String) row.get("nombreEstado"));
            if (row.get("periodo") != null) 
                procesoDTO.setPeriodo((String) row.get("periodo"));
            if (row.get("fechaEjecucion") != null) 
                procesoDTO.setFechaEjecucion(new java.util.Date(((java.sql.Date) row.get("fechaEjecucion")).getTime()));

            return procesoDTO;
        } catch (Exception e) {
            System.out.println("Error al obtener el proceso por periodo: " + e.getMessage());
            return null;
        }
    }

    @Override
    public EmisionDTO obtenerEmision(int idEmision) {
        String sql = "SELECT * FROM paf.fn_ObtenerEmision(?)"; // Llamada a la función con parámetro

        try {
            Map<String, Object> row = jdbcTemplate.queryForMap(sql, new Object[]{idEmision});
            
            EmisionDTO emisionDTO = new EmisionDTO();
            if (row.get("iIdEmision") != null) 
                emisionDTO.setIdEmision((Integer) row.get("iIdEmision"));
            if (row.get("dFecha") != null) 
                emisionDTO.setFechaEmision(new java.util.Date(((java.sql.Date) row.get("dFecha")).getTime()));
            if (row.get("iIdProceso") != null) 
                emisionDTO.setIdProceso((Integer) row.get("iIdProceso"));
            if (row.get("vcRutaArchivo") != null) 
                emisionDTO.setRutaArchivo((String) row.get("vcRutaArchivo"));

            return emisionDTO;
        } catch (Exception e) {
            System.out.println("Error al obtener la emisión: " + e.getMessage());
            return null;
        }
    }

    
}
