package apirest.workzen.modelo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import apirest.workzen.modelo.entities.Justificante;

/**
 * Repositorio JPA para la entidad Justificante.
 */
public interface JustificanteRepository extends JpaRepository<Justificante, Integer> {

    /**
     * Busca los justificantes de las ausencias de un empleado.
     */
    List<Justificante> findByAusenciaEmpleadoIdOrderByIdDesc(int empleadoId);

    /**
     * Busca el justificante de una ausencia concreta.
     */
    Justificante findByAusenciaId(int ausenciaId);

    /**
     * Comprueba si una ausencia ya tiene justificante.
     */
    boolean existsByAusenciaId(int ausenciaId);
}
