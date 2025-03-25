package cl.gob.ips.solicitudes_pago.service.serviceImpl;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.gob.ips.solicitudes_pago.dao.UtilDAO;
import cl.gob.ips.solicitudes_pago.dto.OrigenDTO;
import cl.gob.ips.solicitudes_pago.service.UtilService;

@Service
public class UtilServiceImpl implements UtilService{
    @Autowired
    private UtilDAO utilDAO;

    public List<OrigenDTO> obtenerOrigenes(int tipo){
        return utilDAO.obtenerOrigenes(tipo);    
    }

    public boolean esFechaValida(LocalDate fechaInicioCompensacion, LocalDate fechaSolicitud) {
        if (fechaInicioCompensacion == null || fechaSolicitud == null) {
            return false;
        }
    
        // Restar 5 años a la fecha de comparación
        LocalDate fechaLimite = fechaSolicitud.minusYears(5);
    
        return fechaInicioCompensacion.isAfter(fechaLimite); // Retorna true si fechaInicio es anterior a fechaComparacion - 5 años
    }

    public static Map<String, String> obtenerFechasDesdePeriodo(String periodo) {
        Map<String, String> fechas = new HashMap<>();

        try {
            // Parsear el string "yyyyMM" a LocalDate (primer día del mes)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
            YearMonth yearMonth = YearMonth.parse(periodo, formatter);

            // Obtener fechas de inicio y fin del mes
            LocalDate fechaInicio = yearMonth.atDay(1);
            LocalDate fechaFin = yearMonth.atEndOfMonth();

            // Formatear las fechas como "yyyy-MM-dd"
            DateTimeFormatter salida = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            fechas.put("inicio", fechaInicio.format(salida));
            fechas.put("fin", fechaFin.format(salida));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de periodo inválido. Debe ser 'yyyyMM'", e);
        }

        return fechas;
    }

}
