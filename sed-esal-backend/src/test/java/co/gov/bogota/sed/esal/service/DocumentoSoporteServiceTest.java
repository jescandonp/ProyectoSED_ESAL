package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.Esal;
import co.gov.bogota.sed.esal.domain.enums.EstadoCompletitud;
import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import co.gov.bogota.sed.esal.domain.enums.EstadoValidacionDocumento;
import co.gov.bogota.sed.esal.dto.DocumentoSoporteDto;
import co.gov.bogota.sed.esal.repository.DocumentoSoporteRepository;
import co.gov.bogota.sed.esal.repository.EsalRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests de integración para DocumentoSoporteService (T8).
 *
 * Usa el perfil "test" con H2 en memoria y TestAlmacenamientoService
 * (no escribe al disco real).
 *
 * Tests:
 * 1. uploadPdfExitoso          — PDF registrado con estado PENDIENTE y visible en BD
 * 2. uploadNoPdfRechazado      — image/png lanza 400
 * 3. uploadWordRechazado       — application/msword lanza 400
 * 4. listarDocumentosDeEsal    — 2 PDFs registrados → lista con 2 elementos
 * 5. esalInexistenteLanza404   — esalId inexistente lanza 404
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DocumentoSoporteServiceTest {

    @Autowired
    private DocumentoSoporteService documentoSoporteService;

    @Autowired
    private EsalRepository esalRepository;

    @Autowired
    private DocumentoSoporteRepository documentoSoporteRepository;

    // =========================================================================
    // 1. Upload PDF exitoso
    // =========================================================================

    @Test
    void uploadPdfExitoso() throws IOException {
        // Crear ESAL en BD
        Esal esal = crearEsal("Fundacion Test PDF");

        // Contenido ficticio de PDF
        byte[] contenidoPdf = "%PDF-1.4 test content".getBytes();

        // Llamar registrar() con contentType application/pdf
        DocumentoSoporteDto resultado = documentoSoporteService.registrar(
                esal.getId(),
                "estatutos.pdf",
                "application/pdf",
                contenidoPdf.length,
                new ByteArrayInputStream(contenidoPdf),
                "REGISTRO",
                "ESTATUTOS",
                "admin@educacionbogota.edu.co");

        // Verificar DTO retornado
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isNotNull();
        assertThat(resultado.getEsalId()).isEqualTo(esal.getId());
        assertThat(resultado.getNombreArchivo()).isEqualTo("estatutos.pdf");
        assertThat(resultado.getContentType()).isEqualTo("application/pdf");
        assertThat(resultado.getEstadoValidacion()).isEqualTo(EstadoValidacionDocumento.PENDIENTE);

        // Verificar que el documento queda en BD
        List<co.gov.bogota.sed.esal.domain.DocumentoSoporte> enBd =
                documentoSoporteRepository.findByEsalId(esal.getId());
        assertThat(enBd).hasSize(1);
        assertThat(enBd.get(0).getEstadoValidacion()).isEqualTo(EstadoValidacionDocumento.PENDIENTE);
        assertThat(enBd.get(0).getContentType()).isEqualTo("application/pdf");
    }

    // =========================================================================
    // 2. Upload no-PDF rechazado (image/png)
    // =========================================================================

    @Test
    void uploadNoPdfRechazado() {
        // Crear ESAL en BD
        Esal esal = crearEsal("Fundacion Test PNG");

        byte[] contenido = "fake image content".getBytes();

        // Verificar que lanza ResponseStatusException con status 400
        assertThatThrownBy(() -> documentoSoporteService.registrar(
                esal.getId(),
                "imagen.png",
                "image/png",
                contenido.length,
                new ByteArrayInputStream(contenido),
                null,
                null,
                "admin@educacionbogota.edu.co"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatus().value()).isEqualTo(400);
                });
    }

    // =========================================================================
    // 3. Upload Word rechazado (application/msword)
    // =========================================================================

    @Test
    void uploadWordRechazado() {
        // Crear ESAL en BD
        Esal esal = crearEsal("Fundacion Test Word");

        byte[] contenido = "fake word content".getBytes();

        // Verificar que lanza ResponseStatusException con status 400
        assertThatThrownBy(() -> documentoSoporteService.registrar(
                esal.getId(),
                "documento.doc",
                "application/msword",
                contenido.length,
                new ByteArrayInputStream(contenido),
                null,
                null,
                "admin@educacionbogota.edu.co"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatus().value()).isEqualTo(400);
                });
    }

    // =========================================================================
    // 4. Listar documentos de una ESAL
    // =========================================================================

    @Test
    void listarDocumentosDeEsal() throws IOException {
        // Crear ESAL en BD
        Esal esal = crearEsal("Fundacion Test Listado");

        byte[] contenidoPdf = "%PDF-1.4 test".getBytes();

        // Registrar 2 documentos PDF
        documentoSoporteService.registrar(
                esal.getId(), "doc1.pdf", "application/pdf",
                contenidoPdf.length, new ByteArrayInputStream(contenidoPdf),
                "REGISTRO", "ESTATUTOS", "admin@educacionbogota.edu.co");

        documentoSoporteService.registrar(
                esal.getId(), "doc2.pdf", "application/pdf",
                contenidoPdf.length, new ByteArrayInputStream(contenidoPdf),
                "REGISTRO", "ACTA", "admin@educacionbogota.edu.co");

        // Llamar listar(esalId)
        List<DocumentoSoporteDto> lista = documentoSoporteService.listar(esal.getId());

        // Verificar que retorna lista con 2 elementos
        assertThat(lista).hasSize(2);
        assertThat(lista).allMatch(d -> "application/pdf".equals(d.getContentType()));
        assertThat(lista).allMatch(d -> esal.getId().equals(d.getEsalId()));
    }

    // =========================================================================
    // 5. ESAL inexistente lanza 404
    // =========================================================================

    @Test
    void esalInexistenteLanza404() {
        Long esalIdInexistente = 99999L;
        byte[] contenido = "%PDF-1.4 test".getBytes();

        // Verificar que lanza ResponseStatusException con status 404
        assertThatThrownBy(() -> documentoSoporteService.registrar(
                esalIdInexistente,
                "doc.pdf",
                "application/pdf",
                contenido.length,
                new ByteArrayInputStream(contenido),
                null,
                null,
                "admin@educacionbogota.edu.co"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatus().value()).isEqualTo(404);
                });
    }

    // =========================================================================
    // I9. Validaciones documentales transversales
    // =========================================================================

    @Test
    void cargaSinReferencia_esRechazada() {
        Esal esal = crearEsal("Fundacion Sin Referencia I9");
        byte[] contenidoPdf = "%PDF-1.4 test content".getBytes();

        assertThatThrownBy(() -> documentoSoporteService.registrar(
                esal.getId(),
                "creacion.pdf",
                "application/pdf",
                contenidoPdf.length,
                new ByteArrayInputStream(contenidoPdf),
                "CREACION_FORMACION",
                null,
                "   ",
                LocalDate.now(),
                "Observacion",
                "admin@educacionbogota.edu.co"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatus().value()).isEqualTo(400);
                });
    }

    @Test
    void cargaSinFechaActo_esRechazada() {
        Esal esal = crearEsal("Fundacion Sin Fecha I9");
        byte[] contenidoPdf = "%PDF-1.4 test content".getBytes();

        assertThatThrownBy(() -> documentoSoporteService.registrar(
                esal.getId(),
                "dignatarios.pdf",
                "application/pdf",
                contenidoPdf.length,
                new ByteArrayInputStream(contenidoPdf),
                "DIGNATARIOS",
                null,
                "ACTO-001",
                null,
                null,
                "admin@educacionbogota.edu.co"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatus().value()).isEqualTo(400);
                });
    }

    @Test
    void pdfMayorA10Mb_esRechazado() {
        Esal esal = crearEsal("Fundacion Archivo Grande I9");
        byte[] contenidoPdf = "%PDF-1.4 test content".getBytes();

        assertThatThrownBy(() -> documentoSoporteService.registrar(
                esal.getId(),
                "grande.pdf",
                "application/pdf",
                10L * 1024L * 1024L + 1L,
                new ByteArrayInputStream(contenidoPdf),
                "CREACION_FORMACION",
                null,
                "RES-001",
                LocalDate.now(),
                null,
                "admin@educacionbogota.edu.co"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatus().value()).isEqualTo(400);
                });
    }

    @Test
    void subtipoIncompatible_esRechazado() {
        Esal esal = crearEsal("Fundacion Subtipo Incompatible I9");
        byte[] contenidoPdf = "%PDF-1.4 test content".getBytes();

        assertThatThrownBy(() -> documentoSoporteService.registrar(
                esal.getId(),
                "creacion.pdf",
                "application/pdf",
                contenidoPdf.length,
                new ByteArrayInputStream(contenidoPdf),
                "CREACION_FORMACION",
                "CANCELACION_VOLUNTARIA",
                "RES-001",
                LocalDate.now(),
                null,
                "admin@educacionbogota.edu.co"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatus().value()).isEqualTo(400);
                });
    }

    @Test
    void nuevaCargaMismoTipoSubtipo_reemplazaVigenteYConservaHistorico() throws IOException {
        Esal esal = crearEsal("Fundacion Historico I9");
        byte[] contenidoPdf = "%PDF-1.4 test content".getBytes();

        DocumentoSoporteDto primero = documentoSoporteService.registrar(
                esal.getId(), "liquidacion-1.pdf", "application/pdf", contenidoPdf.length,
                new ByteArrayInputStream(contenidoPdf), "LIQUIDACION", "TERMINO_DURACION",
                "OF-001", LocalDate.of(2026, 6, 1), "Primera version", "admin@educacionbogota.edu.co");

        DocumentoSoporteDto segundo = documentoSoporteService.registrar(
                esal.getId(), "liquidacion-2.pdf", "application/pdf", contenidoPdf.length,
                new ByteArrayInputStream(contenidoPdf), "LIQUIDACION", "TERMINO_DURACION",
                "OF-002", LocalDate.of(2026, 6, 2), "Segunda version", "admin@educacionbogota.edu.co");

        List<DocumentoSoporteDto> documentos = documentoSoporteService.listar(esal.getId());

        assertThat(documentos).hasSize(2);
        assertThat(documentos).filteredOn(DocumentoSoporteDto::isVigente).extracting(DocumentoSoporteDto::getId)
                .containsExactly(segundo.getId());
        assertThat(documentos).filteredOn(d -> !d.isVigente()).extracting(DocumentoSoporteDto::getId)
                .containsExactly(primero.getId());
    }

    @Test
    void descargarDocumento_validaPertenenciaYRetornaBytes() throws IOException {
        Esal esal = crearEsal("Fundacion Descarga I9");
        byte[] contenidoPdf = "%PDF-1.4 descarga".getBytes();

        DocumentoSoporteDto documento = documentoSoporteService.registrar(
                esal.getId(), "descarga.pdf", "application/pdf", contenidoPdf.length,
                new ByteArrayInputStream(contenidoPdf), "CREACION_FORMACION", null,
                "RES-100", LocalDate.now(), null, "admin@educacionbogota.edu.co");

        DocumentoSoporteService.DocumentoDescarga descarga =
                documentoSoporteService.descargar(esal.getId(), documento.getId(), "expedidor@educacionbogota.edu.co");

        assertThat(descarga.getNombreArchivo()).isEqualTo("descarga.pdf");
        assertThat(descarga.getContentType()).isEqualTo("application/pdf");
        assertThat(descarga.getContenido()).containsExactly(contenidoPdf);
    }

    // =========================================================================
    // Helper
    // =========================================================================

    /**
     * Crea y persiste una ESAL mínima para los tests.
     *
     * @param nombre nombre de la ESAL
     * @return la Esal guardada
     */
    private Esal crearEsal(String nombre) {
        Esal esal = new Esal();
        esal.setNombre(nombre);
        esal.setIdSipej("SIPEJ-T8-" + System.nanoTime());
        esal.setDomicilio("Bogotá D.C.");
        esal.setCorreoElectronico("test@fundacion.org");
        esal.setTerminoDuracion("Indefinido");
        esal.setObjetoSocial("Objeto social de prueba para T8.");
        esal.setEstado(EstadoEsal.ACTIVO);
        esal.setEstadoCompletitud(EstadoCompletitud.INCOMPLETO_BLOQUEANTE);
        esal.setCreatedAt(LocalDateTime.now());
        esal.setCreatedBy("test");
        return esalRepository.save(esal);
    }
}
