package apirest.workzen.modelo.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import apirest.workzen.modelo.entities.Contrato;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContratoRepository extends JpaRepository<Contrato, Integer> {

    List<Contrato> findByEmpleadoId(Integer empleadoId);

    /** Buscar contratos por nombre de empleado (nombre o apellido) */
    @Query("SELECT c FROM Contrato c WHERE " +
            "LOWER(c.empleado.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) " +
            "OR LOWER(c.empleado.apellido1) LIKE LOWER(CONCAT('%', :nombre, '%')) " +
            "OR LOWER(c.empleado.apellido2) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Contrato> findByEmpleadoNombre(@Param("nombre") String nombre);

    /** Filtrar contratos por tipo */
    List<Contrato> findByTipo(Contrato.Tipo tipo);

    /** Filtrar contratos por rango de fechas */
    List<Contrato> findByFechaBetween(LocalDate fechaDesde, LocalDate fechaHasta);


    List<Contrato> findByEmpleadoDepartamentoId(Integer departamentoId);

    /**
     * Busca el contrato mas reciente de un empleado.
     *
     * @param empleadoId id del empleado
     * @return contrato mas reciente o null si no tiene contratos
     */
    Contrato findTopByEmpleadoIdOrderByFechaDesc(int empleadoId);

}