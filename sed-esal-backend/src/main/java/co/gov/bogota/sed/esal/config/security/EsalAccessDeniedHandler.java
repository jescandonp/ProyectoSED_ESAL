package co.gov.bogota.sed.esal.config.security;

import co.gov.bogota.sed.esal.service.AuditoriaAcciones;
import co.gov.bogota.sed.esal.service.AuditoriaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Maneja accesos denegados (403): responde JSON estandar y registra auditoria.
 */
@Component
public class EsalAccessDeniedHandler implements AccessDeniedHandler {

    private final AuditoriaService auditoriaService;
    private final ObjectMapper objectMapper;

    public EsalAccessDeniedHandler(AuditoriaService auditoriaService, ObjectMapper objectMapper) {
        this.auditoriaService = auditoriaService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usuario = auth != null ? auth.getName() : "anonimo";
        String rol = auth != null ? auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(a -> a.startsWith("ROLE_") ? a.substring(5) : a)
                .findFirst().orElse(null) : null;

        String recurso = request.getMethod() + " " + request.getRequestURI();

        try {
            auditoriaService.registrar(
                    usuario, rol,
                    AuditoriaAcciones.ACCESO_DENEGADO,
                    AuditoriaAcciones.ENTIDAD_SEGURIDAD,
                    null, null,
                    AuditoriaAcciones.RESULTADO_ERROR,
                    "403 Forbidden: " + recurso
            );
        } catch (Exception ignored) {
            // No bloquear la respuesta si falla la auditoria
        }

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", 403);
        body.put("message", "Acceso denegado");
        body.put("timestamp", LocalDateTime.now().toString());

        objectMapper.writeValue(response.getWriter(), body);
    }
}
