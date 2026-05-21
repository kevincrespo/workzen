package apirest.workzen.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import apirest.workzen.modelo.dto.SolicitudAusenciaDto;
import apirest.workzen.modelo.entities.Ausencia;
import apirest.workzen.modelo.entities.Ausencia.Tipo;
import apirest.workzen.modelo.entities.Empleado;
import apirest.workzen.modelo.entities.Usuario;
import apirest.workzen.modelo.repository.AusenciaRepository;
import apirest.workzen.modelo.repository.UsuarioRepository;
import io.jsonwebtoken.lang.Collections;

@Service
public class AusenciaServiceImplJpaMy8 implements AusenciaService{
	
	// Inyección de AusenciaRepository
	@Autowired
	private AusenciaRepository ausenciaRepository;
	
	//Inyección se UsuarioRepository
	@Autowired
	private UsuarioRepository usuarioRepository;
	

	// -------------------------- CRUD GENÉRICO ---------------------------- //
	@Override
	public Ausencia findOne(Integer atributoPK) {
		return ausenciaRepository.findById(atributoPK).orElse(null);
	}

	@Override
	public List<Ausencia> findAll() {
		return ausenciaRepository.findAll();
	}

	@Override
	public Ausencia insert(Ausencia objeto) {
		if(objeto == null) {
			return null;
		}
		
		if(ausenciaRepository.existsById(objeto.getId())) {
			return null;
		}else {
			return ausenciaRepository.save(objeto);
		}
	}

	@Override
	public Ausencia update(Integer atributoPK, Ausencia objeto) {
		if(ausenciaRepository.existsById(atributoPK)) {
			objeto.setId(atributoPK); // Esto evita que inserte un objeto con (id = null) o (id = 0)
			return ausenciaRepository.save(objeto);
		}else {
			return null;
		}
	}

	@Override
	public boolean delete(Integer atributoPK) {
		if(ausenciaRepository.existsById(atributoPK)) {
			ausenciaRepository.deleteById(atributoPK);
			return true;
		}else {
			return false;
		}
	}
	// --------------------------------------------------------------------- //

	// Valida que las fechas no estén vacías y que la fecha de inicio 
	// no sea posterior a la fecha de fin
	@Override
	public boolean validarFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
		if(fechaInicio == null || fechaFin == null) {
			return false;
		}else if(fechaFin.isBefore(fechaInicio)){
			return false;
		}
		return true;
	}

	// Verifica que las fechas entre distintas solicitudes de ausencias no se solapen
	@Override
	public boolean validarSolapamiento(int empleadoId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
		List<Ausencia> ausencias = ausenciaRepository.findByEmpleadoId(empleadoId);
		
		// Recorre la lista de todas las ausencias del empleado
		for (Ausencia ausencia : ausencias) {
			
			// Si la nueva solicitud se solapa con esta ausencia existente retorna false
	        // Condición de solapamiento: fechaInicio < fin de la ausencia && fechaFin > inicio de la ausencia
            if (fechaInicio.isBefore(ausencia.getFechaFin()) && fechaFin.isAfter(ausencia.getFechaInicio())) {  
            		return false;
            }
        }
        return true;
	}

	// Solicitar ausencias para el ususario logueado
	@Override
	public Ausencia solicitarAusencia(Authentication auth, SolicitudAusenciaDto solicitud) {
		
		String email = auth.getName();
	    Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

	    if (usuario == null || usuario.getEmpleado() == null) {
	        return null;
	    }

	    Empleado empleado = usuario.getEmpleado();

	    // Saca las fechas del DTO
	    LocalDateTime fechaInicio = solicitud.getFechaInicio();
	    LocalDateTime fechaFin    = solicitud.getFechaFin();

	    // Validar fechas y solapamientos
	    if (validarFechas(fechaInicio, fechaFin) == false) {
	        return null;
	    }
	    if (validarSolapamiento(empleado.getId(), fechaInicio, fechaFin) == false) {
	        return null;
	    }

	    // Crea la entidad Ausencia a partir del DTO
	    Ausencia ausencia = new Ausencia();
	    ausencia.setEmpleado(empleado);
	    ausencia.setFechaInicio(fechaInicio);
	    ausencia.setFechaFin(fechaFin);
	    ausencia.setTipo(solicitud.getTipo());
	    ausencia.setEstado(Ausencia.Estado.pendiente); // estado por defecto

	    return ausenciaRepository.save(ausencia);
	}
	
	// Mostrar todos los tipos de ausencia al usuario
	@Override
	public List<Tipo> obtenerTipos() {
		return Arrays.asList(Ausencia.Tipo.values());	
	}

	// Devolver una lista de las ausencias de un empleado
	//@Override
	//public List<Ausencia> obtenerAusenciasDeEmpleado(int empleadoId) {
		
	//	return ausenciaRepository.findByEmpleadoId(empleadoId);
	//}


	@Override
	public List<Ausencia> obtenerMisAusencias(Authentication auth) {
		String email = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario == null || usuario.getEmpleado() == null) {
        	return Collections.emptyList();
        }

        return ausenciaRepository.findByEmpleadoId(usuario.getEmpleado().getId());
	}

	// Aprobar una ausencia pendiente
	@Override
	public Ausencia aprobarAusencia(int id) {
		Ausencia ausencia = ausenciaRepository.findById(id).orElse(null);
		if (ausencia == null) {
			return null;
		}
		ausencia.setEstado(Ausencia.Estado.aprobado);
		return ausenciaRepository.save(ausencia);
	}

	// Rechazar una ausencia pendiente
	@Override
	public Ausencia rechazarAusencia(int id) {
		Ausencia ausencia = ausenciaRepository.findById(id).orElse(null);
		if (ausencia == null) {
			return null;
		}
		ausencia.setEstado(Ausencia.Estado.rechazado);
		return ausenciaRepository.save(ausencia);
	}

	// Obtener ausencias con estado pendiente
	@Override
	public List<Ausencia> obtenerAusenciasPendientes() {
		return ausenciaRepository.findByEstado(Ausencia.Estado.pendiente);
	}
}
