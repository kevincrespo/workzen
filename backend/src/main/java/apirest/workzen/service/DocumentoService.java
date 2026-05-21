package apirest.workzen.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import apirest.workzen.modelo.dto.DocumentoDTO;
import apirest.workzen.modelo.entities.Documento;
import apirest.workzen.modelo.repository.DocumentoRepository;
import jakarta.annotation.PostConstruct;

/**
 * Servicio para gestionar documentos.
 * Permite listar, subir, descargar y eliminar documentos.
 */
@Service
public class DocumentoService {

	@Autowired
	private DocumentoRepository documentoRepo;

	/** Directorio donde se guardan los archivos subidos (ruta absoluta). */
	private Path directorioArchivos;

	@Value("${documentos.upload.dir:uploads/documentos}")
	private String uploadDir;

	/**
	 * Crea el directorio de archivos si no existe.
	 *
	 * @throws IOException si no se puede crear el directorio
	 */
	@PostConstruct
	public void init() throws IOException {
		directorioArchivos = Paths.get(uploadDir).toAbsolutePath();
		Files.createDirectories(directorioArchivos);
	}

	/**
	 * Obtiene la lista de todos los documentos.
	 *
	 * @return lista de documentos como DTOs
	 */
	public List<DocumentoDTO> getDocumentos() {
		return documentoRepo.findAll().stream()
				.map(DocumentoDTO::convertirADto)
				.toList();
	}

	/**
	 * Sube un documento nuevo. Guarda el archivo en disco y crea el registro en BD.
	 *
	 * @param nombre  nombre descriptivo del documento
	 * @param tipo    tipo del documento (laboral, manuales, bienestar)
	 * @param archivo archivo a subir
	 * @return mensaje de confirmacion
	 * @throws IOException si falla la escritura del archivo
	 */
	public String subirDocumento(String nombre, String tipo, MultipartFile archivo) throws IOException {
		String nombreArchivo = System.currentTimeMillis() + "_" + archivo.getOriginalFilename();
		Path destino = directorioArchivos.resolve(nombreArchivo);
		archivo.transferTo(destino.toFile());

		Documento doc = new Documento();
		doc.setNombre(nombre);
		doc.setTipo(Documento.Tipo.valueOf(tipo));
		doc.setArchivo(nombreArchivo);
		documentoRepo.save(doc);

		return "Documento subido correctamente";
	}

	/**
	 * Descarga el archivo de un documento por su id.
	 *
	 * @param id id del documento
	 * @return recurso del archivo para descargar
	 * @throws IOException si el archivo no se encuentra
	 */
	public Resource descargarDocumento(Integer id) throws IOException {
		Documento doc = documentoRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Documento no encontrado"));
		Path ruta = directorioArchivos.resolve(doc.getArchivo());
		return new UrlResource(ruta.toUri());
	}

	/**
	 * Elimina un documento y su archivo del disco.
	 *
	 * @param id id del documento a eliminar
	 * @throws IOException si falla la eliminacion del archivo
	 */
	public void eliminarDocumento(Integer id) throws IOException {
		Documento doc = documentoRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Documento no encontrado"));
		Path ruta = directorioArchivos.resolve(doc.getArchivo());
		Files.deleteIfExists(ruta);
		documentoRepo.deleteById(id);
	}

}
