package apirest.workzen.modelo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import apirest.workzen.modelo.entities.Nomina;

public interface NominaRepository extends JpaRepository<Nomina, Integer> {

    @Query("SELECT n FROM Nomina n WHERE n.empleado.id = :empleadoId AND YEAR(n.fecha) = :year")
    List<Nomina> findByEmpleadoIdAndYear(int empleadoId, int year);
    int countByEmpleadoId(int empleadoId);
}
