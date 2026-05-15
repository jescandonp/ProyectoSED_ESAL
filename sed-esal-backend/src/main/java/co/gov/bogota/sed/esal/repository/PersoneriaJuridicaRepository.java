package co.gov.bogota.sed.esal.repository;

import co.gov.bogota.sed.esal.domain.PersoneriaJuridica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersoneriaJuridicaRepository extends JpaRepository<PersoneriaJuridica, Long> {

    List<PersoneriaJuridica> findByEsalId(Long esalId);
}
