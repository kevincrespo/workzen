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
import apirest.workzen.modelo.entities.Ausencia;
import apirest.workzen.modelo.entities.Notificacion;
import apirest.workzen.service.AusenciaService;
import apirest.workzen.service.NotificacionService;

@RestController
@RequestMapping("/ausencias")
public class AusenciaRestController {
	
	// Inyección de AusenciaService
	@Autowired
	private AusenciaService ausenciaService;

	@Autowired
	private NotificacionService notificacionService;
	
	// -------------------------- CRUD GENÉRICO ---------------------------- //
	@GetMapping("/{atributoPK}")
	public ResponseEntity<Ausencia> findOne(@PathVariable int atributoPK){
		Ausencia ausencia = ausenciaService.findOne(atributoPK);
		if(ausencia == null) {
			return ResponseEntity.notFound().build();
		}else {
			return ResponseEntity.ok(ausencia);
		}
	}
	
	@GetMapping("")
	public ResponseEntity<List<Ausencia>> findAll(){
		List<Ausencia> ausencias = ausenciaService.findAll();
		return ResponseEntity.ok(ausencias);
	}
	
	@PostMapping("")
	public ResponseEntity<Ausencia> insert(@RequestBody Ausencia objeto){
		Ausencia ausenciaInsertada = ausenciaService.insert(objeto);
		return ResponseEntity.status(HttpStatus.CREATED).body(ausenciaInsertada);
	}
	
	@PutMapping("/{atributoPK}")
	public ResponseEntity<Ausencia> update(@PathVariable int atributoPK, 
										  @RequestBody Ausencia objeto){
		Ausencia ausenciaActualizada = ausenciaService.update(atributoPK, objeto);
		if(ausenciaActualizada == null) {
			return ResponseEntity.notFound().build();
		}else {
			return ResponseEntity.ok(ausenciaActualizada);
		}
	}
	
	@DeleteMapping("/{atributoPK}")
	public ResponseEntity<Void> delete(@PathVariable int atributoPK){
		boolean resultado = ausenciaService.delete(atributoPK);
		if(resultado) {
			return ResponseEntity.noContent().build();
		}else {
			return ResponseEntity.notFound().build();
		}
	}
	// --------------------------------------------------------------------- //
	
	// Obtiene la lista de tipos de ausencia
	@GetMapping("/tipos")
	public ResponseEntity<List<Ausencia.Tipo>> obtenerTipos() {
	    List<Ausencia.Tipo> tipos = ausenciaService.obtenerTipos();
	    
	    return ResponseEntity.ok(tipos);
	}
	
	// PARA PRUEBAS EN POSTMAN
	// Devolver una lista de las ausencias de un empleado
	//@GetMapping("/mis-ausencias/{empleadoId}")
	//public ResponseEntity<List<Ausencia>> obtenerAusenciasDeEmpleado(@PathVariable int empleadoId) {
	//    List<Ausencia> ausencias = ausenciaService.obtenerAusenciasDeEmpleado(empleadoId);

	//    return ResponseEntity.ok(ausencias);
	//}
	
	// Solicitar ausencia para un empleado
	@PostMapping("/solicitar")
	public ResponseEntity<AusenciaDto> solicitarAusencia(Authentication auth, 
														@RequestBody SolicitudAusenciaDto solicitudAusenciaDto) {
		
		Ausencia nuevaAusencia = ausenciaService.solicitarAusencia(auth, solicitudAusenciaDto);

	    if (nuevaAusencia == null) {
	        return ResponseEntity.badRequest().build();
	    } else {
	        AusenciaDto ausenciaDto = AusenciaDto.convertirADto(nuevaAusencia);
	        
	        return ResponseEntity.status(HttpStatus.CREATED).body(ausenciaDto);
	    }
	}
	
	// Mostrar una lista de las ausencias del usuario logueado
	@GetMapping("/mis-ausencias")
	public ResponseEntity<List<AusenciaDto>> misAusencias(Authentication auth) {
		List<AusenciaDto> ausencias = ausenciaService.obtenerMisAusencias(auth)
				.stream()
				.map(AusenciaDto::convertirADto)
				.toList();

	    return ResponseEntity.ok(ausencias);
	}

	// Aprobar una ausencia pendiente (Este endpoint es para el Administrador)
	@PutMapping("/{id}/aprobar")
	public ResponseEntity<Ausencia> aprobar(@PathVariable int id) {
		Ausencia ausencia = ausenciaService.aprobarAusencia(id);
		if (ausencia == null) {
			return ResponseEntity.notFound().build();
		}
		// Notificar al empleado
		notificacionService.crear(ausencia.getEmpleado(), Notificacion.Tipo.ausencia_aprobada,
				"Tu solicitud de ausencia (" + ausencia.getTipo() + ") ha sido aprobada");
		return ResponseEntity.ok(ausencia);
	}

	// Rechazar una ausencia pendiente (Este endpoint es para el Administrador)
	@PutMapping("/{id}/rechazar")
	public ResponseEntity<Ausencia> rechazar(@PathVariable int id) {
		Ausencia ausencia = ausenciaService.rechazarAusencia(id);
		if (ausencia == null) {
			return ResponseEntity.notFound().build();
		}
		// Notificar al empleado
		notificacionService.crear(ausencia.getEmpleado(), Notificacion.Tipo.ausencia_rechazada,
				"Tu solicitud de ausencia (" + ausencia.getTipo() + ") ha sido rechazada");
		return ResponseEntity.ok(ausencia);
	}

	// Obtener ausencias pendientes con datos del empleado (Este endpoint es para el Administrador)
	@GetMapping("/admin/pendientes")
	public ResponseEntity<List<AusenciaAdminDto>> obtenerPendientes() {
		List<AusenciaAdminDto> pendientes = ausenciaService.obtenerAusenciasPendientes()
				.stream()
				.map(AusenciaAdminDto::convertirADto)
				.toList();
		return ResponseEntity.ok(pendientes);
	}

}
