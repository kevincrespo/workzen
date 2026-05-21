package apirest.workzen.modelo.dto;

import java.time.LocalDate;
import apirest.workzen.modelo.entities.Teletrabajo;
import apirest.workzen.modelo.entities.Teletrabajo.Estado;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder

public class TeletrabajoDto {
	
	private Integer id;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Estado estado;
    
    public static TeletrabajoDto convertirADto(Teletrabajo teletrabajo) {
    	TeletrabajoDto teletrabajoDto = new TeletrabajoDto();
    	
    	//teletrabajoDto.setId(teletrabajo.getId());
    	teletrabajoDto.setFechaInicio(teletrabajo.getFechaInicio());
    	teletrabajoDto.setFechaFin(teletrabajo.getFechaFin());
    	teletrabajoDto.setEstado(teletrabajo.getEstado());
    	
    	return teletrabajoDto;
    }

}
