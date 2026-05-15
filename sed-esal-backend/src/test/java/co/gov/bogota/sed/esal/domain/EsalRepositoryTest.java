package co.gov.bogota.sed.esal.domain;

import co.gov.bogota.sed.esal.domain.enums.EstadoCompletitud;
import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import co.gov.bogota.sed.esal.domain.enums.TipoAdvertencia;
import co.gov.bogota.sed.esal.repository.AdvertenciaCompletitudRepository;
import co.gov.bogota.sed.esal.repository.CampoObligatoriedadRepository;
import co.gov.bogota.sed.esal.repository.EsalRepository;
import co.gov.bogota.sed.esal.repository.ReformaEstatutariaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests de repositorio JPA para el modelo de dominio T3.
 * Usa H2 en memoria con modo de compatibilidad Oracle (perfil "test").
 */
@DataJpaTest
@ActiveProfiles("test")
class EsalRepositoryTest {

    @Autowired
    private EsalRepository esalRepository;

    @Autowired
    private ReformaEstatutariaRepository reformaRepository;

    @Autowired
    private CampoObligatoriedadRepository campoRepository;

    @Autowired
    private AdvertenciaCompletitudRepository advertenciaRepository;

    // -------------------------------------------------------------------------
    // 1. Guardar y recuperar una Esal básica
    // -------------------------------------------------------------------------

    @Test
    void guardarYRecuperarEsalBasica() {
        Esal esal = new Esal();
        esal.setNombre("Fundación Educativa Bogotá");
        esal.setNit("900123456-1");
        esal.setDomicilio("Bogotá D.C.");
        esal.setEstado(EstadoEsal.ACTIVO);
        esal.setEstadoCompletitud(EstadoCompletitud.INCOMPLETO_BLOQUEANTE);
        esal.setCreatedAt(LocalDateTime.now());
        esal.setCreatedBy("admin@educacionbogota.edu.co");

        Esal guardada = esalRepository.save(esal);

        assertThat(guardada.getId()).isNotNull();
        Optional<Esal> recuperada = esalRepository.findById(guardada.getId());
        assertThat(recuperada).isPresent();
        assertThat(recuperada.get().getNombre()).isEqualTo("Fundación Educativa Bogotá");
        assertThat(recuperada.get().getEstado()).isEqualTo(EstadoEsal.ACTIVO);
    }

    // -------------------------------------------------------------------------
    // 2. Buscar por idSipej
    // -------------------------------------------------------------------------

    @Test
    void buscarPorIdSipej() {
        Esal esal = new Esal();
        esal.setNombre("Corporación Cultural SED");
        esal.setIdSipej("SIPEJ-2024-001");
        esal.setEstado(EstadoEsal.ACTIVO);
        esal.setEstadoCompletitud(EstadoCompletitud.LISTO_PARA_CERTIFICAR);
        esalRepository.save(esal);

        Optional<Esal> resultado = esalRepository.findByIdSipej("SIPEJ-2024-001");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Corporación Cultural SED");
    }

    @Test
    void buscarPorIdSipejInexistenteRetornaVacio() {
        Optional<Esal> resultado = esalRepository.findByIdSipej("NO-EXISTE-999");
        assertThat(resultado).isEmpty();
    }

    // -------------------------------------------------------------------------
    // 3. Guardar ReformaEstatutaria y recuperar ordenada por orden
    // -------------------------------------------------------------------------

    @Test
    void guardarReformasYRecuperarOrdenadas() {
        Esal esal = new Esal();
        esal.setNombre("Asociación Deportiva Distrital");
        esal.setEstado(EstadoEsal.ACTIVO);
        esal.setEstadoCompletitud(EstadoCompletitud.INCOMPLETO_NO_BLOQUEANTE);
        Esal esalGuardada = esalRepository.save(esal);

        ReformaEstatutaria reforma3 = new ReformaEstatutaria();
        reforma3.setEsalId(esalGuardada.getId());
        reforma3.setOrden(3);
        reforma3.setTipoActo("Acta de Asamblea");
        reforma3.setNumeroActo("003");
        reforma3.setFechaActo(LocalDate.of(2023, 3, 15));

        ReformaEstatutaria reforma1 = new ReformaEstatutaria();
        reforma1.setEsalId(esalGuardada.getId());
        reforma1.setOrden(1);
        reforma1.setTipoActo("Acta de Constitución");
        reforma1.setNumeroActo("001");
        reforma1.setFechaActo(LocalDate.of(2021, 1, 10));

        ReformaEstatutaria reforma2 = new ReformaEstatutaria();
        reforma2.setEsalId(esalGuardada.getId());
        reforma2.setOrden(2);
        reforma2.setTipoActo("Reforma Parcial");
        reforma2.setNumeroActo("002");
        reforma2.setFechaActo(LocalDate.of(2022, 6, 20));

        reformaRepository.save(reforma3);
        reformaRepository.save(reforma1);
        reformaRepository.save(reforma2);

        List<ReformaEstatutaria> reformas =
                reformaRepository.findByEsalIdOrderByOrden(esalGuardada.getId());

        assertThat(reformas).hasSize(3);
        assertThat(reformas.get(0).getOrden()).isEqualTo(1);
        assertThat(reformas.get(1).getOrden()).isEqualTo(2);
        assertThat(reformas.get(2).getOrden()).isEqualTo(3);
    }

    // -------------------------------------------------------------------------
    // 4. Guardar CampoObligatoriedad y filtrar por obligatorio = true
    // -------------------------------------------------------------------------

    @Test
    void guardarCamposYFiltrarObligatorios() {
        CampoObligatoriedad campoObligatorio1 = new CampoObligatoriedad();
        campoObligatorio1.setNombreCampo("NOMBRE");
        campoObligatorio1.setSeccion("DATOS_GENERALES");
        campoObligatorio1.setObligatorio(Boolean.TRUE);
        campoObligatorio1.setOrden(1);

        CampoObligatoriedad campoObligatorio2 = new CampoObligatoriedad();
        campoObligatorio2.setNombreCampo("ID_SIPEJ");
        campoObligatorio2.setSeccion("DATOS_GENERALES");
        campoObligatorio2.setObligatorio(Boolean.TRUE);
        campoObligatorio2.setOrden(2);

        CampoObligatoriedad campoOpcional = new CampoObligatoriedad();
        campoOpcional.setNombreCampo("NIT");
        campoOpcional.setSeccion("DATOS_GENERALES");
        campoOpcional.setObligatorio(Boolean.FALSE);
        campoOpcional.setOrden(3);

        campoRepository.save(campoObligatorio1);
        campoRepository.save(campoObligatorio2);
        campoRepository.save(campoOpcional);

        List<CampoObligatoriedad> obligatorios = campoRepository.findByObligatorio(Boolean.TRUE);
        List<CampoObligatoriedad> opcionales = campoRepository.findByObligatorio(Boolean.FALSE);

        assertThat(obligatorios).hasSize(2);
        assertThat(opcionales).hasSize(1);
        assertThat(obligatorios).extracting(CampoObligatoriedad::getNombreCampo)
                .containsExactlyInAnyOrder("NOMBRE", "ID_SIPEJ");
    }

    // -------------------------------------------------------------------------
    // 5. Guardar AdvertenciaCompletitud y filtrar por bloqueante = true
    // -------------------------------------------------------------------------

    @Test
    void guardarAdvertenciasYFiltrarBloqueantes() {
        Esal esal = new Esal();
        esal.setNombre("Fundación Social Norte");
        esal.setEstado(EstadoEsal.ACTIVO);
        esal.setEstadoCompletitud(EstadoCompletitud.INCOMPLETO_BLOQUEANTE);
        Esal esalGuardada = esalRepository.save(esal);

        AdvertenciaCompletitud advertenciaBloqueante = new AdvertenciaCompletitud();
        advertenciaBloqueante.setEsalId(esalGuardada.getId());
        advertenciaBloqueante.setSeccion("DATOS_GENERALES");
        advertenciaBloqueante.setCampo("ID_SIPEJ");
        advertenciaBloqueante.setTipo(TipoAdvertencia.CAMPO_OBLIGATORIO_FALTANTE);
        advertenciaBloqueante.setBloqueante(Boolean.TRUE);
        advertenciaBloqueante.setMensaje("El campo ID SIPEJ es obligatorio para certificación");
        advertenciaBloqueante.setCreatedAt(LocalDateTime.now());

        AdvertenciaCompletitud advertenciaNoBloqueante = new AdvertenciaCompletitud();
        advertenciaNoBloqueante.setEsalId(esalGuardada.getId());
        advertenciaNoBloqueante.setSeccion("DATOS_GENERALES");
        advertenciaNoBloqueante.setCampo("CORREO_ELECTRONICO");
        advertenciaNoBloqueante.setTipo(TipoAdvertencia.ADVERTENCIA_HISTORICA);
        advertenciaNoBloqueante.setBloqueante(Boolean.FALSE);
        advertenciaNoBloqueante.setMensaje("Correo electrónico no registrado en fuente histórica");
        advertenciaNoBloqueante.setCreatedAt(LocalDateTime.now());

        advertenciaRepository.save(advertenciaBloqueante);
        advertenciaRepository.save(advertenciaNoBloqueante);

        List<AdvertenciaCompletitud> todasLasAdvertencias =
                advertenciaRepository.findByEsalId(esalGuardada.getId());
        List<AdvertenciaCompletitud> solosBloqueantes =
                advertenciaRepository.findByEsalIdAndBloqueante(esalGuardada.getId(), Boolean.TRUE);

        assertThat(todasLasAdvertencias).hasSize(2);
        assertThat(solosBloqueantes).hasSize(1);
        assertThat(solosBloqueantes.get(0).getCampo()).isEqualTo("ID_SIPEJ");
        assertThat(solosBloqueantes.get(0).getTipo())
                .isEqualTo(TipoAdvertencia.CAMPO_OBLIGATORIO_FALTANTE);
    }
}
