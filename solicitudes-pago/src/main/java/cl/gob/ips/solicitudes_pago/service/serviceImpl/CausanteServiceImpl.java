package cl.gob.ips.solicitudes_pago.service.serviceImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import cl.gob.ips.solicitudes_pago.dao.CausanteDAO;
import cl.gob.ips.solicitudes_pago.dto.CausanteDTO;
import cl.gob.ips.solicitudes_pago.dto.DerechoCausanteDTO;
import cl.gob.ips.solicitudes_pago.service.CausanteService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CausanteServiceImpl implements CausanteService{
    @Autowired
    private CausanteDAO causanteDAO;

    private final RestTemplate restTemplate;

    @Override
    public List<DerechoCausanteDTO> obtenerDerechoCausantes(int rutBeneficiario, int periodoDesde, int periodoHasta, int tipoBeneficiario) {
        String urlTemplate = "https://ctacorrienteback-dev.azurewebsites.net/derecho-beneficario-service/derecho-beneficiario/listar/{rutBeneficiario}/{periodoDesde}/{periodoHasta}/{tipoBeneficiario}";

        // Construcción de la URL con parámetros
        Map<String, Object> uriVariables = new HashMap<>();
        uriVariables.put("rutBeneficiario", rutBeneficiario);
        uriVariables.put("periodoDesde", periodoDesde);
        uriVariables.put("periodoHasta", periodoHasta);
        uriVariables.put("tipoBeneficiario", tipoBeneficiario);

        // Llamada al endpoint usando RestTemplate
        DerechoCausanteDTO[] responseArray = restTemplate.getForObject(urlTemplate, DerechoCausanteDTO[].class, uriVariables);

        if (responseArray != null) {
            return Arrays.asList(responseArray); // Convertir el array en una lista
        }
        return Collections.emptyList(); // Retornar lista vacía si la respuesta es nula
    }


    public List<CausanteDTO> obtenerDetalleCausante(int rutBeneficiario){
        return causanteDAO.obtenerDetalleCausante(rutBeneficiario);
    }
}
