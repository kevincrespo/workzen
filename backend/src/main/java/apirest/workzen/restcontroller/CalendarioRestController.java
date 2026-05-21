package apirest.workzen.restcontroller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import apirest.workzen.modelo.dto.CalendarioSemanalDTO;
import apirest.workzen.service.CalendarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para el calendario semanal.
 * Devuelve ausencias y teletrabajos aprobados de una semana.
 */
@Tag(name = "Calendario", description = "Endpoints para el calendario semanal")
@RestController
@RequestMapping("/calendario")
public class CalendarioRestController {

	@Autowired
	private CalendarioService calendarioService;

	/**
	 * Obtiene los eventos del calendario de la semana que contiene la fecha dada.
	 * El backend calcula automaticamente el lunes y domingo de esa semana.
	 *
	 * @param fecha cualquier fecha de la semana (formato ISO: yyyy-MM-dd)
	 * @return rango de la semana y lista de eventos
	 */
	@Operation(summary = "Obtener calendario semanal",
			description = "Devuelve las ausencias y teletrabajos aprobados de la semana que contiene la fecha dada")
	@ApiResponse(responseCode = "200", description = "Calendario semanal obtenido correctamente")
	@GetMapping("/semanal")
	public ResponseEntity<CalendarioSemanalDTO> getSemana(
			@Parameter(description = "Cualquier fecha de la semana (yyyy-MM-dd)", example = "2026-02-16")
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
		return ResponseEntity.ok(calendarioService.getSemana(fecha));
	}

}
