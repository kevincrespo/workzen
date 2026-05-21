package apirest.workzen.modelo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import apirest.workzen.modelo.entities.Notificacion;

/**
 * Repositorio JPA para la entidad Notificacion.
 */
public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {

    /**
     * Busca las notificaciones de un empleado ordenadas por fecha (mas recientes primero).
     */
    List<Notificacion> findByEmpleadoIdOrderByFechaCreacionDesc(int empleadoId);

    /**
     * Cuenta las notificaciones no leidas de un empleado.
     */
    long countByEmpleadoIdAndLeidaFalse(int empleadoId);

    /**
     * Busca las notificaciones no leidas de un empleado.
     */
    List<Notificacion> findByEmpleadoIdAndLeidaFalse(int empleadoId);
}
