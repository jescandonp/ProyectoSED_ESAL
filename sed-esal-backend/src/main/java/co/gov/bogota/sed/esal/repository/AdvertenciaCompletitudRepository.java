package co.gov.bogota.sed.esal.repository;

import co.gov.bogota.sed.esal.domain.AdvertenciaCompletitud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdvertenciaCompletitudRepository extends JpaRepository<AdvertenciaCompletitud, Long> {

    List<AdvertenciaCompletitud> findByEsalId(Long esalId);

    List<AdvertenciaCompletitud> findByEsalIdAndBloqueante(Long esalId, Boolean bloqueante);
}
