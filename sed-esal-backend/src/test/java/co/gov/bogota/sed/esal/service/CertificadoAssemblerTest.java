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
import co.gov.bogota.sed.esal.dto.CertificadoNarrativoDto;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CertificadoAssemblerTest {

    @Autowired
    private CertificadoAssembler assembler;

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
    void ensamblar_datosCompletos_mapeaCertificadoNarrativo() {
        Esal esal = crearEsal("Fundacion Test I6", "I6-001", EstadoEsal.ACTIVO);

        PersoneriaJuridica pj = new PersoneriaJuridica();
        pj.setEsalId(esal.getId());
        pj.setReconocimientoPersoneriaJuridica("055");
        pj.setFechaReconocimientoPersoneriaJuridica(LocalDate.of(2020, 3, 15));
        pj.setEntidadQueExpide("SECRETARIA DE EDUCACION DEL DISTRITO");
        pj.setInscripcion("S100001");
        pj.setFechaInscripcion(LocalDate.of(2015, 6, 10));
        personeriaRepository.save(pj);

        Nombramiento representante = new Nombramiento();
        representante.setEsalId(esal.getId());
        representante.setTipoNombramiento(TipoNombramiento.REPRESENTANTE_LEGAL);
        representante.setNombre("JUAN PEREZ");
        representante.setTipoDocumento("CC");
        representante.setNumeroDocumento("12345678");
        representante.setCargo("Representante Legal");
        representante.setActaAprueba("ACT-001");
        representante.setFacultadesLimitaciones("Facultades estatutarias del representante legal");
        representante.setVigente(true);
        nombramientoRepository.save(representante);

        OrganoAdministracion junta = new OrganoAdministracion();
        junta.setEsalId(esal.getId());
        junta.setOrgano("JUNTA DIRECTIVA");
        junta.setMiembro("MARIA LOPEZ");
        junta.setTipoDocumento("CC");
        junta.setNumeroDocumento("87654321");
        junta.setCargo("PRESIDENTE");
        junta.setActaAprueba("ACT-002");
        organoRepository.save(junta);

        CertificadoNarrativoDto dto = assembler.ensamblar(esal.getId());

        assertThat(dto.getNombre()).isEqualTo("Fundacion Test I6");
        assertThat(dto.getIdSipej()).isEqualTo("I6-001");
        assertThat(dto.getNit()).isEqualTo("900111222-1");
        assertThat(dto.getResolucionPersoneria()).isEqualTo("055");
        assertThat(dto.getFechaResolucion()).isEqualTo(LocalDate.of(2020, 3, 15));
        assertThat(dto.getEntidadQueExpide()).isEqualTo("SECRETARIA DE EDUCACION DEL DISTRITO");
        assertThat(dto.getInscripcion()).isEqualTo("S100001");
        assertThat(dto.getFechaInscripcion()).isEqualTo(LocalDate.of(2015, 6, 10));
        assertThat(dto.getRepresentantesLegales()).hasSize(1);
        assertThat(dto.getRepresentantesLegales().get(0).getNombre()).isEqualTo("JUAN PEREZ");
        assertThat(dto.getRepresentantesLegales().get(0).getRadicadoSed()).isNull();
        assertThat(dto.getFacultadesRepresentante()).isEqualTo("Facultades estatutarias del representante legal");
        assertThat(dto.getMiembrosJunta()).hasSize(1);
        assertThat(dto.getMiembrosJunta().get(0).getNombre()).isEqualTo("MARIA LOPEZ");
        assertThat(dto.getMiembrosAsamblea()).isEmpty();
        assertThat(dto.getRevisoresFiscales()).isEmpty();
    }

    @Test
    void ensamblar_separaAsambleaJuntaYRevisoriaFiscal() {
        Esal esal = crearEsal("Fundacion Organos I6", "I6-002", EstadoEsal.ACTIVO);

        OrganoAdministracion asamblea = new OrganoAdministracion();
        asamblea.setEsalId(esal.getId());
        asamblea.setOrgano("ASAMBLEA GENERAL");
        asamblea.setMiembro("CARLOS RUIZ");
        asamblea.setCargo("MIEMBRO");
        organoRepository.save(asamblea);

        OrganoAdministracion junta = new OrganoAdministracion();
        junta.setEsalId(esal.getId());
        junta.setOrgano("JUNTA DIRECTIVA");
        junta.setMiembro("ANA GOMEZ");
        junta.setCargo("PRESIDENTA");
        organoRepository.save(junta);

        Nombramiento revisor = new Nombramiento();
        revisor.setEsalId(esal.getId());
        revisor.setTipoNombramiento(TipoNombramiento.REVISOR_FISCAL_PRINCIPAL);
        revisor.setNombre("PEDRO FISCAL");
        revisor.setTipoDocumento("CC");
        revisor.setNumeroDocumento("11111111");
        revisor.setCargo("Revisor Fiscal Principal");
        revisor.setVigente(true);
        nombramientoRepository.save(revisor);

        CertificadoNarrativoDto dto = assembler.ensamblar(esal.getId());

        assertThat(dto.getMiembrosAsamblea()).extracting(CertificadoNarrativoDto.MiembroDto::getNombre)
                .containsExactly("CARLOS RUIZ");
        assertThat(dto.getMiembrosJunta()).extracting(CertificadoNarrativoDto.MiembroDto::getNombre)
                .containsExactly("ANA GOMEZ");
        assertThat(dto.getRevisoresFiscales()).extracting(CertificadoNarrativoDto.MiembroDto::getNombre)
                .containsExactly("PEDRO FISCAL");
    }

    @Test
    void ensamblar_estadoSuspendidoConActuacion_incluyeAlerta() {
        Esal esal = crearEsal("Fundacion Suspendida I6", "I6-003", EstadoEsal.SUSPENDIDO);

        ActuacionAdministrativa actuacion = new ActuacionAdministrativa();
        actuacion.setEsalId(esal.getId());
        actuacion.setTipoActuacion(TipoActuacion.SUSPENSION);
        actuacionRepository.save(actuacion);

        CertificadoNarrativoDto dto = assembler.ensamblar(esal.getId());

        assertThat(dto.getAlertaEstado()).contains("SUSPENDIDO");
    }

    @Test
    void ensamblar_sinPersoneria_dejaCamposPersoneriaNull() {
        Esal esal = crearEsal("Fundacion Sin Personeria I6", "I6-004", EstadoEsal.ACTIVO);

        CertificadoNarrativoDto dto = assembler.ensamblar(esal.getId());

        assertThat(dto.getResolucionPersoneria()).isNull();
        assertThat(dto.getFechaResolucion()).isNull();
        assertThat(dto.getEntidadQueExpide()).isNull();
        assertThat(dto.getRepresentantesLegales()).isEmpty();
        assertThat(dto.getMiembrosJunta()).isEmpty();
    }

    private Esal crearEsal(String nombre, String idSipej, EstadoEsal estado) {
        Esal esal = new Esal();
        esal.setNombre(nombre);
        esal.setIdSipej(idSipej);
        esal.setNit("900111222-1");
        esal.setDomicilio("Calle 1 Sur");
        esal.setCorreoElectronico("test@test.com");
        esal.setTerminoDuracion("INDEFINIDA");
        esal.setObjetoSocial("Objeto social de prueba");
        esal.setEstado(estado);
        esal.setEstadoCompletitud(EstadoCompletitud.LISTO_PARA_CERTIFICAR);
        return esalRepository.save(esal);
    }
}
