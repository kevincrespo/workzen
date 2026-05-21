package apirest.workzen.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import apirest.workzen.modelo.entities.Festivo;
import apirest.workzen.modelo.repository.FestivoRepository;

/**
 * Implementacion del servicio de festivos usando JPA.
 */
@Service
public class FestivoServiceImplJpaMy8 implements FestivoService {

    @Autowired
    private FestivoRepository festivoRepository;

    @Override
    public Festivo findOne(Integer atributoPK) {
        return festivoRepository.findById(atributoPK).orElse(null);
    }

    @Override
    public List<Festivo> findAll() {
        return festivoRepository.findAll();
    }

    @Override
    public Festivo insert(Festivo objeto) {
        return festivoRepository.save(objeto);
    }

    @Override
    public Festivo update(Integer atributoPK, Festivo objeto) {
        if (festivoRepository.existsById(atributoPK)) {
            objeto.setId(atributoPK);
            return festivoRepository.save(objeto);
        }
        return null;
    }

    @Override
    public boolean delete(Integer atributoPK) {
        if (festivoRepository.existsById(atributoPK)) {
            festivoRepository.deleteById(atributoPK);
            return true;
        }
        return false;
    }
}
