package apirest.workzen.modelo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import apirest.workzen.modelo.entities.PrivilegioUsuario;

/**
 * Repositorio JPA para la entidad PrivilegioUsuario.
 */
public interface PrivilegioUsuarioRepository extends JpaRepository<PrivilegioUsuario, Integer> {
}
