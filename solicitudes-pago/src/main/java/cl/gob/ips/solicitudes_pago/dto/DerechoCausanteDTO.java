package cl.gob.ips.solicitudes_pago.dto;

import lombok.Data;

@Data
public class DerechoCausanteDTO {
	private Long id;
    private Long idCtaCteCausante;
    private int rutCausante;
    private String dvCausante;
    private int rutBeneficiario;
    private String dvBeneficiario;
    private int periodo;
    private int tipoMovimientoId;
    private String fechaMovimiento; // O usa OffsetDateTime si prefieres trabajar como fecha
    private String entradaSalida;
    private int montoMovimiento;
    private int tipoCausante;
    private int idBeneficio;
    private int rentaPromedio;
    private int codigoTramo;
    private int diasReconocimiento;
    private int rutEmpleador;
    private String dvEmpleador;
    private Integer diasTrabajados;
    private Integer diasPagados;
    private int montoProporcional;
    private String numeroDocumento;
    private int cantidadPagos;
    private int montoPagos;
    private int diferenciaDerecho;
    private int diferencia;
}
