package apirest.workzen.modelo.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entidad que representa una pausa dentro de un fichaje.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder

@Schema(description = "Representa una pausa dentro de un fichaje.")
@Entity
@Table(name = "pausas")
public class Pausa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Identificador unico de la pausa", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Schema(description = "Fichaje al que pertenece la pausa")
    @ManyToOne
    @JoinColumn(name = "fichaje_id", nullable = false)
    @JsonBackReference
    private Fichaje fichaje;

    @Schema(description = "Hora de inicio de la pausa", example = "2026-02-21T10:00:00")
    @Column(name = "hora_inicio")
    private LocalDateTime horaInicio;

    @Schema(description = "Hora de fin de la pausa (null si esta activa)", example = "2026-02-21T10:15:00")
    @Column(name = "hora_fin")
    private LocalDateTime horaFin;
}
