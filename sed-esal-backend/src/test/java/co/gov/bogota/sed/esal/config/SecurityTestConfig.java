package co.gov.bogota.sed.esal.config;

import co.gov.bogota.sed.esal.config.security.EsalAccessDeniedHandler;
import co.gov.bogota.sed.esal.config.security.EsalAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuracion de seguridad para perfil "test".
 * Aplica las mismas reglas de autorizacion que local-dev
 * para permitir tests de autorizacion con @WithMockUser.
 */
@Configuration
@Profile("test")
public class SecurityTestConfig {

    @Bean
    SecurityFilterChain testSecurityFilterChain(
            HttpSecurity http,
            EsalAccessDeniedHandler accessDeniedHandler,
            EsalAuthenticationEntryPoint authEntryPoint) throws Exception {

        http
            .csrf().disable()
            .exceptionHandling()
                .authenticationEntryPoint(authEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            .and()
            .authorizeRequests()
                .antMatchers("/actuator/health", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                .antMatchers("/api/auth/**").authenticated()
                .antMatchers(HttpMethod.POST, "/api/admin/**").hasRole("ADMINISTRADOR")
                .antMatchers(HttpMethod.GET,  "/api/admin/**").hasRole("ADMINISTRADOR")
                .antMatchers(HttpMethod.PUT,  "/api/admin/**").hasRole("ADMINISTRADOR")
                .antMatchers(HttpMethod.POST, "/api/esales").hasRole("ADMINISTRADOR")
                .antMatchers(HttpMethod.PUT,  "/api/esales/**").hasRole("ADMINISTRADOR")
                .antMatchers(HttpMethod.GET,  "/api/esales/**").hasAnyRole("ADMINISTRADOR", "EXPEDIDOR")
                .antMatchers(HttpMethod.POST, "/api/certificados/**").hasAnyRole("ADMINISTRADOR", "EXPEDIDOR")
                .antMatchers(HttpMethod.GET,  "/api/certificados/**").hasAnyRole("ADMINISTRADOR", "EXPEDIDOR")
                .anyRequest().authenticated();

        return http.build();
    }
}
