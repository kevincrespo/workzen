package apirest.workzen.modelo.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

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

/**
 * Entidad que representa una notificacion para un empleado.
 * Se genera automaticamente cuando ocurren acciones relevantes
 * como subida de nominas, aprobacion de ausencias, etc.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "notificaciones")
public class Notificacion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Empleado al que va dirigida la notificacion. */
    @ManyToOne
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    /** Tipo de notificacion para poder filtrar o mostrar iconos distintos. */
    @Enumerated(EnumType.STRING)
    private Tipo tipo;

    /** Mensaje descriptivo de la notificacion. */
    @Column(nullable = false, length = 500)
    private String mensaje;

    /** Indica si el empleado ya ha leido la notificacion. */
    @Builder.Default
    private boolean leida = false;

    /** Fecha y hora en que se creo la notificacion. */
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    /**
     * Tipos de notificacion posibles.
     */
    public enum Tipo {
        nomina_nueva,
        contrato_nuevo,
        ausencia_aprobada,
        ausencia_rechazada,
        teletrabajo_aprobado,
        teletrabajo_rechazado,
        dieta_aprobada,
        dieta_rechazada
    }
}
