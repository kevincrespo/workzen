package apirest.workzen.modelo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import apirest.workzen.modelo.entities.Dieta;

/**
 * Repositorio JPA para la entidad Dieta.
 */
public interface DietaRepository extends JpaRepository<Dieta, Integer> {

    /**
     * Devuelve las dietas de un empleado por su id.
     *
     * @param empleadoId el id del empleado
     * @return lista de dietas del empleado
     */
    List<Dieta> findByEmpleadoId(int empleadoId);

    /**
     * Devuelve las dietas filtradas por estado.
     *
     * @param estado el estado a filtrar
     * @return lista de dietas con ese estado
     */
    List<Dieta> findByEstado(Dieta.Estado estado);
}
