package apirest.workzen.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import apirest.workzen.modelo.entities.Justificante;
import apirest.workzen.modelo.repository.JustificanteRepository;

/**
 * Implementacion del servicio de justificantes usando JPA.
 */
@Service
public class JustificanteServiceImplJpaMy8 implements JustificanteService {

    @Autowired
    private JustificanteRepository justificanteRepository;

    @Override
    public List<Justificante> obtenerPorEmpleado(int empleadoId) {
        return justificanteRepository.findByAusenciaEmpleadoIdOrderByIdDesc(empleadoId);
    }

    @Override
    public List<Justificante> obtenerTodos() {
        return justificanteRepository.findAll();
    }

    @Override
    public Justificante guardar(Justificante justificante) {
        return justificanteRepository.save(justificante);
    }

    @Override
    public boolean existePorAusencia(int ausenciaId) {
        return justificanteRepository.existsByAusenciaId(ausenciaId);
    }

    @Override
    public Justificante buscarPorId(int id) {
        return justificanteRepository.findById(id).orElse(null);
    }

    @Override
    public boolean eliminar(int id) {
        if (justificanteRepository.existsById(id)) {
            justificanteRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
