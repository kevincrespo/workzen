package apirest.workzen.modelo.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NominaDto {
    private int id;
    private LocalDate fecha;
    private String archivoUrl;
}
