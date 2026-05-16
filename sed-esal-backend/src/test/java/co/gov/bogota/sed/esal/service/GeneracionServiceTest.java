package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.Esal;
import co.gov.bogota.sed.esal.domain.Firmante;
import co.gov.bogota.sed.esal.domain.Nombramiento;
import co.gov.bogota.sed.esal.domain.PersoneriaJuridica;
import co.gov.bogota.sed.esal.domain.enums.EstadoCertificado;
import co.gov.bogota.sed.esal.domain.enums.EstadoCompletitud;
import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import co.gov.bogota.sed.esal.domain.enums.TipoNombramiento;
import co.gov.bogota.sed.esal.dto.CertificadoDto;
import co.gov.bogota.sed.esal.repository.EsalRepository;
import co.gov.bogota.sed.esal.repository.FirmanteRepository;
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
 * Tests de integración para GeneracionService (I3).
 *
 * 1. generar_esalCompleta_estadoGenerado
 * 2. generar_numeroCertificadoFormato
 * 3. generar_hashSha256NoNulo
 * 4. generar_sinFirmante_lanza422
 * 5. generar_previewBloqueada_lanza422
 * 6. generar_consecutivoIncrementa
 * 7. historial_retornaCertificados
 * 8. obtener_retornaCertificado
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class GeneracionServiceTest {

    @Autowired GeneracionService generacionService;
    @Autowired EsalRepository esalRepository;
    @Autowired FirmanteRepository firmanteRepository;
    @Autowired PersoneriaJuridicaRepository personeriaRepository;
    @Autowired NombramientoRepository nombramientoRepository;

    @Test
    void generar_esalCompleta_estadoGenerado() {
        Esal esal = crearEsalCompleta("Fundación Test Generacion");
        crearFirmanteVigente();

        CertificadoDto dto = generacionService.generar(esal.getId());

        assertThat(dto.getEstadoCertificado()).isEqualTo(EstadoCertificado.GENERADO);
        assertThat(dto.getId()).isNotNull();
    }

    @Test
    void generar_numeroCertificadoFormato() {
        Esal esal = crearEsalCompleta("Fundación Formato Numero");
        crearFirmanteVigente();

        CertificadoDto dto = generacionService.generar(esal.getId());

        int anio = LocalDateTime.now().getYear();
        assertThat(dto.getNumeroCertificado()).matches("ESAL-" + anio + "-\\d{6}");
    }

    @Test
    void generar_hashSha256NoNulo() {
        Esal esal = crearEsalCompleta("Fundación Hash Test");
        crearFirmanteVigente();

        CertificadoDto dto = generacionService.generar(esal.getId());

        assertThat(dto.getHashSha256()).isNotBlank();
        assertThat(dto.getHashSha256()).hasSize(64); // SHA-256 hex = 64 chars
    }

    @Test
    void generar_sinFirmante_lanza422() {
        Esal esal = crearEsalCompleta("Fundación Sin Firmante");

        assertThatThrownBy(() -> generacionService.generar(esal.getId()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("422");
    }

    @Test
    void generar_previewBloqueada_lanza422() {
        // ESAL sin campos obligatorios — preview bloqueada
        Esal esal = new Esal();
        esal.setNombre("ESAL Incompleta");
        esal.setEstado(EstadoEsal.ACTIVO);
        esal.setEstadoCompletitud(EstadoCompletitud.INCOMPLETO_BLOQUEANTE);
        esal.setCreatedAt(LocalDateTime.now());
        esal.setUpdatedAt(LocalDateTime.now());
        esalRepository.save(esal);

        crearFirmanteVigente();

        assertThatThrownBy(() -> generacionService.generar(esal.getId()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("422");
    }

    @Test
    void generar_dosVeces_consecutivoIncrementa() {
        Esal e1 = crearEsalCompleta("Fundacion Consec 1");
        Esal e2 = crearEsalCompleta("Fundacion Consec 2");
        e2.setIdSipej("SIP-CONSEC-2");
        esalRepository.save(e2);
        crearFirmanteVigente();

        CertificadoDto c1 = generacionService.generar(e1.getId());
        CertificadoDto c2 = generacionService.generar(e2.getId());

        assertThat(c1.getNumeroCertificado()).isNotEqualTo(c2.getNumeroCertificado());
    }

    @Test
    void historial_retornaCertificados() {
        Esal esal = crearEsalCompleta("Fundación Historial");
        crearFirmanteVigente();

        generacionService.generar(esal.getId());

        List<CertificadoDto> historial = generacionService.historialPorEsal(esal.getId());
        assertThat(historial).hasSize(1);
        assertThat(historial.get(0).getEsalId()).isEqualTo(esal.getId());
    }

    @Test
    void obtener_retornaCertificado() {
        Esal esal = crearEsalCompleta("Fundación Obtener");
        crearFirmanteVigente();

        CertificadoDto generado = generacionService.generar(esal.getId());
        CertificadoDto obtenido = generacionService.obtener(generado.getId());

        assertThat(obtenido.getId()).isEqualTo(generado.getId());
        assertThat(obtenido.getNumeroCertificado()).isEqualTo(generado.getNumeroCertificado());
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private Esal crearEsalCompleta(String nombre) {
        Esal esal = new Esal();
        esal.setNombre(nombre);
        esal.setIdSipej("SIP-GEN-" + System.nanoTime());
        esal.setNit("900000001-1");
        esal.setDomicilio("Bogotá D.C.");
        esal.setCorreoElectronico("test@test.gov.co");
        esal.setTerminoDuracion("Indefinido");
        esal.setObjetoSocial("Objeto social de prueba para generación de certificado.");
        esal.setEstado(EstadoEsal.ACTIVO);
        esal.setEstadoCompletitud(EstadoCompletitud.LISTO_PARA_CERTIFICAR);
        esal.setCreatedAt(LocalDateTime.now());
        esal.setUpdatedAt(LocalDateTime.now());
        esalRepository.save(esal);

        PersoneriaJuridica pj = new PersoneriaJuridica();
        pj.setEsalId(esal.getId());
        pj.setReconocimientoPersoneriaJuridica("Res. 1234 de 2020");
        pj.setFechaReconocimientoPersoneriaJuridica(LocalDate.of(2020, 1, 15));
        pj.setEntidadQueExpide("Secretaría de Educación del Distrito");
        pj.setInscripcion("Ins. 5678 de 2020");
        pj.setFechaInscripcion(LocalDate.of(2020, 2, 1));
        pj.setEntidadQueInscribio("Cámara de Comercio de Bogotá");
        personeriaRepository.save(pj);

        Nombramiento rl = new Nombramiento();
        rl.setEsalId(esal.getId());
        rl.setTipoNombramiento(TipoNombramiento.REPRESENTANTE_LEGAL);
        rl.setNombre("Juan Test");
        rl.setNumeroDocumento("123456789");
        rl.setActaAprueba("Acta 01 de 2020");
        rl.setFechaActa(LocalDate.of(2020, 3, 10));
        nombramientoRepository.save(rl);

        return esal;
    }

    private void crearFirmanteVigente() {
        Firmante f = new Firmante();
        f.setNombre("María Directora");
        f.setCargo("Directora de Inspección y Vigilancia");
        f.setFechaInicioVigencia(LocalDate.of(2026, 1, 1));
        f.setActivo(true);
        f.setCreatedAt(LocalDateTime.now());
        f.setUpdatedAt(LocalDateTime.now());
        firmanteRepository.save(f);
    }
}
