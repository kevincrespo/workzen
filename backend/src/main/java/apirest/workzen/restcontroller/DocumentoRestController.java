package apirest.workzen.restcontroller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import apirest.workzen.modelo.dto.DocumentoDTO;
import apirest.workzen.service.DocumentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para la gestion de documentos.
 * Permite listar, subir, descargar y eliminar documentos.
 */
@Tag(name = "Documentos", description = "Endpoints para la gestion de documentos")
@RestController
@RequestMapping("/documentos")
public class DocumentoRestController {

	@Autowired
	private DocumentoService documentoService;

	/**
	 * Obtiene la lista de todos los documentos.
	 *
	 * @return lista de documentos
	 */
	@Operation(summary = "Listar documentos", description = "Devuelve todos los documentos disponibles")
	@ApiResponse(responseCode = "200", description = "Lista de documentos obtenida correctamente")
	@GetMapping
	public ResponseEntity<List<DocumentoDTO>> getDocumentos() {
		return ResponseEntity.ok(documentoService.getDocumentos());
	}

	/**
	 * Sube un documento nuevo (solo admin/rrhh).
	 *
	 * @param nombre  nombre descriptivo del documento
	 * @param tipo    tipo del documento (laboral, manuales, bienestar)
	 * @param archivo archivo a subir
	 * @return mensaje de confirmacion
	 * @throws IOException si falla la subida
	 */
	@Operation(summary = "Subir documento", description = "Sube un documento nuevo (solo admin/rrhh)")
	@ApiResponse(responseCode = "200", description = "Documento subido correctamente")
	@PostMapping("/subir")
	public ResponseEntity<String> subirDocumento(
			@RequestParam String nombre,
			@RequestParam String tipo,
			@RequestParam MultipartFile archivo) throws IOException {
		return ResponseEntity.ok(documentoService.subirDocumento(nombre, tipo, archivo));
	}

	/**
	 * Descarga el archivo de un documento.
	 *
	 * @param id id del documento
	 * @return el archivo para descargar
	 * @throws IOException si el archivo no se encuentra
	 */
	@Operation(summary = "Descargar documento", description = "Descarga el archivo de un documento por su id")
	@ApiResponse(responseCode = "200", description = "Archivo descargado correctamente")
	@ApiResponse(responseCode = "404", description = "Documento no encontrado")
	@GetMapping("/{id}/descargar")
	public ResponseEntity<Resource> descargarDocumento(@PathVariable Integer id) throws IOException {
		Resource recurso = documentoService.descargarDocumento(id);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
				.body(recurso);
	}

	/**
	 * Elimina un documento y su archivo (solo admin/rrhh).
	 *
	 * @param id id del documento a eliminar
	 * @return 204 si se elimino correctamente
	 * @throws IOException si falla la eliminacion
	 */
	@Operation(summary = "Eliminar documento", description = "Elimina un documento y su archivo del disco (solo admin/rrhh)")
	@ApiResponse(responseCode = "204", description = "Documento eliminado correctamente")
	@ApiResponse(responseCode = "404", description = "Documento no encontrado")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminarDocumento(@PathVariable Integer id) throws IOException {
		documentoService.eliminarDocumento(id);
		return ResponseEntity.noContent().build();
	}

}
