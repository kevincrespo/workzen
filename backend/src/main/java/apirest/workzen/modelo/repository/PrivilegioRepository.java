package apirest.workzen.modelo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import apirest.workzen.modelo.entities.Privilegio;

/**
 * Repositorio JPA para la entidad Privilegio.
 */
public interface PrivilegioRepository extends JpaRepository<Privilegio, Integer> {

    /**
     * Busca un privilegio por su nombre.
     *
     * @param nombre nombre del privilegio
     * @return el privilegio encontrado o vacio
     */
    Optional<Privilegio> findByNombre(String nombre);
}
