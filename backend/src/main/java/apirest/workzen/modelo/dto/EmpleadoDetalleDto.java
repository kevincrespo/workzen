package apirest.workzen.modelo.dto;

import java.time.LocalDate;

import apirest.workzen.modelo.entities.Empleado;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO con el detalle completo de un empleado, incluyendo departamento.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EmpleadoDetalleDto {

    private Integer id;
    private String nombre;
    private String apellido1;
    private String apellido2;
    private String nif;
    private String numeroSs;
    private String direccion;
    private String provincia;
    private String localidad;
    private Integer codigoPostal;
    private LocalDate fechaNacimiento;
    private String iban;
    private String telefono;
    private Integer departamentoId;
    private String departamento;

    /**
     * Convierte una entidad Empleado a EmpleadoDetalleDto.
     *
     * @param empleado la entidad Empleado a convertir
     * @return el DTO resultante con datos del departamento
     */
    public static EmpleadoDetalleDto convertirADto(Empleado empleado) {
        return EmpleadoDetalleDto.builder()
                .id(empleado.getId())
                .nombre(empleado.getNombre())
                .apellido1(empleado.getApellido1())
                .apellido2(empleado.getApellido2())
                .nif(empleado.getNif())
                .numeroSs(empleado.getNumeroSS())
                .direccion(empleado.getDireccion())
                .provincia(empleado.getProvincia())
                .localidad(empleado.getLocalidad())
                .codigoPostal(empleado.getCodigoPostal())
                .fechaNacimiento(empleado.getFechaNacimiento())
                .iban(empleado.getIban())
                .telefono(empleado.getTelefono())
                .departamentoId(empleado.getDepartamento().getId())
                .departamento(empleado.getDepartamento().getNombre())
                .build();
    }
}
