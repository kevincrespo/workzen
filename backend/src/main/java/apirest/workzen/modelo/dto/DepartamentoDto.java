package apirest.workzen.modelo.dto;

import apirest.workzen.modelo.entities.Departamento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de departamento con el numero de empleados asignados.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DepartamentoDto {
    private Integer id;
    private String nombre;
    private long numEmpleados;

    public static DepartamentoDto convertirADto(Departamento dep, long numEmpleados) {
        return DepartamentoDto.builder()
                .id(dep.getId())
                .nombre(dep.getNombre())
                .numEmpleados(numEmpleados)
                .build();
    }
}