package apirest.workzen.modelo.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import apirest.workzen.modelo.entities.Fichaje;

/**
 * Repositorio JPA para la entidad Fichaje.
 */
public interface FichajeRepository extends JpaRepository<Fichaje, Integer> {

    /**
     * Busca un fichaje activo de un empleado.
     *
     * @param empleadoId id del empleado
     * @param estado     estado del fichaje
     * @return el fichaje encontrado o vacio
     */
    Optional<Fichaje> findByEmpleadoIdAndEstado(int empleadoId, Fichaje.Estado estado);

    /**
     * Devuelve los fichajes de un empleado.
     *
     * @param empleadoId id del empleado
     * @return lista de fichajes del empleado
     */
    List<Fichaje> findByEmpleadoId(int empleadoId);

    /**
     * Devuelve los fichajes de un empleado en un rango de fechas.
     *
     * @param empleadoId id del empleado
     * @param desde      fecha de inicio
     * @param hasta      fecha de fin
     * @return lista de fichajes ordenados por fecha de entrada descendente
     */
    List<Fichaje> findByEmpleadoIdAndFechaEntradaBetweenOrderByFechaEntradaDesc(
            int empleadoId, LocalDateTime desde, LocalDateTime hasta);

    /**
     * Devuelve todos los fichajes en un rango de fechas (admin).
     *
     * @param desde fecha de inicio
     * @param hasta fecha de fin
     * @return lista de fichajes ordenados por fecha de entrada descendente
     */
    List<Fichaje> findByFechaEntradaBetweenOrderByFechaEntradaDesc(
            LocalDateTime desde, LocalDateTime hasta);
}
