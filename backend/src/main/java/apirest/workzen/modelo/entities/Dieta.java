package apirest.workzen.modelo.entities;

import java.io.Serializable;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa una dieta (gastos de viaje) de un empleado.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder

@Schema(description = "Representa una dieta de un empleado.")
@Entity
@Table(name = "dietas")
public class Dieta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Identificador unico de la dieta", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Schema(description = "Empleado al que pertenece la dieta")
    @ManyToOne
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    /**
     * Tipo de dieta: nacional o internacional.
     */
    public enum Tipo {
        nacional, internacional
    }

    @Schema(description = "Tipo de dieta", example = "nacional")
    @Enumerated(EnumType.STRING)
    private Tipo tipo;

    @Schema(description = "Fecha de inicio de la dieta", example = "2026-03-10")
    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Schema(description = "Fecha de fin de la dieta", example = "2026-03-12")
    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Schema(description = "Motivo de la dieta", example = "Reunion con cliente en Madrid")
    private String motivo;

    /**
     * Estado de la dieta: pendiente, aprobado, rechazado o anulado.
     */
    public enum Estado {
        pendiente, aprobado, rechazado, anulado
    }

    @Schema(description = "Estado de la dieta", example = "pendiente")
    @Enumerated(EnumType.STRING)
    private Estado estado;
}
