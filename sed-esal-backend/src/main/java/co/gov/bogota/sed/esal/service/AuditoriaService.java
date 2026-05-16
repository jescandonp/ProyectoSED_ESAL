package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.Auditoria;
import co.gov.bogota.sed.esal.repository.AuditoriaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Servicio centralizado para registrar eventos de auditoría.
 *
 * Cada operación relevante del sistema (crear, editar, cambiar estado,
 * importar, registrar documento, consultar) debe llamar a
 * {@link #registrar(String, String, String, String, Long, String, String, String)}
 * para dejar trazabilidad en la tabla {@code ESAL_AUDITORIA}.
 *
 * El método usa {@code REQUIRES_NEW} para que el registro de auditoría
 * se persista incluso si la transacción principal hace rollback.
 */
@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaService(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    /**
     * Registra un evento de auditoría.
     *
     * @param usuario   email del usuario que ejecuta la acción (null → "sistema")
     * @param rol       rol del usuario (ADMINISTRADOR, EXPEDIDOR, sistema)
     * @param accion    descripción de la acción (usar constantes de {@link AuditoriaAcciones})
     * @param entidad   nombre de la entidad afectada (ESAL, IMPORTACION, DOCUMENTO, etc.)
     * @param entidadId ID de la entidad afectada (puede ser null)
     * @param idSipej   ID SIPEJ de la ESAL afectada (puede ser null)
     * @param resultado EXITO o ERROR (usar constantes de {@link AuditoriaAcciones})
     * @param detalle   detalle adicional (puede ser null; se trunca a 2000 caracteres)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(String usuario, String rol, String accion,
                          String entidad, Long entidadId, String idSipej,
                          String resultado, String detalle) {
        Auditoria auditoria = new Auditoria();
        auditoria.setUsuario(usuario != null ? usuario : "sistema");
        auditoria.setRol(rol);
        auditoria.setAccion(accion);
        auditoria.setEntidad(entidad);
        auditoria.setEntidadId(entidadId);
        auditoria.setIdSipej(idSipej);
        auditoria.setResultado(resultado);
        auditoria.setDetalle(
                detalle != null && detalle.length() > 2000
                        ? detalle.substring(0, 2000)
                        : detalle);
        auditoria.setCreatedAt(LocalDateTime.now());
        auditoriaRepository.save(auditoria);
    }

    /**
     * Obtiene el rol del usuario autenticado en el contexto de seguridad actual.
     * Retorna "sistema" si no hay autenticación activa.
     *
     * @return nombre del rol sin el prefijo "ROLE_"
     */
    public String obtenerRolActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return "sistema";
        return auth.getAuthorities().stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .findFirst()
                .orElse("sistema");
    }
}
