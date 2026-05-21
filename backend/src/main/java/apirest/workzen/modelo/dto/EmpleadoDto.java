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
public class EmpleadoDto {

    private int id;

    private int usuarioId;
    private int departamentoId;

    private String nombre;
    private String apellido1;
    private String apellido2;
    private String nif;

    private String numeroSS;

    private String direccion;
    private String provincia;
    private String localidad;
    private Integer codigoPostal;

    private LocalDate fechaNacimiento;

    private String iban;
    private String telefono;
}
