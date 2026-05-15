package co.gov.bogota.sed.esal.repository;

import co.gov.bogota.sed.esal.domain.CampoObligatoriedad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CampoObligatoriedadRepository extends JpaRepository<CampoObligatoriedad, Long> {

    List<CampoObligatoriedad> findByObligatorio(Boolean obligatorio);

    List<CampoObligatoriedad> findBySeccion(String seccion);
}
