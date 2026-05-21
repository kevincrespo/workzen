package apirest.workzen.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import apirest.workzen.modelo.dto.CalendarioSemanalDTO;
import apirest.workzen.modelo.dto.EventoCalendarioDTO;
import apirest.workzen.modelo.dto.SemanaDTO;
import apirest.workzen.modelo.entities.Ausencia;
import apirest.workzen.modelo.entities.Teletrabajo;
import apirest.workzen.modelo.repository.AusenciaRepository;
import apirest.workzen.modelo.repository.TeletrabajoRepository;

/**
 * Implementacion del servicio de calendario semanal.
 * Combina ausencias y teletrabajos aprobados que solapan con la semana.
 */
@Service
public class CalendarioServiceImplJpaMy8 implements CalendarioService {

	@Autowired
	private AusenciaRepository ausenciaRepo;

	@Autowired
	private TeletrabajoRepository teletrabajoRepo;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CalendarioSemanalDTO getSemana(LocalDate fecha) {
		// Calcular lunes y domingo de la semana
		LocalDate lunes = fecha.with(DayOfWeek.MONDAY);
		LocalDate domingo = fecha.with(DayOfWeek.SUNDAY);

		List<EventoCalendarioDTO> eventos = new ArrayList<>();

		// Ausencias aprobadas que solapan con la semana
		// Ausencia usa LocalDateTime, asi que convertimos las fechas
		List<Ausencia> ausencias = ausenciaRepo
				.findByEstadoAndFechaFinGreaterThanEqualAndFechaInicioLessThanEqual(
						Ausencia.Estado.aprobado,
						lunes.atStartOfDay(),
						domingo.atTime(23, 59, 59));

		for (Ausencia a : ausencias) {
			String nombreCompleto = a.getEmpleado().getNombre() + " "
					+ a.getEmpleado().getApellido1();
			eventos.add(new EventoCalendarioDTO(
					nombreCompleto,
					String.valueOf(a.getEmpleado().getNombre().charAt(0)),
					"ausencia",
					a.getTipo().name(),
					a.getFechaInicio().toLocalDate(),
					a.getFechaFin().toLocalDate()));
		}

		// Teletrabajos aprobados que solapan con la semana
		List<Teletrabajo> teletrabajos = teletrabajoRepo
				.findByEstadoAndFechaFinGreaterThanEqualAndFechaInicioLessThanEqual(
						Teletrabajo.Estado.aprobado, lunes, domingo);

		for (Teletrabajo t : teletrabajos) {
			String nombreCompleto = t.getEmpleado().getNombre() + " "
					+ t.getEmpleado().getApellido1();
			eventos.add(new EventoCalendarioDTO(
					nombreCompleto,
					String.valueOf(t.getEmpleado().getNombre().charAt(0)),
					"teletrabajo",
					"teletrabajo",
					t.getFechaInicio(),
					t.getFechaFin()));
		}

		return new CalendarioSemanalDTO(new SemanaDTO(lunes, domingo), eventos);
	}

}
