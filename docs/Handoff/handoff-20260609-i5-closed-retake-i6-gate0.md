# Handoff - S&G Super App I5 cerrado, retake I6 Gate 0

**Fecha:** 2026-06-09  
**Workspace:** `C:\Users\jmep2\Downloads\AgenIALab\ProyectoS&G`  
**Incremento cerrado:** I5 - Cursos y acreditaciones  
**Siguiente tarea autorizada:** I6 Gate 0 - SPEC y plan de Alertas y Notificaciones  

## Entrada Canonica Obligatoria

Toda nueva sesion debe iniciar leyendo:

1. `README.md`
2. `docs/CONSTITUTION.md`
3. `docs/ARCHITECTURE.md`
4. `docs/TECNOLOGIA.md`
5. `docs/DESIGN.md`
6. `docs/specs/2026-05-21-sg-superapp-spec-00-arquitectura-incrementos.md`
7. `docs/plans/2026-06-05-sg-superapp-i5-cursos-acreditaciones-plan.md`
8. Este handoff

## Estado SDD Canonico

- I2 cerrado tecnicamente.
- I3 cerrado tecnicamente.
- I4 cerrado tecnicamente.
- I5 cerrado tecnicamente.
- No existe aun SPEC I6 especifica ni plan I6 aprobado.
- Retake autorizado: I6 Gate 0, crear SPEC/plan antes de codificar.

## Cierre I5

- Persistencia, permisos, contratos backend, renovaciones, reglas de fecha, auditoria, estados calculados, habilitacion de servicio, consultas backend, cliente API, UI de cumplimiento y UI de gestion TH/ADMIN quedaron cerrados.
- Matriz final de aceptacion 1-20 registrada en `docs/plans/2026-06-05-sg-superapp-i5-cursos-acreditaciones-plan.md`.
- Riesgos residuales registrados en el mismo plan.

## Evidencia

- Suite completa `scripts/dev/Verify-SgSuperAppI5*.ps1` correcta con API temporal en `http://localhost:5080`.
- `npm run build` correcto en `apps/sg-superapp-web`.
- Backend build correcto con `C:\tmp\dotnet6\dotnet.exe build apps\sg-superapp-api\sg-superapp-api.csproj`, 0 advertencias y 0 errores.
- Primer intento de backend build fallo porque el binario estaba bloqueado por un proceso local `sg-superapp-api` PID 8876; se detuvo y el build posterior paso.
- `graphify update .` intentado; falla porque `graphify` no esta disponible en PATH.

## Retake Exacto - I6 Gate 0

Objetivo: crear y aprobar SPEC I6 y plan I6 antes de implementar.

Primer ciclo recomendado:

1. Leer `docs/specs/2026-05-21-sg-superapp-spec-00-arquitectura-incrementos.md`, seccion I6.
2. Crear SPEC I6 de Alertas y Notificaciones.
3. Crear plan I6 con tareas verificables, matriz de aceptacion y restricciones.
4. Mantener como fuera de alcance salvo decision documental nueva:
   - WhatsApp;
   - integracion HELIZA/nomina;
   - bloqueo automatico de turnos;
   - IA avanzada.
5. Reutilizar reglas I5 de vencimiento centralizadas para alertas de cursos/acreditaciones.

## Riesgos Y Observaciones

- Hay cambios acumulados sin commit de I2/I3/I4/I5; no revertir trabajo externo.
- `AGENTS.md` tiene cambios externos; no incluirlo ni revertirlo por accidente.
- I6 debe decidir SMTP o fallback exportable segun disponibilidad real de infraestructura.
- `graphify` sigue no disponible en PATH.
