package apirest.workzen.modelo.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "empleados")
public class Empleado implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "usuario_id")
    @JsonBackReference
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "departamento_id", nullable = false)
    private Departamento departamento;

    private String nombre;
    private String apellido1;
    private String apellido2;
    private String nif;

    @Column(name = "numero_ss")
    private String numeroSS;

    private String direccion;
    private String provincia;
    private String localidad;

    @Column(name = "codigo_postal")
    private Integer codigoPostal;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    private String iban;
    private String telefono;

    /** Dias de vacaciones totales asignados al empleado para el ano actual. */
    @Column(name = "dias_vacaciones")
    private int diasVacaciones;
}

























