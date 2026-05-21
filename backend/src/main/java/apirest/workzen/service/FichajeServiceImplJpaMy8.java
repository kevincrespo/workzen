package apirest.workzen.service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import apirest.workzen.modelo.entities.Empleado;
import apirest.workzen.modelo.entities.Fichaje;
import apirest.workzen.modelo.entities.Pausa;
import apirest.workzen.modelo.entities.Usuario;
import apirest.workzen.modelo.repository.FichajeRepository;
import apirest.workzen.modelo.repository.PausaRepository;
import apirest.workzen.modelo.repository.UsuarioRepository;

/**
 * Implementacion del servicio de fichajes con JPA y MySQL 8.
 */
@Service
public class FichajeServiceImplJpaMy8 implements FichajeService {

    @Autowired
    private FichajeRepository fichajeRepository;

    @Autowired
    private PausaRepository pausaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ==================== CRUD GENERICO ====================

    @Override
    public Fichaje findOne(Integer atributoPK) {
        return fichajeRepository.findById(atributoPK).orElse(null);
    }

    @Override
    public List<Fichaje> findAll() {
        return fichajeRepository.findAll();
    }

    @Override
    public Fichaje insert(Fichaje objeto) {
        return fichajeRepository.save(objeto);
    }

    @Override
    public Fichaje update(Integer atributoPK, Fichaje objeto) {
        if (fichajeRepository.existsById(atributoPK)) {
            objeto.setId(atributoPK);
            return fichajeRepository.save(objeto);
        } else {
            return null;
        }
    }

    @Override
    public boolean delete(Integer atributoPK) {
        if (fichajeRepository.existsById(atributoPK)) {
            fichajeRepository.deleteById(atributoPK);
            return true;
        } else {
            return false;
        }
    }

    // ==================== METODOS AUXILIARES ====================

    /**
     * Obtiene el empleado del usuario autenticado.
     *
     * @param auth autenticacion del usuario
     * @return el empleado o null si no existe
     */
    private Empleado obtenerEmpleado(Authentication auth) {
        String email = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario == null || usuario.getEmpleado() == null) {
            return null;
        }

        return usuario.getEmpleado();
    }

    // ==================== LOGICA DE NEGOCIO ====================

    /**
     * {@inheritDoc}
     */
    @Override
    public Fichaje obtenerFichajeActivo(Authentication auth) {
        Empleado empleado = obtenerEmpleado(auth);
        if (empleado == null) {
            return null;
        }

        return fichajeRepository.findByEmpleadoIdAndEstado(empleado.getId(), Fichaje.Estado.activo)
                .orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Fichaje iniciarFichaje(Authentication auth) {
        Empleado empleado = obtenerEmpleado(auth);
        if (empleado == null) {
            return null;
        }

        // Validar que no exista otro fichaje activo
        Optional<Fichaje> fichajeActivo = fichajeRepository
                .findByEmpleadoIdAndEstado(empleado.getId(), Fichaje.Estado.activo);

        if (fichajeActivo.isPresent()) {
            return null;
        }

        Fichaje fichaje = Fichaje.builder()
                .empleado(empleado)
                .fechaEntrada(LocalDateTime.now())
                .estado(Fichaje.Estado.activo)
                .build();

        return fichajeRepository.save(fichaje);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Fichaje pausarFichaje(Authentication auth, int fichajeId) {
        Empleado empleado = obtenerEmpleado(auth);
        if (empleado == null) {
            return null;
        }

        Fichaje fichaje = fichajeRepository.findById(fichajeId).orElse(null);
        if (fichaje == null) {
            return null;
        }

        // Validar que pertenece al empleado
        if (fichaje.getEmpleado().getId() != empleado.getId()) {
            return null;
        }

        // Validar que esta activo
        if (fichaje.getEstado() != Fichaje.Estado.activo) {
            return null;
        }

        // Validar que no haya otra pausa abierta
        Optional<Pausa> pausaAbierta = pausaRepository.findByFichajeIdAndHoraFinIsNull(fichajeId);
        if (pausaAbierta.isPresent()) {
            return null;
        }

        Pausa pausa = Pausa.builder()
                .fichaje(fichaje)
                .horaInicio(LocalDateTime.now())
                .build();

        pausaRepository.save(pausa);

        // Recargar fichaje con pausas actualizadas
        return fichajeRepository.findById(fichajeId).orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Fichaje reanudarFichaje(Authentication auth, int fichajeId) {
        Empleado empleado = obtenerEmpleado(auth);
        if (empleado == null) {
            return null;
        }

        Fichaje fichaje = fichajeRepository.findById(fichajeId).orElse(null);
        if (fichaje == null) {
            return null;
        }

        // Validar que pertenece al empleado
        if (fichaje.getEmpleado().getId() != empleado.getId()) {
            return null;
        }

        // Buscar pausa abierta
        Optional<Pausa> pausaAbierta = pausaRepository.findByFichajeIdAndHoraFinIsNull(fichajeId);
        if (pausaAbierta.isEmpty()) {
            return null;
        }

        // Cerrar la pausa
        Pausa pausa = pausaAbierta.get();
        pausa.setHoraFin(LocalDateTime.now());
        pausaRepository.save(pausa);

        return fichajeRepository.findById(fichajeId).orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Fichaje finalizarFichaje(Authentication auth, int fichajeId) {
        Empleado empleado = obtenerEmpleado(auth);
        if (empleado == null) {
            return null;
        }

        Fichaje fichaje = fichajeRepository.findById(fichajeId).orElse(null);
        if (fichaje == null) {
            return null;
        }

        // Validar que pertenece al empleado
        if (fichaje.getEmpleado().getId() != empleado.getId()) {
            return null;
        }

        // Validar que esta activo
        if (fichaje.getEstado() != Fichaje.Estado.activo) {
            return null;
        }

        // Si hay pausa abierta, cerrarla automaticamente
        Optional<Pausa> pausaAbierta = pausaRepository.findByFichajeIdAndHoraFinIsNull(fichajeId);
        if (pausaAbierta.isPresent()) {
            Pausa pausa = pausaAbierta.get();
            pausa.setHoraFin(LocalDateTime.now());
            pausaRepository.save(pausa);
        }

        // Finalizar el fichaje
        fichaje.setFechaSalida(LocalDateTime.now());
        fichaje.setEstado(Fichaje.Estado.finalizado);

        return fichajeRepository.save(fichaje);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Fichaje> obtenerPorEmpleadoYMes(int empleadoId, int mes, int anio) {
        LocalDateTime desde = LocalDateTime.of(anio, mes, 1, 0, 0);
        LocalDateTime hasta = YearMonth.of(anio, mes).atEndOfMonth().atTime(23, 59, 59);
        return fichajeRepository.findByEmpleadoIdAndFechaEntradaBetweenOrderByFechaEntradaDesc(
                empleadoId, desde, hasta);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Fichaje> obtenerTodosPorMes(int mes, int anio) {
        LocalDateTime desde = LocalDateTime.of(anio, mes, 1, 0, 0);
        LocalDateTime hasta = YearMonth.of(anio, mes).atEndOfMonth().atTime(23, 59, 59);
        return fichajeRepository.findByFechaEntradaBetweenOrderByFechaEntradaDesc(desde, hasta);
    }
}
