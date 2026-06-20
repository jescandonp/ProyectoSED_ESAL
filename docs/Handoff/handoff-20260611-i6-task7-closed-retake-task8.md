# Handoff - S&G Super App I6 Task 7 cerrada, retake Task 8

**Fecha:** 2026-06-11  
**Workspace:** `C:\Users\jmep2\Downloads\AgenIALab\ProyectoS&G`  
**Incremento activo:** I6 - Alertas y Notificaciones  
**Siguiente tarea autorizada:** I6 Task 8 - UI de bandeja, contador y acciones  

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
- I6 Tasks 1-7 cerradas.
- Retake autorizado: Task 8 del plan I6.

## Cambios Cerrados En Task 7

- `scripts/dev/Verify-SgSuperAppI6FrontendApi.ps1`
  - valida contrato TypeScript de tipos I6 y cliente API.
- `apps/sg-superapp-web/src/types/portal.ts`
  - `NotificationStatus`, `NotificationSeverity`, `NotificationSourceModule`;
  - `NotificationFilters`;
  - `NotificationUnreadCountResponse`;
  - `NotificationGenerationResponse`;
  - `NotificationEmailSummaryRequest`;
  - `NotificationEmailSummaryResponse`;
  - `NotificationItem` enriquecido con modulo, severidad, origen, accion y fechas de gestion.
- `apps/sg-superapp-web/src/services/portalApi.ts`
  - bandeja autenticada;
  - contador de no leidas;
  - marcar como leida;
  - archivar;
  - generadores de alertas training/imports/certificates;
  - exportacion CSV;
  - email summary.
- `apps/sg-superapp-web/src/mock/session.ts`
  - mocks ajustados al contrato I6 de notificacion.
- `README.md` y plan I6 actualizados con retake Task 8.

## Evidencia

- RED: `Verify-SgSuperAppI6FrontendApi.ps1` fallo por tipos y funciones frontend I6 faltantes.
- GREEN: `scripts/dev/Verify-SgSuperAppI6FrontendApi.ps1` correcto.
- GREEN: `npm run build` en `apps/sg-superapp-web` correcto fuera del sandbox; 46 modulos transformados, bundle JS 232.51 kB.
- `graphify update .` intentado; no ejecuta porque `graphify` no esta disponible en PATH.

## Retake Exacto - I6 Task 8

Objetivo: implementar bandeja operativa de notificaciones en el shell.

Primer ciclo recomendado:

1. Crear verificacion RED de UI para contador, bandeja, filtros y acciones si el patron local lo permite.
2. Integrar contador junto al perfil en `ShellLayout`.
3. Crear bandeja con notificaciones personales y por rol usando `fetchNotificationsInbox`.
4. Agregar filtros por estado, severidad y modulo.
5. Exponer acciones leer/archivar usando `markNotificationAsRead` y `archiveNotification`.
6. Mantener Operaciones/Gerencia en modo consulta sin configuracion.
7. Verificar:
   - `npm run build` en `apps/sg-superapp-web`;
   - `scripts/dev/Verify-SgSuperAppI6Security.ps1`.

## Riesgos Y Observaciones

- WhatsApp, HELIZA, nomina y bloqueo automatico siguen fuera de alcance.
- SMTP no bloquea I6; fallback ya esta disponible en backend y cliente.
- Hay cambios acumulados sin commit; no revertir trabajo externo.
- `AGENTS.md` tiene cambios externos; no incluirlo ni revertirlo por accidente.
- `graphify` puede no estar disponible en PATH; intentar `graphify update .` tras cambios de codigo y documentar resultado.
