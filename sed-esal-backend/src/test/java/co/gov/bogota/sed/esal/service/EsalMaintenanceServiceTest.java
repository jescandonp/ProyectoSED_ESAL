package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.Esal;
import co.gov.bogota.sed.esal.domain.Nombramiento;
import co.gov.bogota.sed.esal.domain.PersoneriaJuridica;
import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import co.gov.bogota.sed.esal.domain.enums.TipoNombramiento;
import co.gov.bogota.sed.esal.dto.EsalInformacionPrincipalDto;
import co.gov.bogota.sed.esal.dto.MantenimientoEsalDto;
import co.gov.bogota.sed.esal.dto.NombramientoDto;
import co.gov.bogota.sed.esal.dto.PersoneriaJuridicaDto;
import co.gov.bogota.sed.esal.repository.EsalRepository;
import co.gov.bogota.sed.esal.repository.NombramientoRepository;
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
}
