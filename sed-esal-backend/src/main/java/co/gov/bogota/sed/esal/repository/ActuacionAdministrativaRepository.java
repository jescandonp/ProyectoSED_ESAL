package co.gov.bogota.sed.esal.repository;

import co.gov.bogota.sed.esal.domain.ActuacionAdministrativa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActuacionAdministrativaRepository extends JpaRepository<ActuacionAdministrativa, Long> {

    List<ActuacionAdministrativa> findByEsalId(Long esalId);
}
