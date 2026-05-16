package co.gov.bogota.sed.esal.repository;

import co.gov.bogota.sed.esal.domain.NumeracionCertificado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NumeracionCertificadoRepository extends JpaRepository<NumeracionCertificado, Long> {
    Optional<NumeracionCertificado> findByAnioAndActivoTrue(Integer anio);
}
