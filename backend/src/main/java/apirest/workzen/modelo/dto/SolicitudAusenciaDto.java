package apirest.workzen.modelo.dto;

import java.time.LocalDateTime;
import apirest.workzen.modelo.entities.Ausencia;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SolicitudAusenciaDto {

    private Ausencia.Tipo tipo;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    
}

