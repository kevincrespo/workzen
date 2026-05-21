package apirest.workzen.modelo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO con datos resumidos del empleado incluyendo departamento.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class dtoUsuarios {
    private int id;
    private String nombre;
    private String apellido1;
    private String apellido2;
    private String departamento;
}
