package apirest.workzen.modelo.repository;

import apirest.workzen.modelo.entities.Certificado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificadoRepository extends JpaRepository<Certificado, Integer> {

    // Obtiene lista de certificados por el id de empleado
    List<Certificado> findByEmpleadoId(int empleadoId);
}
