package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.Firmante;
import co.gov.bogota.sed.esal.dto.FirmanteCreateDto;
import co.gov.bogota.sed.esal.dto.FirmanteDto;
import co.gov.bogota.sed.esal.repository.FirmanteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FirmanteServiceTest {

    @Autowired
    private FirmanteService firmanteService;

    @Autowired
    private FirmanteRepository firmanteRepository;

    private FirmanteCreateDto dto(String nombre, LocalDate inicio, LocalDate fin) {
        FirmanteCreateDto d = new FirmanteCreateDto();
        d.setNombre(nombre);
        d.setCargo("Director");
        d.setFechaInicioVigencia(inicio);
        d.setFechaFinVigencia(fin);
        return d;
    }

    @Test
    void crear_firmanteValido_persisteYRetorna() {
        FirmanteDto result = firmanteService.crear(
                dto("Juan Perez", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31)),
                "admin");
        assertThat(result.getId()).isNotNull();
        assertThat(result.getNombre()).isEqualTo("Juan Perez");
        assertThat(result.isActivo()).isTrue();
    }

    @Test
    void crear_conSolapamiento_lanzaConflict() {
        firmanteService.crear(dto("Juan Perez", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31)), "admin");
        assertThatThrownBy(() -> firmanteService.crear(
                dto("Maria Lopez", LocalDate.of(2026, 6, 1), LocalDate.of(2027, 6, 1)),
                "admin"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatus())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void resolverVigente_sinFirmante_lanzaError() {
        assertThatThrownBy(() -> firmanteService.resolverVigente(LocalDate.now()))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatus())
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    void resolverVigente_conFirmanteActivo_retorna() {
        firmanteService.crear(dto("Ana Garcia", LocalDate.of(2020, 1, 1), null), "admin");
        Firmante f = firmanteService.resolverVigente(LocalDate.now());
        assertThat(f.getNombre()).isEqualTo("Ana Garcia");
    }

    @Test
    void inactivar_firmanteActivo_quedaInactivo() {
        FirmanteDto creado = firmanteService.crear(dto("Carlos Ruiz", LocalDate.of(2026, 1, 1), null), "admin");
        FirmanteDto inact = firmanteService.inactivar(creado.getId(), "admin");
        assertThat(inact.isActivo()).isFalse();
    }
}
