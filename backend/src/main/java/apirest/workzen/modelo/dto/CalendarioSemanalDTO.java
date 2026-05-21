package apirest.workzen.modelo.dto;

import java.util.List;

/**
 * Respuesta del endpoint de calendario semanal.
 * Contiene el rango de la semana y la lista de eventos.
 *
 * @param semana  rango lunes-domingo de la semana
 * @param eventos lista de eventos (ausencias y teletrabajos)
 */
public record CalendarioSemanalDTO(
		SemanaDTO semana,
		List<EventoCalendarioDTO> eventos
) {}
