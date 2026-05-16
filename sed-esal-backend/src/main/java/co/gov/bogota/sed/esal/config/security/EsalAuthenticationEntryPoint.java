package co.gov.bogota.sed.esal.config.security;

import co.gov.bogota.sed.esal.service.AuditoriaAcciones;
import co.gov.bogota.sed.esal.service.AuditoriaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Maneja peticiones sin autenticacion (401): responde JSON estandar y registra auditoria.
 */
@Component
public class EsalAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final AuditoriaService auditoriaService;
    private final ObjectMapper objectMapper;

    public EsalAuthenticationEntryPoint(AuditoriaService auditoriaService, ObjectMapper objectMapper) {
        this.auditoriaService = auditoriaService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException ex) throws IOException {

        String recurso = request.getMethod() + " " + request.getRequestURI();
        String motivo = ex != null ? ex.getClass().getSimpleName() : "Sin autenticacion";

        try {
            auditoriaService.registrar(
                    "anonimo", null,
                    AuditoriaAcciones.TOKEN_INVALIDO_O_AUSENTE,
                    AuditoriaAcciones.ENTIDAD_SEGURIDAD,
                    null, null,
                    AuditoriaAcciones.RESULTADO_ERROR,
                    "401 Unauthorized: " + recurso + " | " + motivo
            );
        } catch (Exception ignored) {
            // No bloquear la respuesta si falla la auditoria
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", 401);
        body.put("message", "No autenticado");
        body.put("timestamp", LocalDateTime.now().toString());

        objectMapper.writeValue(response.getWriter(), body);
    }
}
