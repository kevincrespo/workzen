package apirest.workzen.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configuracion de seguridad para la API REST.
 * Define que rutas son publicas, configura CORS para Angular,
 * y registra el filtro JWT.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFiltro jwtFiltro;

    /**
     * Configura la seguridad HTTP.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desactivar CSRF (no necesario en APIs REST con JWT)
                .csrf(csrf -> csrf.disable())

                // Configurar CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Configurar las rutas
                .authorizeHttpRequests(auth -> auth
                        // Rutas publicas (login y swagger)
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()

                        // Rutas que requieren solo autenticacion
                        .requestMatchers("/empleados/dias-disponibles").authenticated()
                        .requestMatchers("/fichajes/mis-fichajes").authenticated()
                        .requestMatchers("/notificaciones/**").authenticated()
                        .requestMatchers("/justificantes/mis-justificantes").authenticated()
                        .requestMatchers("/justificantes/ausencias-pendientes").authenticated()
                        .requestMatchers("/justificantes/subir").authenticated()
                        .requestMatchers("/justificantes/*/descargar").authenticated()
                        .requestMatchers(HttpMethod.GET, "/certificados/empleado/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/certificados/*/descargar").authenticated()

                        // Rutas restringidas a admin y rrhh
                        .requestMatchers("/departamentos/**").hasAnyAuthority("admin", "rrhh")
                        .requestMatchers("/fichajes/todos").hasAnyAuthority("admin", "rrhh")
                        .requestMatchers("/festivos/**").hasAnyAuthority("admin", "rrhh")
                        .requestMatchers("/empleados/**").hasAnyAuthority("admin", "rrhh")
                        .requestMatchers("/nominas/subir").hasAnyAuthority("admin", "rrhh")
                        .requestMatchers("/contratos/subir").hasAnyAuthority("admin", "rrhh")
                        .requestMatchers("/contratos/buscar-por-nombre").hasAnyAuthority("admin", "rrhh")
                        .requestMatchers("/contratos/buscar-por-tipo").hasAnyAuthority("admin", "rrhh")
                        .requestMatchers("/contratos/buscar-por-fechas").hasAnyAuthority("admin", "rrhh")
                        .requestMatchers("/contratos/buscar-por-departamento").hasAnyAuthority("admin", "rrhh")
                        .requestMatchers(HttpMethod.GET, "/justificantes").hasAnyAuthority("admin", "rrhh")
                        .requestMatchers(HttpMethod.DELETE, "/justificantes/**").hasAnyAuthority("admin", "rrhh")
                        .requestMatchers(HttpMethod.POST, "/documentos/**").hasAnyAuthority("admin", "rrhh")
                        .requestMatchers(HttpMethod.DELETE, "/documentos/**").hasAnyAuthority("admin", "rrhh")
                        //.requestMatchers(HttpMethod.GET, "/certificados/**").hasAnyAuthority("admin", "rrhh")
                        .requestMatchers(HttpMethod.POST, "/certificados/**").hasAnyAuthority("admin", "rrhh")
                        .requestMatchers(HttpMethod.PUT, "/certificados/**").hasAnyAuthority("admin", "rrhh")
                        .requestMatchers(HttpMethod.DELETE, "/certificados/**").hasAnyAuthority("admin", "rrhh")
                        // El resto requiere autenticacion
                        .anyRequest().authenticated()
                )

                // No usar sesiones (JWT es stateless)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Añadir el filtro JWT antes del filtro de autenticacion
                .addFilterBefore(jwtFiltro, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configura CORS para permitir peticiones desde Angular.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Origenes permitidos (Angular local y produccion)
        configuration.setAllowedOrigins(List.of(
                "http://localhost:4200",
                "https://workzen.jccv.es"
        ));

        // Metodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Headers permitidos
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

        // Permitir credenciales
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    /**
     * Bean para encriptar contraseñas con BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
