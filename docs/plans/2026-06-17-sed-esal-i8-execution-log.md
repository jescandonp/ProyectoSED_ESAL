# Execution Log I8 - Certificado PDF Exacto desde Plantilla EYRL

> Estado: completado.
> Fecha: 2026-06-17.
> Spec: `docs/specs/2026-06-17-sed-esal-i8-spec.md`.
> Plan: `docs/plans/2026-06-17-sed-esal-i8-plan.md`.
> Handoff base: `handoff-sed-esal-i7-2026-06-17.md`.

## Contexto Inicial

Se retoma `SED_ESAL` despues del cierre I7. El handoff indica:

- Rama: `main`.
- I7 completado.
- Backend sin cambios desde I6.
- Frontend Angular verificado en I7.
- No tocar `.claude/worktrees/practical-chatelet-a3bc4c` salvo instruccion expresa.

## Estado Git Observado

Comando:

```powershell
git status --short --branch
```

Resultado relevante:

```text
## main...origin/main
 ? .claude/worktrees/practical-chatelet-a3bc4c
 D ProyectoESAL.html
 D ProyectoESAL_v2.html
?? handoff-sed-esal-i7-2026-06-17.md
```

Interpretacion:

- Existen cambios no relacionados previos: dos HTML eliminados y el handoff no versionado.
- Esos cambios no se revierten ni se limpian dentro de I8.

## Inspeccion Inicial De Plantilla

Archivo:

```text
Documentos_Referencia/Plantilla Certificado EYRL.docx
```

Hallazgos:

- Documento con 1 seccion.
- Pagina Letter: `7772400 x 10058400` EMU.
- Margenes: izquierdo/derecho `1080135` EMU; superior/inferior `1350645` EMU.
- Header sin texto visible.
- Footer institucional con direccion, PBX, codigo postal, web e Info Linea 195.
- 90 parrafos.
- 3 tablas:
  - Representacion legal: 3 filas x 5 columnas.
  - Junta directiva: 3 filas x 5 columnas.
  - Revisoria fiscal: 4 filas x 3 columnas.
- Tipografia dominante observada: Arial 11 pt.

## GAPs Identificados

1. PDF actual usa A4, no Letter.
2. PDF actual usa Helvetica, no Arial.
3. PDF actual no incluye footer institucional de la plantilla.
4. Orden actual de secciones no coincide completamente con DOCX.
5. Asamblea General requiere tratamiento narrativo segun plantilla, no tabla de miembros por defecto.
6. Revisoria Fiscal requiere tabla de 3 columnas, no tabla de 5 columnas.
7. Falta `Atentamente,` antes de firmante.
8. Version de plantilla sigue en `I6-v1`.
9. Tests actuales validan contenido narrativo, no fidelidad de plantilla.

## Artefactos Creados

- `docs/specs/2026-06-17-sed-esal-i8-spec.md`
- `docs/plans/2026-06-17-sed-esal-i8-plan.md`
- `docs/plans/2026-06-17-sed-esal-i8-execution-log.md`

## Implementacion

Archivos modificados:

- `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/service/CertificadoPdfService.java`
- `sed-esal-backend/src/test/java/co/gov/bogota/sed/esal/service/CertificadoPdfServiceTest.java`
- `README.md`
- `docs/ARRANQUE.md`
- `docs/GUIA_PRUEBAS_FUNCIONALES.md`

Cambios principales:

- `VERSION_PLANTILLA` pasa a `I8-EYRL-v1`.
- Pagina PDF pasa de A4 a Letter.
- Margenes se ajustan a valores equivalentes a la plantilla DOCX.
- Fuente base declarada: Arial.
- Footer institucional agregado en cada pagina.
- Orden juridico reordenado segun plantilla EYRL.
- Asamblea General queda como bloque narrativo y de funciones.
- Junta Directiva conserva tabla de 5 columnas.
- Representacion Legal conserva tabla de 5 columnas.
- Revisoria Fiscal pasa a tabla de 3 columnas.
- Cierre incluye `Atentamente,`.
- Articulos estatutarios no normalizados usan marcador controlado `no registrado`.

## Ajuste Posterior - Header Con Logo Institucional

Fecha: 2026-06-17.

Solicitud: incorporar en el header del formato el logo oficial compartido por el usuario.

Archivo fuente recibido:

```text
C:\Users\jmep2\AppData\Local\Temp\codex-clipboard-db51e4f1-0db6-4e1b-8a10-de9e125ea952.png
```

Implementacion:

- Se agrega el recurso versionado `sed-esal-backend/src/main/resources/certificado/logo-sed-header.png`.
- `CertificadoPdfService` carga el logo desde classpath.
- El encabezado textual anterior se reemplaza por el logo institucional como imagen centrada.
- `CertificadoPdfServiceTest` valida que la primera pagina del PDF tenga una imagen en recursos XObject.

### Ajuste De Tamano Del Logo

Fecha: 2026-06-17.

Solicitud: fijar el logo del header con tamano absoluto:

- Alto: 2,03 cm.
- Ancho: 5,45 cm.

Implementacion:

- `LOGO_HEADER_ANCHO = 154.49f` puntos PDF.
- `LOGO_HEADER_ALTO = 57.54f` puntos PDF.
- El logo usa `scaleAbsolute(LOGO_HEADER_ANCHO, LOGO_HEADER_ALTO)`.
- `CertificadoPdfServiceTest` valida las constantes de tamano y la presencia de imagen en PDF.

Verificacion:

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-backend
mvn test "-Dtest=CertificadoPdfServiceTest"
mvn package -DskipTests
```

Resultado:

- `CertificadoPdfServiceTest`: 2 tests, BUILD SUCCESS.
- Package backend: BUILD SUCCESS.
- Maven copio 1 recurso adicional y genero `sed-esal-backend/target/sed-esal-backend.war`.

## Evidencia De Verificacion

### RED

Comando:

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-backend
mvn test "-Dtest=CertificadoPdfServiceTest"
```

Resultado RED inicial:

- BUILD FAILURE esperado.
- Falla por pagina A4: esperado `612.0f`, recibido `595.0f`.

### GREEN focalizado

Comando:

```powershell
mvn test "-Dtest=CertificadoPdfServiceTest"
```

Resultado:

- 2 tests.
- 0 failures.
- BUILD SUCCESS.

### Bateria enfocada

Comando:

```powershell
mvn test "-Dtest=CertificadoPdfServiceTest,CertificadoAssemblerTest,GeneracionServiceTest"
```

Resultado:

- 11 tests.
- 0 failures.
- BUILD SUCCESS.

### Suite backend completa

Comando:

```powershell
mvn test
```

Resultado:

- 137 tests.
- 0 failures.
- BUILD SUCCESS.

### Package backend

Comando:

```powershell
mvn package -DskipTests
```

Resultado:

- BUILD SUCCESS.
- WAR generado: `sed-esal-backend/target/sed-esal-backend.war`.

### Regresion Angular

Comandos:

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-angular
node "C:\Program Files\nodejs\node_modules\npm\bin\npm-cli.js" test -- --watch=false --browsers=ChromeHeadless
node "C:\Program Files\nodejs\node_modules\npm\bin\npm-cli.js" run build
```

Resultado:

- ChromeHeadless: `TOTAL: 5 SUCCESS`.
- Build Angular: exitoso.
- Persisten advertencias NG8102/NG8107 preexistentes sobre nullish/optional chaining.

## Retake Point

## Ajuste funcional - Numeracion interna no visible en PDF

Fecha: 2026-06-18.

Decision de area funcional: la numeracion automatica del certificado corresponde a un codigo interno. Se conserva para trazabilidad, almacenamiento y contratos existentes, pero no debe imprimirse en el PDF del certificado.

### RED

Comando:

```powershell
mvn test "-Dtest=CertificadoPdfServiceTest"
```

Resultado esperado inicial:

- BUILD FAILURE.
- El PDF aun contenia `No. ESAL-2026-000001` y `No. ESAL-2026-000008`.

### GREEN

Cambio aplicado:

- `CertificadoPdfService` deja de renderizar `No. {numeroCertificado}` en el encabezado del PDF.
- Se mantiene la fecha de expedicion visible.
- No se modifica `GeneracionService`, `NumeracionService`, persistencia, nombre controlado de archivo ni trazabilidad interna.

Verificacion:

```powershell
mvn test "-Dtest=CertificadoPdfServiceTest"
mvn test "-Dtest=CertificadoPdfServiceTest,GeneracionServiceTest"
```

Resultado:

- `CertificadoPdfServiceTest`: 2 tests, 0 failures, BUILD SUCCESS.
- Bateria PDF/generacion: 7 tests, 0 failures, BUILD SUCCESS.
- Persisten advertencias H2 de cierre de base al finalizar tests Spring, sin fallar Maven.

## Retake Point

I8 completado con ajuste de numeracion interna no visible en PDF. Siguiente sesion debe abrir nueva SPEC/plan si se solicita otro incremento.
