package apirest.workzen.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import apirest.workzen.modelo.dto.EmpleadoDetalleDto;
import apirest.workzen.modelo.entities.Usuario;
import apirest.workzen.modelo.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para el perfil del empleado autenticado.
 */
@Tag(name = "Perfil", description = "Endpoint para consultar el perfil del empleado autenticado")
@RestController
@RequestMapping("/perfil")
public class PerfilRestController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Devuelve los datos completos del empleado autenticado.
     *
     * @param auth autenticacion del usuario
     * @return detalle del empleado autenticado
     */
    @Operation(summary = "Obtener mi perfil",
            description = "Devuelve los datos completos del empleado asociado al token JWT")
    @ApiResponse(responseCode = "200", description = "Perfil obtenido correctamente")
    @ApiResponse(responseCode = "404", description = "No se encontro empleado asociado al usuario")
    @GetMapping("")
    public ResponseEntity<EmpleadoDetalleDto> obtenerPerfil(Authentication auth) {
        String email = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario == null || usuario.getEmpleado() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(EmpleadoDetalleDto.convertirADto(usuario.getEmpleado()));
    }
}
