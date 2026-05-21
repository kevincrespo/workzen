package apirest.workzen.modelo.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
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

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder

@Schema(description = "Representa una ausencia de un empleado.")
@Entity
@Table(name = "ausencias")
public class Ausencia implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Identificador único de la ausencia", 
    			example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Schema(description = "Empleado al que pertenece la ausencia")
    @ManyToOne
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @Schema(description = "Fecha y hora de inicio de la ausencia", 
    			example = "2026-02-01T09:00:00")
    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @Schema(description = "Fecha y hora de fin de la ausencia", 
    			example = "2026-02-05T18:00:00")
    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    public enum Estado {
        pendiente, aprobado, rechazado, anulado
    }
    @Enumerated(EnumType.STRING)
    private Estado estado;

    public enum Tipo {
        vacaciones, medico, permiso, otros
    }
    @Enumerated(EnumType.STRING)
    private Tipo tipo;
}






























