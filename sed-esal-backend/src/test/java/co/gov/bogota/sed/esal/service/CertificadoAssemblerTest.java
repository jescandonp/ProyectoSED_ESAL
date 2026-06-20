package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.ActuacionAdministrativa;
import co.gov.bogota.sed.esal.domain.DocumentoSoporte;
import co.gov.bogota.sed.esal.domain.Esal;
import co.gov.bogota.sed.esal.domain.Nombramiento;
import co.gov.bogota.sed.esal.domain.OrganoAdministracion;
import co.gov.bogota.sed.esal.domain.PersoneriaJuridica;
import co.gov.bogota.sed.esal.domain.enums.CertificadoPlantilla;
import co.gov.bogota.sed.esal.domain.enums.EstadoCompletitud;
import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import co.gov.bogota.sed.esal.domain.enums.EstadoValidacionDocumento;
import co.gov.bogota.sed.esal.domain.enums.SubtipoDocumentoSoporte;
import co.gov.bogota.sed.esal.domain.enums.TipoActuacion;
import co.gov.bogota.sed.esal.domain.enums.TipoDocumentoSoporte;
import co.gov.bogota.sed.esal.domain.enums.TipoNombramiento;
import co.gov.bogota.sed.esal.dto.CertificadoNarrativoDto;
import co.gov.bogota.sed.esal.repository.ActuacionAdministrativaRepository;
import co.gov.bogota.sed.esal.repository.DocumentoSoporteRepository;
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

    @Autowired
    private DocumentoSoporteRepository documentoRepository;

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
    void ensamblar_estadoSuspendido_asignaPlantillaSuspendida() {
        Esal esal = crearEsal("Fundacion Suspendida I10", "I10-SUS-001", EstadoEsal.SUSPENDIDO);

        CertificadoNarrativoDto dto = assembler.ensamblar(esal.getId());

        assertThat(dto.getPlantilla()).isEqualTo(CertificadoPlantilla.EYRL_SUSPENDIDA);
    }

    @Test
    void ensamblar_enLiquidacionConDocumentoTermino_asignaPlantillaLiquidacionTermino() {
        Esal esal = crearEsal("Fundacion Liquidacion Termino I10", "I10-LIQ-001", EstadoEsal.EN_LIQUIDACION);
        crearDocumento(esal.getId(), TipoDocumentoSoporte.LIQUIDACION,
                SubtipoDocumentoSoporte.TERMINO_DURACION, "OF-LIQ-001");

        CertificadoNarrativoDto dto = assembler.ensamblar(esal.getId());

        assertThat(dto.getPlantilla()).isEqualTo(CertificadoPlantilla.EYRL_LIQUIDACION_TERMINO_DURACION);
        assertThat(dto.getDocumentoPlantillaReferencia()).isEqualTo("OF-LIQ-001");
        assertThat(dto.getDocumentoPlantillaSubtipo()).isEqualTo(SubtipoDocumentoSoporte.TERMINO_DURACION);
    }

    @Test
    void ensamblar_canceladaConDocumentoOrdenAutoridad_asignaPlantillaCanceladaAutoridad() {
        Esal esal = crearEsal("Fundacion Cancelada Autoridad I10", "I10-CAN-001", EstadoEsal.CANCELADO);
        crearDocumento(esal.getId(), TipoDocumentoSoporte.CANCELACION,
                SubtipoDocumentoSoporte.ORDEN_AUTORIDAD, "RES-CAN-001");

        CertificadoNarrativoDto dto = assembler.ensamblar(esal.getId());

        assertThat(dto.getPlantilla()).isEqualTo(CertificadoPlantilla.EYRL_CANCELADA_ORDEN_AUTORIDAD);
        assertThat(dto.getDocumentoPlantillaReferencia()).isEqualTo("RES-CAN-001");
        assertThat(dto.getDocumentoPlantillaSubtipo()).isEqualTo(SubtipoDocumentoSoporte.ORDEN_AUTORIDAD);
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

    private void crearDocumento(Long esalId,
                                TipoDocumentoSoporte tipo,
                                SubtipoDocumentoSoporte subtipo,
                                String referencia) {
        DocumentoSoporte documento = new DocumentoSoporte();
        documento.setEsalId(esalId);
        documento.setTipoDocumental(tipo);
        documento.setSubtipoDocumental(subtipo);
        documento.setReferenciaActo(referencia);
        documento.setFechaActo(LocalDate.of(2026, 6, 20));
        documento.setNombreArchivo("documento-i10.pdf");
        documento.setContentType("application/pdf");
        documento.setTamanoBytes(100L);
        documento.setRutaAlmacenamiento("test/documento-i10.pdf");
        documento.setEstadoValidacion(EstadoValidacionDocumento.PENDIENTE);
        documento.setVigente(Boolean.TRUE);
        documentoRepository.save(documento);
    }
}
