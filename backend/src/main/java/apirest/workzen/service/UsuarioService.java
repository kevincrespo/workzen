package apirest.workzen.service;

import java.util.Optional;

import apirest.workzen.modelo.entities.Usuario;

public interface UsuarioService extends ICrudGenerico<Usuario, Integer> {
	
	Usuario login(String email, String password);
	
	/**
     * Busca un usuario solo por email (para validar el token JWT).
     */
    Optional<Usuario> findByEmail(String email);
}
