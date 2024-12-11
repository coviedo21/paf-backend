package cl.gob.ips.solicitudes_pago.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ArchivoCriterioDTO {
    private int idCriterioSolicitud;
    private MultipartFile file;

}
