package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.ActuacionAdministrativa;
import co.gov.bogota.sed.esal.domain.Esal;
import co.gov.bogota.sed.esal.domain.Nombramiento;
import co.gov.bogota.sed.esal.domain.OrganoAdministracion;
import co.gov.bogota.sed.esal.domain.PersoneriaJuridica;
import co.gov.bogota.sed.esal.domain.enums.EstadoCompletitud;
import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import co.gov.bogota.sed.esal.domain.enums.TipoActuacion;
import co.gov.bogota.sed.esal.domain.enums.TipoNombramiento;
import co.gov.bogota.sed.esal.dto.PreviewCertificadoDto;
import co.gov.bogota.sed.esal.repository.ActuacionAdministrativaRepository;
import co.gov.bogota.sed.esal.repository.EsalRepository;
import co.gov.bogota.sed.esal.repository.NombramientoRepository;
import co.gov.bogota.sed.esal.repository.OrganoAdministracionRepository;
import co.gov.bogota.sed.esal.repository.PersoneriaJuridicaRepository;
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
    private OrganoAdministracionRepository organoRepository;

    @Autowired
    private ActuacionAdministrativaRepository actuacionRepository;

    @Test
    void previewEsalActiva_generacionHabilitadaTrue() {
        Esal esal = crearEsalCompleta("Fundacion Preview Activa I2", "PRE-I2-001", EstadoEsal.ACTIVO);

        PreviewCertificadoDto preview = previewService.obtenerPreview(esal.getId(), "tester");

        assertThat(preview.getGeneracionHabilitada()).isTrue();
        assertThat(preview.getBloqueos()).isEmpty();
    }

    @Test
    void previewEsalActiva_alertaEstadoNull() {
        Esal esal = crearEsalCompleta("Fundacion Preview Activa Alerta I2", "PRE-I2-002", EstadoEsal.ACTIVO);

        PreviewCertificadoDto preview = previewService.obtenerPreview(esal.getId(), "tester");

        assertThat(preview.getAlertaEstado()).isNull();
    }

    @Test
    void previewConFaltanteObligatorio_bloqueada() {
        Esal esal = crearEsalCompleta("Fundacion Preview Faltante I2", "PRE-I2-003", EstadoEsal.ACTIVO);
        esal.setDomicilio("NR");
        esalRepository.save(esal);

        PreviewCertificadoDto preview = previewService.obtenerPreview(esal.getId(), "tester");

        assertThat(preview.getGeneracionHabilitada()).isFalse();
        assertThat(preview.getBloqueos()).anyMatch(b -> "CAMPO_FALTANTE".equals(b.getTipo()));
    }

    @Test
    void previewUsaRepresentanteLegalVigenteDespuesDeMantenimientoI5() {
        Esal esal = crearEsalCompleta("Fundacion Preview Vigencia I5", "PRE-I5-001", EstadoEsal.ACTIVO);
        nombramientoRepository.findByEsalId(esal.getId()).forEach(n -> {
            n.setNombre("Representante Historico");
            n.setVigente(Boolean.FALSE);
            nombramientoRepository.save(n);
        });

        Nombramiento vigente = new Nombramiento();
        vigente.setEsalId(esal.getId());
        vigente.setTipoNombramiento(TipoNombramiento.REPRESENTANTE_LEGAL);
        vigente.setNombre("Representante Vigente I5");
        vigente.setNumeroDocumento("987654");
        vigente.setVigente(Boolean.TRUE);
        nombramientoRepository.save(vigente);

        PreviewCertificadoDto preview = previewService.obtenerPreview(esal.getId(), "tester");

        assertThat(preview.getSecciones()).anySatisfy(seccion -> {
            assertThat(seccion.getNombre()).isEqualTo("REPRESENTANTE LEGAL");
            assertThat(seccion.getCampos()).anySatisfy(campo -> {
                assertThat(campo.getEtiqueta()).isEqualTo("Nombre");
                assertThat(campo.getValor()).isEqualTo("Representante Vigente I5");
            });
        });
    }

    @Test
    void previewSuspendidaCompleta_habilitada() {
        Esal esal = crearEsalCompleta("Fundacion Preview Suspendida I2", "PRE-I2-004", EstadoEsal.SUSPENDIDO);
        crearActuacion(esal.getId(), TipoActuacion.SUSPENSION);

        PreviewCertificadoDto preview = previewService.obtenerPreview(esal.getId(), "tester");

        assertThat(preview.getGeneracionHabilitada()).isTrue();
        assertThat(preview.getAlertaEstado()).contains("SUSPENDIDO");
    }

    @Test
    void previewSuspendidaSinActuacion_bloqueada() {
        Esal esal = crearEsalCompleta("Fundacion Preview Suspendida Sin Actuacion I2", "PRE-I2-005", EstadoEsal.SUSPENDIDO);

        PreviewCertificadoDto preview = previewService.obtenerPreview(esal.getId(), "tester");

        assertThat(preview.getGeneracionHabilitada()).isFalse();
        assertThat(preview.getBloqueos()).anyMatch(b -> "REGLA_ESTADO".equals(b.getTipo()));
    }

    @Test
    void previewEnLiquidacionCompleta_habilitada() {
        Esal esal = crearEsalCompleta("Fundacion Preview Liquidacion I2", "PRE-I2-006", EstadoEsal.EN_LIQUIDACION);
        crearActuacion(esal.getId(), TipoActuacion.LIQUIDACION);

        PreviewCertificadoDto preview = previewService.obtenerPreview(esal.getId(), "tester");

        assertThat(preview.getGeneracionHabilitada()).isTrue();
        assertThat(preview.getAlertaEstado()).contains("EN_LIQUIDACION");
    }

    @Test
    void previewCanceladaCompleta_habilitada() {
        Esal esal = crearEsalCompleta("Fundacion Preview Cancelada I2", "PRE-I2-007", EstadoEsal.CANCELADO);
        crearActuacion(esal.getId(), TipoActuacion.CANCELACION);

        PreviewCertificadoDto preview = previewService.obtenerPreview(esal.getId(), "tester");

        assertThat(preview.getGeneracionHabilitada()).isTrue();
        assertThat(preview.getAlertaEstado()).contains("CANCELADO");
    }

    @Test
    void previewInexistente_lanza404() {
        assertThatThrownBy(() -> previewService.obtenerPreview(-999L, "tester"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    private Esal crearEsalCompleta(String nombre, String idSipej, EstadoEsal estado) {
        Esal esal = new Esal();
        esal.setNombre(nombre);
        esal.setIdSipej(idSipej);
        esal.setNit("900123456");
        esal.setDomicilio("Bogota D.C.");
        esal.setCorreoElectronico("contacto@example.org");
        esal.setTerminoDuracion("Indefinido");
        esal.setObjetoSocial("Objeto social de prueba");
        esal.setEstado(estado);
        esal.setEstadoCompletitud(EstadoCompletitud.LISTO_PARA_CERTIFICAR);
        esal.setCreatedAt(LocalDateTime.now());
        esal.setUpdatedAt(LocalDateTime.now());
        esal.setCreatedBy("test");
        esal = esalRepository.save(esal);

        PersoneriaJuridica personeria = new PersoneriaJuridica();
        personeria.setEsalId(esal.getId());
        personeria.setReconocimientoPersoneriaJuridica("Resolucion 001");
        personeria.setFechaReconocimientoPersoneriaJuridica(LocalDate.of(2000, 1, 1));
        personeria.setEntidadQueExpide("Alcaldia Mayor");
        personeriaRepository.save(personeria);

        Nombramiento representante = new Nombramiento();
        representante.setEsalId(esal.getId());
        representante.setTipoNombramiento(TipoNombramiento.REPRESENTANTE_LEGAL);
        representante.setNombre("Representante Legal");
        representante.setNumeroDocumento("123456");
        representante.setFacultadesLimitaciones("Facultades amplias");
        representante.setActaAprueba("Acta 001");
        representante.setFechaActa(LocalDate.of(2024, 1, 1));
        representante.setVigente(Boolean.TRUE);
        nombramientoRepository.save(representante);

        OrganoAdministracion organo = new OrganoAdministracion();
        organo.setEsalId(esal.getId());
        organo.setOrgano("Junta Directiva");
        organo.setMiembro("Miembro Principal");
        organoRepository.save(organo);

        return esal;
    }

    private void crearActuacion(Long esalId, TipoActuacion tipo) {
        ActuacionAdministrativa actuacion = new ActuacionAdministrativa();
        actuacion.setEsalId(esalId);
        actuacion.setTipoActuacion(tipo);
        if (TipoActuacion.SUSPENSION.equals(tipo)) {
            actuacion.setTiempoSuspension("6 meses");
            actuacion.setFechaInicio(LocalDate.of(2024, 1, 1));
        } else if (TipoActuacion.LIQUIDACION.equals(tipo)) {
            actuacion.setActa("Acta liquidacion 001");
            actuacion.setFechaActa(LocalDate.of(2024, 2, 1));
        } else if (TipoActuacion.CANCELACION.equals(tipo)) {
            actuacion.setResolucion("Resolucion cancelacion 001");
            actuacion.setFechaResolucion(LocalDate.of(2024, 3, 1));
        }
        actuacionRepository.save(actuacion);
    }
}
