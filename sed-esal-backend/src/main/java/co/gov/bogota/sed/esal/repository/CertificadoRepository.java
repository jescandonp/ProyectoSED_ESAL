package co.gov.bogota.sed.esal.repository;

import co.gov.bogota.sed.esal.domain.Certificado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CertificadoRepository extends JpaRepository<Certificado, Long> {
    List<Certificado> findByEsalIdOrderByCreatedAtDesc(Long esalId);
}
