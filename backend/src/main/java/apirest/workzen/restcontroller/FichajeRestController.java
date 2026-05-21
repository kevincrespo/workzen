package apirest.workzen.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import apirest.workzen.modelo.dto.FichajeDto;
import apirest.workzen.modelo.entities.Empleado;
import apirest.workzen.modelo.entities.Fichaje;
import apirest.workzen.modelo.entities.Usuario;
import apirest.workzen.modelo.repository.UsuarioRepository;
import apirest.workzen.service.FichajeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para la gestion de fichajes.
 */
@Tag(name = "Fichajes", description = "Endpoints para la gestion de fichajes")
@RestController
@RequestMapping("/fichajes")
public class FichajeRestController {

    @Autowired
    private FichajeService fichajeService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Obtiene el fichaje activo del empleado autenticado.
     *
     * @param auth autenticacion del usuario
     * @return el fichaje activo o 204 si no existe
     */
    @Operation(summary = "Obtener fichaje activo",
            description = "Devuelve el fichaje activo del empleado autenticado con sus pausas")
    @ApiResponse(responseCode = "200", description = "Fichaje activo encontrado")
    @ApiResponse(responseCode = "204", description = "No hay fichaje activo")
    @GetMapping("/activo")
    public ResponseEntity<FichajeDto> obtenerActivo(Authentication auth) {
        Fichaje fichaje = fichajeService.obtenerFichajeActivo(auth);

        if (fichaje == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(FichajeDto.convertirADto(fichaje));
    }

    /**
     * Inicia un nuevo fichaje para el empleado autenticado.
     *
     * @param auth autenticacion del usuario
     * @return el fichaje creado o 409 si ya existe uno activo
     */
    @Operation(summary = "Iniciar fichaje",
            description = "Crea un nuevo fichaje con estado activo")
    @ApiResponse(responseCode = "201", description = "Fichaje iniciado correctamente")
    @ApiResponse(responseCode = "409", description = "Ya existe un fichaje activo")
    @PostMapping("/iniciar")
    public ResponseEntity<FichajeDto> iniciar(Authentication auth) {
        Fichaje fichaje = fichajeService.iniciarFichaje(auth);

        if (fichaje == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(FichajeDto.convertirADto(fichaje));
    }

    /**
     * Crea una pausa en el fichaje indicado.
     *
     * @param auth autenticacion del usuario
     * @param id   id del fichaje
     * @return el fichaje actualizado
     */
    @Operation(summary = "Pausar fichaje",
            description = "Crea una nueva pausa en el fichaje indicado")
    @ApiResponse(responseCode = "200", description = "Pausa creada correctamente")
    @ApiResponse(responseCode = "400", description = "Ya hay una pausa abierta")
    @ApiResponse(responseCode = "403", description = "El fichaje no pertenece al empleado")
    @ApiResponse(responseCode = "404", description = "Fichaje no encontrado")
    @PutMapping("/{id}/pausar")
    public ResponseEntity<FichajeDto> pausar(Authentication auth, @PathVariable int id) {
        Fichaje fichaje = fichajeService.pausarFichaje(auth, id);

        if (fichaje == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(FichajeDto.convertirADto(fichaje));
    }

    /**
     * Reanuda el fichaje cerrando la pausa activa.
     *
     * @param auth autenticacion del usuario
     * @param id   id del fichaje
     * @return el fichaje actualizado
     */
    @Operation(summary = "Reanudar fichaje",
            description = "Finaliza la pausa abierta del fichaje indicado")
    @ApiResponse(responseCode = "200", description = "Pausa finalizada correctamente")
    @ApiResponse(responseCode = "400", description = "No hay pausa abierta para reanudar")
    @ApiResponse(responseCode = "403", description = "El fichaje no pertenece al empleado")
    @ApiResponse(responseCode = "404", description = "Fichaje no encontrado")
    @PutMapping("/{id}/reanudar")
    public ResponseEntity<FichajeDto> reanudar(Authentication auth, @PathVariable int id) {
        Fichaje fichaje = fichajeService.reanudarFichaje(auth, id);

        if (fichaje == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(FichajeDto.convertirADto(fichaje));
    }

    /**
     * Finaliza el fichaje estableciendo fecha de salida y estado finalizado.
     *
     * @param auth autenticacion del usuario
     * @param id   id del fichaje
     * @return el fichaje finalizado
     */
    @Operation(summary = "Finalizar fichaje",
            description = "Finaliza el fichaje y cierra pausas abiertas automaticamente")
    @ApiResponse(responseCode = "200", description = "Fichaje finalizado correctamente")
    @ApiResponse(responseCode = "403", description = "El fichaje no pertenece al empleado")
    @ApiResponse(responseCode = "404", description = "Fichaje no encontrado")
    @PutMapping("/{id}/finalizar")
    public ResponseEntity<FichajeDto> finalizar(Authentication auth, @PathVariable int id) {
        Fichaje fichaje = fichajeService.finalizarFichaje(auth, id);

        if (fichaje == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(FichajeDto.convertirADto(fichaje));
    }

    /**
     * Obtiene los fichajes del empleado autenticado filtrados por mes y anio.
     *
     * @param auth autenticacion del usuario
     * @param mes  mes (1-12)
     * @param anio anio
     * @return lista de fichajes del mes
     */
    @Operation(summary = "Mis fichajes por mes",
            description = "Devuelve los fichajes del empleado autenticado para un mes y anio")
    @ApiResponse(responseCode = "200", description = "Lista de fichajes")
    @GetMapping("/mis-fichajes")
    public ResponseEntity<List<FichajeDto>> misFichajes(
            Authentication auth,
            @RequestParam int mes,
            @RequestParam int anio) {

        String email = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario == null || usuario.getEmpleado() == null) {
            return ResponseEntity.badRequest().build();
        }

        Empleado empleado = usuario.getEmpleado();
        List<FichajeDto> fichajes = fichajeService.obtenerPorEmpleadoYMes(empleado.getId(), mes, anio)
                .stream()
                .map(FichajeDto::convertirADto)
                .toList();

        return ResponseEntity.ok(fichajes);
    }

    /**
     * Obtiene todos los fichajes filtrados por mes y anio (admin/rrhh).
     *
     * @param mes  mes (1-12)
     * @param anio anio
     * @return lista de todos los fichajes del mes
     */
    @Operation(summary = "Todos los fichajes por mes (admin)",
            description = "Devuelve todos los fichajes de todos los empleados para un mes y anio")
    @ApiResponse(responseCode = "200", description = "Lista de fichajes")
    @GetMapping("/todos")
    public ResponseEntity<List<FichajeDto>> todosFichajes(
            @RequestParam int mes,
            @RequestParam int anio) {

        List<FichajeDto> fichajes = fichajeService.obtenerTodosPorMes(mes, anio)
                .stream()
                .map(f -> FichajeDto.convertirADto(f, true))
                .toList();

        return ResponseEntity.ok(fichajes);
    }
}
