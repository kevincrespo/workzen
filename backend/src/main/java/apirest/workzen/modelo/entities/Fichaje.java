package apirest.workzen.modelo.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Entidad que representa un fichaje (registro de jornada) de un empleado.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder

@Schema(description = "Representa un fichaje de un empleado.")
@Entity
@Table(name = "fichajes")
public class Fichaje implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Identificador unico del fichaje", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Schema(description = "Empleado al que pertenece el fichaje")
    @ManyToOne
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @Schema(description = "Fecha y hora de entrada", example = "2026-02-21T08:30:00")
    @Column(name = "fecha_entrada")
    private LocalDateTime fechaEntrada;

    @Schema(description = "Fecha y hora de salida", example = "2026-02-21T17:00:00")
    @Column(name = "fecha_salida")
    private LocalDateTime fechaSalida;

    /**
     * Estado del fichaje: activo, finalizado o error.
     */
    public enum Estado {
        activo, finalizado, error
    }

    @Schema(description = "Estado del fichaje", example = "activo")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado;

    @Schema(description = "Lista de pausas del fichaje")
    @OneToMany(mappedBy = "fichaje", cascade = CascadeType.ALL)
    @JsonManagedReference
    @ToString.Exclude
    @Builder.Default
    private List<Pausa> pausas = new ArrayList<>();
}
