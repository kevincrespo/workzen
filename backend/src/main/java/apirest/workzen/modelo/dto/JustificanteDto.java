package apirest.workzen.modelo.dto;

import apirest.workzen.modelo.entities.Justificante;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para devolver informacion de un justificante al frontend.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JustificanteDto {

    private int id;
    private String motivo;
    private String archivo;
    private int ausenciaId;
    private String fechaInicio;
    private String fechaFin;
    private String empleadoNombre;

    /**
     * Convierte una entidad Justificante a DTO.
     *
     * @param j             justificante
     * @param incluirNombre si true incluye el nombre del empleado (para admin)
     * @return el DTO
     */
    public static JustificanteDto convertirADto(Justificante justificante, boolean incluirNombre) {
        JustificanteDtoBuilder builder = JustificanteDto.builder()
                .id(justificante.getId())
                .motivo(justificante.getMotivo())
                .archivo(justificante.getArchivo())
                .ausenciaId(justificante.getAusencia().getId())
                .fechaInicio(justificante.getAusencia().getFechaInicio().toString())
                .fechaFin(justificante.getAusencia().getFechaFin().toString());

        if (incluirNombre && justificante.getAusencia().getEmpleado() != null) {
            String nombre = justificante.getAusencia().getEmpleado().getNombre()
                    + " " + justificante.getAusencia().getEmpleado().getApellido1();
            if (justificante.getAusencia().getEmpleado().getApellido2() != null) {
                nombre += " " + justificante.getAusencia().getEmpleado().getApellido2();
            }
            builder.empleadoNombre(nombre.trim());
        }

        return builder.build();
    }
}
