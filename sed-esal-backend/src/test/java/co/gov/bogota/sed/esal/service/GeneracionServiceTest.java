package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.Esal;
import co.gov.bogota.sed.esal.domain.Nombramiento;
import co.gov.bogota.sed.esal.domain.PersoneriaJuridica;
import co.gov.bogota.sed.esal.domain.enums.EstadoCompletitud;
import co.gov.bogota.sed.esal.domain.enums.EstadoCertificado;
import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import co.gov.bogota.sed.esal.domain.enums.TipoNombramiento;
import co.gov.bogota.sed.esal.dto.CertificadoDto;
import co.gov.bogota.sed.esal.dto.FirmanteCreateDto;
import co.gov.bogota.sed.esal.repository.EsalRepository;
import co.gov.bogota.sed.esal.repository.NombramientoRepository;
import co.gov.bogota.sed.esal.repository.PersoneriaJuridicaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class GeneracionServiceTest {

    @Autowired private GeneracionService generacionService;
    @Autowired private FirmanteService firmanteService;
    @Autowired private EsalRepository esalRepository;
    @Autowired private PersoneriaJuridicaRepository personeriaRepository;
    @Autowired private NombramientoRepository nombramientoRepository;

    private Long esalId;
    private LocalDateTime versionInicial;

    @BeforeEach
    void setUp() {
        versionInicial = LocalDateTime.of(2026, 5, 21, 9, 0);
        Esal esal = new Esal();
        esal.setNombre("Fundacion Test Generacion");
        esal.setNit("900111222-3");
        esal.setIdSipej("SJ-GENTEST-01");
        esal.setDomicilio("Bogota");
        esal.setObjetoSocial("Promover la educacion en la comunidad");
        esal.setTerminoDuracion("Indefinido");
        esal.setEstado(EstadoEsal.ACTIVO);
        esal.setEstadoCompletitud(EstadoCompletitud.LISTO_PARA_CERTIFICAR);
        esal.setCreatedAt(LocalDateTime.now());
        esal.setUpdatedAt(versionInicial);
        esal.setCreatedBy("test");
        esal = esalRepository.save(esal);
        esalId = esal.getId();

        // Personeria juridica obligatoria
        PersoneriaJuridica pj = new PersoneriaJuridica();
        pj.setEsalId(esalId);
        pj.setReconocimientoPersoneriaJuridica("Resolucion 001 de 2020");
        pj.setEntidadQueExpide("Secretaria de Educacion del Distrito");
        pj.setFechaReconocimientoPersoneriaJuridica(LocalDate.of(2020, 1, 15));
        personeriaRepository.save(pj);

        // Representante legal obligatorio
        Nombramiento rep = new Nombramiento();
        rep.setEsalId(esalId);
        rep.setTipoNombramiento(TipoNombramiento.REPRESENTANTE_LEGAL);
        rep.setNombre("Pedro Ramirez");
        rep.setNumeroDocumento("80123456");
        nombramientoRepository.save(rep);

        // Firmante vigente
        FirmanteCreateDto f = new FirmanteCreateDto();
        f.setNombre("Directora General Test");
        f.setCargo("Directora de Inspeccion y Vigilancia");
        f.setFechaInicioVigencia(LocalDate.of(2020, 1, 1));
        f.setFechaFinVigencia(null);
        firmanteService.crear(f, "admin");
    }

    @Test
    void generar_esalCompleta_creaConNumeroCertificado() {
        CertificadoDto cert = generacionService.generar(esalId, "expedidor");
        assertThat(cert.getEstadoCertificado()).isEqualTo(EstadoCertificado.GENERADO);
        assertThat(cert.getNumeroCertificado()).matches("ESAL-\\d{4}-\\d{6}");
        assertThat(cert.getHashSha256()).hasSize(64);
        assertThat(cert.getFirmanteNombre()).isEqualTo("Directora General Test");
    }

    @Test
    void generar_dosVeces_numerosDistintos() {
        CertificadoDto c1 = generacionService.generar(esalId, "expedidor");
        CertificadoDto c2 = generacionService.generar(esalId, "expedidor");
        assertThat(c1.getNumeroCertificado()).isNotEqualTo(c2.getNumeroCertificado());
    }

    @Test
    void generar_sinFirmante_lanzaError() {
        firmanteService.listar().forEach(f -> firmanteService.inactivar(f.getId(), "admin"));
        assertThatThrownBy(() -> generacionService.generar(esalId, "expedidor"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatus())
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    void listarPorEsal_despuesDeGenerar_retornaHistorial() {
        generacionService.generar(esalId, "expedidor");
        assertThat(generacionService.listarPorEsal(esalId)).hasSize(1);
    }

    @Test
    void certificadoHistoricoConservaVersionDatosDespuesDeEditarEsal() {
        CertificadoDto historico = generacionService.generar(esalId, "expedidor");

        LocalDateTime nuevaVersion = versionInicial.plusDays(1);
        Esal esal = esalRepository.findById(esalId).orElseThrow(IllegalStateException::new);
        esal.setNombre("Fundacion Test Generacion Actualizada I5");
        esal.setUpdatedAt(nuevaVersion);
        esalRepository.save(esal);

        CertificadoDto historicoConsultado = generacionService.obtener(historico.getCertificadoId());
        CertificadoDto nuevo = generacionService.generar(esalId, "expedidor");

        assertThat(historicoConsultado.getVersionDatos()).isEqualTo(versionInicial);
        assertThat(nuevo.getVersionDatos()).isEqualTo(nuevaVersion);
    }
}
