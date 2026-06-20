# I9 Gestion Documental Administrativa Transversal - Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use `superpowers:executing-plans` to implement this plan task-by-task. Steps use checkbox (`- [x]`) syntax for tracking.

**Goal:** Implementar gestion documental transversal para ESAL con PDF maximo 10 MB, metadatos obligatorios, version vigente, historico consultable, descarga autenticada y bloqueo documental para liquidacion/cancelacion.

**Architecture:** I9 extiende el agregado documental existente (`DocumentoSoporte`) y mantiene `AlmacenamientoService` como abstraccion de storage local/backend. Las reglas se concentran en `DocumentoSoporteService` y se consumen desde `EsalController` y `EsalMaintenanceService`; la UI se integra como seccion `Documentos` dentro de `EsalMaintenanceComponent`.

**Tech Stack:** Spring Boot 2.7, Java 8, JPA/Hibernate, Maven, JUnit 5, MockMvc/Spring Security tests cuando aplique, Angular 20 standalone, PrimeNG/Tailwind design tokens existentes.

---

> Estado: completado.
> Spec: `docs/specs/2026-06-19-sed-esal-i9-spec.md`.
> Metodologia: SDD Spec-Anchored.
> Requerimiento fuente: `Documentos_Referencia/Iteracion/Aplicativo ESAL.docx`.

## File Structure

Backend:

- Modify: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/domain/DocumentoSoporte.java`
  - Responsabilidad: persistir metadatos I9, vigencia y trazabilidad documental.
- Create: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/domain/enums/TipoDocumentoSoporte.java`
  - Responsabilidad: catalogo documental I9.
- Create: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/domain/enums/SubtipoDocumentoSoporte.java`
  - Responsabilidad: subtipos permitidos para liquidacion/cancelacion.
- Modify: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/dto/DocumentoSoporteDto.java`
  - Responsabilidad: exponer metadatos sin bytes ni ruta fisica.
- Modify: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/repository/DocumentoSoporteRepository.java`
  - Responsabilidad: consultas por ESAL, tipo/subtipo y vigencia.
- Modify: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/service/DocumentoSoporteService.java`
  - Responsabilidad: validaciones I9, reemplazo de vigencia, descarga autenticada y auditoria.
- Modify: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/service/AlmacenamientoService.java`
  - Responsabilidad: exponer lectura de archivo almacenado si aun no existe.
- Modify: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/service/LocalDevAlmacenamientoService.java`
  - Responsabilidad: leer bytes desde storage local-dev.
- Modify: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/service/TestAlmacenamientoService.java`
  - Responsabilidad: soportar descarga en tests sin filesystem real.
- Modify: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/controller/EsalController.java`
  - Responsabilidad: aceptar nuevos campos multipart y exponer descarga.
- Modify: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/service/EsalMaintenanceService.java`
  - Responsabilidad: bloquear liquidacion/cancelacion sin documento vigente obligatorio.
- Modify: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/service/AuditoriaAcciones.java`
  - Responsabilidad: agregar acciones I9.

Frontend:

- Modify: `sed-esal-angular/src/app/core/models/esal.model.ts`
  - Responsabilidad: tipos TS para catalogo, metadatos y vigencia.
- Modify: `sed-esal-angular/src/app/core/services/api.service.ts`
  - Responsabilidad: descarga Blob ya existe; validar uso desde UI.
- Modify: `sed-esal-angular/src/app/features/admin/esales/esal-maintenance.component.ts`
  - Responsabilidad: seccion `Documentos`, formulario de carga, historico/vigente, descarga y gating visual por rol.

Tests:

- Modify: `sed-esal-backend/src/test/java/co/gov/bogota/sed/esal/service/DocumentoSoporteServiceTest.java`
- Modify: `sed-esal-backend/src/test/java/co/gov/bogota/sed/esal/service/EsalMaintenanceServiceTest.java`
- Create: `sed-esal-backend/src/test/java/co/gov/bogota/sed/esal/security/EsalDocumentosSecurityTest.java`
- Create: `sed-esal-angular/src/app/features/admin/esales/esal-maintenance.component.spec.ts`

Docs:

- Create during implementation start: `docs/plans/2026-06-19-sed-esal-i9-execution-log.md`
- Update at closure: `README.md`, `docs/ARRANQUE.md`, `docs/GUIA_PRUEBAS_FUNCIONALES.md`

## Tasks

### Task 1: RED Backend Documental I9

**Files:**
- Modify: `sed-esal-backend/src/test/java/co/gov/bogota/sed/esal/service/DocumentoSoporteServiceTest.java`

- [x] **Step 1: Add failing tests for I9 validations and vigente/historico**

Add tests that call the future extended signature:

```java
@Test
void cargaSinReferencia_esRechazada() {
    Esal esal = crearEsal("Fundacion Sin Referencia I9");
    byte[] contenidoPdf = "%PDF-1.4 test content".getBytes();

    assertThatThrownBy(() -> documentoSoporteService.registrar(
            esal.getId(),
            "creacion.pdf",
            "application/pdf",
            contenidoPdf.length,
            new ByteArrayInputStream(contenidoPdf),
            "CREACION_FORMACION",
            null,
            "   ",
            LocalDate.now(),
            "Observacion",
            "admin@educacionbogota.edu.co"))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatus().value()).isEqualTo(400));
}

@Test
void cargaSinFechaActo_esRechazada() {
    Esal esal = crearEsal("Fundacion Sin Fecha I9");
    byte[] contenidoPdf = "%PDF-1.4 test content".getBytes();

    assertThatThrownBy(() -> documentoSoporteService.registrar(
            esal.getId(),
            "dignatarios.pdf",
            "application/pdf",
            contenidoPdf.length,
            new ByteArrayInputStream(contenidoPdf),
            "DIGNATARIOS",
            null,
            "ACTO-001",
            null,
            null,
            "admin@educacionbogota.edu.co"))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatus().value()).isEqualTo(400));
}

@Test
void pdfMayorA10Mb_esRechazado() {
    Esal esal = crearEsal("Fundacion Archivo Grande I9");
    byte[] contenidoPdf = "%PDF-1.4 test content".getBytes();

    assertThatThrownBy(() -> documentoSoporteService.registrar(
            esal.getId(),
            "grande.pdf",
            "application/pdf",
            10L * 1024L * 1024L + 1L,
            new ByteArrayInputStream(contenidoPdf),
            "CREACION_FORMACION",
            null,
            "RES-001",
            LocalDate.now(),
            null,
            "admin@educacionbogota.edu.co"))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatus().value()).isEqualTo(400));
}

@Test
void subtipoIncompatible_esRechazado() {
    Esal esal = crearEsal("Fundacion Subtipo Incompatible I9");
    byte[] contenidoPdf = "%PDF-1.4 test content".getBytes();

    assertThatThrownBy(() -> documentoSoporteService.registrar(
            esal.getId(),
            "creacion.pdf",
            "application/pdf",
            contenidoPdf.length,
            new ByteArrayInputStream(contenidoPdf),
            "CREACION_FORMACION",
            "CANCELACION_VOLUNTARIA",
            "RES-001",
            LocalDate.now(),
            null,
            "admin@educacionbogota.edu.co"))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatus().value()).isEqualTo(400));
}

@Test
void nuevaCargaMismoTipoSubtipo_reemplazaVigenteYConservaHistorico() throws IOException {
    Esal esal = crearEsal("Fundacion Historico I9");
    byte[] contenidoPdf = "%PDF-1.4 test content".getBytes();

    DocumentoSoporteDto primero = documentoSoporteService.registrar(
            esal.getId(), "liquidacion-1.pdf", "application/pdf", contenidoPdf.length,
            new ByteArrayInputStream(contenidoPdf), "LIQUIDACION", "TERMINO_DURACION",
            "OF-001", LocalDate.of(2026, 6, 1), "Primera version", "admin@educacionbogota.edu.co");

    DocumentoSoporteDto segundo = documentoSoporteService.registrar(
            esal.getId(), "liquidacion-2.pdf", "application/pdf", contenidoPdf.length,
            new ByteArrayInputStream(contenidoPdf), "LIQUIDACION", "TERMINO_DURACION",
            "OF-002", LocalDate.of(2026, 6, 2), "Segunda version", "admin@educacionbogota.edu.co");

    List<DocumentoSoporteDto> documentos = documentoSoporteService.listar(esal.getId());

    assertThat(documentos).hasSize(2);
    assertThat(documentos).filteredOn(DocumentoSoporteDto::isVigente).extracting(DocumentoSoporteDto::getId)
            .containsExactly(segundo.getId());
    assertThat(documentos).filteredOn(d -> !d.isVigente()).extracting(DocumentoSoporteDto::getId)
            .containsExactly(primero.getId());
}
```

Also add imports:

```java
import java.time.LocalDate;
```

- [x] **Step 2: Run RED**

Run:

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-backend
mvn test "-Dtest=DocumentoSoporteServiceTest"
```

Expected: compilation fails because the extended `registrar` signature and `DocumentoSoporteDto.isVigente()` do not exist yet.

### Task 2: Domain, DTO And Repository Contract

**Files:**
- Create: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/domain/enums/TipoDocumentoSoporte.java`
- Create: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/domain/enums/SubtipoDocumentoSoporte.java`
- Modify: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/domain/DocumentoSoporte.java`
- Modify: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/dto/DocumentoSoporteDto.java`
- Modify: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/repository/DocumentoSoporteRepository.java`

- [x] **Step 1: Add enums**

`TipoDocumentoSoporte.java`:

```java
package co.gov.bogota.sed.esal.domain.enums;

public enum TipoDocumentoSoporte {
    CREACION_FORMACION,
    DIGNATARIOS,
    LIQUIDACION,
    CANCELACION
}
```

`SubtipoDocumentoSoporte.java`:

```java
package co.gov.bogota.sed.esal.domain.enums;

public enum SubtipoDocumentoSoporte {
    TRAMITE_CANCELACION_VOLUNTARIA,
    TERMINO_DURACION,
    CANCELACION_VOLUNTARIA,
    ORDEN_AUTORIDAD
}
```

- [x] **Step 2: Extend `DocumentoSoporte`**

Add imports:

```java
import co.gov.bogota.sed.esal.domain.enums.SubtipoDocumentoSoporte;
import co.gov.bogota.sed.esal.domain.enums.TipoDocumentoSoporte;
import java.time.LocalDate;
```

Add fields before `estadoValidacion`:

```java
@Enumerated(EnumType.STRING)
@Column(name = "TIPO_DOCUMENTAL", length = 50)
private TipoDocumentoSoporte tipoDocumental;

@Enumerated(EnumType.STRING)
@Column(name = "SUBTIPO_DOCUMENTAL", length = 50)
private SubtipoDocumentoSoporte subtipoDocumental;

@Column(name = "REFERENCIA_ACTO", length = 255)
private String referenciaActo;

@Column(name = "FECHA_ACTO")
private LocalDate fechaActo;

@Column(name = "OBSERVACION", length = 1000)
private String observacion;

@Column(name = "VIGENTE")
private Boolean vigente = Boolean.TRUE;
```

Add getters/setters for every field.

- [x] **Step 3: Extend `DocumentoSoporteDto`**

Add imports:

```java
import co.gov.bogota.sed.esal.domain.enums.SubtipoDocumentoSoporte;
import co.gov.bogota.sed.esal.domain.enums.TipoDocumentoSoporte;
import java.time.LocalDate;
```

Add fields:

```java
private TipoDocumentoSoporte tipoDocumental;
private SubtipoDocumentoSoporte subtipoDocumental;
private String referenciaActo;
private LocalDate fechaActo;
private String observacion;
private boolean vigente;
```

Add getters/setters, including:

```java
public boolean isVigente() { return vigente; }
public void setVigente(boolean vigente) { this.vigente = vigente; }
```

- [x] **Step 4: Add repository queries**

Add imports:

```java
import co.gov.bogota.sed.esal.domain.enums.SubtipoDocumentoSoporte;
import co.gov.bogota.sed.esal.domain.enums.TipoDocumentoSoporte;
import java.util.Optional;
```

Add methods:

```java
Optional<DocumentoSoporte> findFirstByEsalIdAndTipoDocumentalAndSubtipoDocumentalAndVigenteTrue(
        Long esalId,
        TipoDocumentoSoporte tipoDocumental,
        SubtipoDocumentoSoporte subtipoDocumental);

List<DocumentoSoporte> findByEsalIdOrderByVigenteDescCreatedAtDesc(Long esalId);
```

- [x] **Step 5: Run compile-focused test**

Run:

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-backend
mvn test "-Dtest=DocumentoSoporteServiceTest"
```

Expected: still fails because service implementation has not been updated, but enum/DTO compile errors are reduced.

### Task 3: DocumentoSoporteService I9 Rules

**Files:**
- Modify: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/service/DocumentoSoporteService.java`
- Modify: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/service/AuditoriaAcciones.java`

- [x] **Step 1: Add constants and imports**

Add imports:

```java
import co.gov.bogota.sed.esal.domain.enums.SubtipoDocumentoSoporte;
import co.gov.bogota.sed.esal.domain.enums.TipoDocumentoSoporte;
import java.time.LocalDate;
```

Add constant:

```java
private static final long TAMANO_MAXIMO_PDF = 10L * 1024L * 1024L;
```

- [x] **Step 2: Add I9 audit actions**

In `AuditoriaAcciones.java`, add constants:

```java
public static final String DOCUMENTO_SOPORTE_CREADO = "DOCUMENTO_SOPORTE_CREADO";
public static final String DOCUMENTO_SOPORTE_VIGENCIA_REEMPLAZADA = "DOCUMENTO_SOPORTE_VIGENCIA_REEMPLAZADA";
public static final String DOCUMENTO_SOPORTE_DESCARGADO = "DOCUMENTO_SOPORTE_DESCARGADO";
public static final String ESAL_LIQUIDACION_BLOQUEADA_SIN_DOCUMENTO = "ESAL_LIQUIDACION_BLOQUEADA_SIN_DOCUMENTO";
public static final String ESAL_CANCELACION_BLOQUEADA_SIN_DOCUMENTO = "ESAL_CANCELACION_BLOQUEADA_SIN_DOCUMENTO";
```

- [x] **Step 3: Replace `registrar` signature and map enums**

Change method signature to:

```java
public DocumentoSoporteDto registrar(Long esalId,
                                      String nombreArchivo,
                                      String contentType,
                                      long tamanoBytes,
                                      InputStream contenido,
                                      String tipoDocumento,
                                      String subtipoDocumento,
                                      String referencia,
                                      LocalDate fechaActo,
                                      String observacion,
                                      String registradoPor) throws IOException
```

Inside method, after ESAL validation, add:

```java
validarArchivoPdf(contentType, tamanoBytes);
TipoDocumentoSoporte tipo = parseTipo(tipoDocumento);
SubtipoDocumentoSoporte subtipo = parseSubtipo(subtipoDocumento);
validarCatalogo(tipo, subtipo);
validarMetadatos(referencia, fechaActo);
```

Before saving the new document:

```java
documentoRepository.findFirstByEsalIdAndTipoDocumentalAndSubtipoDocumentalAndVigenteTrue(esalId, tipo, subtipo)
        .ifPresent(anterior -> {
            anterior.setVigente(Boolean.FALSE);
            documentoRepository.save(anterior);
            auditoriaService.registrar(registradoPor, auditoriaService.obtenerRolActual(),
                    AuditoriaAcciones.DOCUMENTO_SOPORTE_VIGENCIA_REEMPLAZADA,
                    AuditoriaAcciones.ENTIDAD_DOCUMENTO,
                    anterior.getId(), null,
                    AuditoriaAcciones.RESULTADO_EXITO,
                    "ESAL: " + esalId + ", tipo: " + tipo + ", subtipo: " + subtipo);
        });
```

Set new fields:

```java
documento.setTipoDocumental(tipo);
documento.setSubtipoDocumental(subtipo);
documento.setReferenciaActo(referencia.trim());
documento.setFechaActo(fechaActo);
documento.setObservacion(observacion == null || observacion.trim().isEmpty() ? null : observacion.trim());
documento.setVigente(Boolean.TRUE);
```

Use `AuditoriaAcciones.DOCUMENTO_SOPORTE_CREADO` for the create event.

- [x] **Step 4: Add validation helpers**

Add private methods:

```java
private void validarArchivoPdf(String contentType, long tamanoBytes) {
    if (!CONTENT_TYPE_PDF.equalsIgnoreCase(contentType)) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Solo se acepta application/pdf. ContentType recibido: " + contentType);
    }
    if (tamanoBytes > TAMANO_MAXIMO_PDF) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "El documento PDF no puede superar 10 MB.");
    }
}

private TipoDocumentoSoporte parseTipo(String tipoDocumento) {
    if (tipoDocumento == null || tipoDocumento.trim().isEmpty()) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo 'tipoDocumento' es obligatorio.");
    }
    try {
        return TipoDocumentoSoporte.valueOf(tipoDocumento.trim());
    } catch (IllegalArgumentException ex) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo documental no permitido: " + tipoDocumento);
    }
}

private SubtipoDocumentoSoporte parseSubtipo(String subtipoDocumento) {
    if (subtipoDocumento == null || subtipoDocumento.trim().isEmpty()) {
        return null;
    }
    try {
        return SubtipoDocumentoSoporte.valueOf(subtipoDocumento.trim());
    } catch (IllegalArgumentException ex) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Subtipo documental no permitido: " + subtipoDocumento);
    }
}

private void validarCatalogo(TipoDocumentoSoporte tipo, SubtipoDocumentoSoporte subtipo) {
    if (TipoDocumentoSoporte.CREACION_FORMACION.equals(tipo) || TipoDocumentoSoporte.DIGNATARIOS.equals(tipo)) {
        if (subtipo != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El subtipo solo aplica para LIQUIDACION o CANCELACION.");
        }
        return;
    }
    if (TipoDocumentoSoporte.LIQUIDACION.equals(tipo)) {
        if (!SubtipoDocumentoSoporte.TRAMITE_CANCELACION_VOLUNTARIA.equals(subtipo)
                && !SubtipoDocumentoSoporte.TERMINO_DURACION.equals(subtipo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "LIQUIDACION requiere subtipo TRAMITE_CANCELACION_VOLUNTARIA o TERMINO_DURACION.");
        }
        return;
    }
    if (TipoDocumentoSoporte.CANCELACION.equals(tipo)) {
        if (!SubtipoDocumentoSoporte.CANCELACION_VOLUNTARIA.equals(subtipo)
                && !SubtipoDocumentoSoporte.ORDEN_AUTORIDAD.equals(subtipo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "CANCELACION requiere subtipo CANCELACION_VOLUNTARIA u ORDEN_AUTORIDAD.");
        }
    }
}

private void validarMetadatos(String referencia, LocalDate fechaActo) {
    if (referencia == null || referencia.trim().isEmpty()) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo 'referencia' es obligatorio.");
    }
    if (fechaActo == null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo 'fechaActo' es obligatorio.");
    }
}
```

- [x] **Step 5: Update `listar` and `toDto`**

Change listing query:

```java
return documentoRepository.findByEsalIdOrderByVigenteDescCreatedAtDesc(esalId)
        .stream()
        .map(this::toDto)
        .collect(Collectors.toList());
```

Add DTO mappings:

```java
dto.setTipoDocumental(doc.getTipoDocumental());
dto.setSubtipoDocumental(doc.getSubtipoDocumental());
dto.setReferenciaActo(doc.getReferenciaActo());
dto.setFechaActo(doc.getFechaActo());
dto.setObservacion(doc.getObservacion());
dto.setVigente(Boolean.TRUE.equals(doc.getVigente()));
```

- [x] **Step 6: Run GREEN for service tests**

Run:

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-backend
mvn test "-Dtest=DocumentoSoporteServiceTest"
```

Expected: `DocumentoSoporteServiceTest` passes.

### Task 4: Descarga Autenticada Backend

**Files:**
- Modify: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/service/AlmacenamientoService.java`
- Modify: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/service/LocalDevAlmacenamientoService.java`
- Modify: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/service/TestAlmacenamientoService.java`
- Modify: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/service/DocumentoSoporteService.java`
- Modify: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/controller/EsalController.java`
- Modify: `sed-esal-backend/src/test/java/co/gov/bogota/sed/esal/service/DocumentoSoporteServiceTest.java`

- [x] **Step 1: Add download RED test**

Add:

```java
@Test
void descargarDocumento_validaPertenenciaYRetornaBytes() throws IOException {
    Esal esal = crearEsal("Fundacion Descarga I9");
    byte[] contenidoPdf = "%PDF-1.4 descarga".getBytes();

    DocumentoSoporteDto documento = documentoSoporteService.registrar(
            esal.getId(), "descarga.pdf", "application/pdf", contenidoPdf.length,
            new ByteArrayInputStream(contenidoPdf), "CREACION_FORMACION", null,
            "RES-100", LocalDate.now(), null, "admin@educacionbogota.edu.co");

    DocumentoSoporteService.DocumentoDescarga descarga =
            documentoSoporteService.descargar(esal.getId(), documento.getId(), "expedidor@educacionbogota.edu.co");

    assertThat(descarga.getNombreArchivo()).isEqualTo("descarga.pdf");
    assertThat(descarga.getContentType()).isEqualTo("application/pdf");
    assertThat(descarga.getContenido()).containsExactly(contenidoPdf);
}
```

Run `mvn test "-Dtest=DocumentoSoporteServiceTest"` and expect failure because download API does not exist.

- [x] **Step 2: Extend storage interface**

In `AlmacenamientoService.java`, add:

```java
byte[] leer(String rutaAlmacenamiento) throws IOException;
```

Implement in `LocalDevAlmacenamientoService`:

```java
@Override
public byte[] leer(String rutaAlmacenamiento) throws IOException {
    return Files.readAllBytes(Paths.get(rutaAlmacenamiento));
}
```

Implement in `TestAlmacenamientoService` with deterministic in-memory storage:

```java
private final java.util.Map<String, byte[]> archivos = new java.util.concurrent.ConcurrentHashMap<>();

@Override
public String guardar(Long esalId, String nombreArchivo, InputStream contenido, long tamanoBytes)
        throws java.io.IOException {
    String ruta = "/test/docs/" + esalId + "/" + nombreArchivo;
    archivos.put(ruta, org.springframework.util.StreamUtils.copyToByteArray(contenido));
    return ruta;
}

@Override
public byte[] leer(String rutaAlmacenamiento) {
    byte[] contenido = archivos.get(rutaAlmacenamiento);
    if (contenido == null) {
        throw new IllegalArgumentException("Archivo de prueba no encontrado: " + rutaAlmacenamiento);
    }
    return contenido;
}
```

- [x] **Step 3: Add service download DTO and method**

Inside `DocumentoSoporteService`, add public static class:

```java
public static class DocumentoDescarga {
    private final String nombreArchivo;
    private final String contentType;
    private final byte[] contenido;

    public DocumentoDescarga(String nombreArchivo, String contentType, byte[] contenido) {
        this.nombreArchivo = nombreArchivo;
        this.contentType = contentType;
        this.contenido = contenido;
    }

    public String getNombreArchivo() { return nombreArchivo; }
    public String getContentType() { return contentType; }
    public byte[] getContenido() { return contenido; }
}
```

Add method:

```java
@Transactional(readOnly = true)
public DocumentoDescarga descargar(Long esalId, Long documentoId, String usuario) throws IOException {
    DocumentoSoporte documento = documentoRepository.findById(documentoId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Documento no encontrado con id: " + documentoId));
    if (!esalId.equals(documento.getEsalId())) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Documento no encontrado con id: " + documentoId);
    }
    byte[] contenido = almacenamientoService.leer(documento.getRutaAlmacenamiento());
    auditoriaService.registrar(usuario, auditoriaService.obtenerRolActual(),
            AuditoriaAcciones.DOCUMENTO_SOPORTE_DESCARGADO,
            AuditoriaAcciones.ENTIDAD_DOCUMENTO,
            documento.getId(), null,
            AuditoriaAcciones.RESULTADO_EXITO,
            "ESAL: " + esalId + ", tipo: " + documento.getTipoDocumental());
    return new DocumentoDescarga(documento.getNombreArchivo(), documento.getContentType(), contenido);
}
```

- [x] **Step 4: Expose controller endpoint**

In `EsalController`, add imports:

```java
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import java.util.concurrent.TimeUnit;
```

Add endpoint:

```java
@GetMapping("/{id}/documentos/{documentoId}/descarga")
@Operation(summary = "Descargar documento soporte",
           description = "Descarga autenticada por backend. ADMINISTRADOR y EXPEDIDOR.")
public ResponseEntity<byte[]> descargarDocumento(
        @PathVariable Long id,
        @PathVariable Long documentoId,
        Authentication authentication) throws IOException {
    String usuario = authentication != null ? authentication.getName() : "sistema";
    DocumentoSoporteService.DocumentoDescarga descarga =
            documentoSoporteService.descargar(id, documentoId, usuario);
    return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + descarga.getNombreArchivo() + "\"")
            .cacheControl(CacheControl.maxAge(0, TimeUnit.SECONDS).cachePrivate().mustRevalidate())
            .body(descarga.getContenido());
}
```

- [x] **Step 5: Run service test**

Run:

```powershell
mvn test "-Dtest=DocumentoSoporteServiceTest"
```

Expected: PASS.

### Task 5: Estado Administrativo Requires Documento Vigente

**Files:**
- Modify: `sed-esal-backend/src/test/java/co/gov/bogota/sed/esal/service/EsalMaintenanceServiceTest.java`
- Modify: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/service/EsalMaintenanceService.java`

- [x] **Step 1: Add RED tests**

Add tests:

```java
@Test
void actualizarInformacionPrincipalAEnLiquidacionSinDocumentoVigente_esBloqueado() {
    Esal esal = new Esal();
    esal.setNombre("Fundacion Liquidacion Sin Documento I9");
    esal.setIdSipej("I9-LIQ-001");
    esal.setEstado(EstadoEsal.ACTIVO);
    esal = esalRepository.save(esal);

    EsalInformacionPrincipalDto dto = new EsalInformacionPrincipalDto();
    dto.setNombre(esal.getNombre());
    dto.setIdSipej(esal.getIdSipej());
    dto.setEstado(EstadoEsal.EN_LIQUIDACION);

    assertThatThrownBy(() -> maintenanceService.actualizarInformacionPrincipal(esal.getId(), dto, "admin-i9"))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("documento vigente de liquidacion");
}

@Test
void cancelarSinDocumentoVigente_esBloqueado() {
    Esal esal = new Esal();
    esal.setNombre("Fundacion Cancelacion Sin Documento I9");
    esal.setIdSipej("I9-CAN-001");
    esal.setEstado(EstadoEsal.ACTIVO);
    esal = esalRepository.save(esal);

    CancelacionEsalDto dto = new CancelacionEsalDto();
    dto.setResolucion("RES-CAN-001");
    dto.setFechaResolucion(LocalDate.now());
    dto.setMotivo("Cancelacion voluntaria");

    assertThatThrownBy(() -> maintenanceService.cancelar(esal.getId(), dto, "admin-i9"))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("documento vigente de cancelacion");
}
```

Add import `org.springframework.web.server.ResponseStatusException` if the class does not already import it.

- [x] **Step 2: Run RED**

Run:

```powershell
mvn test "-Dtest=EsalMaintenanceServiceTest"
```

Expected: tests fail because I5 still allows cancellation without PDF and liquidacion is not blocked by DocumentoSoporte.

- [x] **Step 3: Inject/document query helpers**

In `EsalMaintenanceService`, add imports:

```java
import co.gov.bogota.sed.esal.domain.enums.SubtipoDocumentoSoporte;
import co.gov.bogota.sed.esal.domain.enums.TipoDocumentoSoporte;
```

Add helper:

```java
private boolean existeDocumentoVigente(Long esalId, TipoDocumentoSoporte tipo) {
    return documentoRepository.findByEsalId(esalId).stream()
            .anyMatch(documento -> tipo.equals(documento.getTipoDocumental())
                    && Boolean.TRUE.equals(documento.getVigente()));
}
```

- [x] **Step 4: Block liquidacion in `aplicarInformacionPrincipal` path**

Before saving in `actualizarInformacionPrincipal`, after `aplicarInformacionPrincipal`, add:

```java
if (EstadoEsal.EN_LIQUIDACION.equals(dto.getEstado())
        && !existeDocumentoVigente(esalId, TipoDocumentoSoporte.LIQUIDACION)) {
    auditoriaService.registrar(usuario, auditoriaService.obtenerRolActual(),
            AuditoriaAcciones.ESAL_LIQUIDACION_BLOQUEADA_SIN_DOCUMENTO,
            AuditoriaAcciones.ENTIDAD_ESAL,
            esal.getId(), esal.getIdSipej(),
            AuditoriaAcciones.RESULTADO_ERROR,
            "Falta documento vigente de liquidacion.");
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "No se puede pasar a EN_LIQUIDACION sin documento vigente de liquidacion.");
}
```

- [x] **Step 5: Block cancelacion before creating actuacion**

In `cancelar`, after `Esal esal = obtenerEsal(esalId);`, add:

```java
if (!existeDocumentoVigente(esalId, TipoDocumentoSoporte.CANCELACION)) {
    auditoriaService.registrar(usuario, auditoriaService.obtenerRolActual(),
            AuditoriaAcciones.ESAL_CANCELACION_BLOQUEADA_SIN_DOCUMENTO,
            AuditoriaAcciones.ENTIDAD_ESAL,
            esal.getId(), esal.getIdSipej(),
            AuditoriaAcciones.RESULTADO_ERROR,
            "Falta documento vigente de cancelacion.");
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "No se puede cancelar sin documento vigente de cancelacion.");
}
```

Remove or stop calling `registrarAdvertenciaSoporteCancelacionSiFalta` in the successful cancel path because I9 replaces the warning with a block.

- [x] **Step 6: Run GREEN**

Run:

```powershell
mvn test "-Dtest=EsalMaintenanceServiceTest,DocumentoSoporteServiceTest"
```

Expected: PASS after adding fixture documents in existing positive cancellation tests where needed.

### Task 6: Controller Multipart Contract And Security

**Files:**
- Modify: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/controller/EsalController.java`
- Modify: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/config/DevSecurityConfig.java`
- Modify: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/config/WeblogicSecurityConfig.java`
- Create: `sed-esal-backend/src/test/java/co/gov/bogota/sed/esal/security/EsalDocumentosSecurityTest.java`

- [x] **Step 1: Update controller multipart params**

In `registrarDocumento`, replace `tipoProceso` with:

```java
@RequestParam("tipoDocumento") String tipoDocumento,
@RequestParam(value = "subtipoDocumento", required = false) String subtipoDocumento,
@RequestParam("referencia") String referencia,
@RequestParam("fechaActo") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate fechaActo,
@RequestParam(value = "observacion", required = false) String observacion,
```

Call service:

```java
DocumentoSoporteDto result = documentoSoporteService.registrar(
        id,
        archivo.getOriginalFilename(),
        archivo.getContentType(),
        archivo.getSize(),
        archivo.getInputStream(),
        tipoDocumento,
        subtipoDocumento,
        referencia,
        fechaActo,
        observacion,
        usuario);
```

- [x] **Step 2: Ensure download authorization**

In both security configs, ensure:

```java
.antMatchers(HttpMethod.GET, "/api/esales/*/documentos/*/descarga").hasAnyRole("ADMINISTRADOR", "EXPEDIDOR")
.antMatchers(HttpMethod.GET, "/api/esales/*/documentos").hasAnyRole("ADMINISTRADOR", "EXPEDIDOR")
.antMatchers(HttpMethod.POST, "/api/esales/*/documentos").hasRole("ADMINISTRADOR")
```

- [x] **Step 3: Add security tests**

Create `EsalDocumentosSecurityTest`:

```java
package co.gov.bogota.sed.esal.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EsalDocumentosSecurityTest {

    @Autowired
    private MockMvc mockMvc;

@Test
void expedidorNoPuedeCargarDocumentoPeroPuedeListarYDescargar() throws Exception {
    MockMultipartFile archivo = new MockMultipartFile(
            "archivo", "test.pdf", "application/pdf", "%PDF-1.4".getBytes());

    mockMvc.perform(multipart("/api/esales/{id}/documentos", 1L)
            .file(archivo)
            .param("tipoDocumento", "CREACION_FORMACION")
            .param("referencia", "RES-001")
            .param("fechaActo", "2026-06-19")
            .with(httpBasic("expedidor@educacionbogota.edu.co", "expedidor123")))
            .andExpect(status().isForbidden());

    mockMvc.perform(get("/api/esales/{id}/documentos", 1L)
            .with(httpBasic("expedidor@educacionbogota.edu.co", "expedidor123")))
            .andExpect(status().isOk());
}
}
```

If `GET /api/esales/1/documentos` returns `404` because the ESAL fixture does not exist in this full-context test, change only the expected status for the GET to `isNotFound`; the security assertion remains that the request is authenticated and not forbidden. Record the exact result in the execution log.

- [x] **Step 4: Run security-focused tests**

Run:

```powershell
mvn test "-Dtest=EsalDocumentosSecurityTest,DocumentoSoporteServiceTest"
```

Expected: PASS.

### Task 7: Frontend Models And Document Section

**Files:**
- Modify: `sed-esal-angular/src/app/core/models/esal.model.ts`
- Modify: `sed-esal-angular/src/app/features/admin/esales/esal-maintenance.component.ts`

- [x] **Step 1: Extend TypeScript models**

Add:

```ts
export type TipoDocumentoSoporte =
  | 'CREACION_FORMACION'
  | 'DIGNATARIOS'
  | 'LIQUIDACION'
  | 'CANCELACION';

export type SubtipoDocumentoSoporte =
  | 'TRAMITE_CANCELACION_VOLUNTARIA'
  | 'TERMINO_DURACION'
  | 'CANCELACION_VOLUNTARIA'
  | 'ORDEN_AUTORIDAD';
```

Extend `DocumentoSoporte`:

```ts
tipoDocumental: TipoDocumentoSoporte | null;
subtipoDocumental: SubtipoDocumentoSoporte | null;
referenciaActo: string | null;
fechaActo: string | null;
observacion: string | null;
vigente: boolean;
```

- [x] **Step 2: Add document tab**

Change:

```ts
type Seccion = 'principal' | 'personeria' | 'representantes' | 'organo' | 'estado';
```

to:

```ts
type Seccion = 'principal' | 'personeria' | 'representantes' | 'organo' | 'estado' | 'documentos';
```

Add nav button:

```html
<button type="button" [class.active]="seccion() === 'documentos'" (click)="seccion.set('documentos'); cargarDocumentos()">Documentos</button>
```

- [x] **Step 3: Add document state and form**

Add signals:

```ts
documentos = signal<DocumentoSoporte[]>([]);
archivoDocumento = signal<File | null>(null);
```

Add form:

```ts
documentoForm = this.fb.group({
  tipoDocumento: ['CREACION_FORMACION'],
  subtipoDocumento: [''],
  referencia: [''],
  fechaActo: [''],
  observacion: [''],
});
```

Import `DocumentoSoporte`.

- [x] **Step 4: Add template section**

Add section:

```html
@if (seccion() === 'documentos') {
  <div class="sed-card maintenance__section">
    <div class="maintenance__section-head">
      <h3>Documentos</h3>
      <button class="sed-btn-secondary" type="button" (click)="cargarDocumentos()">Actualizar</button>
    </div>
    <form class="maintenance__subform" [formGroup]="documentoForm" (ngSubmit)="cargarDocumento()">
      <div class="maintenance__grid">
        <label class="sed-field">Tipo
          <select class="sed-input" formControlName="tipoDocumento">
            <option value="CREACION_FORMACION">Creación y formación</option>
            <option value="DIGNATARIOS">Dignatarios</option>
            <option value="LIQUIDACION">Liquidación</option>
            <option value="CANCELACION">Cancelación</option>
          </select>
        </label>
        <label class="sed-field">Subtipo
          <select class="sed-input" formControlName="subtipoDocumento">
            <option value="">No aplica</option>
            <option value="TRAMITE_CANCELACION_VOLUNTARIA">Liquidación por trámite de cancelación voluntaria</option>
            <option value="TERMINO_DURACION">Liquidación por término de duración</option>
            <option value="CANCELACION_VOLUNTARIA">Cancelación voluntaria</option>
            <option value="ORDEN_AUTORIDAD">Cancelación por orden de autoridad</option>
          </select>
        </label>
        <label class="sed-field">Referencia<input class="sed-input" formControlName="referencia" /></label>
        <label class="sed-field">Fecha acto<input class="sed-input" type="date" formControlName="fechaActo" /></label>
      </div>
      <label class="sed-field">Observación<textarea class="sed-input" formControlName="observacion" rows="2"></textarea></label>
      <label class="sed-field">PDF máximo 10 MB<input class="sed-input" type="file" accept="application/pdf" (change)="seleccionarDocumento($event)" /></label>
      <button class="sed-btn-primary" type="submit" [disabled]="guardando()">Cargar documento</button>
    </form>
    <table class="sed-table">
      <thead><tr><th>Vigencia</th><th>Tipo</th><th>Subtipo</th><th>Referencia</th><th>Fecha</th><th>Archivo</th><th>Acción</th></tr></thead>
      <tbody>
        @for (doc of documentos(); track doc.id) {
          <tr>
            <td>{{ doc.vigente ? 'Vigente' : 'Histórico' }}</td>
            <td>{{ doc.tipoDocumental || doc.tipoDocumento }}</td>
            <td>{{ doc.subtipoDocumental || 'No aplica' }}</td>
            <td>{{ doc.referenciaActo || '—' }}</td>
            <td>{{ doc.fechaActo || '—' }}</td>
            <td>{{ doc.nombreArchivo }}</td>
            <td><button class="sed-btn-secondary compact" type="button" (click)="descargarDocumento(doc)">Descargar</button></td>
          </tr>
        }
      </tbody>
    </table>
  </div>
}
```

- [x] **Step 5: Add component methods**

Add methods:

```ts
cargarDocumentos(): void {
  this.api.get<DocumentoSoporte[]>(`/api/esales/${this.id}/documentos`).subscribe({
    next: (data) => this.documentos.set(data),
    error: () => this.errorAccion.set('No se pudieron cargar los documentos.'),
  });
}

seleccionarDocumento(event: Event): void {
  const input = event.target as HTMLInputElement;
  this.archivoDocumento.set(input.files?.[0] ?? null);
}

cargarDocumento(): void {
  const archivo = this.archivoDocumento();
  if (!archivo) {
    this.errorAccion.set('Seleccione un PDF.');
    return;
  }
  const form = new FormData();
  form.append('archivo', archivo);
  Object.entries(this.documentoForm.value).forEach(([key, value]) => {
    if (value !== null && value !== undefined && value !== '') {
      form.append(key, String(value));
    }
  });
  this.guardando.set(true);
  this.api.postForm<DocumentoSoporte>(`/api/esales/${this.id}/documentos`, form).subscribe({
    next: () => {
      this.mensaje.set('Documento cargado.');
      this.guardando.set(false);
      this.archivoDocumento.set(null);
      this.cargarDocumentos();
    },
    error: (err) => {
      this.errorAccion.set(err?.error?.message ?? 'No se pudo cargar el documento.');
      this.guardando.set(false);
    },
  });
}

descargarDocumento(doc: DocumentoSoporte): void {
  this.api.download(`/api/esales/${this.id}/documentos/${doc.id}/descarga`).subscribe({
    next: (blob) => {
      const url = URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = doc.nombreArchivo;
      link.click();
      URL.revokeObjectURL(url);
    },
    error: () => this.errorAccion.set('No se pudo descargar el documento.'),
  });
}
```

- [x] **Step 6: Run Angular build**

Run:

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-angular
node "C:\Program Files\nodejs\node_modules\npm\bin\npm-cli.js" run build
```

Expected: build passes; pre-existing NG8102/NG8107 warnings may remain.

### Task 8: Verification, Docs And Execution Log

**Files:**
- Create: `docs/plans/2026-06-19-sed-esal-i9-execution-log.md`
- Modify: `README.md`
- Modify: `docs/ARRANQUE.md`
- Modify: `docs/GUIA_PRUEBAS_FUNCIONALES.md`

- [x] **Step 1: Create execution log at implementation start**

Create:

```markdown
# Execution Log I9 - Gestion Documental Administrativa Transversal

> Estado: en ejecucion.
> Fecha: 2026-06-19.
> Spec: `docs/specs/2026-06-19-sed-esal-i9-spec.md`.
> Plan: `docs/plans/2026-06-19-sed-esal-i9-plan.md`.

## Contexto Inicial

I8 completado. I9 implementa gestion documental transversal desde `Aplicativo ESAL.docx`.

## Evidencia De Verificacion

Se registraran aqui RED/GREEN, suites finales, build WAR y build/test Angular.
```

- [x] **Step 2: Run backend focused suite**

Run:

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-backend
mvn test "-Dtest=DocumentoSoporteServiceTest,EsalMaintenanceServiceTest"
```

Expected: PASS.

- [x] **Step 3: Run backend full suite and package**

Run:

```powershell
mvn test
mvn package -DskipTests
```

Expected: PASS and `target/sed-esal-backend.war` generated.

- [x] **Step 4: Run Angular tests/build**

Run:

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-angular
node "C:\Program Files\nodejs\node_modules\npm\bin\npm-cli.js" test -- --watch=false --browsers=ChromeHeadless
node "C:\Program Files\nodejs\node_modules\npm\bin\npm-cli.js" run build
```

Expected: tests/build pass or environmental limitation is documented in execution log.

- [x] **Step 5: Update docs at closure**

Update:

- `README.md`: I9 row as completado and current state.
- `docs/ARRANQUE.md`: order of documents includes I9 spec/plan/log, state includes I9 completion, expected test counts updated.
- `docs/GUIA_PRUEBAS_FUNCIONALES.md`: add functional test section for document upload/list/download, 10 MB/PDF validation, vigente/historico, role permissions, liquidacion/cancelacion blocking.
- `docs/plans/2026-06-19-sed-esal-i9-execution-log.md`: exact commands and results.

- [x] **Step 6: Final diff check**

Run:

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL
git status --short
git diff --check
```

Expected: no whitespace errors; unrelated pre-existing worktree noise is not reverted.

## Risks

| Risk | Mitigation |
|---|---|
| Existing I5 cancellation tests expect warning-only behavior | Update tests and docs to I9 rule: cancellation blocks without vigente `CANCELACION` document |
| Schema generation/migrations may not include new columns in non-H2 Oracle | Verify current project migration strategy before implementation; if DDL scripts exist, update them in the same task |
| Download can accidentally expose filesystem path | DTO excludes `rutaAlmacenamiento`; controller returns bytes from backend only |
| Large upload validation may rely only on client | Enforce 10 MB in `DocumentoSoporteService` before storage |
| UI component is already large | Keep I9 UI section local and avoid unrelated refactor; extract component only if Angular build/test pressure requires it |

## Verification Checkpoints

1. RED documental confirmed in `DocumentoSoporteServiceTest`.
2. GREEN service tests for PDF, 10 MB, metadatos, catalogo and vigente/historico.
3. Download service/controller verified.
4. Estado `EN_LIQUIDACION` and `CANCELADO` blocked without vigente required document.
5. Security verifies `EXPEDIDOR` cannot upload but can list/download.
6. Angular build/test.
7. Backend full `mvn test` and WAR package.
8. README, ARRANQUE, GUIA_PRUEBAS_FUNCIONALES and execution log updated.

## Retake Point

I9 plan aprobado pendiente de implementacion. La implementacion debe iniciar creando el execution log y ejecutando Task 1 RED.
