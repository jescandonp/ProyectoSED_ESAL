# I10 Seleccion De Plantilla EYRL Por Estado Y Documento Vigente Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implementar seleccion explicita de plantilla EYRL para certificados segun estado de ESAL y documento vigente I9, conservando plantilla default para los demas casos.

**Architecture:** I10 agrega un enum `CertificadoPlantilla` y un `CertificadoTemplateSelector` testeable que decide la variante antes de generar el PDF. `CertificadoAssembler` incorporara la variante y metadatos del documento vigente I9 al `CertificadoNarrativoDto`; `CertificadoPdfService` consumira esa variante para renderizar bloques especificos sin cambiar endpoints ni autorizacion.

**Tech Stack:** Spring Boot 2.7, Java 8, JPA/Hibernate, Maven, JUnit 5, AssertJ, OpenPDF, Angular 20 solo para build de regresion.

---

> Estado: pendiente de aprobacion.
> Spec: `docs/specs/2026-06-20-sed-esal-i10-spec.md`.
> Metodologia: SDD Spec-Anchored.
> Plantillas fuente: `Documentos_Referencia/Iteracion/Plantilla Certificado EYRL*.docx`.

## File Structure

Backend:

- Create: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/domain/enums/CertificadoPlantilla.java`
  - Responsabilidad: catalogo tecnico de variantes I10, version visible y textos clave.
- Create: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/service/CertificadoTemplateSelector.java`
  - Responsabilidad: decidir plantilla desde estado y documentos vigentes I9.
- Modify: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/dto/CertificadoNarrativoDto.java`
  - Responsabilidad: transportar variante seleccionada y metadatos documentales al PDF.
- Modify: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/service/CertificadoAssembler.java`
  - Responsabilidad: consultar documentos vigentes I9 e invocar selector.
- Modify: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/service/CertificadoPdfService.java`
  - Responsabilidad: renderizar bloques especificos por variante y version I10.

Tests:

- Create: `sed-esal-backend/src/test/java/co/gov/bogota/sed/esal/service/CertificadoTemplateSelectorTest.java`
- Modify: `sed-esal-backend/src/test/java/co/gov/bogota/sed/esal/service/CertificadoAssemblerTest.java`
- Modify: `sed-esal-backend/src/test/java/co/gov/bogota/sed/esal/service/CertificadoPdfServiceTest.java`
- Modify if needed: `sed-esal-backend/src/test/java/co/gov/bogota/sed/esal/service/GeneracionServiceTest.java`

Docs:

- Create at implementation start: `docs/plans/2026-06-20-sed-esal-i10-execution-log.md`
- Create milestone handoffs under: `docs/Handoff/`
- Update at closure: `README.md`, `docs/ARRANQUE.md`, `docs/GUIA_PRUEBAS_FUNCIONALES.md`

## Tasks

### Task 1: RED Selector De Plantilla I10

**Files:**
- Create: `sed-esal-backend/src/test/java/co/gov/bogota/sed/esal/service/CertificadoTemplateSelectorTest.java`
- Create during implementation start: `docs/plans/2026-06-20-sed-esal-i10-execution-log.md`

- [ ] **Step 1: Create execution log**

Create `docs/plans/2026-06-20-sed-esal-i10-execution-log.md`:

```markdown
# Execution Log I10 - Seleccion De Plantilla EYRL Por Estado Y Documento Vigente

> Estado: en ejecucion.
> Fecha: 2026-06-20.
> Spec: `docs/specs/2026-06-20-sed-esal-i10-spec.md`.
> Plan: `docs/plans/2026-06-20-sed-esal-i10-plan.md`.

## Contexto Inicial

I9 completado. I10 implementa seleccion explicita de plantilla de certificado EYRL por estado y documento vigente I9.

## Evidencia De Verificacion

Se registraran aqui RED/GREEN, suites finales, WAR, build Angular y handoffs por hitos.
```

- [ ] **Step 2: Add RED selector tests**

Create `CertificadoTemplateSelectorTest.java`:

```java
package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.DocumentoSoporte;
import co.gov.bogota.sed.esal.domain.enums.CertificadoPlantilla;
import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import co.gov.bogota.sed.esal.domain.enums.SubtipoDocumentoSoporte;
import co.gov.bogota.sed.esal.domain.enums.TipoDocumentoSoporte;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class CertificadoTemplateSelectorTest {

    private final CertificadoTemplateSelector selector = new CertificadoTemplateSelector();

    @Test
    void seleccionar_suspendida_retornaPlantillaSuspendida() {
        assertThat(selector.seleccionar(EstadoEsal.SUSPENDIDO, Collections.emptyList()))
                .isEqualTo(CertificadoPlantilla.EYRL_SUSPENDIDA);
    }

    @Test
    void seleccionar_enLiquidacionConTramiteCancelacionVoluntaria_retornaPlantillaLiquidacionTramite() {
        assertThat(selector.seleccionar(EstadoEsal.EN_LIQUIDACION,
                Collections.singletonList(documento(TipoDocumentoSoporte.LIQUIDACION,
                        SubtipoDocumentoSoporte.TRAMITE_CANCELACION_VOLUNTARIA, true))))
                .isEqualTo(CertificadoPlantilla.EYRL_LIQUIDACION_TRAMITE_CANCELACION_VOLUNTARIA);
    }

    @Test
    void seleccionar_enLiquidacionConTerminoDuracion_retornaPlantillaLiquidacionTermino() {
        assertThat(selector.seleccionar(EstadoEsal.EN_LIQUIDACION,
                Collections.singletonList(documento(TipoDocumentoSoporte.LIQUIDACION,
                        SubtipoDocumentoSoporte.TERMINO_DURACION, true))))
                .isEqualTo(CertificadoPlantilla.EYRL_LIQUIDACION_TERMINO_DURACION);
    }

    @Test
    void seleccionar_canceladaVoluntariamente_retornaPlantillaCanceladaVoluntaria() {
        assertThat(selector.seleccionar(EstadoEsal.CANCELADO,
                Collections.singletonList(documento(TipoDocumentoSoporte.CANCELACION,
                        SubtipoDocumentoSoporte.CANCELACION_VOLUNTARIA, true))))
                .isEqualTo(CertificadoPlantilla.EYRL_CANCELADA_VOLUNTARIAMENTE);
    }

    @Test
    void seleccionar_canceladaPorOrdenAutoridad_retornaPlantillaCanceladaAutoridad() {
        assertThat(selector.seleccionar(EstadoEsal.CANCELADO,
                Collections.singletonList(documento(TipoDocumentoSoporte.CANCELACION,
                        SubtipoDocumentoSoporte.ORDEN_AUTORIDAD, true))))
                .isEqualTo(CertificadoPlantilla.EYRL_CANCELADA_ORDEN_AUTORIDAD);
    }

    @Test
    void seleccionar_activaSinReglaEspecial_retornaDefault() {
        assertThat(selector.seleccionar(EstadoEsal.ACTIVO, Collections.emptyList()))
                .isEqualTo(CertificadoPlantilla.EYRL_DEFAULT);
    }

    @Test
    void seleccionar_ignoraDocumentosNoVigentes() {
        assertThat(selector.seleccionar(EstadoEsal.CANCELADO,
                Collections.singletonList(documento(TipoDocumentoSoporte.CANCELACION,
                        SubtipoDocumentoSoporte.CANCELACION_VOLUNTARIA, false))))
                .isEqualTo(CertificadoPlantilla.EYRL_DEFAULT);
    }

    @Test
    void seleccionar_prefiereDocumentoVigenteCompatibleEntreHistoricos() {
        DocumentoSoporte historico = documento(TipoDocumentoSoporte.CANCELACION,
                SubtipoDocumentoSoporte.CANCELACION_VOLUNTARIA, false);
        DocumentoSoporte vigente = documento(TipoDocumentoSoporte.CANCELACION,
                SubtipoDocumentoSoporte.ORDEN_AUTORIDAD, true);

        assertThat(selector.seleccionar(EstadoEsal.CANCELADO, Arrays.asList(historico, vigente)))
                .isEqualTo(CertificadoPlantilla.EYRL_CANCELADA_ORDEN_AUTORIDAD);
    }

    private DocumentoSoporte documento(TipoDocumentoSoporte tipo,
                                       SubtipoDocumentoSoporte subtipo,
                                       boolean vigente) {
        DocumentoSoporte documento = new DocumentoSoporte();
        documento.setTipoDocumental(tipo);
        documento.setSubtipoDocumental(subtipo);
        documento.setVigente(vigente);
        return documento;
    }
}
```

- [ ] **Step 3: Run RED**

Run:

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-backend
mvn test "-Dtest=CertificadoTemplateSelectorTest"
```

Expected: compilation fails because `CertificadoPlantilla` and `CertificadoTemplateSelector` do not exist yet.

- [ ] **Step 4: Update execution log**

Record the RED command and exact failure class in `docs/plans/2026-06-20-sed-esal-i10-execution-log.md`.

### Task 2: GREEN Selector And Variant Contract

**Files:**
- Create: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/domain/enums/CertificadoPlantilla.java`
- Create: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/service/CertificadoTemplateSelector.java`
- Modify: `sed-esal-backend/src/test/java/co/gov/bogota/sed/esal/service/CertificadoTemplateSelectorTest.java`

- [ ] **Step 1: Create enum**

Create `CertificadoPlantilla.java`:

```java
package co.gov.bogota.sed.esal.domain.enums;

public enum CertificadoPlantilla {
    EYRL_DEFAULT("I10-EYRL-DEFAULT-v1"),
    EYRL_SUSPENDIDA("I10-EYRL-SUSPENDIDA-v1"),
    EYRL_LIQUIDACION_TRAMITE_CANCELACION_VOLUNTARIA("I10-EYRL-LIQUIDACION-TRAMITE-v1"),
    EYRL_LIQUIDACION_TERMINO_DURACION("I10-EYRL-LIQUIDACION-TERMINO-v1"),
    EYRL_CANCELADA_VOLUNTARIAMENTE("I10-EYRL-CANCELADA-VOLUNTARIA-v1"),
    EYRL_CANCELADA_ORDEN_AUTORIDAD("I10-EYRL-CANCELADA-AUTORIDAD-v1");

    private final String version;

    CertificadoPlantilla(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }
}
```

- [ ] **Step 2: Create selector**

Create `CertificadoTemplateSelector.java`:

```java
package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.DocumentoSoporte;
import co.gov.bogota.sed.esal.domain.enums.CertificadoPlantilla;
import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import co.gov.bogota.sed.esal.domain.enums.SubtipoDocumentoSoporte;
import co.gov.bogota.sed.esal.domain.enums.TipoDocumentoSoporte;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class CertificadoTemplateSelector {

    public CertificadoPlantilla seleccionar(EstadoEsal estado, List<DocumentoSoporte> documentos) {
        if (EstadoEsal.SUSPENDIDO.equals(estado)) {
            return CertificadoPlantilla.EYRL_SUSPENDIDA;
        }
        List<DocumentoSoporte> documentosSeguros = documentos == null ? Collections.emptyList() : documentos;
        if (EstadoEsal.EN_LIQUIDACION.equals(estado)) {
            if (existeVigente(documentosSeguros, TipoDocumentoSoporte.LIQUIDACION,
                    SubtipoDocumentoSoporte.TRAMITE_CANCELACION_VOLUNTARIA)) {
                return CertificadoPlantilla.EYRL_LIQUIDACION_TRAMITE_CANCELACION_VOLUNTARIA;
            }
            if (existeVigente(documentosSeguros, TipoDocumentoSoporte.LIQUIDACION,
                    SubtipoDocumentoSoporte.TERMINO_DURACION)) {
                return CertificadoPlantilla.EYRL_LIQUIDACION_TERMINO_DURACION;
            }
        }
        if (EstadoEsal.CANCELADO.equals(estado)) {
            if (existeVigente(documentosSeguros, TipoDocumentoSoporte.CANCELACION,
                    SubtipoDocumentoSoporte.CANCELACION_VOLUNTARIA)) {
                return CertificadoPlantilla.EYRL_CANCELADA_VOLUNTARIAMENTE;
            }
            if (existeVigente(documentosSeguros, TipoDocumentoSoporte.CANCELACION,
                    SubtipoDocumentoSoporte.ORDEN_AUTORIDAD)) {
                return CertificadoPlantilla.EYRL_CANCELADA_ORDEN_AUTORIDAD;
            }
        }
        return CertificadoPlantilla.EYRL_DEFAULT;
    }

    private boolean existeVigente(List<DocumentoSoporte> documentos,
                                  TipoDocumentoSoporte tipo,
                                  SubtipoDocumentoSoporte subtipo) {
        return documentos.stream()
                .anyMatch(documento -> Boolean.TRUE.equals(documento.getVigente())
                        && tipo.equals(documento.getTipoDocumental())
                        && subtipo.equals(documento.getSubtipoDocumental()));
    }
}
```

- [ ] **Step 3: Run GREEN selector**

Run:

```powershell
mvn test "-Dtest=CertificadoTemplateSelectorTest"
```

Expected: all selector tests pass.

- [ ] **Step 4: Update execution log and handoff**

Append Task 2 result to execution log and create `docs/Handoff/handoff-20260620-i10-task2-closed-retake-task3.md` with:

```markdown
# Handoff - I10 Task 2 Cerrada

Retake: continuar en Task 3 del plan I10.

Evidencia: `mvn test "-Dtest=CertificadoTemplateSelectorTest"` en verde.

Estado: selector de plantilla I10 implementado y testeado; pendiente integrar en assembler y PDF.
```

### Task 3: Integrar Selector En Assembler Y DTO

**Files:**
- Modify: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/dto/CertificadoNarrativoDto.java`
- Modify: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/service/CertificadoAssembler.java`
- Modify: `sed-esal-backend/src/test/java/co/gov/bogota/sed/esal/service/CertificadoAssemblerTest.java`

- [ ] **Step 1: Add RED assembler tests**

Add imports in `CertificadoAssemblerTest.java`:

```java
import co.gov.bogota.sed.esal.domain.DocumentoSoporte;
import co.gov.bogota.sed.esal.domain.enums.CertificadoPlantilla;
import co.gov.bogota.sed.esal.domain.enums.SubtipoDocumentoSoporte;
import co.gov.bogota.sed.esal.domain.enums.TipoDocumentoSoporte;
import co.gov.bogota.sed.esal.repository.DocumentoSoporteRepository;
```

Add field:

```java
@Autowired
private DocumentoSoporteRepository documentoRepository;
```

Add tests:

```java
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
```

Add helper:

```java
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
    documento.setEstadoValidacion("PENDIENTE");
    documento.setVigente(Boolean.TRUE);
    documentoRepository.save(documento);
}
```

- [ ] **Step 2: Run RED assembler**

Run:

```powershell
mvn test "-Dtest=CertificadoAssemblerTest"
```

Expected: compilation fails because `CertificadoNarrativoDto.getPlantilla()`, `getDocumentoPlantillaReferencia()` and `getDocumentoPlantillaSubtipo()` do not exist.

- [ ] **Step 3: Extend DTO**

In `CertificadoNarrativoDto.java`, add imports:

```java
import co.gov.bogota.sed.esal.domain.enums.CertificadoPlantilla;
import co.gov.bogota.sed.esal.domain.enums.SubtipoDocumentoSoporte;
```

Add fields:

```java
private CertificadoPlantilla plantilla = CertificadoPlantilla.EYRL_DEFAULT;
private String documentoPlantillaReferencia;
private LocalDate documentoPlantillaFechaActo;
private SubtipoDocumentoSoporte documentoPlantillaSubtipo;
```

Add getters/setters:

```java
public CertificadoPlantilla getPlantilla() { return plantilla; }
public void setPlantilla(CertificadoPlantilla plantilla) { this.plantilla = plantilla; }

public String getDocumentoPlantillaReferencia() { return documentoPlantillaReferencia; }
public void setDocumentoPlantillaReferencia(String documentoPlantillaReferencia) {
    this.documentoPlantillaReferencia = documentoPlantillaReferencia;
}

public LocalDate getDocumentoPlantillaFechaActo() { return documentoPlantillaFechaActo; }
public void setDocumentoPlantillaFechaActo(LocalDate documentoPlantillaFechaActo) {
    this.documentoPlantillaFechaActo = documentoPlantillaFechaActo;
}

public SubtipoDocumentoSoporte getDocumentoPlantillaSubtipo() { return documentoPlantillaSubtipo; }
public void setDocumentoPlantillaSubtipo(SubtipoDocumentoSoporte documentoPlantillaSubtipo) {
    this.documentoPlantillaSubtipo = documentoPlantillaSubtipo;
}
```

- [ ] **Step 4: Inject selector and document repository into assembler**

Modify `CertificadoAssembler` constructor and fields:

```java
private final DocumentoSoporteRepository documentoRepository;
private final CertificadoTemplateSelector templateSelector;
```

Constructor parameters:

```java
DocumentoSoporteRepository documentoRepository,
CertificadoTemplateSelector templateSelector
```

Assignments:

```java
this.documentoRepository = documentoRepository;
this.templateSelector = templateSelector;
```

Add imports:

```java
import co.gov.bogota.sed.esal.domain.DocumentoSoporte;
import co.gov.bogota.sed.esal.domain.enums.CertificadoPlantilla;
import co.gov.bogota.sed.esal.repository.DocumentoSoporteRepository;
```

- [ ] **Step 5: Select template in `ensamblar`**

After actuaciones are loaded in `ensamblar`, add:

```java
List<DocumentoSoporte> documentos = documentoRepository.findByEsalId(esalId);
CertificadoPlantilla plantilla = templateSelector.seleccionar(esal.getEstado(), documentos);
```

After `dto.setAlertaEstado(...)`, add:

```java
dto.setPlantilla(plantilla);
documentoParaPlantilla(documentos, plantilla).ifPresent(documento -> {
    dto.setDocumentoPlantillaReferencia(documento.getReferenciaActo());
    dto.setDocumentoPlantillaFechaActo(documento.getFechaActo());
    dto.setDocumentoPlantillaSubtipo(documento.getSubtipoDocumental());
});
```

Add helper imports:

```java
import java.util.Optional;
```

Add helper method:

```java
private Optional<DocumentoSoporte> documentoParaPlantilla(List<DocumentoSoporte> documentos,
                                                          CertificadoPlantilla plantilla) {
    if (documentos == null || documentos.isEmpty()) {
        return Optional.empty();
    }
    return documentos.stream()
            .filter(documento -> Boolean.TRUE.equals(documento.getVigente()))
            .filter(documento -> coincidePlantilla(documento, plantilla))
            .findFirst();
}

private boolean coincidePlantilla(DocumentoSoporte documento, CertificadoPlantilla plantilla) {
    switch (plantilla) {
        case EYRL_LIQUIDACION_TRAMITE_CANCELACION_VOLUNTARIA:
            return TipoDocumentoSoporte.LIQUIDACION.equals(documento.getTipoDocumental())
                    && SubtipoDocumentoSoporte.TRAMITE_CANCELACION_VOLUNTARIA.equals(documento.getSubtipoDocumental());
        case EYRL_LIQUIDACION_TERMINO_DURACION:
            return TipoDocumentoSoporte.LIQUIDACION.equals(documento.getTipoDocumental())
                    && SubtipoDocumentoSoporte.TERMINO_DURACION.equals(documento.getSubtipoDocumental());
        case EYRL_CANCELADA_VOLUNTARIAMENTE:
            return TipoDocumentoSoporte.CANCELACION.equals(documento.getTipoDocumental())
                    && SubtipoDocumentoSoporte.CANCELACION_VOLUNTARIA.equals(documento.getSubtipoDocumental());
        case EYRL_CANCELADA_ORDEN_AUTORIDAD:
            return TipoDocumentoSoporte.CANCELACION.equals(documento.getTipoDocumental())
                    && SubtipoDocumentoSoporte.ORDEN_AUTORIDAD.equals(documento.getSubtipoDocumental());
        default:
            return false;
    }
}
```

Add missing imports:

```java
import co.gov.bogota.sed.esal.domain.enums.SubtipoDocumentoSoporte;
import co.gov.bogota.sed.esal.domain.enums.TipoDocumentoSoporte;
```

- [ ] **Step 6: Run GREEN assembler and selector**

Run:

```powershell
mvn test "-Dtest=CertificadoTemplateSelectorTest,CertificadoAssemblerTest"
```

Expected: selector and assembler tests pass.

- [ ] **Step 7: Update execution log and handoff**

Append Task 3 results and create `docs/Handoff/handoff-20260620-i10-task3-closed-retake-task4.md`.

### Task 4: RED PDF Variants I10

**Files:**
- Modify: `sed-esal-backend/src/test/java/co/gov/bogota/sed/esal/service/CertificadoPdfServiceTest.java`

- [ ] **Step 1: Add helper imports and fixture methods**

Add import:

```java
import co.gov.bogota.sed.esal.domain.enums.CertificadoPlantilla;
```

Add helper method near test helpers:

```java
private CertificadoNarrativoDto dtoBase(String nombre, EstadoEsal estado, CertificadoPlantilla plantilla) {
    CertificadoNarrativoDto dto = new CertificadoNarrativoDto();
    dto.setNombre(nombre);
    dto.setIdSipej("I10-PDF-001");
    dto.setNit("900123456-1");
    dto.setDomicilio("Bogota D.C.");
    dto.setCorreoElectronico("i10@test.com");
    dto.setTerminoDuracion("INDEFINIDA");
    dto.setObjetoSocial("Objeto social de prueba para variante I10.");
    dto.setEstado(estado);
    dto.setPlantilla(plantilla);
    dto.setResolucionPersoneria("Resolucion 001");
    dto.setFechaResolucion(LocalDate.of(2020, 1, 15));
    dto.setEntidadQueExpide("Secretaria de Educacion del Distrito");
    dto.setInscripcion("S100001");
    dto.setFechaInscripcion(LocalDate.of(2020, 2, 10));

    MiembroDto representante = new MiembroDto();
    representante.setNombre("JUAN REPRESENTANTE");
    representante.setTipoDocumento("CC");
    representante.setNumeroDocumento("12345678");
    representante.setCargo("Representante Legal");
    representante.setActaNombramiento("ACT-001");
    dto.setRepresentantesLegales(Arrays.asList(representante));

    MiembroDto revisor = new MiembroDto();
    revisor.setNombre("PEDRO REVISOR");
    revisor.setTipoDocumento("CC");
    revisor.setNumeroDocumento("87654321");
    revisor.setCargo("Revisor Fiscal");
    dto.setRevisoresFiscales(Arrays.asList(revisor));
    return dto;
}
```

- [ ] **Step 2: Add RED tests for version/text per variant**

Add tests:

```java
@Test
void generar_suspendida_usaPlantillaSuspendida() throws Exception {
    CertificadoNarrativoDto dto = dtoBase("Fundacion Suspendida I10", EstadoEsal.SUSPENDIDO,
            CertificadoPlantilla.EYRL_SUSPENDIDA);

    byte[] pdf = service.generar(dto, "ESAL-2026-000010", "LIDA DIAZ VELANDIA",
            "Directora de Inspeccion y Vigilancia", LocalDateTime.of(2026, 6, 20, 10, 0));

    String texto = extraerTexto(pdf);
    assertThat(texto).contains("Plantilla: I10-EYRL-SUSPENDIDA-v1");
    assertThat(texto).contains("LA MENCIONADA ESAL TIENE PERSONERIA JURIDICA SUSPENDIDA");
    assertThat(texto).contains("Fundacion Suspendida I10");
}

@Test
void generar_liquidacionTermino_usaPlantillaLiquidacionTermino() throws Exception {
    CertificadoNarrativoDto dto = dtoBase("Fundacion Liquidacion Termino I10", EstadoEsal.EN_LIQUIDACION,
            CertificadoPlantilla.EYRL_LIQUIDACION_TERMINO_DURACION);

    byte[] pdf = service.generar(dto, "ESAL-2026-000011", "LIDA DIAZ VELANDIA",
            "Directora de Inspeccion y Vigilancia", LocalDateTime.of(2026, 6, 20, 10, 0));

    String texto = extraerTexto(pdf);
    assertThat(texto).contains("Plantilla: I10-EYRL-LIQUIDACION-TERMINO-v1");
    assertThat(texto).contains("ESTADO DE LIQUIDACION:");
    assertThat(texto).contains("por cumplimiento del termino de duracion de la ESAL");
}

@Test
void generar_liquidacionTramite_usaPlantillaLiquidacionTramite() throws Exception {
    CertificadoNarrativoDto dto = dtoBase("Fundacion Liquidacion Tramite I10", EstadoEsal.EN_LIQUIDACION,
            CertificadoPlantilla.EYRL_LIQUIDACION_TRAMITE_CANCELACION_VOLUNTARIA);

    byte[] pdf = service.generar(dto, "ESAL-2026-000012", "LIDA DIAZ VELANDIA",
            "Directora de Inspeccion y Vigilancia", LocalDateTime.of(2026, 6, 20, 10, 0));

    String texto = extraerTexto(pdf);
    assertThat(texto).contains("Plantilla: I10-EYRL-LIQUIDACION-TRAMITE-v1");
    assertThat(texto).contains("LA ENTIDAD SE ENCUENTRA DISUELTA Y EN ESTADO DE LIQUIDACION");
}

@Test
void generar_canceladaVoluntaria_usaPlantillaCanceladaVoluntaria() throws Exception {
    CertificadoNarrativoDto dto = dtoBase("Fundacion Cancelada Voluntaria I10", EstadoEsal.CANCELADO,
            CertificadoPlantilla.EYRL_CANCELADA_VOLUNTARIAMENTE);

    byte[] pdf = service.generar(dto, "ESAL-2026-000013", "LIDA DIAZ VELANDIA",
            "Directora de Inspeccion y Vigilancia", LocalDateTime.of(2026, 6, 20, 10, 0));

    String texto = extraerTexto(pdf);
    assertThat(texto).contains("Plantilla: I10-EYRL-CANCELADA-VOLUNTARIA-v1");
    assertThat(texto).contains("LA MENCIONADA ESAL FUE LIQUIDADA Y SU PERSONERIA JURIDICA CANCELADA");
    assertThat(texto).contains("efectuo el tramite correspondiente a su Liquidacion");
}

@Test
void generar_canceladaAutoridad_usaPlantillaCanceladaAutoridad() throws Exception {
    CertificadoNarrativoDto dto = dtoBase("Fundacion Cancelada Autoridad I10", EstadoEsal.CANCELADO,
            CertificadoPlantilla.EYRL_CANCELADA_ORDEN_AUTORIDAD);

    byte[] pdf = service.generar(dto, "ESAL-2026-000014", "LIDA DIAZ VELANDIA",
            "Directora de Inspeccion y Vigilancia", LocalDateTime.of(2026, 6, 20, 10, 0));

    String texto = extraerTexto(pdf);
    assertThat(texto).contains("Plantilla: I10-EYRL-CANCELADA-AUTORIDAD-v1");
    assertThat(texto).contains("LA PERSONERIA JURIDICA DE LA MENCIONADA ESAL FUE CANCELADA");
    assertThat(texto).contains("no ha adelantado el tramite correspondiente a su Liquidacion");
}
```

- [ ] **Step 3: Update existing I8 version expectations**

In existing tests, replace:

```java
assertThat(texto).contains("Plantilla: I8-EYRL-v1");
```

with:

```java
assertThat(texto).contains("Plantilla: I10-EYRL-DEFAULT-v1");
```

- [ ] **Step 4: Run RED PDF**

Run:

```powershell
mvn test "-Dtest=CertificadoPdfServiceTest"
```

Expected: tests fail because `CertificadoPdfService` still prints `I8-EYRL-v1` and lacks I10 variant text blocks.

- [ ] **Step 5: Update execution log**

Record RED PDF failures in execution log.

### Task 5: GREEN PDF Variant Rendering

**Files:**
- Modify: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/service/CertificadoPdfService.java`
- Modify: `sed-esal-backend/src/test/java/co/gov/bogota/sed/esal/service/CertificadoPdfServiceTest.java`

- [ ] **Step 1: Import variant enum**

Add import to `CertificadoPdfService.java`:

```java
import co.gov.bogota.sed.esal.domain.enums.CertificadoPlantilla;
```

Replace:

```java
static final String VERSION_PLANTILLA = "I8-EYRL-v1";
```

with:

```java
static final CertificadoPlantilla PLANTILLA_DEFAULT = CertificadoPlantilla.EYRL_DEFAULT;
```

- [ ] **Step 2: Resolve template and use version**

At the beginning of `generar`, after `doc.open();`, add:

```java
CertificadoPlantilla plantilla = narrativo.getPlantilla() != null
        ? narrativo.getPlantilla()
        : PLANTILLA_DEFAULT;
```

Replace the technical footer paragraph construction:

```java
Paragraph tecnico = new Paragraph("Plantilla: " + VERSION_PLANTILLA + "  |  Generado: "
```

with:

```java
Paragraph tecnico = new Paragraph("Plantilla: " + plantilla.getVersion() + "  |  Generado: "
```

- [ ] **Step 3: Replace state alert block with variant block**

Replace the current block:

```java
if (texto(narrativo.getAlertaEstado())) {
    Paragraph pAlerta = new Paragraph(narrativo.getAlertaEstado(), alerta);
    pAlerta.setSpacingAfter(4);
    doc.add(pAlerta);
    Paragraph legalEstado = parrafoLegalEstado(narrativo.getEstado(), normal);
    if (legalEstado != null) {
        legalEstado.setSpacingAfter(8);
        doc.add(legalEstado);
    }
}
```

with:

```java
agregarBloqueVariante(doc, narrativo, plantilla, normal, bold, alerta);
```

- [ ] **Step 4: Add variant rendering helpers**

Add methods before `parrafoLegalEstado` or replace it if no longer used:

```java
private void agregarBloqueVariante(Document doc,
                                   CertificadoNarrativoDto narrativo,
                                   CertificadoPlantilla plantilla,
                                   Font normal,
                                   Font bold,
                                   Font alerta) throws Exception {
    switch (plantilla) {
        case EYRL_SUSPENDIDA:
            agregarParrafoJustificado(doc,
                    "LA MENCIONADA ESAL TIENE PERSONERIA JURIDICA SUSPENDIDA por el termino no registrado, "
                            + "de acuerdo con el acto administrativo "
                            + referenciaDocumento(narrativo)
                            + ".",
                    alerta,
                    8,
                    8);
            return;
        case EYRL_LIQUIDACION_TRAMITE_CANCELACION_VOLUNTARIA:
            agregarParrafoJustificado(doc,
                    "LA ENTIDAD SE ENCUENTRA DISUELTA Y EN ESTADO DE LIQUIDACION segun acto administrativo "
                            + referenciaDocumento(narrativo)
                            + ".",
                    bold,
                    8,
                    8);
            return;
        case EYRL_LIQUIDACION_TERMINO_DURACION:
            agregarParrafoJustificado(doc, "ESTADO DE LIQUIDACION:", bold, 8, 4);
            agregarParrafoJustificado(doc,
                    "Que de acuerdo con lo establecido en sus estatutos, la entidad sin animo de lucro denominada "
                            + nvl(narrativo.getNombre())
                            + " se encuentra en ESTADO DE LIQUIDACION por cumplimiento del termino de duracion de la ESAL.",
                    normal,
                    0,
                    8);
            return;
        case EYRL_CANCELADA_VOLUNTARIAMENTE:
            agregarParrafoJustificado(doc,
                    "LA MENCIONADA ESAL FUE LIQUIDADA Y SU PERSONERIA JURIDICA CANCELADA mediante acto administrativo "
                            + referenciaDocumento(narrativo)
                            + ".",
                    bold,
                    8,
                    8);
            agregarParrafoJustificado(doc,
                    "Que a la fecha de expedicion del presente certificado, se observa que la ESAL efectuo el tramite correspondiente a su Liquidacion.",
                    normal,
                    0,
                    8);
            return;
        case EYRL_CANCELADA_ORDEN_AUTORIDAD:
            agregarParrafoJustificado(doc,
                    "LA PERSONERIA JURIDICA DE LA MENCIONADA ESAL FUE CANCELADA mediante acto administrativo "
                            + referenciaDocumento(narrativo)
                            + ".",
                    bold,
                    8,
                    8);
            agregarParrafoJustificado(doc,
                    "Que a la fecha de expedicion del presente certificado se observa que la ESAL no ha adelantado el tramite correspondiente a su Liquidacion.",
                    normal,
                    0,
                    8);
            return;
        default:
            if (texto(narrativo.getAlertaEstado())) {
                Paragraph pAlerta = new Paragraph(narrativo.getAlertaEstado(), alerta);
                pAlerta.setSpacingAfter(4);
                doc.add(pAlerta);
                Paragraph legalEstado = parrafoLegalEstado(narrativo.getEstado(), normal);
                if (legalEstado != null) {
                    legalEstado.setSpacingAfter(8);
                    doc.add(legalEstado);
                }
            }
    }
}

private String referenciaDocumento(CertificadoNarrativoDto narrativo) {
    if (texto(narrativo.getDocumentoPlantillaReferencia())) {
        if (narrativo.getDocumentoPlantillaFechaActo() != null) {
            return narrativo.getDocumentoPlantillaReferencia()
                    + " del "
                    + narrativo.getDocumentoPlantillaFechaActo().format(FMT_FECHA);
        }
        return narrativo.getDocumentoPlantillaReferencia();
    }
    return "no registrado";
}
```

- [ ] **Step 5: Run GREEN PDF**

Run:

```powershell
mvn test "-Dtest=CertificadoPdfServiceTest"
```

Expected: PDF tests pass. If text extraction wraps accents or line breaks, adjust assertions to stable fragments without weakening the variant guarantee.

- [ ] **Step 6: Run focused regression**

Run:

```powershell
mvn test "-Dtest=CertificadoTemplateSelectorTest,CertificadoAssemblerTest,CertificadoPdfServiceTest,GeneracionServiceTest"
```

Expected: selector, assembler, PDF and generation tests pass.

- [ ] **Step 7: Update execution log and handoff**

Append Task 5 results and create `docs/Handoff/handoff-20260620-i10-task5-closed-retake-task6.md`.

### Task 6: Generation Flow Regression And Historical Safety

**Files:**
- Modify if needed: `sed-esal-backend/src/test/java/co/gov/bogota/sed/esal/service/GeneracionServiceTest.java`

- [ ] **Step 1: Add end-to-end generation test for default variant**

Add to `GeneracionServiceTest.java`:

```java
@Test
void generar_esalActiva_usaPlantillaDefaultI10() {
    CertificadoDto cert = generacionService.generar(esalId, "expedidor");

    assertThat(cert.getEstadoCertificado()).isEqualTo(EstadoCertificado.GENERADO);
    assertThat(cert.getHashSha256()).hasSize(64);
}
```

This test intentionally checks the generation contract, not PDF text, because text extraction is already covered in `CertificadoPdfServiceTest`.

- [ ] **Step 2: Run generation regression**

Run:

```powershell
mvn test "-Dtest=GeneracionServiceTest"
```

Expected: generation tests pass.

- [ ] **Step 3: Run backend full suite**

Run:

```powershell
mvn test
```

Expected: all backend tests pass. Record final test count in execution log.

- [ ] **Step 4: Package WAR**

Run:

```powershell
mvn package -DskipTests
```

Expected: `sed-esal-backend/target/sed-esal-backend.war` generated.

- [ ] **Step 5: Update execution log**

Record backend full suite and WAR result.

### Task 7: Docs, Functional Guide And Final Handoff

**Files:**
- Modify: `README.md`
- Modify: `docs/ARRANQUE.md`
- Modify: `docs/GUIA_PRUEBAS_FUNCIONALES.md`
- Modify: `docs/plans/2026-06-20-sed-esal-i10-plan.md`
- Modify: `docs/plans/2026-06-20-sed-esal-i10-execution-log.md`
- Create: `docs/Handoff/handoff-20260620-i10-closed-retake-i11.md`

- [ ] **Step 1: Update README**

Update:

- Current phase to I10 completed.
- Add I10 spec to reading order.
- Add increment row:

```markdown
| I10 | Completado | Seleccion de plantilla EYRL por estado y documento vigente I9 |
```

- Update backend test count after final `mvn test`.
- Keep Angular test limitation wording if runner remains unavailable.

- [ ] **Step 2: Update ARRANQUE**

Update:

- Header status to I10 completed.
- Last update to `2026-06-20`.
- Document order adds:
  - `docs/specs/2026-06-20-sed-esal-i10-spec.md`
  - `docs/plans/2026-06-20-sed-esal-i10-plan.md`
  - `docs/plans/2026-06-20-sed-esal-i10-execution-log.md`
- State section mentions I10 selector by estado/documento vigente I9.
- Source artifacts table includes the six I10 templates under `Documentos_Referencia/Iteracion`.
- Local backend test count updated from final suite.

- [ ] **Step 3: Update functional guide**

Add section after I9:

```markdown
## I10 - Seleccion De Plantilla EYRL Por Estado Y Documento Vigente

Fuente de especificacion: `docs/specs/2026-06-20-sed-esal-i10-spec.md`.

Estado: completado.

| ID | Accion | Datos | Esperado |
|---|---|---|---|
| I10-PDF-01 | Generar certificado para ESAL activa | ESAL sin estado especial | PDF usa `I10-EYRL-DEFAULT-v1` |
| I10-PDF-02 | Generar certificado para ESAL suspendida | Estado `SUSPENDIDO` | PDF usa `I10-EYRL-SUSPENDIDA-v1` y texto de suspension |
| I10-PDF-03 | Generar certificado en liquidacion por tramite | Documento vigente `LIQUIDACION.TRAMITE_CANCELACION_VOLUNTARIA` | PDF usa `I10-EYRL-LIQUIDACION-TRAMITE-v1` |
| I10-PDF-04 | Generar certificado en liquidacion por termino | Documento vigente `LIQUIDACION.TERMINO_DURACION` | PDF usa `I10-EYRL-LIQUIDACION-TERMINO-v1` |
| I10-PDF-05 | Generar certificado cancelada voluntariamente | Documento vigente `CANCELACION.CANCELACION_VOLUNTARIA` | PDF usa `I10-EYRL-CANCELADA-VOLUNTARIA-v1` |
| I10-PDF-06 | Generar certificado cancelada por autoridad | Documento vigente `CANCELACION.ORDEN_AUTORIDAD` | PDF usa `I10-EYRL-CANCELADA-AUTORIDAD-v1` |
```

- [ ] **Step 4: Run Angular build regression**

Run:

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-angular
node "C:\Program Files\nodejs\node_modules\npm\bin\npm-cli.js" run build
```

Expected: build passes. Record warnings if any.

- [ ] **Step 5: Final diff checks**

Run:

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL
git diff --check
git status --short
```

Expected: no whitespace errors. Preserve unrelated pre-existing worktree noise.

- [ ] **Step 6: Close execution log**

Update execution log:

- `Estado: completado`.
- List RED/GREEN evidence.
- List full backend suite result and test count.
- List WAR result.
- List Angular build result or limitation.
- List final `git diff --check` result.
- Mention any controlled deviations.

- [ ] **Step 7: Create final handoff**

Create `docs/Handoff/handoff-20260620-i10-closed-retake-i11.md`:

```markdown
# Handoff - SED_ESAL I10 Cerrado

> Fecha: 2026-06-20.
> Estado: I10 completado.
> Retake recomendado: abrir SPEC I11 antes de implementar nuevo alcance.

## Resumen

I10 implemento seleccion de plantilla EYRL por estado y documento vigente I9.

## Evidencia

- Backend full suite: registrar conteo final.
- WAR: `sed-esal-backend/target/sed-esal-backend.war`.
- Angular build: registrar resultado.

## Retake

Revisar `docs/ARRANQUE.md`, SPEC/PLAN/log I10 y esta nota antes de continuar.
```

## Risks

| Risk | Mitigation |
|---|---|
| `CertificadoPdfService` crece demasiado | Mantener selector externo; extraer helpers de renderizado solo si el cambio local queda ilegible |
| Datos juridicos no normalizados en plantillas | Usar datos disponibles y marcador `no registrado`; no inventar datos |
| Liquidacion/cancelacion historica sin documento vigente I9 | Fallback default defensivo; flujos I9 nuevos ya bloquean falta de documento |
| Pruebas PDF fragiles por saltos de linea/acentos | Asertar fragmentos estables y versiones tecnicas de plantilla |
| Worktree con cambios previos | No revertir ni limpiar cambios no relacionados; documentar status final |

## Verification Checkpoints

1. RED selector confirmado.
2. GREEN selector.
3. GREEN assembler con documento vigente I9.
4. RED PDF por variantes.
5. GREEN PDF por variantes.
6. Regression `CertificadoTemplateSelectorTest,CertificadoAssemblerTest,CertificadoPdfServiceTest,GeneracionServiceTest`.
7. Backend full `mvn test`.
8. WAR `mvn package -DskipTests`.
9. Angular build.
10. README, ARRANQUE, GUIA, execution log y handoff final actualizados.

## Retake Point

I10 plan pendiente de aprobacion. Al aprobar, iniciar con Task 1 creando execution log y RED selector.
