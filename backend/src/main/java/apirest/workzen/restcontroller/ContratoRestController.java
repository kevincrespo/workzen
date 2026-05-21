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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import apirest.workzen.modelo.dto.ContratoDto;
import apirest.workzen.modelo.entities.Contrato;
import apirest.workzen.modelo.entities.Empleado;
import apirest.workzen.modelo.entities.Notificacion;
import apirest.workzen.modelo.entities.Usuario;
import apirest.workzen.modelo.repository.ContratoRepository;
import apirest.workzen.modelo.repository.EmpleadoRepository;
import apirest.workzen.modelo.repository.UsuarioRepository;
import apirest.workzen.service.ContratoService;
import apirest.workzen.service.NotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/contratos")
@Tag(name = "Contratos", description = "Gestión de contratos de empleados")
@SecurityRequirement(name = "bearerAuth")
public class ContratoRestController {

    @Autowired
    private ContratoService contratoService;

    @Autowired
    private NotificacionService notificacionService;

    @Autowired
    private ContratoRepository contratoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Value("${nominas.upload-dir}")
    private String directorioArchivos;

    /**
     * Lista los contratos del usuario autenticado según su rol
     *
     * Empleado: solo devuelve sus propios contratos
     * ADMIN o RRHH: devuelve todos los contratos
     *
     * @param auth objeto de autenticación proporcionado por Spring Security
     * @return lista de contratos visibles para el usuario
     */
    @GetMapping("")
    @Operation(summary = "Listar contratos", description = "Lista contratos según rol del usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de contratos"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public List<Contrato> listarContratos(Authentication auth) {

        String email = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        // Si no existe el usuario o no tiene empleado asociado, devolvemos una lista vacía
        if (usuario == null || usuario.getEmpleado() == null) {
            return new ArrayList<>();
        }

        // Determinamos el rol del usuario
        String rol = auth.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .findFirst()
                .orElse("");

        // Filtrado según rol
        if (rol.equals("empleado")) {
            // Si es empleado, solo devolvemos sus propios contratos
            return contratoService.listarPorEmpleado(usuario.getEmpleado().getId());
        } else {
            // Si es ADMIN o RRHH, devolvemos todos los contratos
            return contratoService.listarTodos();
        }
    }

    /**
     *  devuelve la información de un contrato específico.
     * - Un empleado solo puede acceder a su propio contrato.
     * - ADMIN o RRHH pueden acceder a cualquier contrato.
     *
     * @param id ID del contrato
     * @param auth objeto de autenticación
     * @return ResponseEntity con el contrato y código HTTP
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener contrato por ID",
            description = "Devuelve un contrato por su ID. Empleado solo puede acceder a sus propios contratos; ADMIN y RRHH a cualquiera")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contrato encontrado"),
            @ApiResponse(responseCode = "400", description = "ID inválido"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Contrato no encontrado")
    })
    public ResponseEntity<Contrato> obtenerContratoPorId(
            @PathVariable Integer id,
            Authentication auth) {

        Contrato contrato = contratoService.buscarPorId(id);

        if (contrato == null) {
            return ResponseEntity.notFound().build();
        }

        // Obtener rol
        String rol = auth.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .findFirst()
                .orElse("");

        // Si es empleado, solo puede ver su propio contrato
        if (rol.equals("empleado") && contrato.getEmpleado().getId() != getEmpleadoIdDelUsuario(auth)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(contrato);
    }

    /**
     * Devuelve el numero total de contratos del empleado autenticado.
     *
     * @param auth autenticacion del usuario
     * @return numero de contratos del empleado
     */
    @GetMapping("/contar-activos")
    @Operation(summary = "Contar contratos del empleado",
            description = "Devuelve el numero total de contratos del empleado autenticado")
    @ApiResponse(responseCode = "200", description = "Numero de contratos")
    @ApiResponse(responseCode = "404", description = "Usuario o empleado no encontrado")
    public ResponseEntity<Integer> contarActivos(Authentication auth) {
        String email = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario == null || usuario.getEmpleado() == null) {
            return ResponseEntity.notFound().build();
        }

        int total = contratoRepository.findByEmpleadoId(usuario.getEmpleado().getId()).size();
        return ResponseEntity.ok(total);
    }

    // Metodo auxiliar para obtener el ID del empleado desde Authentication
    private Integer getEmpleadoIdDelUsuario(Authentication auth) {
        String email = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario == null || usuario.getEmpleado() == null) {
            return -1; // valor inválido para denegar acceso
        }
        return usuario.getEmpleado().getId();
    }

    /**
     * Este endpoint devuelve todos los contratos cuyo empleado contenga
     * el texto indicado en su nombre o apellidos. Solo tiene sentido
     * para usuarios con rol ADMIN o RRHH
     *
     * @param nombre texto a buscar en el nombre o apellidos del empleado
     * @return ResponseEntity con la lista de contratos encontrados y código HTTP
     */
    @GetMapping("/buscar-por-nombre")
    @Operation(summary = "Buscar contratos por nombre de empleado",
            description = "Devuelve los contratos cuyo empleado contiene el nombre indicado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de contratos"),
            @ApiResponse(responseCode = "400", description = "Parámetro 'nombre' inválido"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<List<Contrato>> listarPorNombreEmpleado(@RequestParam String nombre) {
        List<Contrato> contratos = contratoService.listarPorNombreEmpleado(nombre);

        return ResponseEntity.ok(contratos);
    }

    /**
     * devuelve todos los contratos cuyo tipo coincida
     * con el parámetro indicado .
     * Solo tiene sentido para usuarios con rol ADMIN o RRHH.
     *
     * @param tipo tipo de contrato a filtrar
     * @return ResponseEntity con la lista de contratos encontrados y código HTTP
     */
    @GetMapping("/buscar-por-tipo")
    @Operation(summary = "Buscar contratos por tipo",
            description = "Devuelve los contratos filtrados por tipo (por ejemplo, 'alta' o 'baja'). Solo ADMIN y RRHH")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de contratos"),
            @ApiResponse(responseCode = "400", description = "Parámetro 'tipo' inválido"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<List<Contrato>> listarPorTipo(@RequestParam String tipo) {
        List<Contrato> contratos = contratoService.listarPorTipo(tipo);

        return ResponseEntity.ok(contratos);
    }

    /**
     * devuelve todos los contratos cuya fecha se encuentre
     * entre 'fechaDesde' y 'fechaHasta' (inclusive). Solo
     * para usuarios con rol ADMIN o RRHH.
     *
     * @param fechaDesde fecha inicial del rango en formato yyyy-MM-dd
     * @param fechaHasta fecha final del rango en formato yyyy-MM-dd
     * @return ResponseEntity con la lista de contratos encontrados y código HTTP
     */
    @GetMapping("/buscar-por-fechas")
    @Operation(summary = "Buscar contratos por rango de fechas",
            description = "Devuelve los contratos cuya fecha esté dentro del rango indicado. Solo ADMIN y RRHH")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de contratos"),
            @ApiResponse(responseCode = "400", description = "Parámetros 'fechaDesde' o 'fechaHasta' inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<List<Contrato>> listarPorRangoFechas(
            @RequestParam String fechaDesde,
            @RequestParam String fechaHasta) {

        LocalDate desde = LocalDate.parse(fechaDesde);
        LocalDate hasta = LocalDate.parse(fechaHasta);

        List<Contrato> contratos = contratoService.listarPorRangoFechas(desde, hasta);

        return ResponseEntity.ok(contratos);
    }

    /**
     * devuelve todos los contratos cuyo empleado pertenezca
     * al departamento indicado. Solo para ADMIN o RRHH.
     *
     * @param departamentoId id del departamento
     * @return ResponseEntity con la lista de contratos encontrados y código HTTP
     */
    @GetMapping("/buscar-por-departamento")
    @Operation(summary = "Buscar contratos por departamento",
            description = "Devuelve los contratos de un departamento específico. Solo ADMIN y RRHH")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de contratos"),
            @ApiResponse(responseCode = "400", description = "Parámetro 'departamentoId' inválido"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<List<Contrato>> listarPorDepartamento(@RequestParam Integer departamentoId) {
        List<Contrato> contratos = contratoService.listarPorDepartamento(departamentoId);

        return ResponseEntity.ok(contratos);
    }

    /**
     * Descarga el archivo PDF de un contrato por su ID
     *
     * Un empleado solo puede descargar sus propios contratos
     * ADMIN y RRHH pueden descargar cualquier contrato
     *
     * @param id identificador del contrato
     * @param auth objeto de autenticación del usuario logueado
     * @return el archivo como recurso descargable o 404/403 según el caso
     * @throws IOException si hay un error leyendo el archivo en el servidor
     */
    @Operation(summary = "Descargar archivo de contrato", description = "Devuelve el archivo PDF de un contrato")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Archivo descargado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos para acceder a este contrato"),
            @ApiResponse(responseCode = "404", description = "Contrato o archivo no encontrado")
    })
    @GetMapping("/{id}/descargar")
    public ResponseEntity<Resource> descargarContrato(
            @Parameter(description = "ID del contrato") @PathVariable int id,
            Authentication auth) throws IOException {

        // Obtener el contrato por ID
        Contrato contrato = contratoService.buscarPorId(id);
        if (contrato == null) {
            return ResponseEntity.notFound().build();
        }

        // Obtener el usuario logueado
        String email = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario == null || usuario.getEmpleado() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Obtener el rol del usuario (tomamos el primer privilegio)
        String rol = usuario.getPrivilegiosUsuarios().stream()
                .map(privilegioUsuario -> privilegioUsuario.getPrivilegio().getNombre())
                .findFirst()
                .orElse("");

        // Verificar permisos
        if (rol.equals("empleado") && contrato.getEmpleado().getId() != usuario.getEmpleado().getId()) {
            // Un empleado solo puede descargar sus propios contratos
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Construir la ruta del archivo
        Path rutaArchivo = Paths.get(directorioArchivos).resolve(contrato.getArchivo()).normalize();
        Resource recurso = new UrlResource(rutaArchivo.toUri());

        // Verificar que el archivo existe
        if (!recurso.exists()) {
            return ResponseEntity.notFound().build();
        }

        // Determinar el tipo de contenido (MIME)
        String tipoContenido = Files.probeContentType(rutaArchivo);
        if (tipoContenido == null) {
            tipoContenido = "application/octet-stream";
        }

        // Devolver el recurso con los headers adecuados para descarga
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(tipoContenido))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + contrato.getArchivo() + "\"")
                .body(recurso);
    }

    /**
     * Sube un archivo PDF de contrato asociado a un empleado
     * Solo accesible para usuarios con rol admin o rrhh
     *
     * @param empleadoId ID del empleado al que pertenece el contrato
     * @param tipo tipo de contrato
     * @param fecha fecha del contrato
     * @param archivo archivo PDF del contrato
     * @return 201 Created si se creó correctamente
     * @throws IOException si hay error al guardar el archivo
     */
    @Operation(summary = "Subir contrato", description = "Sube un archivo PDF de contrato para un empleado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Contrato creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Archivo no válido o datos incorrectos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No tiene privilegios suficientes"),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado")
    })
    @PostMapping("/subir")
    @PreAuthorize("hasRole('admin') or hasRole('rrhh')")
    public ResponseEntity<Void> subirContrato(
            @RequestParam int empleadoId,
            @RequestParam Contrato.Tipo tipo,
            @RequestParam(required = false) Integer salario,
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

        // Generar nombre único para el archivo
        String nombreArchivo = UUID.randomUUID() + ".pdf";

        // Guardar el archivo en el servidor
        Path rutaArchivo = directorio.resolve(nombreArchivo);
        Files.copy(archivo.getInputStream(), rutaArchivo);

        // Crear registro en la BD
        ContratoDto dto = new ContratoDto();
        dto.setEmpleadoId(empleadoId);
        dto.setTipo(tipo);
        dto.setSalario(salario);
        dto.setFecha(LocalDate.parse(fecha));
        dto.setArchivo(nombreArchivo);

        contratoService.crearContrato(dto);

        // Crear notificacion para el empleado
        notificacionService.crear(empleado, Notificacion.Tipo.contrato_nuevo,
                "Tienes un nuevo contrato disponible (" + tipo + ")");

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Elimina un contrato por su ID.
     * Solo accesible para usuarios con rol admin o rrhh.
     *
     * @param id identificador del contrato a eliminar
     * @return 200 si se eliminó, 403 si no tiene privilegios, 404 si no existe
     */
    @Operation(summary = "Eliminar contrato", description = "Elimina un contrato por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contrato eliminado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No tiene privilegios suficientes"),
            @ApiResponse(responseCode = "404", description = "Contrato no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarContrato(@Parameter(description = "ID del contrato") @PathVariable int id) {

        // Obtener email del usuario logueado
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // Buscar el usuario en la BD
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Intentar eliminar el contrato
        if (contratoService.eliminarContrato(id)) {
            return ResponseEntity.ok().build();
        }

        // Contrato no encontrado
        return ResponseEntity.notFound().build();
    }
}











































