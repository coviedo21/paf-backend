package cl.gob.ips.pagos_asignacion_familiar.dao.daoImpl;

import cl.gob.ips.pagos_asignacion_familiar.dto.CausanteSolicitudDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.ProcesoDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.ResolucionDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.SolicitudDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.TipoSolicitanteDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import cl.gob.ips.pagos_asignacion_familiar.dao.SolicitudPagoDAO;

import java.math.BigDecimal;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class SolicitudPagoDAOImpl implements SolicitudPagoDAO {

    private final JdbcTemplate jdbcTemplate;

    @Value("${spring.datasource.schema}")
    private String esquema;

    @Autowired
    public SolicitudPagoDAOImpl(@Qualifier("pafJdbc") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int insertarSolicitudPago(SolicitudDTO solicitudPago) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withSchemaName(esquema)
                .withProcedureName("SP_InsertarSolicitudPago")
                .declareParameters(
                        new SqlParameter("iIdProceso", Types.INTEGER),
                        new SqlParameter("iIdUsuario", Types.INTEGER),
                        new SqlParameter("iIdTipoCausante", Types.INTEGER),
                        new SqlParameter("iIdComuna", Types.INTEGER),
                        new SqlParameter("iIdInstitucion", Types.INTEGER),
                        new SqlParameter("iIdHaber", Types.INTEGER),
                        new SqlParameter("iIdFormaPago", Types.INTEGER),
                        new SqlParameter("vcPeriodo", Types.VARCHAR),
                        new SqlParameter("vcCode", Types.VARCHAR),
                        new SqlParameter("iRutEmpleador", Types.INTEGER),
                        new SqlParameter("vcDvEmpleador", Types.VARCHAR),
                        new SqlParameter("vcRazonSocialEmpleador", Types.VARCHAR),
                        new SqlParameter("vcDireccion", Types.VARCHAR),
                        new SqlParameter("vcEmail", Types.VARCHAR),
                        new SqlParameter("iRutBeneficiario", Types.INTEGER),
                        new SqlParameter("vcDvBeneficiario", Types.VARCHAR),
                        new SqlParameter("vcNombresBeneficiario", Types.VARCHAR),
                        new SqlParameter("vcApellidoPaternoBeneficiario", Types.VARCHAR),
                        new SqlParameter("vcApellidoMaternoBeneficiario", Types.VARCHAR),
                        new SqlParameter("iRutCausante", Types.INTEGER),
                        new SqlParameter("vcDvCausante", Types.VARCHAR),
                        new SqlParameter("vcNombresCausante", Types.VARCHAR),
                        new SqlParameter("vcApellidoPaternoCausante", Types.VARCHAR),
                        new SqlParameter("vcApellidoMaternoCausante", Types.VARCHAR),
                        new SqlParameter("iRutReteniente", Types.INTEGER),
                        new SqlParameter("vcDvReteniente", Types.VARCHAR),
                        new SqlParameter("vcNombresReteniente", Types.VARCHAR),
                        new SqlParameter("vcApellidoPaternoReteniente", Types.VARCHAR),
                        new SqlParameter("vcApellidoMaternoReteniente", Types.VARCHAR),
                        new SqlParameter("dInicioCompensacion", Types.DATE),
                        new SqlParameter("dFinCompensacion", Types.DATE),
                        new SqlParameter("vcFilePathPrevired", Types.VARCHAR),
                        new SqlParameter("vcFilePathEspecial", Types.VARCHAR),
                        new SqlParameter("iOrigen", Types.INTEGER),
                        new SqlParameter("iTipoSolicitante", Types.INTEGER),
                        new SqlParameter("iSubTipoSolicitante", Types.INTEGER),
                        new SqlParameter("vcObservaciones", Types.VARCHAR),
                        new SqlParameter("montoReconocimientoDerecho", Types.DECIMAL),
                        new SqlParameter("montoReconocimientoEfectivo", Types.DECIMAL),
                        new SqlParameter("conRetencionJudicial", Types.VARCHAR),
                        new SqlParameter("invalida", Types.VARCHAR),
                        new SqlParameter("emitida", Types.VARCHAR),
                        new SqlParameter("iIdCuentaBancaria", Types.INTEGER),
                        new SqlParameter("iTelefono", Types.INTEGER),
                        new SqlParameter("iIdRegion", Types.INTEGER),
                        new SqlParameter("vcFiniquito", Types.VARCHAR),
                        new SqlParameter("dFechaFiniquito", Types.DATE),
                        new SqlParameter("iRutRepresentante", Types.INTEGER),
                        new SqlParameter("vcDvRepresentante", Types.VARCHAR),
                        new SqlParameter("dFechaSolicitud", Types.TIMESTAMP),
                        new SqlParameter("folio", Types.BIGINT),
                        new SqlParameter("vcNombreComuna", Types.VARCHAR),
                        new SqlOutParameter("idSolicitud", Types.INTEGER),
                        new SqlOutParameter("mensajeRespuesta", Types.VARCHAR)
                );

        MapSqlParameterSource inParams = new MapSqlParameterSource()
                .addValue("iIdProceso", solicitudPago.getIdProceso()>0?solicitudPago.getIdProceso():null)
                .addValue("iIdUsuario", solicitudPago.getIdUsuario())
                .addValue("iIdTipoCausante", solicitudPago.getIdTipoCausante()>0?solicitudPago.getIdTipoCausante()>0:null)
                .addValue("iIdComuna", solicitudPago.getIdComuna())
                .addValue("iIdInstitucion", solicitudPago.getIdInstitucion())
                .addValue("iIdHaber", solicitudPago.getIdHaber())
                .addValue("iIdFormaPago", solicitudPago.getIdFormaPago()>0?solicitudPago.getIdFormaPago():null)
                .addValue("vcPeriodo", solicitudPago.getPeriodo())
                .addValue("vcCode", solicitudPago.getCode())
                .addValue("iRutEmpleador", solicitudPago.getRutEmpleador())
                .addValue("vcDvEmpleador", solicitudPago.getDvEmpleador())
                .addValue("vcRazonSocialEmpleador", solicitudPago.getRazonSocialEmpleador())
                .addValue("vcDireccion", solicitudPago.getDireccion())
                .addValue("vcEmail", solicitudPago.getEmail())
                .addValue("iRutBeneficiario", solicitudPago.getRutBeneficiario())
                .addValue("vcDvBeneficiario", solicitudPago.getDvBeneficiario())
                .addValue("vcNombresBeneficiario", solicitudPago.getNombresBeneficiario())
                .addValue("vcApellidoPaternoBeneficiario", solicitudPago.getApellidoPaternoBeneficiario())
                .addValue("vcApellidoMaternoBeneficiario", solicitudPago.getApellidoMaternoBeneficiario())
                .addValue("iRutCausante", solicitudPago.getRutCausante())
                .addValue("vcDvCausante", solicitudPago.getDvCausante())
                .addValue("vcNombresCausante", solicitudPago.getNombresCausante())
                .addValue("vcApellidoPaternoCausante", solicitudPago.getApellidoPaternoCausante())
                .addValue("vcApellidoMaternoCausante", solicitudPago.getApellidoMaternoCausante())
                .addValue("iRutReteniente", solicitudPago.getRutReteniente())
                .addValue("vcDvReteniente", solicitudPago.getDvReteniente())
                .addValue("vcNombresReteniente", solicitudPago.getNombresReteniente())
                .addValue("vcApellidoPaternoReteniente", solicitudPago.getApellidoPaternoReteniente())
                .addValue("vcApellidoMaternoReteniente", solicitudPago.getApellidoMaternoReteniente())
                .addValue("dInicioCompensacion", solicitudPago.getInicioCompensacion())
                .addValue("dFinCompensacion", solicitudPago.getFinCompensacion())
                .addValue("vcFilePathPrevired", solicitudPago.getFilePathPrevired())
                .addValue("vcFilePathEspecial", solicitudPago.getFilePathEspecial())
                .addValue("iOrigen", solicitudPago.getOrigen())
                .addValue("iTipoSolicitante", solicitudPago.getTipoSolicitante()>0?solicitudPago.getTipoSolicitante():null)
                .addValue("iSubTipoSolicitante", solicitudPago.getSubTipoSolicitante()>0?solicitudPago.getSubTipoSolicitante():null)
                .addValue("vcObservaciones", solicitudPago.getObservaciones())
                .addValue("montoReconocimientoDerecho", solicitudPago.getMontoReconocimientoDerecho())
                .addValue("montoReconocimientoEfectivo", solicitudPago.getMontoReconocimientoEfectivo())
                .addValue("conRetencionJudicial", solicitudPago.getConRetencionJudicial())
                .addValue("invalida", solicitudPago.getInvalida())
                .addValue("emitida", solicitudPago.getEmitida())
                .addValue("iIdCuentaBancaria", solicitudPago.getIdCuentaBancaria())
                .addValue("iTelefono", solicitudPago.getTelefono())
                .addValue("iIdRegion", solicitudPago.getIdRegion())
                .addValue("vcFiniquito", solicitudPago.getFiniquito())
                .addValue("dFechaFiniquito", solicitudPago.getFechaFiniquito())
                .addValue("iRutRepresentante", solicitudPago.getRutRepresentante())
                .addValue("vcDvRepresentante", solicitudPago.getDvRepresentante())
                .addValue("dFechaSolicitud", LocalDateTime.now())
                .addValue("folio", solicitudPago.getFolio())
                .addValue("vcNombreComuna", solicitudPago.getNombreComuna());

        try {
            Map<String, Object> result = jdbcCall.execute(inParams);
            Integer idSolicitud = (Integer) result.get("idSolicitud");
            String mensajeRespuesta = (String) result.get("mensajeRespuesta");

            if (idSolicitud != null && idSolicitud > 0) {
                System.out.println("Solicitud insertada correctamente con ID: " + idSolicitud);
                // Si la solicitud se inserta correctamente, insertamos los causantes
                insertarCausantesSolicitud(idSolicitud, solicitudPago.getListaCausantes());
                return idSolicitud;
            } else {
                System.out.println("Error al insertar la solicitud: " + mensajeRespuesta);
                return 0;
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return 0;
        }
    }

    private void insertarCausantesSolicitud(int idSolicitud, List<CausanteSolicitudDTO> listaCausante) {
        if (listaCausante == null || listaCausante.isEmpty()) {
            return;
        }
    
        for (CausanteSolicitudDTO causante : listaCausante) {
            SimpleJdbcCall jdbcCallCausante = new SimpleJdbcCall(jdbcTemplate).withSchemaName(esquema)
                    .withProcedureName("SP_InsertarCausanteSolicitud")
                    .declareParameters(
                            new SqlParameter("iIdSolicitud", Types.INTEGER),
                            new SqlParameter("iRutBeneficiario", Types.INTEGER),
                            new SqlParameter("vcDvBeneficiario", Types.VARCHAR),
                            new SqlParameter("iRutCausante", Types.INTEGER),
                            new SqlParameter("vcDvCausante", Types.VARCHAR),
                            new SqlParameter("vcNombresCausante", Types.VARCHAR),
                            new SqlParameter("vcApellidoPaternoCausante", Types.VARCHAR),
                            new SqlParameter("vcApellidoMaternoCausante", Types.VARCHAR),
                            new SqlParameter("dFechaInicioRango", Types.DATE),
                            new SqlParameter("dFechaFinRango", Types.DATE),
                            new SqlParameter("vcPeriodosAprobados", Types.VARCHAR),
                            new SqlParameter("mTotalPagar", Types.DECIMAL),
                            new SqlOutParameter("mensajeRespuesta", Types.VARCHAR)
                    );
    
            MapSqlParameterSource inParamsCausante = new MapSqlParameterSource()
                    .addValue("iIdSolicitud", idSolicitud)
                    .addValue("iRutBeneficiario", causante.getRutBeneficiario())
                    .addValue("vcDvBeneficiario", causante.getVcDvBeneficiario())
                    .addValue("iRutCausante", causante.getRutCausante())
                    .addValue("vcDvCausante", causante.getVcDvCausante())
                    .addValue("vcNombresCausante", causante.getVcNombreCausante())
                    .addValue("dFechaInicioRango", causante.getFechaInicioRango())
                    .addValue("dFechaFinRango", causante.getFechaFinRango())
                    .addValue("vcPeriodosAprobados", causante.getVcPeriodosAprobados())
                    .addValue("mTotalPagar", causante.getTotalPagar());
    
            try {
                Map<String, Object> resultCausante = jdbcCallCausante.execute(inParamsCausante);
                System.out.println("Causante insertado: " + resultCausante.get("mensajeRespuesta"));
            } catch (Exception e) {
                System.out.println("ERROR al insertar causante: " + e.getMessage());
            }
        }
    }    

    @Override
    public List<SolicitudDTO> consultarSolicitudPago(Integer idSolicitud) {
        String sql = "SELECT * FROM paf.fn_ObtenerSolicitudesPago(?)";
        String sqlCausantes = "SELECT * FROM paf.fn_ObtenerCausanteSolicitud(?)";

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, new Object[]{idSolicitud});

        List<SolicitudDTO> solicitudesPago = new ArrayList<>();
        for (Map<String, Object> row : results) {
            SolicitudDTO solicitudPagoDTO = new SolicitudDTO();
            if (row.get("idSolicitud") != null) solicitudPagoDTO.setIdSolicitud((Integer) row.get("idSolicitud"));
            if (row.get("idProceso") != null) solicitudPagoDTO.setIdProceso((Integer) row.get("idProceso"));
            if (row.get("idUsuario") != null) solicitudPagoDTO.setIdUsuario((Integer) row.get("idUsuario"));
            if (row.get("idTipoCausante") != null) solicitudPagoDTO.setIdTipoCausante((Integer) row.get("idTipoCausante"));
            if (row.get("idComuna") != null) solicitudPagoDTO.setIdComuna((Integer) row.get("idComuna"));
            if (row.get("nombreComuna") != null) solicitudPagoDTO.setNombreComuna((String) row.get("nombreComuna"));
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
            if (row.get("rutCausante") != null) solicitudPagoDTO.setRutCausante((Integer) row.get("rutCausante"));
            if (row.get("dvCausante") != null) solicitudPagoDTO.setDvCausante((String) row.get("dvCausante"));
            if (row.get("nombresCausante") != null) solicitudPagoDTO.setNombresCausante((String) row.get("nombresCausante"));
            if (row.get("apellidoPaternoCausante") != null) solicitudPagoDTO.setApellidoPaternoCausante((String) row.get("apellidoPaternoCausante"));
            if (row.get("apellidoMaternoCausante") != null) solicitudPagoDTO.setApellidoMaternoCausante((String) row.get("apellidoMaternoCausante"));
            if (row.get("rutReteniente") != null) solicitudPagoDTO.setRutReteniente((Integer) row.get("rutReteniente"));
            if (row.get("dvReteniente") != null) solicitudPagoDTO.setDvReteniente((String) row.get("dvReteniente"));
            if (row.get("nombresReteniente") != null) solicitudPagoDTO.setNombresReteniente((String) row.get("nombresReteniente"));
            if (row.get("apellidoPaternoReteniente") != null) solicitudPagoDTO.setApellidoPaternoReteniente((String) row.get("apellidoPaternoReteniente"));
            if (row.get("apellidoMaternoReteniente") != null) solicitudPagoDTO.setApellidoMaternoReteniente((String) row.get("apellidoMaternoReteniente"));
            if (row.get("inicioCompensacion") != null) solicitudPagoDTO.setInicioCompensacion((Date) row.get("inicioCompensacion"));
            if (row.get("finCompensacion") != null) solicitudPagoDTO.setFinCompensacion((Date) row.get("finCompensacion"));
            if (row.get("filePathPrevired") != null) solicitudPagoDTO.setFilePathPrevired((String) row.get("filePathPrevired"));
            if (row.get("filePathEspecial") != null) solicitudPagoDTO.setFilePathEspecial((String) row.get("filePathEspecial"));
            if (row.get("origen") != null) solicitudPagoDTO.setOrigen((Integer) row.get("origen"));
            if (row.get("tipoSolicitante") != null) solicitudPagoDTO.setTipoSolicitante((Integer) row.get("tipoSolicitante"));
            if (row.get("nombreTipoSolicitante") != null) solicitudPagoDTO.setNombreTipoSolicitante((String) row.get("nombreTipoSolicitante"));
            if (row.get("subTipoSolicitante") != null) solicitudPagoDTO.setSubTipoSolicitante((Integer) row.get("subTipoSolicitante"));
            if (row.get("observaciones") != null) solicitudPagoDTO.setObservaciones((String) row.get("observaciones"));
            if (row.get("montoReconocimientoDerecho") != null) solicitudPagoDTO.setMontoReconocimientoDerecho((BigDecimal) row.get("montoReconocimientoDerecho"));
            if (row.get("montoReconocimientoEfectivo") != null) solicitudPagoDTO.setMontoReconocimientoEfectivo((BigDecimal) row.get("montoReconocimientoEfectivo"));
            if (row.get("conRetencionJudicial") != null) solicitudPagoDTO.setConRetencionJudicial((String) row.get("conRetencionJudicial"));
            if (row.get("invalida") != null) solicitudPagoDTO.setInvalida((String) row.get("invalida"));
            if (row.get("emitida") != null) solicitudPagoDTO.setEmitida((String) row.get("emitida"));
            if (row.get("idCuentaBancaria") != null) solicitudPagoDTO.setIdCuentaBancaria((Integer) row.get("idCuentaBancaria"));
            if (row.get("telefono") != null) solicitudPagoDTO.setTelefono((Integer) row.get("telefono"));
            if (row.get("idRegion") != null) solicitudPagoDTO.setIdRegion((Integer) row.get("idRegion"));
            if (row.get("finiquito") != null) solicitudPagoDTO.setFiniquito((String) row.get("finiquito"));
            if (row.get("fechaFiniquito") != null) solicitudPagoDTO.setFechaFiniquito((Date) row.get("fechaFiniquito"));
            if (row.get("rutRepresentante") != null) solicitudPagoDTO.setRutRepresentante((Integer) row.get("rutRepresentante"));
            if (row.get("dvRepresentante") != null) solicitudPagoDTO.setDvRepresentante((String) row.get("dvRepresentante"));
            if (row.get("fechaSolicitud") != null) solicitudPagoDTO.setFechaSolicitud((Date) row.get("fechaSolicitud"));
            if (row.get("folio") != null) solicitudPagoDTO.setFolio((Long) row.get("folio"));
            if (row.get("estado") != null) solicitudPagoDTO.setEstado((String) row.get("estado"));
            if (row.get("nombreOrigen") != null) solicitudPagoDTO.setNombreOrigen((String) row.get("nombreOrigen"));
            solicitudesPago.add(solicitudPagoDTO);

            // Consulta los causantes de la solicitud actual 
            /*List<Map<String, Object>> causantesResult = jdbcTemplate.queryForList(sqlCausantes, new Object[]{solicitudPagoDTO.getIdSolicitud()});
            List<CausanteSolicitudDTO> listaCausantes = new ArrayList<>();

            for (Map<String, Object> causanteRow : causantesResult) {
                CausanteSolicitudDTO causanteDTO = new CausanteSolicitudDTO();
                if (causanteRow.get("iIdCausanteSolicitud") != null) causanteDTO.setIIdCausanteSolicitud((Integer) causanteRow.get("iIdCausanteSolicitud"));
                if (causanteRow.get("iRutBeneficiario") != null) causanteDTO.setIRutBeneficiario((String) causanteRow.get("iRutBeneficiario"));
                if (causanteRow.get("vcDvBeneficiario") != null) causanteDTO.setVcDvBeneficiario((String) causanteRow.get("vcDvBeneficiario"));
                if (causanteRow.get("iRutCausante") != null) causanteDTO.setIRutCausante((String) causanteRow.get("iRutCausante"));
                if (causanteRow.get("vcDvCausante") != null) causanteDTO.setVcDvCausante((String) causanteRow.get("vcDvCausante"));
                if (causanteRow.get("vcNombreCausante") != null) causanteDTO.setVcNombreCausante((String) causanteRow.get("vcNombreCausante"));
                if (causanteRow.get("dFechaInicioRango") != null) causanteDTO.setDFechaInicioRango((Date) causanteRow.get("dFechaInicioRango"));
                if (causanteRow.get("dFechaFinRango") != null) causanteDTO.setDFechaFinRango((Date) causanteRow.get("dFechaFinRango"));
                if (causanteRow.get("vcPeriodosAprobados") != null) causanteDTO.setVcPeriodosAprobados((String) causanteRow.get("vcPeriodosAprobados"));
                if (causanteRow.get("mTotalPagar") != null) causanteDTO.setMTotalPagar((BigDecimal) causanteRow.get("mTotalPagar"));
    
                listaCausantes.add(causanteDTO); // Agrega el causante a la lista
            }*/
            //solicitudPagoDTO.setListaCausantes(listaCausantes);
        }

        return solicitudesPago;
    }

    @Override
    public boolean actualizarSolicitudPago(SolicitudDTO solicitudPago) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withSchemaName(esquema)
                .withProcedureName("SP_ActualizarSolicitudPago")
                .declareParameters(
                        new SqlParameter("iIdSolicitud", Types.INTEGER),
                        new SqlParameter("iIdProceso", Types.INTEGER),
                        new SqlParameter("iIdUsuario", Types.INTEGER),
                        new SqlParameter("iIdTipoCausante", Types.INTEGER),
                        new SqlParameter("iIdComuna", Types.INTEGER),
                        new SqlParameter("iIdInstitucion", Types.INTEGER),
                        new SqlParameter("iIdHaber", Types.INTEGER),
                        new SqlParameter("iIdFormaPago", Types.INTEGER),
                        new SqlParameter("vcPeriodo", Types.VARCHAR),
                        new SqlParameter("vcCode", Types.VARCHAR),
                        new SqlParameter("iRutEmpleador", Types.INTEGER),
                        new SqlParameter("vcDvEmpleador", Types.VARCHAR),
                        new SqlParameter("vcRazonSocialEmpleador", Types.VARCHAR),
                        new SqlParameter("vcDireccion", Types.VARCHAR),
                        new SqlParameter("vcEmail", Types.VARCHAR),
                        new SqlParameter("iRutBeneficiario", Types.INTEGER),
                        new SqlParameter("vcDvBeneficiario", Types.VARCHAR),
                        new SqlParameter("vcNombresBeneficiario", Types.VARCHAR),
                        new SqlParameter("vcApellidoPaternoBeneficiario", Types.VARCHAR),
                        new SqlParameter("vcApellidoMaternoBeneficiario", Types.VARCHAR),
                        new SqlParameter("iRutCausante", Types.INTEGER),
                        new SqlParameter("vcDvCausante", Types.VARCHAR),
                        new SqlParameter("vcNombresCausante", Types.VARCHAR),
                        new SqlParameter("vcApellidoPaternoCausante", Types.VARCHAR),
                        new SqlParameter("vcApellidoMaternoCausante", Types.VARCHAR),
                        new SqlParameter("iRutReteniente", Types.INTEGER),
                        new SqlParameter("vcDvReteniente", Types.VARCHAR),
                        new SqlParameter("vcNombresReteniente", Types.VARCHAR),
                        new SqlParameter("vcApellidoPaternoReteniente", Types.VARCHAR),
                        new SqlParameter("vcApellidoMaternoReteniente", Types.VARCHAR),
                        new SqlParameter("dInicioCompensacion", Types.DATE),
                        new SqlParameter("dFinCompensacion", Types.DATE),
                        new SqlParameter("vcFilePathPrevired", Types.VARCHAR),
                        new SqlParameter("vcFilePathEspecial", Types.VARCHAR),
                        new SqlParameter("iOrigen", Types.INTEGER),
                        new SqlParameter("iTipoSolicitante", Types.INTEGER),
                        new SqlParameter("iSubTipoSolicitante", Types.INTEGER),
                        new SqlParameter("vcObservaciones", Types.VARCHAR),
                        new SqlParameter("montoReconocimientoDerecho", Types.DECIMAL),
                        new SqlParameter("montoReconocimientoEfectivo", Types.DECIMAL),
                        new SqlParameter("conRetencionJudicial", Types.VARCHAR),
                        new SqlParameter("invalida", Types.VARCHAR),
                        new SqlParameter("emitida", Types.VARCHAR),
                        new SqlParameter("iIdCuentaBancaria", Types.INTEGER),
                        new SqlParameter("iTelefono", Types.INTEGER),
                        new SqlParameter("iIdRegion", Types.INTEGER),
                        new SqlParameter("vcFiniquito", Types.VARCHAR),
                        new SqlParameter("dFechaFiniquito", Types.DATE),
                        new SqlParameter("iRutRepresentante", Types.INTEGER),
                        new SqlParameter("vcDvRepresentante", Types.VARCHAR),
                        new SqlOutParameter("mensajeRespuesta", Types.VARCHAR)
                );

        MapSqlParameterSource inParams = new MapSqlParameterSource()
                .addValue("iIdSolicitud", solicitudPago.getIdSolicitud())
                .addValue("iIdProceso", solicitudPago.getIdProceso() > 0 ? solicitudPago.getIdProceso() : null)
                .addValue("iIdUsuario", solicitudPago.getIdUsuario())
                .addValue("iIdTipoCausante", solicitudPago.getIdTipoCausante() > 0 ? solicitudPago.getIdTipoCausante() : null)
                .addValue("iIdComuna", solicitudPago.getIdComuna()>0?solicitudPago.getIdComuna():null)
                .addValue("iIdInstitucion", solicitudPago.getIdInstitucion())
                .addValue("iIdHaber", solicitudPago.getIdHaber())
                .addValue("iIdFormaPago", solicitudPago.getIdFormaPago() > 0 ? solicitudPago.getIdFormaPago() : null)
                .addValue("vcPeriodo", solicitudPago.getPeriodo())
                .addValue("vcCode", solicitudPago.getCode())
                .addValue("iRutEmpleador", solicitudPago.getRutEmpleador())
                .addValue("vcDvEmpleador", solicitudPago.getDvEmpleador())
                .addValue("vcRazonSocialEmpleador", solicitudPago.getRazonSocialEmpleador())
                .addValue("vcDireccion", solicitudPago.getDireccion())
                .addValue("vcEmail", solicitudPago.getEmail())
                .addValue("iRutBeneficiario", solicitudPago.getRutBeneficiario())
                .addValue("vcDvBeneficiario", solicitudPago.getDvBeneficiario())
                .addValue("vcNombresBeneficiario", solicitudPago.getNombresBeneficiario())
                .addValue("vcApellidoPaternoBeneficiario", solicitudPago.getApellidoPaternoBeneficiario())
                .addValue("vcApellidoMaternoBeneficiario", solicitudPago.getApellidoMaternoBeneficiario())
                .addValue("iRutCausante", solicitudPago.getRutCausante())
                .addValue("vcDvCausante", solicitudPago.getDvCausante())
                .addValue("vcNombresCausante", solicitudPago.getNombresCausante())
                .addValue("vcApellidoPaternoCausante", solicitudPago.getApellidoPaternoCausante())
                .addValue("vcApellidoMaternoCausante", solicitudPago.getApellidoMaternoCausante())
                .addValue("iRutReteniente", solicitudPago.getRutReteniente())
                .addValue("vcDvReteniente", solicitudPago.getDvReteniente())
                .addValue("vcNombresReteniente", solicitudPago.getNombresReteniente())
                .addValue("vcApellidoPaternoReteniente", solicitudPago.getApellidoPaternoReteniente())
                .addValue("vcApellidoMaternoReteniente", solicitudPago.getApellidoMaternoReteniente())
                .addValue("dInicioCompensacion", solicitudPago.getInicioCompensacion())
                .addValue("dFinCompensacion", solicitudPago.getFinCompensacion())
                .addValue("vcFilePathPrevired", solicitudPago.getFilePathPrevired())
                .addValue("vcFilePathEspecial", solicitudPago.getFilePathEspecial())
                .addValue("iOrigen", solicitudPago.getOrigen()>0?solicitudPago.getOrigen():null)
                .addValue("iTipoSolicitante", solicitudPago.getTipoSolicitante() > 0 ? solicitudPago.getTipoSolicitante() : null)
                .addValue("iSubTipoSolicitante", solicitudPago.getSubTipoSolicitante() > 0 ? solicitudPago.getSubTipoSolicitante() : null)
                .addValue("vcObservaciones", solicitudPago.getObservaciones())
                .addValue("montoReconocimientoDerecho", solicitudPago.getMontoReconocimientoDerecho())
                .addValue("montoReconocimientoEfectivo", solicitudPago.getMontoReconocimientoEfectivo())
                .addValue("conRetencionJudicial", solicitudPago.getConRetencionJudicial())
                .addValue("invalida", solicitudPago.getInvalida())
                .addValue("emitida", solicitudPago.getEmitida())
                .addValue("iIdCuentaBancaria", solicitudPago.getIdCuentaBancaria())
                .addValue("iTelefono", solicitudPago.getTelefono())
                .addValue("iIdRegion", solicitudPago.getIdRegion())
                .addValue("vcFiniquito", solicitudPago.getFiniquito())
                .addValue("dFechaFiniquito", solicitudPago.getFechaFiniquito())
                .addValue("iRutRepresentante", solicitudPago.getRutRepresentante())
                .addValue("vcDvRepresentante", solicitudPago.getDvRepresentante())
                .addValue("dFechaSolicitud", solicitudPago.getFechaSolicitud())
                .addValue("folio", solicitudPago.getFolio());

        try {
            Map<String, Object> result = jdbcCall.execute(inParams);
            System.out.println("Mensaje de Respuesta: " + result.get("mensajeRespuesta"));
            return true;
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return false;
            //return 0;
        }
    }

@Override
public List<SolicitudDTO> filtrarSolicitudesPago(String texto) {
    if(texto.equalsIgnoreCase("null")){
        texto = null;
    }
    String sql = "SELECT * FROM paf.fn_FiltrarSolicitudesPago(?)";
    List<SolicitudDTO> solicitudesPago = new ArrayList<>();
    try{
    List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, new Object[]{texto});
    
    
    for (Map<String, Object> row : results) {
        SolicitudDTO solicitudPagoDTO = new SolicitudDTO();
        if (row.get("idSolicitud") != null) solicitudPagoDTO.setIdSolicitud((Integer) row.get("idSolicitud"));
        if (row.get("idProceso") != null) solicitudPagoDTO.setIdProceso((Integer) row.get("idProceso"));
        if (row.get("idUsuario") != null) solicitudPagoDTO.setIdUsuario((Integer) row.get("idUsuario"));
        if (row.get("idTipoCausante") != null) solicitudPagoDTO.setIdTipoCausante((Integer) row.get("idTipoCausante"));
        if (row.get("idComuna") != null) solicitudPagoDTO.setIdComuna((Integer) row.get("idComuna"));
        if (row.get("idInstitucion") != null) solicitudPagoDTO.setIdInstitucion((Integer) row.get("idInstitucion"));
        if (row.get("idHaber") != null) solicitudPagoDTO.setIdHaber((Integer) row.get("idHaber"));
        if (row.get("idFormaPago") != null) solicitudPagoDTO.setIdFormaPago((Integer) row.get("idFormaPago"));
        if (row.get("periodo") != null) solicitudPagoDTO.setPeriodo((String) row.get("periodo"));
        if (row.get("codigo") != null) solicitudPagoDTO.setCode((String) row.get("codigo"));
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
        if (row.get("rutCausante") != null) solicitudPagoDTO.setRutCausante((Integer) row.get("rutCausante"));
        if (row.get("dvCausante") != null) solicitudPagoDTO.setDvCausante((String) row.get("dvCausante"));
        if (row.get("nombresCausante") != null) solicitudPagoDTO.setNombresCausante((String) row.get("nombresCausante"));
        if (row.get("apellidoPaternoCausante") != null) solicitudPagoDTO.setApellidoPaternoCausante((String) row.get("apellidoPaternoCausante"));
        if (row.get("apellidoMaternoCausante") != null) solicitudPagoDTO.setApellidoMaternoCausante((String) row.get("apellidoMaternoCausante"));
        if (row.get("rutReteniente") != null) solicitudPagoDTO.setRutReteniente((Integer) row.get("rutReteniente"));
        if (row.get("dvReteniente") != null) solicitudPagoDTO.setDvReteniente((String) row.get("dvReteniente"));
        if (row.get("nombresReteniente") != null) solicitudPagoDTO.setNombresReteniente((String) row.get("nombresReteniente"));
        if (row.get("apellidoPaternoReteniente") != null) solicitudPagoDTO.setApellidoPaternoReteniente((String) row.get("apellidoPaternoReteniente"));
        if (row.get("apellidoMaternoReteniente") != null) solicitudPagoDTO.setApellidoMaternoReteniente((String) row.get("apellidoMaternoReteniente"));
        if (row.get("inicioCompensacion") != null) solicitudPagoDTO.setInicioCompensacion((Date) row.get("inicioCompensacion"));
        if (row.get("finCompensacion") != null) solicitudPagoDTO.setFinCompensacion((Date) row.get("finCompensacion"));
        if (row.get("filePathPrevired") != null) solicitudPagoDTO.setFilePathPrevired((String) row.get("filePathPrevired"));
        if (row.get("filePathEspecial") != null) solicitudPagoDTO.setFilePathEspecial((String) row.get("filePathEspecial"));
        if (row.get("origen") != null) solicitudPagoDTO.setOrigen((Integer) row.get("origen"));
        if (row.get("tipoSolicitante") != null) solicitudPagoDTO.setTipoSolicitante((Integer) row.get("tipoSolicitante"));
        if (row.get("subTipoSolicitante") != null) solicitudPagoDTO.setSubTipoSolicitante((Integer) row.get("subTipoSolicitante"));
        if (row.get("observaciones") != null) solicitudPagoDTO.setObservaciones((String) row.get("observaciones"));
        if (row.get("montoReconocimientoDerecho") != null) solicitudPagoDTO.setMontoReconocimientoDerecho((BigDecimal) row.get("montoReconocimientoDerecho"));
        if (row.get("montoReconocimientoEfectivo") != null) solicitudPagoDTO.setMontoReconocimientoEfectivo((BigDecimal) row.get("montoReconocimientoEfectivo"));
        if (row.get("conRetencionJudicial") != null) solicitudPagoDTO.setConRetencionJudicial((String) row.get("conRetencionJudicial"));
        if (row.get("invalida") != null) solicitudPagoDTO.setInvalida((String) row.get("invalida"));
        if (row.get("emitida") != null) solicitudPagoDTO.setEmitida((String) row.get("emitida"));
        if (row.get("idCuentaBancaria") != null) solicitudPagoDTO.setIdCuentaBancaria((Integer) row.get("idCuentaBancaria"));
        if (row.get("telefono") != null) solicitudPagoDTO.setTelefono((Integer) row.get("telefono"));
        if (row.get("idRegion") != null) solicitudPagoDTO.setIdRegion((Integer) row.get("idRegion"));
        if (row.get("finiquito") != null) solicitudPagoDTO.setFiniquito((String) row.get("finiquito"));
        if (row.get("fechaFiniquito") != null) solicitudPagoDTO.setFechaFiniquito((Date) row.get("fechaFiniquito"));
        if (row.get("rutRepresentante") != null) solicitudPagoDTO.setRutRepresentante((Integer) row.get("rutRepresentante"));
        if (row.get("dvRepresentante") != null) solicitudPagoDTO.setDvRepresentante((String) row.get("dvRepresentante"));
        if (row.get("estado") != null) solicitudPagoDTO.setEstado((String) row.get("estado"));
        if (row.get("nombreOrigen") != null) solicitudPagoDTO.setNombreOrigen((String) row.get("nombreOrigen"));
        solicitudesPago.add(solicitudPagoDTO);
    }
    }catch(Exception e){
        System.out.println("Error al filtrar solicitud: "+e.getMessage());
        return null;
    }

    return solicitudesPago;
}

@Override
public List<SolicitudDTO> obtenerSolicitudesPorProceso(int idProceso, int tipoSolicitante) {
    String sql = "SELECT * FROM paf.fn_ObtenerSolicitudesPorProceso(?, ?)";

    List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, idProceso, tipoSolicitante);

    List<SolicitudDTO> solicitudes = new ArrayList<>();

    List<SolicitudDTO> solicitudesPago = new ArrayList<>();
        for (Map<String, Object> row : results) {
            SolicitudDTO solicitudPagoDTO = new SolicitudDTO();
            if (row.get("idSolicitud") != null) solicitudPagoDTO.setIdSolicitud((Integer) row.get("idSolicitud"));
            if (row.get("idProceso") != null) solicitudPagoDTO.setIdProceso((Integer) row.get("idProceso"));
            if (row.get("idUsuario") != null) solicitudPagoDTO.setIdUsuario((Integer) row.get("idUsuario"));
            if (row.get("idTipoCausante") != null) solicitudPagoDTO.setIdTipoCausante((Integer) row.get("idTipoCausante"));
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
            if (row.get("rutCausante") != null) solicitudPagoDTO.setRutCausante((Integer) row.get("rutCausante"));
            if (row.get("dvCausante") != null) solicitudPagoDTO.setDvCausante((String) row.get("dvCausante"));
            if (row.get("nombresCausante") != null) solicitudPagoDTO.setNombresCausante((String) row.get("nombresCausante"));
            if (row.get("apellidoPaternoCausante") != null) solicitudPagoDTO.setApellidoPaternoCausante((String) row.get("apellidoPaternoCausante"));
            if (row.get("apellidoMaternoCausante") != null) solicitudPagoDTO.setApellidoMaternoCausante((String) row.get("apellidoMaternoCausante"));
            if (row.get("rutReteniente") != null) solicitudPagoDTO.setRutReteniente((Integer) row.get("rutReteniente"));
            if (row.get("dvReteniente") != null) solicitudPagoDTO.setDvReteniente((String) row.get("dvReteniente"));
            if (row.get("nombresReteniente") != null) solicitudPagoDTO.setNombresReteniente((String) row.get("nombresReteniente"));
            if (row.get("apellidoPaternoReteniente") != null) solicitudPagoDTO.setApellidoPaternoReteniente((String) row.get("apellidoPaternoReteniente"));
            if (row.get("apellidoMaternoReteniente") != null) solicitudPagoDTO.setApellidoMaternoReteniente((String) row.get("apellidoMaternoReteniente"));
            if (row.get("inicioCompensacion") != null) solicitudPagoDTO.setInicioCompensacion((Date) row.get("inicioCompensacion"));
            if (row.get("finCompensacion") != null) solicitudPagoDTO.setFinCompensacion((Date) row.get("finCompensacion"));
            if (row.get("filePathPrevired") != null) solicitudPagoDTO.setFilePathPrevired((String) row.get("filePathPrevired"));
            if (row.get("filePathEspecial") != null) solicitudPagoDTO.setFilePathEspecial((String) row.get("filePathEspecial"));
            if (row.get("origen") != null) solicitudPagoDTO.setOrigen((Integer) row.get("origen"));
            if (row.get("tipoSolicitante") != null) solicitudPagoDTO.setTipoSolicitante((Integer) row.get("tipoSolicitante"));
            if (row.get("subTipoSolicitante") != null) solicitudPagoDTO.setSubTipoSolicitante((Integer) row.get("subTipoSolicitante"));
            if (row.get("observaciones") != null) solicitudPagoDTO.setObservaciones((String) row.get("observaciones"));
            if (row.get("montoReconocimientoDerecho") != null) solicitudPagoDTO.setMontoReconocimientoDerecho((BigDecimal) row.get("montoReconocimientoDerecho"));
            if (row.get("montoReconocimientoEfectivo") != null) solicitudPagoDTO.setMontoReconocimientoEfectivo((BigDecimal) row.get("montoReconocimientoEfectivo"));
            if (row.get("conRetencionJudicial") != null) solicitudPagoDTO.setConRetencionJudicial((String) row.get("conRetencionJudicial"));
            if (row.get("invalida") != null) solicitudPagoDTO.setInvalida((String) row.get("invalida"));
            if (row.get("emitida") != null) solicitudPagoDTO.setEmitida((String) row.get("emitida"));
            if (row.get("idCuentaBancaria") != null) solicitudPagoDTO.setIdCuentaBancaria((Integer) row.get("idCuentaBancaria"));
            if (row.get("telefono") != null) solicitudPagoDTO.setTelefono((Integer) row.get("telefono"));
            if (row.get("idRegion") != null) solicitudPagoDTO.setIdRegion((Integer) row.get("idRegion"));
            if (row.get("finiquito") != null) solicitudPagoDTO.setFiniquito((String) row.get("finiquito"));
            if (row.get("fechaFiniquito") != null) solicitudPagoDTO.setFechaFiniquito((Date) row.get("fechaFiniquito"));
            if (row.get("rutRepresentante") != null) solicitudPagoDTO.setRutRepresentante((Integer) row.get("rutRepresentante"));
            if (row.get("dvRepresentante") != null) solicitudPagoDTO.setDvRepresentante((String) row.get("dvRepresentante"));
            if (row.get("fechaSolicitud") != null) solicitudPagoDTO.setFechaSolicitud((Date) row.get("fechaSolicitud"));
            if (row.get("folio") != null) solicitudPagoDTO.setFolio((Long) row.get("folio"));
            if (row.get("estado") != null) solicitudPagoDTO.setEstado((String) row.get("estado"));
            if (row.get("nombreOrigen") != null) solicitudPagoDTO.setNombreOrigen((String) row.get("nombreOrigen"));
            solicitudesPago.add(solicitudPagoDTO);
        }

        return solicitudesPago;
}

@Override
public boolean actualizarIdProceso(int iIdSolicitud, int nuevoIdProceso) {
    SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withSchemaName(esquema)
            .withProcedureName("sp_ActualizarIdProceso")
            .declareParameters(
                    new SqlParameter("iIdSolicitud", Types.INTEGER),
                    new SqlParameter("nuevoIdProceso", Types.INTEGER),
                    new SqlOutParameter("mensajeRespuesta", Types.VARCHAR),
                    new SqlOutParameter("codigoRespuesta", Types.INTEGER)
            );

    MapSqlParameterSource inParams = new MapSqlParameterSource()
            .addValue("iIdSolicitud", iIdSolicitud)
            .addValue("nuevoIdProceso", nuevoIdProceso);

    try {
        Map<String, Object> result = jdbcCall.execute(inParams);
        String mensajeRespuesta = (String) result.get("mensajeRespuesta");
        Integer codigoRespuesta = (Integer) result.get("codigoRespuesta");

        System.out.println("Mensaje de Respuesta: " + mensajeRespuesta);
        return codigoRespuesta == 0;
    } catch (Exception e) {
        System.out.println("ERROR: " + e.getMessage());
        return false;
    }
}

@Override
public int insertarResolucion(ResolucionDTO resolucionDTO) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withSchemaName(esquema)
                .withProcedureName("SP_InsertarResolucion")
                .declareParameters(
                        new SqlParameter("iIdSolicitud", Types.INTEGER),
                        new SqlParameter("iIdEstado", Types.INTEGER),
                        new SqlParameter("vcDescripcion", Types.VARCHAR),
                        new SqlParameter("vcApelable", Types.VARCHAR),
                        new SqlParameter("iAutor", Types.INTEGER),
                        new SqlParameter("dFechaAval", Types.DATE),
                        new SqlParameter("dFechaApelacion", Types.DATE),
                        new SqlParameter("vcVigente", Types.VARCHAR),
                        new SqlOutParameter("idResolucion", Types.INTEGER),
                        new SqlOutParameter("mensajeRespuesta", Types.VARCHAR),
                        new SqlOutParameter("codigoRespuesta", Types.INTEGER)
                );

        MapSqlParameterSource inParams = new MapSqlParameterSource()
                .addValue("iIdSolicitud", resolucionDTO.getIIdSolicitud())
                .addValue("iIdEstado", resolucionDTO.getIIdEstado())
                .addValue("vcDescripcion", resolucionDTO.getVcDescripcion())
                .addValue("vcApelable", resolucionDTO.getVcApelable())
                .addValue("iAutor", resolucionDTO.getIAutor())
                .addValue("dFechaAval", resolucionDTO.getDFechaAval())
                .addValue("dFechaApelacion", resolucionDTO.getDFechaApelacion())
                .addValue("vcVigente", resolucionDTO.getVcVigente());

        try {
            Map<String, Object> result = jdbcCall.execute(inParams);
            System.out.println("Mensaje de Respuesta: " + result.get("mensajeRespuesta"));
            return (Integer) result.get("idResolucion");
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return 0;
        }
    }

@Override
public List<TipoSolicitanteDTO> obtenerTiposSolicitante() {
    String sql = "SELECT idTipoSolicitante, nombre FROM paf.vw_TipoSolicitante";

    List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);

    List<TipoSolicitanteDTO> tiposSolicitante = new ArrayList<>();
    for (Map<String, Object> row : results) {
        TipoSolicitanteDTO tipoSolicitanteDTO = new TipoSolicitanteDTO();
        if (row.get("idTipoSolicitante") != null) tipoSolicitanteDTO.setIdTipoSolicitante((Integer) row.get("idTipoSolicitante"));
        if (row.get("nombre") != null) tipoSolicitanteDTO.setNombre((String) row.get("nombre"));
        tiposSolicitante.add(tipoSolicitanteDTO);
    }

    return tiposSolicitante;
}

@Override
public List<CausanteSolicitudDTO> obtenerCausantesPorSolicitud(int idSolicitud) {
    String sql = "SELECT * FROM paf.fn_ObtenerCausanteSolicitud(?)";

    List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, idSolicitud);

    List<CausanteSolicitudDTO> causantesSolicitud = new ArrayList<>();

    for (Map<String, Object> row : results) {
        CausanteSolicitudDTO causanteSolicitudDTO = new CausanteSolicitudDTO();

        causanteSolicitudDTO.setIIdCausanteSolicitud((Integer) row.get("iIdCausanteSolicitud"));
        causanteSolicitudDTO.setIIdSolicitud((Integer) row.get("iIdSolicitud"));
        causanteSolicitudDTO.setRutBeneficiario((Integer) row.get("iRutBeneficiario"));
        causanteSolicitudDTO.setVcDvBeneficiario((String) row.get("vcDvBeneficiario"));
        causanteSolicitudDTO.setRutCausante((Integer) row.get("iRutCausante"));
        causanteSolicitudDTO.setVcDvCausante((String) row.get("vcDvCausante"));
        causanteSolicitudDTO.setVcNombreCausante((String) row.get("vcNombreCausante"));
        causanteSolicitudDTO.setFechaInicioRango((Date) row.get("dFechaInicioRango"));
        causanteSolicitudDTO.setFechaFinRango((Date) row.get("dFechaFinRango"));
        causanteSolicitudDTO.setVcPeriodosAprobados((String) row.get("vcPeriodosAprobados"));
        causanteSolicitudDTO.setTotalPagar((BigDecimal) row.get("mTotalPagar"));

        causantesSolicitud.add(causanteSolicitudDTO);
    }

    return causantesSolicitud;
}

@Override
public boolean eliminarCausanteSolicitud(int idCausanteSolicitud) {
    SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withSchemaName(esquema)
            .withProcedureName("SP_EliminarCausanteSolicitud")
            .declareParameters(
                    new SqlParameter("iIdCausanteSolicitud", Types.INTEGER)
            );

    MapSqlParameterSource inParams = new MapSqlParameterSource()
            .addValue("iIdCausanteSolicitud", idCausanteSolicitud);

    try {
        jdbcCall.execute(inParams);
        System.out.println("Causante con ID " + idCausanteSolicitud + " eliminado correctamente.");
        return true;
    } catch (Exception e) {
        System.out.println("ERROR: " + e.getMessage());
        return false;
    }
}


}