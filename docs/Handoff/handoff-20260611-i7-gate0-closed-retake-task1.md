# Handoff - I7 Gate 0 cerrado, retake Task 1

**Fecha:** 2026-06-11  
**Workspace:** `C:\Users\jmep2\Downloads\AgenIALab\ProyectoS&G`  
**Estado:** I7 Gate 0 cerrado  
**Siguiente punto autorizado:** I7 Task 1 - contratos backend de dashboard por rol  

## Premisa De Retoma

Retomar desde `README.md` y seguir el orden canonico:

1. `README.md`
2. `docs/CONSTITUTION.md`
3. `docs/ARCHITECTURE.md`
4. `docs/TECNOLOGIA.md`
5. `docs/DESIGN.md`
6. `docs/specs/2026-06-11-sg-superapp-spec-i7-auditoria-dashboard-cierre-piloto.md`
7. `docs/plans/2026-06-11-sg-superapp-i7-auditoria-dashboard-cierre-piloto-plan.md`

## Gate 0 Cerrado

Se crearon y aprobaron:

- SPEC I7: `docs/specs/2026-06-11-sg-superapp-spec-i7-auditoria-dashboard-cierre-piloto.md`
- Plan I7: `docs/plans/2026-06-11-sg-superapp-i7-auditoria-dashboard-cierre-piloto-plan.md`

I7 consolida dashboard, auditoria y cierre piloto. No reabre funcionalidades cerradas de I1-I6.

## Alcance I7

Incluye:

- dashboard comun con widgets por perfil;
- indicadores de certificaciones, cursos/acreditaciones, importaciones, notificaciones, puestos y salud de plataforma;
- consulta de auditoria transversal con filtros;
- demo checklist;
- reporte de cierre piloto;
- backlog priorizado;
- riesgos residuales y recomendacion de escalamiento.

Excluye:

- WhatsApp;
- HELIZA;
- nomina;
- IA avanzada;
- modulo funcional completo de novedades;
- bloqueo automatico operativo;
- cambios de stack.

## Retake Task 1

Objetivo:

Crear contratos backend de dashboard por rol.

Aceptacion esperada:

- endpoint autenticado de dashboard disponible;
- widgets filtrados por rol/permisos;
- indicadores cubren certificaciones, cursos, importaciones, notificaciones, puestos y plataforma segun rol;
- widgets no exponen acciones no autorizadas.

Verificacion esperada:

- crear RED script `scripts/dev/Verify-SgSuperAppI7Dashboard.ps1`;
- confirmar RED antes de implementar;
- implementar contratos backend;
- ejecutar `scripts/dev/Verify-SgSuperAppI7Dashboard.ps1`;
- ejecutar `C:\tmp\dotnet6\dotnet.exe build apps/sg-superapp-api/sg-superapp-api.csproj`;
- intentar `graphify update .` si se modifica codigo y la herramienta esta disponible.

## Notas Operativas

- Usar `C:\tmp\dotnet6\dotnet.exe` si `dotnet` no esta en PATH.
- No revertir cambios locales ajenos.
- `AGENTS.md` tiene cambios externos y no forma parte de I7.
- `graphify` ha fallado previamente porque no esta disponible en PATH.
