package apirest.workzen.modelo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import apirest.workzen.modelo.entities.Festivo;

/**
 * Repositorio JPA para la entidad Festivo.
 */
public interface FestivoRepository extends JpaRepository<Festivo, Integer> {
}
