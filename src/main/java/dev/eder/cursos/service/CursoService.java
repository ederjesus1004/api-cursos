package dev.eder.cursos.service;

import dev.eder.cursos.exception.RecursoNoEncontradoException;
import dev.eder.cursos.model.Curso;
import dev.eder.cursos.repository.CursoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CursoService {

    private final CursoRepository cursoRepository;

    //listar todos los cursos
    public List<Curso> listar(){
        return cursoRepository.findAll();
    }
    // Busca por curso de ID
    public Curso buscarPorId(Long id){
        return cursoRepository.findById(id)
                .orElseThrow(()-> new RecursoNoEncontradoException("No existe curso con id "+id));
    }
    //Crea los curso
    public Curso crear(Curso curso){
        return cursoRepository.save(curso);
    }
    // Actualiza el curso y guarda los datos nombre, descripcion, creditos y va a retornar
    // a curso (Curso curso = buscarPorId(id))
    public Curso actualizar(Long id, Curso datos){
        Curso curso = buscarPorId(id); // si no existe, lanza 404
        curso.setNombre(datos.getNombre());
        curso.setDescripcion(datos.getDescripcion());
        curso.setCreditos(datos.getCreditos());
        return cursoRepository.save(curso);
    }

    //Elimina por ID, se llama a Curso y se le nombra curso para poder eliminar por id
    public void eliminar(Long id){
        Curso curso = buscarPorId(id);
        cursoRepository.delete(curso);
    }
}
