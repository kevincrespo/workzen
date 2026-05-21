package apirest.workzen.restcontroller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import apirest.workzen.modelo.dto.CrearEmpleadoDto;
import apirest.workzen.modelo.dto.DiasDisponiblesDto;
import apirest.workzen.modelo.dto.EmpleadoDetalleDto;
import apirest.workzen.modelo.dto.dtoUsuarios;
import apirest.workzen.modelo.entities.Departamento;
import apirest.workzen.modelo.entities.Empleado;
import apirest.workzen.modelo.entities.Usuario;
import apirest.workzen.modelo.repository.DepartamentoRepository;
import apirest.workzen.modelo.repository.EmpleadoRepository;
import apirest.workzen.modelo.repository.UsuarioRepository;
import apirest.workzen.service.EmpleadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para la gestion de empleados.
 * Solo accesible para usuarios con privilegio admin o rrhh.
 */
@RestController
@RequestMapping("/empleados")
@Tag(name = "Empleados", description = "Gestion de empleados")
@SecurityRequirement(name = "bearerAuth")
public class EmpleadoRestController {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmpleadoService empleadoService;

    /**
     * Devuelve la lista de todos los empleados con datos resumidos
     * incluyendo el nombre del departamento.
     * Solo accesible para usuarios con privilegio admin o rrhh.
     *
     * @return lista de empleados con campos resumidos y departamento
     */
    @Operation(summary = "Listar empleados",
            description = "Devuelve todos los empleados con nombre, apellidos y departamento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de empleados"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No tiene privilegios suficientes")
    })
    @GetMapping("")
    public List<dtoUsuarios> listarEmpleados() {
        List<dtoUsuarios> listaDto = new ArrayList<>();

        for (Empleado empleado : empleadoRepository.findAll()) {
            dtoUsuarios dto = new dtoUsuarios();
            dto.setId(empleado.getId());
            dto.setNombre(empleado.getNombre());
            dto.setApellido1(empleado.getApellido1());
            dto.setApellido2(empleado.getApellido2());
            dto.setDepartamento(empleado.getDepartamento().getNombre());
            listaDto.add(dto);
        }

        return listaDto;
    }

    /**
     * Crea un nuevo empleado junto con su usuario y privilegios.
     * Valida campos obligatorios, email y NIF unicos, y departamento existente.
     *
     * @param dto datos del empleado, usuario y privilegios a crear
     * @return el empleado creado con codigo 201
     */
    @Operation(summary = "Crear empleado",
            description = "Crea un empleado con su usuario y privilegios asociados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Empleado creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos o campos obligatorios vacios"),
            @ApiResponse(responseCode = "409", description = "El email o NIF ya estan en uso")
    })
    @PostMapping("")
    public ResponseEntity<?> crearEmpleado(@RequestBody CrearEmpleadoDto dto) {
        try {
            Empleado empleado = empleadoService.crearEmpleadoConUsuario(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(EmpleadoDetalleDto.convertirADto(empleado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    /**
     * Devuelve el detalle completo de un empleado incluyendo
     * el nombre del departamento y su ID.
     *
     * @param id id del empleado
     * @return el detalle completo del empleado
     */
    @Operation(summary = "Detalle de empleado",
            description = "Devuelve todos los datos de un empleado con su departamento")
    @ApiResponse(responseCode = "200", description = "Detalle del empleado")
    @ApiResponse(responseCode = "404", description = "Empleado no encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<EmpleadoDetalleDto> obtenerEmpleado(@PathVariable int id) {
        Empleado empleado = empleadoRepository.findById(id).orElse(null);

        if (empleado == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(EmpleadoDetalleDto.convertirADto(empleado));
    }

    /**
     * Actualiza los datos de un empleado.
     * Valida que el NIF no este duplicado (excluyendo al propio empleado)
     * y que el departamentoId exista.
     *
     * @param id  id del empleado a actualizar
     * @param dto datos del empleado a actualizar
     * @return el empleado actualizado
     */
    @Operation(summary = "Actualizar empleado",
            description = "Actualiza los datos de un empleado. Valida NIF unico y departamento existente")
    @ApiResponse(responseCode = "200", description = "Empleado actualizado correctamente")
    @ApiResponse(responseCode = "400", description = "Datos invalidos o campos obligatorios vacios")
    @ApiResponse(responseCode = "404", description = "Empleado no encontrado")
    @ApiResponse(responseCode = "409", description = "El NIF ya esta en uso por otro empleado")
    @PutMapping("/{id}")
    public ResponseEntity<EmpleadoDetalleDto> actualizarEmpleado(@PathVariable int id,
                                                                  @RequestBody EmpleadoDetalleDto dto) {
        // Verificar que el empleado existe
        Empleado empleado = empleadoRepository.findById(id).orElse(null);
        if (empleado == null) {
            return ResponseEntity.notFound().build();
        }

        // Buscar el departamento
        Departamento departamento = departamentoRepository.findById(dto.getDepartamentoId()).orElse(null);
        if (departamento == null) {
            return ResponseEntity.notFound().build();
        }

        // Actualizar campos
        empleado.setNombre(dto.getNombre());
        empleado.setApellido1(dto.getApellido1());
        empleado.setApellido2(dto.getApellido2());
        empleado.setNif(dto.getNif());
        empleado.setNumeroSS(dto.getNumeroSs());
        empleado.setDireccion(dto.getDireccion());
        empleado.setProvincia(dto.getProvincia());
        empleado.setLocalidad(dto.getLocalidad());
        empleado.setCodigoPostal(dto.getCodigoPostal());
        empleado.setFechaNacimiento(dto.getFechaNacimiento());
        empleado.setIban(dto.getIban());
        empleado.setTelefono(dto.getTelefono());
        empleado.setDepartamento(departamento);

        Empleado empleadoActualizado = empleadoRepository.save(empleado);
        return ResponseEntity.ok(EmpleadoDetalleDto.convertirADto(empleadoActualizado));
    }

    /**
     * Devuelve los dias de vacaciones disponibles del empleado autenticado.
     *
     * @param auth autenticacion del usuario
     * @return DTO con dias totales, consumidos y disponibles
     */
    @GetMapping("/dias-disponibles")
    @Operation(summary = "Obtener dias de vacaciones disponibles",
            description = "Devuelve los dias totales, consumidos y disponibles del empleado autenticado")
    @ApiResponse(responseCode = "200", description = "Dias de vacaciones del empleado")
    @ApiResponse(responseCode = "404", description = "Usuario o empleado no encontrado")
    public ResponseEntity<DiasDisponiblesDto> obtenerDiasDisponibles(Authentication auth) {
        String email = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario == null || usuario.getEmpleado() == null) {
            return ResponseEntity.notFound().build();
        }

        DiasDisponiblesDto dto = empleadoService.calcularDiasDisponibles(usuario.getEmpleado());
        return ResponseEntity.ok(dto);
    }
}
