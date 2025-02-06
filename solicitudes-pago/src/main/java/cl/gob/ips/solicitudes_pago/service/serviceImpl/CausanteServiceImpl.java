package cl.gob.ips.solicitudes_pago.service.serviceImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import cl.gob.ips.solicitudes_pago.dao.CausanteDAO;
import cl.gob.ips.solicitudes_pago.dto.CausanteDTO;
import cl.gob.ips.solicitudes_pago.dto.DerechoCausanteDTO;
import cl.gob.ips.solicitudes_pago.dto.DetalleCausanteDTO;
import cl.gob.ips.solicitudes_pago.service.CausanteService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CausanteServiceImpl implements CausanteService{
    @Autowired
    private CausanteDAO causanteDAO;

    private final RestTemplate restTemplate;

    @Override
    public List<DerechoCausanteDTO> obtenerDerechoCausantes(String rutCausante, String rutBeneficiario, String periodoDesde, String periodoHasta, String tipoCausante) {
        String baseUrl = "https://ctacorrienteback-dev.azurewebsites.net/causante-service/causante/derecho/sinPagar/listar";

        // Construcción de la URL con parámetros en query string
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("rutCausante", rutCausante)
                .queryParam("rutBeneficiario", rutBeneficiario)
                .queryParam("periodoDesde", periodoDesde)
                .queryParam("periodoHasta", periodoHasta)
                .queryParam("tipoCausante", tipoCausante);

        // Llamada al endpoint usando RestTemplate
        DerechoCausanteDTO[] responseArray = restTemplate.getForObject(builder.toUriString(), DerechoCausanteDTO[].class);

        return (responseArray != null) ? Arrays.asList(responseArray) : Collections.emptyList();
    }


    public List<CausanteDTO> obtenerDetalleCausante(int rutBeneficiario){
        return causanteDAO.obtenerDetalleCausante(rutBeneficiario);
    }

    public String insertarDetalleCausante(DetalleCausanteDTO causanteDTO){
        return causanteDAO.insertarDetalleCausante(causanteDTO);
    }

    public List<DetalleCausanteDTO> obtenerDetalleCausantePorId(int iIdCausanteSolicitud){
        return causanteDAO.obtenerDetalleCausantePorId(iIdCausanteSolicitud);
    }
}
