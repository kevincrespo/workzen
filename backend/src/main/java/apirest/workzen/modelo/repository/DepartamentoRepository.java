package apirest.workzen.modelo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import apirest.workzen.modelo.entities.Departamento;

/**
 * Repositorio JPA para la entidad Departamento.
 */
public interface DepartamentoRepository extends JpaRepository<Departamento, Integer> {
}
