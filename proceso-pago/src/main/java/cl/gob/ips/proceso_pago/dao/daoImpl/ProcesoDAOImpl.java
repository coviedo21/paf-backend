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
import cl.gob.ips.proceso_pago.dto.SolicitudDTO;
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
        int idProcesoFinal = 0;
        boolean crearProceso = false;

        // 1) Recorrer lista de tipo de solicitante
        for (Integer idTipoSolicitante : insertarProcesoDTO.getListaTipoSolicitante()) {
            if (existeSolicitudPendiente(idTipoSolicitante)) {
                crearProceso = true;
                break; // 3) Salimos del bucle al encontrar la primera coincidencia
            }
        }

        // 4) Si la bandera está activa, creamos el proceso
        if (crearProceso) {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withSchemaName(esquema)
                    .withProcedureName("SP_InsertarProceso")
                    .declareParameters(
                            new SqlParameter("iIdUsuario", Types.INTEGER),
                            new SqlParameter("iPagosTotales", Types.INTEGER),
                            new SqlParameter("iAprobados", Types.INTEGER),
                            new SqlParameter("iRechazados", Types.INTEGER),
                            new SqlParameter("iIdEstadoProceso", Types.INTEGER),
                            new SqlParameter("dFechaCreacion", Types.DATE),
                            new SqlParameter("dFechaEjecucion", Types.DATE),
                            new SqlParameter("vcPeriodo", Types.VARCHAR),
                            new SqlOutParameter("mensajeRespuesta", Types.VARCHAR),
                            new SqlOutParameter("idProceso", Types.INTEGER));

            MapSqlParameterSource inParams = new MapSqlParameterSource()
                    .addValue("iIdUsuario", insertarProcesoDTO.getIdUsuario())
                    .addValue("iPagosTotales", insertarProcesoDTO.getPagosTotales())
                    .addValue("iAprobados", insertarProcesoDTO.getAprobados())
                    .addValue("iRechazados", insertarProcesoDTO.getRechazados())
                    .addValue("iIdEstadoProceso", insertarProcesoDTO.getIdEstado())
                    .addValue("dFechaCreacion", insertarProcesoDTO.getFechaCreacion())
                    .addValue("dFechaEjecucion", insertarProcesoDTO.getFechaEjecucion())
                    .addValue("vcPeriodo", insertarProcesoDTO.getPeriodo());

            try {
                Map<String, Object> result = jdbcCall.execute(inParams);
                String mensajeRespuesta = (String) result.get("mensajeRespuesta");
                System.out.println("Mensaje de Respuesta: " + mensajeRespuesta);

                Integer idProceso = (Integer) result.get("idProceso");

                // Si se generó un proceso, guardamos el ID
                if (idProceso != null && idProceso > 0) {
                    idProcesoFinal = idProceso;
                    // Insertar las relaciones Proceso/Tipo Solicitante
                    insertarProcesoTipoSolicitante(idProceso, insertarProcesoDTO.getListaTipoSolicitante());
                }
            } catch (Exception e) {
                System.out.println("ERROR al insertar proceso: " + e.getMessage());
            }
        } else {
            return -1;
            //System.out.println("No se insertó el proceso porque no hay solicitudes pendientes.");
        }

        return idProcesoFinal;
    }

    public boolean existeSolicitudPendiente(Integer idTipoSolicitante) {
        String sql = "SELECT paf.fn_ExisteSolicitudPendiente(?) AS existeSolicitud";
    
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, new Object[]{idTipoSolicitante});
    
        if (!results.isEmpty()) {
            Map<String, Object> row = results.get(0);
            Object value = row.get("existeSolicitud");
    
            if (value instanceof Boolean) {
                return (Boolean) value; // Devuelve directamente el booleano si ya está en este formato
            } else if (value instanceof Number) {
                return ((Number) value).intValue() == 1; // Convierte números (ej. Integer, Long, Short) a booleano
            }
        }
    
        return false;
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
        if (u.get("periodo") != null) procesoDTO.setPeriodo((String) u.get("periodo"));
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
            if (row.get("periodo") != null) procesoDTO.setPeriodo((String) row.get("periodo"));
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
        if (row.get("nombreTipo") != null) dto.setNombreTipoSolicitante((String) row.get("nombreTipo"));
        if (row.get("Cantidad") != null) dto.setCantidad((Integer) row.get("Cantidad"));
        if (row.get("Porcentaje") != null) dto.setPorcentaje((BigDecimal) row.get("Porcentaje"));
        if (row.get("PagosTotales") != null) dto.setPagosTotales((Integer) row.get("PagosTotales"));
        if (row.get("PagosAprobados") != null) dto.setPagosAprobados((Integer) row.get("PagosAprobados"));
        if (row.get("PagosRechazados") != null) dto.setPagosRechazados((Integer) row.get("PagosRechazados"));
        
        
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

    @Override
    public List<SolicitudDTO> obtenerSolicitudesPorProceso(int idProceso) {
        String sql = "SELECT * FROM paf.fn_ListarSolicitudesPorProceso(?)";

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, idProceso);

        List<SolicitudDTO> solicitudes = new ArrayList<>();

        List<SolicitudDTO> solicitudesPago = new ArrayList<>();
            for (Map<String, Object> row : results) {
                SolicitudDTO solicitudPagoDTO = new SolicitudDTO();
                if (row.get("idSolicitud") != null) solicitudPagoDTO.setIdSolicitud((Integer) row.get("idSolicitud"));
                if (row.get("idProceso") != null) solicitudPagoDTO.setIdProceso((Integer) row.get("idProceso"));
                if (row.get("idUsuario") != null) solicitudPagoDTO.setIdUsuario((Integer) row.get("idUsuario"));
                if (row.get("idComuna") != null) solicitudPagoDTO.setIdComuna((Integer) row.get("idComuna"));
                if (row.get("idInstitucion") != null) solicitudPagoDTO.setIdInstitucion((Integer) row.get("idInstitucion"));
                if (row.get("idHaber") != null) solicitudPagoDTO.setIdHaber((Integer) row.get("idHaber"));
                if (row.get("idFormaPago") != null) solicitudPagoDTO.setIdFormaPago((Integer) row.get("idFormaPago"));
                if (row.get("periodo") != null) solicitudPagoDTO.setPeriodo((String) row.get("periodo"));
                if (row.get("code") != null) solicitudPagoDTO.setCode((String) row.get("code"));
                if (row.get("rutEmpleador") != null) solicitudPagoDTO.setRutEmpleador((Integer) row.get("rutEmpleador"));
                if (row.get("dvEmpleador") != null) solicitudPagoDTO.setDvEmpleador((String) row.get("dvEmpleador"));
                if (row.get("razonSocialEmpleador") != null) solicitudPagoDTO.setRazonSocialEmpleador((String) row.get("razonSocialEmpleador"));
                if (row.get("direccion") != null) solicitudPagoDTO.setDireccion((String) row.get("direccion"));
                if (row.get("email") != null) solicitudPagoDTO.setEmail((String) row.get("email"));
                if (row.get("rutBeneficiario") != null) solicitudPagoDTO.setRutBeneficiario((Integer) row.get("rutBeneficiario"));
                if (row.get("dvBeneficiario") != null) solicitudPagoDTO.setDvBeneficiario((String) row.get("dvBeneficiario"));
                if (row.get("nombresBeneficiario") != null) solicitudPagoDTO.setNombresBeneficiario((String) row.get("nombresBeneficiario"));
                if (row.get("apellidoPaternoBeneficiario") != null) solicitudPagoDTO.setApellidoPaternoBeneficiario((String) row.get("apellidoPaternoBeneficiario"));
                if (row.get("apellidoMaternoBeneficiario") != null) solicitudPagoDTO.setApellidoMaternoBeneficiario((String) row.get("apellidoMaternoBeneficiario"));
                if (row.get("rutReteniente") != null) solicitudPagoDTO.setRutReteniente((Integer) row.get("rutReteniente"));
                if (row.get("dvReteniente") != null) solicitudPagoDTO.setDvReteniente((String) row.get("dvReteniente"));
                if (row.get("nombresReteniente") != null) solicitudPagoDTO.setNombresReteniente((String) row.get("nombresReteniente"));
                if (row.get("apellidoPaternoReteniente") != null) solicitudPagoDTO.setApellidoPaternoReteniente((String) row.get("apellidoPaternoReteniente"));
                if (row.get("apellidoMaternoReteniente") != null) solicitudPagoDTO.setApellidoMaternoReteniente((String) row.get("apellidoMaternoReteniente"));
                if (row.get("filePathPrevired") != null) solicitudPagoDTO.setFilePathPrevired((String) row.get("filePathPrevired"));
                if (row.get("filePathEspecial") != null) solicitudPagoDTO.setFilePathEspecial((String) row.get("filePathEspecial"));
                if (row.get("origen") != null) solicitudPagoDTO.setOrigen((Integer) row.get("origen"));
                if (row.get("tipoSolicitante") != null) solicitudPagoDTO.setTipoSolicitante((Integer) row.get("tipoSolicitante"));
                if (row.get("observaciones") != null) solicitudPagoDTO.setObservaciones((String) row.get("observaciones"));
                if (row.get("conRetencionJudicial") != null) solicitudPagoDTO.setConRetencionJudicial((String) row.get("conRetencionJudicial"));
                if (row.get("idCuentaBancaria") != null) solicitudPagoDTO.setIdCuentaBancaria((Integer) row.get("idCuentaBancaria"));
                if (row.get("telefono") != null) solicitudPagoDTO.setTelefono((Integer) row.get("telefono"));
                if (row.get("idRegion") != null) solicitudPagoDTO.setIdRegion((Integer) row.get("idRegion"));
                if (row.get("finiquito") != null) solicitudPagoDTO.setFiniquito((String) row.get("finiquito"));
                if (row.get("fechaFiniquito") != null) solicitudPagoDTO.setFechaFiniquito((Date) row.get("fechaFiniquito"));
                if (row.get("rutRepresentante") != null) solicitudPagoDTO.setRutRepresentante((Integer) row.get("rutRepresentante"));
                if (row.get("dvRepresentante") != null) solicitudPagoDTO.setDvRepresentante((String) row.get("dvRepresentante"));
                if (row.get("fechaSolicitud") != null) solicitudPagoDTO.setFechaSolicitud(new Date(((java.sql.Timestamp) row.get("fechaSolicitud")).getTime()));
                if (row.get("folio") != null) solicitudPagoDTO.setFolio((Long) row.get("folio"));
                if (row.get("estado") != null) solicitudPagoDTO.setEstado((String) row.get("estado"));
                if (row.get("nombreOrigen") != null) solicitudPagoDTO.setNombreOrigen((String) row.get("nombreOrigen"));
                if (row.get("nombreRegion") != null) solicitudPagoDTO.setNombreRegion((String) row.get("nombreRegion"));
                if (row.get("ciudad") != null) solicitudPagoDTO.setCiudadEmpleador((String) row.get("ciudad"));
                if (row.get("montoHaber") != null) solicitudPagoDTO.setMontoHaber((BigDecimal) row.get("montoHaber"));
                solicitudesPago.add(solicitudPagoDTO);
            }

            return solicitudesPago;
    }

}