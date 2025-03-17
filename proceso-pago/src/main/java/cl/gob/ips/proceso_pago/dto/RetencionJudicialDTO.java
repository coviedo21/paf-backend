package cl.gob.ips.proceso_pago.dto;

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
    private Date fecha_inicio_retencion;
    private Date fecha_fin_retencion;
}
