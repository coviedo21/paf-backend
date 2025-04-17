package cl.gob.ips.proceso_pago.mapper;

import cl.gob.ips.proceso_pago.dto.NominaPagoDTO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.swing.tree.TreePath;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class NominaPagoRowMapper implements RowMapper<NominaPagoDTO> {

    public NominaPagoDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        NominaPagoDTO dto = new NominaPagoDTO();

        dto.setEnviadoNominaPago(rs.getString("ENVIADO-NOMINA_PAGO"));
        dto.setNumNominaNuevaForma(rs.getInt("numNominaNuevaForma"));
        dto.setFecPago(rs.getString("fecPago"));
        dto.setCodHaber(rs.getInt("codHaber"));
        dto.setRutB(rs.getInt("rutB"));
        dto.setDvB(rs.getString("dvB"));
        dto.setApPat(rs.getString("apPat"));
        dto.setApMat(rs.getString("apMat"));
        dto.setNombres(rs.getString("nombres"));
        dto.setMontoPago(rs.getDouble("montoPago"));
        dto.setDireccion(rs.getString("direccion"));
        dto.setComuna(rs.getInt("comuna"));
        dto.setNumeroCta(rs.getString("numero_cta"));
        dto.setCodigoBanco(rs.getInt("codigoBanco"));
        dto.setCodigoTipoCuenta(rs.getInt("codigoTipoCuenta"));
        dto.setCodigoFormaPago(rs.getInt("codigoFormaPago"));
        dto.setNumNominaOrigen(rs.getInt("NumNominaOrigen"));
        dto.setOrigenPago(rs.getInt("OrigenPago"));
        dto.setHaber2(rs.getInt("Haber 2"));
        dto.setMonto2(rs.getInt("Monto 2"));
        dto.setHaber3(rs.getInt("Haber 3"));
        dto.setMonto3(rs.getInt("Monto 3"));
        dto.setHaber4(rs.getInt("Haber 4"));
        dto.setMonto4(rs.getInt("Monto 4"));
        dto.setHaber5(rs.getInt("Haber 5"));
        dto.setMonto5(rs.getInt("Monto 5"));
        dto.setHaber6(rs.getInt("Haber 6"));
        dto.setMonto6(rs.getInt("Monto 6"));

        return dto;
    }

//    public int[] getRowsForPaths(TreePath[] treePaths) {
//        return new int[0];
//    }
}
