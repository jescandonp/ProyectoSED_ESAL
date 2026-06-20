# Handoff - S&G Super App I6 Task 2 cerrada, retake Task 3

**Fecha:** 2026-06-09  
**Workspace:** `C:\Users\jmep2\Downloads\AgenIALab\ProyectoS&G`  
**Incremento activo:** I6 - Alertas y Notificaciones  
**Siguiente tarea autorizada:** I6 Task 3 - Gestion de lectura, archivo y trazabilidad  

## Entrada Canonica Obligatoria

Toda nueva sesion debe iniciar leyendo:

1. `README.md`
2. `docs/CONSTITUTION.md`
3. `docs/ARCHITECTURE.md`
4. `docs/TECNOLOGIA.md`
5. `docs/DESIGN.md`
6. `docs/specs/2026-06-09-sg-superapp-spec-i6-alertas-notificaciones.md`
7. `docs/plans/2026-06-09-sg-superapp-i6-alertas-notificaciones-plan.md`
8. Este handoff

## Estado SDD Canonico

- I5 cerrado tecnicamente.
- SPEC I6 aprobada.
- Plan I6 aprobado.
- I6 Task 1 cerrada.
- I6 Task 2 cerrada.
- Retake autorizado: Task 3 del plan I6.

## Cambios Cerrados En Task 2

- `scripts/dev/Verify-SgSuperAppI6Inbox.ps1`
  - verifica bandeja autenticada por usuario/rol;
  - verifica filtros por estado, severidad y modulo;
  - verifica contador de no leidas.
- `scripts/dev/Verify-SgSuperAppI6Security.ps1`
  - verifica lectura para ADMIN, TH, GERENCIA y OPERACIONES;
  - verifica que GERENCIA/OPERACIONES no tengan permisos de generacion/configuracion.
- `apps/sg-superapp-api/Contracts/Portal/NotificationResponse.cs`
  - contrato extendido con severidad, modulo, origen, URL de accion y fechas de gestion.
- `apps/sg-superapp-api/Contracts/Portal/NotificationUnreadCountResponse.cs`
  - contrato de contador.
- `apps/sg-superapp-api/Endpoints/PortalEndpoints.cs`
  - `GET /api/portal/notifications`;
  - `GET /api/portal/notifications/unread-count`.
- `apps/sg-superapp-api/Services/PostgresPortalRepository.cs`
  - consultas por usuario autenticado y rol;
  - filtros opcionales por estado, severidad y modulo;
  - contador `UNREAD`.
- `apps/sg-superapp-api/Services/MockPortalQueryService.cs`
  - compatibilidad con contrato extendido.
- `README.md` y plan I6 actualizados con retake Task 3.

## Evidencia

- RED: `Verify-SgSuperAppI6Inbox.ps1` fallo por HTTP 404 en `GET /api/portal/notifications`.
- RED: `Verify-SgSuperAppI6Security.ps1` fallo por HTTP 404 en `GET /api/portal/notifications`.
- GREEN: `scripts/dev/Verify-SgSuperAppI6Inbox.ps1` correcto.
- GREEN: `scripts/dev/Verify-SgSuperAppI6Security.ps1` correcto.
- Backend build: `C:\tmp\dotnet6\dotnet.exe build apps\sg-superapp-api\sg-superapp-api.csproj` correcto, 0 advertencias y 0 errores.

## Retake Exacto - I6 Task 3

Objetivo: implementar acciones de lectura y archivo con eventos trazables.

Primer ciclo recomendado:

1. Crear `scripts/dev/Verify-SgSuperAppI6NotificationActions.ps1` en RED para:
   - marcar notificacion visible como leida;
   - archivar notificacion visible;
   - validar `read_at`, `managed_at`, `managed_by`;
   - validar eventos `READ` y `ARCHIVED`.
2. Ampliar `scripts/dev/Verify-SgSuperAppI6Security.ps1` para:
   - impedir gestion de notificaciones fuera del usuario/rol autenticado;
   - mantener lectura para roles autorizados.
3. Implementar endpoints:
   - `POST /api/portal/notifications/{notificationId}/read`
   - `POST /api/portal/notifications/{notificationId}/archive`
4. Verificar:
   - `scripts/dev/Verify-SgSuperAppI6NotificationActions.ps1`
   - `scripts/dev/Verify-SgSuperAppI6Security.ps1`
   - `C:\tmp\dotnet6\dotnet.exe build apps\sg-superapp-api\sg-superapp-api.csproj`

## Riesgos Y Observaciones

- El endpoint legado `GET /api/portal/notifications/{username}` se conserva por compatibilidad.
- WhatsApp, HELIZA, nomina y bloqueo automatico siguen fuera de alcance.
- SMTP no bloquea I6; fallback exportable se implementa en tareas posteriores.
- Hay cambios acumulados sin commit; no revertir trabajo externo.
- `AGENTS.md` tiene cambios externos; no incluirlo ni revertirlo por accidente.
- `graphify` sigue no disponible en PATH.
