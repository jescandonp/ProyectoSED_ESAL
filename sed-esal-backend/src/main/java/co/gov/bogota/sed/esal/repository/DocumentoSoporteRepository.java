package co.gov.bogota.sed.esal.repository;

import co.gov.bogota.sed.esal.domain.DocumentoSoporte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentoSoporteRepository extends JpaRepository<DocumentoSoporte, Long> {

    List<DocumentoSoporte> findByEsalId(Long esalId);
}
