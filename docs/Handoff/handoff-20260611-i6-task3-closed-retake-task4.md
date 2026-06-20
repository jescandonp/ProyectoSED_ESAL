# Handoff - S&G Super App I6 Task 3 cerrada, retake Task 4

**Fecha:** 2026-06-11  
**Workspace:** `C:\Users\jmep2\Downloads\AgenIALab\ProyectoS&G`  
**Incremento activo:** I6 - Alertas y Notificaciones  
**Siguiente tarea autorizada:** I6 Task 4 - Generador de alertas I5 por vencimiento  

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
- I6 Task 3 cerrada.
- Retake autorizado: Task 4 del plan I6.

## Cambios Cerrados En Task 3

- `scripts/dev/Verify-SgSuperAppI6NotificationActions.ps1`
  - valida marcar notificacion visible como leida;
  - valida archivar notificacion visible por usuario/rol;
  - valida `read_at`, `managed_at`, `managed_by`;
  - valida eventos `READ` y `ARCHIVED`;
  - valida bloqueo para notificaciones fuera de usuario/rol autenticado.
- `scripts/dev/Verify-SgSuperAppI6Security.ps1`
  - conserva lectura para ADMIN, TH, GERENCIA y OPERACIONES;
  - conserva restriccion de permisos de generacion/configuracion para GERENCIA/OPERACIONES;
  - agrega bloqueo de gestion fuera del alcance autenticado.
- `apps/sg-superapp-api/Endpoints/PortalEndpoints.cs`
  - `POST /api/portal/notifications/{notificationId}/read`;
  - `POST /api/portal/notifications/{notificationId}/archive`.
- `apps/sg-superapp-api/Services/PostgresPortalRepository.cs`
  - actualizacion transaccional de lectura;
  - actualizacion transaccional de archivo;
  - insercion de eventos de trazabilidad.
- `README.md` y plan I6 actualizados con retake Task 4.

## Evidencia

- RED: `Verify-SgSuperAppI6NotificationActions.ps1` fallo por HTTP 404 en `POST /api/portal/notifications/{notificationId}/read`.
- GREEN: `scripts/dev/Verify-SgSuperAppI6NotificationActions.ps1` correcto.
- GREEN: `scripts/dev/Verify-SgSuperAppI6Security.ps1` correcto.
- Backend build: `C:\tmp\dotnet6\dotnet.exe build apps\sg-superapp-api\sg-superapp-api.csproj` correcto, 0 advertencias y 0 errores.

## Retake Exacto - I6 Task 4

Objetivo: generar alertas desde cursos/acreditaciones reutilizando reglas I5.

Primer ciclo recomendado:

1. Crear `scripts/dev/Verify-SgSuperAppI6TrainingAlerts.ps1` en RED para:
   - generar alerta `CRITICAL` para `VENCIDO`;
   - generar alerta `CRITICAL` para `CRITICO`;
   - generar alerta `WARNING` para `PREVENTIVO`;
   - generar alerta `INFO` para `INFORMATIVO`;
   - validar que `AL_DIA` no genera alerta;
   - validar dedupe de alertas activas por empleado/tipo/estado.
2. Implementar endpoint:
   - `POST /api/portal/alerts/training/generate`
3. Reutilizar el calculo I5 existente; no duplicar umbrales como texto manual.
4. Verificar:
   - `scripts/dev/Verify-SgSuperAppI6TrainingAlerts.ps1`
   - `C:\tmp\dotnet6\dotnet.exe build apps\sg-superapp-api\sg-superapp-api.csproj`

## Riesgos Y Observaciones

- WhatsApp, HELIZA, nomina y bloqueo automatico siguen fuera de alcance.
- SMTP no bloquea I6; fallback exportable se implementa en tareas posteriores.
- Hay cambios acumulados sin commit; no revertir trabajo externo.
- `AGENTS.md` tiene cambios externos; no incluirlo ni revertirlo por accidente.
- `graphify` puede no estar disponible en PATH; intentar `graphify update .` tras cambios de codigo y documentar resultado.
