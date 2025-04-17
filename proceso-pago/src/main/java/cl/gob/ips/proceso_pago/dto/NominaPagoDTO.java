package cl.gob.ips.proceso_pago.dto;

import lombok.Data;

@Data
public class NominaPagoDTO {
    private String enviadoNominaPago;
    private int numNominaNuevaForma;
    private String fecPago;
    private int codHaber;
    private int rutB;
    private String dvB;
    private String apPat;
    private String apMat;
    private String nombres;
    private double montoPago;
    private String direccion;
    private int comuna;
    private String numeroCta;
    private int codigoBanco;
    private int codigoTipoCuenta;
    private int codigoFormaPago;
    private int numNominaOrigen;
    private int origenPago;
    private int haber2;
    private int monto2;
    private int haber3;
    private int monto3;
    private int haber4;
    private int monto4;
    private int haber5;
    private int monto5;
    private int haber6;
    private int monto6;

    public String toCsvRow() {
        return String.join(",",
                escapeCsv(enviadoNominaPago),
                String.valueOf(numNominaNuevaForma),
                escapeCsv(fecPago),
                String.valueOf(codHaber),
                String.valueOf(rutB),
                escapeCsv(dvB),
                escapeCsv(apPat),
                escapeCsv(apMat),
                escapeCsv(nombres),
                "0", "0", // apMat, nombres, sexo, fecNac
                String.valueOf(montoPago),
                escapeCsv(direccion),
                String.valueOf(comuna),
                escapeCsv(numeroCta),
                String.valueOf(codigoBanco),
                String.valueOf(codigoTipoCuenta),
                String.valueOf(codigoFormaPago),
                "3290",
                String.valueOf(numNominaOrigen),
                String.valueOf(origenPago),
                "95",
                String.valueOf(haber2),
                String.valueOf(monto2),
                String.valueOf(haber3),
                String.valueOf(monto3),
                String.valueOf(haber4),
                String.valueOf(monto4),
                String.valueOf(haber5),
                String.valueOf(monto5),
                String.valueOf(haber6),
                String.valueOf(monto6)
        );
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        return value.contains(",") ? "\"" + value.replace("\"", "\"\"") + "\"" : value;
    }
}
