package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.Esal;
import co.gov.bogota.sed.esal.domain.Nombramiento;
import co.gov.bogota.sed.esal.domain.OrganoAdministracion;
import co.gov.bogota.sed.esal.domain.PersoneriaJuridica;
import co.gov.bogota.sed.esal.domain.enums.EstadoCompletitud;
import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import co.gov.bogota.sed.esal.domain.enums.TipoNombramiento;
import co.gov.bogota.sed.esal.dto.BusquedaDetalleDto;
import co.gov.bogota.sed.esal.dto.BusquedaResultadoDto;
import co.gov.bogota.sed.esal.dto.PageDto;
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
class BusquedaServiceTest {

    @Autowired
    private BusquedaService busquedaService;

    @Autowired
    private EsalRepository esalRepository;

    @Autowired
    private PersoneriaJuridicaRepository personeriaRepository;

    @Autowired
    private NombramientoRepository nombramientoRepository;

    @Autowired
    private OrganoAdministracionRepository organoRepository;

    @Test
    void buscarSinFiltros_retornaPaginado() {
        crearEsal("Fundacion Alfa", "SIPEJ-I2-001", "900001", EstadoEsal.ACTIVO,
                EstadoCompletitud.LISTO_PARA_CERTIFICAR);
        crearEsal("Corporacion Beta", "SIPEJ-I2-002", "900002", EstadoEsal.SUSPENDIDO,
                EstadoCompletitud.INCOMPLETO_BLOQUEANTE);

        PageDto<BusquedaResultadoDto> resultado = busquedaService.buscar(
                null, null, null, null, null, null, 0, 20, "tester");

        assertThat(resultado.getTotalElements()).isGreaterThanOrEqualTo(2);
        assertThat(resultado.getContent()).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void buscarPorNombreExacto() {
        crearEsal("Fundacion Nombre Exacto I2", "SIPEJ-I2-003", "900003", EstadoEsal.ACTIVO,
                EstadoCompletitud.LISTO_PARA_CERTIFICAR);

        PageDto<BusquedaResultadoDto> resultado = busquedaService.buscar(
                null, null, "Fundacion Nombre Exacto I2", null, null, null, 0, 20, "tester");

        assertThat(resultado.getContent()).extracting(BusquedaResultadoDto::getNombre)
                .contains("Fundacion Nombre Exacto I2");
    }

    @Test
    void buscarPorNombreParcial() {
        crearEsal("Fundacion Busqueda Parcial I2", "SIPEJ-I2-004", "900004", EstadoEsal.ACTIVO,
                EstadoCompletitud.LISTO_PARA_CERTIFICAR);

        PageDto<BusquedaResultadoDto> resultado = busquedaService.buscar(
                null, null, "busqueda parcial", null, null, null, 0, 20, "tester");

        assertThat(resultado.getContent()).extracting(BusquedaResultadoDto::getNombre)
                .contains("Fundacion Busqueda Parcial I2");
    }

    @Test
    void buscarPorIdSipejExacto() {
        crearEsal("Fundacion Sipej Exacto", "SIPEJ-I2-005", "900005", EstadoEsal.ACTIVO,
                EstadoCompletitud.LISTO_PARA_CERTIFICAR);

        PageDto<BusquedaResultadoDto> resultado = busquedaService.buscar(
                null, "SIPEJ-I2-005", null, null, null, null, 0, 20, "tester");

        assertThat(resultado.getContent()).extracting(BusquedaResultadoDto::getIdSipej)
                .contains("SIPEJ-I2-005");
    }

    @Test
    void buscarPorIdSipejParcial() {
        crearEsal("Fundacion Sipej Parcial", "SIPEJ-I2-006", "900006", EstadoEsal.ACTIVO,
                EstadoCompletitud.LISTO_PARA_CERTIFICAR);

        PageDto<BusquedaResultadoDto> resultado = busquedaService.buscar(
                null, "i2-006", null, null, null, null, 0, 20, "tester");

        assertThat(resultado.getContent()).extracting(BusquedaResultadoDto::getIdSipej)
                .contains("SIPEJ-I2-006");
    }

    @Test
    void buscarSinCoincidencias_retornaVacio() {
        PageDto<BusquedaResultadoDto> resultado = busquedaService.buscar(
                null, null, "Nombre Inexistente I2", null, null, null, 0, 20, "tester");

        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }

    @Test
    void buscarPorEstado_filtrado() {
        crearEsal("Fundacion Activa I2", "SIPEJ-I2-007", "900007", EstadoEsal.ACTIVO,
                EstadoCompletitud.LISTO_PARA_CERTIFICAR);
        crearEsal("Fundacion Cancelada I2", "SIPEJ-I2-008", "900008", EstadoEsal.CANCELADO,
                EstadoCompletitud.LISTO_PARA_CERTIFICAR);

        PageDto<BusquedaResultadoDto> resultado = busquedaService.buscar(
                "Fundacion", null, null, null, EstadoEsal.CANCELADO, null, 0, 20, "tester");

        assertThat(resultado.getContent()).extracting(BusquedaResultadoDto::getEstado)
                .contains(EstadoEsal.CANCELADO);
        assertThat(resultado.getContent()).extracting(BusquedaResultadoDto::getEstado)
                .doesNotContain(EstadoEsal.ACTIVO);
    }

    @Test
    void buscarPorCompletitud_filtrado() {
        crearEsal("Fundacion Lista I2", "SIPEJ-I2-009", "900009", EstadoEsal.ACTIVO,
                EstadoCompletitud.LISTO_PARA_CERTIFICAR);
        crearEsal("Fundacion Incompleta I2", "SIPEJ-I2-010", "900010", EstadoEsal.ACTIVO,
                EstadoCompletitud.INCOMPLETO_BLOQUEANTE);

        PageDto<BusquedaResultadoDto> resultado = busquedaService.buscar(
                "Fundacion", null, null, null, null,
                EstadoCompletitud.INCOMPLETO_BLOQUEANTE, 0, 20, "tester");

        assertThat(resultado.getContent()).extracting(BusquedaResultadoDto::getEstadoCompletitud)
                .contains(EstadoCompletitud.INCOMPLETO_BLOQUEANTE);
        assertThat(resultado.getContent()).extracting(BusquedaResultadoDto::getEstadoCompletitud)
                .doesNotContain(EstadoCompletitud.LISTO_PARA_CERTIFICAR);
    }

    @Test
    void obtenerDetalle_retornaSecciones() {
        Esal esal = crearEsalCompleta("Fundacion Detalle I2", "SIPEJ-I2-011", EstadoEsal.ACTIVO);

        BusquedaDetalleDto detalle = busquedaService.obtenerDetalle(esal.getId(), "tester");

        assertThat(detalle.getEsalId()).isEqualTo(esal.getId());
        assertThat(detalle.getPersoneria()).isNotNull();
        assertThat(detalle.getNombramientos()).isNotEmpty();
        assertThat(detalle.getOrganos()).isNotEmpty();
        assertThat(detalle.getCompletitud()).isNotNull();
    }

    @Test
    void obtenerDetalleInexistente_lanza404() {
        assertThatThrownBy(() -> busquedaService.obtenerDetalle(-999L, "tester"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    private Esal crearEsal(String nombre, String idSipej, String nit, EstadoEsal estado,
                           EstadoCompletitud estadoCompletitud) {
        Esal esal = new Esal();
        esal.setNombre(nombre);
        esal.setIdSipej(idSipej);
        esal.setNit(nit);
        esal.setDomicilio("Bogota D.C.");
        esal.setCorreoElectronico("contacto@example.org");
        esal.setTerminoDuracion("Indefinido");
        esal.setObjetoSocial("Objeto social de prueba");
        esal.setEstado(estado);
        esal.setEstadoCompletitud(estadoCompletitud);
        esal.setCreatedAt(LocalDateTime.now());
        esal.setUpdatedAt(LocalDateTime.now());
        esal.setCreatedBy("test");
        return esalRepository.save(esal);
    }

    private Esal crearEsalCompleta(String nombre, String idSipej, EstadoEsal estado) {
        Esal esal = crearEsal(nombre, idSipej, "901000", estado, EstadoCompletitud.LISTO_PARA_CERTIFICAR);

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
        nombramientoRepository.save(representante);

        OrganoAdministracion organo = new OrganoAdministracion();
        organo.setEsalId(esal.getId());
        organo.setOrgano("Junta Directiva");
        organo.setMiembro("Miembro Principal");
        organoRepository.save(organo);

        return esal;
    }
}
