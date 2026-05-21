package apirest.workzen.modelo.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la creacion de un empleado con su usuario y privilegios.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CrearEmpleadoDto {

    /** Email del usuario a crear. */
    private String email;

    /** Password del usuario a crear. */
    private String password;

    /** Lista de nombres de privilegios (ej: "admin", "rrhh"). */
    private List<String> privilegios;

    /** Nombre del empleado. */
    private String nombre;

    /** Primer apellido del empleado. */
    private String apellido1;

    /** Segundo apellido del empleado. */
    private String apellido2;

    /** NIF del empleado. */
    private String nif;

    /** Numero de la Seguridad Social. */
    private String numeroSs;

    /** Direccion del empleado. */
    private String direccion;

    /** Provincia del empleado. */
    private String provincia;

    /** Localidad del empleado. */
    private String localidad;

    /** Codigo postal del empleado. */
    private Integer codigoPostal;

    /** Fecha de nacimiento del empleado. */
    private LocalDate fechaNacimiento;

    /** IBAN del empleado. */
    private String iban;

    /** Telefono del empleado. */
    private String telefono;

    /** ID del departamento al que pertenece. */
    private Integer departamentoId;
}
