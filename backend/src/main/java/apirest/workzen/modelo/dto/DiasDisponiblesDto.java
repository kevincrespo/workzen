package apirest.workzen.modelo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO con los dias de vacaciones del empleado.
 * Incluye dias totales asignados, consumidos y disponibles.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiasDisponiblesDto {

    /** Dias de vacaciones asignados al empleado. */
    private int diasTotales;

    /** Dias gastados (ausencias aprobadas tipo vacaciones). */
    private int diasConsumidos;

    /** Dias restantes (diasTotales - diasConsumidos). */
    private int diasDisponibles;
}
