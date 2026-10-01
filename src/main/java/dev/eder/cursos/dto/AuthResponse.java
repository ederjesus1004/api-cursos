package dev.eder.cursos.dto;

// Lo que devolvemos después del registro o login.
// Fíjate: NO devolvemos la contraseña, ni siquiera el hash.
public record AuthResponse(
        String tocken,
        String email,
        String rol
) {
}
