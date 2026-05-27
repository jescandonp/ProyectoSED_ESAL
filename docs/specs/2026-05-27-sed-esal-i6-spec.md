# Spec I6 - Fidelidad del Certificado PDF a Plantilla Oficial

> Estado: aprobado para diseno.
> Fecha: 2026-05-27.
> Sistema: `SED_ESAL`.
> Metodologia: SDD Spec-Anchored.
> PRD base: `docs/specs/2026-05-09-sed-esal-certificados-prd.md`.
> Depende de: `docs/specs/2026-05-21-sed-esal-i5-spec.md`.

## 1. Objetivo

Cerrar los GAPs entre el certificado PDF generado actualmente y la plantilla oficial Word
(`Plantilla Certificado EYRL.docx`) usada por la Direccion de Inspeccion y Vigilancia.

El PDF generado en I3 tiene formato tabular (etiqueta/valor) y omite el preambulo juridico,
la formula de cierre, la nota legal al pie, las tablas estructuradas de organos y la narracion
prosa exigida por el formato institucional.

I6 reemplaza ese layout por el formato narrativo de la plantilla, sin romper la logica de
bloqueos, trazabilidad ni inmutabilidad de certificados historicos ya emitidos.

## 2. Alcance

Incluye:

- Nuevo DTO `CertificadoNarrativoDto` con estructura tipada para todos los bloques del PDF.
- Nuevo `CertificadoAssembler` que construye el DTO desde el dominio (sin tocar `PreviewService`).
- Reescritura de `CertificadoPdfService` con layout narrativo fiel a la plantilla.
- Seccion REPRESENTACION LEGAL como tabla con columnas NOMBRE, IDENTIFICACION, CARGO, ACTA.
- Seccion JUNTA DIRECTIVA como tabla (miembros de `OrganoAdministracion` con organo = JUNTA DIRECTIVA).
- Seccion ASAMBLEA GENERAL como tabla (si existen miembros con organo = ASAMBLEA GENERAL).
- Seccion REVISORIA FISCAL como tabla (usando `TipoNombramiento.REVISOR_FISCAL_PRINCIPAL/SUPLENTE`).
- Seccion DURACION con texto narrativo.
- Preambulo juridico fijo: cargo firmante, decreto habilitante, CERTIFICA.
- Formula de cierre con fecha en letras.
- NOTA 1 al pie de pagina.
- Actualizacion de `VERSION_PLANTILLA` de I3-v1 a I6-v1.
- Tests TDD para el assembler y el PDF service.

Excluye:

- Campos de numero de articulo estatutario como campo independiente en BD
  (el texto del objeto social y facultades ya los contiene cuando aplica).
- Campo RADICADO SED en tablas de organos (sin integracion de correspondencia en I6).
- Lineas Elaboro/Reviso/Aprobo (requieren UI propia en iteracion futura).
- Cambios en `PreviewCertificadoDto` ni en `PreviewService`.
- Cambios en la UI Angular (el endpoint de descarga no cambia, solo el contenido del PDF).
- Regeneracion de certificados historicos ya emitidos.

## 3. GAPs Cerrados

| # | GAP | Estado antes | Estado despues |
|---|-----|-------------|----------------|
| G1 | Preambulo juridico | Ausente | Incluido: cargo firmante, decretos, CERTIFICA |
| G2 | Formato narrativo | Tablas etiqueta/valor | Prosa juridica para datos principales |
| G3 | Tablas de organos estructuradas | Pares planos | Tabla NOMBRE, IDENTIFICACION, CARGO, ACTA |
| G4 | Formula de cierre | Ausente | "Se expide en Bogota D.C., a los..." |
| G5 | NOTA 1 al pie | Ausente | Nota legal de alcance del certificado |
| G6 | Secciones JUNTA/ASAMBLEA/REVISORIA | Ausentes o mezcladas | Secciones separadas segun tipo de organo |
| G7 | Decreto habilitante | Ausente | Decretos 479/2024 y 650/2025 en preambulo |

## 4. Diseno

### 4.1 Flujo de Generacion

```
CertificadoController
  → GeneracionService.generarCertificado(esalId, usuario)
      → CertificadoAssembler.ensamblar(esalId)          [NUEVO]
          consulta: Esal, PersoneriaJuridica, Nombramiento[], OrganoAdministracion[]
          retorna: CertificadoNarrativoDto               [NUEVO]
      → CertificadoPdfService.generar(narrativo, nroCert, firmante, fecha)  [MODIFICADO]
          retorna: byte[]
      → Certificado guardado (inmutable, sin cambio)
```

### 4.2 `CertificadoNarrativoDto`

Clases y campos:

```
CertificadoNarrativoDto
  String nombre
  String idSipej
  String nit
  String domicilio
  String correoElectronico
  String terminoDuracion
  String objetoSocial
  EstadoEsal estado
  String alertaEstado

  // Personeria juridica
  String resolucionPersoneria
  LocalDate fechaResolucion
  String entidadQueExpide
  String inscripcion
  LocalDate fechaInscripcion

  // Organos
  List<MiembroDto> representantesLegales
  String facultadesRepresentante
  List<MiembroDto> miembrosJunta
  List<MiembroDto> miembrosAsamblea
  List<MiembroDto> revisoresFiscales

  static class MiembroDto
    String nombre
    String tipoDocumento
    String numeroDocumento
    String cargo
    String actaNombramiento
    String radicadoSed     // siempre null en I6
```

### 4.3 `CertificadoAssembler`

Logica de construccion desde el dominio:

- `representantesLegales`: `Nombramiento` con tipo `REPRESENTANTE_LEGAL` o `REPRESENTANTE_LEGAL_SUPLENTE`,
  ordenados por tipo (principal primero). Solo los que tienen `vigente = true`.
- `facultadesRepresentante`: `facultadesLimitaciones` del representante legal principal.
- `miembrosJunta`: `OrganoAdministracion` donde `organo` contiene "JUNTA" (case-insensitive).
- `miembrosAsamblea`: `OrganoAdministracion` donde `organo` contiene "ASAMBLEA" (case-insensitive).
- `revisoresFiscales`: `Nombramiento` con tipo `REVISOR_FISCAL_PRINCIPAL` o `REVISOR_FISCAL_SUPLENTE`.
- Si `PersoneriaJuridica` esta vacia, los campos de personeria quedan null.
- `alertaEstado`: mismo criterio que en `PreviewService`.

### 4.4 Layout del PDF Narrativo

Bloques en orden:

1. **Encabezado institucional** (centrado, fuente pequeña gris):
   "ALCALDIA MAYOR DE BOGOTA D.C."
   "Secretaria de Educacion del Distrito - SED"

2. **Titulo** (centrado, verde, 14pt bold):
   "CERTIFICADO DE EXISTENCIA Y REPRESENTACION LEGAL"

3. **Numero y fecha** (centrado):
   "No. [nroCert]   Expedido: [dd/MM/yyyy HH:mm]"

4. **Preambulo** (centrado, bold):
   "LA SUSCRITA DIRECTORA DE INSPECCION Y VIGILANCIA"
   "En uso de las facultades concedidas por los Decretos Distritales 479 de 2024 y 650 de 2025"
   "CERTIFICA"

5. **Parrafo narrativo inicial** (justificado, 10pt):
   "Que, la entidad sin animo de lucro denominada **[nombre]**, cuenta con domicilio en
   **[domicilio]**, correo electronico **[correo]**, se encuentra registrada en el Sistema
   de Informacion de Personas Juridicas SIPEJ e identificada con ID. **[idSipej]**, NIT
   **[nit]**, tiene personeria juridica vigente reconocida mediante la Resolucion No.
   **[resolucion]** del **[fechaResolucion]** expedida por **[entidadQueExpide]**.
   Inscripcion **[inscripcion]** del **[fechaInscripcion]**."
   Si algun campo es null, se omite esa clausula de la narracion.

6. **Objeto social** (justificado):
   "Que, revisados los estatutos de la entidad, su objeto social es el siguiente:
   «[objetoSocial]»"

7. **Representacion legal** (titulo seccion + tabla):
   Titulo: "REPRESENTACION LEGAL:"
   Tabla: NOMBRE | IDENTIFICACION | CARGO | ACTA NOMBRAMIENTO | RADICADO SED
   Nota: RADICADO SED siempre vacio en I6.
   Si `facultadesRepresentante` no es null:
   "FUNCIONES DE LA REPRESENTACION LEGAL: «[facultadesRepresentante]»"

8. **Junta Directiva** (si `miembrosJunta` no esta vacio):
   Titulo: "JUNTA DIRECTIVA:"
   Tabla: NOMBRE | IDENTIFICACION | CARGO | ACTA NOMBRAMIENTO | RADICADO SED

9. **Asamblea General** (si `miembrosAsamblea` no esta vacio):
   Titulo: "ASAMBLEA GENERAL:"
   Tabla: NOMBRE | IDENTIFICACION | CARGO | ACTA NOMBRAMIENTO | RADICADO SED

10. **Revisoria Fiscal** (si `revisoresFiscales` no esta vacio):
    Titulo: "REVISORIA FISCAL:"
    Tabla: NOMBRE | IDENTIFICACION | CARGO | ACTA NOMBRAMIENTO | RADICADO SED

11. **Duracion** (parrafo):
    "DURACION: De acuerdo con los estatutos, la entidad tendra una duracion [terminoDuracion]."

12. **Alerta de estado** (si `alertaEstado` != null, texto en amarillo bold):
    Mismos textos legales de I3 segun estado SUSPENDIDO/EN_LIQUIDACION/CANCELADO.

13. **Formula de cierre** (justificado):
    "Se expide en Bogota D.C., a los [dd] ([dd_letras]) dias del mes de [mes] de dos mil
    [anio_letras] (20[yy])."

14. **Firmante** (verde bold + cargo + institucion):
    "[firmanteNombre]"
    "[firmanteCargo]"
    "Secretaria de Educacion del Distrito"

15. **Pie tecnico** (derecha, gris 8pt):
    "Plantilla: I6-v1  |  Generado: [dd/MM/yyyy HH:mm]"

16. **NOTA 1** (pie, gris 7pt, separador horizontal):
    "NOTA 1: Este certificado de existencia y representacion legal NO hace las veces de
    autorizacion o licencia de funcionamiento de los establecimientos educativos presentes
    y futuros de propiedad de la entidad. [idSipej] - [nombre]"

### 4.5 Invariantes

- Los certificados historicos ya emitidos (filas en `ESAL_CERTIFICADO`) NO se regeneran.
  El PDF se genera de nuevo en el momento de descarga, pero con los datos congelados en el
  momento de emision (dato almacenado en el certificado historico).
- La logica de bloqueo y generacion habilitada permanece en `PreviewService` sin cambios.
- El endpoint `/api/certificados/{id}/descargar` no cambia su firma.
- `GeneracionService` sigue siendo el unico punto de entrada a la generacion de PDF.

### 4.6 Estrategia de Test

- `CertificadoAssemblerTest`: verifica que dado un conjunto de entidades de dominio,
  el assembler produce el DTO correcto (campos mapeados, organos separados por tipo).
- `CertificadoPdfServiceTest`: verifica que dado un `CertificadoNarrativoDto` valido,
  `generar()` retorna un array de bytes no vacio (smoke test, sin parsear el PDF).
- Tests de regresion existentes no deben romperse.

## 5. Riesgos y Limitaciones

| Riesgo | Mitigacion |
|--------|-----------|
| `OrganoAdministracion.organo` es texto libre — puede no coincidir con "JUNTA" o "ASAMBLEA" | El assembler usa contains case-insensitive; documentar en GUIA_PRUEBAS_FUNCIONALES |
| La formula de cierre en letras requiere conversion de numeros a palabras en espanol | Implementar helper estatico `FechaEnLetras` restringido a fechas del siglo XXI |
| Certificados historicos emitidos con I3-v1 muestran el layout antiguo al descargarse | Aceptado: el PDF se regenera pero con datos congelados; el layout refleja la version actual |
| El campo `VERSION_PLANTILLA` pasa a I6-v1 — los tests que lo verifican deben actualizarse | Buscar y actualizar referencias a "I3-v1" en los tests |

## 6. Criterios de Exito

- El PDF generado para una ESAL de prueba contiene el preambulo, la narracion inicial,
  las tablas de organos con cabeceras, la formula de cierre y la NOTA 1.
- `mvn test` pasa con 131+ tests en verde.
- El endpoint de descarga retorna el PDF autenticado con el nuevo layout (verificacion manual).
- El campo VERSION_PLANTILLA del pie tecnico es "I6-v1".

## 7. Archivos Involucrados

### Backend — nuevos
- `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/dto/CertificadoNarrativoDto.java`
- `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/service/CertificadoAssembler.java`
- `sed-esal-backend/src/test/java/co/gov/bogota/sed/esal/service/CertificadoAssemblerTest.java`

### Backend — modificados
- `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/service/CertificadoPdfService.java`
- `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/service/GeneracionService.java`
- `sed-esal-backend/src/test/java/co/gov/bogota/sed/esal/service/CertificadoPdfServiceTest.java`
  (si existe; si no, crearlo)

### Documentacion — actualizada
- `docs/GUIA_PRUEBAS_FUNCIONALES.md` — agregar seccion de verificacion del layout I6
- `README.md` — actualizar estado I6

### No se tocan
- `PreviewService.java`
- `PreviewCertificadoDto.java`
- Frontend Angular (ningun cambio)
- Base de datos (sin migraciones)
