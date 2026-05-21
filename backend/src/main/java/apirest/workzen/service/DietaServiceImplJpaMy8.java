package apirest.workzen.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import apirest.workzen.modelo.entities.Dieta;
import apirest.workzen.modelo.entities.Empleado;
import apirest.workzen.modelo.entities.Usuario;
import apirest.workzen.modelo.repository.DietaRepository;
import apirest.workzen.modelo.repository.UsuarioRepository;

/**
 * Implementacion del servicio de dietas con JPA y MySQL 8.
 */
@Service
public class DietaServiceImplJpaMy8 implements DietaService {

    @Autowired
    private DietaRepository dietaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ==================== CRUD GENERICO ====================

    @Override
    public Dieta findOne(Integer atributoPK) {
        return dietaRepository.findById(atributoPK).orElse(null);
    }

    @Override
    public List<Dieta> findAll() {
        return dietaRepository.findAll();
    }

    @Override
    public Dieta insert(Dieta objeto) {
        return dietaRepository.save(objeto);
    }

    @Override
    public Dieta update(Integer atributoPK, Dieta objeto) {
        if (dietaRepository.existsById(atributoPK)) {
            objeto.setId(atributoPK);
            return dietaRepository.save(objeto);
        } else {
            return null;
        }
    }

    @Override
    public boolean delete(Integer atributoPK) {
        if (dietaRepository.existsById(atributoPK)) {
            dietaRepository.deleteById(atributoPK);
            return true;
        } else {
            return false;
        }
    }

    // ==================== VALIDACIONES ====================

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validarFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            return false;
        }
        return !fechaFin.isBefore(fechaInicio);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validarSolapamiento(int empleadoId, LocalDate fechaInicio, LocalDate fechaFin) {
        List<Dieta> dietas = dietaRepository.findByEmpleadoId(empleadoId);

        for (Dieta dieta : dietas) {
            if (fechaInicio.isBefore(dieta.getFechaFin()) && fechaFin.isAfter(dieta.getFechaInicio())) {
                return false;
            }
        }

        return true;
    }

    // ==================== LOGICA DE NEGOCIO ====================

    /**
     * {@inheritDoc}
     */
    @Override
    public Dieta solicitarDieta(Authentication auth, Dieta dieta) {
        String email = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario == null || usuario.getEmpleado() == null) {
            return null;
        }

        Empleado empleado = usuario.getEmpleado();

        if (validarFechas(dieta.getFechaInicio(), dieta.getFechaFin())
                && validarSolapamiento(empleado.getId(), dieta.getFechaInicio(), dieta.getFechaFin())) {

            dieta.setEmpleado(empleado);
            dieta.setEstado(Dieta.Estado.pendiente);

            return dietaRepository.save(dieta);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Dieta> obtenerMisDietas(Authentication auth) {
        String email = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario == null || usuario.getEmpleado() == null) {
            return null;
        }

        return dietaRepository.findByEmpleadoId(usuario.getEmpleado().getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dieta aprobarDieta(int id) {
        Dieta dieta = dietaRepository.findById(id).orElse(null);
        if (dieta == null) {
            return null;
        }
        dieta.setEstado(Dieta.Estado.aprobado);
        return dietaRepository.save(dieta);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dieta rechazarDieta(int id) {
        Dieta dieta = dietaRepository.findById(id).orElse(null);
        if (dieta == null) {
            return null;
        }
        dieta.setEstado(Dieta.Estado.rechazado);
        return dietaRepository.save(dieta);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Dieta> obtenerPendientes() {
        return dietaRepository.findByEstado(Dieta.Estado.pendiente);
    }
}
