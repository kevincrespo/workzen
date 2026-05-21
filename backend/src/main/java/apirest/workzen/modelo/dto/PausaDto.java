package apirest.workzen.modelo.dto;

import java.time.LocalDateTime;

import apirest.workzen.modelo.entities.Pausa;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar una pausa de un fichaje.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PausaDto {

    private int id;
    private LocalDateTime horaInicio;
    private LocalDateTime horaFin;

    /**
     * Convierte una entidad Pausa a PausaDto.
     *
     * @param pausa la entidad Pausa a convertir
     * @return el DTO resultante
     */
    public static PausaDto convertirADto(Pausa pausa) {
        return PausaDto.builder()
                .id(pausa.getId())
                .horaInicio(pausa.getHoraInicio())
                .horaFin(pausa.getHoraFin())
                .build();
    }
}
