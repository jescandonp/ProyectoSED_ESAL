package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.Auditoria;
import co.gov.bogota.sed.esal.domain.Esal;
import co.gov.bogota.sed.esal.domain.Nombramiento;
import co.gov.bogota.sed.esal.domain.PersoneriaJuridica;
import co.gov.bogota.sed.esal.domain.enums.EstadoCompletitud;
import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import co.gov.bogota.sed.esal.domain.enums.TipoNombramiento;
import co.gov.bogota.sed.esal.dto.PreviewCertificadoDto;
import co.gov.bogota.sed.esal.repository.AuditoriaRepository;
import co.gov.bogota.sed.esal.repository.EsalRepository;
import co.gov.bogota.sed.esal.repository.NombramientoRepository;
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
 * Tests de integración para PreviewService (I2).
 *
 * 1. preview_esalActiva_generacionHabilitadaSiCompleta
 * 2. preview_esalCancelada_generacionNoHabilitada
 * 3. preview_conCamposFaltantes_tieneBloqueos
 * 4. preview_conTodosLosCampos_sinBloqueos
 * 5. preview_estadoSuspendido_alertaPresente
 * 6. preview_estadoEnLiquidacion_alertaPresente
 * 7. validar_registraAuditoriaErrorCuandoNoHabilitada
 * 8. preview_esalInexistente_lanza404
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PreviewServiceTest {

    @Autowired
    private PreviewService previewService;

    @Autowired
    private EsalRepository esalRepository;

    @Autowired
    private PersoneriaJuridicaRepository personeriaRepository;

    @Autowired
    private NombramientoRepository nombramientoRepository;

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    // =========================================================================
    // 1. ESAL con estado LISTO_PARA_CERTIFICAR → generación habilitada
    // =========================================================================

    @Test
    void preview_esalActiva_generacionHabilitadaSiCompleta() {
        Esal esal = crearEsalCompleta("Fundacion Preview OK");

        PreviewCertificadoDto dto = previewService.obtenerPreview(esal.getId(), null);

        assertThat(dto.getEsalId()).isEqualTo(esal.getId());
        assertThat(dto.getEstado()).isEqualTo(EstadoEsal.ACTIVO);
        assertThat(dto.getGeneracionHabilitada()).isTrue();
        assertThat(dto.getBloqueos()).isEmpty();
    }

    // =========================================================================
    // 2. ESAL CANCELADA → generación no habilitada
    // =========================================================================

    @Test
    void preview_esalCancelada_generacionNoHabilitada() {
        Esal esal = crearEsal("Fundacion Cancelada P", EstadoEsal.CANCELADO,
                EstadoCompletitud.LISTO_PARA_CERTIFICAR);

        PreviewCertificadoDto dto = previewService.obtenerPreview(esal.getId(), null);

        assertThat(dto.getGeneracionHabilitada()).isFalse();
        assertThat(dto.getAlertaEstado()).contains("CANCELADA");
    }

    // =========================================================================
    // 3. Campos obligatorios faltantes → bloqueos presentes
    // =========================================================================

    @Test
    void preview_conCamposFaltantes_tieneBloqueos() {
        // ESAL sin domicilio ni correo (campos obligatorios)
        Esal esal = crearEsal("Fundacion Sin Datos", EstadoEsal.ACTIVO,
                EstadoCompletitud.INCOMPLETO_BLOQUEANTE);

        PreviewCertificadoDto dto = previewService.obtenerPreview(esal.getId(), null);

        assertThat(dto.getBloqueos()).isNotEmpty();
        assertThat(dto.getGeneracionHabilitada()).isFalse();
        assertThat(dto.getBloqueos())
                .anyMatch(b -> b.getMensaje().contains("obligatorio"));
    }

    // =========================================================================
    // 4. Todos los campos completos → sin bloqueos
    // =========================================================================

    @Test
    void preview_conTodosLosCampos_sinBloqueos() {
        Esal esal = crearEsalCompleta("Fundacion Completa Preview");

        PreviewCertificadoDto dto = previewService.obtenerPreview(esal.getId(), null);

        assertThat(dto.getSecciones()).isNotEmpty();
        // Con todos los campos presentes no debe haber bloqueos por campos faltantes
        boolean tieneCamposObligatoriosFaltantes = dto.getBloqueos().stream()
                .anyMatch(b -> "CAMPO_OBLIGATORIO_FALTANTE".equals(b.getTipo()));
        assertThat(tieneCamposObligatoriosFaltantes).isFalse();
    }

    // =========================================================================
    // 5. Estado SUSPENDIDO → alerta presente
    // =========================================================================

    @Test
    void preview_estadoSuspendido_alertaPresente() {
        Esal esal = crearEsal("Fundacion Suspendida P", EstadoEsal.SUSPENDIDO,
                EstadoCompletitud.INCOMPLETO_BLOQUEANTE);

        PreviewCertificadoDto dto = previewService.obtenerPreview(esal.getId(), null);

        assertThat(dto.getAlertaEstado()).isEqualTo("ESAL SUSPENDIDA");
        assertThat(dto.getAdvertencias())
                .anyMatch(a -> a.contains("SUSPENDIDA"));
    }

    // =========================================================================
    // 6. Estado EN_LIQUIDACION → alerta presente
    // =========================================================================

    @Test
    void preview_estadoEnLiquidacion_alertaPresente() {
        Esal esal = crearEsal("Fundacion Liquidada P", EstadoEsal.EN_LIQUIDACION,
                EstadoCompletitud.INCOMPLETO_BLOQUEANTE);

        PreviewCertificadoDto dto = previewService.obtenerPreview(esal.getId(), null);

        assertThat(dto.getAlertaEstado()).isEqualTo("ESAL EN LIQUIDACION");
        assertThat(dto.getAdvertencias())
                .anyMatch(a -> a.contains("LIQUIDACION"));
    }

    // =========================================================================
    // 7. validar() con generación no habilitada → registra ERROR_VALIDACION_PREVIEW
    // =========================================================================

    @Test
    void validar_registraAuditoriaErrorCuandoNoHabilitada() {
        Esal esal = crearEsal("Fundacion Validar Error", EstadoEsal.ACTIVO,
                EstadoCompletitud.INCOMPLETO_BLOQUEANTE);
        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);

        PreviewCertificadoDto dto = previewService.validar(esal.getId(), null);

        assertThat(dto.getGeneracionHabilitada()).isFalse();

        List<Auditoria> auds = auditoriaRepository.findByAccion(AuditoriaAcciones.ERROR_VALIDACION_PREVIEW);
        assertThat(auds).anyMatch(a ->
                esal.getId().equals(a.getEntidadId()) && a.getCreatedAt().isAfter(antes));
    }

    // =========================================================================
    // 8. ESAL inexistente → 404
    // =========================================================================

    @Test
    void preview_esalInexistente_lanza404() {
        assertThatThrownBy(() -> previewService.obtenerPreview(999888L, null))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404");
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private Esal crearEsal(String nombre, EstadoEsal estado, EstadoCompletitud completitud) {
        Esal esal = new Esal();
        esal.setNombre(nombre);
        esal.setEstado(estado);
        esal.setEstadoCompletitud(completitud);
        esal.setCreatedAt(LocalDateTime.now());
        esal.setUpdatedAt(LocalDateTime.now());
        esal.setCreatedBy("test");
        return esalRepository.save(esal);
    }

    private Esal crearEsalCompleta(String nombre) {
        Esal esal = new Esal();
        esal.setNombre(nombre);
        esal.setIdSipej("SIPEJ-PREVIEW-001");
        esal.setNit("900999001");
        esal.setDomicilio("Bogotá D.C.");
        esal.setCorreoElectronico("info@fundacion.org");
        esal.setTerminoDuracion("Indefinido");
        esal.setObjetoSocial("Promover la educación en Bogotá.");
        esal.setEstado(EstadoEsal.ACTIVO);
        esal.setEstadoCompletitud(EstadoCompletitud.LISTO_PARA_CERTIFICAR);
        esal.setCreatedAt(LocalDateTime.now());
        esal.setUpdatedAt(LocalDateTime.now());
        esal.setCreatedBy("test");
        esal = esalRepository.save(esal);

        PersoneriaJuridica pj = new PersoneriaJuridica();
        pj.setEsalId(esal.getId());
        pj.setReconocimientoPersoneriaJuridica("Resolución 1234-2019");
        pj.setFechaReconocimientoPersoneriaJuridica(LocalDate.of(2019, 6, 1));
        pj.setEntidadQueExpide("Secretaría de Educación de Bogotá");
        personeriaRepository.save(pj);

        Nombramiento rl = new Nombramiento();
        rl.setEsalId(esal.getId());
        rl.setTipoNombramiento(TipoNombramiento.REPRESENTANTE_LEGAL);
        rl.setNombre("María López");
        rl.setTipoDocumento("CC");
        rl.setNumeroDocumento("51800111");
        rl.setActaAprueba("Acta 007-2021");
        rl.setFechaActa(LocalDate.of(2021, 3, 10));
        rl.setFacultadesLimitaciones("Representación plena");
        nombramientoRepository.save(rl);

        return esal;
    }
}
