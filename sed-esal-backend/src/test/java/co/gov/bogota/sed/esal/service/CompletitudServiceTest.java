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
import co.gov.bogota.sed.esal.dto.CompletitudDto;
import co.gov.bogota.sed.esal.repository.ActuacionAdministrativaRepository;
import co.gov.bogota.sed.esal.repository.EsalRepository;
import co.gov.bogota.sed.esal.repository.NombramientoRepository;
import co.gov.bogota.sed.esal.repository.OrganoAdministracionRepository;
import co.gov.bogota.sed.esal.repository.PersoneriaJuridicaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests de integración para CompletitudService.
 *
 * Crea datos de prueba directamente con los repositorios (sin archivo Excel).
 * Verifica los 4 estados y los 3 semáforos de completitud.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CompletitudServiceTest {

    @Autowired
    private CompletitudService completitudService;

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

    // =========================================================================
    // Tests estado ACTIVO
    // =========================================================================

    @Test
    void esalActivaCompleta_esListaParaCertificar() {
        Esal esal = crearEsalCompleta("Fundacion Activa Completa", "SIPEJ-001", EstadoEsal.ACTIVO);

        CompletitudDto resultado = completitudService.calcular(esal.getId());

        assertThat(resultado.getEstadoCompletitud()).isEqualTo(EstadoCompletitud.LISTO_PARA_CERTIFICAR);
        assertThat(resultado.getAdvertenciasBloqueantes()).isEqualTo(0);
        assertThat(resultado.getTotalAdvertencias()).isEqualTo(0);
    }

    @Test
    void esalActivaConNombreFaltante_esIncompletaBloqueante() {
        // Nombre con solo espacios — pasa la restricción NOT NULL de la BD
        // pero el servicio lo detecta como faltante (trim().isEmpty())
        Esal esal = crearEsalCompleta("   ", "SIPEJ-002", EstadoEsal.ACTIVO);

        CompletitudDto resultado = completitudService.calcular(esal.getId());

        assertThat(resultado.getEstadoCompletitud()).isEqualTo(EstadoCompletitud.INCOMPLETO_BLOQUEANTE);
        assertThat(resultado.getAdvertenciasBloqueantes()).isGreaterThan(0);

        boolean tieneAdvertenciaNombre = resultado.getAdvertencias().stream()
                .anyMatch(a -> a.isBloqueante() && "NOMBRE".equals(a.getCampo()));
        assertThat(tieneAdvertenciaNombre).isTrue();
    }

    @Test
    void esalActivaConIdSipejNR_esIncompletaBloqueante() {
        Esal esal = crearEsalCompleta("Fundacion Sin Sipej", "NR", EstadoEsal.ACTIVO);

        CompletitudDto resultado = completitudService.calcular(esal.getId());

        assertThat(resultado.getEstadoCompletitud()).isEqualTo(EstadoCompletitud.INCOMPLETO_BLOQUEANTE);
        assertThat(resultado.getAdvertenciasBloqueantes()).isGreaterThan(0);

        boolean tieneAdvertenciaSipej = resultado.getAdvertencias().stream()
                .anyMatch(a -> a.isBloqueante() && "ID SIPEJ".equals(a.getCampo()));
        assertThat(tieneAdvertenciaSipej).isTrue();
    }

    // =========================================================================
    // Tests estado SUSPENDIDO
    // =========================================================================

    @Test
    void esalSuspendidaSinDatosSuspension_esIncompletaBloqueante() {
        Esal esal = crearEsalCompleta("Fundacion Suspendida Sin Datos", "SIPEJ-003", EstadoEsal.SUSPENDIDO);
        // Sin ActuacionAdministrativa de tipo SUSPENSION

        CompletitudDto resultado = completitudService.calcular(esal.getId());

        assertThat(resultado.getEstadoCompletitud()).isEqualTo(EstadoCompletitud.INCOMPLETO_BLOQUEANTE);
        assertThat(resultado.getAdvertenciasBloqueantes()).isGreaterThan(0);

        boolean tieneAdvertenciaSuspension = resultado.getAdvertencias().stream()
                .anyMatch(a -> a.isBloqueante() && a.getCampo().contains("SUSPENSION"));
        assertThat(tieneAdvertenciaSuspension).isTrue();
    }

    @Test
    void esalSuspendidaConDatosSuspension_esListaParaCertificar() {
        Esal esal = crearEsalCompleta("Fundacion Suspendida Completa", "SIPEJ-004", EstadoEsal.SUSPENDIDO);

        // Crear ActuacionAdministrativa de tipo SUSPENSION con datos completos
        ActuacionAdministrativa suspension = new ActuacionAdministrativa();
        suspension.setEsalId(esal.getId());
        suspension.setTipoActuacion(TipoActuacion.SUSPENSION);
        suspension.setTiempoSuspension("6 meses");
        suspension.setFechaInicio(LocalDate.of(2024, 1, 15));
        actuacionRepository.save(suspension);

        CompletitudDto resultado = completitudService.calcular(esal.getId());

        assertThat(resultado.getEstadoCompletitud()).isEqualTo(EstadoCompletitud.LISTO_PARA_CERTIFICAR);
        assertThat(resultado.getAdvertenciasBloqueantes()).isEqualTo(0);
    }

    // =========================================================================
    // Tests estado EN_LIQUIDACION
    // =========================================================================

    @Test
    void esalEnLiquidacionSinActa_esIncompletaBloqueante() {
        Esal esal = crearEsalCompleta("Fundacion En Liquidacion Sin Acta", "SIPEJ-005", EstadoEsal.EN_LIQUIDACION);
        // Sin ActuacionAdministrativa de tipo LIQUIDACION

        CompletitudDto resultado = completitudService.calcular(esal.getId());

        assertThat(resultado.getEstadoCompletitud()).isEqualTo(EstadoCompletitud.INCOMPLETO_BLOQUEANTE);
        assertThat(resultado.getAdvertenciasBloqueantes()).isGreaterThan(0);

        boolean tieneAdvertenciaLiquidacion = resultado.getAdvertencias().stream()
                .anyMatch(a -> a.isBloqueante() && a.getCampo().contains("LIQUIDACION"));
        assertThat(tieneAdvertenciaLiquidacion).isTrue();
    }

    // =========================================================================
    // Tests estado CANCELADO
    // =========================================================================

    @Test
    void esalCanceladaSinResolucion_esIncompletaBloqueante() {
        Esal esal = crearEsalCompleta("Fundacion Cancelada Sin Resolucion", "SIPEJ-006", EstadoEsal.CANCELADO);
        // Sin ActuacionAdministrativa de tipo CANCELACION

        CompletitudDto resultado = completitudService.calcular(esal.getId());

        assertThat(resultado.getEstadoCompletitud()).isEqualTo(EstadoCompletitud.INCOMPLETO_BLOQUEANTE);
        assertThat(resultado.getAdvertenciasBloqueantes()).isGreaterThan(0);

        boolean tieneAdvertenciaCancelacion = resultado.getAdvertencias().stream()
                .anyMatch(a -> a.isBloqueante() && a.getCampo().contains("CANCELACION"));
        assertThat(tieneAdvertenciaCancelacion).isTrue();
    }

    @Test
    void esalCanceladaConResolucion_esListaParaCertificar() {
        Esal esal = crearEsalCompleta("Fundacion Cancelada Completa", "SIPEJ-007", EstadoEsal.CANCELADO);

        // Crear ActuacionAdministrativa de tipo CANCELACION con datos completos
        ActuacionAdministrativa cancelacion = new ActuacionAdministrativa();
        cancelacion.setEsalId(esal.getId());
        cancelacion.setTipoActuacion(TipoActuacion.CANCELACION);
        cancelacion.setResolucion("Resolucion-2024-001");
        cancelacion.setFechaResolucion(LocalDate.of(2024, 3, 20));
        actuacionRepository.save(cancelacion);

        CompletitudDto resultado = completitudService.calcular(esal.getId());

        assertThat(resultado.getEstadoCompletitud()).isEqualTo(EstadoCompletitud.LISTO_PARA_CERTIFICAR);
        assertThat(resultado.getAdvertenciasBloqueantes()).isEqualTo(0);
    }

    // =========================================================================
    // Helper: crear ESAL completa con todos los campos obligatorios
    // =========================================================================

    /**
     * Crea una ESAL con todos los campos base obligatorios completos:
     * - Esal con datos básicos
     * - PersoneriaJuridica con campos obligatorios
     * - Nombramiento(REPRESENTANTE_LEGAL) con campos obligatorios
     * - OrganoAdministracion con organo y miembro
     *
     * @param nombre  nombre de la ESAL (puede ser vacío para simular faltante)
     * @param idSipej ID SIPEJ (puede ser "NR" para simular faltante)
     * @param estado  estado administrativo de la ESAL
     * @return la Esal guardada
     */
    private Esal crearEsalCompleta(String nombre, String idSipej, EstadoEsal estado) {
        // Esal
        Esal esal = new Esal();
        esal.setNombre(nombre);
        esal.setIdSipej(idSipej);
        esal.setDomicilio("Bogotá D.C.");
        esal.setCorreoElectronico("contacto@fundacion.org");
        esal.setTerminoDuracion("Indefinido");
        esal.setObjetoSocial("Promover la educación y el desarrollo social en Bogotá.");
        esal.setEstado(estado);
        esal.setEstadoCompletitud(EstadoCompletitud.INCOMPLETO_BLOQUEANTE);
        esal.setCreatedAt(LocalDateTime.now());
        esal.setCreatedBy("test");
        esal = esalRepository.save(esal);
        Long esalId = esal.getId();

        // PersoneriaJuridica
        PersoneriaJuridica pj = new PersoneriaJuridica();
        pj.setEsalId(esalId);
        pj.setReconocimientoPersoneriaJuridica("Resolucion-PJ-2000-001");
        pj.setFechaReconocimientoPersoneriaJuridica(LocalDate.of(2000, 6, 15));
        pj.setEntidadQueExpide("Alcaldía Mayor de Bogotá");
        pj.setInscripcion("INS-2000-001");
        pj.setFechaInscripcion(LocalDate.of(2000, 7, 1));
        pj.setEntidadQueInscribio("Cámara de Comercio de Bogotá");
        personeriaRepository.save(pj);

        // Nombramiento - Representante Legal
        Nombramiento rl = new Nombramiento();
        rl.setEsalId(esalId);
        rl.setTipoNombramiento(TipoNombramiento.REPRESENTANTE_LEGAL);
        rl.setNombre("Juan Pérez García");
        rl.setTipoDocumento("CC");
        rl.setNumeroDocumento("12345678");
        rl.setActaAprueba("Acta-2023-001");
        rl.setFechaActa(LocalDate.of(2023, 1, 10));
        rl.setFacultadesLimitaciones("Representar a la entidad ante terceros con plenas facultades.");
        rl.setVigente(Boolean.TRUE);
        nombramientoRepository.save(rl);

        // OrganoAdministracion
        OrganoAdministracion organo = new OrganoAdministracion();
        organo.setEsalId(esalId);
        organo.setOrgano("Junta Directiva");
        organo.setMiembro("María López Rodríguez");
        organo.setCargo("Presidenta");
        organoRepository.save(organo);

        return esal;
    }
}
