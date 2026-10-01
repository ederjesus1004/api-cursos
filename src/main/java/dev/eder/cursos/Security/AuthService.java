package dev.eder.cursos.Security;

import dev.eder.cursos.dto.AuthResponse;
import dev.eder.cursos.dto.LoginRequest;
import dev.eder.cursos.dto.RegistroRequest;
import dev.eder.cursos.exception.EmailYaRegistradoException;
import dev.eder.cursos.model.Rol;
import dev.eder.cursos.model.Usuario;
import dev.eder.cursos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse registrar(RegistroRequest request) {
        // 1. ¿Ya existe ese email?
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new EmailYaRegistradoException(request.email());
        }

        // 2. Creamos el usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre());
        usuario.setEmail(request.email());
        // 3. Guardamos el HASH de la contraseña, nunca la original
        usuario.setPassword(passwordEncoder.encode(request.password()));
        // 4. Todo el que se registra es USER. El rol lo decide el servidor, no el cliente.
        usuario.setRol(Rol.USER);

        usuarioRepository.save(usuario);

        // 5. Le damos su token para que no tenga que hacer login después de registrarse
        String token = jwtService.generarToken(usuario);
        return new AuthResponse(token, usuario.getEmail(), usuario.getRol().name());
    }

    public AuthResponse login(LoginRequest request) {
        // 1. Spring revisa email + contraseña.
        //    Por dentro usa UsuarioDetailsService y BCrypt.
        //    Si algo está mal, lanza BadCredentialsException y no sigue.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        // 2. Si llegó aquí, las credenciales son correctas
        Usuario usuario = usuarioRepository.findByEmail(request.email()).orElseThrow();

        // 3. Creamos su token
        String token = jwtService.generarToken(usuario);
        return new AuthResponse(token, usuario.getEmail(), usuario.getRol().name());
    }
}
