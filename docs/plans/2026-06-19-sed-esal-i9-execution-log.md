# Execution Log I9 - Gestion Documental Administrativa Transversal

> Estado: completado.
> Fecha: 2026-06-19.
> Spec: `docs/specs/2026-06-19-sed-esal-i9-spec.md`.
> Plan: `docs/plans/2026-06-19-sed-esal-i9-plan.md`.

## Contexto Inicial

I8 completado. I9 implementa gestion documental transversal desde `Documentos_Referencia/Iteracion/Aplicativo ESAL.docx`.

Estado git observado al inicio de implementacion:

```text
 ? .claude/worktrees/practical-chatelet-a3bc4c
 M AGENTS.md
 D handoff-dxph2wrh.md
 D handoff-sed-esal-i7-2026-06-17.md
 D handoff-yc5z3keh.md
?? docs/Handoff/
?? docs/plans/2026-06-19-sed-esal-i9-plan.md
?? docs/specs/2026-06-19-sed-esal-i9-spec.md
```

Interpretacion:

- Existen cambios previos no relacionados en handoffs y `AGENTS.md`.
- I9 no revierte ni limpia esos cambios.
- La implementacion se limita a SPEC/PLAN/log I9 y archivos backend/frontend/documentacion necesarios.

## Task 1 - RED Backend Documental I9

Estado: completado.

Objetivo: agregar tests RED para validaciones I9 de documentos soporte: referencia, fecha, 10 MB, subtipo incompatible y version vigente/historico.

Cambios:

- `DocumentoSoporteServiceTest` amplia contrato I9 con tests para:
  - carga sin referencia;
  - carga sin fecha de acto;
  - PDF mayor a 10 MB;
  - subtipo incompatible;
  - reemplazo de vigente conservando historico.

Verificacion RED:

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-backend
mvn test "-Dtest=DocumentoSoporteServiceTest"
```

Resultado:

- BUILD FAILURE esperado.
- Falla por compilacion/test runtime pendiente:
  - `DocumentoSoporteService.registrar(...)` aun no acepta `tipoDocumento`, `subtipoDocumento`, `referencia`, `fechaActo`, `observacion`.
  - `DocumentoSoporteDto.isVigente()` aun no existe.

## Task 2 - Domain, DTO And Repository Contract

Estado: completado.

Objetivo: agregar catalogo documental I9, metadatos persistidos, DTO extendido y consultas de vigencia.

Cambios:

- Creados enums:
  - `TipoDocumentoSoporte`
  - `SubtipoDocumentoSoporte`
- `DocumentoSoporte` extendido con:
  - `tipoDocumental`
  - `subtipoDocumental`
  - `referenciaActo`
  - `fechaActo`
  - `observacion`
  - `vigente`
- `DocumentoSoporteDto` extendido con metadatos y `isVigente()`.
- `DocumentoSoporteRepository` extiende consultas por vigente y orden de historico.

Verificacion:

```powershell
mvn test "-Dtest=DocumentoSoporteServiceTest"
```

Resultado:

- BUILD FAILURE esperado.
- Falla reducida a firma pendiente en `DocumentoSoporteService.registrar(...)`.

## Task 3 - DocumentoSoporteService I9 Rules

Estado: completado.

Objetivo: implementar validaciones I9, reemplazo de vigente, catalogo documental y mapeo DTO.

Cambios:

- `AuditoriaAcciones` agrega constantes I9 para documento creado, reemplazo de vigencia, descarga y bloqueos de estado.
- `DocumentoSoporteService` agrega firma I9 de `registrar(...)` con:
  - validacion PDF;
  - limite 10 MB;
  - parseo de `TipoDocumentoSoporte`;
  - parseo de `SubtipoDocumentoSoporte`;
  - validacion tipo/subtipo;
  - referencia y fecha obligatorias;
  - reemplazo de vigente por ESAL+tipo+subtipo;
  - mapeo DTO completo.
- Se conserva una sobrecarga legacy temporal para llamadas existentes mientras se actualiza controller/UI.

Verificacion GREEN:

```powershell
mvn test "-Dtest=DocumentoSoporteServiceTest"
```

Resultado:

- 10 tests.
- 0 failures.
- BUILD SUCCESS.
- Persisten advertencias H2 de cierre de base al finalizar JVM, sin fallar Maven.

## Handoff Hito 1 - Task 3 Cerrada

Retake:

1. Continuar en Task 4 del plan I9.
2. `DocumentoSoporteServiceTest` esta verde con 10 pruebas.
3. Pendiente implementar descarga autenticada: `AlmacenamientoService.leer`, `DocumentoSoporteService.descargar` y endpoint `/api/esales/{id}/documentos/{documentoId}/descarga`.

Archivos tocados hasta este hito:

- `sed-esal-backend/src/test/java/co/gov/bogota/sed/esal/service/DocumentoSoporteServiceTest.java`
- `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/domain/enums/TipoDocumentoSoporte.java`
- `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/domain/enums/SubtipoDocumentoSoporte.java`
- `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/domain/DocumentoSoporte.java`
- `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/dto/DocumentoSoporteDto.java`
- `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/repository/DocumentoSoporteRepository.java`
- `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/service/DocumentoSoporteService.java`
- `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/service/AuditoriaAcciones.java`

## Task 4 - Descarga Autenticada Backend

Estado: completado.

Objetivo: agregar lectura desde almacenamiento, servicio de descarga y endpoint backend autenticado.

Cambios:

- `AlmacenamientoService` expone lectura binaria por ruta almacenada.
- `LocalDevAlmacenamientoService` implementa descarga desde disco local.
- `TestAlmacenamientoService` conserva bytes en memoria para pruebas de carga/descarga.
- `DocumentoSoporteService.descargar(...)` valida pertenencia documento-ESAL, lee bytes y registra auditoria.
- `EsalController` expone `GET /api/esales/{id}/documentos/{documentoId}/descarga` con `Content-Disposition`, `application/pdf` y cache control.

Verificacion GREEN:

```powershell
mvn test "-Dtest=DocumentoSoporteServiceTest"
```

Resultado:

- 11 tests.
- 0 failures.
- BUILD SUCCESS.
- Persisten advertencias H2 de cierre de base al finalizar JVM, sin fallar Maven.

## Handoff Hito 2 - Task 4 Cerrada

Retake:

1. Continuar en Task 5 del plan I9.
2. `DocumentoSoporteServiceTest` esta verde con 11 pruebas.
3. Pendiente bloquear cambio de estado a `EN_LIQUIDACION` y cancelacion si no existe documento vigente obligatorio.

## Task 5 - Bloqueo De Estados Sin Documento Vigente

Estado: completado.

Objetivo: impedir que una ESAL pase a `EN_LIQUIDACION` o `CANCELADO` sin documento vigente obligatorio segun catalogo I9.

Cambios:

- `EsalMaintenanceServiceTest` agrega RED para:
  - bloqueo de `EN_LIQUIDACION` sin documento vigente tipo `LIQUIDACION`;
  - bloqueo de cancelacion sin documento vigente tipo `CANCELACION`;
  - cancelacion positiva con fixture documental vigente.
- `EsalMaintenanceService` valida documento vigente antes de liquidacion/cancelacion.
- El bloqueo registra auditoria con resultado de error.
- El flujo legacy de advertencia no bloqueante por PDF faltante deja de ejecutarse en cancelacion.

Verificacion RED:

```powershell
mvn test "-Dtest=EsalMaintenanceServiceTest,DocumentoSoporteServiceTest"
```

Resultado:

- BUILD FAILURE esperado.
- Fallaron 3 pruebas: liquidacion no bloqueaba, cancelacion no bloqueaba y cancelacion positiva aun generaba advertencia legacy.

Verificacion GREEN:

```powershell
mvn test "-Dtest=EsalMaintenanceServiceTest,DocumentoSoporteServiceTest"
```

Resultado:

- 24 tests.
- 0 failures.
- BUILD SUCCESS.
- Persisten advertencias H2 de cierre de base al finalizar JVM, sin fallar Maven.

## Handoff Hito 3 - Task 5 Cerrada

Retake:

1. Continuar en Task 6 del plan I9.
2. Suites verdes: `DocumentoSoporteServiceTest` + `EsalMaintenanceServiceTest`.
3. Pendiente actualizar endpoint multipart para metadatos I9 y cubrir seguridad ADMINISTRADOR/EXPEDIDOR.

## Task 6 - Controller Multipart Y Seguridad

Estado: completado.

Objetivo: actualizar contrato REST de carga documental I9 y verificar roles.

Cambios:

- `EsalController.registrarDocumento(...)` deja de usar `tipoProceso` y exige:
  - `archivo`;
  - `tipoDocumento`;
  - `subtipoDocumento` opcional;
  - `referencia`;
  - `fechaActo`;
  - `observacion` opcional.
- La fecha se recibe como ISO date mediante `@DateTimeFormat`.
- `SecurityConfigTest` cubre:
  - EXPEDIDOR no puede subir documento;
  - ADMINISTRADOR pasa seguridad del endpoint multipart;
  - EXPEDIDOR puede listar documentos de una ESAL.
- Las reglas existentes de `DevSecurityConfig` y `WeblogicSecurityConfig` cubren GET de documentos/descarga por `/api/esales/**` y POST documentos solo ADMINISTRADOR.

Verificacion GREEN:

```powershell
mvn test "-Dtest=SecurityConfigTest,DocumentoSoporteServiceTest"
```

Resultado:

- 23 tests.
- 0 failures.
- BUILD SUCCESS.
- Persisten advertencias H2 de cierre de base al finalizar JVM, sin fallar Maven.

## Handoff Hito 4 - Task 6 Cerrada

Retake:

1. Continuar en Task 7 del plan I9.
2. Backend documental + seguridad objetivo estan verdes.
3. Pendiente frontend: modelos, API client, seccion Documentos en mantenimiento ESAL, carga ADMINISTRADOR, consulta/descarga EXPEDIDOR.

## Task 7 - Frontend Documental

Estado: completado.

Objetivo: exponer gestion documental I9 en Angular con version vigente, historico consultable y descarga.

Cambios:

- `DocumentoSoporte` frontend agrega metadatos I9:
  - `tipoDocumental`;
  - `subtipoDocumental`;
  - `referenciaActo`;
  - `fechaActo`;
  - `observacion`;
  - `vigente`.
- `EsalMaintenanceComponent` agrega seccion `Documentos`:
  - lista vigente/historico;
  - carga PDF con tipo, subtipo, referencia, fecha y observacion;
  - carga visible solo para ADMINISTRADOR;
  - descarga desde endpoint backend autenticado.
- `EsalesDetailComponent` de consulta agrega campos I9 y descarga para ADMINISTRADOR/EXPEDIDOR.
- `AdminEsalesDetailComponent` deja de ofrecer carga legacy incompleta y enlaza a mantenimiento para gestion documental; conserva consulta/descarga con metadatos I9.

Verificacion:

```powershell
node "C:\Program Files\nodejs\node_modules\npm\bin\npm-cli.js" run build
```

Resultado:

- Angular build SUCCESS.
- Warnings NG8102/NG8107 persisten en pantallas existentes; no bloquean build.
- `npm test -- --watch=false` no fue evidencia util: el runner quedo en watch y fallo por restricciones/resolucion del sandbox.

## Handoff Hito 5 - Task 7 Cerrada

Retake:

1. Continuar en Task 8 del plan I9.
2. Ejecutar verificacion final backend y frontend.
3. Actualizar README, ARRANQUE, plan/log y cierre documental.

## Evidencia De Verificacion

## Task 8 - Verificacion, Docs Y Cierre

Estado: completado.

Objetivo: ejecutar verificacion final, ajustar regresiones por cambio de regla I9, actualizar documentacion y dejar retake.

Verificacion enfocada backend:

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-backend
mvn test "-Dtest=DocumentoSoporteServiceTest,EsalMaintenanceServiceTest"
mvn test "-Dtest=SecurityConfigTest,DocumentoSoporteServiceTest"
mvn test "-Dtest=EsalApiTest"
```

Resultados:

- `DocumentoSoporteServiceTest,EsalMaintenanceServiceTest`: 24 tests, 0 failures, BUILD SUCCESS.
- `SecurityConfigTest,DocumentoSoporteServiceTest`: 23 tests, 0 failures, BUILD SUCCESS.
- `EsalApiTest`: 20 tests, 0 failures, BUILD SUCCESS.

Regresion detectada y corregida:

- La primera ejecucion de `mvn test` fallo en `EsalApiTest` porque pruebas historicas de cancelacion/reactivacion esperaban el comportamiento I5 de advertencia no bloqueante.
- Se actualizaron fixtures API para cargar documento vigente `CANCELACION` antes de flujos que ahora requieren soporte documental I9.
- Despues del ajuste, `EsalApiTest` y la suite completa quedaron en verde.

Verificacion completa:

```powershell
mvn test
mvn package -DskipTests
```

Resultado:

- Suite backend completa: 148 tests, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS.
- WAR generado: `sed-esal-backend/target/sed-esal-backend.war`.

Verificacion frontend:

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-angular
node "C:\Program Files\nodejs\node_modules\npm\bin\npm-cli.js" run build
```

Resultado:

- Angular build SUCCESS.
- Persisten advertencias NG8102/NG8107 no bloqueantes.
- `npm test -- --watch=false` no fue evidencia util en esta sesion por restricciones de sandbox/watch; se registra como limitacion ambiental, no como fallo funcional confirmado.

Documentacion actualizada:

- `README.md`: I9 como fase actual y conteo backend 148 tests.
- `docs/ARRANQUE.md`: orden I9 SPEC/PLAN/log, estado vigente, comandos y artefacto fuente.
- `docs/GUIA_PRUEBAS_FUNCIONALES.md`: nueva seccion I9 con pruebas funcionales de carga, vigencia, historico, descarga, roles y bloqueo de estados.
- `docs/plans/2026-06-19-sed-esal-i9-plan.md`: plan marcado como completado.

Chequeo final de repo:

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL
git diff --check
git status --short
```

Resultado:

- `git diff --check` sin errores de whitespace.
- Git reporta warnings ambientales por acceso denegado a `C:\Users\jmep2\.config\git\ignore` y normalizacion LF/CRLF.
- El worktree conserva cambios previos no relacionados ya observados al inicio (`AGENTS.md`, handoffs raiz eliminados y `.claude/worktrees/...`).
- I9 deja nuevos artefactos en `docs/specs`, `docs/plans`, `docs/Handoff` y cambios backend/frontend/documentacion.

Desviaciones controladas frente al plan:

- La cobertura de seguridad se integro en `SecurityConfigTest` en lugar de crear una clase nueva `EsalDocumentosSecurityTest`, porque el proyecto ya centraliza reglas de seguridad local-dev/weblogic en esa suite.
- No se creo un nuevo spec Angular aislado; la verificacion frontend se hizo con build productivo debido a restricciones del runner `npm test` en el sandbox/watch.

## Cierre I9

Estado funcional alcanzado:

- Carga PDF administrativa por ADMINISTRADOR con metadatos obligatorios.
- Catalogo documental I9 con subtipos controlados.
- Version vigente por ESAL+tipo+subtipo e historico consultable.
- Descarga autenticada para ADMINISTRADOR y EXPEDIDOR.
- Bloqueo de `EN_LIQUIDACION` y `CANCELADO` sin documento vigente obligatorio.
- UI de mantenimiento con seccion `Documentos`; detalle de consulta/admin muestra metadatos y descarga.
