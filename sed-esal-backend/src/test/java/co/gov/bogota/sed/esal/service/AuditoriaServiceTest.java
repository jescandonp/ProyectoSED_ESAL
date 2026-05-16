package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.Auditoria;
import co.gov.bogota.sed.esal.domain.Esal;
import co.gov.bogota.sed.esal.domain.enums.EstadoCompletitud;
import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import co.gov.bogota.sed.esal.dto.DocumentoSoporteDto;
import co.gov.bogota.sed.esal.dto.EsalCreateDto;
import co.gov.bogota.sed.esal.dto.EsalResumenDto;
import co.gov.bogota.sed.esal.repository.AuditoriaRepository;
import co.gov.bogota.sed.esal.repository.EsalRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests de integración para la cobertura de auditoría (T10).
 *
 * Verifica que los eventos de auditoría se persisten correctamente
 * en las operaciones clave del sistema.
 *
 * Tests:
 * 1. registrar_persisteConCamposCorrectos     — llamada directa al AuditoriaService
 * 2. crearEsal_generaEventoAuditoria          — EsalService.crear() → CREAR_ESAL
 * 3. registrarDocumento_generaEventoAuditoria — DocumentoSoporteService.registrar() → REGISTRAR_DOCUMENTO
 * 4. recalcularCompletitud_generaEventoAuditoria — CompletitudService.calcular() → RECALCULAR_COMPLETITUD
 * 5. consultarCompletitud_generaEventoAuditoria  — CompletitudService.consultar() → CONSULTAR_COMPLETITUD
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuditoriaServiceTest {

    @Autowired
    private AuditoriaService auditoriaService;

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private EsalService esalService;

    @Autowired
    private EsalRepository esalRepository;

    @Autowired
    private DocumentoSoporteService documentoSoporteService;

    @Autowired
    private CompletitudService completitudService;

    // =========================================================================
    // 1. Llamada directa — campos persistidos correctamente
    // =========================================================================

    @Test
    void registrar_persisteConCamposCorrectos() {
        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);

        auditoriaService.registrar(
                "test@educacionbogota.edu.co",
                "ADMINISTRADOR",
                AuditoriaAcciones.CREAR_ESAL,
                AuditoriaAcciones.ENTIDAD_ESAL,
                99L, "SIPEJ-TEST",
                AuditoriaAcciones.RESULTADO_EXITO,
                "Detalle de prueba");

        List<Auditoria> registros = auditoriaRepository.findByAccion(AuditoriaAcciones.CREAR_ESAL);
        List<Auditoria> recientes = registros.stream()
                .filter(a -> a.getCreatedAt() != null && a.getCreatedAt().isAfter(antes))
                .filter(a -> "SIPEJ-TEST".equals(a.getIdSipej()))
                .toList();

        assertThat(recientes).isNotEmpty();
        Auditoria reg = recientes.get(0);
        assertThat(reg.getUsuario()).isEqualTo("test@educacionbogota.edu.co");
        assertThat(reg.getRol()).isEqualTo("ADMINISTRADOR");
        assertThat(reg.getEntidad()).isEqualTo(AuditoriaAcciones.ENTIDAD_ESAL);
        assertThat(reg.getResultado()).isEqualTo(AuditoriaAcciones.RESULTADO_EXITO);
        assertThat(reg.getDetalle()).isEqualTo("Detalle de prueba");
        assertThat(reg.getCreatedAt()).isNotNull();
    }

    // =========================================================================
    // 2. EsalService.crear() → CREAR_ESAL
    // =========================================================================

    @Test
    void crearEsal_generaEventoAuditoria() {
        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);

        EsalCreateDto dto = new EsalCreateDto();
        dto.setNombre("Fundacion Auditoria Test");
        EsalResumenDto creada = esalService.crear(dto, "admin@educacionbogota.edu.co");

        List<Auditoria> registros = auditoriaRepository.findByAccion(AuditoriaAcciones.CREAR_ESAL);
        List<Auditoria> recientes = registros.stream()
                .filter(a -> a.getCreatedAt() != null && a.getCreatedAt().isAfter(antes))
                .filter(a -> creada.getId().equals(a.getEntidadId()))
                .toList();

        assertThat(recientes).isNotEmpty();
        Auditoria reg = recientes.get(0);
        assertThat(reg.getEntidad()).isEqualTo(AuditoriaAcciones.ENTIDAD_ESAL);
        assertThat(reg.getResultado()).isEqualTo(AuditoriaAcciones.RESULTADO_EXITO);
    }

    // =========================================================================
    // 3. DocumentoSoporteService.registrar() → REGISTRAR_DOCUMENTO
    // =========================================================================

    @Test
    void registrarDocumento_generaEventoAuditoria() throws Exception {
        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);

        Esal esal = crearEsal("Fundacion Doc Auditoria");
        byte[] pdfBytes = "%PDF-1.4 test".getBytes();

        DocumentoSoporteDto doc = documentoSoporteService.registrar(
                esal.getId(),
                "test.pdf",
                "application/pdf",
                pdfBytes.length,
                new ByteArrayInputStream(pdfBytes),
                null, null,
                "admin@educacionbogota.edu.co");

        List<Auditoria> registros = auditoriaRepository.findByAccion(AuditoriaAcciones.REGISTRAR_DOCUMENTO);
        List<Auditoria> recientes = registros.stream()
                .filter(a -> a.getCreatedAt() != null && a.getCreatedAt().isAfter(antes))
                .filter(a -> doc.getId().equals(a.getEntidadId()))
                .toList();

        assertThat(recientes).isNotEmpty();
        Auditoria reg = recientes.get(0);
        assertThat(reg.getEntidad()).isEqualTo(AuditoriaAcciones.ENTIDAD_DOCUMENTO);
        assertThat(reg.getResultado()).isEqualTo(AuditoriaAcciones.RESULTADO_EXITO);
        assertThat(reg.getDetalle()).contains("test.pdf");
    }

    // =========================================================================
    // 4. CompletitudService.calcular() → RECALCULAR_COMPLETITUD
    // =========================================================================

    @Test
    void recalcularCompletitud_generaEventoAuditoria() {
        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);

        Esal esal = crearEsal("Fundacion Completitud Calc");
        completitudService.calcular(esal.getId());

        List<Auditoria> registros = auditoriaRepository.findByAccion(AuditoriaAcciones.RECALCULAR_COMPLETITUD);
        List<Auditoria> recientes = registros.stream()
                .filter(a -> a.getCreatedAt() != null && a.getCreatedAt().isAfter(antes))
                .filter(a -> esal.getId().equals(a.getEntidadId()))
                .toList();

        assertThat(recientes).isNotEmpty();
        Auditoria reg = recientes.get(0);
        assertThat(reg.getEntidad()).isEqualTo(AuditoriaAcciones.ENTIDAD_ESAL);
        assertThat(reg.getResultado()).isEqualTo(AuditoriaAcciones.RESULTADO_EXITO);
        assertThat(reg.getDetalle()).contains("Semaforo:");
    }

    // =========================================================================
    // 5. CompletitudService.consultar() → CONSULTAR_COMPLETITUD
    // =========================================================================

    @Test
    void consultarCompletitud_generaEventoAuditoria() {
        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);

        Esal esal = crearEsal("Fundacion Completitud Cons");
        completitudService.consultar(esal.getId());

        List<Auditoria> registros = auditoriaRepository.findByAccion(AuditoriaAcciones.CONSULTAR_COMPLETITUD);
        List<Auditoria> recientes = registros.stream()
                .filter(a -> a.getCreatedAt() != null && a.getCreatedAt().isAfter(antes))
                .filter(a -> esal.getId().equals(a.getEntidadId()))
                .toList();

        assertThat(recientes).isNotEmpty();
        Auditoria reg = recientes.get(0);
        assertThat(reg.getEntidad()).isEqualTo(AuditoriaAcciones.ENTIDAD_ESAL);
        assertThat(reg.getResultado()).isEqualTo(AuditoriaAcciones.RESULTADO_EXITO);
    }

    // =========================================================================
    // Helper
    // =========================================================================

    private Esal crearEsal(String nombre) {
        Esal esal = new Esal();
        esal.setNombre(nombre);
        esal.setEstado(EstadoEsal.ACTIVO);
        esal.setEstadoCompletitud(EstadoCompletitud.INCOMPLETO_BLOQUEANTE);
        esal.setCreatedAt(LocalDateTime.now());
        esal.setCreatedBy("test");
        return esalRepository.save(esal);
    }
}
