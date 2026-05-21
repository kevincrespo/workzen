package apirest.workzen.modelo.dto;

import java.time.LocalDate;

import apirest.workzen.modelo.entities.Contrato;
import apirest.workzen.modelo.entities.Contrato.Tipo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ContratoDto {

    private Integer id;
    private Integer empleadoId;
    private Tipo tipo;
    private Integer salario;
    private LocalDate fecha;
    private String archivo;

    public static ContratoDto convertirADto(Contrato contrato) {
        ContratoDto dto = new ContratoDto();

        dto.setId(contrato.getId());
        dto.setEmpleadoId(contrato.getEmpleado().getId());
        dto.setTipo(contrato.getTipo());
        dto.setSalario(contrato.getSalario());
        dto.setFecha(contrato.getFecha());
        dto.setArchivo(contrato.getArchivo());

        return dto;
    }
}