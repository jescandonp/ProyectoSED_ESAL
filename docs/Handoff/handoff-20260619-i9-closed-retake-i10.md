# Handoff - SED_ESAL I9 Cerrado

> Fecha: 2026-06-19.
> Estado: I9 completado.
> Retake recomendado: abrir SPEC I10 antes de implementar cualquier nuevo alcance.

## Resumen

I9 implemento gestion documental administrativa transversal para ESAL con almacenamiento backend/local, metadatos obligatorios, version vigente, historico consultable y descarga autenticada.

La regla funcional queda endurecida: una ESAL no puede pasar a `EN_LIQUIDACION` sin documento vigente tipo `LIQUIDACION`, ni a `CANCELADO` sin documento vigente tipo `CANCELACION`.

## Artefactos SDD

- SPEC: `docs/specs/2026-06-19-sed-esal-i9-spec.md`
- PLAN: `docs/plans/2026-06-19-sed-esal-i9-plan.md`
- Execution log: `docs/plans/2026-06-19-sed-esal-i9-execution-log.md`
- Guia funcional: `docs/GUIA_PRUEBAS_FUNCIONALES.md`
- Arranque: `docs/ARRANQUE.md`

## Implementacion Principal

Backend:

- `DocumentoSoporte` extendido con catalogo I9, referencia, fecha, observacion y vigencia.
- `DocumentoSoporteService` valida PDF, tamano maximo 10 MB, catalogo/subtipo, metadatos y reemplazo de vigente.
- `AlmacenamientoService` expone lectura para descarga autenticada.
- `EsalController` actualiza multipart y agrega endpoint de descarga.
- `EsalMaintenanceService` bloquea liquidacion/cancelacion sin documento vigente obligatorio.

Frontend:

- `DocumentoSoporte` TS incluye metadatos I9.
- `EsalMaintenanceComponent` agrega seccion `Documentos` con carga ADMINISTRADOR, vigente/historico y descarga.
- Detalles de consulta/admin muestran metadatos I9 y permiten descarga.

## Evidencia

Backend:

```powershell
mvn test "-Dtest=DocumentoSoporteServiceTest,EsalMaintenanceServiceTest"
mvn test "-Dtest=SecurityConfigTest,DocumentoSoporteServiceTest"
mvn test "-Dtest=EsalApiTest"
mvn test
mvn package -DskipTests
```

Resultado:

- Suites enfocadas verdes.
- Suite completa backend: 148 tests, BUILD SUCCESS.
- WAR generado: `sed-esal-backend/target/sed-esal-backend.war`.

Frontend:

```powershell
node "C:\Program Files\nodejs\node_modules\npm\bin\npm-cli.js" run build
```

Resultado:

- Angular build SUCCESS.
- Advertencias NG8102/NG8107 no bloqueantes.
- `npm test -- --watch=false` no fue evidencia util por restriccion sandbox/watch.

## Notas De Retoma

- El worktree tenia cambios previos no relacionados antes de I9 (`AGENTS.md`, handoffs raiz eliminados y carpeta `docs/Handoff/`); no fueron revertidos.
- Si se abre I10, empezar por `docs/CONSTITUTION.md`, `docs/ARRANQUE.md`, SPEC/PLAN/log I9 y este handoff.
- Mantener la regla de version vigente + historico; no reemplazarla por eliminacion fisica.
- Para cambios Oracle/WebLogic, revisar si se requiere script DDL explicito para las columnas nuevas de `DocumentoSoporte`.
