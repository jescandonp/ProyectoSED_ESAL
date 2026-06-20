# Handoff - S&G Super App I6 Gate 0 cerrado, retake Task 1

**Fecha:** 2026-06-09  
**Workspace:** `C:\Users\jmep2\Downloads\AgenIALab\ProyectoS&G`  
**Incremento activo:** I6 - Alertas y Notificaciones  
**Siguiente tarea autorizada:** I6 Task 1 - Persistencia I6 y permisos base  

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

- I2 cerrado tecnicamente.
- I3 cerrado tecnicamente.
- I4 cerrado tecnicamente.
- I5 cerrado tecnicamente.
- SPEC I6 aprobada.
- Plan I6 aprobado.
- Retake autorizado: Task 1 del plan I6.

## Cambios Cerrados En Gate 0

- `docs/specs/2026-06-09-sg-superapp-spec-i6-alertas-notificaciones.md`
  - contrato funcional I6 con alcance, actores, conceptos, flujos, permisos, reglas, criterios 1-20 y riesgos.
- `docs/plans/2026-06-09-sg-superapp-i6-alertas-notificaciones-plan.md`
  - plan I6 con 10 tareas, checkpoints, matriz de trazabilidad y verificaciones.
- `README.md`
  - actualiza el gate actual a I6 Task 1.

## Retake Exacto - I6 Task 1

Objetivo: crear/evolucionar persistencia I6 y permisos base.

Primer ciclo recomendado:

1. Crear pruebas RED:
   - `scripts/dev/Verify-SgSuperAppI6Persistence.ps1`
   - `scripts/dev/Verify-SgSuperAppI6PersistenceClean.ps1`
2. Crear migracion/ajuste SQL para:
   - `notification_items`
   - `notification_events`
   - constraints de `target_type`, `severity`, `source_module`, `status` y dedupe.
3. Crear seed de permisos I6 para ADMIN, TH, GERENCIA y OPERACIONES.
4. Verificar:
   - `scripts/dev/Verify-SgSuperAppI6Persistence.ps1`
   - `scripts/dev/Verify-SgSuperAppI6PersistenceClean.ps1`
   - `C:\tmp\dotnet6\dotnet.exe build apps\sg-superapp-api\sg-superapp-api.csproj`

## Riesgos Y Observaciones

- WhatsApp, HELIZA, nomina y bloqueo automatico de turnos quedan fuera de I6 salvo decision documental nueva.
- SMTP no bloquea I6; fallback exportable es obligatorio.
- I6 debe reutilizar reglas I5 para vencimientos de cursos/acreditaciones.
- Hay cambios acumulados sin commit de I2/I3/I4/I5/I6; no revertir trabajo externo.
- `AGENTS.md` tiene cambios externos; no incluirlo ni revertirlo por accidente.
- `graphify` sigue no disponible en PATH.
