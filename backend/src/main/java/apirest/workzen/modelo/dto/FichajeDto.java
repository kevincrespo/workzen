package apirest.workzen.modelo.dto;

import java.time.LocalDateTime;
import java.util.List;

import apirest.workzen.modelo.entities.Fichaje;
import apirest.workzen.modelo.entities.Fichaje.Estado;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar un fichaje con sus pausas.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FichajeDto {

    private int id;
    private LocalDateTime fechaEntrada;
    private LocalDateTime fechaSalida;
    private Estado estado;
    private List<PausaDto> pausas;
    private Integer empleadoId;
    private String empleadoNombre;

    /**
     * Convierte una entidad Fichaje a FichajeDto incluyendo sus pausas.
     *
     * @param fichaje la entidad Fichaje a convertir
     * @return el DTO resultante con pausas
     */
    public static FichajeDto convertirADto(Fichaje fichaje) {
        return convertirADto(fichaje, false);
    }

    /**
     * Convierte una entidad Fichaje a FichajeDto, opcionalmente con nombre del empleado.
     *
     * @param fichaje        la entidad Fichaje a convertir
     * @param incluirEmpleado si true, incluye id y nombre del empleado
     * @return el DTO resultante
     */
    public static FichajeDto convertirADto(Fichaje fichaje, boolean incluirEmpleado) {
        List<PausaDto> pausasDto = fichaje.getPausas()
                .stream()
                .map(PausaDto::convertirADto)
                .toList();

        FichajeDtoBuilder builder = FichajeDto.builder()
                .id(fichaje.getId())
                .fechaEntrada(fichaje.getFechaEntrada())
                .fechaSalida(fichaje.getFechaSalida())
                .estado(fichaje.getEstado())
                .pausas(pausasDto);

        if (incluirEmpleado && fichaje.getEmpleado() != null) {
            builder.empleadoId(fichaje.getEmpleado().getId());
            builder.empleadoNombre(fichaje.getEmpleado().getNombre() + " " + fichaje.getEmpleado().getApellido1());
        }

        return builder.build();
    }
}
