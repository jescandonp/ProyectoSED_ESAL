package co.gov.bogota.sed.esal.config.security;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Stub preparado para I4 - Seguridad institucional con Azure AD JWT.
 *
 * En I1 (local-dev) este filtro no se activa: {@link #shouldNotFilter}
 * devuelve {@code true} para todas las peticiones.
 *
 * En I4 se implementará:
 * - Extracción del token Bearer del header Authorization.
 * - Validación del JWT contra Azure AD / Office 365 (JWKS endpoint).
 * - Construcción del {@link org.springframework.security.core.Authentication}
 *   con los roles mapeados desde los claims del token.
 * - Registro en el SecurityContext para que las reglas de autorización
 *   definidas en la cadena de filtros puedan evaluarse correctamente.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        // I4: implementar validación JWT Azure AD aquí
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // En local-dev este filtro no aplica; se activará en el perfil weblogic (I4)
        return true;
    }
}
