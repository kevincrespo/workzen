package apirest.workzen.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;

import apirest.workzen.modelo.dto.SolicitudAusenciaDto;
import apirest.workzen.modelo.entities.Ausencia;

public interface AusenciaService extends ICrudGenerico<Ausencia, Integer>{
	
	// Validar que las fechas sean correctas (inicio <= fin)
	boolean validarFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin);
	
	// Validar que no se solape con otras ausencias del mismo empleado
	boolean validarSolapamiento(int empleadoId, LocalDateTime fechaInicio, LocalDateTime fechaFin);
	
	// Crear ausencia para un empleado
	Ausencia solicitarAusencia(Authentication auth, SolicitudAusenciaDto solicitud);

    // Obtener tipos de ausencia
    List<Ausencia.Tipo> obtenerTipos();
    
    // Obtener una lista de las ausencias de un empleado (PARA PRUEBAS EN POSTMAN)
    //List<Ausencia> obtenerAusenciasDeEmpleado(int empleadoId);
    
    // Obtener las ausencias del empleado logueado
    List<Ausencia> obtenerMisAusencias(Authentication auth);

    // Aprobar una ausencia
    Ausencia aprobarAusencia(int id);

    // Rechazar una ausencia
    Ausencia rechazarAusencia(int id);

    // Obtener ausencias pendientes
    List<Ausencia> obtenerAusenciasPendientes();

}
