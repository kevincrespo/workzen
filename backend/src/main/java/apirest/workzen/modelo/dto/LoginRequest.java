package apirest.workzen.modelo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para recibir las credenciales de login desde Angular.
 * Contiene el email y password que envia el usuario.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginRequest {

    private String email;
    private String password;
}
