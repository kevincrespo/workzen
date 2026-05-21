package apirest.workzen.modelo.dto;

import java.time.LocalDate;

/**
 * Representa el rango de una semana (lunes a domingo).
 *
 * @param inicio fecha del lunes
 * @param fin    fecha del domingo
 */
public record SemanaDTO(
		LocalDate inicio,
		LocalDate fin
) {}
