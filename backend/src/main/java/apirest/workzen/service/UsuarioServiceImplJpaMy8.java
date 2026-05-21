package apirest.workzen.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import apirest.workzen.modelo.entities.Usuario;
import apirest.workzen.modelo.repository.UsuarioRepository;

/**
 * Implementacion del servicio de usuarios usando JPA.
 */
@Service
public class UsuarioServiceImplJpaMy8 implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;	// para verificar la contraseña encriptada (BCrypt)

    @Override
    public Usuario findOne(Integer atributoPK) {
        return usuarioRepository.findById(atributoPK).orElse(null);
    }

    @Override
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario insert(Usuario usuario) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Usuario update(Integer atributoPK, Usuario usuario) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean delete(Integer atributoPK) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Verifica las credenciales del usuario usando BCrypt.
     * Busca el usuario por email y compara la contraseña encriptada.
     */
    @Override
    public Usuario login(String email, String password) {
        // Buscar usuario solo por email
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        // Si existe y la contraseña coincide, devolver el usuario
        if (usuario != null && passwordEncoder.matches(password, usuario.getPassword())) {
            return usuario;
        }

        return null;
    }

    // Busca un usuario solo por email
	@Override
	public Optional<Usuario> findByEmail(String email) {
		return usuarioRepository.findByEmail(email);
	}
}
