package apirest.workzen.restcontroller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import apirest.workzen.modelo.dto.DepartamentoDto;
import apirest.workzen.modelo.entities.Departamento;
import apirest.workzen.modelo.repository.EmpleadoRepository;
import apirest.workzen.service.DepartamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para la gestion de departamentos.
 * Solo accesible para usuarios con privilegio admin o rrhh.
 */
@Tag(name = "Departamentos", description = "Endpoints para la gestion de departamentos")
@RestController
@RequestMapping("/departamentos")
@SecurityRequirement(name = "bearerAuth")
public class DepartamentoRestController {

    @Autowired
    private DepartamentoService departamentoService;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    /**
     * Devuelve la lista de todos los departamentos con el numero de empleados.
     *
     * @return lista de departamentos con contador de empleados
     */
    @Operation(summary = "Listar departamentos",
            description = "Devuelve todos los departamentos con el numero de empleados")
    @ApiResponse(responseCode = "200", description = "Lista de departamentos")
    @GetMapping("")
    public ResponseEntity<List<DepartamentoDto>> listarDepartamentos() {
        List<DepartamentoDto> dtos = departamentoService.findAll().stream()
                .map(dep -> DepartamentoDto.convertirADto(dep,
                        empleadoRepository.countByDepartamentoId(dep.getId())))
                .toList();
        return ResponseEntity.ok(dtos);
    }

    /**
     * Crea un nuevo departamento.
     *
     * @param body JSON con campo "nombre"
     * @return el departamento creado
     */
    @Operation(summary = "Crear departamento",
            description = "Crea un nuevo departamento")
    @ApiResponse(responseCode = "201", description = "Departamento creado")
    @ApiResponse(responseCode = "400", description = "Nombre vacio o invalido")
    @PostMapping("")
    public ResponseEntity<DepartamentoDto> crearDepartamento(@RequestBody Map<String, String> body) {
        String nombre = body.get("nombre");

        Departamento nuevo = Departamento.builder()
                .nombre(nombre)
                .build();
        Departamento guardado = departamentoService.insert(nuevo);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DepartamentoDto.convertirADto(guardado, 0));
    }

    /**
     * Actualiza el nombre de un departamento existente.
     *
     * @param id   ID del departamento
     * @param body JSON con campo "nombre"
     * @return el departamento actualizado
     */
    @Operation(summary = "Actualizar departamento",
            description = "Actualiza el nombre de un departamento")
    @ApiResponse(responseCode = "200", description = "Departamento actualizado")
    @ApiResponse(responseCode = "404", description = "Departamento no encontrado")
    @ApiResponse(responseCode = "400", description = "Nombre vacio o invalido")
    @PutMapping("/{id}")
    public ResponseEntity<DepartamentoDto> actualizarDepartamento(
            @PathVariable int id,
            @RequestBody Map<String, String> body) {

        String nombre = body.get("nombre");

        Departamento existente = departamentoService.findOne(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        existente.setNombre(nombre);
        Departamento actualizado = departamentoService.update(id, existente);

        long numEmpleados = empleadoRepository.countByDepartamentoId(id);
        return ResponseEntity.ok(DepartamentoDto.convertirADto(actualizado, numEmpleados));
    }

    /**
     * Elimina un departamento si no tiene empleados asignados.
     *
     * @param id ID del departamento
     * @return mensaje de confirmacion o error
     */
    @Operation(summary = "Eliminar departamento",
            description = "Elimina un departamento si no tiene empleados asignados")
    @ApiResponse(responseCode = "200", description = "Departamento eliminado")
    @ApiResponse(responseCode = "404", description = "Departamento no encontrado")
    @ApiResponse(responseCode = "409", description = "No se puede eliminar: tiene empleados asignados")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarDepartamento(@PathVariable int id) {
        Departamento existente = departamentoService.findOne(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        long numEmpleados = empleadoRepository.countByDepartamentoId(id);
        if (numEmpleados > 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede eliminar el departamento: tiene " + numEmpleados + " empleado(s) asignado(s)");
        }

        departamentoService.delete(id);
        return ResponseEntity.ok("Departamento eliminado");
    }
}