package apirest.workzen.modelo.dto;

import java.time.LocalDateTime;
import apirest.workzen.modelo.entities.Ausencia;
import apirest.workzen.modelo.entities.Ausencia.Estado;
import apirest.workzen.modelo.entities.Ausencia.Tipo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder

public class AusenciaDto {
	
	private Integer id;
	private LocalDateTime fechaInicio;
	private LocalDateTime fechaFin;
	private Estado estado;
	private Tipo tipo;
       
	public static AusenciaDto convertirADto(Ausencia ausencia) {
		AusenciaDto ausenciaDto = new AusenciaDto();
		
		ausenciaDto.setFechaInicio(ausencia.getFechaInicio());
		ausenciaDto.setFechaFin(ausencia.getFechaFin());
		ausenciaDto.setEstado(ausencia.getEstado());
		ausenciaDto.setTipo(ausencia.getTipo());
		
		return ausenciaDto;
	}
    
}
