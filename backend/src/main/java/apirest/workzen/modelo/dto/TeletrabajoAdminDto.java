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
public class TeletrabajoAdminDto {
	
	private Integer id;
	private LocalDate fechaInicio;
	private LocalDate fechaFin;
	private Estado estado;
	private EmpleadoResumenDto empleado;

	public static TeletrabajoAdminDto convertirADto(Teletrabajo teletrabajo) {
		return TeletrabajoAdminDto.builder()
				.id(teletrabajo.getId())
				.fechaInicio(teletrabajo.getFechaInicio())
				.fechaFin(teletrabajo.getFechaFin())
				.estado(teletrabajo.getEstado())
				.empleado(EmpleadoResumenDto.convertirADto(teletrabajo.getEmpleado()))
				.build();
	}
}