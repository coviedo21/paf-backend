package cl.gob.ips.solicitudes_pago.service.serviceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import cl.gob.ips.solicitudes_pago.dao.CausanteDAO;
import cl.gob.ips.solicitudes_pago.dto.CausanteCuentaCorrienteDTO;
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
    public List<CausanteCuentaCorrienteDTO> obtenerDerechoCausantes(String rutCausante, String rutBeneficiario, String rutEmpleador, String periodoDesde, String periodoHasta, String tipoCausante) {
        String baseUrl = "https://ctacorrienteback-dev.azurewebsites.net/causante-service/v1/ctacte/causante/derecho/sinPagar/listar";

        // Construcción de la URL con parámetros en query string
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("rutCausante", rutCausante)
                .queryParam("rutBeneficiario", rutBeneficiario)
                .queryParam("periodoDesde", periodoDesde)
                .queryParam("periodoHasta", periodoHasta)
                .queryParam("tipoCausante", tipoCausante);

        // Llamada al endpoint usando RestTemplate
        DerechoCausanteDTO[] responseArray = restTemplate.getForObject(builder.toUriString(), DerechoCausanteDTO[].class);

     // Convertir el array en lista y filtrar los registros donde rutEmpleador sea 0 o negativo
        List<DerechoCausanteDTO> derechos = (responseArray != null)
                ? Arrays.stream(responseArray)
                        .filter(d -> d.getRutEmpleador() > 0) // Excluir si rutEmpleador es 0 o negativo
                        .collect(Collectors.toList())
                : Collections.emptyList();



        // 🔹 Agrupar por rutCausante y dvCausante
        Map<String, List<DerechoCausanteDTO>> agrupados = derechos.stream()
                .collect(Collectors.groupingBy(d -> d.getRutCausante() + "-" + d.getDvCausante()));

        // Lista de resultado
        List<CausanteCuentaCorrienteDTO> resultado = new ArrayList<>();

        for (Map.Entry<String, List<DerechoCausanteDTO>> entry : agrupados.entrySet()) {
            List<DerechoCausanteDTO> listaDerechos = entry.getValue();
            
            // Tomar el primer elemento para la cabecera
            DerechoCausanteDTO primerRegistro = listaDerechos.get(0);

            CausanteCuentaCorrienteDTO dto = new CausanteCuentaCorrienteDTO();
            dto.setRutCausante(primerRegistro.getRutCausante());
            dto.setDvRutCausante(primerRegistro.getDvCausante());
            dto.setNombreCausante("Nombre Desconocido"); // Si tienes nombres en otro lado, agrégalo aquí

            // Obtener el menor y mayor periodo
            List<Integer> periodos = listaDerechos.stream()
                    .map(DerechoCausanteDTO::getPeriodo)
                    .sorted()
                    .collect(Collectors.toList());
            
            int periodoInicio = periodos.get(0);
            int periodoFin = periodos.get(periodos.size() - 1);

            // Convertir a formato de fecha
            LocalDate fechaInicio = obtenerPrimerDiaMes(periodoInicio);
            LocalDate fechaFin = obtenerUltimoDiaMes(periodoFin);

            dto.setFechaInicioPeriodo(fechaInicio);
            dto.setFechaFinPeriodo(fechaFin);
            
            // Calcular monto total a pagar sumando `montoMovimiento`
            int montoTotal = listaDerechos.stream().mapToInt(DerechoCausanteDTO::getMontoMovimiento).sum();
            dto.setMontoPagar(BigDecimal.valueOf(montoTotal));

            // Construir periodos aprobados como una lista separada por comas
            String periodosAprobados = listaDerechos.stream()
                    .map(d -> String.valueOf(d.getPeriodo()))
                    .distinct()
                    .sorted()
                    .collect(Collectors.joining(", "));
            dto.setPeriodosAprobados(periodosAprobados);

            // Asignar los detalles
            dto.setDetalle(listaDerechos);

            // Agregar al resultado
            resultado.add(dto);
        }

        return resultado;
    }
    
    /*@Override
    public List<CausanteCuentaCorrienteDTO> obtenerDerechoCausantes(String rutCausante, String rutBeneficiario, String rutEmpleador, String periodoDesde, String periodoHasta, String tipoCausante) {
        String baseUrl = "https://ctacorrienteback-dev.azurewebsites.net/causante-service/v1/ctacte/causante/derecho/sinPagar/listar";

        // Construcción de la URL con parámetros en query string
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("rutCausante", rutCausante)
                .queryParam("rutBeneficiario", rutBeneficiario)
                .queryParam("periodoDesde", periodoDesde)
                .queryParam("periodoHasta", periodoHasta)
                .queryParam("tipoCausante", tipoCausante);

        // Llamada al endpoint usando RestTemplate
        DerechoCausanteDTO[] responseArray = restTemplate.getForObject(builder.toUriString(), DerechoCausanteDTO[].class);

        // Convertir el array en lista, si es nulo devolver una lista vacía
        List<DerechoCausanteDTO> derechos = (responseArray != null) ? Arrays.asList(responseArray) : Collections.emptyList();

     // Convertir el rutEmpleador recibido a int (si no es null y es un número válido)
        int rutEmpleadorInt = (rutEmpleador != null && !rutEmpleador.isEmpty()) ? Integer.parseInt(rutEmpleador) : 0;

        // 🔹 Filtrar solo los registros donde el rutEmpleador de la API coincida con el rutEmpleador recibido como parámetro
        List<DerechoCausanteDTO> derechosFiltrados = derechos.stream()
                .filter(d -> d.getRutEmpleador() == rutEmpleadorInt)
                .collect(Collectors.toList());


        // 🔹 Agrupar por rutCausante y dvCausante
        Map<String, List<DerechoCausanteDTO>> agrupados = derechosFiltrados.stream()
                .collect(Collectors.groupingBy(d -> d.getRutCausante() + "-" + d.getDvCausante()));

        // Lista de resultado
        List<CausanteCuentaCorrienteDTO> resultado = new ArrayList<>();

        for (Map.Entry<String, List<DerechoCausanteDTO>> entry : agrupados.entrySet()) {
            List<DerechoCausanteDTO> listaDerechos = entry.getValue();
            
            // Tomar el primer elemento para la cabecera
            DerechoCausanteDTO primerRegistro = listaDerechos.get(0);

            CausanteCuentaCorrienteDTO dto = new CausanteCuentaCorrienteDTO();
            dto.setRutCausante(primerRegistro.getRutCausante());
            dto.setDvRutCausante(primerRegistro.getDvCausante());
            dto.setNombreCausante("Nombre Desconocido"); // Si tienes nombres en otro lado, agrégalo aquí

            // Obtener el menor y mayor periodo
            List<Integer> periodos = listaDerechos.stream()
                    .map(DerechoCausanteDTO::getPeriodo)
                    .sorted()
                    .collect(Collectors.toList());
            
            int periodoInicio = periodos.get(0);
            int periodoFin = periodos.get(periodos.size() - 1);

            // Convertir a formato de fecha
            LocalDate fechaInicio = obtenerPrimerDiaMes(periodoInicio);
            LocalDate fechaFin = obtenerUltimoDiaMes(periodoFin);

            dto.setFechaInicioPeriodo(fechaInicio);
            dto.setFechaFinPeriodo(fechaFin);
            
            // Calcular monto total a pagar sumando `montoMovimiento`
            int montoTotal = listaDerechos.stream().mapToInt(DerechoCausanteDTO::getMontoMovimiento).sum();
            dto.setMontoPagar(BigDecimal.valueOf(montoTotal));

            // Construir periodos aprobados como una lista separada por comas
            String periodosAprobados = listaDerechos.stream()
                    .map(d -> String.valueOf(d.getPeriodo()))
                    .distinct()
                    .sorted()
                    .collect(Collectors.joining(", "));
            dto.setPeriodosAprobados(periodosAprobados);

            // Asignar los detalles
            dto.setDetalle(listaDerechos);

            // Agregar al resultado
            resultado.add(dto);
        }

        return resultado;
    }*/


    public List<CausanteDTO> obtenerDetalleCausante(int rutBeneficiario){
        return causanteDAO.obtenerDetalleCausante(rutBeneficiario);
    }

    public String insertarDetalleCausante(DetalleCausanteDTO causanteDTO){
        return causanteDAO.insertarDetalleCausante(causanteDTO);
    }

    public List<DetalleCausanteDTO> obtenerDetalleCausantePorId(int iIdCausanteSolicitud){
        return causanteDAO.obtenerDetalleCausantePorId(iIdCausanteSolicitud);
    }
    
    public DetalleCausanteDTO obtenerDetalleCausantePorIdDetalle(int iIdDetalleCausante) {
    	return causanteDAO.obtenerDetalleCausantePorIdDetalle(iIdDetalleCausante);
    }
    
    public boolean actualizarDetalleCausante(DetalleCausanteDTO detalleCausanteDTO) {
    	return causanteDAO.actualizarDetalleCausante(detalleCausanteDTO);
    }

    private LocalDate obtenerPrimerDiaMes(int periodo) {
        int year = periodo / 100;
        int month = periodo % 100;
        return LocalDate.of(year, month, 1);
    }

    /**
     * Convierte un periodo YYYYMM en el último día del mes en formato LocalDate.
     */
    private LocalDate obtenerUltimoDiaMes(int periodo) {
        int year = periodo / 100;
        int month = periodo % 100;
        return YearMonth.of(year, month).atEndOfMonth();
    }
}
