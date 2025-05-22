package cl.gob.ips.solicitudes_pago.dto;

import java.util.List;

import lombok.Data;

@Data
public class CuentaCorrienteListaDTO {
	private List<DerechoCausanteDTO> lista;
}
