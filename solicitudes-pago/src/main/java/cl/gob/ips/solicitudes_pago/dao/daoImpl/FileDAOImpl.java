package cl.gob.ips.solicitudes_pago.dao.daoImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cl.gob.ips.solicitudes_pago.dao.FileDAO;
import cl.gob.ips.solicitudes_pago.dto.ArchivoSolicitudDTO;
import cl.gob.ips.solicitudes_pago.dto.CausanteSolicitudDTO;
import cl.gob.ips.solicitudes_pago.dto.SolicitudDTO;
import cl.gob.ips.solicitudes_pago.service.SolicitudPagoService;

@Repository
public class FileDAOImpl implements FileDAO{
    @Autowired
    SolicitudPagoService solicitudPagoService;

    public void insertarSolicitud(ArchivoSolicitudDTO archivo){
        //List<SolicitudDTO> solicitudes = new ArrayList<>();

        //for (ArchivoSolicitudDTO archivo : listaSolicitudes) {
            SolicitudDTO solicitud = new SolicitudDTO();
            List<CausanteSolicitudDTO> listaCausantes = new ArrayList<>();
            CausanteSolicitudDTO causante = new CausanteSolicitudDTO();

            try {
                solicitud.setFolio(Long.parseLong(archivo.getFolio()));
                solicitud.setFechaSolicitud(new SimpleDateFormat("dd/MM/YYYY HH:mm:ss").parse(archivo.getFechaHora()));
                solicitud.setRutEmpleador(Integer.parseInt(archivo.getRutEmpleador()));
                solicitud.setDvEmpleador(archivo.getDvEmpleador());
                solicitud.setRazonSocialEmpleador(archivo.getRazonSocialEmpleador());
                solicitud.setDireccion(archivo.getDireccionEmpleador());
                solicitud.setEmail(archivo.getEmailEmpleador());
                //La Comuna viene en texto por lo que hay que buscar su ID
                solicitud.setIdComuna(archivo.getIdComuna());
                solicitud.setNombreComuna(archivo.getNombreComuna());
                //solicitud.setIdRegion(Integer.parseInt(archivo.getRegionEmpleador()));
                solicitud.setRutBeneficiario(Integer.parseInt(archivo.getRutTrabajador()));
                solicitud.setDvBeneficiario(archivo.getDvTrabajador());
                solicitud.setApellidoPaternoBeneficiario(archivo.getApellidoPaternoTrabajador());
                solicitud.setApellidoMaternoBeneficiario(archivo.getApellidoMaternoTrabajador());
                solicitud.setNombresBeneficiario(archivo.getNombresTrabajador());
                
                //Causante
                causante.setRutCausante(Integer.parseInt(archivo.getRutCargaFamiliar()));
                causante.setVcDvCausante(archivo.getDvCargaFamiliar());
                causante.setVcNombreCausante(archivo.getApellidoPaternoCarga()+" "+archivo.getApellidoMaternoCarga()+" "+archivo.getNombresCarga());
                //solicitud.setApellidoPaternoCausante(archivo.getApellidoPaternoCarga());
                //solicitud.setApellidoMaternoCausante(archivo.getApellidoMaternoCarga());
                //solicitud.setNombresCausante(archivo.getNombresCarga());
                causante.setFechaInicioRango(new SimpleDateFormat("dd/MM/YYYY").parse(archivo.getFechaInicioCompensacion()));
                causante.setFechaFinRango(new SimpleDateFormat("dd/MM/YYYY").parse(archivo.getFechaFinCompensacion()));
                listaCausantes.add(causante);
                solicitud.setTipoSolicitante(2); //Empleador si es previred
                solicitud.setOrigen(Integer.parseInt(archivo.getOrigen()));
                solicitud.setIdUsuario(1);
                solicitud.setObservaciones("Solicitud Importada de Archivo");
                solicitud.setListaCausantes(listaCausantes);
                solicitudPagoService.insertarSolicitudPago(solicitud);
                
            } catch (NumberFormatException | ParseException e) {
                // Manejar la excepción adecuadamente
                e.printStackTrace();
            }

            // Agregar la solicitud a la lista
//            solicitudes.add(solicitud);
  //      }
    }

}