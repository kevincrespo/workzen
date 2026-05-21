package apirest.workzen.service;

import java.time.LocalDate;

import apirest.workzen.modelo.dto.CalendarioSemanalDTO;

/**
 * Interfaz del servicio de calendario semanal.
 * Define las operaciones para obtener eventos del calendario.
 */
public interface CalendarioService {

	/**
	 * Obtiene los eventos de la semana que contiene la fecha dada.
	 *
	 * @param fecha cualquier fecha de la semana
	 * @return dto con el rango de la semana y la lista de eventos
	 */
	CalendarioSemanalDTO getSemana(LocalDate fecha);

}
