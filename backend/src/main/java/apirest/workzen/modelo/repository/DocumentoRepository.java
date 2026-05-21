package apirest.workzen.modelo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import apirest.workzen.modelo.entities.Documento;

/**
 * Repositorio para la entidad Documento.
 */
public interface DocumentoRepository extends JpaRepository<Documento, Integer> {

}
