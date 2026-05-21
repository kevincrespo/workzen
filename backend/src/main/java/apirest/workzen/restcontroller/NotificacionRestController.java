package apirest.workzen.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import apirest.workzen.modelo.entities.Notificacion;
import apirest.workzen.modelo.entities.Usuario;
import apirest.workzen.modelo.repository.UsuarioRepository;
import apirest.workzen.service.NotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para la gestion de notificaciones.
 * Permite al empleado ver sus notificaciones y marcarlas como leidas.
 */
@Tag(name = "Notificaciones", description = "Endpoints para las notificaciones del empleado")
@RestController
@RequestMapping("/notificaciones")
public class NotificacionRestController {

    @Autowired
    private NotificacionService notificacionService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Obtiene las notificaciones del empleado autenticado.
     *
     * @param auth autenticacion del usuario
     * @return lista de notificaciones ordenadas por fecha
     */
    @Operation(summary = "Obtener mis notificaciones", description = "Devuelve las notificaciones del empleado autenticado")
    @ApiResponse(responseCode = "200", description = "Lista de notificaciones")
    @ApiResponse(responseCode = "404", description = "Usuario o empleado no encontrado")
    @GetMapping("")
    public ResponseEntity<List<Notificacion>> obtenerNotificaciones(Authentication auth) {
        Integer empleadoId = obtenerEmpleadoId(auth);
        if (empleadoId == null) {
            return ResponseEntity.notFound().build();
        }
        List<Notificacion> notificaciones = notificacionService.obtenerPorEmpleado(empleadoId);
        return ResponseEntity.ok(notificaciones);
    }

    /**
     * Cuenta las notificaciones no leidas del empleado autenticado.
     *
     * @param auth autenticacion del usuario
     * @return cantidad de notificaciones no leidas
     */
    @Operation(summary = "Contar no leidas", description = "Devuelve el numero de notificaciones no leidas")
    @ApiResponse(responseCode = "200", description = "Cantidad de no leidas")
    @GetMapping("/no-leidas")
    public ResponseEntity<Long> contarNoLeidas(Authentication auth) {
        Integer empleadoId = obtenerEmpleadoId(auth);
        if (empleadoId == null) {
            return ResponseEntity.notFound().build();
        }
        long cantidad = notificacionService.contarNoLeidas(empleadoId);
        return ResponseEntity.ok(cantidad);
    }

    /**
     * Marca una notificacion como leida.
     *
     * @param id id de la notificacion
     * @return 200 si se marco, 404 si no existe
     */
    @Operation(summary = "Marcar como leida", description = "Marca una notificacion como leida")
    @ApiResponse(responseCode = "200", description = "Notificacion marcada como leida")
    @ApiResponse(responseCode = "404", description = "Notificacion no encontrada")
    @PutMapping("/{id}/leer")
    public ResponseEntity<Void> marcarComoLeida(@PathVariable int id) {
        boolean resultado = notificacionService.marcarComoLeida(id);
        if (resultado) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Marca todas las notificaciones del empleado como leidas.
     *
     * @param auth autenticacion del usuario
     * @return 200 si se marcaron todas
     */
    @Operation(summary = "Marcar todas como leidas", description = "Marca todas las notificaciones del empleado como leidas")
    @ApiResponse(responseCode = "200", description = "Todas marcadas como leidas")
    @PutMapping("/leer-todas")
    public ResponseEntity<Void> marcarTodasComoLeidas(Authentication auth) {
        Integer empleadoId = obtenerEmpleadoId(auth);
        if (empleadoId == null) {
            return ResponseEntity.notFound().build();
        }
        notificacionService.marcarTodasComoLeidas(empleadoId);
        return ResponseEntity.ok().build();
    }

    /**
     * Obtiene el ID del empleado a partir de la autenticacion.
     */
    private Integer obtenerEmpleadoId(Authentication auth) {
        String email = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario == null || usuario.getEmpleado() == null) {
            return null;
        }
        return usuario.getEmpleado().getId();
    }
}
