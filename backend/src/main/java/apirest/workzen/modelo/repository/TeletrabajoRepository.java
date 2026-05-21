package apirest.workzen.modelo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import apirest.workzen.modelo.entities.Teletrabajo;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import apirest.workzen.modelo.entities.Teletrabajo;

/**
 * Repositorio para la entidad Teletrabajo.
 */
public interface TeletrabajoRepository extends JpaRepository<Teletrabajo, Integer>{
	
	// Devolver una lista de Teletrabajos de un empleado
	List<Teletrabajo> findByEmpleadoId(int empleadoId);

	// Devolver una lista de Teletrabajos filtradas por estado
	List<Teletrabajo> findByEstado(Teletrabajo.Estado estado);

	/**
	 * Busca teletrabajos por estado cuyas fechas solapen con un rango dado.
	 * La logica de solapamiento: fechaFin >= desde AND fechaInicio <= hasta.
	 *
	 * @param estado estado del teletrabajo
	 * @param desde  fecha inicio del rango (lunes)
	 * @param hasta  fecha fin del rango (domingo)
	 * @return lista de teletrabajos que solapan con el rango
	 */
	List<Teletrabajo> findByEstadoAndFechaFinGreaterThanEqualAndFechaInicioLessThanEqual(
			Teletrabajo.Estado estado, LocalDate desde, LocalDate hasta);

}
