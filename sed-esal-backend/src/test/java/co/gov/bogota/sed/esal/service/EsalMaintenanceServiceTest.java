package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.Esal;
import co.gov.bogota.sed.esal.domain.PersoneriaJuridica;
import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import co.gov.bogota.sed.esal.dto.EsalInformacionPrincipalDto;
import co.gov.bogota.sed.esal.dto.MantenimientoEsalDto;
import co.gov.bogota.sed.esal.dto.PersoneriaJuridicaDto;
import co.gov.bogota.sed.esal.repository.EsalRepository;
import co.gov.bogota.sed.esal.repository.PersoneriaJuridicaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
}
