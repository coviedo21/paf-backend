package cl.gob.ips.solicitudes_pago.dao.daoImpl;

import cl.gob.ips.solicitudes_pago.dto.CausanteSolicitudDTO;
import cl.gob.ips.solicitudes_pago.dto.DetalleCausanteDTO;
import cl.gob.ips.solicitudes_pago.dto.MotivoRechazoDTO;
import cl.gob.ips.solicitudes_pago.dto.OrigenArchivoDTO;
//import cl.gob.ips.solicitudes_pago.dto.ProcesoDTO;
import cl.gob.ips.solicitudes_pago.dto.ResolucionDTO;
import cl.gob.ips.solicitudes_pago.dto.ResponseDTO;
import cl.gob.ips.solicitudes_pago.dto.SolicitudDTO;
import cl.gob.ips.solicitudes_pago.dto.TipoSolicitanteDTO;
import cl.gob.ips.solicitudes_pago.service.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import cl.gob.ips.solicitudes_pago.dao.CausanteDAO;
import cl.gob.ips.solicitudes_pago.dao.SolicitudPagoDAO;

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

    @Autowired
    EmailService emailService;

    @Autowired
    CausanteDAO causanteDAO;

    private final JdbcTemplate jdbcTemplate;

    @Value("${spring.datasource.schema}")
    private String esquema;

    @Autowired
    public SolicitudPagoDAOImpl(@Qualifier("pafJdbc") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ResponseDTO insertarSolicitudPago(SolicitudDTO solicitudPago, boolean esArchivo) {
        ResponseDTO response = new ResponseDTO();
        // Validación previa: Verificar duplicados
    SimpleJdbcCall validarDuplicadosCall = new SimpleJdbcCall(jdbcTemplate)
    .withSchemaName(esquema)
    .withProcedureName("SP_ValidarDuplicadosSolicitudCausante")
    .declareParameters(
            new SqlParameter("iRutBeneficiario", Types.INTEGER),
            new SqlParameter("vcPeriodo", Types.VARCHAR),
            new SqlParameter("iRutCausante", Types.INTEGER),
            new SqlOutParameter("mensajeRespuesta", Types.VARCHAR),
            new SqlOutParameter("idSolicitud", Types.INTEGER)
    );

    try {
    for (CausanteSolicitudDTO causante : solicitudPago.getListaCausantes()) {
        MapSqlParameterSource inParams = new MapSqlParameterSource()
                .addValue("iRutBeneficiario", solicitudPago.getRutBeneficiario())
                .addValue("vcPeriodo", solicitudPago.getPeriodo())
                .addValue("iRutCausante", causante.getRutCausante());

        Map<String, Object> validationResult = validarDuplicadosCall.execute(inParams);
        int idSolicitud = (int) validationResult.get("idSolicitud");
        String mensajeRespuesta = (String) validationResult.get("mensajeRespuesta");

        // Si se detecta un duplicado
        if(idSolicitud>0){
            if(esArchivo){
                SolicitudDTO solicitud = consultarSolicitudPago(idSolicitud).get(0);
                if(solicitud.getIdEstado()<4){
                    if(solicitud.getFechaSolicitud().after(solicitudPago.getFechaSolicitud())){
                        //Fecha menor es del archivo, se rechaza la que ya existe.
                        ResolucionDTO resolucion = new ResolucionDTO();
                        resolucion.setIIdSolicitud(idSolicitud);
                        resolucion.setIAutor(1);
                        resolucion.setIIdEstado(4);
                        resolucion.setVcDescripcion("Ya existe solicitud.");
                        resolucion.setIMotivoRechazo(4);
                        insertarResolucion(resolucion);
                        try {
                            emailService.enviarCorreo(solicitud.getEmail(),"Solicitud "+idSolicitud+" rechazada.","Su solicitud N° "+idSolicitud+" ha sido rechazada. Motivo de Rechazo:  Solicitud ya existe");    
                            response.setCodigoRetorno(3);
                            response.setGlosaRetorno("Se inserta solicitud y se rechaza la existente");
                            response.setResultado(0);
                            //return response; // Detener si se encuentra un duplicado
                        } catch (Exception e) {
                            // Captura cualquier excepción relacionada con el envío del correo y loguea el error
                            System.err.println("Error enviando correo para la solicitud " + solicitud.getIdSolicitud() + ": " + e.getMessage());
                        }   
                    }else{
                        System.out.println("Validación fallida: " + mensajeRespuesta);
                        response.setCodigoRetorno(3);
                        response.setGlosaRetorno(mensajeRespuesta);
                        response.setResultado(0);
                        return response; // Detener si se encuentra un duplicado        
                    }
                }
            }
            else{
                System.out.println("Validación fallida: " + mensajeRespuesta);
                response.setCodigoRetorno(3);
                response.setGlosaRetorno(mensajeRespuesta);
                response.setResultado(0);
                return response; // Detener si se encuentra un duplicado
            }
            
        }
    }
    } catch (Exception e) {
        response.setCodigoRetorno(1);
        response.setGlosaRetorno("ERROR");
        response.setResultado(0);
        return response; // Detener si se encuentra un duplicado
    }
        
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withSchemaName(esquema)
                .withProcedureName("SP_InsertarSolicitudPago")
                .declareParameters(
                        new SqlParameter("iIdProceso", Types.INTEGER),
                        new SqlParameter("iIdUsuario", Types.INTEGER),
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
                        new SqlParameter("iRutReteniente", Types.INTEGER),
                        new SqlParameter("vcDvReteniente", Types.VARCHAR),
                        new SqlParameter("vcNombresReteniente", Types.VARCHAR),
                        new SqlParameter("vcApellidoPaternoReteniente", Types.VARCHAR),
                        new SqlParameter("vcApellidoMaternoReteniente", Types.VARCHAR),
                        new SqlParameter("vcFilePathPrevired", Types.VARCHAR),
                        new SqlParameter("vcFilePathEspecial", Types.VARCHAR),
                        new SqlParameter("iOrigen", Types.INTEGER),
                        new SqlParameter("iTipoSolicitante", Types.INTEGER),
                        new SqlParameter("vcObservaciones", Types.VARCHAR),
                        new SqlParameter("conRetencionJudicial", Types.VARCHAR),
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
                        new SqlParameter("vcNombreRegion", Types.VARCHAR),
                        new SqlParameter("vcCiudad", Types.VARCHAR),
                        new SqlOutParameter("idSolicitud", Types.INTEGER),
                        new SqlOutParameter("mensajeRespuesta", Types.VARCHAR)
                );

        MapSqlParameterSource inParams = new MapSqlParameterSource()
                .addValue("iIdProceso", solicitudPago.getIdProceso()>0?solicitudPago.getIdProceso():null)
                .addValue("iIdUsuario", solicitudPago.getIdUsuario())
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
                .addValue("iRutReteniente", solicitudPago.getRutReteniente())
                .addValue("vcDvReteniente", solicitudPago.getDvReteniente())
                .addValue("vcNombresReteniente", solicitudPago.getNombresReteniente())
                .addValue("vcApellidoPaternoReteniente", solicitudPago.getApellidoPaternoReteniente())
                .addValue("vcApellidoMaternoReteniente", solicitudPago.getApellidoMaternoReteniente())
                .addValue("vcFilePathPrevired", solicitudPago.getFilePathPrevired())
                .addValue("vcFilePathEspecial", solicitudPago.getFilePathEspecial())
                .addValue("iOrigen", solicitudPago.getOrigen())
                .addValue("iTipoSolicitante", solicitudPago.getTipoSolicitante()>0?solicitudPago.getTipoSolicitante():null)
                .addValue("vcObservaciones", solicitudPago.getObservaciones())
                .addValue("conRetencionJudicial", solicitudPago.getConRetencionJudicial())
                .addValue("iIdCuentaBancaria", solicitudPago.getIdCuentaBancaria())
                .addValue("iTelefono", solicitudPago.getTelefono())
                .addValue("iIdRegion", solicitudPago.getIdRegion())
                .addValue("vcFiniquito", solicitudPago.getFiniquito())
                .addValue("dFechaFiniquito", solicitudPago.getFechaFiniquito())
                .addValue("iRutRepresentante", solicitudPago.getRutRepresentante())
                .addValue("vcDvRepresentante", solicitudPago.getDvRepresentante())
                .addValue("dFechaSolicitud", solicitudPago.getFechaSolicitud()!=null?solicitudPago.getFechaSolicitud():LocalDateTime.now())
                .addValue("folio", solicitudPago.getFolio())
                .addValue("vcNombreComuna", solicitudPago.getNombreComuna())
                .addValue("vcNombreRegion", solicitudPago.getNombreRegion())
                .addValue("vcCiudad", solicitudPago.getCiudadEmpleador());

        try {
            Map<String, Object> result = jdbcCall.execute(inParams);
            Integer idSolicitud = (Integer) result.get("idSolicitud");
            String mensajeRespuesta = (String) result.get("mensajeRespuesta");

            if (idSolicitud != null && idSolicitud > 0) {
                System.out.println("Solicitud insertada correctamente con ID: " + idSolicitud);
                // Si la solicitud se inserta correctamente, insertamos los causantes
                insertarCausantesSolicitud(idSolicitud, solicitudPago.getListaCausantes());
                response.setCodigoRetorno(0);
                response.setGlosaRetorno("Solicitud creada exitósamente. ID: "+idSolicitud);
                response.setResultado(idSolicitud);
                return response; // Detener si se encuentra un duplicado
                //return idSolicitud;
            } else {
                response.setCodigoRetorno(1);
                response.setGlosaRetorno("Error al insertar la solicitud: " + mensajeRespuesta);
                response.setResultado(0);
                return response; // Detener si se encuentra un duplicado
                //System.out.println("Error al insertar la solicitud: " + mensajeRespuesta);
                //return 0;
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            response.setCodigoRetorno(1);
                response.setGlosaRetorno("ERROR: " + e.getMessage());
                response.setResultado(0);
                return response; // Detener si se encuentra un duplicado
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
                            new SqlOutParameter("iIdCausanteSolicitud", Types.INTEGER),
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
                // Obtener el ID del causante insertado
                Integer idCausanteSolicitud = (Integer) resultCausante.get("iIdCausanteSolicitud");

                for (DetalleCausanteDTO detalle : causante.getDetalle()) {
                    detalle.setIdCausanteSolicitud(idCausanteSolicitud);
                    String insertarCausante = causanteDAO.insertarDetalleCausante(detalle);
                    System.out.println(insertarCausante);
                }
            } catch (Exception e) {
                System.out.println("ERROR al insertar causante: " + e.getMessage());
            }
        }
    }    


    //Obtener solicitudes de pago por id o todas
    @Override
    public List<SolicitudDTO> consultarSolicitudPago(Integer idSolicitud) {
        String sql = "SELECT * FROM paf.fn_ObtenerSolicitudesPago(?)";
        ///String sqlCausantes = "SELECT * FROM paf.fn_ObtenerCausanteSolicitud(?)";

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, new Object[]{idSolicitud});

        List<SolicitudDTO> solicitudesPago = new ArrayList<>();
        for (Map<String, Object> row : results) {
            SolicitudDTO solicitudPagoDTO = new SolicitudDTO();
            if (row.get("idSolicitud") != null) solicitudPagoDTO.setIdSolicitud((Integer) row.get("idSolicitud"));
            if (row.get("idProceso") != null) solicitudPagoDTO.setIdProceso((Integer) row.get("idProceso"));
            if (row.get("idUsuario") != null) solicitudPagoDTO.setIdUsuario((Integer) row.get("idUsuario"));
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
            if (row.get("rutReteniente") != null) solicitudPagoDTO.setRutReteniente((Integer) row.get("rutReteniente"));
            if (row.get("dvReteniente") != null) solicitudPagoDTO.setDvReteniente((String) row.get("dvReteniente"));
            if (row.get("nombresReteniente") != null) solicitudPagoDTO.setNombresReteniente((String) row.get("nombresReteniente"));
            if (row.get("apellidoPaternoReteniente") != null) solicitudPagoDTO.setApellidoPaternoReteniente((String) row.get("apellidoPaternoReteniente"));
            if (row.get("apellidoMaternoReteniente") != null) solicitudPagoDTO.setApellidoMaternoReteniente((String) row.get("apellidoMaternoReteniente"));
            if (row.get("filePathPrevired") != null) solicitudPagoDTO.setFilePathPrevired((String) row.get("filePathPrevired"));
            if (row.get("filePathEspecial") != null) solicitudPagoDTO.setFilePathEspecial((String) row.get("filePathEspecial"));
            if (row.get("origen") != null) solicitudPagoDTO.setOrigen((Integer) row.get("origen"));
            if (row.get("tipoSolicitante") != null) solicitudPagoDTO.setTipoSolicitante((Integer) row.get("tipoSolicitante"));
            if (row.get("nombreTipoSolicitante") != null) solicitudPagoDTO.setNombreTipoSolicitante((String) row.get("nombreTipoSolicitante"));
            if (row.get("observaciones") != null) solicitudPagoDTO.setObservaciones((String) row.get("observaciones"));
            if (row.get("conRetencionJudicial") != null) solicitudPagoDTO.setConRetencionJudicial((String) row.get("conRetencionJudicial"));
            if (row.get("idCuentaBancaria") != null) solicitudPagoDTO.setIdCuentaBancaria((Integer) row.get("idCuentaBancaria"));
            if (row.get("telefono") != null) solicitudPagoDTO.setTelefono((Integer) row.get("telefono"));
            if (row.get("idRegion") != null) solicitudPagoDTO.setIdRegion((Integer) row.get("idRegion"));
            if (row.get("finiquito") != null) solicitudPagoDTO.setFiniquito((String) row.get("finiquito"));
            if (row.get("fechaFiniquito") != null) solicitudPagoDTO.setFechaFiniquito((Date) row.get("fechaFiniquito"));
            if (row.get("rutRepresentante") != null) solicitudPagoDTO.setRutRepresentante((Integer) row.get("rutRepresentante"));
            if (row.get("dvRepresentante") != null) solicitudPagoDTO.setDvRepresentante((String) row.get("dvRepresentante"));
            if (row.get("fechaSolicitud") != null) solicitudPagoDTO.setFechaSolicitud((Date) row.get("fechaSolicitud"));
            if (row.get("folio") != null) solicitudPagoDTO.setFolio((Long) row.get("folio"));
            if (row.get("idEstado") != null) solicitudPagoDTO.setIdEstado((Integer) row.get("idEstado"));
            if (row.get("estado") != null) solicitudPagoDTO.setEstado((String) row.get("estado"));
            if (row.get("nombreOrigen") != null) solicitudPagoDTO.setNombreOrigen((String) row.get("nombreOrigen"));
            if (row.get("cumpleCriterios") != null) solicitudPagoDTO.setCumpleCriterios((String) row.get("cumpleCriterios"));
            if (row.get("nombreRegion") != null) solicitudPagoDTO.setNombreRegion((String) row.get("nombreRegion"));
            if (row.get("ciudad") != null) solicitudPagoDTO.setCiudadEmpleador((String) row.get("ciudad"));
            if (row.get("motivoRechazo") != null) solicitudPagoDTO.setMotivoRechazo((String) row.get("motivoRechazo"));
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

    //Actualización de solicitudes de pago
    @Override
    public boolean actualizarSolicitudPago(SolicitudDTO solicitudPago) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withSchemaName(esquema)
                .withProcedureName("SP_ActualizarSolicitudPago")
                .declareParameters(
                        new SqlParameter("iIdSolicitud", Types.INTEGER),
                        new SqlParameter("iIdProceso", Types.INTEGER),
                        new SqlParameter("iIdUsuario", Types.INTEGER),
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
                        new SqlParameter("iRutReteniente", Types.INTEGER),
                        new SqlParameter("vcDvReteniente", Types.VARCHAR),
                        new SqlParameter("vcNombresReteniente", Types.VARCHAR),
                        new SqlParameter("vcApellidoPaternoReteniente", Types.VARCHAR),
                        new SqlParameter("vcApellidoMaternoReteniente", Types.VARCHAR),
                        new SqlParameter("vcFilePathPrevired", Types.VARCHAR),
                        new SqlParameter("vcFilePathEspecial", Types.VARCHAR),
                        new SqlParameter("iOrigen", Types.INTEGER),
                        new SqlParameter("iTipoSolicitante", Types.INTEGER),
                        new SqlParameter("vcObservaciones", Types.VARCHAR),
                        new SqlParameter("conRetencionJudicial", Types.VARCHAR),
                        new SqlParameter("iIdCuentaBancaria", Types.INTEGER),
                        new SqlParameter("iTelefono", Types.INTEGER),
                        new SqlParameter("iIdRegion", Types.INTEGER),
                        new SqlParameter("vcFiniquito", Types.VARCHAR),
                        new SqlParameter("dFechaFiniquito", Types.DATE),
                        new SqlParameter("iRutRepresentante", Types.INTEGER),
                        new SqlParameter("vcDvRepresentante", Types.VARCHAR),
                        new SqlParameter("dFechaSolicitud", Types.TIMESTAMP),
                        new SqlParameter("folio", Types.BIGINT),
                        new SqlParameter("nombreComuna", Types.VARCHAR),
                        new SqlParameter("cumpleCriterios", Types.VARCHAR),
                        new SqlParameter("nombreRegion", Types.VARCHAR),
                        new SqlOutParameter("mensajeRespuesta", Types.VARCHAR)
                );

        MapSqlParameterSource inParams = new MapSqlParameterSource()
                .addValue("iIdSolicitud", solicitudPago.getIdSolicitud())
                .addValue("iIdProceso", solicitudPago.getIdProceso() > 0 ? solicitudPago.getIdProceso() : null)
                .addValue("iIdUsuario", solicitudPago.getIdUsuario() > 0 ? solicitudPago.getIdUsuario(): null)
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
                .addValue("iRutReteniente", solicitudPago.getRutReteniente())
                .addValue("vcDvReteniente", solicitudPago.getDvReteniente())
                .addValue("vcNombresReteniente", solicitudPago.getNombresReteniente())
                .addValue("vcApellidoPaternoReteniente", solicitudPago.getApellidoPaternoReteniente())
                .addValue("vcApellidoMaternoReteniente", solicitudPago.getApellidoMaternoReteniente())
                .addValue("vcFilePathPrevired", solicitudPago.getFilePathPrevired())
                .addValue("vcFilePathEspecial", solicitudPago.getFilePathEspecial())
                .addValue("iOrigen", solicitudPago.getOrigen()>0?solicitudPago.getOrigen():null)
                .addValue("iTipoSolicitante", solicitudPago.getTipoSolicitante() > 0 ? solicitudPago.getTipoSolicitante() : null)
                .addValue("vcObservaciones", solicitudPago.getObservaciones())
                .addValue("conRetencionJudicial", solicitudPago.getConRetencionJudicial())
                .addValue("iIdCuentaBancaria", solicitudPago.getIdCuentaBancaria())
                .addValue("iTelefono", solicitudPago.getTelefono())
                .addValue("iIdRegion", solicitudPago.getIdRegion())
                .addValue("vcFiniquito", solicitudPago.getFiniquito())
                .addValue("dFechaFiniquito", solicitudPago.getFechaFiniquito())
                .addValue("iRutRepresentante", solicitudPago.getRutRepresentante())
                .addValue("vcDvRepresentante", solicitudPago.getDvRepresentante())
                .addValue("dFechaSolicitud", solicitudPago.getFechaSolicitud())
                .addValue("folio", solicitudPago.getFolio())
                .addValue("nombreComuna", solicitudPago.getNombreComuna())
                .addValue("cumpleCriterios", solicitudPago.getCumpleCriterios())
                .addValue("nombreRegion", solicitudPago.getNombreRegion());

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
            if (row.get("estado") != null) solicitudPagoDTO.setEstado((String) row.get("estado"));
            if (row.get("nombreOrigen") != null) solicitudPagoDTO.setNombreOrigen((String) row.get("nombreOrigen"));
            if (row.get("nombreRegion") != null) solicitudPagoDTO.setNombreRegion((String) row.get("nombreRegion"));
                if (row.get("ciudad") != null) solicitudPagoDTO.setCiudadEmpleador((String) row.get("ciudad"));
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
                if (row.get("fechaSolicitud") != null) solicitudPagoDTO.setFechaSolicitud((Date) row.get("fechaSolicitud"));
                if (row.get("folio") != null) solicitudPagoDTO.setFolio((Long) row.get("folio"));
                if (row.get("estado") != null) solicitudPagoDTO.setEstado((String) row.get("estado"));
                if (row.get("nombreOrigen") != null) solicitudPagoDTO.setNombreOrigen((String) row.get("nombreOrigen"));
                if (row.get("nombreRegion") != null) solicitudPagoDTO.setNombreRegion((String) row.get("nombreRegion"));
                if (row.get("ciudad") != null) solicitudPagoDTO.setCiudadEmpleador((String) row.get("ciudad"));
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
                            new SqlParameter("iMotivoRechazo", Types.INTEGER),
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
                    .addValue("iMotivoRechazo", resolucionDTO.getIMotivoRechazo());

            try {
                Map<String, Object> result = jdbcCall.execute(inParams);
                System.out.println("Resolución creada exitósamente");
                String mensaje = (String) result.get("mensajeRespuesta");
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

    public boolean actualizarCausanteSolicitud(CausanteSolicitudDTO causanteSolicitud) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withSchemaName(esquema)
                .withProcedureName("SP_ActualizarCausanteSolicitud")
                .declareParameters(
                        new SqlParameter("iIdCausanteSolicitud", Types.INTEGER),
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

        MapSqlParameterSource inParams = new MapSqlParameterSource()
                .addValue("iIdCausanteSolicitud", causanteSolicitud.getIIdCausanteSolicitud())
                .addValue("iRutBeneficiario", causanteSolicitud.getRutBeneficiario())
                .addValue("vcDvBeneficiario", causanteSolicitud.getVcDvBeneficiario())
                .addValue("iRutCausante", causanteSolicitud.getRutCausante())
                .addValue("vcDvCausante", causanteSolicitud.getVcDvCausante())
                .addValue("vcNombresCausante", causanteSolicitud.getVcNombreCausante())
                .addValue("dFechaInicioRango", causanteSolicitud.getFechaInicioRango())
                .addValue("dFechaFinRango", causanteSolicitud.getFechaFinRango())
                .addValue("vcPeriodosAprobados", causanteSolicitud.getVcPeriodosAprobados())
                .addValue("mTotalPagar", causanteSolicitud.getTotalPagar());

        try {
            Map<String, Object> result = jdbcCall.execute(inParams);
            String mensajeRespuesta = (String) result.get("mensajeRespuesta");
            System.out.println("Respuesta del procedimiento: " + mensajeRespuesta);
            return true;
        } catch (Exception e) {
            System.out.println("ERROR al actualizar causante: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<SolicitudDTO> obtenerSolicitudesPorEstado(int idEstado) {
        String sql = "SELECT * FROM paf.fn_ObtenerSolicitudesPorEstado(?)";

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, idEstado);

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
                if (row.get("fechaSolicitud") != null) solicitudPagoDTO.setFechaSolicitud((Date) row.get("fechaSolicitud"));
                if (row.get("folio") != null) solicitudPagoDTO.setFolio((Long) row.get("folio"));
                if (row.get("estado") != null) solicitudPagoDTO.setEstado((String) row.get("estado"));
                if (row.get("nombreOrigen") != null) solicitudPagoDTO.setNombreOrigen((String) row.get("nombreOrigen"));
                if (row.get("nombreRegion") != null) solicitudPagoDTO.setNombreRegion((String) row.get("nombreRegion"));
                if (row.get("ciudad") != null) solicitudPagoDTO.setCiudadEmpleador((String) row.get("ciudad"));
                solicitudesPago.add(solicitudPagoDTO);
            }

            return solicitudesPago;
    }

    @Override
    public List<SolicitudDTO> obtenerSolicitudesAntiguas(int dias) {
        String sql = "SELECT * FROM paf.fn_ObtenerSolicitudesAntiguas(?)";
        
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, dias);
        
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
            if (row.get("fechaSolicitud") != null) solicitudPagoDTO.setFechaSolicitud((Date) row.get("fechaSolicitud"));
            if (row.get("folio") != null) solicitudPagoDTO.setFolio((Long) row.get("folio"));
            if (row.get("nombreRegion") != null) solicitudPagoDTO.setNombreRegion((String) row.get("nombreRegion"));
            if (row.get("ciudad") != null) solicitudPagoDTO.setCiudadEmpleador((String) row.get("ciudad"));
            solicitudesPago.add(solicitudPagoDTO);
        }

        return solicitudesPago;
    }

    @Override
    public List<SolicitudDTO> obtenerSolicitudesPorBeneficiario(Integer rutBeneficiario) {
        String sql = "SELECT * FROM paf.fn_ObtenerSolicitudesPorBeneficiario(?)";
        
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, new Object[]{rutBeneficiario});
        
        List<SolicitudDTO> solicitudes = new ArrayList<>();
        
        for (Map<String, Object> row : results) {
            SolicitudDTO solicitudPagoDTO = new SolicitudDTO();
            
            if (row.get("idSolicitud") != null) solicitudPagoDTO.setIdSolicitud((Integer) row.get("idSolicitud"));
            if (row.get("idProceso") != null) solicitudPagoDTO.setIdProceso((Integer) row.get("idProceso"));
            if (row.get("idUsuario") != null) solicitudPagoDTO.setIdUsuario((Integer) row.get("idUsuario"));
            if (row.get("idComuna") != null) solicitudPagoDTO.setIdComuna((Integer) row.get("idComuna"));
            if (row.get("nombreComuna") != null) solicitudPagoDTO.setNombreComuna((String) row.get("nombreComuna"));
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
            if (row.get("estado") != null) solicitudPagoDTO.setEstado((String) row.get("estado"));
            if (row.get("nombreOrigen") != null) solicitudPagoDTO.setNombreOrigen((String) row.get("nombreOrigen"));
            if (row.get("cumpleCriterios") != null) solicitudPagoDTO.setCumpleCriterios((String) row.get("cumpleCriterios"));
            if (row.get("nombreRegion") != null) solicitudPagoDTO.setNombreRegion((String) row.get("nombreRegion"));
            if (row.get("ciudad") != null) solicitudPagoDTO.setCiudadEmpleador((String) row.get("ciudad"));
            solicitudes.add(solicitudPagoDTO);
        }
        
        return solicitudes;
    }

    @Override
    public List<SolicitudDTO> obtenerSolicitudesPorEmpleador(Integer rutEmpleador) {
        String sql = "SELECT * FROM paf.fn_ObtenerSolicitudesPorEmpleador(?)";
        
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, new Object[]{rutEmpleador});
        
        List<SolicitudDTO> solicitudes = new ArrayList<>();
        
        for (Map<String, Object> row : results) {
            SolicitudDTO solicitudPagoDTO = new SolicitudDTO();
            
            if (row.get("idSolicitud") != null) solicitudPagoDTO.setIdSolicitud((Integer) row.get("idSolicitud"));
            if (row.get("idProceso") != null) solicitudPagoDTO.setIdProceso((Integer) row.get("idProceso"));
            if (row.get("idUsuario") != null) solicitudPagoDTO.setIdUsuario((Integer) row.get("idUsuario"));
            if (row.get("idComuna") != null) solicitudPagoDTO.setIdComuna((Integer) row.get("idComuna"));
            if (row.get("nombreComuna") != null) solicitudPagoDTO.setNombreComuna((String) row.get("nombreComuna"));
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
            if (row.get("estado") != null) solicitudPagoDTO.setEstado((String) row.get("estado"));
            if (row.get("nombreOrigen") != null) solicitudPagoDTO.setNombreOrigen((String) row.get("nombreOrigen"));
            if (row.get("cumpleCriterios") != null) solicitudPagoDTO.setCumpleCriterios((String) row.get("cumpleCriterios"));
            if (row.get("nombreRegion") != null) solicitudPagoDTO.setNombreRegion((String) row.get("nombreRegion"));
            if (row.get("ciudad") != null) solicitudPagoDTO.setCiudadEmpleador((String) row.get("ciudad"));
            solicitudes.add(solicitudPagoDTO);
        }
        
        return solicitudes;
    }

    @Override
    public List<SolicitudDTO> obtenerSolicitudesPorOrigen(Integer origen, Integer proceso, Integer estado) {
        String sql = "SELECT * FROM paf.fn_ObtenerSolicitudesPorOrigen(?, ?, ?)";
        
        // Ejecutar la consulta con los tres parámetros
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, new Object[]{origen, proceso, estado});
        
        List<SolicitudDTO> solicitudes = new ArrayList<>();
        
        for (Map<String, Object> row : results) {
            SolicitudDTO solicitudPagoDTO = new SolicitudDTO();
            
            if (row.get("idSolicitud") != null) solicitudPagoDTO.setIdSolicitud((Integer) row.get("idSolicitud"));
            if (row.get("idProceso") != null) solicitudPagoDTO.setIdProceso((Integer) row.get("idProceso"));
            if (row.get("idUsuario") != null) solicitudPagoDTO.setIdUsuario((Integer) row.get("idUsuario"));
            if (row.get("idComuna") != null) solicitudPagoDTO.setIdComuna((Integer) row.get("idComuna"));
            if (row.get("nombreComuna") != null) solicitudPagoDTO.setNombreComuna((String) row.get("nombreComuna"));
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
            if (row.get("estado") != null) solicitudPagoDTO.setEstado((String) row.get("estado"));
            if (row.get("nombreOrigen") != null) solicitudPagoDTO.setNombreOrigen((String) row.get("nombreOrigen"));
            if (row.get("nombreRegion") != null) solicitudPagoDTO.setNombreRegion((String) row.get("nombreRegion"));
            if (row.get("ciudad") != null) solicitudPagoDTO.setCiudadEmpleador((String) row.get("ciudad"));        
            solicitudes.add(solicitudPagoDTO);
        }
        
        return solicitudes;
    }

    @Override
    public List<MotivoRechazoDTO> obtenerMotivosRechazo() {
        String sql = "SELECT * FROM paf.vw_MotivoRechazo";

        try {
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);

            List<MotivoRechazoDTO> motivosRechazo = new ArrayList<>();
            for (Map<String, Object> row : results) {
                MotivoRechazoDTO motivoRechazoDTO = new MotivoRechazoDTO();
                if (row.get("idMotivoRechazo") != null) 
                    motivoRechazoDTO.setIdMotivoRechazo((Integer) row.get("idMotivoRechazo"));
                if (row.get("nombre") != null) 
                    motivoRechazoDTO.setNombre((String) row.get("nombre"));
                if (row.get("vigente") != null) 
                    motivoRechazoDTO.setVigente("S".equalsIgnoreCase((String) row.get("vigente")));
                
                motivosRechazo.add(motivoRechazoDTO);
            }

            return motivosRechazo;
        } catch (Exception e) {
            System.out.println("Error al obtener los motivos de rechazo: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public MotivoRechazoDTO obtenerMotivoRechazoPorId(int idMotivoRechazo) {
        String sql = "SELECT * FROM paf.fn_ObtenerMotivosRechazo(?)";

        try {
            // Ejecutar la consulta con el parámetro idMotivoRechazo
            Map<String, Object> result = jdbcTemplate.queryForMap(sql, idMotivoRechazo);

            // Convertir el resultado en un objeto MotivoRechazoDTO
            MotivoRechazoDTO motivoRechazoDTO = new MotivoRechazoDTO();
            if (result.get("idMotivoRechazo") != null)
                motivoRechazoDTO.setIdMotivoRechazo((Integer) result.get("idMotivoRechazo"));
            if (result.get("nombre") != null)
                motivoRechazoDTO.setNombre((String) result.get("nombre"));
            if (result.get("vigente") != null)
                motivoRechazoDTO.setVigente("S".equalsIgnoreCase((String) result.get("vigente")));

            return motivoRechazoDTO;
        } catch (Exception e) {
            System.out.println("Error al obtener el motivo de rechazo por ID: " + e.getMessage());
            return null; // Retorna null si ocurre algún error
        }
    }


}