package apirest.workzen.restcontroller;

import apirest.workzen.modelo.dto.CertificadoDto;
import apirest.workzen.modelo.entities.Certificado;
import apirest.workzen.modelo.entities.Empleado;
import apirest.workzen.modelo.entities.Usuario;
import apirest.workzen.modelo.repository.CertificadoRepository;
import apirest.workzen.modelo.repository.EmpleadoRepository;
import apirest.workzen.modelo.repository.UsuarioRepository;
import apirest.workzen.service.CertificadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

// TODO @CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/certificados")
public class CertificadoRestController {

    @Autowired
    CertificadoService certificadoService;

    @Autowired
    CertificadoRepository certificadoRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    EmpleadoRepository empleadoRepository;

    @Value("${nominas.upload-dir}")
    private String directorioArchivos;

    @GetMapping
    public ResponseEntity<List<CertificadoDto>> obtenerTodos(Authentication auth) {
        // Obtener el usuario logueado
        String email = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Obtener el rol
        String rol = usuario.getPrivilegiosUsuarios().stream()
                .map(pu -> pu.getPrivilegio().getNombre())
                .findFirst()
                .orElse("");

        // Si es empleado, devolver solo los suyos
        if (rol.equals("empleado")) {
            if (usuario.getEmpleado() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            return ResponseEntity.ok(certificadoService.obtenerPorEmpleadoId(usuario.getEmpleado().getId()));
        }

        // Si es admin o rrhh, devolver todos
        return ResponseEntity.ok(certificadoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CertificadoDto> obtenerPorId(@PathVariable int id) {
        return ResponseEntity.ok(certificadoService.obtenerPorId(id));
    }

    @GetMapping("/empleado/{empleadoId}")
    public ResponseEntity<List<CertificadoDto>> obtenerPorEmpleado(@PathVariable int empleadoId) {
        return ResponseEntity.ok(certificadoService.obtenerPorEmpleadoId(empleadoId));
    }

    @PostMapping
    public ResponseEntity<CertificadoDto> crear(@RequestBody CertificadoDto certificadoDto) {
        return ResponseEntity.ok(certificadoService.crear(certificadoDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CertificadoDto> actualizar(
            @PathVariable int id,
            @RequestBody CertificadoDto certificadoDto) {
        CertificadoDto actualizado = certificadoService.actualizar(id, certificadoDto);
        return ResponseEntity.ok(actualizado);
    }

    @PostMapping("/subir")
    public ResponseEntity<Void> subirCertificado(
            @RequestParam int empleadoId,
            @RequestParam String nombre,
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
        CertificadoDto dto = new CertificadoDto();
        dto.setEmpleadoId(empleadoId);
        dto.setNombre(nombre);
        dto.setFecha(LocalDate.parse(fecha));
        dto.setArchivo(nombreArchivo);

        certificadoService.crear(dto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        certificadoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/descargar")
    public ResponseEntity<Resource> descargarCertificado(
            @PathVariable int id,
            Authentication auth) throws IOException {

        // Obtener el certificado por ID
        Certificado certificado = certificadoRepository.findById(id).orElse(null);
        if (certificado == null) {
            return ResponseEntity.notFound().build();
        }

        // Obtener el usuario logueado
        String email = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario == null || usuario.getEmpleado() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Obtener el rol del usuario
        String rol = usuario.getPrivilegiosUsuarios().stream()
                .map(privilegioUsuario -> privilegioUsuario.getPrivilegio().getNombre())
                .findFirst()
                .orElse("");

        // Verificar permisos
        if (rol.equals("empleado") && certificado.getEmpleado().getId() != usuario.getEmpleado().getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Construir la ruta del archivo
        Path rutaArchivo = Paths.get(directorioArchivos).resolve(certificado.getArchivo()).normalize();
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
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + certificado.getArchivo() + "\"")
                .body(recurso);
    }
}












































