package cl.gob.ips.solicitudes_pago.service.serviceImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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

    public boolean esFechaValida(LocalDate fechaInicio, LocalDate fechaComparacion) {
        if (fechaInicio == null || fechaComparacion == null) {
            return false;
        }
    
        // Restar 5 años a la fecha de comparación
        LocalDate fechaLimite = fechaComparacion.minusYears(5);
    
        return fechaInicio.isAfter(fechaLimite); // Retorna true si fechaInicio es anterior a fechaComparacion - 5 años
    }
    

}
