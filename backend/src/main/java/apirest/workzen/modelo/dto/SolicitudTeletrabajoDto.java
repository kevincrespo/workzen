package apirest.workzen.modelo.dto;

import java.time.LocalDate;
import apirest.workzen.modelo.entities.Teletrabajo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder	
public class SolicitudTeletrabajoDto {
	
	private LocalDate fechaInicio;
	private LocalDate fechaFin;
	//private Teletrabajo.Estado estado;
	
}
