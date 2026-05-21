package apirest.workzen.modelo.dto;

import apirest.workzen.modelo.entities.Empleado;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EmpleadoResumenDto {

	private int id;
	private String nombre;
	private String apellido1;
	private String apellido2;

	public static EmpleadoResumenDto convertirADto(Empleado empleado) {
		return EmpleadoResumenDto.builder()
				.id(empleado.getId())
				.nombre(empleado.getNombre())
				.apellido1(empleado.getApellido1())
				.apellido2(empleado.getApellido2())
				.build();
	}
}