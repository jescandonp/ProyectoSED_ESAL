package co.gov.bogota.sed.esal.repository;

import co.gov.bogota.sed.esal.domain.OrganoAdministracion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrganoAdministracionRepository extends JpaRepository<OrganoAdministracion, Long> {

    List<OrganoAdministracion> findByEsalId(Long esalId);
}
