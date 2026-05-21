package apirest.workzen.modelo.repository;

import java.util.Optional;

import apirest.workzen.modelo.entities.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad Empleado.
 */
@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {

    /**
     * Busca un empleado por su NIF.
     *
     * @param nif el NIF del empleado
     * @return el empleado encontrado o vacio
     */
    Optional<Empleado> findByNif(String nif);

    /**
     * Cuenta el numero de empleados asignados a un departamento.
     *
     * @param departamentoId el ID del departamento
     * @return numero de empleados
     */
    long countByDepartamentoId(int departamentoId);
}
