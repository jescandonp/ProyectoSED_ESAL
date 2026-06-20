# Handoff I9 Task 3 Cerrada - Retake Task 4

> Fecha: 2026-06-19.
> Sistema: `SED_ESAL`.
> Incremento: I9 Gestion Documental Administrativa Transversal.
> Spec: `docs/specs/2026-06-19-sed-esal-i9-spec.md`.
> Plan: `docs/plans/2026-06-19-sed-esal-i9-plan.md`.
> Execution log: `docs/plans/2026-06-19-sed-esal-i9-execution-log.md`.

## Estado

Task 1, Task 2 y Task 3 del plan I9 quedaron completadas.

## Evidencia

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-backend
mvn test "-Dtest=DocumentoSoporteServiceTest"
```

Resultado:

- 10 tests.
- 0 failures.
- BUILD SUCCESS.

## Cambios Relevantes

- Tests RED/GREEN de documentos I9 agregados en `DocumentoSoporteServiceTest`.
- Enums creados:
  - `TipoDocumentoSoporte`
  - `SubtipoDocumentoSoporte`
- `DocumentoSoporte`, `DocumentoSoporteDto` y `DocumentoSoporteRepository` extendidos para metadatos, vigencia e historico.
- `DocumentoSoporteService` implementa:
  - PDF obligatorio;
  - limite 10 MB;
  - referencia y fecha obligatorias;
  - validacion tipo/subtipo;
  - reemplazo de vigente por ESAL+tipo+subtipo;
  - listado vigente primero e historico despues.

## Retake Point

Continuar con Task 4 del plan:

1. Agregar `AlmacenamientoService.leer`.
2. Implementar lectura en `LocalDevAlmacenamientoService` y `TestAlmacenamientoService`.
3. Agregar `DocumentoSoporteService.descargar`.
4. Exponer `GET /api/esales/{id}/documentos/{documentoId}/descarga`.
5. Verificar con `mvn test "-Dtest=DocumentoSoporteServiceTest"`.

## Cautelas

- No revertir cambios preexistentes en `AGENTS.md`, handoffs raiz o `.claude/worktrees/practical-chatelet-a3bc4c`.
- La sobrecarga legacy de `DocumentoSoporteService.registrar(...)` sigue temporalmente para compatibilidad mientras se actualizan controller y UI.
