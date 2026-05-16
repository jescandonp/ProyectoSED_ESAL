package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.Auditoria;
import co.gov.bogota.sed.esal.domain.Esal;
import co.gov.bogota.sed.esal.domain.PersoneriaJuridica;
import co.gov.bogota.sed.esal.domain.enums.EstadoCompletitud;
import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import co.gov.bogota.sed.esal.dto.BusquedaDetalleDto;
import co.gov.bogota.sed.esal.dto.BusquedaResultadoDto;
import co.gov.bogota.sed.esal.dto.PageDto;
import co.gov.bogota.sed.esal.repository.AuditoriaRepository;
import co.gov.bogota.sed.esal.repository.EsalRepository;
import co.gov.bogota.sed.esal.repository.PersoneriaJuridicaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests de integración para BusquedaService (I2).
 *
 * 1.  buscar_sinFiltros_devuelveTodos
 * 2.  buscar_porNombreExacto_encuentraEsal
 * 3.  buscar_porNombreParcial_encuentraVarios
 * 4.  buscar_porIdSipej_encuentraEsal
 * 5.  buscar_porNit_encuentraEsal
 * 6.  buscar_porEstado_filtraCorrectamente
 * 7.  buscar_porEstadoCompletitud_filtraCorrectamente
 * 8.  buscar_porQ_buscaEnVariasColunas
 * 9.  obtenerDetalle_retornaEsalCompleta
 * 10. obtenerDetalle_esalInexistente_lanza404
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BusquedaServiceTest {

    @Autowired
    private BusquedaService busquedaService;

    @Autowired
    private EsalRepository esalRepository;

    @Autowired
    private PersoneriaJuridicaRepository personeriaRepository;

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    // =========================================================================
    // 1. Sin filtros — devuelve todos
    // =========================================================================

    @Test
    void buscar_sinFiltros_devuelveTodos() {
        crearEsal("Fundacion Alpha", "SA-001", "900001001", EstadoEsal.ACTIVO);
        crearEsal("Corporacion Beta", "SA-002", "900001002", EstadoEsal.ACTIVO);

        PageDto<BusquedaResultadoDto> resultado = busquedaService.buscar(
                null, null, null, null, null, null, 0, 20, null);

        assertThat(resultado.getTotalElements()).isGreaterThanOrEqualTo(2);
    }

    // =========================================================================
    // 2. Por nombre exacto
    // =========================================================================

    @Test
    void buscar_porNombreExacto_encuentraEsal() {
        crearEsal("Asociacion Exacta Test", "SA-003", null, EstadoEsal.ACTIVO);

        PageDto<BusquedaResultadoDto> resultado = busquedaService.buscar(
                null, null, "Asociacion Exacta Test", null, null, null, 0, 20, null);

        assertThat(resultado.getContent())
                .extracting(BusquedaResultadoDto::getNombre)
                .contains("Asociacion Exacta Test");
    }

    // =========================================================================
    // 3. Por nombre parcial
    // =========================================================================

    @Test
    void buscar_porNombreParcial_encuentraVarios() {
        crearEsal("Fundacion Parcial Uno", "SA-010", null, EstadoEsal.ACTIVO);
        crearEsal("Fundacion Parcial Dos", "SA-011", null, EstadoEsal.ACTIVO);

        PageDto<BusquedaResultadoDto> resultado = busquedaService.buscar(
                null, null, "Parcial", null, null, null, 0, 20, null);

        assertThat(resultado.getContent()).hasSizeGreaterThanOrEqualTo(2);
    }

    // =========================================================================
    // 4. Por idSipej
    // =========================================================================

    @Test
    void buscar_porIdSipej_encuentraEsal() {
        crearEsal("Fundacion SIPEJ Test", "SIPEJ-999", null, EstadoEsal.ACTIVO);

        PageDto<BusquedaResultadoDto> resultado = busquedaService.buscar(
                null, "SIPEJ-999", null, null, null, null, 0, 20, null);

        assertThat(resultado.getContent())
                .extracting(BusquedaResultadoDto::getIdSipej)
                .contains("SIPEJ-999");
    }

    // =========================================================================
    // 5. Por NIT
    // =========================================================================

    @Test
    void buscar_porNit_encuentraEsal() {
        crearEsal("Fundacion NIT Test", "SA-020", "800555777", EstadoEsal.ACTIVO);

        PageDto<BusquedaResultadoDto> resultado = busquedaService.buscar(
                null, null, null, "800555777", null, null, 0, 20, null);

        assertThat(resultado.getContent())
                .extracting(BusquedaResultadoDto::getNit)
                .contains("800555777");
    }

    // =========================================================================
    // 6. Por estado
    // =========================================================================

    @Test
    void buscar_porEstado_filtraCorrectamente() {
        crearEsal("Fundacion Suspendida X", "SA-030", null, EstadoEsal.SUSPENDIDO);
        crearEsal("Fundacion Activa Y", "SA-031", null, EstadoEsal.ACTIVO);

        PageDto<BusquedaResultadoDto> suspendidas = busquedaService.buscar(
                null, null, null, null, EstadoEsal.SUSPENDIDO, null, 0, 50, null);

        assertThat(suspendidas.getContent())
                .allMatch(dto -> EstadoEsal.SUSPENDIDO.equals(dto.getEstado()));
        assertThat(suspendidas.getContent())
                .extracting(BusquedaResultadoDto::getIdSipej)
                .contains("SA-030");
    }

    // =========================================================================
    // 7. Por estadoCompletitud
    // =========================================================================

    @Test
    void buscar_porEstadoCompletitud_filtraCorrectamente() {
        Esal esal = crearEsal("Fundacion Completa Z", "SA-040", null, EstadoEsal.ACTIVO);
        esal.setEstadoCompletitud(EstadoCompletitud.LISTO_PARA_CERTIFICAR);
        esalRepository.save(esal);

        PageDto<BusquedaResultadoDto> resultado = busquedaService.buscar(
                null, null, null, null, null,
                EstadoCompletitud.LISTO_PARA_CERTIFICAR, 0, 50, null);

        assertThat(resultado.getContent())
                .allMatch(dto -> EstadoCompletitud.LISTO_PARA_CERTIFICAR.equals(dto.getEstadoCompletitud()));
        assertThat(resultado.getContent())
                .extracting(BusquedaResultadoDto::getIdSipej)
                .contains("SA-040");
    }

    // =========================================================================
    // 8. Q — busca en nombre, idSipej y nit
    // =========================================================================

    @Test
    void buscar_porQ_buscaEnVariasColumnas() {
        crearEsal("Fundacion Omega", "SA-050", null, EstadoEsal.ACTIVO);

        PageDto<BusquedaResultadoDto> resultado = busquedaService.buscar(
                "omega", null, null, null, null, null, 0, 20, null);

        assertThat(resultado.getContent())
                .extracting(BusquedaResultadoDto::getNombre)
                .anyMatch(n -> n.toLowerCase().contains("omega"));
    }

    // =========================================================================
    // 9. Detalle completo — con personería
    // =========================================================================

    @Test
    void obtenerDetalle_retornaEsalCompleta() {
        Esal esal = crearEsal("Fundacion Detalle Full", "SA-060", "900123456", EstadoEsal.ACTIVO);
        esal.setDomicilio("Bogotá");
        esal.setCorreoElectronico("info@detalle.org");
        esalRepository.save(esal);

        PersoneriaJuridica pj = new PersoneriaJuridica();
        pj.setEsalId(esal.getId());
        pj.setReconocimientoPersoneriaJuridica("Resolución 001-2020");
        pj.setFechaReconocimientoPersoneriaJuridica(LocalDate.of(2020, 1, 15));
        pj.setEntidadQueExpide("Secretaría de Educación");
        personeriaRepository.save(pj);

        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);
        BusquedaDetalleDto detalle = busquedaService.obtenerDetalle(esal.getId(), null);

        assertThat(detalle.getEsalId()).isEqualTo(esal.getId());
        assertThat(detalle.getNombre()).isEqualTo("Fundacion Detalle Full");
        assertThat(detalle.getIdSipej()).isEqualTo("SA-060");
        assertThat(detalle.getPersoneria()).isNotNull();
        assertThat(detalle.getPersoneria().getReconocimiento()).isEqualTo("Resolución 001-2020");
        assertThat(detalle.getCompletitud()).isNotNull();

        // Verifica auditoría
        List<Auditoria> auds = auditoriaRepository.findByAccion(AuditoriaAcciones.DETALLE_ESAL_CONSULTADO);
        assertThat(auds).anyMatch(a ->
                esal.getId().equals(a.getEntidadId()) && a.getCreatedAt().isAfter(antes));
    }

    // =========================================================================
    // 10. Detalle — ESAL inexistente lanza 404
    // =========================================================================

    @Test
    void obtenerDetalle_esalInexistente_lanza404() {
        assertThatThrownBy(() -> busquedaService.obtenerDetalle(999999L, null))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404");
    }

    // =========================================================================
    // Helper
    // =========================================================================

    private Esal crearEsal(String nombre, String idSipej, String nit, EstadoEsal estado) {
        Esal esal = new Esal();
        esal.setNombre(nombre);
        esal.setIdSipej(idSipej);
        esal.setNit(nit);
        esal.setEstado(estado);
        esal.setEstadoCompletitud(EstadoCompletitud.INCOMPLETO_BLOQUEANTE);
        esal.setCreatedAt(LocalDateTime.now());
        esal.setUpdatedAt(LocalDateTime.now());
        esal.setCreatedBy("test");
        return esalRepository.save(esal);
    }
}
