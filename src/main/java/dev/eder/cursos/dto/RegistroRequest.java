package dev.eder.cursos.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Fíjate: NO tiene campo "rol". Nadie puede registrarse como ADMIN.
public record RegistroRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "EL email es obligatorio")
        @Email(message = "El email no tiene el formato valido")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, message = "La contraseña debe tener la menos 6 caracteres")
        String password
) {
}
