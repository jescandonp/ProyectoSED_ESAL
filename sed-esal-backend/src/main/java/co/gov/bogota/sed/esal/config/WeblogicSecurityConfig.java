package co.gov.bogota.sed.esal.config;

import co.gov.bogota.sed.esal.config.security.EsalAccessDeniedHandler;
import co.gov.bogota.sed.esal.config.security.EsalAuthenticationEntryPoint;
import co.gov.bogota.sed.esal.config.security.JwtRolConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Configuración de seguridad para el perfil weblogic (I4).
 * Usa Bearer JWT emitido por Azure AD / Office 365.
 * Las coordenadas institucionales (tenant, issuer, audience, JWKS, CORS)
 * se configuran via variables de entorno — ver application-weblogic.yml.
 */
@Configuration
@Profile("weblogic")
public class WeblogicSecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Value("${esal.security.jwt.issuer-uri}")
    private String issuerUri;

    @Value("${esal.security.jwt.audience}")
    private String audience;

    @Value("${esal.security.cors-origins:https://DOMINIO_INSTITUCIONAL_PENDIENTE}")
    private String corsOrigins;

    @Bean
    SecurityFilterChain weblogicSecurityFilterChain(
            HttpSecurity http,
            JwtDecoder jwtDecoder,
            JwtRolConverter jwtRolConverter,
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
                .antMatchers("/actuator/health").permitAll()
                // Swagger — restringido en institucional segun politica SED
                .antMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").hasRole("ADMINISTRADOR")
                // Auth
                .antMatchers("/api/auth/**").authenticated()
                // Solo ADMINISTRADOR — endpoints admin
                .antMatchers(HttpMethod.POST, "/api/admin/**").hasRole("ADMINISTRADOR")
                .antMatchers(HttpMethod.GET,  "/api/admin/**").hasRole("ADMINISTRADOR")
                .antMatchers(HttpMethod.PUT,  "/api/admin/**").hasRole("ADMINISTRADOR")
                // Solo ADMINISTRADOR — escritura ESAL
                .antMatchers(HttpMethod.POST, "/api/esales").hasRole("ADMINISTRADOR")
                .antMatchers(HttpMethod.POST, "/api/esales/mantenimiento").hasRole("ADMINISTRADOR")
                .antMatchers(HttpMethod.POST, "/api/esales/*/representantes").hasRole("ADMINISTRADOR")
                .antMatchers(HttpMethod.POST, "/api/esales/*/organos-administracion").hasRole("ADMINISTRADOR")
                .antMatchers(HttpMethod.PUT,  "/api/esales/**").hasRole("ADMINISTRADOR")
                .antMatchers(HttpMethod.POST, "/api/esales/*/documentos").hasRole("ADMINISTRADOR")
                // ADMINISTRADOR o EXPEDIDOR — consultas ESAL
                .antMatchers(HttpMethod.GET, "/api/esales/**").hasAnyRole("ADMINISTRADOR", "EXPEDIDOR")
                // ADMINISTRADOR o EXPEDIDOR — certificados
                .antMatchers(HttpMethod.POST, "/api/certificados/**").hasAnyRole("ADMINISTRADOR", "EXPEDIDOR")
                .antMatchers(HttpMethod.GET,  "/api/certificados/**").hasAnyRole("ADMINISTRADOR", "EXPEDIDOR")
                .anyRequest().authenticated()
            .and()
            .oauth2ResourceServer()
                .jwt()
                .decoder(jwtDecoder)
                .jwtAuthenticationConverter(jwtRolConverter);

        return http.build();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(this.jwkSetUri).build();

        String aud = this.audience;
        List<OAuth2TokenValidator<Jwt>> validators = new ArrayList<>();
        validators.add(new JwtTimestampValidator());
        validators.add(new JwtIssuerValidator(issuerUri));
        validators.add(new JwtClaimValidator<List<String>>(JwtClaimNames.AUD,
                audClaim -> audClaim != null && audClaim.contains(aud)));

        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(validators));
        return decoder;
    }

    @Bean
    CorsConfigurationSource weblogicCorsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(corsOrigins.split(",")));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
