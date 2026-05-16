package co.gov.bogota.sed.esal.repository;

import co.gov.bogota.sed.esal.domain.Certificado;
import co.gov.bogota.sed.esal.domain.enums.EstadoCertificado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CertificadoRepository extends JpaRepository<Certificado, Long> {

    List<Certificado> findByEsalIdOrderByCreatedAtDesc(Long esalId);

    Optional<Certificado> findByNumeroCertificado(String numeroCertificado);

    List<Certificado> findByEsalIdAndEstadoCertificado(Long esalId, EstadoCertificado estado);
}
