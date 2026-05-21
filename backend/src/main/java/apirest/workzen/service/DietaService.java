package apirest.workzen.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.Authentication;

import apirest.workzen.modelo.entities.Dieta;

/**
 * Interfaz de servicio para la gestion de dietas.
 */
public interface DietaService extends ICrudGenerico<Dieta, Integer> {

    /**
     * Valida que las fechas de inicio y fin sean correctas.
     *
     * @param fechaInicio fecha de inicio
     * @param fechaFin    fecha de fin
     * @return true si las fechas son validas
     */
    boolean validarFechas(LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Valida que no se solape con otras dietas del mismo empleado.
     *
     * @param empleadoId  id del empleado
     * @param fechaInicio fecha de inicio
     * @param fechaFin    fecha de fin
     * @return true si no hay solapamiento
     */
    boolean validarSolapamiento(int empleadoId, LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Crea una solicitud de dieta para el empleado autenticado.
     *
     * @param auth  autenticacion del usuario
     * @param dieta datos de la dieta
     * @return la dieta creada o null si hay error
     */
    Dieta solicitarDieta(Authentication auth, Dieta dieta);

    /**
     * Obtiene las dietas del empleado autenticado.
     *
     * @param auth autenticacion del usuario
     * @return lista de dietas del empleado
     */
    List<Dieta> obtenerMisDietas(Authentication auth);

    /**
     * Aprueba una dieta pendiente.
     *
     * @param id id de la dieta
     * @return la dieta aprobada o null si no existe
     */
    Dieta aprobarDieta(int id);

    /**
     * Rechaza una dieta pendiente.
     *
     * @param id id de la dieta
     * @return la dieta rechazada o null si no existe
     */
    Dieta rechazarDieta(int id);

    /**
     * Obtiene todas las dietas pendientes.
     *
     * @return lista de dietas pendientes
     */
    List<Dieta> obtenerPendientes();
}
