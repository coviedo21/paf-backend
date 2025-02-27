package cl.gob.ips.proceso_pago.service.serviceImpl;

import cl.gob.ips.proceso_pago.dao.NominaPagoDAO;
import cl.gob.ips.proceso_pago.dto.NominaPagoDTO;
import cl.gob.ips.proceso_pago.service.NominaPagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

@Service
public class NominaPagoServiceImpl implements NominaPagoService {

    @Autowired
    private NominaPagoDAO nominaPagoDAO;

    @Override
    public void generateCsvResponse(HttpServletResponse response, int iIdProcesoIN, String fecPago) throws IOException {
        List<NominaPagoDTO> results = nominaPagoDAO.ejecutarProcedimientoAlmacenado(iIdProcesoIN, fecPago);

        try (OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream())) {
            writeCsvHeaders(writer);
            writeCsvData(writer, results);
        }
    }

    private void writeCsvHeaders(OutputStreamWriter writer) throws IOException {
        writer.write("ENVIADO-NOMINA_PAGO,numNominaNuevaForma,fecPago,codHaber,rutB,dvB,apPat,apMat,nombres,sexo,fecNac,montoPago,"
                + "direccion,comuna,numero_cta,codigoBanco,codigoTipoCuenta,codigoFormaPago,SucDep,NumNominaOrigen,OrigenPago,"
                + "Institucion,Haber 2,Monto 2,Haber 3,Monto 3,Haber 4,Monto 4,Haber 5,Monto 5,Haber 6,Monto 6\n");
    }

    private void writeCsvData(OutputStreamWriter writer, List<NominaPagoDTO> results) throws IOException {
        for (NominaPagoDTO item : results) {
            writer.write(item.toCsvRow() + "\n");
        }
    }
}
