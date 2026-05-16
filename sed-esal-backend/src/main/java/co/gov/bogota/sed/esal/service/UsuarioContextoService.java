package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.dto.PermisosDto;
import co.gov.bogota.sed.esal.dto.UsuarioContextoDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Resuelve el usuario autenticado desde cualquier tipo de Authentication:
 * HTTP Basic (local-dev) o JWT Azure AD (weblogic).
 */
@Service
public class UsuarioContextoService {

    public UsuarioContextoDto resolver(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }

        String usuario = authentication.getName();
        String email = usuario;
        String nombre = usuario;
        String rol = resolverRol(authentication);

        if (authentication instanceof JwtAuthenticationToken) {
            Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
            String emailClaim = jwt.getClaimAsString("email");
            if (emailClaim != null) email = emailClaim;
            String preferredUsername = jwt.getClaimAsString("preferred_username");
            if (preferredUsername != null && email.equals(usuario)) email = preferredUsername;
            String nameClaim = jwt.getClaimAsString("name");
            if (nameClaim != null) nombre = nameClaim;
        }

        return new UsuarioContextoDto(usuario, email, nombre, rol);
    }

    public PermisosDto resolverPermisos(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
        String rol = resolverRol(authentication);
        if ("ADMINISTRADOR".equals(rol)) return PermisosDto.paraAdministrador();
        if ("EXPEDIDOR".equals(rol)) return PermisosDto.paraExpedidor();
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Rol no reconocido");
    }

    private String resolverRol(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(a -> a.startsWith("ROLE_") ? a.substring(5) : a)
                .findFirst()
                .orElse(null);
    }
}
