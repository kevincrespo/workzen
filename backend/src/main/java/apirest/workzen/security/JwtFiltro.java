package apirest.workzen.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro que intercepta todas las peticiones HTTP.
 * Busca el header "Authorization" con el token JWT y lo valida.
 * Si el token es valido, autentica al usuario en Spring Security.
 */
@Component
public class JwtFiltro extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Obtener el header Authorization
        String authHeader = request.getHeader("Authorization");

        // Comprobar si tiene el formato "Bearer <token>"
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Quitar "Bearer "

            // Validar el token
            if (jwtUtil.validarToken(token)) {
                String email = jwtUtil.getEmailDelToken(token);

                // Extraer privilegios del token y convertirlos en authorities
                List<String> privilegios = jwtUtil.getPrivilegiosDelToken(token);
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                if (privilegios != null) {
                    for (String privilegio : privilegios) {
                        authorities.add(new SimpleGrantedAuthority(privilegio));
                    }
                }

                // Crear autenticacion para Spring Security con los privilegios
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(email, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        // Continuar con el siguiente filtro
        filterChain.doFilter(request, response);
    }
}
