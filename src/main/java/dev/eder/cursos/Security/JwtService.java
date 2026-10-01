package dev.eder.cursos.Security;

import dev.eder.cursos.model.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    // Se leen de application.properties
    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiracion-ms}")
    private long expiracionMs;

    // Crea un token para el usuario
    public String generarToken(Usuario usuario) {
        Date ahora = new Date();
        Date vence = new Date(ahora.getTime() + expiracionMs);

        return Jwts.builder()
                .subject(usuario.getEmail()) // a quién pertenece
                .claim("rol", usuario.getRol().name()) // dato extra: su rol
                .issuedAt(ahora) // cuándo se creó
                .expiration(vence) // cuándo vence
                .signWith(obtenerClave()) // se firma con la clave secreta
                .compact(); // se convierte a texto
    }

    // Lee el email que está dentro del token.
    // Si el token fue alterado o ya venció, lanza una excepción.
    public String extraerEmail(String token) {
        return Jwts.parser()
                .verifyWith(obtenerClave()) // revisa que la firma sea nuestra
                .build()
                .parseSignedClaims(token) // aquí falla si está vencido o alterado
                .getPayload()
                .getSubject(); // devuelve el email
    }

    // Convierte el texto secreto en una clave para firmar

    private SecretKey obtenerClave() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
