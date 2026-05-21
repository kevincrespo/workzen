package apirest.workzen.modelo.dto;

import apirest.workzen.modelo.entities.Documento;

/**
 * DTO para devolver los datos de un documento.
 *
 * @param id      identificador del documento
 * @param nombre  nombre descriptivo del documento
 * @param tipo    tipo del documento (laboral, manuales, bienestar)
 * @param archivo nombre del archivo en disco
 */
public record DocumentoDTO(
		Integer id,
		String nombre,
		String tipo,
		String archivo
) {

	/**
	 * Convierte una entidad Documento a DTO.
	 *
	 * @param doc entidad Documento
	 * @return el DTO con los datos del documento
	 */
	public static DocumentoDTO convertirADto(Documento doc) {
		return new DocumentoDTO(
				doc.getId(),
				doc.getNombre(),
				doc.getTipo().name(),
				doc.getArchivo());
	}

}
