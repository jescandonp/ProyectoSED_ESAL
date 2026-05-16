package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.Firmante;
import co.gov.bogota.sed.esal.dto.FirmanteCreateDto;
import co.gov.bogota.sed.esal.dto.FirmanteDto;
import co.gov.bogota.sed.esal.repository.FirmanteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests de integración para FirmanteService (I3).
 *
 * 1.  crear_firmanteValido_persiste
 * 2.  crear_sinNombre_lanza400
 * 3.  crear_finAntesDeinicio_lanza400
 * 4.  crear_vigenciasSolapadas_lanza409
 * 5.  activar_firmanteInactivo_activa
 * 6.  inactivar_firmanteActivo_inactiva
 * 7.  resolverVigente_sinFirmante_lanza422
 * 8.  resolverVigente_firmanteVigente_retorna
 * 9.  resolverVigente_dosFirmantesVigentes_lanza422
 * 10. listar_retornaOrdenPorFechaDesc
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FirmanteServiceTest {

    @Autowired
    private FirmanteService firmanteService;

    @Autowired
    private FirmanteRepository firmanteRepository;

    @Test
    void crear_firmanteValido_persiste() {
        FirmanteDto dto = firmanteService.crear(buildDto("Juan Perez", "Director", LocalDate.of(2026, 1, 1), null));
        assertThat(dto.getId()).isNotNull();
        assertThat(dto.getNombre()).isEqualTo("Juan Perez");
        assertThat(dto.getActivo()).isTrue();
    }

    @Test
    void crear_sinNombre_lanza400() {
        FirmanteCreateDto dto = buildDto(null, "Director", LocalDate.of(2026, 1, 1), null);
        assertThatThrownBy(() -> firmanteService.crear(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("400");
    }

    @Test
    void crear_finAntesDeInicio_lanza400() {
        FirmanteCreateDto dto = buildDto("Ana", "Jefe", LocalDate.of(2026, 6, 1), LocalDate.of(2026, 1, 1));
        assertThatThrownBy(() -> firmanteService.crear(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("400");
    }

    @Test
    void crear_vigenciasSolapadas_lanza409() {
        firmanteService.crear(buildDto("Primero", "Cargo", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31)));
        FirmanteCreateDto solapado = buildDto("Segundo", "Cargo", LocalDate.of(2026, 6, 1), null);
        assertThatThrownBy(() -> firmanteService.crear(solapado))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("409");
    }

    @Test
    void inactivar_firmanteActivo_inactiva() {
        FirmanteDto creado = firmanteService.crear(buildDto("Maria", "Jefe", LocalDate.of(2026, 1, 1), null));
        FirmanteDto inactivado = firmanteService.inactivar(creado.getId());
        assertThat(inactivado.getActivo()).isFalse();
    }

    @Test
    void activar_firmanteInactivo_activa() {
        FirmanteDto creado = firmanteService.crear(buildDto("Carlos", "Jefe", LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31)));
        firmanteService.inactivar(creado.getId());
        FirmanteDto activado = firmanteService.activar(creado.getId());
        assertThat(activado.getActivo()).isTrue();
    }

    @Test
    void resolverVigente_sinFirmante_lanza422() {
        assertThatThrownBy(() -> firmanteService.resolverFirmanteVigente(LocalDate.of(2026, 6, 1)))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("422");
    }

    @Test
    void resolverVigente_firmanteVigente_retornaFirmante() {
        firmanteService.crear(buildDto("Ana Gomez", "Directora", LocalDate.of(2026, 1, 1), null));
        Firmante f = firmanteService.resolverFirmanteVigente(LocalDate.of(2026, 6, 1));
        assertThat(f.getNombre()).isEqualTo("Ana Gomez");
    }

    @Test
    void resolverVigente_dosFirmantesActivos_lanza422() {
        // Insertamos directamente en repo para simular estado inválido (dos activos solapados)
        Firmante f1 = new Firmante();
        f1.setNombre("Primero"); f1.setCargo("C");
        f1.setFechaInicioVigencia(LocalDate.of(2026, 1, 1));
        f1.setActivo(true);
        f1.setCreatedAt(java.time.LocalDateTime.now());
        firmanteRepository.save(f1);

        Firmante f2 = new Firmante();
        f2.setNombre("Segundo"); f2.setCargo("C");
        f2.setFechaInicioVigencia(LocalDate.of(2026, 6, 1));
        f2.setActivo(true);
        f2.setCreatedAt(java.time.LocalDateTime.now());
        firmanteRepository.save(f2);

        assertThatThrownBy(() -> firmanteService.resolverFirmanteVigente(LocalDate.of(2026, 7, 1)))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("422");
    }

    @Test
    void listar_retornaFirmantesOrdenados() {
        firmanteService.crear(buildDto("A", "C", LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31)));
        firmanteService.crear(buildDto("B", "C", LocalDate.of(2026, 1, 1), null));
        List<FirmanteDto> lista = firmanteService.listar();
        assertThat(lista).hasSizeGreaterThanOrEqualTo(2);
        // más reciente primero
        assertThat(lista.get(0).getFechaInicioVigencia())
                .isAfterOrEqualTo(lista.get(1).getFechaInicioVigencia());
    }

    // -------------------------------------------------------------------------

    private FirmanteCreateDto buildDto(String nombre, String cargo, LocalDate inicio, LocalDate fin) {
        FirmanteCreateDto dto = new FirmanteCreateDto();
        dto.setNombre(nombre);
        dto.setCargo(cargo);
        dto.setFechaInicioVigencia(inicio);
        dto.setFechaFinVigencia(fin);
        return dto;
    }
}
