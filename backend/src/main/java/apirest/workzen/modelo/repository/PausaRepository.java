package apirest.workzen.modelo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import apirest.workzen.modelo.entities.Pausa;

/**
 * Repositorio JPA para la entidad Pausa.
 */
public interface PausaRepository extends JpaRepository<Pausa, Integer> {

    /**
     * Busca una pausa abierta (sin hora de fin) de un fichaje.
     *
     * @param fichajeId id del fichaje
     * @return la pausa abierta o vacio
     */
    Optional<Pausa> findByFichajeIdAndHoraFinIsNull(int fichajeId);
}
