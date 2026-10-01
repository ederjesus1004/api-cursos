package dev.eder.cursos.config;

import dev.eder.cursos.Security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)throws Exception{
        http
        // 1. Desactivamos CSRF: protege formularios con cookies, y nosotros usamos tokens
                .csrf(AbstractHttpConfigurer::disable)
        // 2. Sin sesiones: el servidor no recuerda a nadie, cada petición trae su token
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // 3. Las reglas de acceso (se revisan de arriba hacia abajo)
                .authorizeHttpRequests(auth -> auth
                        // Registro y login: abiertos para todos
                        .requestMatchers("/api/auth/**").permitAll()
                        // Página de error de Spring: abierta para que se vean los mensajes
                        .requestMatchers("/error").permitAll()
                        // Ver cursos: cualquier usuario con sesión
                        .requestMatchers(HttpMethod.GET,"/api/cursos/**").authenticated()
                        // Crear, editar y eliminar cursos: solo ADMIN
                        .requestMatchers(HttpMethod.POST,"/api/cursos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/cursos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/cursos/**").hasRole("ADMIN")
                        // Cualquier otra ruta: necesita sesión
                        .anyRequest().authenticated()
                        // 4. Si no tiene token válido, responde 401 (sin esto, Spring respondería 403)
                )
                .exceptionHandling(e-> e.authenticationEntryPoint(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
        // 5. Nuestro vigilante va ANTES del filtro de login normal de Spring
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();

    }

    // BCrypt: el encargado de convertir contraseñas en hash y compararlas
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    // El que revisa email + contraseña en el login
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }
}
