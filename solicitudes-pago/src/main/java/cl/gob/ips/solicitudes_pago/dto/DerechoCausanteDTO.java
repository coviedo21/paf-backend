package cl.gob.ips.solicitudes_pago.dto;

import lombok.Data;

@Data
public class DerechoCausanteDTO {
    private int id;
    private long rutCausante;
    private String dvCausante;
    private long rutBeneficiario;
    private String dvBeneficiario;
    private int periodo;
    private int tipoMovimientoId;
    private String fechaMovimiento;
    private String entradaSalida;
    private int montoMovimiento;
    private int tipoCausante;
    private int idBeneficio;
    private int rentaPromedio;
    private int codigoTramo;
    private int diasReconocimiento;
    private int cantidadPagos;
    private int montoPagos;
    private int diferencia;
}
