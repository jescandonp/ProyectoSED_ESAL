package co.gov.bogota.sed.esal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Configuración de seguridad para el perfil local-dev (I1).
 * Usa HTTP Basic con usuarios en memoria.
 * Protege endpoints por rol: ADMINISTRADOR y EXPEDIDOR.
 * Habilita CORS para el frontend Angular en http://localhost:4200.
 *
 * Preparado para extensión en I4 con JWT Azure AD:
 * - JwtAuthenticationFilter stub en config/security/ listo para activarse.
 * - La separación de perfiles (local-dev vs weblogic) permite agregar
 *   la configuración JWT sin modificar esta clase.
 */
@Configuration
@Profile("local-dev")
public class DevSecurityConfig {

    @Bean
    SecurityFilterChain localDevSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                .authorizeRequests()
                // Endpoints públicos — sin autenticación
                .antMatchers(
                        "/actuator/health",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
                ).permitAll()
                // Solo ADMINISTRADOR — endpoints administrativos
                .antMatchers(HttpMethod.POST, "/api/admin/**").hasRole("ADMINISTRADOR")
                .antMatchers(HttpMethod.GET,  "/api/admin/**").hasRole("ADMINISTRADOR")
                // Solo ADMINISTRADOR — escritura sobre ESAL
                .antMatchers(HttpMethod.POST, "/api/esales").hasRole("ADMINISTRADOR")
                .antMatchers(HttpMethod.PUT,  "/api/esales/**").hasRole("ADMINISTRADOR")
                .antMatchers(HttpMethod.POST, "/api/esales/*/documentos").hasRole("ADMINISTRADOR")
                // ADMINISTRADOR o EXPEDIDOR — consultas ESAL
                .antMatchers(HttpMethod.GET,  "/api/esales/**").hasAnyRole("ADMINISTRADOR", "EXPEDIDOR")
                // ADMINISTRADOR o EXPEDIDOR — búsqueda y preview (I2)
                .antMatchers(HttpMethod.GET,  "/api/busquedas/**").hasAnyRole("ADMINISTRADOR", "EXPEDIDOR")
                .antMatchers(HttpMethod.GET,  "/api/certificados/**").hasAnyRole("ADMINISTRADOR", "EXPEDIDOR")
                .antMatchers(HttpMethod.POST, "/api/certificados/**").hasAnyRole("ADMINISTRADOR", "EXPEDIDOR")
                // Cualquier otra petición autenticada
                .anyRequest().authenticated()
                .and()
                .httpBasic();

        return http.build();
    }

    /**
     * CORS habilitado para el frontend Angular local (http://localhost:4200).
     * En I4 se ajustará para el dominio institucional real.
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    UserDetailsService localDevUsers(PasswordEncoder passwordEncoder) {
        return new InMemoryUserDetailsManager(
                User.withUsername("admin@educacionbogota.edu.co")
                        .password(passwordEncoder.encode("admin123"))
                        .roles("ADMINISTRADOR")
                        .build(),
                User.withUsername("expedidor@educacionbogota.edu.co")
                        .password(passwordEncoder.encode("expedidor123"))
                        .roles("EXPEDIDOR")
                        .build());
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
