# I6 Certificado Narrativo — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Reemplazar el layout tabular del certificado PDF por el formato narrativo de la plantilla institucional oficial, cerrando los GAPs de preambulo juridico, prosa narrativa, tablas de organos estructuradas, formula de cierre y NOTA 1.

**Architecture:** Se introduce `CertificadoNarrativoDto` como DTO dedicado para el PDF, construido por el nuevo `CertificadoAssembler` desde el dominio. `GeneracionService` inyecta el assembler y lo usa justo antes de llamar al `CertificadoPdfService` reescrito. `PreviewService` y `PreviewCertificadoDto` no se tocan.

**Tech Stack:** Java 11, Spring Boot, OpenPDF (com.lowagie), JUnit 5, AssertJ, H2 (tests), @SpringBootTest/@ActiveProfiles("test")/@Transactional.

---

## Mapa de Archivos

| Archivo | Accion |
|---------|--------|
| `dto/CertificadoNarrativoDto.java` | CREAR — DTO estructurado para el PDF |
| `service/CertificadoAssembler.java` | CREAR — construye el DTO desde el dominio |
| `service/CertificadoPdfService.java` | MODIFICAR — reescribir layout narrativo, nueva firma `generar(CertificadoNarrativoDto,...)` |
| `service/GeneracionService.java` | MODIFICAR — inyectar CertificadoAssembler, cambiar llamada a pdfService |
| `test/.../CertificadoAssemblerTest.java` | CREAR — tests de integracion del assembler |
| `test/.../GeneracionServiceTest.java` | MODIFICAR — actualizar VERSION_PLANTILLA y firma de pdfService mock |

Rutas base:
- `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/`
- `sed-esal-backend/src/test/java/co/gov/bogota/sed/esal/`

---

## Task 1: Crear `CertificadoNarrativoDto`

**Files:**
- Create: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/dto/CertificadoNarrativoDto.java`

- [ ] **Step 1: Crear el archivo con todas las clases**

```java
package co.gov.bogota.sed.esal.dto;

import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;

import java.time.LocalDate;
import java.util.List;

public class CertificadoNarrativoDto {

    private String nombre;
    private String idSipej;
    private String nit;
    private String domicilio;
    private String correoElectronico;
    private String terminoDuracion;
    private String objetoSocial;
    private EstadoEsal estado;
    private String alertaEstado;

    // Personeria juridica
    private String resolucionPersoneria;
    private LocalDate fechaResolucion;
    private String entidadQueExpide;
    private String inscripcion;
    private LocalDate fechaInscripcion;

    // Organos
    private List<MiembroDto> representantesLegales;
    private String facultadesRepresentante;
    private List<MiembroDto> miembrosJunta;
    private List<MiembroDto> miembrosAsamblea;
    private List<MiembroDto> revisoresFiscales;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getIdSipej() { return idSipej; }
    public void setIdSipej(String idSipej) { this.idSipej = idSipej; }

    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }

    public String getDomicilio() { return domicilio; }
    public void setDomicilio(String domicilio) { this.domicilio = domicilio; }

    public String getCorreoElectronico() { return correoElectronico; }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico; }

    public String getTerminoDuracion() { return terminoDuracion; }
    public void setTerminoDuracion(String terminoDuracion) { this.terminoDuracion = terminoDuracion; }

    public String getObjetoSocial() { return objetoSocial; }
    public void setObjetoSocial(String objetoSocial) { this.objetoSocial = objetoSocial; }

    public EstadoEsal getEstado() { return estado; }
    public void setEstado(EstadoEsal estado) { this.estado = estado; }

    public String getAlertaEstado() { return alertaEstado; }
    public void setAlertaEstado(String alertaEstado) { this.alertaEstado = alertaEstado; }

    public String getResolucionPersoneria() { return resolucionPersoneria; }
    public void setResolucionPersoneria(String resolucionPersoneria) { this.resolucionPersoneria = resolucionPersoneria; }

    public LocalDate getFechaResolucion() { return fechaResolucion; }
    public void setFechaResolucion(LocalDate fechaResolucion) { this.fechaResolucion = fechaResolucion; }

    public String getEntidadQueExpide() { return entidadQueExpide; }
    public void setEntidadQueExpide(String entidadQueExpide) { this.entidadQueExpide = entidadQueExpide; }

    public String getInscripcion() { return inscripcion; }
    public void setInscripcion(String inscripcion) { this.inscripcion = inscripcion; }

    public LocalDate getFechaInscripcion() { return fechaInscripcion; }
    public void setFechaInscripcion(LocalDate fechaInscripcion) { this.fechaInscripcion = fechaInscripcion; }

    public List<MiembroDto> getRepresentantesLegales() { return representantesLegales; }
    public void setRepresentantesLegales(List<MiembroDto> representantesLegales) { this.representantesLegales = representantesLegales; }

    public String getFacultadesRepresentante() { return facultadesRepresentante; }
    public void setFacultadesRepresentante(String facultadesRepresentante) { this.facultadesRepresentante = facultadesRepresentante; }

    public List<MiembroDto> getMiembrosJunta() { return miembrosJunta; }
    public void setMiembrosJunta(List<MiembroDto> miembrosJunta) { this.miembrosJunta = miembrosJunta; }

    public List<MiembroDto> getMiembrosAsamblea() { return miembrosAsamblea; }
    public void setMiembrosAsamblea(List<MiembroDto> miembrosAsamblea) { this.miembrosAsamblea = miembrosAsamblea; }

    public List<MiembroDto> getRevisoresFiscales() { return revisoresFiscales; }
    public void setRevisoresFiscales(List<MiembroDto> revisoresFiscales) { this.revisoresFiscales = revisoresFiscales; }

    public static class MiembroDto {
        private String nombre;
        private String tipoDocumento;
        private String numeroDocumento;
        private String cargo;
        private String actaNombramiento;
        private String radicadoSed; // siempre null en I6

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }

        public String getTipoDocumento() { return tipoDocumento; }
        public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

        public String getNumeroDocumento() { return numeroDocumento; }
        public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

        public String getCargo() { return cargo; }
        public void setCargo(String cargo) { this.cargo = cargo; }

        public String getActaNombramiento() { return actaNombramiento; }
        public void setActaNombramiento(String actaNombramiento) { this.actaNombramiento = actaNombramiento; }

        public String getRadicadoSed() { return radicadoSed; }
        public void setRadicadoSed(String radicadoSed) { this.radicadoSed = radicadoSed; }
    }
}
```

- [ ] **Step 2: Verificar que compila**

```powershell
Set-Location "C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-backend"
mvn compile -q
```
Esperado: `BUILD SUCCESS` sin errores.

- [ ] **Step 3: Commit**

```powershell
git add sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/dto/CertificadoNarrativoDto.java
git commit -m "feat: add CertificadoNarrativoDto for I6 PDF layout"
```

---

## Task 2: Crear `CertificadoAssembler` con tests

**Files:**
- Create: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/service/CertificadoAssembler.java`
- Create: `sed-esal-backend/src/test/java/co/gov/bogota/sed/esal/service/CertificadoAssemblerTest.java`

- [ ] **Step 1: Escribir el test (TDD — primero)**

Crear `CertificadoAssemblerTest.java`:

```java
package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.*;
import co.gov.bogota.sed.esal.domain.enums.EstadoCompletitud;
import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import co.gov.bogota.sed.esal.domain.enums.TipoNombramiento;
import co.gov.bogota.sed.esal.dto.CertificadoNarrativoDto;
import co.gov.bogota.sed.esal.repository.*;
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

    @Autowired CertificadoAssembler assembler;
    @Autowired EsalRepository esalRepository;
    @Autowired PersoneriaJuridicaRepository personeriaRepository;
    @Autowired NombramientoRepository nombramientoRepository;
    @Autowired OrganoAdministracionRepository organoRepository;

    @Test
    void ensamblar_datosCompletos_mapeaCorrectamente() {
        Esal esal = new Esal();
        esal.setNombre("Fundacion Test I6");
        esal.setIdSipej("I6-001");
        esal.setNit("900111222-1");
        esal.setDomicilio("Calle 1 Sur");
        esal.setCorreoElectronico("test@test.com");
        esal.setTerminoDuracion("INDEFINIDA");
        esal.setObjetoSocial("Objeto social de prueba");
        esal.setEstado(EstadoEsal.ACTIVO);
        esal.setEstadoCompletitud(EstadoCompletitud.COMPLETO);
        esal = esalRepository.save(esal);

        PersoneriaJuridica pj = new PersoneriaJuridica();
        pj.setEsalId(esal.getId());
        pj.setReconocimientoPersoneriaJuridica("055");
        pj.setFechaReconocimientoPersoneriaJuridica(LocalDate.of(2020, 3, 15));
        pj.setEntidadQueExpide("SECRETARIA DE EDUCACION DEL DISTRITO");
        pj.setInscripcion("S100001");
        pj.setFechaInscripcion(LocalDate.of(2015, 6, 10));
        personeriaRepository.save(pj);

        Nombramiento rep = new Nombramiento();
        rep.setEsalId(esal.getId());
        rep.setTipoNombramiento(TipoNombramiento.REPRESENTANTE_LEGAL);
        rep.setNombre("JUAN PEREZ");
        rep.setTipoDocumento("CC");
        rep.setNumeroDocumento("12345678");
        rep.setCargo("Representante Legal");
        rep.setActaAprueba("ACT-001");
        rep.setFacultadesLimitaciones("Facultades estatutarias del representante legal");
        rep.setVigente(true);
        nombramientoRepository.save(rep);

        OrganoAdministracion miembroJunta = new OrganoAdministracion();
        miembroJunta.setEsalId(esal.getId());
        miembroJunta.setOrgano("JUNTA DIRECTIVA");
        miembroJunta.setMiembro("MARIA LOPEZ");
        miembroJunta.setTipoDocumento("CC");
        miembroJunta.setNumeroDocumento("87654321");
        miembroJunta.setCargo("PRESIDENTE");
        miembroJunta.setActaAprueba("ACT-002");
        organoRepository.save(miembroJunta);

        CertificadoNarrativoDto dto = assembler.ensamblar(esal.getId());

        assertThat(dto.getNombre()).isEqualTo("Fundacion Test I6");
        assertThat(dto.getIdSipej()).isEqualTo("I6-001");
        assertThat(dto.getNit()).isEqualTo("900111222-1");
        assertThat(dto.getDomicilio()).isEqualTo("Calle 1 Sur");
        assertThat(dto.getCorreoElectronico()).isEqualTo("test@test.com");
        assertThat(dto.getTerminoDuracion()).isEqualTo("INDEFINIDA");
        assertThat(dto.getObjetoSocial()).isEqualTo("Objeto social de prueba");
        assertThat(dto.getEstado()).isEqualTo(EstadoEsal.ACTIVO);

        assertThat(dto.getResolucionPersoneria()).isEqualTo("055");
        assertThat(dto.getFechaResolucion()).isEqualTo(LocalDate.of(2020, 3, 15));
        assertThat(dto.getEntidadQueExpide()).isEqualTo("SECRETARIA DE EDUCACION DEL DISTRITO");
        assertThat(dto.getInscripcion()).isEqualTo("S100001");
        assertThat(dto.getFechaInscripcion()).isEqualTo(LocalDate.of(2015, 6, 10));

        assertThat(dto.getRepresentantesLegales()).hasSize(1);
        CertificadoNarrativoDto.MiembroDto m = dto.getRepresentantesLegales().get(0);
        assertThat(m.getNombre()).isEqualTo("JUAN PEREZ");
        assertThat(m.getTipoDocumento()).isEqualTo("CC");
        assertThat(m.getNumeroDocumento()).isEqualTo("12345678");
        assertThat(m.getCargo()).isEqualTo("Representante Legal");
        assertThat(m.getActaNombramiento()).isEqualTo("ACT-001");
        assertThat(m.getRadicadoSed()).isNull();

        assertThat(dto.getFacultadesRepresentante()).isEqualTo("Facultades estatutarias del representante legal");

        assertThat(dto.getMiembrosJunta()).hasSize(1);
        assertThat(dto.getMiembrosJunta().get(0).getNombre()).isEqualTo("MARIA LOPEZ");
        assertThat(dto.getMiembrosJunta().get(0).getCargo()).isEqualTo("PRESIDENTE");

        assertThat(dto.getMiembrosAsamblea()).isEmpty();
        assertThat(dto.getRevisoresFiscales()).isEmpty();
    }

    @Test
    void ensamblar_organoAsamblea_separadoDeJunta() {
        Esal esal = new Esal();
        esal.setNombre("Fund Asamblea Test");
        esal.setIdSipej("I6-002");
        esal.setNit("900000001-1");
        esal.setDomicilio("Calle 2");
        esal.setTerminoDuracion("INDEFINIDA");
        esal.setObjetoSocial("Objeto");
        esal.setEstado(EstadoEsal.ACTIVO);
        esal.setEstadoCompletitud(EstadoCompletitud.COMPLETO);
        esal = esalRepository.save(esal);

        OrganoAdministracion miembroAsamblea = new OrganoAdministracion();
        miembroAsamblea.setEsalId(esal.getId());
        miembroAsamblea.setOrgano("ASAMBLEA GENERAL");
        miembroAsamblea.setMiembro("CARLOS RUIZ");
        miembroAsamblea.setCargo("MIEMBRO");
        organoRepository.save(miembroAsamblea);

        OrganoAdministracion miembroJunta = new OrganoAdministracion();
        miembroJunta.setEsalId(esal.getId());
        miembroJunta.setOrgano("JUNTA DIRECTIVA");
        miembroJunta.setMiembro("ANA GOMEZ");
        miembroJunta.setCargo("PRESIDENTA");
        organoRepository.save(miembroJunta);

        CertificadoNarrativoDto dto = assembler.ensamblar(esal.getId());

        assertThat(dto.getMiembrosAsamblea()).hasSize(1);
        assertThat(dto.getMiembrosAsamblea().get(0).getNombre()).isEqualTo("CARLOS RUIZ");
        assertThat(dto.getMiembrosJunta()).hasSize(1);
        assertThat(dto.getMiembrosJunta().get(0).getNombre()).isEqualTo("ANA GOMEZ");
    }

    @Test
    void ensamblar_revisoresFiscales_incluidosSeparados() {
        Esal esal = new Esal();
        esal.setNombre("Fund Revisor Test");
        esal.setIdSipej("I6-003");
        esal.setNit("900000002-2");
        esal.setDomicilio("Calle 3");
        esal.setTerminoDuracion("INDEFINIDA");
        esal.setObjetoSocial("Objeto");
        esal.setEstado(EstadoEsal.ACTIVO);
        esal.setEstadoCompletitud(EstadoCompletitud.COMPLETO);
        esal = esalRepository.save(esal);

        Nombramiento revisor = new Nombramiento();
        revisor.setEsalId(esal.getId());
        revisor.setTipoNombramiento(TipoNombramiento.REVISOR_FISCAL_PRINCIPAL);
        revisor.setNombre("PEDRO FISCAL");
        revisor.setTipoDocumento("CC");
        revisor.setNumeroDocumento("11111111");
        revisor.setCargo("Revisor Fiscal Principal");
        revisor.setActaAprueba("ACT-REV-001");
        revisor.setVigente(true);
        nombramientoRepository.save(revisor);

        CertificadoNarrativoDto dto = assembler.ensamblar(esal.getId());

        assertThat(dto.getRevisoresFiscales()).hasSize(1);
        assertThat(dto.getRevisoresFiscales().get(0).getNombre()).isEqualTo("PEDRO FISCAL");
        assertThat(dto.getRevisoresFiscales().get(0).getCargo()).isEqualTo("Revisor Fiscal Principal");
    }

    @Test
    void ensamblar_sinPersoneria_camposPersoneriaNull() {
        Esal esal = new Esal();
        esal.setNombre("Fund Sin PJ");
        esal.setIdSipej("I6-004");
        esal.setNit("900000003-3");
        esal.setDomicilio("Calle 4");
        esal.setTerminoDuracion("INDEFINIDA");
        esal.setObjetoSocial("Objeto");
        esal.setEstado(EstadoEsal.ACTIVO);
        esal.setEstadoCompletitud(EstadoCompletitud.INCOMPLETO_BLOQUEANTE);
        esal = esalRepository.save(esal);

        CertificadoNarrativoDto dto = assembler.ensamblar(esal.getId());

        assertThat(dto.getResolucionPersoneria()).isNull();
        assertThat(dto.getFechaResolucion()).isNull();
        assertThat(dto.getEntidadQueExpide()).isNull();
        assertThat(dto.getRepresentantesLegales()).isEmpty();
        assertThat(dto.getMiembrosJunta()).isEmpty();
    }
}
```

- [ ] **Step 2: Ejecutar el test para confirmar que falla**

```powershell
Set-Location "C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-backend"
mvn test -pl . -Dtest=CertificadoAssemblerTest -q 2>&1 | Select-String -Pattern "ERROR|FAIL|CertificadoAssembler"
```
Esperado: error de compilacion porque `CertificadoAssembler` no existe aun.

- [ ] **Step 3: Implementar `CertificadoAssembler`**

Crear `CertificadoAssembler.java`:

```java
package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.*;
import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import co.gov.bogota.sed.esal.domain.enums.TipoActuacion;
import co.gov.bogota.sed.esal.domain.enums.TipoNombramiento;
import co.gov.bogota.sed.esal.dto.CertificadoNarrativoDto;
import co.gov.bogota.sed.esal.dto.CertificadoNarrativoDto.MiembroDto;
import co.gov.bogota.sed.esal.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CertificadoAssembler {

    private final EsalRepository esalRepository;
    private final PersoneriaJuridicaRepository personeriaRepository;
    private final NombramientoRepository nombramientoRepository;
    private final OrganoAdministracionRepository organoRepository;
    private final ActuacionAdministrativaRepository actuacionRepository;

    public CertificadoAssembler(EsalRepository esalRepository,
                                 PersoneriaJuridicaRepository personeriaRepository,
                                 NombramientoRepository nombramientoRepository,
                                 OrganoAdministracionRepository organoRepository,
                                 ActuacionAdministrativaRepository actuacionRepository) {
        this.esalRepository       = esalRepository;
        this.personeriaRepository = personeriaRepository;
        this.nombramientoRepository = nombramientoRepository;
        this.organoRepository     = organoRepository;
        this.actuacionRepository  = actuacionRepository;
    }

    public CertificadoNarrativoDto ensamblar(Long esalId) {
        Esal esal = esalRepository.findById(esalId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "ESAL no encontrada: " + esalId));

        List<PersoneriaJuridica> personerias = personeriaRepository.findByEsalId(esalId);
        List<Nombramiento> nombramientos     = nombramientoRepository.findByEsalId(esalId);
        List<OrganoAdministracion> organos   = organoRepository.findByEsalId(esalId);
        List<ActuacionAdministrativa> actuaciones = actuacionRepository.findByEsalId(esalId);

        CertificadoNarrativoDto dto = new CertificadoNarrativoDto();

        // Datos basicos de la ESAL
        dto.setNombre(esal.getNombre());
        dto.setIdSipej(esal.getIdSipej());
        dto.setNit(esal.getNit());
        dto.setDomicilio(esal.getDomicilio());
        dto.setCorreoElectronico(esal.getCorreoElectronico());
        dto.setTerminoDuracion(esal.getTerminoDuracion());
        dto.setObjetoSocial(esal.getObjetoSocial());
        dto.setEstado(esal.getEstado());
        dto.setAlertaEstado(resolverAlertaEstado(esal.getEstado(), actuaciones));

        // Personeria juridica
        if (!personerias.isEmpty()) {
            PersoneriaJuridica pj = personerias.get(0);
            dto.setResolucionPersoneria(pj.getReconocimientoPersoneriaJuridica());
            dto.setFechaResolucion(pj.getFechaReconocimientoPersoneriaJuridica());
            dto.setEntidadQueExpide(pj.getEntidadQueExpide());
            dto.setInscripcion(pj.getInscripcion());
            dto.setFechaInscripcion(pj.getFechaInscripcion());
        }

        // Representantes legales (principal primero, suplente segundo)
        List<TipoNombramiento> tiposRep = Arrays.asList(
                TipoNombramiento.REPRESENTANTE_LEGAL,
                TipoNombramiento.REPRESENTANTE_LEGAL_SUPLENTE);
        List<MiembroDto> representantes = nombramientos.stream()
                .filter(n -> tiposRep.contains(n.getTipoNombramiento()))
                .filter(n -> Boolean.TRUE.equals(n.getVigente()))
                .sorted(Comparator.comparingInt(n -> tiposRep.indexOf(n.getTipoNombramiento())))
                .map(this::toMiembro)
                .collect(Collectors.toList());
        dto.setRepresentantesLegales(representantes);

        // Facultades del representante legal principal
        nombramientos.stream()
                .filter(n -> TipoNombramiento.REPRESENTANTE_LEGAL.equals(n.getTipoNombramiento()))
                .filter(n -> Boolean.TRUE.equals(n.getVigente()))
                .findFirst()
                .ifPresent(n -> dto.setFacultadesRepresentante(n.getFacultadesLimitaciones()));

        // Miembros de junta y asamblea (por organo, case-insensitive)
        dto.setMiembrosJunta(organos.stream()
                .filter(o -> o.getOrgano() != null && o.getOrgano().toUpperCase().contains("JUNTA"))
                .map(this::toMiembroOrgano)
                .collect(Collectors.toList()));

        dto.setMiembrosAsamblea(organos.stream()
                .filter(o -> o.getOrgano() != null && o.getOrgano().toUpperCase().contains("ASAMBLEA"))
                .map(this::toMiembroOrgano)
                .collect(Collectors.toList()));

        // Revisores fiscales
        List<TipoNombramiento> tiposRevisor = Arrays.asList(
                TipoNombramiento.REVISOR_FISCAL_PRINCIPAL,
                TipoNombramiento.REVISOR_FISCAL_SUPLENTE);
        dto.setRevisoresFiscales(nombramientos.stream()
                .filter(n -> tiposRevisor.contains(n.getTipoNombramiento()))
                .filter(n -> Boolean.TRUE.equals(n.getVigente()))
                .sorted(Comparator.comparingInt(n -> tiposRevisor.indexOf(n.getTipoNombramiento())))
                .map(this::toMiembro)
                .collect(Collectors.toList()));

        return dto;
    }

    private MiembroDto toMiembro(Nombramiento n) {
        MiembroDto m = new MiembroDto();
        m.setNombre(n.getNombre());
        m.setTipoDocumento(n.getTipoDocumento());
        m.setNumeroDocumento(n.getNumeroDocumento());
        m.setCargo(n.getCargo());
        m.setActaNombramiento(n.getActaAprueba());
        m.setRadicadoSed(null);
        return m;
    }

    private MiembroDto toMiembroOrgano(OrganoAdministracion o) {
        MiembroDto m = new MiembroDto();
        m.setNombre(o.getMiembro());
        m.setTipoDocumento(o.getTipoDocumento());
        m.setNumeroDocumento(o.getNumeroDocumento());
        m.setCargo(o.getCargo());
        m.setActaNombramiento(o.getActaAprueba());
        m.setRadicadoSed(null);
        return m;
    }

    private String resolverAlertaEstado(EstadoEsal estado, List<ActuacionAdministrativa> actuaciones) {
        if (EstadoEsal.SUSPENDIDO.equals(estado) &&
                actuaciones.stream().anyMatch(a -> TipoActuacion.SUSPENSION.equals(a.getTipoActuacion()))) {
            return "ESAL en estado SUSPENDIDO. El certificado refleja esta situacion.";
        }
        if (EstadoEsal.EN_LIQUIDACION.equals(estado) &&
                actuaciones.stream().anyMatch(a -> TipoActuacion.LIQUIDACION.equals(a.getTipoActuacion()))) {
            return "ESAL en estado EN_LIQUIDACION. El certificado refleja esta situacion.";
        }
        if (EstadoEsal.CANCELADO.equals(estado) &&
                actuaciones.stream().anyMatch(a -> TipoActuacion.CANCELACION.equals(a.getTipoActuacion()))) {
            return "ESAL en estado CANCELADO. El certificado se expide solo para verificacion historica.";
        }
        return null;
    }
}
```

- [ ] **Step 4: Ejecutar los tests del assembler**

```powershell
Set-Location "C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-backend"
mvn test -Dtest=CertificadoAssemblerTest -q
```
Esperado: `Tests run: 4, Failures: 0, Errors: 0`

- [ ] **Step 5: Commit**

```powershell
git add sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/service/CertificadoAssembler.java
git add sed-esal-backend/src/test/java/co/gov/bogota/sed/esal/service/CertificadoAssemblerTest.java
git commit -m "feat: add CertificadoAssembler for I6 narrative PDF DTO"
```

---

## Task 3: Reescribir `CertificadoPdfService` con layout narrativo

**Files:**
- Modify: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/service/CertificadoPdfService.java`

- [ ] **Step 1: Reemplazar el contenido completo del archivo**

```java
package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import co.gov.bogota.sed.esal.dto.CertificadoNarrativoDto;
import co.gov.bogota.sed.esal.dto.CertificadoNarrativoDto.MiembroDto;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CertificadoPdfService {

    static final String VERSION_PLANTILLA = "I6-v1";

    private static final DateTimeFormatter FMT     = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FMT_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final Color COLOR_VERDE   = new Color(0x1B, 0x5E, 0x20);
    private static final Color COLOR_HDR_BG  = new Color(0xE8, 0xF5, 0xE9);
    private static final Color COLOR_GRIS    = new Color(0x60, 0x60, 0x60);
    private static final Color COLOR_ALERTA  = new Color(0xD9, 0x7F, 0x06);
    private static final Color COLOR_TBL_HDR = new Color(0xC8, 0xE6, 0xC9);

    public byte[] generar(CertificadoNarrativoDto narrativo,
                          String numeroCertificado,
                          String firmanteNombre,
                          String firmanteCargo,
                          LocalDateTime fechaExpedicion) throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 60, 60, 70, 70);
        PdfWriter.getInstance(doc, out);
        doc.open();

        Font fTituloGrande  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, COLOR_VERDE);
        Font fTituloMed     = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, COLOR_VERDE);
        Font fBold10        = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);
        Font fBoldVerde10   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, COLOR_VERDE);
        Font fNormal10      = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
        Font fLabel8        = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, COLOR_GRIS);
        Font fLabel8Negro   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.BLACK);
        Font fPie8          = FontFactory.getFont(FontFactory.HELVETICA, 8, COLOR_GRIS);
        Font fAlerta9       = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, COLOR_ALERTA);
        Font fFirmante      = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, COLOR_VERDE);
        Font fTblHdr        = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.BLACK);
        Font fTblVal        = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.BLACK);

        // --- 1. Encabezado institucional ---
        Paragraph enc = new Paragraph();
        enc.add(new Chunk("ALCALDIA MAYOR DE BOGOTA D.C.", fLabel8));
        enc.add(Chunk.NEWLINE);
        enc.add(new Chunk("Secretaria de Educacion del Distrito - SED", fLabel8));
        enc.setAlignment(Element.ALIGN_CENTER);
        doc.add(enc);
        doc.add(new Paragraph(" "));

        // --- 2. Titulo ---
        Paragraph titulo = new Paragraph("CERTIFICADO DE EXISTENCIA Y REPRESENTACION LEGAL", fTituloGrande);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(4);
        doc.add(titulo);

        // --- 3. Numero y fecha ---
        Paragraph numFecha = new Paragraph();
        numFecha.add(new Chunk("No. " + numeroCertificado, fTituloMed));
        numFecha.add(new Chunk("     Expedido: " +
                (fechaExpedicion != null ? fechaExpedicion.format(FMT) : "—"), fLabel8));
        numFecha.setAlignment(Element.ALIGN_CENTER);
        numFecha.setSpacingAfter(14);
        doc.add(numFecha);

        // --- 4. Preambulo ---
        Paragraph preambulo = new Paragraph();
        preambulo.add(new Chunk("LA SUSCRITA DIRECTORA DE INSPECCION Y VIGILANCIA", fBold10));
        preambulo.setAlignment(Element.ALIGN_CENTER);
        preambulo.setSpacingAfter(4);
        doc.add(preambulo);

        Paragraph decretos = new Paragraph(
                "En uso de las facultades concedidas por los Decretos Distritales 479 de 2024 y 650 de 2025",
                fNormal10);
        decretos.setAlignment(Element.ALIGN_CENTER);
        decretos.setSpacingAfter(4);
        doc.add(decretos);

        Paragraph certifica = new Paragraph("CERTIFICA", fBold10);
        certifica.setAlignment(Element.ALIGN_CENTER);
        certifica.setSpacingAfter(14);
        doc.add(certifica);

        // --- 5. Parrafo narrativo inicial ---
        doc.add(parrafoNarrativo(narrativo, fNormal10, fBold10));
        doc.add(new Paragraph(" "));

        // --- 6. Objeto social ---
        if (narrativo.getObjetoSocial() != null) {
            Paragraph tObjetoSocial = new Paragraph(
                    "Que, revisados los estatutos de la entidad, su objeto social es el siguiente:",
                    fNormal10);
            tObjetoSocial.setSpacingAfter(4);
            doc.add(tObjetoSocial);

            Paragraph pObjetoSocial = new Paragraph(
                    "«" + narrativo.getObjetoSocial() + "»", fNormal10);
            pObjetoSocial.setAlignment(Element.ALIGN_JUSTIFIED);
            pObjetoSocial.setSpacingAfter(10);
            doc.add(pObjetoSocial);
        }

        // --- 7. Representacion legal ---
        if (narrativo.getRepresentantesLegales() != null &&
                !narrativo.getRepresentantesLegales().isEmpty()) {
            doc.add(parrafoSeccion("REPRESENTACION LEGAL:", fLabel8Negro));
            doc.add(tablaOrgano(narrativo.getRepresentantesLegales(), fTblHdr, fTblVal));

            if (narrativo.getFacultadesRepresentante() != null) {
                Paragraph tFac = new Paragraph(
                        "FUNCIONES DE LA REPRESENTACION LEGAL:", fLabel8Negro);
                tFac.setSpacingBefore(8);
                tFac.setSpacingAfter(4);
                doc.add(tFac);
                Paragraph pFac = new Paragraph(
                        "«" + narrativo.getFacultadesRepresentante() + "»", fNormal10);
                pFac.setAlignment(Element.ALIGN_JUSTIFIED);
                pFac.setSpacingAfter(8);
                doc.add(pFac);
            }
        }

        // --- 8. Junta Directiva ---
        if (narrativo.getMiembrosJunta() != null && !narrativo.getMiembrosJunta().isEmpty()) {
            doc.add(parrafoSeccion("JUNTA DIRECTIVA:", fLabel8Negro));
            doc.add(tablaOrgano(narrativo.getMiembrosJunta(), fTblHdr, fTblVal));
        }

        // --- 9. Asamblea General ---
        if (narrativo.getMiembrosAsamblea() != null && !narrativo.getMiembrosAsamblea().isEmpty()) {
            doc.add(parrafoSeccion("ASAMBLEA GENERAL:", fLabel8Negro));
            doc.add(tablaOrgano(narrativo.getMiembrosAsamblea(), fTblHdr, fTblVal));
        }

        // --- 10. Revisoria Fiscal ---
        if (narrativo.getRevisoresFiscales() != null && !narrativo.getRevisoresFiscales().isEmpty()) {
            doc.add(parrafoSeccion("REVISORIA FISCAL:", fLabel8Negro));
            doc.add(tablaOrgano(narrativo.getRevisoresFiscales(), fTblHdr, fTblVal));
        }

        // --- 11. Duracion ---
        if (narrativo.getTerminoDuracion() != null) {
            doc.add(new Paragraph(" "));
            Paragraph pDur = new Paragraph(
                    "DURACION: De acuerdo con los estatutos, la entidad tendra una duracion "
                            + narrativo.getTerminoDuracion() + ".", fNormal10);
            pDur.setSpacingAfter(8);
            doc.add(pDur);
        }

        // --- 12. Alerta de estado ---
        if (narrativo.getAlertaEstado() != null) {
            doc.add(new Paragraph(" "));
            Paragraph alerta = new Paragraph(narrativo.getAlertaEstado(), fAlerta9);
            alerta.setSpacingAfter(8);
            doc.add(alerta);
            doc.add(parrafoLegalEstado(narrativo.getEstado(), fNormal10));
        }

        // --- 13. Formula de cierre ---
        doc.add(new Paragraph(" "));
        if (fechaExpedicion != null) {
            doc.add(new Paragraph(FechaEnLetras.formatear(fechaExpedicion.toLocalDate()), fNormal10));
        }

        // --- 14. Firmante ---
        doc.add(new Paragraph(" "));
        doc.add(new Paragraph(" "));
        Paragraph pFirmante = new Paragraph();
        pFirmante.add(new Chunk(firmanteNombre, fFirmante));
        pFirmante.add(Chunk.NEWLINE);
        pFirmante.add(new Chunk(firmanteCargo, fLabel8));
        pFirmante.add(Chunk.NEWLINE);
        pFirmante.add(new Chunk("Secretaria de Educacion del Distrito", fLabel8));
        pFirmante.setSpacingBefore(12);
        doc.add(pFirmante);

        // --- 15. Pie tecnico ---
        doc.add(new Paragraph(" "));
        Paragraph pieTec = new Paragraph(
                "Plantilla: " + VERSION_PLANTILLA + "  |  Generado: " +
                        (fechaExpedicion != null ? fechaExpedicion.format(FMT) : "—"), fPie8);
        pieTec.setAlignment(Element.ALIGN_RIGHT);
        doc.add(pieTec);

        // --- 16. NOTA 1 ---
        doc.add(new Paragraph(" "));
        LineSeparator linea = new LineSeparator(0.5f, 100, COLOR_GRIS, Element.ALIGN_CENTER, -2);
        doc.add(new Chunk(linea));
        Paragraph nota1 = new Paragraph(
                "NOTA 1: Este certificado de existencia y representacion legal NO hace las veces de "
                        + "autorizacion o licencia de funcionamiento de los establecimientos educativos "
                        + "presentes y futuros de propiedad de la entidad.  "
                        + (narrativo.getNombre() != null ? narrativo.getNombre() : "")
                        + " - SIPEJ ID. "
                        + (narrativo.getIdSipej() != null ? narrativo.getIdSipej() : ""), fPie8);
        nota1.setSpacingBefore(4);
        nota1.setAlignment(Element.ALIGN_JUSTIFIED);
        doc.add(nota1);

        doc.close();
        return out.toByteArray();
    }

    // --- Helpers de construccion ---

    private Paragraph parrafoNarrativo(CertificadoNarrativoDto n, Font fNormal, Font fBold) {
        Paragraph p = new Paragraph();
        p.setAlignment(Element.ALIGN_JUSTIFIED);

        p.add(new Chunk("Que, la entidad sin animo de lucro denominada ", fNormal));
        p.add(new Chunk(nvl(n.getNombre()), fBold));

        if (n.getDomicilio() != null) {
            p.add(new Chunk(", cuenta con domicilio en ", fNormal));
            p.add(new Chunk(n.getDomicilio(), fBold));
        }
        if (n.getCorreoElectronico() != null) {
            p.add(new Chunk(", correo electronico ", fNormal));
            p.add(new Chunk(n.getCorreoElectronico(), fBold));
        }
        p.add(new Chunk(", se encuentra registrada en el Sistema de Informacion de Personas "
                + "Juridicas SIPEJ e identificada con ID. ", fNormal));
        p.add(new Chunk(nvl(n.getIdSipej()), fBold));
        p.add(new Chunk(", NIT ", fNormal));
        p.add(new Chunk(nvl(n.getNit()), fBold));
        p.add(new Chunk(", tiene personeria juridica vigente", fNormal));

        if (n.getResolucionPersoneria() != null) {
            p.add(new Chunk(" reconocida mediante la Resolucion No. ", fNormal));
            p.add(new Chunk(n.getResolucionPersoneria(), fBold));
            if (n.getFechaResolucion() != null) {
                p.add(new Chunk(" del " + n.getFechaResolucion().format(FMT_FECHA), fNormal));
            }
            if (n.getEntidadQueExpide() != null) {
                p.add(new Chunk(" expedida por ", fNormal));
                p.add(new Chunk(n.getEntidadQueExpide(), fBold));
            }
        }
        if (n.getInscripcion() != null) {
            p.add(new Chunk(". Inscripcion ", fNormal));
            p.add(new Chunk(n.getInscripcion(), fBold));
            if (n.getFechaInscripcion() != null) {
                p.add(new Chunk(" del " + n.getFechaInscripcion().format(FMT_FECHA), fNormal));
            }
        }
        p.add(new Chunk(".", fNormal));
        return p;
    }

    private Paragraph parrafoSeccion(String titulo, Font f) {
        Paragraph p = new Paragraph(titulo, f);
        p.setSpacingBefore(10);
        p.setSpacingAfter(4);
        return p;
    }

    private PdfPTable tablaOrgano(List<MiembroDto> miembros, Font fHdr, Font fVal) throws Exception {
        PdfPTable tabla = new PdfPTable(5);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{3f, 2f, 2f, 2f, 2f});
        tabla.setSpacingAfter(8);

        String[] cabeceras = {"NOMBRE", "IDENTIFICACION", "CARGO", "ACTA NOMBRAMIENTO", "RADICADO SED"};
        for (String cab : cabeceras) {
            PdfPCell cel = new PdfPCell(new Phrase(cab, fHdr));
            cel.setBackgroundColor(new Color(0xC8, 0xE6, 0xC9));
            cel.setPadding(4);
            tabla.addCell(cel);
        }

        for (MiembroDto m : miembros) {
            tabla.addCell(celda(nvl(m.getNombre()), fVal));
            tabla.addCell(celda(
                    (m.getTipoDocumento() != null ? m.getTipoDocumento() + " " : "") +
                    nvl(m.getNumeroDocumento()), fVal));
            tabla.addCell(celda(nvl(m.getCargo()), fVal));
            tabla.addCell(celda(nvl(m.getActaNombramiento()), fVal));
            tabla.addCell(celda("", fVal)); // RADICADO SED vacio en I6
        }
        return tabla;
    }

    private PdfPCell celda(String texto, Font f) {
        PdfPCell cel = new PdfPCell(new Phrase(texto != null ? texto : "—", f));
        cel.setPadding(4);
        return cel;
    }

    private String nvl(String v) {
        return v != null ? v : "—";
    }

    private Paragraph parrafoLegalEstado(EstadoEsal estado, Font f) {
        String texto = null;
        if (EstadoEsal.SUSPENDIDO.equals(estado)) {
            texto = "La presente entidad se encuentra SUSPENDIDA. Los efectos juridicos de la "
                    + "personeria juridica quedan en suspenso durante el tiempo indicado en la "
                    + "respectiva actuacion administrativa.";
        } else if (EstadoEsal.EN_LIQUIDACION.equals(estado)) {
            texto = "La presente entidad se encuentra EN PROCESO DE DISOLUCION Y LIQUIDACION "
                    + "conforme a las normas aplicables. La personeria juridica subsiste para "
                    + "efectos del proceso liquidatorio.";
        } else if (EstadoEsal.CANCELADO.equals(estado)) {
            texto = "La personeria juridica de la presente entidad ha sido CANCELADA mediante "
                    + "acto administrativo. El presente certificado se expide unicamente para "
                    + "efectos de verificacion historica.";
        }
        if (texto == null) return new Paragraph();
        Paragraph p = new Paragraph(texto, f);
        p.setAlignment(Element.ALIGN_JUSTIFIED);
        p.setSpacingAfter(8);
        return p;
    }

    // --- Helper de fecha en letras ---

    static final class FechaEnLetras {

        private static final String[] UNIDADES = {
            "", "uno", "dos", "tres", "cuatro", "cinco", "seis", "siete",
            "ocho", "nueve", "diez", "once", "doce", "trece", "catorce",
            "quince", "dieciseis", "diecisiete", "dieciocho", "diecinueve",
            "veinte", "veintiuno", "veintidos", "veintitres", "veinticuatro",
            "veinticinco", "veintiseis", "veintisiete", "veintiocho", "veintinueve",
            "treinta", "treinta y uno"
        };

        private static final String[] MESES = {
            "enero", "febrero", "marzo", "abril", "mayo", "junio",
            "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
        };

        static String formatear(java.time.LocalDate fecha) {
            int dia   = fecha.getDayOfMonth();
            int mes   = fecha.getMonthValue();
            int anio  = fecha.getYear();

            String diaLetras  = dia <= 31 ? UNIDADES[dia] : String.valueOf(dia);
            String mesNombre  = MESES[mes - 1];
            String anioLetras = anioEnLetras(anio);

            return String.format(
                    "Se expide en Bogota D.C., a los %s (%d) dias del mes de %s de %s (%d).",
                    diaLetras, dia, mesNombre, anioLetras, anio);
        }

        private static String anioEnLetras(int anio) {
            // Solo cubre el siglo XXI (2001-2099) que es el rango operativo del sistema
            if (anio < 2001 || anio > 2099) return String.valueOf(anio);
            int decenas = anio - 2000;
            if (decenas == 0) return "dos mil";
            if (decenas < 10) return "dos mil " + UNIDADES[decenas];
            if (decenas == 10) return "dos mil diez";
            if (decenas < 20) return "dos mil " + UNIDADES[decenas];
            if (decenas == 20) return "dos mil veinte";
            if (decenas < 30) return "dos mil veinti" + UNIDADES[decenas - 20];
            if (decenas == 30) return "dos mil treinta";
            return "dos mil treinta y " + UNIDADES[decenas - 30];
        }
    }
}
```

- [ ] **Step 2: Verificar que compila**

```powershell
Set-Location "C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-backend"
mvn compile -q
```
Esperado: `BUILD SUCCESS`.

- [ ] **Step 3: Commit**

```powershell
git add sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/service/CertificadoPdfService.java
git commit -m "feat: rewrite CertificadoPdfService with I6 narrative layout"
```

---

## Task 4: Conectar `CertificadoAssembler` en `GeneracionService`

**Files:**
- Modify: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/service/GeneracionService.java`

- [ ] **Step 1: Agregar `CertificadoAssembler` como dependencia inyectada**

Localizar el bloque de campos y el constructor en `GeneracionService.java`. Agregar el campo y el parametro:

En la clase (despues de `private final CertificadoPdfService pdfService;`):
```java
private final CertificadoAssembler certificadoAssembler;
```

En el constructor, agregar el parametro `CertificadoAssembler certificadoAssembler` y la asignacion:
```java
this.certificadoAssembler = certificadoAssembler;
```

El constructor completo queda:
```java
public GeneracionService(PreviewService previewService,
                         NumeracionService numeracionService,
                         FirmanteService firmanteService,
                         CertificadoPdfService pdfService,
                         CertificadoAssembler certificadoAssembler,
                         AlmacenamientoService almacenamientoService,
                         CertificadoRepository certificadoRepository,
                         AuditoriaService auditoriaService) {
    this.previewService       = previewService;
    this.numeracionService    = numeracionService;
    this.firmanteService      = firmanteService;
    this.pdfService           = pdfService;
    this.certificadoAssembler = certificadoAssembler;
    this.almacenamientoService = almacenamientoService;
    this.certificadoRepository = certificadoRepository;
    this.auditoriaService     = auditoriaService;
}
```

- [ ] **Step 2: Cambiar la llamada al PDF service en el metodo `generar`**

Localizar (linea ~79 en el original):
```java
pdfBytes = pdfService.generar(preview, numero, firmante.getNombre(), firmante.getCargo(), ahora);
```

Reemplazar por:
```java
CertificadoNarrativoDto narrativo = certificadoAssembler.ensamblar(esalId);
pdfBytes = pdfService.generar(narrativo, numero, firmante.getNombre(), firmante.getCargo(), ahora);
```

Agregar el import al inicio del archivo (si no existe):
```java
import co.gov.bogota.sed.esal.dto.CertificadoNarrativoDto;
```

- [ ] **Step 3: Verificar compilacion**

```powershell
Set-Location "C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-backend"
mvn compile -q
```
Esperado: `BUILD SUCCESS`.

- [ ] **Step 4: Commit**

```powershell
git add sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/service/GeneracionService.java
git commit -m "feat: wire CertificadoAssembler into GeneracionService for I6"
```

---

## Task 5: Actualizar tests existentes afectados por el cambio de firma

**Files:**
- Modify: `sed-esal-backend/src/test/java/co/gov/bogota/sed/esal/service/GeneracionServiceTest.java`

- [ ] **Step 1: Ejecutar la suite completa para identificar fallos**

```powershell
Set-Location "C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-backend"
mvn test -q 2>&1 | Select-String -Pattern "FAIL|ERROR|Tests run"
```

Identificar que tests fallan por el cambio de firma de `CertificadoPdfService.generar()`.

- [ ] **Step 2: Leer `GeneracionServiceTest.java` para entender los mocks**

Leer el archivo para ver como mockea el `pdfService.generar()` actual.

- [ ] **Step 3: Actualizar los mocks de `pdfService.generar` para la nueva firma**

Buscar en `GeneracionServiceTest.java` cualquier `when(pdfService.generar(...)` o `verify(pdfService).generar(...)`.

Cambiar la firma del mock de:
```java
when(pdfService.generar(any(PreviewCertificadoDto.class), anyString(), anyString(), anyString(), any()))
    .thenReturn(new byte[]{1, 2, 3});
```
a:
```java
when(pdfService.generar(any(CertificadoNarrativoDto.class), anyString(), anyString(), anyString(), any()))
    .thenReturn(new byte[]{1, 2, 3});
```

Agregar mock para `certificadoAssembler.ensamblar(anyLong())` si el test usa mocks:
```java
when(certificadoAssembler.ensamblar(anyLong())).thenReturn(new CertificadoNarrativoDto());
```

Agregar `CertificadoAssembler` como `@Mock` si usa Mockito:
```java
@Mock
private CertificadoAssembler certificadoAssembler;
```

Y agregar al constructor de `GeneracionService` en el `@BeforeEach` o en la construccion del servicio bajo prueba.

Actualizar el import:
```java
import co.gov.bogota.sed.esal.dto.CertificadoNarrativoDto;
import co.gov.bogota.sed.esal.service.CertificadoAssembler;
```

Tambien buscar y actualizar cualquier assert sobre `VERSION_PLANTILLA`:
```java
// Antes
assertThat(saved.getPlantillaVersion()).isEqualTo("I3-v1");
// Despues
assertThat(saved.getPlantillaVersion()).isEqualTo("I6-v1");
```

- [ ] **Step 4: Ejecutar la suite completa**

```powershell
Set-Location "C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-backend"
mvn test -q
```
Esperado: todos los tests en verde, numero igual o mayor al de antes (131+).

- [ ] **Step 5: Commit**

```powershell
git add sed-esal-backend/src/test/java/co/gov/bogota/sed/esal/service/GeneracionServiceTest.java
git commit -m "test: update GeneracionServiceTest for I6 PDF service signature"
```

---

## Task 6: Verificacion funcional manual

**Files:** ninguno — solo verificacion.

- [ ] **Step 1: Levantar el backend**

```powershell
Set-Location "C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-backend"
mvn spring-boot:run -q
```
Esperar hasta ver `Started SedEsalApplication`.

- [ ] **Step 2: Verificar health**

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/sed-esal/actuator/health"
```
Esperado: `{"status":"UP"}`

- [ ] **Step 3: Levantar el frontend**

En otra terminal:
```powershell
Set-Location "C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-angular"
node "C:\Program Files\nodejs\node_modules\npm\bin\npm-cli.js" start
```

- [ ] **Step 4: Generar y descargar un certificado**

1. Abrir Chrome en `http://localhost:4200`.
2. Iniciar sesion como `ADMINISTRADOR` (usuario de prueba configurado en localStorage).
3. Navegar a una ESAL con datos completos.
4. Ir a la pantalla de preview del certificado.
5. Hacer clic en "Generar Certificado".
6. En la pantalla de resultado, hacer clic en "Descargar PDF".

- [ ] **Step 5: Verificar el contenido del PDF descargado**

Abrir el PDF y confirmar que contiene:
- [ ] Bloque "LA SUSCRITA DIRECTORA DE INSPECCION Y VIGILANCIA"
- [ ] Bloque "En uso de las facultades concedidas por los Decretos Distritales 479 de 2024 y 650 de 2025"
- [ ] Bloque "CERTIFICA"
- [ ] Parrafo narrativo con nombre en negrita, domicilio, SIPEJ, NIT, resolucion de personeria
- [ ] Seccion "REPRESENTACION LEGAL:" con tabla de 5 columnas
- [ ] Formula "Se expide en Bogota D.C., a los [dia en letras] ([dia]) dias del mes de [mes] de dos mil [anio en letras] ([anio])."
- [ ] Firmante en verde bold
- [ ] Pie tecnico con "Plantilla: I6-v1"
- [ ] NOTA 1 al pie con linea separadora

- [ ] **Step 6: Commit de documentacion actualizada**

Abrir `docs/GUIA_PRUEBAS_FUNCIONALES.md` y agregar al final una seccion:

```markdown
## Verificacion Layout I6 — Certificado Narrativo

Checklist de verificacion visual del PDF generado con plantilla I6-v1:

- Preambulo: "LA SUSCRITA DIRECTORA DE INSPECCION Y VIGILANCIA"
- Decretos habilitantes: 479 de 2024 y 650 de 2025
- CERTIFICA centrado
- Parrafo narrativo inicial con nombre en negrita
- Tabla REPRESENTACION LEGAL con cabeceras: NOMBRE, IDENTIFICACION, CARGO, ACTA NOMBRAMIENTO, RADICADO SED
- Tabla JUNTA DIRECTIVA (si existen miembros)
- Tabla ASAMBLEA GENERAL (si existen miembros)
- Tabla REVISORIA FISCAL (si existen revisores fiscales)
- Duracion: parrafo narrativo
- Formula de cierre: "Se expide en Bogota D.C., a los..."
- Firmante en verde bold
- Pie tecnico: "Plantilla: I6-v1"
- NOTA 1 con linea separadora
```

Actualizar `README.md`: cambiar la fila de I5 de "Completado" a sin cambio, y agregar la fila de I6:

```markdown
| I6 | Completado | Fidelidad del certificado PDF a plantilla oficial |
```

```powershell
git add docs/GUIA_PRUEBAS_FUNCIONALES.md README.md
git commit -m "docs: add I6 PDF layout verification guide and update README"
```

---

## Task 7: Suite de tests final y push

- [ ] **Step 1: Ejecutar suite completa una ultima vez**

```powershell
Set-Location "C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-backend"
mvn test -q
```
Esperado: `BUILD SUCCESS`, todos los tests pasan.

- [ ] **Step 2: Build Angular**

```powershell
Set-Location "C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-angular"
node "C:\Program Files\nodejs\node_modules\npm\bin\npm-cli.js" run build
```
Esperado: `BUILD SUCCESS` (no hay cambios Angular pero se confirma que no se rompio nada).

- [ ] **Step 3: Push**

```powershell
Set-Location "C:\Users\jmep2\Downloads\SED\ProyectoESAL"
git push
```

---

## Resumen de Commits

| Commit | Descripcion |
|--------|-------------|
| 1 | `feat: add CertificadoNarrativoDto for I6 PDF layout` |
| 2 | `feat: add CertificadoAssembler for I6 narrative PDF DTO` |
| 3 | `feat: rewrite CertificadoPdfService with I6 narrative layout` |
| 4 | `feat: wire CertificadoAssembler into GeneracionService for I6` |
| 5 | `test: update GeneracionServiceTest for I6 PDF service signature` |
| 6 | `docs: add I6 PDF layout verification guide and update README` |
