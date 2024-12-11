package cl.gob.ips.pagos_asignacion_familiar.dao.daoImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cl.gob.ips.pagos_asignacion_familiar.dao.ComunaDAO;
import cl.gob.ips.pagos_asignacion_familiar.dao.CuentaBancariaDAO;
import cl.gob.ips.pagos_asignacion_familiar.dao.FileDAO;
import cl.gob.ips.pagos_asignacion_familiar.dto.ArchivoSolicitudDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.CuentaBancariaDTO;
import cl.gob.ips.pagos_asignacion_familiar.dto.SolicitudDTO;
import cl.gob.ips.pagos_asignacion_familiar.service.SolicitudPagoService;

@Repository
public class FileDAOImpl implements FileDAO{
    @Autowired
    SolicitudPagoService solicitudPagoService;

    //@Autowired 
    //ComunaDAO comunaDAO;

    @Autowired
    private CuentaBancariaDAO cuentaBancariaDAO;

    public void insertarSolicitud(ArchivoSolicitudDTO archivo){
        //List<SolicitudDTO> solicitudes = new ArrayList<>();

        //for (ArchivoSolicitudDTO archivo : listaSolicitudes) {
            SolicitudDTO solicitud = new SolicitudDTO();

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
                solicitud.setRutCausante(Integer.parseInt(archivo.getRutCargaFamiliar()));
                solicitud.setDvCausante(archivo.getDvCargaFamiliar());
                solicitud.setApellidoPaternoCausante(archivo.getApellidoPaternoCarga());
                solicitud.setApellidoMaternoCausante(archivo.getApellidoMaternoCarga());
                solicitud.setNombresCausante(archivo.getNombresCarga());
                solicitud.setInicioCompensacion(new SimpleDateFormat("dd/MM/YYYY").parse(archivo.getFechaInicioCompensacion()));
                solicitud.setFinCompensacion(new SimpleDateFormat("dd/MM/YYYY").parse(archivo.getFechaFinCompensacion()));
                solicitud.setTipoSolicitante(2); //Empleador si es previred
                solicitud.setOrigen(Integer.parseInt(archivo.getOrigen()));
                solicitud.setIdUsuario(1);
                solicitud.setObservaciones("Solicitud Importada de Archivo");
                solicitudPagoService.insertarSolicitudPago(solicitud);
            } catch (NumberFormatException | ParseException e) {
                // Manejar la excepción adecuadamente
                e.printStackTrace();
            }

            // Agregar la solicitud a la lista
//            solicitudes.add(solicitud);
  //      }
    }

    public void insertarCuentaBancaria(CuentaBancariaDTO cuentaBancaria){
        cuentaBancariaDAO.insertarCuentaBancaria(cuentaBancaria,true);
    }
}
