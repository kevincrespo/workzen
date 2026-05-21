package apirest.workzen.restcontroller;

import java.util.List;

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

import apirest.workzen.modelo.entities.Festivo;
import apirest.workzen.service.FestivoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para la gestion de festivos.
 * Solo accesible para usuarios con privilegio admin o rrhh.
 */
@Tag(name = "Festivos", description = "Endpoints para la gestion de dias festivos")
@RestController
@RequestMapping("/festivos")
@SecurityRequirement(name = "bearerAuth")
public class FestivoRestController {

    @Autowired
    private FestivoService festivoService;

    /**
     * Devuelve la lista de todos los festivos.
     *
     * @return lista de festivos
     */
    @Operation(summary = "Listar festivos",
            description = "Devuelve todos los dias festivos")
    @ApiResponse(responseCode = "200", description = "Lista de festivos")
    @GetMapping("")
    public ResponseEntity<List<Festivo>> listarFestivos() {
        return ResponseEntity.ok(festivoService.findAll());
    }

    /**
     * Crea un nuevo festivo.
     *
     * @param festivo datos del festivo (nombre y fecha)
     * @return el festivo creado
     */
    @Operation(summary = "Crear festivo",
            description = "Crea un nuevo dia festivo")
    @ApiResponse(responseCode = "201", description = "Festivo creado")
    @ApiResponse(responseCode = "400", description = "Datos invalidos")
    @PostMapping("")
    public ResponseEntity<Festivo> crearFestivo(@RequestBody Festivo festivo) {
        Festivo guardado = festivoService.insert(festivo);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    /**
     * Actualiza un festivo existente.
     *
     * @param id      ID del festivo
     * @param festivo datos actualizados (nombre y fecha)
     * @return el festivo actualizado
     */
    @Operation(summary = "Actualizar festivo",
            description = "Actualiza el nombre y/o fecha de un festivo")
    @ApiResponse(responseCode = "200", description = "Festivo actualizado")
    @ApiResponse(responseCode = "404", description = "Festivo no encontrado")
    @ApiResponse(responseCode = "400", description = "Datos invalidos")
    @PutMapping("/{id}")
    public ResponseEntity<Festivo> actualizarFestivo(
            @PathVariable int id,
            @RequestBody Festivo festivo) {

        Festivo existente = festivoService.findOne(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        existente.setNombre(festivo.getNombre().trim());
        existente.setFecha(festivo.getFecha());
        Festivo actualizado = festivoService.update(id, existente);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * Elimina un festivo.
     *
     * @param id ID del festivo
     * @return mensaje de confirmacion
     */
    @Operation(summary = "Eliminar festivo",
            description = "Elimina un dia festivo")
    @ApiResponse(responseCode = "200", description = "Festivo eliminado")
    @ApiResponse(responseCode = "404", description = "Festivo no encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarFestivo(@PathVariable int id) {
        Festivo existente = festivoService.findOne(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        festivoService.delete(id);
        return ResponseEntity.ok("Festivo eliminado");
    }
}
