package apirest.workzen.modelo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para devolver la respuesta del login a Angular.
 * Incluye el token JWT.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginResponse {

    private String token;
}
