package apirest.workzen.restcontroller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import apirest.workzen.modelo.dto.LoginRequest;
import apirest.workzen.modelo.dto.LoginResponse;
import apirest.workzen.modelo.entities.Usuario;
import apirest.workzen.security.JwtUtil;
import apirest.workzen.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador para la autenticacion de usuarios.
 * Proporciona el endpoint de login que devuelve un token JWT.
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticacion", description = "Endpoints para login y autenticacion")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Endpoint de login.
     * Recibe el email y la pass, y devuelve un token si es correcto.
     *
     * @param loginRequest Credenciales del usuario (email y pass)
     * @return Token JWT y datos del usuario, o error 401 si las credenciales son incorrectas
     */
    @Operation(summary = "Iniciar sesion", description = "Autentica al usuario y devuelve un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login correcto",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas",
                    content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        // Buscar usuario con las credenciales
        Usuario usuario = usuarioService.login(loginRequest.getEmail(), loginRequest.getPassword());

        // Si no existe, devolver error
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        }

        // Extraer nombres de privilegios del usuario
        List<String> privilegios = usuario.getPrivilegiosUsuarios() != null
                ? usuario.getPrivilegiosUsuarios().stream()
                        .map(privilegio -> privilegio.getPrivilegio().getNombre())
                        .toList()
                : null;

        // Obtener nombre del empleado
        String nombre = usuario.getEmpleado() != null ? usuario.getEmpleado().getNombre() : null;

        // Generar token JWT con nombre y privilegios
        String token = jwtUtil.generarToken(usuario.getEmail(), nombre, privilegios);

        // Crear respuesta con token y datos del usuario
        LoginResponse response = new LoginResponse(token);

        return ResponseEntity.ok(response);
    }
}
