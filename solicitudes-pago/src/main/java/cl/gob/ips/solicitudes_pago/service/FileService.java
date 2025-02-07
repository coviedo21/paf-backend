package cl.gob.ips.solicitudes_pago.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import cl.gob.ips.solicitudes_pago.dto.ArchivoResponseDTO;
import cl.gob.ips.solicitudes_pago.dto.ArchivoSolicitudDTO;

public interface FileService {
    public ArchivoResponseDTO insertarSolicitudes(List<ArchivoSolicitudDTO> listaSolicitudes, String periodo);
    public Boolean uploadFileAzure(String connectStr, String shareName,
    String dirName, String fileName, byte[] file);
    byte[] downloadFile(String connectStr, String shareName, String dirName, String fileName);
    public String convertirStringAUTF8(String texto, String encodingOriginal) throws UnsupportedEncodingException;
    public String detectarCodificacion(InputStream inputStream) throws IOException;
    public InputStream convertirAUTF8(InputStream inputStream, String encoding) throws IOException;
}