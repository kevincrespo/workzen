package apirest.workzen.restcontroller;

import java.util.ArrayList;
import java.util.List;

import apirest.workzen.modelo.dto.dtoUsuarios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import apirest.workzen.modelo.entities.Usuario;
import apirest.workzen.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para gestionar usuarios.
 * Todos los endpoints requieren autenticacion JWT.
 */
@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuarios", description = "Gestion de usuarios del sistema")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioRestController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Obtiene un usuario por su ID.
     */
    @Operation(summary = "Obtener usuario por ID", description = "Devuelve un usuario segun su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("{id}")
    public Usuario usuario(@Parameter(description = "ID del usuario") @PathVariable int id) {
        return usuarioService.findOne(id);
    }

    /**
     * Obtiene todos los usuarios.
     */
    @Operation(summary = "Listar usuarios", description = "Devuelve todos los usuarios del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("")
    public List<Usuario> todos() {
        return usuarioService.findAll();
    }

    /**
     * Devuelve una lista de usuarios con nombre, apellido1 y apellido 2
     * @see dtoUsuarios
     */
    @GetMapping("/listado")
    public List<dtoUsuarios> listado() {
        List<dtoUsuarios> listaDto = new ArrayList<>();

        for (Usuario usuario : usuarioService.findAll()) {
            if (usuario.getEmpleado() != null) {
                dtoUsuarios dto = new dtoUsuarios();
                dto.setNombre(usuario.getEmpleado().getNombre());
                dto.setApellido1(usuario.getEmpleado().getApellido1());
                dto.setApellido2(usuario.getEmpleado().getApellido2());
                dto.setId(usuario.getEmpleado().getId());
                listaDto.add(dto);
            }
        }

        return listaDto;
    }
}
