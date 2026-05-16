package co.gov.bogota.sed.esal.repository;

import co.gov.bogota.sed.esal.domain.Firmante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FirmanteRepository extends JpaRepository<Firmante, Long> {

    List<Firmante> findAllByOrderByCreatedAtDesc();

    @Query("SELECT f FROM Firmante f WHERE f.activo = true " +
           "AND f.fechaInicioVigencia <= :fecha " +
           "AND (f.fechaFinVigencia IS NULL OR f.fechaFinVigencia >= :fecha)")
    List<Firmante> findVigentesEnFecha(@Param("fecha") LocalDate fecha);

    @Query("SELECT f FROM Firmante f WHERE f.activo = true " +
           "AND f.id <> :excludeId " +
           "AND f.fechaInicioVigencia <= :fin " +
           "AND (f.fechaFinVigencia IS NULL OR f.fechaFinVigencia >= :inicio)")
    List<Firmante> findSolapados(@Param("inicio") LocalDate inicio,
                                  @Param("fin") LocalDate fin,
                                  @Param("excludeId") Long excludeId);

    @Query("SELECT f FROM Firmante f WHERE f.activo = true " +
           "AND f.fechaInicioVigencia <= :fin " +
           "AND (f.fechaFinVigencia IS NULL OR f.fechaFinVigencia >= :inicio)")
    List<Firmante> findSolapadosNew(@Param("inicio") LocalDate inicio,
                                     @Param("fin") LocalDate fin);
}
