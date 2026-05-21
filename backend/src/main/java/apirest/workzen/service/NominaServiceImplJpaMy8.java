package apirest.workzen.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import apirest.workzen.modelo.entities.Nomina;
import apirest.workzen.modelo.repository.NominaRepository;

@Service
public class NominaServiceImplJpaMy8 implements NominaService {

    @Autowired
    private NominaRepository nominaRepository;

    @Override
    public Nomina findOne(Integer id) {
        return nominaRepository.findById(id).orElse(null);
    }

    @Override
    public List<Nomina> findAll() {
        return nominaRepository.findAll();
    }

    @Override
    public Nomina insert(Nomina nomina) {
        return nominaRepository.save(nomina);
    }

    @Override
    public Nomina update(Integer id, Nomina nomina) {
        Nomina existente = nominaRepository.findById(id).orElse(null);
        if (existente != null) {
            nomina.setId(id);
            return nominaRepository.save(nomina);
        }
        return null;
    }

    @Override
    public boolean delete(Integer id) {
        if (nominaRepository.existsById(id)) {
            nominaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public int findByEmpleadoId(int empleadoId) {
        return nominaRepository.countByEmpleadoId(empleadoId);
    }

    @Override
    public List<Nomina> findByEmpleadoIdAndYear(int empleadoId, int year) {
        return nominaRepository.findByEmpleadoIdAndYear(empleadoId, year);
    }
}
