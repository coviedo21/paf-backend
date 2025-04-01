package cl.gob.ips.proceso_pago.service;

import cl.gob.ips.proceso_pago.dto.SpResponse;

public interface CtaCtePAFService {
    SpResponse copiaCteCtePAF(int iIdProcesoPago);
}