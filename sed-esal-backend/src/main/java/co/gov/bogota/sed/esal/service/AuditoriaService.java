package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.Auditoria;
import co.gov.bogota.sed.esal.repository.AuditoriaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaService(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(String usuario, String rol,
                          String accion, String entidad,
                          Long entidadId, String idSipej,
                          String resultado, String detalle) {
        Auditoria a = new Auditoria();
        a.setUsuario(usuario != null ? usuario : "sistema");
        a.setRol(rol);
        a.setAccion(accion);
        a.setEntidad(entidad);
        a.setEntidadId(entidadId);
        a.setIdSipej(idSipej);
        a.setResultado(resultado);
        a.setDetalle(detalle);
        a.setCreatedAt(LocalDateTime.now());
        auditoriaRepository.save(a);
    }

    public String obtenerRolActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(r -> r.startsWith("ROLE_") ? r.substring(5) : r)
                .findFirst()
                .orElse(null);
    }
}
