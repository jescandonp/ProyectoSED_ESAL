package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.Esal;
import co.gov.bogota.sed.esal.domain.Nombramiento;
import co.gov.bogota.sed.esal.domain.OrganoAdministracion;
import co.gov.bogota.sed.esal.domain.PersoneriaJuridica;
import co.gov.bogota.sed.esal.domain.enums.TipoActuacion;
import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import co.gov.bogota.sed.esal.domain.enums.TipoNombramiento;
import co.gov.bogota.sed.esal.domain.enums.TipoAdvertencia;
import co.gov.bogota.sed.esal.dto.CancelacionEsalDto;
import co.gov.bogota.sed.esal.dto.EsalInformacionPrincipalDto;
import co.gov.bogota.sed.esal.dto.MantenimientoEsalDto;
import co.gov.bogota.sed.esal.dto.NombramientoDto;
import co.gov.bogota.sed.esal.dto.OrganoAdministracionDto;
import co.gov.bogota.sed.esal.dto.PersoneriaJuridicaDto;
import co.gov.bogota.sed.esal.dto.ReactivacionEsalDto;
import co.gov.bogota.sed.esal.repository.ActuacionAdministrativaRepository;
import co.gov.bogota.sed.esal.repository.AdvertenciaCompletitudRepository;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EsalMaintenanceServiceTest {

    @Autowired
    private EsalMaintenanceService maintenanceService;

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

    @Autowired
    private AdvertenciaCompletitudRepository advertenciaRepository;

    @Test
    void crearEsalDesdeMantenimientoGuardaInformacionPrincipalYRecalculaCompletitud() {
        EsalInformacionPrincipalDto dto = new EsalInformacionPrincipalDto();
        dto.setNombre("Fundacion I5");
        dto.setIdSipej("I5-001");
        dto.setNit("900111222-3");
        dto.setDomicilio("Bogota D.C.");
        dto.setCorreoElectronico("i5@fundacion.org");
        dto.setTerminoDuracion("Indefinido");
        dto.setObjetoSocial("Objeto social I5");
        dto.setEstado(EstadoEsal.ACTIVO);

        MantenimientoEsalDto result = maintenanceService.crear(dto, "admin-i5");

        assertThat(result.getId()).isNotNull();
        assertThat(result.getInformacionPrincipal().getNombre()).isEqualTo("Fundacion I5");
        assertThat(result.getInformacionPrincipal().getEstado()).isEqualTo(EstadoEsal.ACTIVO);

        Esal saved = esalRepository.findById(result.getId()).orElseThrow(AssertionError::new);
        assertThat(saved.getCreatedBy()).isEqualTo("admin-i5");
        assertThat(saved.getEstadoCompletitud()).isNotNull();
    }

    @Test
    void actualizarInformacionPrincipalNoPermiteCancelarPorEdicionLibre() {
        Esal esal = new Esal();
        esal.setNombre("Fundacion Estado Libre");
        esal.setIdSipej("I5-002");
        esal.setEstado(EstadoEsal.ACTIVO);
        esal = esalRepository.save(esal);

        EsalInformacionPrincipalDto dto = new EsalInformacionPrincipalDto();
        dto.setNombre("Fundacion Estado Libre Actualizada");
        dto.setEstado(EstadoEsal.CANCELADO);

        MantenimientoEsalDto result = maintenanceService.actualizarInformacionPrincipal(esal.getId(), dto, "admin-i5");

        assertThat(result.getInformacionPrincipal().getNombre()).isEqualTo("Fundacion Estado Libre Actualizada");
        assertThat(result.getInformacionPrincipal().getEstado()).isEqualTo(EstadoEsal.ACTIVO);
    }

    @Test
    void guardarPersoneriaHaceUpsertUnoAUnoPorEsal() {
        Esal esal = new Esal();
        esal.setNombre("Fundacion Personeria");
        esal.setIdSipej("I5-003");
        esal = esalRepository.save(esal);

        PersoneriaJuridicaDto primera = new PersoneriaJuridicaDto();
        primera.setReconocimientoPersoneriaJuridica("Resolucion 001");
        primera.setFechaReconocimientoPersoneriaJuridica(LocalDate.of(2024, 1, 10));
        primera.setEntidadQueExpide("SED");

        maintenanceService.guardarPersoneriaJuridica(esal.getId(), primera, "admin-i5");

        PersoneriaJuridicaDto segunda = new PersoneriaJuridicaDto();
        segunda.setReconocimientoPersoneriaJuridica("Resolucion 002");
        segunda.setFechaReconocimientoPersoneriaJuridica(LocalDate.of(2025, 2, 20));
        segunda.setEntidadQueExpide("Secretaria de Educacion");
        segunda.setInscripcion("Inscripcion 123");

        MantenimientoEsalDto result = maintenanceService.guardarPersoneriaJuridica(esal.getId(), segunda, "admin-i5");

        List<PersoneriaJuridica> registros = personeriaRepository.findByEsalId(esal.getId());
        assertThat(registros).hasSize(1);
        assertThat(registros.get(0).getReconocimientoPersoneriaJuridica()).isEqualTo("Resolucion 002");
        assertThat(result.getPersoneriaJuridica().getInscripcion()).isEqualTo("Inscripcion 123");
    }

    @Test
    void crearEditarYListarRepresentanteLegal() {
        Esal esal = new Esal();
        esal.setNombre("Fundacion Representante");
        esal.setIdSipej("I5-004");
        esal = esalRepository.save(esal);

        NombramientoDto representante = new NombramientoDto();
        representante.setTipoNombramiento(TipoNombramiento.REPRESENTANTE_LEGAL);
        representante.setNombre("Representante Original");
        representante.setTipoDocumento("CC");
        representante.setNumeroDocumento("123");
        representante.setCargo("Representante legal");
        representante.setActaAprueba("Acta 1");
        representante.setFechaActa(LocalDate.of(2025, 1, 15));
        representante.setFacultadesLimitaciones("Facultades iniciales");
        representante.setVigente(Boolean.TRUE);

        NombramientoDto creado = maintenanceService.crearRepresentante(esal.getId(), representante, "admin-i5");

        NombramientoDto actualizacion = new NombramientoDto();
        actualizacion.setTipoNombramiento(TipoNombramiento.REPRESENTANTE_LEGAL_SUPLENTE);
        actualizacion.setNombre("Representante Actualizado");
        actualizacion.setNumeroDocumento("456");
        actualizacion.setVigente(Boolean.FALSE);

        NombramientoDto actualizado = maintenanceService.actualizarRepresentante(
                esal.getId(), creado.getId(), actualizacion, "admin-i5");

        List<NombramientoDto> representantes = maintenanceService.listarRepresentantes(esal.getId());
        List<Nombramiento> entidades = nombramientoRepository.findByEsalId(esal.getId());

        assertThat(actualizado.getNombre()).isEqualTo("Representante Actualizado");
        assertThat(actualizado.getTipoNombramiento()).isEqualTo(TipoNombramiento.REPRESENTANTE_LEGAL_SUPLENTE);
        assertThat(actualizado.getVigente()).isFalse();
        assertThat(representantes).hasSize(1);
        assertThat(entidades).hasSize(1);
        assertThat(entidades.get(0).getNumeroDocumento()).isEqualTo("456");
    }

    @Test
    void representanteLegalRechazaTiposFueraDelAlcanceI5() {
        Esal esal = new Esal();
        esal.setNombre("Fundacion Tipo No Permitido");
        esal.setIdSipej("I5-005");
        esal = esalRepository.save(esal);

        NombramientoDto revisor = new NombramientoDto();
        revisor.setTipoNombramiento(TipoNombramiento.REVISOR_FISCAL_PRINCIPAL);
        revisor.setNombre("Revisor Fiscal");

        Long esalId = esal.getId();
        assertThatThrownBy(() -> maintenanceService.crearRepresentante(esalId, revisor, "admin-i5"))
                .hasMessageContaining("tipoNombramiento");
    }

    @Test
    void crearEditarYListarMiembroOrganoAdministracion() {
        Esal esal = new Esal();
        esal.setNombre("Fundacion Organo");
        esal.setIdSipej("I5-006");
        esal = esalRepository.save(esal);

        OrganoAdministracionDto miembro = new OrganoAdministracionDto();
        miembro.setOrgano("Junta Directiva");
        miembro.setMiembro("Miembro Original");
        miembro.setCargo("Presidente");
        miembro.setTipoDocumento("CC");
        miembro.setNumeroDocumento("789");
        miembro.setActaAprueba("Acta 10");
        miembro.setFechaActa(LocalDate.of(2025, 3, 10));
        miembro.setFacultadesLimitaciones("Facultades del organo");

        OrganoAdministracionDto creado = maintenanceService.crearMiembroOrgano(esal.getId(), miembro, "admin-i5");

        OrganoAdministracionDto actualizacion = new OrganoAdministracionDto();
        actualizacion.setMiembro("Miembro Actualizado");
        actualizacion.setCargo("Secretario");
        actualizacion.setNumeroDocumento("987");
        actualizacion.setActaAclaratoria("Acta aclaratoria 11");
        actualizacion.setFechaActaAclaratoria(LocalDate.of(2025, 4, 11));

        OrganoAdministracionDto actualizado = maintenanceService.actualizarMiembroOrgano(
                esal.getId(), creado.getId(), actualizacion, "admin-i5");

        List<OrganoAdministracionDto> miembros = maintenanceService.listarMiembrosOrgano(esal.getId());
        List<OrganoAdministracion> entidades = organoRepository.findByEsalId(esal.getId());

        assertThat(actualizado.getMiembro()).isEqualTo("Miembro Actualizado");
        assertThat(actualizado.getCargo()).isEqualTo("Secretario");
        assertThat(actualizado.getOrgano()).isEqualTo("Junta Directiva");
        assertThat(miembros).hasSize(1);
        assertThat(entidades).hasSize(1);
        assertThat(entidades.get(0).getNumeroDocumento()).isEqualTo("987");
    }

    @Test
    void actualizarMiembroOrganoNoPermiteCruzarEsal() {
        Esal esalUno = new Esal();
        esalUno.setNombre("Fundacion Organo Uno");
        esalUno.setIdSipej("I5-007");
        esalUno = esalRepository.save(esalUno);

        Esal esalDos = new Esal();
        esalDos.setNombre("Fundacion Organo Dos");
        esalDos.setIdSipej("I5-008");
        esalDos = esalRepository.save(esalDos);

        OrganoAdministracionDto miembro = new OrganoAdministracionDto();
        miembro.setOrgano("Consejo Directivo");
        miembro.setMiembro("Miembro Asociado");

        OrganoAdministracionDto creado = maintenanceService.crearMiembroOrgano(esalUno.getId(), miembro, "admin-i5");
        OrganoAdministracionDto actualizacion = new OrganoAdministracionDto();
        actualizacion.setMiembro("Cruce no permitido");

        Long esalDosId = esalDos.getId();
        assertThatThrownBy(() -> maintenanceService.actualizarMiembroOrgano(
                esalDosId, creado.getId(), actualizacion, "admin-i5"))
                .hasMessageContaining("Miembro de organo no encontrado");
    }

    @Test
    void cancelarEsalCreaActuacionCambiaEstadoYAdvierteSiFaltaPdf() {
        Esal esal = new Esal();
        esal.setNombre("Fundacion Cancelacion");
        esal.setIdSipej("I5-009");
        esal.setEstado(EstadoEsal.ACTIVO);
        esal = esalRepository.save(esal);

        CancelacionEsalDto dto = new CancelacionEsalDto();
        dto.setResolucion("Resolucion Cancelacion 001");
        dto.setFechaResolucion(LocalDate.of(2026, 5, 20));
        dto.setMotivo("Cancelacion por solicitud formal");

        MantenimientoEsalDto result = maintenanceService.cancelar(esal.getId(), dto, "admin-i5");

        Esal actualizada = esalRepository.findById(esal.getId()).orElseThrow(AssertionError::new);

        assertThat(result.getInformacionPrincipal().getEstado()).isEqualTo(EstadoEsal.CANCELADO);
        assertThat(actualizada.getEstado()).isEqualTo(EstadoEsal.CANCELADO);
        assertThat(actuacionRepository.findByEsalId(esal.getId()))
                .anySatisfy(actuacion -> {
                    assertThat(actuacion.getTipoActuacion()).isEqualTo(TipoActuacion.CANCELACION);
                    assertThat(actuacion.getResolucion()).isEqualTo("Resolucion Cancelacion 001");
                    assertThat(actuacion.getFechaResolucion()).isEqualTo(LocalDate.of(2026, 5, 20));
                    assertThat(actuacion.getMotivo()).isEqualTo("Cancelacion por solicitud formal");
                });
        assertThat(advertenciaRepository.findByEsalId(esal.getId()))
                .anySatisfy(advertencia -> {
                    assertThat(advertencia.getTipo()).isEqualTo(TipoAdvertencia.DOCUMENTO_REQUERIDO_FALTANTE);
                    assertThat(advertencia.getCampo()).isEqualTo("PDF SOPORTE CANCELACION");
                    assertThat(advertencia.getBloqueante()).isFalse();
                });
    }

    @Test
    void cancelarEsalValidaResolucionFechaYMotivo() {
        Esal esal = new Esal();
        esal.setNombre("Fundacion Cancelacion Validaciones");
        esal.setIdSipej("I5-010");
        esal.setEstado(EstadoEsal.ACTIVO);
        esal = esalRepository.save(esal);

        CancelacionEsalDto sinResolucion = new CancelacionEsalDto();
        sinResolucion.setFechaResolucion(LocalDate.of(2026, 5, 20));
        sinResolucion.setMotivo("Motivo informado");

        CancelacionEsalDto sinFecha = new CancelacionEsalDto();
        sinFecha.setResolucion("Resolucion 002");
        sinFecha.setMotivo("Motivo informado");

        CancelacionEsalDto sinMotivo = new CancelacionEsalDto();
        sinMotivo.setResolucion("Resolucion 003");
        sinMotivo.setFechaResolucion(LocalDate.of(2026, 5, 20));

        Long esalId = esal.getId();
        assertThatThrownBy(() -> maintenanceService.cancelar(esalId, sinResolucion, "admin-i5"))
                .hasMessageContaining("resolucion");
        assertThatThrownBy(() -> maintenanceService.cancelar(esalId, sinFecha, "admin-i5"))
                .hasMessageContaining("fechaResolucion");
        assertThatThrownBy(() -> maintenanceService.cancelar(esalId, sinMotivo, "admin-i5"))
                .hasMessageContaining("motivo");
    }

    @Test
    void reactivarEsalCanceladaExigeMotivoYCambiaEstadoSinEliminarCancelacion() {
        Esal esal = new Esal();
        esal.setNombre("Fundacion Reactivacion");
        esal.setIdSipej("I5-011");
        esal.setEstado(EstadoEsal.ACTIVO);
        esal = esalRepository.save(esal);

        CancelacionEsalDto cancelacion = new CancelacionEsalDto();
        cancelacion.setResolucion("Resolucion Reactivacion 001");
        cancelacion.setFechaResolucion(LocalDate.of(2026, 5, 20));
        cancelacion.setMotivo("Cancelacion previa");
        maintenanceService.cancelar(esal.getId(), cancelacion, "admin-i5");

        ReactivacionEsalDto reactivacion = new ReactivacionEsalDto();
        reactivacion.setMotivo("Reactivacion por acto administrativo posterior");

        MantenimientoEsalDto result = maintenanceService.reactivar(esal.getId(), reactivacion, "admin-i5");

        Esal actualizada = esalRepository.findById(esal.getId()).orElseThrow(AssertionError::new);

        assertThat(result.getInformacionPrincipal().getEstado()).isEqualTo(EstadoEsal.ACTIVO);
        assertThat(actualizada.getEstado()).isEqualTo(EstadoEsal.ACTIVO);
        assertThat(actuacionRepository.findByEsalId(esal.getId()))
                .anyMatch(actuacion -> TipoActuacion.CANCELACION.equals(actuacion.getTipoActuacion()));
    }

    @Test
    void reactivarPermiteEstadoDestinoDistintoACanceladoYRechazaCasosInvalidos() {
        Esal cancelada = new Esal();
        cancelada.setNombre("Fundacion Reactivacion Destino");
        cancelada.setIdSipej("I5-012");
        cancelada.setEstado(EstadoEsal.CANCELADO);
        cancelada = esalRepository.save(cancelada);

        Esal activa = new Esal();
        activa.setNombre("Fundacion No Cancelada");
        activa.setIdSipej("I5-013");
        activa.setEstado(EstadoEsal.ACTIVO);
        activa = esalRepository.save(activa);

        ReactivacionEsalDto suspendida = new ReactivacionEsalDto();
        suspendida.setEstadoDestino(EstadoEsal.SUSPENDIDO);
        suspendida.setMotivo("Reactivacion hacia suspension controlada");

        MantenimientoEsalDto result = maintenanceService.reactivar(cancelada.getId(), suspendida, "admin-i5");

        ReactivacionEsalDto sinMotivo = new ReactivacionEsalDto();
        sinMotivo.setEstadoDestino(EstadoEsal.ACTIVO);

        ReactivacionEsalDto destinoCancelado = new ReactivacionEsalDto();
        destinoCancelado.setEstadoDestino(EstadoEsal.CANCELADO);
        destinoCancelado.setMotivo("Destino invalido");

        Long activaId = activa.getId();
        Long canceladaId = cancelada.getId();

        assertThat(result.getInformacionPrincipal().getEstado()).isEqualTo(EstadoEsal.SUSPENDIDO);
        assertThatThrownBy(() -> maintenanceService.reactivar(activaId, suspendida, "admin-i5"))
                .hasMessageContaining("CANCELADO");
        assertThatThrownBy(() -> maintenanceService.reactivar(canceladaId, sinMotivo, "admin-i5"))
                .hasMessageContaining("motivo");
        assertThatThrownBy(() -> maintenanceService.reactivar(canceladaId, destinoCancelado, "admin-i5"))
                .hasMessageContaining("estadoDestino");
    }
}
