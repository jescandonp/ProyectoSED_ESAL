package co.gov.bogota.sed.esal.repository;

import co.gov.bogota.sed.esal.domain.DocumentoSoporte;
import co.gov.bogota.sed.esal.domain.enums.SubtipoDocumentoSoporte;
import co.gov.bogota.sed.esal.domain.enums.TipoDocumentoSoporte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentoSoporteRepository extends JpaRepository<DocumentoSoporte, Long> {

    List<DocumentoSoporte> findByEsalId(Long esalId);

    Optional<DocumentoSoporte> findFirstByEsalIdAndTipoDocumentalAndSubtipoDocumentalAndVigenteTrue(
            Long esalId,
            TipoDocumentoSoporte tipoDocumental,
            SubtipoDocumentoSoporte subtipoDocumental);

    List<DocumentoSoporte> findByEsalIdOrderByVigenteDescCreatedAtDesc(Long esalId);
}
