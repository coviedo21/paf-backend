package cl.gob.ips.solicitudes_pago.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import cl.gob.ips.solicitudes_pago.dto.ArchivoSolicitudDTO;

public interface FileService {
    public void insertarSolicitud(List<ArchivoSolicitudDTO> listaSolicitudes);
    public Boolean uploadFileAzure(String connectStr, String shareName,
    String dirName, String fileName, byte[] file);
    byte[] downloadFile(String connectStr, String shareName, String dirName, String fileName);
}