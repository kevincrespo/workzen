package apirest.workzen.service;

import java.util.List;

import org.springframework.security.core.Authentication;

import apirest.workzen.modelo.entities.Fichaje;

/**
 * Interfaz de servicio para la gestion de fichajes.
 */
public interface FichajeService extends ICrudGenerico<Fichaje, Integer> {

    /**
     * Obtiene el fichaje activo del empleado autenticado.
     *
     * @param auth autenticacion del usuario
     * @return el fichaje activo o null si no existe
     */
    Fichaje obtenerFichajeActivo(Authentication auth);

    /**
     * Inicia un nuevo fichaje para el empleado autenticado.
     *
     * @param auth autenticacion del usuario
     * @return el fichaje creado, o null si ya existe uno activo o hay error
     */
    Fichaje iniciarFichaje(Authentication auth);

    /**
     * Crea una pausa en el fichaje indicado.
     *
     * @param auth autenticacion del usuario
     * @param fichajeId id del fichaje
     * @return el fichaje actualizado, o null si hay error
     */
    Fichaje pausarFichaje(Authentication auth, int fichajeId);

    /**
     * Reanuda el fichaje cerrando la pausa activa.
     *
     * @param auth autenticacion del usuario
     * @param fichajeId id del fichaje
     * @return el fichaje actualizado, o null si hay error
     */
    Fichaje reanudarFichaje(Authentication auth, int fichajeId);

    /**
     * Finaliza el fichaje estableciendo fecha de salida y estado finalizado.
     *
     * @param auth autenticacion del usuario
     * @param fichajeId id del fichaje
     * @return el fichaje finalizado, o null si hay error
     */
    Fichaje finalizarFichaje(Authentication auth, int fichajeId);

    /**
     * Obtiene los fichajes de un empleado filtrados por mes y año.
     *
     * @param empleadoId id del empleado
     * @param mes        mes (1-12)
     * @param anio       año
     * @return lista de fichajes del mes
     */
    List<Fichaje> obtenerPorEmpleadoYMes(int empleadoId, int mes, int anio);

    /**
     * Obtiene todos los fichajes filtrados por mes y año (admin).
     *
     * @param mes  mes (1-12)
     * @param anio año
     * @return lista de todos los fichajes del mes
     */
    List<Fichaje> obtenerTodosPorMes(int mes, int anio);
}
