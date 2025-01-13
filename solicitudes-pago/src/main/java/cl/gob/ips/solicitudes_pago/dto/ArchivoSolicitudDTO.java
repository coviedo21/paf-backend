package cl.gob.ips.solicitudes_pago.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArchivoSolicitudDTO {
    private String folio;
    private String fechaHora;
    private String rutEmpleador;
    private String dvEmpleador;
    private String razonSocialEmpleador;
    private String direccionEmpleador;
    private String emailEmpleador;
    private String comunaEmpleador;
    private int idComuna;
    private String nombreComuna;
    private String ciudadEmpleador;
    private int idRegion;
    private String nombreRegion;
    private String rutTrabajador;
    private String dvTrabajador;
    private String apellidoPaternoTrabajador;
    private String apellidoMaternoTrabajador;
    private String nombresTrabajador;
    private String rutCargaFamiliar;
    private String dvCargaFamiliar;
    private String apellidoPaternoCarga;
    private String apellidoMaternoCarga;
    private String nombresCarga;
    private String tipoCarga;
    private String fechaInicioCompensacion;
    private String fechaFinCompensacion;
    private String estadoCarga;
    private String origen;
}
