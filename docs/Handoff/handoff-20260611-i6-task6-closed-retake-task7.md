# Handoff - S&G Super App I6 Task 6 cerrada, retake Task 7

**Fecha:** 2026-06-11  
**Workspace:** `C:\Users\jmep2\Downloads\AgenIALab\ProyectoS&G`  
**Incremento activo:** I6 - Alertas y Notificaciones  
**Siguiente tarea autorizada:** I6 Task 7 - Cliente API y tipos frontend  

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
- I6 Tasks 1-6 cerradas.
- Retake autorizado: Task 7 del plan I6.

## Cambios Cerrados En Task 6

- `scripts/dev/Verify-SgSuperAppI6Export.ps1`
  - valida exportacion CSV filtrable por estado, severidad y modulo;
  - valida visibilidad por rol;
  - valida evento `EXPORTED`.
- `scripts/dev/Verify-SgSuperAppI6EmailFallback.ps1`
  - valida intento de email summary sin SMTP disponible;
  - valida respuesta controlada con fallback disponible;
  - valida eventos `EMAIL_ATTEMPTED` y `EMAIL_FAILED`;
  - valida bloqueo para `GERENCIA`.
- `apps/sg-superapp-api/Endpoints/PortalEndpoints.cs`
  - `GET /api/portal/notifications-summary/export`;
  - `POST /api/portal/notifications-summary/email`.
- `apps/sg-superapp-api/Services/PostgresPortalRepository.cs`
  - `ExportNotificationsAsync`;
  - `AttemptNotificationEmailSummaryAsync`;
  - consulta transaccional reutilizable de notificaciones visibles.
- `apps/sg-superapp-api/Contracts/Portal/NotificationEmailSummaryRequest.cs`
- `apps/sg-superapp-api/Contracts/Portal/NotificationEmailSummaryResponse.cs`
- `README.md` y plan I6 actualizados con retake Task 7.

## Evidencia

- RED: `Verify-SgSuperAppI6Export.ps1` fallo por HTTP 404 en `GET /api/portal/notifications-summary/export`.
- RED: `Verify-SgSuperAppI6EmailFallback.ps1` fallo por HTTP 404 en `POST /api/portal/notifications-summary/email`.
- GREEN: `scripts/dev/Verify-SgSuperAppI6Export.ps1` correcto.
- GREEN: `scripts/dev/Verify-SgSuperAppI6EmailFallback.ps1` correcto.
- Backend build: `C:\tmp\dotnet6\dotnet.exe build apps\sg-superapp-api\sg-superapp-api.csproj` correcto, 0 advertencias y 0 errores.
- `graphify update .` intentado; no ejecuta porque `graphify` no esta disponible en PATH.

## Retake Exacto - I6 Task 7

Objetivo: agregar tipos TypeScript y cliente API I6.

Primer ciclo recomendado:

1. Crear verificacion RED de frontend/types para cliente I6 si el patron local lo permite.
2. Agregar tipos TypeScript para notificacion, filtros, contador, generadores, exportacion y email fallback.
3. Ampliar cliente API con bandeja, contador, acciones, generadores, exportacion y correo opcional.
4. Mantener propagacion de errores HTTP del backend.
5. Verificar:
   - `npm run build` en `apps/sg-superapp-web`.

## Riesgos Y Observaciones

- WhatsApp, HELIZA, nomina y bloqueo automatico siguen fuera de alcance.
- SMTP no bloquea I6; el flujo actual responde con fallback disponible.
- Hay cambios acumulados sin commit; no revertir trabajo externo.
- `AGENTS.md` tiene cambios externos; no incluirlo ni revertirlo por accidente.
- `graphify` puede no estar disponible en PATH; intentar `graphify update .` tras cambios de codigo y documentar resultado.
