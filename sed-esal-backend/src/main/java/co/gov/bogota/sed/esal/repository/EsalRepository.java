package co.gov.bogota.sed.esal.repository;

import co.gov.bogota.sed.esal.domain.Esal;
import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EsalRepository extends JpaRepository<Esal, Long>, JpaSpecificationExecutor<Esal> {

    Optional<Esal> findByIdSipej(String idSipej);

    Page<Esal> findByEstado(EstadoEsal estado, Pageable pageable);

    /**
     * Búsqueda paginada filtrando por nombre (LIKE) y estado.
     */
    @Query("SELECT e FROM Esal e WHERE " +
           "LOWER(e.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) AND " +
           "e.estado = :estado")
    Page<Esal> findByNombreContainingIgnoreCaseAndEstado(
            @Param("nombre") String nombre,
            @Param("estado") EstadoEsal estado,
            Pageable pageable);

    /**
     * Búsqueda paginada filtrando solo por nombre (LIKE).
     */
    @Query("SELECT e FROM Esal e WHERE " +
           "LOWER(e.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    Page<Esal> findByNombreContainingIgnoreCase(
            @Param("nombre") String nombre,
            Pageable pageable);
}
