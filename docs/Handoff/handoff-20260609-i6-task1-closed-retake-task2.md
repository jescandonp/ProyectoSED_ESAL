# Handoff - S&G Super App I6 Task 1 cerrada, retake Task 2

**Fecha:** 2026-06-09  
**Workspace:** `C:\Users\jmep2\Downloads\AgenIALab\ProyectoS&G`  
**Incremento activo:** I6 - Alertas y Notificaciones  
**Siguiente tarea autorizada:** I6 Task 2 - Contratos backend de bandeja y contador  

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
- Retake autorizado: Task 2 del plan I6.

## Cambios Cerrados En Task 1

- `db/tests/006_i6_notifications_contract.sql`
  - contrato de persistencia para notificaciones, eventos, constraints, dedupe y permisos.
- `scripts/dev/Verify-SgSuperAppI6Persistence.ps1`
  - verificacion sobre `sg_superapp_dev`.
- `scripts/dev/Verify-SgSuperAppI6PersistenceClean.ps1`
  - verificacion limpia sobre esquema temporal `i6_verify_clean`.
- `db/migrations/008_i6_notifications.sql`
  - evoluciona `notification_items`;
  - crea `notification_events`;
  - agrega constraints e indices I6.
- `db/seeds/008_i6_notification_permissions.sql`
  - permisos I6 para ADMIN, TH, GERENCIA y OPERACIONES.
- `README.md` y plan I6 actualizados con retake Task 2.

## Evidencia

- RED: `Verify-SgSuperAppI6Persistence.ps1` fallo por `notification_events` ausente.
- RED: `Verify-SgSuperAppI6PersistenceClean.ps1` fallo por `008_i6_notifications.sql` inexistente.
- GREEN: migracion 008 + seed 008 aplicados sobre `sg_superapp_dev`.
- GREEN: `scripts/dev/Verify-SgSuperAppI6Persistence.ps1` correcto.
- GREEN: `scripts/dev/Verify-SgSuperAppI6PersistenceClean.ps1` correcto.
- Backend build: `C:\tmp\dotnet6\dotnet.exe build apps\sg-superapp-api\sg-superapp-api.csproj` correcto, 0 advertencias y 0 errores.

## Retake Exacto - I6 Task 2

Objetivo: exponer bandeja de notificaciones y contador de no leidas por usuario/rol.

Primer ciclo recomendado:

1. Crear `scripts/dev/Verify-SgSuperAppI6Inbox.ps1` en RED para:
   - listar notificaciones personales y del rol;
   - filtrar por estado, severidad y modulo;
   - consultar contador de no leidas.
2. Crear `scripts/dev/Verify-SgSuperAppI6Security.ps1` en RED para:
   - ADMIN/TH con acceso de gestion;
   - GERENCIA/OPERACIONES solo consulta;
   - sin endpoints de configuracion para roles de consulta.
3. Implementar contratos backend y endpoints:
   - `GET /api/portal/notifications`
   - `GET /api/portal/notifications/unread-count`
4. Verificar:
   - `scripts/dev/Verify-SgSuperAppI6Inbox.ps1`
   - `scripts/dev/Verify-SgSuperAppI6Security.ps1`
   - `C:\tmp\dotnet6\dotnet.exe build apps\sg-superapp-api\sg-superapp-api.csproj`

## Riesgos Y Observaciones

- `notification_items` existia desde I1; I6 la evoluciono de forma compatible.
- WhatsApp, HELIZA, nomina y bloqueo automatico siguen fuera de alcance.
- SMTP no bloquea I6; fallback exportable se implementa en tareas posteriores.
- Hay cambios acumulados sin commit; no revertir trabajo externo.
- `AGENTS.md` tiene cambios externos; no incluirlo ni revertirlo por accidente.
- `graphify` sigue no disponible en PATH.
