package dev.eder.cursos.dto;


import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "EL email es obligatorio")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {

}
