package co.gov.bogota.sed.esal.repository;

import co.gov.bogota.sed.esal.domain.ReformaEstatutaria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReformaEstatutariaRepository extends JpaRepository<ReformaEstatutaria, Long> {

    List<ReformaEstatutaria> findByEsalIdOrderByOrden(Long esalId);
}
