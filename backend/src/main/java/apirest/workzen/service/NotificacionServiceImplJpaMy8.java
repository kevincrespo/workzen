package apirest.workzen.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import apirest.workzen.modelo.entities.Empleado;
import apirest.workzen.modelo.entities.Notificacion;
import apirest.workzen.modelo.repository.NotificacionRepository;

/**
 * Implementacion del servicio de notificaciones usando JPA.
 */
@Service
public class NotificacionServiceImplJpaMy8 implements NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Override
    public Notificacion crear(Empleado empleado, Notificacion.Tipo tipo, String mensaje) {
        Notificacion notificacion = Notificacion.builder()
                .empleado(empleado)
                .tipo(tipo)
                .mensaje(mensaje)
                .leida(false)
                .fechaCreacion(LocalDateTime.now())
                .build();
        return notificacionRepository.save(notificacion);
    }

    @Override
    public List<Notificacion> obtenerPorEmpleado(int empleadoId) {
        return notificacionRepository.findByEmpleadoIdOrderByFechaCreacionDesc(empleadoId);
    }

    @Override
    public long contarNoLeidas(int empleadoId) {
        return notificacionRepository.countByEmpleadoIdAndLeidaFalse(empleadoId);
    }

    @Override
    public boolean marcarComoLeida(int notificacionId) {
        Notificacion notificacion = notificacionRepository.findById(notificacionId).orElse(null);
        if (notificacion == null) {
            return false;
        }
        notificacion.setLeida(true);
        notificacionRepository.save(notificacion);
        return true;
    }

    @Override
    public void marcarTodasComoLeidas(int empleadoId) {
        List<Notificacion> noLeidas = notificacionRepository.findByEmpleadoIdAndLeidaFalse(empleadoId);
        for (Notificacion n : noLeidas) {
            n.setLeida(true);
        }
        notificacionRepository.saveAll(noLeidas);
    }
}
