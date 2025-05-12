package cl.gob.ips.solicitudes_pago.dao.daoImpl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

@Repository
public class FileDAOImpl implements FileDAO{
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
                List<String> periodosCausante = obtenerPeriodos(causante.getFechaInicioRango(),causante.getFechaFinRango());
                 boolean tieneDerecho = false;
                String primerPeriodo = null;
                String ultimoPeriodo = null;
                try {
                for(String periodo: periodosCausante){

                    detalle = causanteService.obtenerDerechoCausantes(archivo.getRutCargaFamiliar(), archivo.getRutTrabajador(), archivo.getRutEmpleador(), periodo, periodo, null);

                    derechoCausante = (detalle != null && 
                   !detalle.isEmpty()) 
                   ? detalle.get(0) 
                   : null;

                    if (primerPeriodo == null) { 
                        primerPeriodo = periodo; // Guarda el primer periodo
                    }
                    ultimoPeriodo = periodo; // Siempre actualiza al último periodo
                    if(derechoCausante!=null){
                        tieneDerecho = true;
                        DetalleCausanteDTO derecho = new DetalleCausanteDTO();
                        derecho.setRutCausante(derechoCausante.getDetalle().get(0).getRutCausante());
                        derecho.setDvCausante(derechoCausante.getDetalle().get(0).getDvCausante());
                        derecho.setRutBeneficiario(derechoCausante.getDetalle().get(0).getRutBeneficiario());
                        derecho.setDvBeneficiario(derechoCausante.getDetalle().get(0).getDvBeneficiario());
                        derecho.setPeriodo(derechoCausante.getDetalle().get(0).getPeriodo());
                        derecho.setTipoMovimiento(derechoCausante.getDetalle().get(0).getTipoMovimientoId());
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        String fechaMovimientoStr = derechoCausante.getDetalle().get(0).getFechaMovimiento();

	                     // Verificar si la fecha no es null ni vacía antes de convertirla
	                     if (fechaMovimientoStr != null && !fechaMovimientoStr.isEmpty()) {
	                         Date fechaMovimiento = dateFormat.parse(fechaMovimientoStr);
	                         derecho.setFechaMovimiento(fechaMovimiento);
	                     } else {
	                         derecho.setFechaMovimiento(null); // O cualquier valor por defecto si lo necesitas
	                     }
	                     
                        derecho.setEntradaSalida(derechoCausante.getDetalle().get(0).getEntradaSalida());
                        derecho.setMontoMovimiento(new BigDecimal(derechoCausante.getDetalle().get(0).getMontoMovimiento()));
                        derecho.setTipoCausante(derechoCausante.getDetalle().get(0).getTipoCausante());
                        derecho.setIdBeneficio(derechoCausante.getDetalle().get(0).getIdBeneficio());
                        derecho.setRentaPromedio(new BigDecimal(derechoCausante.getDetalle().get(0).getRentaPromedio()));
                        derecho.setCodigoTramo(derechoCausante.getDetalle().get(0).getCodigoTramo());
                        derecho.setDiasReconocimiento(derechoCausante.getDetalle().get(0).getDiasReconocimiento());
                        derecho.setRutEmpleador(derechoCausante.getDetalle().get(0).getRutEmpleador());
                        derecho.setDvEmpleador(derechoCausante.getDetalle().get(0).getDvEmpleador());
                        derecho.setEstado(1);
                        derecho.setDiferenciaDerecho(new BigDecimal(derechoCausante.getDetalle().get(0).getDiferenciaDerecho()));
                        derecho.setDiferencia(new BigDecimal(derechoCausante.getDetalle().get(0).getDiferencia()));
                        //periodosAprobados = periodosAprobados + derecho.getPeriodo()+","; 
                        //totalPagar = totalPagar.add(derecho.getMontoMovimiento());
                        listaDetalle.add(derecho);
                    }
                    else{
                        DetalleCausanteDTO derecho = new DetalleCausanteDTO();
                        derecho.setRutCausante(causante.getRutCausante());
                        derecho.setDvCausante(causante.getVcDvCausante());
                        derecho.setRutBeneficiario(causante.getRutBeneficiario());
                        derecho.setDvBeneficiario(causante.getVcDvBeneficiario());
                        derecho.setPeriodo(Integer.valueOf(periodo));
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
                        listaDetalle.add(derecho);    
                    }
                }
                }catch(Exception e) {
                	return "Error al obtener derecho causante Folio: "+archivo.getFolio()+" Rut Beneficiario: "+archivo.getRutTrabajador()+"-"+archivo.getDvTrabajador()+", Rut Causante: "+archivo.getRutCargaFamiliar()+"-"+archivo.getDvCargaFamiliar()+" Periodo: "+archivo.getPeriodo()+" Fecha Inicio Compensación: "+archivo.getFechaInicioCompensacion()+" Fecha Fin Compensación: "+archivo.getFechaFinCompensacion();
                }
                causante.setDetalle(listaDetalle);
                causante.setVcPeriodosAprobados(primerPeriodo+" a "+ultimoPeriodo);
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

}