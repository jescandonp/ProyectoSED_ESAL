# Execution Log I10 - Seleccion De Plantilla EYRL Por Estado Y Documento Vigente

> Estado: completado.
> Fecha: 2026-06-20.
> Spec: `docs/specs/2026-06-20-sed-esal-i10-spec.md`.
> Plan: `docs/plans/2026-06-20-sed-esal-i10-plan.md`.

## Contexto Inicial

I9 completado. I10 implementa seleccion explicita de plantilla de certificado EYRL por estado y documento vigente I9.

Estado git observado al inicio de implementacion:

```text
## main...origin/main
 ? .claude/worktrees/practical-chatelet-a3bc4c
```

Interpretacion:

- La SPEC y el PLAN I10 fueron commit/push antes de iniciar implementacion.
- El unico pendiente local observado es un artefacto de worktree `.claude/...` no versionado.
- La implementacion I10 no limpia ni revierte ese artefacto local.

## Evidencia De Verificacion

Se registraran aqui RED/GREEN, suites finales, WAR, build Angular y handoffs por hitos.

## Task 1 - RED Selector De Plantilla I10

Estado: completado.

Objetivo: introducir pruebas RED para la seleccion de plantilla por estado y documento vigente I9.

Cambios:

- Creado `CertificadoTemplateSelectorTest` con cobertura para:
  - `SUSPENDIDO` -> `EYRL_SUSPENDIDA`;
  - `EN_LIQUIDACION` + `LIQUIDACION.TRAMITE_CANCELACION_VOLUNTARIA`;
  - `EN_LIQUIDACION` + `LIQUIDACION.TERMINO_DURACION`;
  - `CANCELADO` + `CANCELACION.CANCELACION_VOLUNTARIA`;
  - `CANCELADO` + `CANCELACION.ORDEN_AUTORIDAD`;
  - fallback `EYRL_DEFAULT`;
  - ignorar documentos no vigentes;
  - preferir documento vigente compatible entre historicos.

Verificacion RED:

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-backend
mvn test "-Dtest=CertificadoTemplateSelectorTest"
```

Resultado:

- BUILD FAILURE esperado.
- Falla por compilacion: no existen `CertificadoPlantilla` ni `CertificadoTemplateSelector`.

## Task 2 - GREEN Selector And Variant Contract

Estado: completado.

Objetivo: implementar contrato minimo de variantes I10 y selector por estado/documento vigente.

Cambios:

- Creado `CertificadoPlantilla` con versiones tecnicas:
  - `I10-EYRL-DEFAULT-v1`;
  - `I10-EYRL-SUSPENDIDA-v1`;
  - `I10-EYRL-LIQUIDACION-TRAMITE-v1`;
  - `I10-EYRL-LIQUIDACION-TERMINO-v1`;
  - `I10-EYRL-CANCELADA-VOLUNTARIA-v1`;
  - `I10-EYRL-CANCELADA-AUTORIDAD-v1`.
- Creado `CertificadoTemplateSelector`.
- El selector usa `EstadoEsal` y solo documentos `vigente=true` para liquidacion/cancelacion.

Verificacion GREEN:

```powershell
mvn test "-Dtest=CertificadoTemplateSelectorTest"
```

Resultado:

- 8 tests.
- 0 failures.
- BUILD SUCCESS.

## Handoff Hito 1 - Task 2 Cerrada

Retake:

1. Continuar en Task 3 del plan I10.
2. `CertificadoTemplateSelectorTest` esta verde con 8 pruebas.
3. Pendiente integrar selector en `CertificadoAssembler` y exponer plantilla/metadatos en `CertificadoNarrativoDto`.

## Task 3 - Integrar Selector En Assembler Y DTO

Estado: en ejecucion.

Objetivo: extender el DTO narrativo y el assembler para que el flujo de generacion reciba la variante I10 y los metadatos del documento vigente I9.

Verificacion RED:

```powershell
mvn test "-Dtest=CertificadoAssemblerTest"
```

Resultado:

- BUILD FAILURE esperado.
- Falla por compilacion: `CertificadoNarrativoDto` no expone `getPlantilla()`, `getDocumentoPlantillaReferencia()` ni `getDocumentoPlantillaSubtipo()`.
- El fixture documental se ajusto para usar `EstadoValidacionDocumento.PENDIENTE`, ya que el dominio I9 usa enum y no `String`.

Cambios GREEN:

- `CertificadoNarrativoDto` expone:
  - `plantilla`;
  - `documentoPlantillaReferencia`;
  - `documentoPlantillaFechaActo`;
  - `documentoPlantillaSubtipo`.
- `CertificadoAssembler` consulta `DocumentoSoporteRepository`, invoca `CertificadoTemplateSelector` y copia metadatos del documento vigente que decide la plantilla.

Verificacion GREEN:

```powershell
mvn test "-Dtest=CertificadoTemplateSelectorTest,CertificadoAssemblerTest"
```

Resultado:

- 15 tests.
- 0 failures.
- BUILD SUCCESS.
- Persisten advertencias H2 de cierre de base al finalizar JVM, sin fallar Maven.

## Handoff Hito 2 - Task 3 Cerrada

Retake:

1. Continuar en Task 4 del plan I10.
2. Selector y assembler estan verdes.
3. Pendiente agregar RED PDF para versiones y textos clave por variante.

## Task 4 - RED PDF Variants I10

Estado: completado.

Objetivo: exigir versiones y textos clave I10 en el PDF para default, suspendida, liquidacion y cancelacion.

Cambios:

- `CertificadoPdfServiceTest` actualiza expectativas default de `I8-EYRL-v1` a `I10-EYRL-DEFAULT-v1`.
- Agrega pruebas para:
  - suspendida;
  - liquidacion por termino;
  - liquidacion por tramite;
  - cancelada voluntariamente;
  - cancelada por orden de autoridad.

Verificacion RED:

```powershell
mvn test "-Dtest=CertificadoPdfServiceTest"
```

Resultado:

- BUILD FAILURE esperado.
- 7 failures.
- El servicio aun imprime `Plantilla: I8-EYRL-v1`.
- No existen aun los bloques de texto I10 esperados por variante.

## Task 5 - GREEN PDF Variant Rendering

Estado: completado.

Objetivo: renderizar versiones tecnicas y textos clave por variante I10 en el PDF.

Cambios:

- `CertificadoPdfService` resuelve `CertificadoPlantilla` desde `CertificadoNarrativoDto`.
- El pie tecnico imprime `plantilla.getVersion()`.
- Se agregan bloques especificos para:
  - `EYRL_SUSPENDIDA`;
  - `EYRL_LIQUIDACION_TRAMITE_CANCELACION_VOLUNTARIA`;
  - `EYRL_LIQUIDACION_TERMINO_DURACION`;
  - `EYRL_CANCELADA_VOLUNTARIAMENTE`;
  - `EYRL_CANCELADA_ORDEN_AUTORIDAD`.
- `GeneracionService` persiste `plantillaVersion` desde `narrativo.getPlantilla().getVersion()`.
- Se ajustaron aserciones PDF largas a fragmentos estables por saltos de linea del extractor.

Verificacion GREEN PDF:

```powershell
mvn test "-Dtest=CertificadoPdfServiceTest"
```

Resultado:

- 7 tests.
- 0 failures.
- BUILD SUCCESS.

Verificacion focalizada de regresion:

```powershell
mvn test "-Dtest=CertificadoTemplateSelectorTest,CertificadoAssemblerTest,CertificadoPdfServiceTest,GeneracionServiceTest"
```

Resultado:

- 27 tests.
- 0 failures.
- BUILD SUCCESS.
- Persisten advertencias H2 de cierre de base al finalizar JVM, sin fallar Maven.

## Handoff Hito 3 - Task 5 Cerrada

Retake:

1. Continuar en Task 6 del plan I10.
2. Selector, assembler, PDF y generacion focalizada estan verdes.
3. Pendiente suite backend completa, WAR y documentacion de cierre.

## Task 6 - Generation Flow Regression And Historical Safety

Estado: completado.

Objetivo: verificar el flujo de generacion, suite backend completa y empaquetado WAR.

Cambios:

- `GeneracionServiceTest.generar_esalCompleta_creaConNumeroCertificado` ahora valida que una ESAL activa persiste `plantillaVersion = I10-EYRL-DEFAULT-v1`.

Verificacion generacion:

```powershell
mvn test "-Dtest=GeneracionServiceTest"
```

Resultado:

- 5 tests.
- 0 failures.
- BUILD SUCCESS.
- Persisten advertencias H2 de cierre de base al finalizar JVM, sin fallar Maven.

Verificacion suite completa:

```powershell
mvn test
```

Resultado:

- 164 tests.
- 0 failures.
- 0 errors.
- 0 skipped.
- BUILD SUCCESS.

Empaquetado:

```powershell
mvn package -DskipTests
```

Resultado:

- BUILD SUCCESS.
- WAR generado: `sed-esal-backend/target/sed-esal-backend.war`.

## Task 7 - Docs, Functional Guide And Final Handoff

Estado: completado.

Objetivo: cerrar documentacion de I10, verificar build Angular y dejar punto de retoma.

Cambios documentales:

- `README.md` actualizado a I10 completado y 164 tests backend.
- `docs/ARRANQUE.md` actualizado con orden de lectura I10, artefactos fuente y estado de verificacion.
- `docs/GUIA_PRUEBAS_FUNCIONALES.md` actualizado con escenarios funcionales I10 por plantilla.
- `docs/plans/2026-06-20-sed-esal-i10-plan.md` marcado como completado.
- `docs/Handoff/handoff-20260620-i10-closed-retake-i11.md` creado.

Verificacion Angular:

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-angular
node "C:\Program Files\nodejs\node_modules\npm\bin\npm-cli.js" run build
```

Resultado:

- BUILD SUCCESS.
- Bundle generado en `sed-esal-angular/dist/sed-esal-angular`.
- Persisten advertencias NG8102/NG8107 no bloqueantes ya conocidas en componentes Angular existentes.

Chequeo final de diff:

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL
git diff --check
git status --short --branch
```

Resultado:

- `git diff --check` sin errores de whitespace.
- Warnings no bloqueantes: permiso denegado al ignore global `C:\Users\jmep2/.config/git/ignore` y normalizacion CRLF esperada en archivos de trabajo.
- `git status` muestra cambios I10 pendientes y conserva sin tocar `.claude/worktrees/practical-chatelet-a3bc4c`.

## Cierre I10

Estado final:

- Selector I10 verde: 8 tests.
- Selector + assembler verde: 15 tests.
- PDF I10 verde: 7 tests.
- Regresion focalizada selector/assembler/PDF/generacion verde: 27 tests.
- Generacion verde: 5 tests.
- Suite backend completa: 164 tests, 0 failures, 0 errors, 0 skipped.
- WAR generado: `sed-esal-backend/target/sed-esal-backend.war`.
- Build Angular: BUILD SUCCESS.

Desviaciones controladas:

- `GeneracionServiceTest` se reforzo sobre el caso existente para validar `plantillaVersion = I10-EYRL-DEFAULT-v1`, evitando duplicar una prueba de generacion equivalente.
- Las aserciones PDF de textos largos se ajustaron a fragmentos estables por saltos de linea del extractor, manteniendo la garantia sobre version y bloque juridico de cada variante.
- El artefacto local `.claude/worktrees/practical-chatelet-a3bc4c` permanece sin tocar y fuera del alcance de I10.
