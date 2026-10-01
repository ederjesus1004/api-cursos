package dev.eder.cursos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cursos")
@Data
@NoArgsConstructor
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false, length = 55)
    private String nombre;

    @Column(nullable = false, length = 255)
    private String descripcion;

    @NotNull(message = "Los creditos son obligatorios")
    @Min(value = 1, message = "Minimo 1 credito")
    @Max(value = 6, message = "Maximo 6 creditos ")
    @Column(nullable = false)
    private Integer creditos;
    
}
