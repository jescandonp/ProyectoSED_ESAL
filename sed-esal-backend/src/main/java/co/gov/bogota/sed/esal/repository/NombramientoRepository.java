package co.gov.bogota.sed.esal.repository;

import co.gov.bogota.sed.esal.domain.Nombramiento;
import co.gov.bogota.sed.esal.domain.enums.TipoNombramiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NombramientoRepository extends JpaRepository<Nombramiento, Long> {

    List<Nombramiento> findByEsalId(Long esalId);

    List<Nombramiento> findByEsalIdAndTipoNombramiento(Long esalId, TipoNombramiento tipo);
}
