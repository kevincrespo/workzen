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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import apirest.workzen.modelo.dto.DietaAdminDto;
import apirest.workzen.modelo.dto.DietaDto;
import apirest.workzen.modelo.entities.Dieta;
import apirest.workzen.modelo.entities.Notificacion;
import apirest.workzen.service.DietaService;
import apirest.workzen.service.NotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para la gestion de dietas.
 */
@Tag(name = "Dietas", description = "Endpoints para la gestion de dietas")
@RestController
@RequestMapping("/dietas")
public class DietaRestController {

    @Autowired
    private DietaService dietaService;

    @Autowired
    private NotificacionService notificacionService;

    /**
     * Obtiene las dietas del empleado autenticado.
     *
     * @param auth autenticacion del usuario
     * @return lista de dietas del empleado
     */
    @Operation(summary = "Obtener mis dietas", description = "Devuelve las dietas del empleado autenticado")
    @ApiResponse(responseCode = "200", description = "Lista de dietas obtenida correctamente")
    @GetMapping("/mis-dietas")
    public ResponseEntity<List<DietaDto>> misDietas(Authentication auth) {
        List<Dieta> dietas = dietaService.obtenerMisDietas(auth);
        if (dietas == null) {
            return ResponseEntity.badRequest().build();
        }
        List<DietaDto> dietasDto = dietas.stream()
                .map(DietaDto::convertirADto)
                .toList();
        return ResponseEntity.ok(dietasDto);
    }

    /**
     * Crea una nueva solicitud de dieta. El estado inicial es pendiente.
     *
     * @param auth  autenticacion del usuario
     * @param dieta datos de la dieta a solicitar
     * @return la dieta creada
     */
    @Operation(summary = "Solicitar dieta", description = "Crea una nueva solicitud de dieta con estado pendiente")
    @ApiResponse(responseCode = "201", description = "Dieta creada correctamente")
    @ApiResponse(responseCode = "400", description = "Datos invalidos")
    @ApiResponse(responseCode = "409", description = "Solapamiento con otra dieta existente")
    @PostMapping("/solicitar")
    public ResponseEntity<DietaDto> solicitarDieta(Authentication auth, @RequestBody Dieta dieta) {
        Dieta nuevaDieta = dietaService.solicitarDieta(auth, dieta);

        if (nuevaDieta == null) {
            return ResponseEntity.badRequest().build();
        }

        DietaDto dietaDto = DietaDto.convertirADto(nuevaDieta);
        return ResponseEntity.status(HttpStatus.CREATED).body(dietaDto);
    }

    /**
     * Obtiene todas las dietas de todos los empleados (solo admin/rrhh).
     *
     * @return lista de dietas con datos del empleado
     */
    @Operation(summary = "Obtener todas las dietas", description = "Devuelve todas las dietas de todos los empleados (solo admin/rrhh)")
    @ApiResponse(responseCode = "200", description = "Lista de dietas obtenida correctamente")
    @GetMapping("")
    public ResponseEntity<List<DietaAdminDto>> findAll() {
        List<DietaAdminDto> dietas = dietaService.findAll()
                .stream()
                .map(DietaAdminDto::convertirADto)
                .toList();
        return ResponseEntity.ok(dietas);
    }

    /**
     * Aprueba una dieta pendiente (solo admin/rrhh).
     *
     * @param id id de la dieta a aprobar
     * @return la dieta aprobada
     */
    @Operation(summary = "Aprobar dieta", description = "Aprueba una dieta pendiente (solo admin/rrhh)")
    @ApiResponse(responseCode = "200", description = "Dieta aprobada correctamente")
    @ApiResponse(responseCode = "404", description = "Dieta no encontrada")
    @PutMapping("/{id}/aprobar")
    public ResponseEntity<Dieta> aprobar(@PathVariable int id) {
        Dieta dieta = dietaService.aprobarDieta(id);
        if (dieta == null) {
            return ResponseEntity.notFound().build();
        }
        // Notificar al empleado
        notificacionService.crear(dieta.getEmpleado(), Notificacion.Tipo.dieta_aprobada,
                "Tu solicitud de dieta ha sido aprobada");
        return ResponseEntity.ok(dieta);
    }

    /**
     * Rechaza una dieta pendiente (solo admin/rrhh).
     *
     * @param id id de la dieta a rechazar
     * @return la dieta rechazada
     */
    @Operation(summary = "Rechazar dieta", description = "Rechaza una dieta pendiente (solo admin/rrhh)")
    @ApiResponse(responseCode = "200", description = "Dieta rechazada correctamente")
    @ApiResponse(responseCode = "404", description = "Dieta no encontrada")
    @PutMapping("/{id}/rechazar")
    public ResponseEntity<Dieta> rechazar(@PathVariable int id) {
        Dieta dieta = dietaService.rechazarDieta(id);
        if (dieta == null) {
            return ResponseEntity.notFound().build();
        }
        // Notificar al empleado
        notificacionService.crear(dieta.getEmpleado(), Notificacion.Tipo.dieta_rechazada,
                "Tu solicitud de dieta ha sido rechazada");
        return ResponseEntity.ok(dieta);
    }
}
