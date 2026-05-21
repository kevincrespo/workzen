package apirest.workzen.restcontroller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import apirest.workzen.modelo.dto.NominaDto;
import apirest.workzen.modelo.entities.Empleado;
import apirest.workzen.modelo.entities.Nomina;
import apirest.workzen.modelo.entities.Notificacion;
import apirest.workzen.modelo.entities.Usuario;
import apirest.workzen.modelo.repository.EmpleadoRepository;
import apirest.workzen.modelo.repository.UsuarioRepository;
import apirest.workzen.service.NominaService;
import apirest.workzen.service.NotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para la gestion de nominas.
 * Permite listar, descargar, crear, actualizar y eliminar nominas.
 */
@RestController
@RequestMapping("/nominas")
@Tag(name = "Nominas", description = "Gestion de nominas de empleados")
@SecurityRequirement(name = "bearerAuth")
public class NominaRestController {

    @Autowired
    private NominaService nominaService;

    @Autowired
    private NotificacionService notificacionService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Value("${nominas.upload-dir}")
    private String directorioArchivos;

    /**
     * Lista las nominas del usuario autenticado filtradas por año.
     * Devuelve un DTO con la URL de descarga del archivo.
     *
     * @param year año para filtrar las nominas
     * @return lista de NominaDto con la URL de descarga
     */
    @Operation(summary = "Listar nominas del usuario logueado", description = "Devuelve las nominas del empleado asociado al usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de nominas"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("")
    public List<NominaDto> nominas(@RequestParam int year) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario == null || usuario.getEmpleado() == null) {
            return new ArrayList<>();
        }

        List<NominaDto> nominasDto = new ArrayList<>();
        for (Nomina nomina : nominaService.findByEmpleadoIdAndYear(usuario.getEmpleado().getId(), year)) {
            NominaDto dto = new NominaDto();
            dto.setId(nomina.getId());
            dto.setFecha(nomina.getFecha());
            dto.setArchivoUrl("/nominas/" + nomina.getId() + "/descargar");
            nominasDto.add(dto);
        }
        return nominasDto;
    }

    /**
     * Descarga el archivo PDF de una nomina por su ID.
     *
     * @param id identificador de la nomina
     * @return el archivo como recurso descargable
     */
    @Operation(summary = "Descargar archivo de nomina", description = "Devuelve el archivo PDF de la nomina")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Archivo descargado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Nomina o archivo no encontrado")
    })
    @GetMapping("/{id}/descargar")
    public ResponseEntity<Resource> descargarNomina(@Parameter(description = "ID de la nomina") @PathVariable int id) throws IOException {
        Nomina nomina = nominaService.findOne(id);
        if (nomina == null) {
            return ResponseEntity.notFound().build();
        }

        Path rutaArchivo = Paths.get(directorioArchivos).resolve(nomina.getArchivo()).normalize();
        Resource recurso = new UrlResource(rutaArchivo.toUri());

        if (!recurso.exists()) {
            return ResponseEntity.notFound().build();
        }

        String tipoContenido = Files.probeContentType(rutaArchivo);
        if (tipoContenido == null) {
            tipoContenido = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(tipoContenido))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomina.getArchivo() + "\"")
                .body(recurso);
    }

    /**
     * Sube un archivo PDF de nomina asociado a un empleado y una fecha.
     * Solo accesible para usuarios con privilegio admin o rrhh.
     *
     * @param empleadoId ID del empleado
     * @param fecha fecha de la nomina (formato yyyy-MM-dd)
     * @param archivo archivo PDF de la nomina
     * @return 201 Created si se subio correctamente
     */
    @Operation(summary = "Subir nomina", description = "Sube un archivo PDF de nomina para un empleado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Nomina subida correctamente"),
            @ApiResponse(responseCode = "400", description = "Faltan campos o el archivo no es PDF"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No tiene privilegios suficientes"),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado")
    })
    @PostMapping("/subir")
    public ResponseEntity<Void> subirNomina(
            @RequestParam int empleadoId,
            @RequestParam String fecha,
            @RequestParam MultipartFile archivo) throws IOException {

        // Buscar el empleado
        Empleado empleado = empleadoRepository.findById(empleadoId).orElse(null);
        if (empleado == null) {
            return ResponseEntity.notFound().build();
        }

        // Crear el directorio si no existe
        Path directorio = Paths.get(directorioArchivos);
        if (!Files.exists(directorio)) {
            Files.createDirectories(directorio);
        }

        // Generar nombre unico para el archivo
        String nombreArchivo = UUID.randomUUID() + ".pdf";

        // Guardar el archivo en el servidor
        Path rutaArchivo = directorio.resolve(nombreArchivo);
        Files.copy(archivo.getInputStream(), rutaArchivo);

        // Crear el registro en la base de datos
        Nomina nomina = new Nomina();
        nomina.setEmpleado(empleado);
        nomina.setFecha(LocalDate.parse(fecha));
        nomina.setArchivo(nombreArchivo);
        nominaService.insert(nomina);

        // Crear notificacion para el empleado
        notificacionService.crear(empleado, Notificacion.Tipo.nomina_nueva,
                "Tienes una nueva nomina disponible (" + fecha + ")");

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Obtiene una nomina por su ID.
     *
     * @param id identificador de la nomina
     * @return la nomina encontrada o 404
     */
    @Operation(summary = "Obtener nomina por ID", description = "Devuelve una nomina segun su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nomina encontrada"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Nomina no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Nomina> mostrarNomina(@Parameter(description = "ID de la nomina") @PathVariable int id) {
        Nomina nomina = nominaService.findOne(id);
        if (nomina != null) {
            return ResponseEntity.ok(nomina);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Crea una nueva nomina.
     *
     * @param nomina datos de la nomina a crear
     * @return la nomina creada
     */
    @Operation(summary = "Crear nomina", description = "Crea una nueva nomina")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nomina creada"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PostMapping("")
    public Nomina crearNomina(@RequestBody Nomina nomina) {
        return nominaService.insert(nomina);
    }

    /**
     * Actualiza una nomina existente.
     *
     * @param id identificador de la nomina a actualizar
     * @param nomina datos actualizados
     * @return la nomina actualizada o 404
     */
    @Operation(summary = "Actualizar nomina", description = "Actualiza una nomina existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nomina actualizada"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Nomina no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Nomina> actualizarNomina(@Parameter(description = "ID de la nomina") @PathVariable int id,
            @RequestBody Nomina nomina) {
        Nomina actualizada = nominaService.update(id, nomina);
        if (actualizada != null) {
            return ResponseEntity.ok(actualizada);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Elimina una nomina por su ID.
     *
     * @param id identificador de la nomina a eliminar
     * @return 200 si se elimino o 404 si no existe
     */
    @Operation(summary = "Eliminar nomina", description = "Elimina una nomina por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nomina eliminada"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Nomina no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarNomina(@Parameter(description = "ID de la nomina") @PathVariable int id) {
        if (nominaService.delete(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Cuenta el numero de nominas del usuario autenticado.
     *
     * @return cantidad de nominas
     */
    @GetMapping("/contar")
    public ResponseEntity<Integer> contarNominas() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario == null || usuario.getEmpleado() == null) {
            return null;
        }

        return ResponseEntity.ok(nominaService.findByEmpleadoId(usuario.getEmpleado().getId()));
    }
}
