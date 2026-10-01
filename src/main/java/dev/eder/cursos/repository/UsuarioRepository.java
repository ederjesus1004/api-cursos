package dev.eder.cursos.repository;

import dev.eder.cursos.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // SELECT * FROM usuarios WHERE email = ?
    Optional<Usuario> findByEmail(String email);

    // ¿Ya existe alguien con este email? → true / false
    boolean existsByEmail(String email);
}
