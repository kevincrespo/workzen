package apirest.workzen.modelo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import apirest.workzen.modelo.entities.Usuario;

/**
 * Repositorio para acceder a los usuarios en la base de datos.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    /**
     * Busca un usuario solo por email (para validar el token JWT).
     */
    Optional<Usuario> findByEmail(String email);
}
