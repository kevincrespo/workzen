package apirest.workzen.modelo.dto;

import java.time.LocalDate;

import apirest.workzen.modelo.entities.Dieta;
import apirest.workzen.modelo.entities.Dieta.Estado;
import apirest.workzen.modelo.entities.Dieta.Tipo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar una dieta del empleado autenticado.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DietaDto {

    private Integer id;
    private Tipo tipo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String motivo;
    private Estado estado;

    /**
     * Convierte una entidad Dieta a DietaDto.
     *
     * @param dieta la entidad Dieta a convertir
     * @return el DTO resultante
     */
    public static DietaDto convertirADto(Dieta dieta) {
        return DietaDto.builder()
                .id(dieta.getId())
                .tipo(dieta.getTipo())
                .fechaInicio(dieta.getFechaInicio())
                .fechaFin(dieta.getFechaFin())
                .motivo(dieta.getMotivo())
                .estado(dieta.getEstado())
                .build();
    }
}
