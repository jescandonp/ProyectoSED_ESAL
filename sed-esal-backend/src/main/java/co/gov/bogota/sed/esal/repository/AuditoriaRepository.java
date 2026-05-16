package co.gov.bogota.sed.esal.repository;

import co.gov.bogota.sed.esal.domain.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    List<Auditoria> findByUsuario(String usuario);

    List<Auditoria> findByEntidadAndEntidadId(String entidad, Long entidadId);

    List<Auditoria> findByAccion(String accion);
}
