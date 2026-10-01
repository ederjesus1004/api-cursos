package dev.eder.cursos.controller;

import dev.eder.cursos.model.Curso;
import dev.eder.cursos.service.CursoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cursos")
@RequiredArgsConstructor
public class CursoController {

    private final CursoService cursoService;

    // LLama a service y lista todos los cursos
    @GetMapping
    public List<Curso> listar() {
        return cursoService.listar();
    }

    // busca por ID y @PathVariable permite transformar a un objeto
    @GetMapping("/{id}")
    public Curso buscarPorId(@PathVariable Long id) {
        return cursoService.buscarPorId(id);
    }

    // Crea un curso
    // Establece el código HTTP 201 Created, que es el estándar REST para indicar
    // que se creó un recurso exitosamente.
    @PostMapping
    public ResponseEntity<Curso> actualizar(@Valid @RequestBody Curso curso) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cursoService.crear(curso));
    }

    // Actualizar por ID y curso con sus datos
    @PutMapping("/{id}")
    public Curso actualizar(@PathVariable Long id, @Valid @RequestBody Curso curso) {
        return cursoService.actualizar(id, curso);
    }

    // Elimina por ID al curso
    @DeleteMapping("/{id}")
    public ResponseEntity<Curso> eliminar(@PathVariable Long id) {
        cursoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}
