package apirest.workzen.security;

import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Clase de utilidad para manejar tokens JWT.
 * Permite generar tokens, validarlos y extraer informacion de ellos.
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * Genera la clave secreta para firmar los tokens.
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Genera un token JWT para el email dado, incluyendo el nombre y los privilegios del usuario.
     * El token expira segun el tiempo configurado en application.properties.
     */
    public String generarToken(String email, String nombre, List<String> privilegios) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expiration);

        var builder = Jwts.builder()
                .subject(email)
                .issuedAt(ahora)
                .expiration(expiracion);

        if (nombre != null) {
            builder.claim("nombre", nombre);
        }

        if (privilegios != null) {
            builder.claim("privilegios", privilegios);
        }

        return builder.signWith(getSigningKey()).compact();
    }

    /**
     * Extrae el email del token JWT.
     */
    public String getEmailDelToken(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Extrae los privilegios del token JWT.
     */
    @SuppressWarnings("unchecked")
    public List<String> getPrivilegiosDelToken(String token) {
        return getClaims(token).get("privilegios", List.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Valida si el token es correcto y no ha expirado.
     */
    public boolean validarToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
