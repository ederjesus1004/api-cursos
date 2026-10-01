package dev.eder.cursos.Security;


import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioDetailsService usuarioDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Leemos la cabecera "Authorization"
        String cabecera = request.getHeader("Authorization");

        // 2. Si no hay token, seguimos sin identificar a nadie.
        //    SecurityConfig decidirá después si la ruta era pública o no.
        if (cabecera == null || !cabecera.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Quitamos la palabra "Bearer " y nos quedamos con el token
        String token = cabecera.substring(7);

        try {
            // 4. Sacamos el email del token (aquí falla si está vencido o alterado)
            String email = jwtService.extraerEmail(token);

            // 5. Buscamos al usuario en la base de datos
            UserDetails usuario = usuarioDetailsService.loadUserByUsername(email);

            // 6. Le decimos a Spring: "esta petición es de este usuario, con estos permisos"
            UsernamePasswordAuthenticationToken autenticacion =
                    new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
            autenticacion.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(autenticacion);

        } catch (JwtException | UsernameNotFoundException e) {
            // Token inválido, vencido o de un usuario que ya no existe.
            // No hacemos nada: la petición sigue sin usuario y terminará en 401.
        }

        // 7. Dejamos pasar la petición al siguiente paso
        filterChain.doFilter(request, response);
    }
}
