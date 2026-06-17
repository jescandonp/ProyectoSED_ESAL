# Spec I8 - Certificado PDF Exacto desde Plantilla EYRL

> Estado: especificado para revision.
> Fecha: 2026-06-17.
> Sistema: `SED_ESAL`.
> Metodologia: SDD Spec-Anchored.
> Handoff base: `handoff-sed-esal-i7-2026-06-17.md`.
> Plantilla base: `Documentos_Referencia/Plantilla Certificado EYRL.docx`.
> Depende de: I6 certificado narrativo e I7 alineacion UI institucional.

## 1. Objetivo

Abrir una nueva iteracion para que el certificado PDF generado por `SED_ESAL` reproduzca de forma fiel y verificable la plantilla oficial EYRL en Word, no solo su contenido narrativo.

I6 cerro el primer salto funcional: preambulo, narracion, organos, formula de cierre y NOTA 1. I8 eleva el contrato de salida: el PDF debe conservar estructura, orden, textos juridicos, tablas, tipografia, margenes, pie institucional y versionamiento de plantilla conforme al DOCX fuente.

El usuario objetivo es el funcionario expedidor de Inspeccion y Vigilancia que necesita emitir certificados con apariencia institucional equivalente al formato oficial.

## 2. Supuestos

1. La plantilla fuente oficial para I8 es `Documentos_Referencia/Plantilla Certificado EYRL.docx`.
2. El entregable principal sigue siendo PDF desde el backend; no se cambia el endpoint de descarga.
3. La reproduccion "exacta" se mide por contrato visual y textual automatizable, no por edicion manual posterior.
4. Si algun dato de articulo estatutario no existe en el modelo actual, I8 debe documentar el GAP y usar texto controlado, sin inventar datos.
5. `Documentos_Referencia/` sigue siendo local y no se publica sin aprobacion expresa.

## 3. Tech Stack

- Backend: Java, Spring Boot 2.7.x, Maven, OpenPDF (`com.lowagie`).
- Tests backend: JUnit 5, AssertJ, extractor PDF de OpenPDF.
- Frontend: Angular 20 sin cambios esperados en I8.
- Documento fuente: DOCX inspeccionado con `python-docx` para derivar estructura y GAPs.

## 4. Commands

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL
git status --short --branch

Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-backend
mvn test "-Dtest=CertificadoPdfServiceTest,CertificadoAssemblerTest,GeneracionServiceTest"
mvn test
mvn package -DskipTests

Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-angular
node "C:\Program Files\nodejs\node_modules\npm\bin\npm-cli.js" test -- --watch=false --browsers=ChromeHeadless
node "C:\Program Files\nodejs\node_modules\npm\bin\npm-cli.js" run build
```

## 5. Project Structure

```text
docs/specs/2026-06-17-sed-esal-i8-spec.md
docs/plans/2026-06-17-sed-esal-i8-plan.md
docs/plans/2026-06-17-sed-esal-i8-execution-log.md
Documentos_Referencia/Plantilla Certificado EYRL.docx
sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/service/CertificadoPdfService.java
sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/dto/CertificadoNarrativoDto.java
sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/service/CertificadoAssembler.java
sed-esal-backend/src/test/java/co/gov/bogota/sed/esal/service/CertificadoPdfServiceTest.java
```

## 6. Extracto De La Plantilla Fuente

Inspeccion inicial de `Plantilla Certificado EYRL.docx`:

- Tamano pagina: Letter, `7772400 x 10058400` EMU.
- Margenes: izquierdo/derecho `1080135` EMU, superior/inferior `1350645` EMU.
- Header: sin texto visible ni tablas.
- Footer: bloque institucional con:
  - `Av. El Dorado No. 66 - 63`
  - `PBX: 324 1000 - Fax: 315 34 48`
  - `Codigo postal: 111321`
  - `www.educacionbogota.edu.co`
  - `Info: Linea 195`
- Parrafos: 90.
- Tablas: 3.
- Tipografia dominante: Arial 11 pt en titulos y cuerpo.
- Alineacion dominante: centrada en preambulo y justificada en cuerpo.
- Tablas detectadas:
  - Tabla 0: representacion legal, 3 filas x 5 columnas.
  - Tabla 1: junta directiva, 3 filas x 5 columnas.
  - Tabla 2: revisoria fiscal, 4 filas x 3 columnas.

## 7. GAPs Actuales

| GAP | Estado actual I6/I7 | Contrato esperado I8 |
|---|---|---|
| G1 - Tamano de pagina | PDF usa A4 con margenes propios | Usar Letter y margenes equivalentes a la plantilla DOCX |
| G2 - Tipografia | Helvetica 8-14 pt | Arial 11 pt como base, negrillas e italicas segun plantilla |
| G3 - Pie institucional | Solo NOTA 1 y pie tecnico | Incluir footer institucional de direccion, PBX, web e Info Linea 195 |
| G4 - Orden de secciones | Representacion, Junta, Asamblea, Revisoria, Duracion | Respetar orden DOCX: objeto, representacion, funciones, asamblea, funciones, junta, funciones, revisoria, duracion, cierre |
| G5 - Asamblea General | I6 la trata como tabla de miembros si existe | Plantilla contiene seccion narrativa de Asamblea y funciones, sin tabla principal detectada |
| G6 - Revisoria Fiscal | I6 usa tabla de 5 columnas igual a organos | Plantilla usa tabla de 3 columnas: NOMBRE, IDENTIFICACION, CARGO |
| G7 - Textos juridicos | Narracion abreviada y generalizada | Reproducir formulas de la plantilla: articulos estatutarios, "a la fecha de expedicion", "Atentamente" |
| G8 - Campos de articulo | No modelados como campos independientes | Definir politica: extraer si estan en texto fuente o dejar marcador controlado sin inventar |
| G9 - Version de plantilla | `I6-v1` | Actualizar a `I8-EYRL-v1` |
| G10 - Verificacion | Test por texto extraido | Agregar verificacion de pagina, tablas, columnas, footer, version y textos clave |

## 8. Code Style

Mantener estilo Java imperativo simple, sin introducir un motor de templates externo en I8.

```java
private void agregarParrafoJustificado(Document doc, Phrase contenido) throws DocumentException {
    Paragraph parrafo = new Paragraph(contenido);
    parrafo.setAlignment(Element.ALIGN_JUSTIFIED);
    parrafo.setLeading(0, 1.15f);
    parrafo.setSpacingAfter(8);
    doc.add(parrafo);
}
```

Reglas:

- Helpers pequenos para parrafos, tablas, footer y frases mixtas.
- Constantes visibles para version, margenes, fuentes y textos institucionales.
- No duplicar literales juridicos en tests y servicio si se puede centralizar con constantes package-private.

## 9. Testing Strategy

- RED: ampliar `CertificadoPdfServiceTest` con expectativas que hoy fallan: version I8, footer institucional, tabla revisoria de 3 columnas, orden textual y pagina Letter si es verificable con `PdfReader`.
- GREEN: ajustar `CertificadoPdfService` hasta cumplir el contrato.
- Regression: `CertificadoAssemblerTest` y `GeneracionServiceTest` deben seguir verdes.
- Suite final: `mvn test` y `mvn package -DskipTests`.
- Frontend: build/test Angular solo como regresion de cierre; no se esperan cambios Angular.

## 10. Boundaries

- Always:
  - Preservar endpoint de descarga y autorizacion existente.
  - Mantener trazabilidad y almacenamiento de certificado.
  - Usar la plantilla DOCX como fuente de verdad visual/textual.
  - Registrar evidencias en execution log.
- Ask first:
  - Agregar dependencias nuevas.
  - Cambiar modelo de datos o migraciones.
  - Publicar `Documentos_Referencia/` en Git.
  - Reemplazar OpenPDF por otra libreria.
- Never:
  - Inventar numero de articulo estatutario si no existe en datos.
  - Regenerar certificados historicos por lote.
  - Revertir cambios no relacionados del worktree.
  - Mover logica de autorizacion al frontend.

## 11. Success Criteria

1. El PDF usa pagina Letter y margenes equivalentes a la plantilla.
2. El texto extraido contiene los bloques juridicos de la plantilla en el orden esperado.
3. El footer institucional contiene direccion, PBX, codigo postal, web e Info Linea 195.
4. La tabla de representacion legal conserva 5 columnas.
5. La tabla de junta directiva conserva 5 columnas.
6. La tabla de revisoria fiscal conserva 3 columnas.
7. El cierre incluye `Atentamente,` antes del firmante.
8. El pie tecnico identifica `Plantilla: I8-EYRL-v1`.
9. Tests backend pasan.
10. README, ARRANQUE, GUIA_PRUEBAS_FUNCIONALES y execution log quedan actualizados al cierre.

## 12. Open Questions

1. Los articulos estatutarios (`articulo ----`) no existen como campos normalizados. Decision propuesta: I8 no agrega BD; usa texto fuente cuando venga dentro de objeto/facultades y deja marcador controlado cuando no exista.
2. La plantilla no contiene imagen en header segun inspeccion inicial. Si SED exige logos en el PDF final, se requiere artefacto oficial adicional.
3. Confirmar si se acepta que el PDF preserve acentos institucionales aunque el codigo fuente permanezca en ASCII donde sea viable.
