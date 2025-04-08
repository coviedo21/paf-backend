package cl.gob.ips.proceso_pago.service;

import cl.gob.ips.proceso_pago.dto.SpResponse;

public interface CtaCtePAFService {
    SpResponse procesarDatosCtaCtePAF(int IdProceso);
    SpResponse procesarDatosCtaCte(int IdProceso);
}