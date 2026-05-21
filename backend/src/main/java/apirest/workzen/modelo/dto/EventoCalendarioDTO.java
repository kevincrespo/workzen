package apirest.workzen.modelo.dto;

import java.time.LocalDate;

/**
 * Representa un evento del calendario semanal (ausencia o teletrabajo).
 *
 * @param empleadoNombre  nombre completo del empleado
 * @param empleadoInicial primera letra del nombre
 * @param tipo            "ausencia" o "teletrabajo"
 * @param subtipo         "vacaciones", "medico", "teletrabajo", etc.
 * @param fechaInicio     fecha de inicio del evento
 * @param fechaFin        fecha de fin del evento
 */
public record EventoCalendarioDTO(
		String empleadoNombre,
		String empleadoInicial,
		String tipo,
		String subtipo,
		LocalDate fechaInicio,
		LocalDate fechaFin
) {}
