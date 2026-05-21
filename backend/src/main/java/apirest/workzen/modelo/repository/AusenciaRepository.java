package apirest.workzen.modelo.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import apirest.workzen.modelo.entities.Ausencia;

public interface AusenciaRepository extends JpaRepository<Ausencia, Integer>{

	// Devolver una lista de las ausencias de un empleado
	List<Ausencia> findByEmpleadoId(int empleadoId);

	// Devolver una lista de ausencias filtradas por estado
	List<Ausencia> findByEstado(Ausencia.Estado estado);

	/**
	 * Busca ausencias por estado cuyas fechas solapen con un rango dado.
	 * La logica de solapamiento: fechaFin >= desde AND fechaInicio <= hasta.
	 *
	 * @param estado estado de la ausencia
	 * @param desde  inicio del rango
	 * @param hasta  fin del rango
	 * @return lista de ausencias que solapan con el rango
	 */
	List<Ausencia> findByEstadoAndFechaFinGreaterThanEqualAndFechaInicioLessThanEqual(
			Ausencia.Estado estado, LocalDateTime desde, LocalDateTime hasta);

}
