package co.gov.bogota.sed.esal.config;

import co.gov.bogota.sed.esal.config.security.EsalAccessDeniedHandler;
import co.gov.bogota.sed.esal.config.security.EsalAuthenticationEntryPoint;
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
 * Configuracion de seguridad para el perfil local-dev.
 * Usa HTTP Basic con usuarios en memoria.
 * En I4 se agrego: headers de seguridad, handlers de auditoria
 * y cobertura completa de endpoints I1-I3 (certificados, importacion, auditoria).
 */
@Configuration
@Profile("local-dev")
public class DevSecurityConfig {

    @Bean
    SecurityFilterChain localDevSecurityFilterChain(
            HttpSecurity http,
            EsalAccessDeniedHandler accessDeniedHandler,
            EsalAuthenticationEntryPoint authEntryPoint) throws Exception {

        http
            .cors().and()
            .csrf().disable()
            .headers()
                .frameOptions().deny()
                .contentTypeOptions().and()
                .referrerPolicy().and()
                .cacheControl().and()
            .and()
            .exceptionHandling()
                .authenticationEntryPoint(authEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            .and()
            .authorizeRequests()
                // Publicos
                .antMatchers(
                        "/actuator/health",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
                ).permitAll()
                // Auth
                .antMatchers("/api/auth/**").authenticated()
                // Solo ADMINISTRADOR — endpoints admin
                .antMatchers(HttpMethod.POST, "/api/admin/**").hasRole("ADMINISTRADOR")
                .antMatchers(HttpMethod.GET,  "/api/admin/**").hasRole("ADMINISTRADOR")
                .antMatchers(HttpMethod.PUT,  "/api/admin/**").hasRole("ADMINISTRADOR")
                // Solo ADMINISTRADOR — escritura ESAL
                .antMatchers(HttpMethod.POST, "/api/esales").hasRole("ADMINISTRADOR")
                .antMatchers(HttpMethod.POST, "/api/esales/mantenimiento").hasRole("ADMINISTRADOR")
                .antMatchers(HttpMethod.PUT,  "/api/esales/**").hasRole("ADMINISTRADOR")
                .antMatchers(HttpMethod.POST, "/api/esales/*/documentos").hasRole("ADMINISTRADOR")
                // ADMINISTRADOR o EXPEDIDOR — consultas ESAL
                .antMatchers(HttpMethod.GET, "/api/esales/**").hasAnyRole("ADMINISTRADOR", "EXPEDIDOR")
                // ADMINISTRADOR o EXPEDIDOR — certificados
                .antMatchers(HttpMethod.POST, "/api/certificados/**").hasAnyRole("ADMINISTRADOR", "EXPEDIDOR")
                .antMatchers(HttpMethod.GET,  "/api/certificados/**").hasAnyRole("ADMINISTRADOR", "EXPEDIDOR")
                .anyRequest().authenticated()
            .and()
            .httpBasic();

        return http.build();
    }

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
