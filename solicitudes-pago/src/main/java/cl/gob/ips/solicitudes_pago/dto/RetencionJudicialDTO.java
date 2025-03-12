package cl.gob.ips.solicitudes_pago.dto;

import java.util.Date;

import lombok.Data;

@Data
public class RetencionJudicialDTO {
	private int idRetencion;
    private int rutRetenedor;
    private String dvRetenedor;
    private String nombreRetenedor;
    private String apellidoPaternoRetenedor;
    private String apellidoMaternoRetenedor;
    private int idFormaPago;
    private int idCuentaBancaria;
    private int codTipoCuenta;
    private String nombreTipoCuenta;
    private int codBanco;
    private String nombreBanco;
    private String numeroCuenta;
    private Date fechaRetencion;
    private String rutRetenedorString;
    private Date fechaInicioRetencion;
    private Date fechaFinRetencion;
}
