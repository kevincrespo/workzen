package apirest.workzen.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import apirest.workzen.modelo.dto.CrearEmpleadoDto;
import apirest.workzen.modelo.dto.DiasDisponiblesDto;
import apirest.workzen.modelo.entities.Ausencia;
import apirest.workzen.modelo.entities.Departamento;
import apirest.workzen.modelo.entities.Empleado;
import apirest.workzen.modelo.entities.Privilegio;
import apirest.workzen.modelo.entities.PrivilegioUsuario;
import apirest.workzen.modelo.entities.Usuario;
import apirest.workzen.modelo.repository.AusenciaRepository;
import apirest.workzen.modelo.repository.DepartamentoRepository;
import apirest.workzen.modelo.repository.EmpleadoRepository;
import apirest.workzen.modelo.repository.PrivilegioRepository;
import apirest.workzen.modelo.repository.PrivilegioUsuarioRepository;
import apirest.workzen.modelo.repository.UsuarioRepository;

/**
 * Implementacion del servicio de empleados usando JPA.
 */
@Service
public class EmpleadoServiceImplJpaMy8 implements EmpleadoService {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private AusenciaRepository ausenciaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PrivilegioRepository privilegioRepository;

    @Autowired
    private PrivilegioUsuarioRepository privilegioUsuarioRepository;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Empleado findOne(Integer atributoPK) {
        return empleadoRepository.findById(atributoPK).orElse(null);
    }

    @Override
    public List<Empleado> findAll() {
        return empleadoRepository.findAll();
    }

    @Override
    public Empleado insert(Empleado objeto) {
        return empleadoRepository.save(objeto);
    }

    @Override
    public Empleado update(Integer atributoPK, Empleado objeto) {
        if (empleadoRepository.existsById(atributoPK)) {
            objeto.setId(atributoPK);
            return empleadoRepository.save(objeto);
        }
        return null;
    }

    @Override
    public boolean delete(Integer atributoPK) {
        if (empleadoRepository.existsById(atributoPK)) {
            empleadoRepository.deleteById(atributoPK);
            return true;
        }
        return false;
    }

    /**
     * Calcula los dias de vacaciones disponibles de un empleado.
     * Obtiene las ausencias del empleado, filtra las aprobadas de tipo vacaciones
     * del ano actual y suma los dias naturales de cada una.
     *
     * @param empleado empleado del que calcular los dias
     * @return DTO con dias totales, consumidos y disponibles
     */
    @Override
    public DiasDisponiblesDto calcularDiasDisponibles(Empleado empleado) {
        int anioActual = LocalDateTime.now().getYear();
        int diasTotales = empleado.getDiasVacaciones();

        // Obtener todas las ausencias del empleado
        List<Ausencia> ausencias = ausenciaRepository.findByEmpleadoId(empleado.getId());

        // Recorrer y sumar los dias de vacaciones aprobadas o pendientes del ano actual
        int diasConsumidos = 0;
        for (Ausencia ausencia : ausencias) {
            if ((ausencia.getEstado() == Ausencia.Estado.aprobado
                    || ausencia.getEstado() == Ausencia.Estado.pendiente)
                    && ausencia.getTipo() == Ausencia.Tipo.vacaciones
                    && ausencia.getFechaInicio().getYear() == anioActual) {

                LocalDate inicio = ausencia.getFechaInicio().toLocalDate();
                LocalDate fin = ausencia.getFechaFin().toLocalDate();
                // Dias naturales: diferencia de dias + 1 (incluye inicio y fin)
                int dias = fin.getDayOfYear() - inicio.getDayOfYear() + 1;
                diasConsumidos += dias;
            }
        }

        int diasDisponibles = diasTotales - diasConsumidos;

        return DiasDisponiblesDto.builder()
                .diasTotales(diasTotales)
                .diasConsumidos(diasConsumidos)
                .diasDisponibles(diasDisponibles)
                .build();
    }

    /**
     * Crea un empleado junto con su usuario y privilegios en una transaccion.
     * Valida que el email y el NIF no existan y que el departamento sea valido.
     *
     * @param dto datos del empleado, usuario y privilegios a crear
     * @return el empleado creado
     * @throws IllegalArgumentException si el email o NIF ya existen, o el departamento no existe
     */
    @Override
    @Transactional
    public Empleado crearEmpleadoConUsuario(CrearEmpleadoDto dto) {
        // Validar que el email no exista
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El email ya esta en uso");
        }

        // Validar que el NIF no exista
        if (empleadoRepository.findByNif(dto.getNif()).isPresent()) {
            throw new IllegalArgumentException("El NIF ya esta en uso");
        }

        // Validar que el departamento exista
        Departamento departamento = departamentoRepository.findById(dto.getDepartamentoId()).orElse(null);
        if (departamento == null) {
            throw new IllegalArgumentException("El departamento no existe");
        }

        // Crear usuario con password encriptada
        Usuario usuario = Usuario.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .build();
        usuario = usuarioRepository.save(usuario);

        // Asignar privilegios al usuario
        if (dto.getPrivilegios() != null) {
            for (String nombrePrivilegio : dto.getPrivilegios()) {
                Privilegio privilegio = privilegioRepository.findByNombre(nombrePrivilegio).orElse(null);
                if (privilegio != null) {
                    PrivilegioUsuario pu = PrivilegioUsuario.builder()
                            .usuario(usuario)
                            .privilegio(privilegio)
                            .build();
                    privilegioUsuarioRepository.save(pu);
                }
            }
        }

        // Crear empleado vinculado al usuario
        Empleado empleado = Empleado.builder()
                .usuario(usuario)
                .departamento(departamento)
                .nombre(dto.getNombre())
                .apellido1(dto.getApellido1())
                .apellido2(dto.getApellido2())
                .nif(dto.getNif())
                .numeroSS(dto.getNumeroSs())
                .direccion(dto.getDireccion())
                .provincia(dto.getProvincia())
                .localidad(dto.getLocalidad())
                .codigoPostal(dto.getCodigoPostal())
                .fechaNacimiento(dto.getFechaNacimiento())
                .iban(dto.getIban())
                .telefono(dto.getTelefono())
                .diasVacaciones(22)
                .build();

        return empleadoRepository.save(empleado);
    }
}
