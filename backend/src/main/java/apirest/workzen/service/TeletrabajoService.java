package apirest.workzen.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.security.core.Authentication;
import apirest.workzen.modelo.dto.SolicitudTeletrabajoDto;
import apirest.workzen.modelo.entities.Teletrabajo;

public interface TeletrabajoService extends ICrudGenerico<Teletrabajo, Integer>{

	// Validar que las fechas sean correctas (inicio <= fin)
	boolean validarFechas(LocalDate fechaInicio, LocalDate fechaFin);
		
	// Validar que no se solape con otros Teletrabajos del mismo empleado
	boolean validarSolapamiento(int empleadoId, LocalDate fechaInicio, LocalDate fechaFin);
	
	// Crear Teletrabajo para un empleado
	Teletrabajo solicitarTeletrabajo(Authentication auth, SolicitudTeletrabajoDto solicitud);
	
	// Obtener los Teletrabajos del empleado logueado
	List<Teletrabajo> obtenerMisTeletrabajos(Authentication auth);
	
	// Aprobar un teletrabajo
	Teletrabajo aprobarTeletrabajo(int id);
	
	// Rechazar un teletrabajo
	Teletrabajo rechazarTeletrabajo(int id);
	
	// Obtener teletrabajos pendientes
	List<Teletrabajo> obtenerTeletrabajosPendientes();
	
	
	
}
