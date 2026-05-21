package apirest.workzen.restcontroller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import apirest.workzen.modelo.dto.JustificanteDto;
import apirest.workzen.modelo.entities.Ausencia;
import apirest.workzen.modelo.entities.Justificante;
import apirest.workzen.modelo.entities.Usuario;
import apirest.workzen.modelo.repository.AusenciaRepository;
import apirest.workzen.modelo.repository.UsuarioRepository;
import apirest.workzen.service.JustificanteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para la gestion de justificantes medicos.
 * Los empleados pueden subir justificantes a sus ausencias medicas aprobadas.
 * Los administradores pueden ver y eliminar todos los justificantes.
 */
@Tag(name = "Justificantes", description = "Endpoints para la gestion de justificantes medicos")
@RestController
@RequestMapping("/justificantes")
public class JustificanteRestController {

    @Autowired
    private JustificanteService justificanteService;

    @Autowired
    private AusenciaRepository ausenciaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Value("${justificantes.upload-dir}")
    private String directorioArchivos;

    /** Tipos de archivo permitidos (PDF e imagenes). */
    private static final List<String> TIPOS_PERMITIDOS = List.of(
            "application/pdf", "image/jpeg", "image/png"
    );

    /**
     * Obtiene las ausencias medicas aprobadas del empleado que aun no tienen justificante.
     *
     * @param auth autenticacion del usuario
     * @return lista de ausencias pendientes de justificante
     */
    @Operation(summary = "Ausencias pendientes de justificante",
            description = "Devuelve las ausencias medicas aprobadas del empleado sin justificante")
    @ApiResponse(responseCode = "200", description = "Lista de ausencias")
    @GetMapping("/ausencias-pendientes")
    public ResponseEntity<List<Ausencia>> ausenciasPendientes(Authentication auth) {
        Integer empleadoId = obtenerEmpleadoId(auth);
        if (empleadoId == null) {
            return ResponseEntity.notFound().build();
        }

        // Obtener ausencias medicas aprobadas del empleado
        List<Ausencia> ausencias = ausenciaRepository.findByEmpleadoId(empleadoId)
                .stream()
                .filter(ausencia -> ausencia.getTipo() == Ausencia.Tipo.medico
                        && ausencia.getEstado() == Ausencia.Estado.aprobado
                        && !justificanteService.existePorAusencia(ausencia.getId()))
                .toList();

        return ResponseEntity.ok(ausencias);
    }

    /**
     * Obtiene los justificantes del empleado autenticado.
     *
     * @param auth autenticacion del usuario
     * @return lista de justificantes con datos de la ausencia
     */
    @Operation(summary = "Mis justificantes", description = "Devuelve los justificantes del empleado autenticado")
    @ApiResponse(responseCode = "200", description = "Lista de justificantes")
    @GetMapping("/mis-justificantes")
    public ResponseEntity<List<JustificanteDto>> misJustificantes(Authentication auth) {
        Integer empleadoId = obtenerEmpleadoId(auth);
        if (empleadoId == null) {
            return ResponseEntity.notFound().build();
        }

        List<JustificanteDto> justificantes = justificanteService.obtenerPorEmpleado(empleadoId)
                .stream()
                .map(justificante -> JustificanteDto.convertirADto(justificante, false))
                .toList();

        return ResponseEntity.ok(justificantes);
    }

    /**
     * Sube un justificante para una ausencia medica aprobada.
     * Acepta archivos PDF, JPG y PNG.
     *
     * @param ausenciaId id de la ausencia
     * @param motivo     motivo del justificante (opcional)
     * @param archivo    archivo a subir
     * @param auth       autenticacion del usuario
     * @return 201 si se subio correctamente
     */
    @Operation(summary = "Subir justificante", description = "Sube un justificante para una ausencia medica aprobada")
    @ApiResponse(responseCode = "201", description = "Justificante subido correctamente")
    @ApiResponse(responseCode = "400", description = "Archivo no valido o ausencia no elegible")
    @ApiResponse(responseCode = "409", description = "La ausencia ya tiene justificante")
    @PostMapping("/subir")
    public ResponseEntity<String> subirJustificante(
            @RequestParam int ausenciaId,
            @RequestParam(required = false) String motivo,
            @RequestParam MultipartFile archivo,
            Authentication auth) throws IOException {

        Integer empleadoId = obtenerEmpleadoId(auth);
        if (empleadoId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
        }

        // Buscar la ausencia
        Ausencia ausencia = ausenciaRepository.findById(ausenciaId).orElse(null);
        if (ausencia == null) {
            return ResponseEntity.notFound().build();
        }
        if (ausencia.getEmpleado().getId() != empleadoId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para esta ausencia");
        }

        // Validar que es medica y aprobada
        if (ausencia.getTipo() != Ausencia.Tipo.medico || ausencia.getEstado() != Ausencia.Estado.aprobado) {
            return ResponseEntity.badRequest().body("Solo se pueden adjuntar justificantes a ausencias medicas aprobadas");
        }

        // Validar que no tiene ya un justificante
        if (justificanteService.existePorAusencia(ausenciaId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Esta ausencia ya tiene un justificante");
        }

        // Crear directorio si no existe
        Path directorio = Paths.get(directorioArchivos);
        if (!Files.exists(directorio)) {
            Files.createDirectories(directorio);
        }

        // Generar nombre unico para el archivo
        String extension = obtenerExtension(archivo.getOriginalFilename());
        String nombreArchivo = UUID.randomUUID() + extension;

        // Guardar archivo en disco
        Path rutaArchivo = directorio.resolve(nombreArchivo);
        Files.copy(archivo.getInputStream(), rutaArchivo);

        // Guardar registro en BD
        Justificante justificante = Justificante.builder()
                .ausencia(ausencia)
                .motivo(motivo != null ? motivo : "")
                .archivo(nombreArchivo)
                .build();
        justificanteService.guardar(justificante);

        return ResponseEntity.status(HttpStatus.CREATED).body("Justificante subido correctamente");
    }

    /**
     * Descarga el archivo de un justificante.
     *
     * @param id id del justificante
     * @return el archivo como recurso descargable
     */
    @Operation(summary = "Descargar justificante", description = "Descarga el archivo de un justificante")
    @ApiResponse(responseCode = "200", description = "Archivo descargado")
    @ApiResponse(responseCode = "404", description = "Justificante o archivo no encontrado")
    @GetMapping("/{id}/descargar")
    public ResponseEntity<Resource> descargarJustificante(@PathVariable int id) throws IOException {
        Justificante justificante = justificanteService.buscarPorId(id);
        if (justificante == null) {
            return ResponseEntity.notFound().build();
        }

        Path rutaArchivo = Paths.get(directorioArchivos).resolve(justificante.getArchivo()).normalize();
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
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + justificante.getArchivo() + "\"")
                .body(recurso);
    }

    /**
     * Obtiene todos los justificantes (solo admin/rrhh).
     *
     * @return lista de todos los justificantes con datos del empleado
     */
    @Operation(summary = "Listar todos los justificantes", description = "Devuelve todos los justificantes (solo admin/rrhh)")
    @ApiResponse(responseCode = "200", description = "Lista de justificantes")
    @GetMapping("")
    public ResponseEntity<List<JustificanteDto>> listarTodos() {
        List<JustificanteDto> justificantes = justificanteService.obtenerTodos()
                .stream()
                .map(justificante -> JustificanteDto.convertirADto(justificante, true))
                .toList();

        return ResponseEntity.ok(justificantes);
    }

    /**
     * Elimina un justificante (solo admin/rrhh).
     *
     * @param id id del justificante
     * @return 204 si se elimino correctamente
     */
    @Operation(summary = "Eliminar justificante", description = "Elimina un justificante (solo admin/rrhh)")
    @ApiResponse(responseCode = "204", description = "Justificante eliminado")
    @ApiResponse(responseCode = "404", description = "Justificante no encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarJustificante(@PathVariable int id) {
        if (justificanteService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
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

    /**
     * Obtiene la extension de un nombre de archivo.
     */
    private String obtenerExtension(String nombreArchivo) {
        if (nombreArchivo != null && nombreArchivo.contains(".")) {
            return nombreArchivo.substring(nombreArchivo.lastIndexOf("."));
        }
        return ".pdf";
    }
}
