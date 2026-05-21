package apirest.workzen.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import apirest.workzen.modelo.entities.Departamento;
import apirest.workzen.modelo.repository.DepartamentoRepository;

/**
 * Implementacion del servicio de departamentos usando JPA.
 */
@Service
public class DepartamentoServiceImplJpaMy8 implements DepartamentoService {

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Override
    public Departamento findOne(Integer atributoPK) {
        return departamentoRepository.findById(atributoPK).orElse(null);
    }

    @Override
    public List<Departamento> findAll() {
        return departamentoRepository.findAll();
    }

    @Override
    public Departamento insert(Departamento objeto) {
        return departamentoRepository.save(objeto);
    }

    @Override
    public Departamento update(Integer atributoPK, Departamento objeto) {
        if (departamentoRepository.existsById(atributoPK)) {
            objeto.setId(atributoPK);
            return departamentoRepository.save(objeto);
        }
        return null;
    }

    @Override
    public boolean delete(Integer atributoPK) {
        if (departamentoRepository.existsById(atributoPK)) {
            departamentoRepository.deleteById(atributoPK);
            return true;
        }
        return false;
    }
}
