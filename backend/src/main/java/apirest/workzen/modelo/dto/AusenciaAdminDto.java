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
public class AusenciaAdminDto {

	private Integer id;
	private Tipo tipo;
	private LocalDateTime fechaInicio;
	private LocalDateTime fechaFin;
	private Estado estado;
	private EmpleadoResumenDto empleado;

	public static AusenciaAdminDto convertirADto(Ausencia ausencia) {
		return AusenciaAdminDto.builder()
				.id(ausencia.getId())
				.tipo(ausencia.getTipo())
				.fechaInicio(ausencia.getFechaInicio())
				.fechaFin(ausencia.getFechaFin())
				.estado(ausencia.getEstado())
				.empleado(EmpleadoResumenDto.convertirADto(ausencia.getEmpleado()))
				.build();
	}
}