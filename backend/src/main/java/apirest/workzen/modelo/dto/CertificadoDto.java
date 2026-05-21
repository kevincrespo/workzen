package apirest.workzen.modelo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CertificadoDto {

    private int certificadoId;
    private int empleadoId;
    private String nombre;
    private String empleadoNombre; // Nombre completo del empleado
    private LocalDate fecha;
    private String archivo;
}
