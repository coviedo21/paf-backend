package cl.gob.ips.solicitudes_pago.dao.daoImpl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cl.gob.ips.solicitudes_pago.dao.FileDAO;
import cl.gob.ips.solicitudes_pago.dto.ArchivoSolicitudDTO;
import cl.gob.ips.solicitudes_pago.dto.CausanteCuentaCorrienteDTO;
import cl.gob.ips.solicitudes_pago.dto.CausanteSolicitudDTO;
import cl.gob.ips.solicitudes_pago.dto.DerechoCausanteDTO;
import cl.gob.ips.solicitudes_pago.dto.DetalleCausanteDTO;
import cl.gob.ips.solicitudes_pago.dto.ResponseDTO;
import cl.gob.ips.solicitudes_pago.dto.SolicitudDTO;
import cl.gob.ips.solicitudes_pago.service.CausanteService;
import cl.gob.ips.solicitudes_pago.service.CriterioSolicitudService;
import cl.gob.ips.solicitudes_pago.service.SolicitudPagoService;
import cl.gob.ips.solicitudes_pago.service.serviceImpl.CriterioSolicitudServiceImpl;

@Repository
public class FileDAOImpl implements FileDAO{
	private static final Logger logger = LoggerFactory.getLogger(FileDAOImpl.class);
	@Autowired
    SolicitudPagoService solicitudPagoService;

    @Autowired
    CausanteService causanteService;

    @Autowired
    CriterioSolicitudService criterioSolicitudService;

    public String insertarSolicitud(ArchivoSolicitudDTO archivo){
        //List<SolicitudDTO> solicitudes = new ArrayList<>();

        //for (ArchivoSolicitudDTO archivo : listaSolicitudes) {
            SolicitudDTO solicitud = new SolicitudDTO();
            List<CausanteSolicitudDTO> listaCausantes = new ArrayList<>();
            CausanteSolicitudDTO causante = new CausanteSolicitudDTO();
            String periodosAprobados = "";
            //BigDecimal totalPagar = new BigDecimal(0);
            try {
                solicitud.setFolio(Long.parseLong(archivo.getFolio()));
                solicitud.setFechaSolicitud(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(archivo.getFechaHora()));
                solicitud.setRutEmpleador(Integer.parseInt(archivo.getRutEmpleador()));
                solicitud.setDvEmpleador(archivo.getDvEmpleador());
                solicitud.setRazonSocialEmpleador(archivo.getRazonSocialEmpleador());
                solicitud.setDireccion(archivo.getDireccionEmpleador());
                solicitud.setEmail(archivo.getEmailEmpleador());
                //La Comuna viene en texto por lo que hay que buscar su ID
                solicitud.setIdComuna(archivo.getIdComuna());
                solicitud.setNombreComuna(archivo.getNombreComuna());
                solicitud.setIdRegion(archivo.getIdRegion());
                solicitud.setNombreRegion(archivo.getNombreRegion());
                solicitud.setRutBeneficiario(Integer.parseInt(archivo.getRutTrabajador()));
                solicitud.setDvBeneficiario(archivo.getDvTrabajador());
                solicitud.setApellidoPaternoBeneficiario(archivo.getApellidoPaternoTrabajador());
                solicitud.setApellidoMaternoBeneficiario(archivo.getApellidoMaternoTrabajador());
                solicitud.setNombresBeneficiario(archivo.getNombresTrabajador());
                solicitud.setEsPortuario("N");
                
                //Causante
                causante.setRutBeneficiario(Integer.parseInt(archivo.getRutTrabajador()));
                causante.setVcDvBeneficiario(archivo.getDvTrabajador());
                causante.setRutCausante(Integer.parseInt(archivo.getRutCargaFamiliar()));
                causante.setVcDvCausante(archivo.getDvCargaFamiliar());
                causante.setVcNombreCausante(archivo.getApellidoPaternoCarga()+" "+archivo.getApellidoMaternoCarga()+" "+archivo.getNombresCarga());
                //solicitud.setApellidoPaternoCausante(archivo.getApellidoPaternoCarga());
                //solicitud.setApellidoMaternoCausante(archivo.getApellidoMaternoCarga());
                //solicitud.setNombresCausante(archivo.getNombresCarga());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                causante.setFechaInicioRango(LocalDate.parse(archivo.getFechaInicioCompensacion(), formatter));
                //causante.setFechaInicioRango(new SimpleDateFormat("dd/MM/yyyy").parse(archivo.getFechaInicioCompensacion()));
                causante.setFechaFinRango(LocalDate.parse(archivo.getFechaFinCompensacion(), formatter));
                //listaCausantes.add(causante);
                //causante.setDetalle
                //Buscar en cuenta corriente
                CausanteCuentaCorrienteDTO derechoCausante;
                List<CausanteCuentaCorrienteDTO> detalle = new ArrayList<>();
                List<DetalleCausanteDTO> listaDetalle  = new ArrayList<>();
                long tiempoInicioObtenerPeriodos = System.currentTimeMillis();
                List<String> periodosCausante = obtenerPeriodos(causante.getFechaInicioRango(),causante.getFechaFinRango());
                String periodoInicio = obtenerPeriodo(causante.getFechaInicioRango());
                String periodoFin = obtenerPeriodo(causante.getFechaFinRango());
                long tiempoFinObtenerPeriodos = System.currentTimeMillis();
                logger.error("Obtener periodos demoró: "+(tiempoInicioObtenerPeriodos-tiempoFinObtenerPeriodos));
                boolean tieneDerecho = false;
                long tiempoInicioObtenerDerecho = System.currentTimeMillis();
                detalle = causanteService.obtenerDerechoCausantes(archivo.getRutCargaFamiliar(), archivo.getRutTrabajador(), archivo.getRutEmpleador(), periodoInicio,periodoFin, null);
                long tiempoFinObtenerDerecho = System.currentTimeMillis();
                logger.error("Obtener derecho demoró: "+(tiempoInicioObtenerDerecho-tiempoFinObtenerDerecho));
                
                try {
                	for (String periodo : periodosCausante) {
                	    boolean encontrado = false;
                	    DetalleCausanteDTO derecho = new DetalleCausanteDTO();

                	    for (CausanteCuentaCorrienteDTO dto : detalle) {
                	        String aprobados = dto.getPeriodosAprobados();

                	        if (aprobados != null && !aprobados.isEmpty()) {
                	            List<String> listaAprobados = Arrays.stream(aprobados.split(","))
                	                .map(String::trim)
                	                .collect(Collectors.toList());

                	            if (listaAprobados.contains(periodo)) {
                	                encontrado = true;

                	                if (dto.getDetalle() != null && !dto.getDetalle().isEmpty()) {
                	                    var origen = dto.getDetalle().get(0);

                	                    derecho.setRutCausante(origen.getRutCausante());
                	                    derecho.setDvCausante(origen.getDvCausante());
                	                    derecho.setRutBeneficiario(origen.getRutBeneficiario());
                	                    derecho.setDvBeneficiario(origen.getDvBeneficiario());
                	                    derecho.setPeriodo(Integer.parseInt(periodo));
                	                    derecho.setTipoMovimiento(origen.getTipoMovimientoId());

                	                    String fechaMovimientoStr = origen.getFechaMovimiento();
                	                    if (fechaMovimientoStr != null && !fechaMovimientoStr.trim().isEmpty()) {
                	                        try {
                	                        	Date parsedDate = new SimpleDateFormat("yyyy-MM-dd").parse(fechaMovimientoStr);
                	                            java.sql.Date fechaSQL = new java.sql.Date(parsedDate.getTime());
                	                            derecho.setFechaMovimiento(fechaSQL);
                	                        } catch (ParseException e) {
                	                            derecho.setFechaMovimiento(null);
                	                            System.out.println("Error parseando fecha: " + fechaMovimientoStr);
                	                        }
                	                    } else {
                	                        derecho.setFechaMovimiento(null);
                	                    }

                	                    derecho.setEntradaSalida(origen.getEntradaSalida());
                	                    derecho.setMontoMovimiento(new BigDecimal(origen.getMontoMovimiento()));
                	                    derecho.setTipoCausante(origen.getTipoCausante());
                	                    derecho.setIdBeneficio(origen.getIdBeneficio());
                	                    derecho.setRentaPromedio(new BigDecimal(origen.getRentaPromedio()));
                	                    derecho.setCodigoTramo(origen.getCodigoTramo());
                	                    derecho.setDiasReconocimiento(origen.getDiasReconocimiento());
                	                    derecho.setRutEmpleador(origen.getRutEmpleador());
                	                    derecho.setDvEmpleador(origen.getDvEmpleador());
                	                    derecho.setDiferencia(new BigDecimal(origen.getDiferencia()));
                	                    derecho.setEstado(1);
                	                    derecho.setValorTramo30(new BigDecimal(origen.getValorTramo30()));
                	                    derecho.setDiasTrabajados(origen.getDiasTrabajados());
                	                    derecho.setDiasPagados(origen.getDiasPagados());
                	                    derecho.setMontoPagos(new BigDecimal(origen.getMontoPagos()));
                	                    tieneDerecho = true;
                	                }

                	                break; // ya lo conseguimos, salimos del for
                	            }
                	        }
                	    }

                	    if (!encontrado) {
                	        derecho.setRutCausante(causante.getRutCausante());
                	        derecho.setDvCausante(causante.getVcDvCausante());
                	        derecho.setRutBeneficiario(causante.getRutBeneficiario());
                	        derecho.setDvBeneficiario(causante.getVcDvBeneficiario());
                	        derecho.setPeriodo(Integer.parseInt(periodo));
                	        derecho.setTipoMovimiento(0);
                	        derecho.setFechaMovimiento(null);
                	        derecho.setEntradaSalida(null);
                	        derecho.setMontoMovimiento(BigDecimal.ZERO);
                	        derecho.setDiferencia(BigDecimal.ZERO);
                	        derecho.setTipoCausante(0);
                	        derecho.setIdBeneficio(0);
                	        derecho.setRentaPromedio(BigDecimal.ZERO);
                	        derecho.setCodigoTramo(0);
                	        derecho.setDiasReconocimiento(0);
                	        derecho.setRutEmpleador(0);
                	        derecho.setDvEmpleador(null);
                	        derecho.setEstado(2);
                	        derecho.setValorTramo30(BigDecimal.ZERO);
                	        derecho.setDiasTrabajados(0);
                	        derecho.setDiasPagados(0);
                	        derecho.setMontoPagos(BigDecimal.ZERO);
                	    }

                	    listaDetalle.add(derecho);
                	}


                }catch(Exception e) {
                	return "Error al obtener derecho causante Folio: "+archivo.getFolio()+" Rut Beneficiario: "+archivo.getRutTrabajador()+"-"+archivo.getDvTrabajador()+", Rut Causante: "+archivo.getRutCargaFamiliar()+"-"+archivo.getDvCargaFamiliar()+" Periodo: "+archivo.getPeriodo()+" Fecha Inicio Compensación: "+archivo.getFechaInicioCompensacion()+" Fecha Fin Compensación: "+archivo.getFechaFinCompensacion();
                }
                causante.setDetalle(listaDetalle);
                //causante.setVcPeriodosAprobados(primerPeriodo+" a "+ultimoPeriodo);
                //causante.setTotalReconocimiento(0);
                //causante.setTotalPago(causante.getTotalReconocimiento().subtract(criterioSolicitudService.obtenerMontoDescuento(causante.getRutBeneficiario())));
                listaCausantes.add(causante);
                solicitud.setTipoSolicitante(2); //Empleador si es previred
                solicitud.setOrigen(Integer.parseInt(archivo.getOrigen()));
                solicitud.setIdUsuario(1);
                solicitud.setObservaciones("Solicitud Importada de Archivo");
                solicitud.setListaCausantes(listaCausantes);
                solicitud.setCiudadEmpleador(archivo.getCiudadEmpleador());
                solicitud.setPeriodo(archivo.getPeriodo());
                solicitud.setIdCuentaBancaria(archivo.getIdCuentaBancaria());
                solicitud.setIdFormaPago(archivo.getFormaPago());
                solicitud.setUsuarioCreacion(archivo.getUsuario());
                if(tieneDerecho){
                    ResponseDTO respuesta = solicitudPagoService.insertarSolicitudPago(solicitud,true);
                    if((int) respuesta.getResultado()>0){
                        return "";
                    }
                    else{return "**ERROR** Solicitud ya existe. Folio: "+archivo.getFolio()+" Rut Beneficiario: "+archivo.getRutTrabajador()+"-"+archivo.getDvTrabajador()+", Rut Causante: "+archivo.getRutCargaFamiliar()+"-"+archivo.getDvCargaFamiliar()+" Periodo: "+archivo.getPeriodo()+" Fecha Inicio Compensación: "+archivo.getFechaInicioCompensacion()+" Fecha Fin Compensación: "+archivo.getFechaFinCompensacion();}
                }
                else{
                    return "**ERROR** Causante no cuenta con periodos con derecho a pago. Folio: "+archivo.getFolio()+" Rut Beneficiario: "+archivo.getRutTrabajador()+"-"+archivo.getDvTrabajador()+", Rut Causante: "+archivo.getRutCargaFamiliar()+"-"+archivo.getDvCargaFamiliar()+" Periodo: "+archivo.getPeriodo()+" Fecha Inicio Compensación: "+archivo.getFechaInicioCompensacion()+" Fecha Fin Compensación: "+archivo.getFechaFinCompensacion();
                }
            } catch (NumberFormatException | ParseException e) {
                // Manejar la excepción adecuadamente
                e.printStackTrace();
                return e.getMessage();
            }

            // Agregar la solicitud a la lista
//            solicitudes.add(solicitud);
  //      }
    }

    public List<String> obtenerPeriodos(LocalDate fechaInicio, LocalDate fechaFin) {
        List<String> periodos = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");

        while (!fechaInicio.isAfter(fechaFin)) {
            periodos.add(fechaInicio.format(formatter));
            fechaInicio = fechaInicio.plusMonths(1); // Avanza un mes
        }

        return periodos;
    }
    
    public String obtenerPeriodo(LocalDate fecha) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
        return fecha.format(formatter);
    }


}