package cl.gob.ips.proceso_pago.dao.daoImpl;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import cl.gob.ips.proceso_pago.dao.ProcesoDAO;
import cl.gob.ips.proceso_pago.dto.ProcesoDTO;
import cl.gob.ips.proceso_pago.dto.DatosProcesoPorTipoDTO;

@Repository
public class ProcesoDAOImpl implements ProcesoDAO {

    private final JdbcTemplate jdbcTemplate;

    //@Autowired
    //ComunaService comuna;
    
    @Value("${spring.datasource.schema}")
    private String esquema;

    @Autowired
    public ProcesoDAOImpl(@Qualifier("pafJdbc") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int insertarProceso(ProcesoDTO insertarProcesoDTO) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withSchemaName(esquema)
                .withProcedureName("SP_InsertarProceso")
                .declareParameters(
                        new SqlParameter("iIdUsuario", Types.INTEGER),
                        new SqlParameter("iPagosTotales", Types.INTEGER),
                        new SqlParameter("iAprobados", Types.INTEGER),
                        new SqlParameter("iRechazados", Types.INTEGER),
                        new SqlParameter("iIdEstado", Types.INTEGER),
                        new SqlParameter("dFechaCreacion", Types.DATE),
                        new SqlParameter("dFechaEjecucion", Types.DATE),
                        new SqlParameter("dFechaInicio", Types.DATE),
                        new SqlParameter("dFechaFin", Types.DATE),
                        new SqlOutParameter("idProceso", Types.INTEGER));

        MapSqlParameterSource inParams = new MapSqlParameterSource()
                .addValue("iIdUsuario", insertarProcesoDTO.getIdUsuario())
                .addValue("iPagosTotales", insertarProcesoDTO.getPagosTotales())
                .addValue("iAprobados", insertarProcesoDTO.getAprobados())
                .addValue("iRechazados", insertarProcesoDTO.getRechazados())
                .addValue("iIdEstadoProceso", insertarProcesoDTO.getIdEstado())
                .addValue("dFechaCreacion", insertarProcesoDTO.getFechaCreacion())
                .addValue("dFechaEjecucion", insertarProcesoDTO.getFechaEjecucion())
                .addValue("dFechaInicio", insertarProcesoDTO.getFechaInicio())
                .addValue("dFechaFin", insertarProcesoDTO.getFechaFin());

        try {
            Map<String, Object> result = jdbcCall.execute(inParams);
            String mensajeRespuesta = (String) result.get("mensajeRespuesta");
        System.out.println("Mensaje de Respuesta: " + mensajeRespuesta);
        //Una vez insertado el proceso se insertan las relaciones Proceso/Tipo Solicitante
        insertarProcesoTipoSolicitante((Integer) result.get("idProceso"),insertarProcesoDTO.getListaTipoSolicitante());
            return (Integer) result.get("idProceso");
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return 0;
        }
    }

@Override
public List<ProcesoDTO> consultarProceso(Long idProceso) {
    String sql = "SELECT * FROM paf.fn_ObtenerProcesos(?)";

    List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, new Object[]{idProceso});

    List<ProcesoDTO> procesos = new ArrayList<>();
    for (Map<String, Object> u : results) {
        ProcesoDTO procesoDTO = new ProcesoDTO();
        if (u.get("IdProceso") != null) procesoDTO.setIdProceso((Integer) u.get("IdProceso"));
        if (u.get("IdUsuario") != null) procesoDTO.setIdUsuario((Integer) u.get("IdUsuario"));
        if (u.get("nombreUsuario") != null) procesoDTO.setNombreUsuario((String) u.get("nombreUsuario"));
        if (u.get("PagosTotales") != null) procesoDTO.setPagosTotales((Integer) u.get("PagosTotales"));
        if (u.get("Aprobados") != null) procesoDTO.setAprobados((Integer) u.get("Aprobados"));
        if (u.get("Rechazados") != null) procesoDTO.setRechazados((Integer) u.get("Rechazados"));
        if (u.get("IdEstado") != null) procesoDTO.setIdEstado((Integer) u.get("IdEstado"));
        if (u.get("FechaCreacion") != null) procesoDTO.setFechaCreacion((Date) u.get("FechaCreacion"));
        if (u.get("nombreEstado") != null) procesoDTO.setNombreEstado((String) u.get("nombreEstado"));
        if (u.get("fechaEjecucion") != null) procesoDTO.setFechaEjecucion((Date) u.get("fechaEjecucion"));
        if (u.get("fechaInicio") != null) procesoDTO.setFechaInicio((Date) u.get("fechaInicio"));
        if (u.get("fechaFin") != null) procesoDTO.setFechaFin((Date) u.get("fechaFin"));
        procesos.add(procesoDTO);
    }

    return procesos;
}

@Override
public List<ProcesoDTO> filtrarProcesos(String texto) {
    if (texto.equalsIgnoreCase("null")) {
        texto = null;
    }
    String sql = "EXEC paf.SP_FiltrarProcesos ?";
    List<ProcesoDTO> procesos = new ArrayList<>();
    try {
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, new Object[]{texto});

        for (Map<String, Object> row : results) {
            ProcesoDTO procesoDTO = new ProcesoDTO();
            if (row.get("idProceso") != null) procesoDTO.setIdProceso((Integer) row.get("idProceso"));
            if (row.get("idUsuario") != null) procesoDTO.setIdUsuario((Integer) row.get("idUsuario"));
            if (row.get("nombreUsuario") != null) procesoDTO.setNombreUsuario((String) row.get("nombreUsuario"));
            if (row.get("pagosTotales") != null) procesoDTO.setPagosTotales((Integer) row.get("pagosTotales"));
            if (row.get("aprobados") != null) procesoDTO.setAprobados((Integer) row.get("aprobados"));
            if (row.get("rechazados") != null) procesoDTO.setRechazados((Integer) row.get("rechazados"));
            if (row.get("idEstado") != null) procesoDTO.setIdEstado((Integer) row.get("idEstado"));
            if (row.get("fechaCreacion") != null) procesoDTO.setFechaCreacion((Date) row.get("fechaCreacion"));
            if (row.get("fechaEjecucion") != null) procesoDTO.setFechaEjecucion((Date) row.get("fechaEjecucion"));
            if (row.get("fechaInicio") != null) procesoDTO.setFechaInicio((Date) row.get("fechaInicio"));
            if (row.get("fechaFin") != null) procesoDTO.setFechaFin((Date) row.get("fechaFin"));
            procesos.add(procesoDTO);
        }
    } catch (Exception e) {
        System.out.println("Error al filtrar procesos: " + e.getMessage());
        return null;
    }

    return procesos;
}

@Override
public List<DatosProcesoPorTipoDTO> obtenerDatosProcesoPorTipo(int idProceso) {
    

    //comuna.obtenerRegiones();
    String sql = "SELECT * FROM paf.fn_ObtenerDatosProcesoPorTipo(?)";

    List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, idProceso);

    List<DatosProcesoPorTipoDTO> datosProceso = new ArrayList<>();
    for (Map<String, Object> row : results) {
        DatosProcesoPorTipoDTO dto = new DatosProcesoPorTipoDTO();
        if (row.get("Tipo") != null) dto.setTipo((Integer) row.get("Tipo"));
        if (row.get("Cantidad") != null) dto.setCantidad((Integer) row.get("Cantidad"));
        if (row.get("Porcentaje") != null) dto.setPorcentaje((BigDecimal) row.get("Porcentaje"));
        if (row.get("PagosTotales") != null) dto.setPagosTotales((Integer) row.get("PagosTotales"));
        if (row.get("PagosAprobadosCompletamente") != null) dto.setPagosAprobadosCompletamente((Integer) row.get("PagosAprobadosCompletamente"));
        if (row.get("PorcentajeAprobadosCompletamente") != null) dto.setPorcentajeAprobadosCompletamente((BigDecimal) row.get("PorcentajeAprobadosCompletamente"));
        if (row.get("PagosRechazados") != null) dto.setPagosRechazados((Integer) row.get("PagosRechazados"));
        if (row.get("PorcentajeRechazados") != null) dto.setPorcentajeRechazados((BigDecimal) row.get("PorcentajeRechazados"));
        
        datosProceso.add(dto);
    }

    return datosProceso;
}

public boolean actualizarEstadoProceso(int idProceso, int idEstadoProceso) {
    SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
        .withSchemaName(esquema)
        .withProcedureName("SP_ActualizarEstadoProceso")
        .declareParameters(
            new SqlParameter("iIdProceso", Types.INTEGER),
            new SqlParameter("iIdEstadoProceso", Types.INTEGER)
        );

    MapSqlParameterSource inParams = new MapSqlParameterSource()
        .addValue("iIdProceso", idProceso)
        .addValue("iIdEstadoProceso", idEstadoProceso);

    try {
        jdbcCall.execute(inParams);
        return true;
    } catch (Exception e) {
        System.out.println("ERROR: " + e.getMessage());
        return false;
    }
}

@Override
public void insertarProcesoTipoSolicitante(int idProceso, List<Integer> listaTipoSolicitud) {
    SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withSchemaName(esquema)
            .withProcedureName("SP_InsertarProcesoTipoSolicitante")
            .declareParameters(
                    new SqlParameter("iIdProceso", Types.INTEGER),
                    new SqlParameter("iIdTipoSolicitante", Types.INTEGER),
                    new SqlOutParameter("idProcesoTipoSolicitante", Types.INTEGER));  // Parámetro de salida

    // Iterar sobre la lista de DTOs
    for (Integer tipoSolicitud : listaTipoSolicitud) {
        MapSqlParameterSource inParams = new MapSqlParameterSource()
                .addValue("iIdProceso", idProceso)
                .addValue("iIdTipoSolicitante", tipoSolicitud);

        try {
            // Ejecutar el SP para cada DTO
            Map<String, Object> result = jdbcCall.execute(inParams);

            // Obtener el mensaje de respuesta si es necesario
            String mensajeRespuesta = (String) result.get("mensajeRespuesta");
            System.out.println("Mensaje de Respuesta: " + mensajeRespuesta);

            // Obtener el ID generado y añadirlo a la lista
            //return (Integer) result.get("idProcesoTipoSolicitante");
            //idsGenerados.add(idGenerado);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

}

}