package cl.gob.ips.solicitudes_pago.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import cl.gob.ips.solicitudes_pago.dto.ArchivoResponseDTO;
import cl.gob.ips.solicitudes_pago.dto.ArchivoSolicitudDTO;
import cl.gob.ips.solicitudes_pago.dto.CausanteSolicitudDTO;
import cl.gob.ips.solicitudes_pago.dto.SolicitudDTO;

@Service
public class PDFService {
	@Autowired
	@Lazy
	SolicitudPagoService solicitudPagoService;
	
	public byte[] generarSolicitudAsignacionFamiliar(int idSolicitud) throws Exception {
		SolicitudDTO solicitudPago = solicitudPagoService.consultarSolicitudPago(idSolicitud).get(0);
		List<CausanteSolicitudDTO> listaCausantes = solicitudPagoService.obtenerCausantesPorSolicitud(idSolicitud);
		Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        document.open();

        // === ENCABEZADO CON LOGO Y FECHA ===
        PdfPTable header = new PdfPTable(2);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{1, 2});

        Image logo = Image.getInstance(getClass().getResource("/images/logo.png"));

        logo.scaleToFit(70, 70);

        PdfPCell logoCell = new PdfPCell(logo);
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        Font small = new Font(Font.HELVETICA, 10);
        
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String fechaHoraActual = ahora.format(formatter);

        Paragraph solicitud = new Paragraph("Solicitud: "+solicitudPago.getIdSolicitud()+"\n" + fechaHoraActual, small);
        PdfPCell rightCell = new PdfPCell(solicitud);
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        rightCell.setVerticalAlignment(Element.ALIGN_TOP);

        header.addCell(logoCell);
        header.addCell(rightCell);
        document.add(header);

        // === TÍTULO ===
        Paragraph title = new Paragraph("Solicitud de Pago para Asignación Familiar", new Font(Font.HELVETICA, 14, Font.BOLD));
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingBefore(10);
        title.setSpacingAfter(10);
        document.add(title);
        int contador = 1;
        int contadorFormaPago = 0;
        // === 1) ANTECEDENTES DEL BENEFICIARIO ===
        document.add(new Paragraph("1) Antecedentes del Beneficiario", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        document.add(espacio());
        contador++;
        PdfPTable beneficiario = new PdfPTable(2);
        beneficiario.setWidthPercentage(100);
        addRow(beneficiario, "RUT", solicitudPago.getRutBeneficiario()+"-"+solicitudPago.getDvBeneficiario());
        addRow(beneficiario, "Nombre Completo", solicitudPago.getNombresBeneficiario()+" "+solicitudPago.getApellidoPaternoBeneficiario()+" "+solicitudPago.getApellidoMaternoBeneficiario());
        addRow(beneficiario, "Tipo de Beneficiario", "Trabajador");
        if(solicitudPago.getTipoSolicitante()==3) {
        	addRow(beneficiario, "Tipo de Trabajador", "Finiquitado");
        	addRow(beneficiario, "Fecha Finiquito", "26/05/2025");
        }
        addRow(beneficiario, "Teléfono", String.valueOf(solicitudPago.getTelefono()));
        addRow(beneficiario, "Email", solicitudPago.getEmail());
        addRow(beneficiario, "Domicilio", solicitudPago.getDireccion());
        addRow(beneficiario, "Comuna", solicitudPago.getNombreComuna());
        //addRow(beneficiario, "Provincia", "Santiago");
        addRow(beneficiario, "Región", solicitudPago.getNombreRegion());
        document.add(beneficiario);

        document.add(espacio());

        // === 2) EMPLEADOR ===
        if(solicitudPago.getTipoSolicitante()==2) {
	        document.add(new Paragraph(contador+") Antecedentes Empleador", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
	        document.add(espacio());
	        contador++;
	        PdfPTable empleador = new PdfPTable(2);
	        empleador.setWidthPercentage(100);
	        addRow(empleador, "Nombre Empleador", solicitudPago.getRazonSocialEmpleador());
	        addRow(empleador, "Rut Empleador", solicitudPago.getRutEmpleador()+"-"+solicitudPago.getDvEmpleador());
	        document.add(empleador);
	
	        document.add(espacio());
        }
        // === 3) FORMA DE PAGO ===
        document.add(new Paragraph(contador+") Forma de Pago", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        document.add(espacio());
        contadorFormaPago = contador;
        contador++;
        PdfPTable pago = new PdfPTable(2);
        pago.setWidthPercentage(100);
        addRow(pago, "Tipo de Pago", solicitudPago.getNombreFormaPago());
        if(solicitudPago.getCuentaBancaria()!=null){
        addRow(pago, "Cuenta", solicitudPago.getCuentaBancaria());
		}
        document.add(pago);

        document.add(espacio());

        // === 4) CARGAS RETROACTIVAS ===
        document.add(new Paragraph(contador+") Cargas", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        document.add(espacio());
        contador++;
        PdfPTable cargas = new PdfPTable(2);
        cargas.setWidthPercentage(100);
        cargas.setSpacingBefore(5);
        addHeader(cargas, "RUT Carga");
        addHeader(cargas, "Nombre Carga");
        for(CausanteSolicitudDTO causante:listaCausantes) {
        	addRow(cargas, causante.getRutCausante()+"-"+causante.getVcDvCausante(), causante.getVcNombreCausante());
        }
        document.add(cargas);

        document.add(espacio());

        // === AUTORIZACIÓN ===
//        Paragraph texto = new Paragraph("Por este acto, autorizo al Instituto de Previsión Social (IPS) para generar el posible pago según la \"Forma de Pago\" ingresada en el ítem N°"+contadorFormaPago);
//        texto.setSpacingBefore(10);
//        document.add(texto);
//
//        document.add(espacio());
//
//        // === FIRMA ===
//        PdfPTable firmas = new PdfPTable(2);
//        firmas.setWidthPercentage(100);
//        firmas.setSpacingBefore(20);
//        firmas.setWidths(new float[]{1, 1});
//
//        // Columna izquierda - Beneficiario
//        PdfPTable col1 = new PdfPTable(1);
//        col1.setWidthPercentage(100);
//        col1.addCell(celdaLineaFirma());
//        col1.addCell(celdaTextoCentrada(solicitudPago.getApellidoPaternoBeneficiario()+" "+solicitudPago.getApellidoMaternoBeneficiario()+" "+solicitudPago.getNombresBeneficiario())); // nombre del beneficiario
//        col1.addCell(celdaTextoCentrada(solicitudPago.getRutBeneficiario()+"-"+solicitudPago.getDvBeneficiario())); // rut
//        col1.addCell(celdaTextoCentrada("Beneficiario/a"));
//
//        // Columna derecha - IPS
//        PdfPTable col2 = new PdfPTable(1);
//        col2.setWidthPercentage(100);
//        col2.addCell(celdaLineaFirma());
//        col2.addCell(celdaTextoCentrada("VICTOR EDUARDO AVILA BARRERA"));
//        col2.addCell(celdaTextoCentrada("Sucursal IPS"));
//        col2.addCell(celdaTextoCentrada("")); // Para mantener simetría
//
//        // Agregar columnas como celdas
//        firmas.addCell(celdaContenedor(col1));
//        firmas.addCell(celdaContenedor(col2));
//
//        document.add(new Paragraph("Firmo ante mi don:\n\n"));
//        document.add(firmas);
//
//        document.add(espacio());
//
//        // === COPIA ===
//        Paragraph copia = new Paragraph("Copia Beneficiario/a", FontFactory.getFont(FontFactory.HELVETICA_BOLD));
//        copia.setAlignment(Element.ALIGN_CENTER);
//        copia.setSpacingBefore(15);
//        document.add(copia);
//
//        document.newPage();
        document.close();
        return baos.toByteArray();
    }

    private void addRow(PdfPTable table, String key, String value) {
        Font bold = new Font(Font.HELVETICA, 11, Font.BOLD);
        Font normal = new Font(Font.HELVETICA, 11);
        table.addCell(new PdfPCell(new Phrase(key, bold)));
        table.addCell(new PdfPCell(new Phrase(value, normal)));
    }

    private void addRow(PdfPTable table, String col1, String col2, String col3, String col4) {
        table.addCell(new PdfPCell(new Phrase(col1)));
        table.addCell(new PdfPCell(new Phrase(col2)));
        table.addCell(new PdfPCell(new Phrase(col3)));
        table.addCell(new PdfPCell(new Phrase(col4)));
    }

    private void addHeader(PdfPTable table, String text) {
        Font bold = new Font(Font.HELVETICA, 11, Font.BOLD);
        PdfPCell cell = new PdfPCell(new Phrase(text, bold));
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        table.addCell(cell);
    }

    private Paragraph espacio() {
        Paragraph p = new Paragraph(" ");
        p.setSpacingBefore(8);
        return p;
    }
    
    private PdfPCell celdaLineaFirma() {
        Paragraph linea = new Paragraph("_______________________________");
        PdfPCell cell = new PdfPCell(linea);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPaddingBottom(5);
        return cell;
    }

    private PdfPCell celdaTextoCentrada(String texto) {
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }

    private PdfPCell celdaContenedor(PdfPTable tablaInterna) {
        PdfPCell cell = new PdfPCell(tablaInterna);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }


}