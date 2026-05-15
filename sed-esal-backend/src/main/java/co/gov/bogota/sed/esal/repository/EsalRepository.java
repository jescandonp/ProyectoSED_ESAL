package co.gov.bogota.sed.esal.repository;

import co.gov.bogota.sed.esal.domain.Esal;
import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EsalRepository extends JpaRepository<Esal, Long> {

    Optional<Esal> findByIdSipej(String idSipej);

    Page<Esal> findByEstado(EstadoEsal estado, Pageable pageable);
}
