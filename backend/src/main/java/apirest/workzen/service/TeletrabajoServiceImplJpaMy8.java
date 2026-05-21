package apirest.workzen.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import apirest.workzen.modelo.dto.SolicitudTeletrabajoDto;
import apirest.workzen.modelo.entities.Empleado;
import apirest.workzen.modelo.entities.Teletrabajo;
import apirest.workzen.modelo.entities.Usuario;
import apirest.workzen.modelo.repository.TeletrabajoRepository;
import apirest.workzen.modelo.repository.UsuarioRepository;
import io.jsonwebtoken.lang.Collections;

@Service
public class TeletrabajoServiceImplJpaMy8 implements TeletrabajoService{
	
	// Inyección de TeletrabajoRepository
	@Autowired
	private TeletrabajoRepository teletrabajoRepository;
	
	// Inyección de UsuarioRepository
	@Autowired
	private UsuarioRepository usuarioRepository;

	// CRUD GENÉRICO
	@Override
	public Teletrabajo findOne(Integer atributoPK) {
		return teletrabajoRepository.findById(atributoPK).orElse(null);
	}

	@Override
	public List<Teletrabajo> findAll() {
		return teletrabajoRepository.findAll();
	}

	@Override
	public Teletrabajo insert(Teletrabajo objeto) {
		if(objeto == null) {
			return null;
		}
		
		if(teletrabajoRepository.existsById(objeto.getId())) {
			return null;
		}else {
			return teletrabajoRepository.save(objeto);
		}
	}

	@Override
	public Teletrabajo update(Integer atributoPK, Teletrabajo objeto) {
		if(teletrabajoRepository.existsById(atributoPK)) {
			objeto.setId(atributoPK); // Esto evita que inserte un objeto con (id = null) o (id = 0)
			return teletrabajoRepository.save(objeto);
		}else {
			return null;
		}
	}

	@Override
	public boolean delete(Integer atributoPK) {
		if(teletrabajoRepository.existsById(atributoPK)) {
			teletrabajoRepository.deleteById(atributoPK);
			return true;
		}else {
			return false;
		}
	}
	// --------------------------------------------------------------------- //

	@Override
	public boolean validarFechas(LocalDate fechaInicio, LocalDate fechaFin) {
		if(fechaInicio == null || fechaFin == null) {
			return false;
		}else if(fechaFin.isBefore(fechaInicio)){
			return false;
		}
		return true;
	}

	@Override
	public boolean validarSolapamiento(int empleadoId, LocalDate fechaInicio, LocalDate fechaFin) {
	    List<Teletrabajo> teletrabajos = teletrabajoRepository.findByEmpleadoId(empleadoId);

	    for (Teletrabajo teletrabajo : teletrabajos) {
	    	
	    	if ((fechaInicio.isBefore(teletrabajo.getFechaFin()) || fechaInicio.isEqual(teletrabajo.getFechaFin())) &&
	    		    (fechaFin.isAfter(teletrabajo.getFechaInicio()) || fechaFin.isEqual(teletrabajo.getFechaInicio()))) {
	    		    return false;
	    	}
	    }
	    return true;
	}

	@Override
	public Teletrabajo solicitarTeletrabajo(Authentication auth, SolicitudTeletrabajoDto solicitud) {
		String email = auth.getName();
	    Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

	    if (usuario == null || usuario.getEmpleado() == null) {
	        return null;
	    }

	    Empleado empleado = usuario.getEmpleado();

	    // Saca las fechas del DTO
	    LocalDate fechaInicio = solicitud.getFechaInicio();
	    LocalDate fechaFin    = solicitud.getFechaFin();

	    // Validar fechas y solapamientos
	    if (validarFechas(fechaInicio, fechaFin) == false) {
	        return null;
	    }
	    if (validarSolapamiento(empleado.getId(), fechaInicio, fechaFin) == false) {
	        return null;
	    }

	    // Crea la entidad Teletrabajo a partir del DTO
	    Teletrabajo teletrabajo = new Teletrabajo();
	    teletrabajo.setEmpleado(empleado);
	    teletrabajo.setFechaInicio(fechaInicio);
	    teletrabajo.setFechaFin(fechaFin);
	    teletrabajo.setEstado(Teletrabajo.Estado.pendiente); // estado por defecto

	    return teletrabajoRepository.save(teletrabajo);
	}

	@Override
	public List<Teletrabajo> obtenerMisTeletrabajos(Authentication auth) {
		String email = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario == null || usuario.getEmpleado() == null) {
        	return Collections.emptyList();
        }

        return teletrabajoRepository.findByEmpleadoId(usuario.getEmpleado().getId());
	}

	@Override
	public Teletrabajo aprobarTeletrabajo(int id) {
		Teletrabajo teletrabajo = teletrabajoRepository.findById(id).orElse(null);
	    if (teletrabajo == null) {
	        return null;
	    }
	    teletrabajo.setEstado(Teletrabajo.Estado.aprobado);
	    return teletrabajoRepository.save(teletrabajo);
	}

	@Override
	public Teletrabajo rechazarTeletrabajo(int id) {
		Teletrabajo teletrabajo = teletrabajoRepository.findById(id).orElse(null);
	    if (teletrabajo == null) {
	        return null;
	    }
	    teletrabajo.setEstado(Teletrabajo.Estado.rechazado);
	    return teletrabajoRepository.save(teletrabajo);
	}

	@Override
	public List<Teletrabajo> obtenerTeletrabajosPendientes() {
		return teletrabajoRepository.findByEstado(Teletrabajo.Estado.pendiente);
	}

}
