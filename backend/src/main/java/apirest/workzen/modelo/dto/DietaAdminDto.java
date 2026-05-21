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
 * DTO para representar una dieta con datos del empleado (vista admin/rrhh).
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DietaAdminDto {

    private Integer id;
    private Tipo tipo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String motivo;
    private Estado estado;
    private EmpleadoResumenDto empleado;

    /**
     * Convierte una entidad Dieta a DietaAdminDto incluyendo datos del empleado.
     *
     * @param dieta la entidad Dieta a convertir
     * @return el DTO resultante con informacion del empleado
     */
    public static DietaAdminDto convertirADto(Dieta dieta) {
        return DietaAdminDto.builder()
                .id(dieta.getId())
                .tipo(dieta.getTipo())
                .fechaInicio(dieta.getFechaInicio())
                .fechaFin(dieta.getFechaFin())
                .motivo(dieta.getMotivo())
                .estado(dieta.getEstado())
                .empleado(EmpleadoResumenDto.convertirADto(dieta.getEmpleado()))
                .build();
    }
}
