package apirest.workzen.restcontroller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import apirest.workzen.modelo.dto.AusenciaAdminDto;
import apirest.workzen.modelo.dto.AusenciaDto;
import apirest.workzen.modelo.dto.SolicitudAusenciaDto;
import apirest.workzen.modelo.dto.SolicitudTeletrabajoDto;
import apirest.workzen.modelo.dto.TeletrabajoAdminDto;
import apirest.workzen.modelo.dto.TeletrabajoDto;
import apirest.workzen.modelo.entities.Ausencia;
import apirest.workzen.modelo.entities.Notificacion;
import apirest.workzen.modelo.entities.Teletrabajo;
import apirest.workzen.service.NotificacionService;
import apirest.workzen.service.TeletrabajoService;

@RestController
@RequestMapping("/teletrabajos")
public class TeletrabajoRestController {
	
	// Inyección de TeletrabajoService
	@Autowired
	private TeletrabajoService teletrabajoService;

	@Autowired
	private NotificacionService notificacionService;
	
	// -------------------------- CRUD GENÉRICO ---------------------------- //
	@GetMapping("/{atributoPK}")
	public ResponseEntity<Teletrabajo> findOne(@PathVariable int atributoPK){
	    Teletrabajo teletrabajo = teletrabajoService.findOne(atributoPK);
	    if(teletrabajo == null) {
	        return ResponseEntity.notFound().build();
	    } else {
	        return ResponseEntity.ok(teletrabajo);
	    }
	}

	@GetMapping("")
	public ResponseEntity<List<Teletrabajo>> findAll(){
	    List<Teletrabajo> teletrabajos = teletrabajoService.findAll();
	    return ResponseEntity.ok(teletrabajos);
	}

	@PostMapping("")
	public ResponseEntity<Teletrabajo> insert(@RequestBody Teletrabajo objeto){
	    Teletrabajo teletrabajoInsertado = teletrabajoService.insert(objeto);
	    return ResponseEntity.status(HttpStatus.CREATED).body(teletrabajoInsertado);
	}

	@PutMapping("/{atributoPK}")
	public ResponseEntity<Teletrabajo> update(@PathVariable int atributoPK, 
	                                          @RequestBody Teletrabajo objeto){
	    Teletrabajo teletrabajoActualizado = teletrabajoService.update(atributoPK, objeto);
	    if(teletrabajoActualizado == null) {
	        return ResponseEntity.notFound().build();
	    } else {
	        return ResponseEntity.ok(teletrabajoActualizado);
	    }
	}

	@DeleteMapping("/{atributoPK}")
	public ResponseEntity<Void> delete(@PathVariable int atributoPK){
	    boolean resultado = teletrabajoService.delete(atributoPK);
	    if(resultado) {
	        return ResponseEntity.noContent().build();
	    } else {
	        return ResponseEntity.notFound().build();
	    }
	}
	// --------------------------------------------------------------------- //
	
	// Solicitar teletrabajo para un empleado
	@PostMapping("/solicitar")
	public ResponseEntity<TeletrabajoDto> solicitarTeletrabajo(
	        Authentication auth,
	        @RequestBody SolicitudTeletrabajoDto solicitudTeletrabajoDto) {

	    Teletrabajo nuevoTeletrabajo = teletrabajoService.solicitarTeletrabajo(auth, solicitudTeletrabajoDto);

	    if (nuevoTeletrabajo == null) {
	        return ResponseEntity.badRequest().build();
	    } else {
	        TeletrabajoDto teletrabajoDto = TeletrabajoDto.convertirADto(nuevoTeletrabajo);

	        return ResponseEntity.status(HttpStatus.CREATED).body(teletrabajoDto);
	    }
	}
	
	// Mostrar una lista de los teletrabajos del usuario logueado
	@GetMapping("/mis-teletrabajos")
	public ResponseEntity<List<TeletrabajoDto>> misTeletrabajos(Authentication auth) {
	    List<TeletrabajoDto> teletrabajos = teletrabajoService.obtenerMisTeletrabajos(auth)
	            .stream()
	            .map(TeletrabajoDto::convertirADto)
	            .toList();

	    return ResponseEntity.ok(teletrabajos);
	}
	
	// Aprobar un teletrabajo pendiente (Este endpoint es para el Administrador)
	@PutMapping("/{id}/aprobar")
	public ResponseEntity<Teletrabajo> aprobar(@PathVariable int id) {
	    Teletrabajo teletrabajo = teletrabajoService.aprobarTeletrabajo(id);
	    if (teletrabajo == null) {
	        return ResponseEntity.notFound().build();
	    }
	    // Notificar al empleado
	    notificacionService.crear(teletrabajo.getEmpleado(), Notificacion.Tipo.teletrabajo_aprobado,
	            "Tu solicitud de teletrabajo ha sido aprobada");
	    return ResponseEntity.ok(teletrabajo);
	}

	// Rechazar un teletrabajo pendiente (Este endpoint es para el Administrador)
	@PutMapping("/{id}/rechazar")
	public ResponseEntity<Teletrabajo> rechazar(@PathVariable int id) {
	    Teletrabajo teletrabajo = teletrabajoService.rechazarTeletrabajo(id);
	    if (teletrabajo == null) {
	        return ResponseEntity.notFound().build();
	    }
	    // Notificar al empleado
	    notificacionService.crear(teletrabajo.getEmpleado(), Notificacion.Tipo.teletrabajo_rechazado,
	            "Tu solicitud de teletrabajo ha sido rechazada");
	    return ResponseEntity.ok(teletrabajo);
	}
	
	// Obtener teletrabajos pendientes con datos del empleado (Este endpoint es para el Administrador)
	@GetMapping("/admin/pendientes")
	public ResponseEntity<List<TeletrabajoAdminDto>> obtenerPendientes() {
	    List<TeletrabajoAdminDto> pendientes = teletrabajoService.obtenerTeletrabajosPendientes()
	            .stream()
	            .map(TeletrabajoAdminDto::convertirADto)
	            .toList();
	    return ResponseEntity.ok(pendientes);
	}

}
