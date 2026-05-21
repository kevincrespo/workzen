package apirest.workzen.service;

import java.util.List;

import apirest.workzen.modelo.entities.Empleado;
import apirest.workzen.modelo.entities.Notificacion;

/**
 * Servicio para gestionar notificaciones de empleados.
 */
public interface NotificacionService {

    /**
     * Crea una notificacion para un empleado.
     *
     * @param empleado empleado destinatario
     * @param tipo     tipo de notificacion
     * @param mensaje  texto descriptivo
     * @return la notificacion creada
     */
    Notificacion crear(Empleado empleado, Notificacion.Tipo tipo, String mensaje);

    /**
     * Obtiene las notificaciones de un empleado (ordenadas por fecha, mas recientes primero).
     *
     * @param empleadoId id del empleado
     * @return lista de notificaciones
     */
    List<Notificacion> obtenerPorEmpleado(int empleadoId);

    /**
     * Cuenta las notificaciones no leidas de un empleado.
     *
     * @param empleadoId id del empleado
     * @return cantidad de notificaciones no leidas
     */
    long contarNoLeidas(int empleadoId);

    /**
     * Marca una notificacion como leida.
     *
     * @param notificacionId id de la notificacion
     * @return true si se marco correctamente, false si no existe
     */
    boolean marcarComoLeida(int notificacionId);

    /**
     * Marca todas las notificaciones de un empleado como leidas.
     *
     * @param empleadoId id del empleado
     */
    void marcarTodasComoLeidas(int empleadoId);
}
