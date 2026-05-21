# Execution Log I5 - CRUD Y Mantenimiento Operativo De ESAL

> Spec: `docs/specs/2026-05-21-sed-esal-i5-spec.md`
> Plan: `docs/plans/2026-05-21-sed-esal-i5-plan.md`
> Estado: en ejecucion.
> Fecha inicio: 2026-05-21.

## Resumen

Se inicia la implementacion incremental de I5 bajo SDD, con avance por tareas y verificacion por bloque. La premisa operativa es actualizar este log conforme se cierren tareas, sin esperar al final del incremento.

## Registro

| Fecha | Tarea | Evento | Resultado |
|---|---|---|---|
| 2026-05-21 | Preparacion | Spec y plan I5 aprobados por el usuario | Base documental lista |
| 2026-05-21 | T1-T3 | Inicio de backend seccional con TDD | Completado primer bloque |
| 2026-05-21 | T1 | DTOs I5 creados | `EsalInformacionPrincipalDto`, `PersoneriaJuridicaDto`, `NombramientoDto`, `OrganoAdministracionDto`, `CancelacionEsalDto`, `ReactivacionEsalDto`, `MantenimientoEsalDto` |
| 2026-05-21 | T2 | Servicio seccional base creado | `EsalMaintenanceService` crea ESAL, actualiza informacion principal, obtiene vista de mantenimiento, recalcula completitud y audita |
| 2026-05-21 | T3 | Upsert 1:1 de personeria juridica | Guarda personeria sin duplicar registros por ESAL |
| 2026-05-21 | T1-T3 | Contratos REST iniciales | `POST /api/esales/mantenimiento`, `GET /api/esales/{id}/mantenimiento`, `PUT /api/esales/{id}/informacion-principal`, `PUT /api/esales/{id}/personeria-juridica` |
| 2026-05-21 | T8 parcial | Seguridad endpoints I5 iniciales | `ADMINISTRADOR` puede mutar; `EXPEDIDOR` recibe 403 en creacion de mantenimiento |
| 2026-05-21 | Verificacion | `mvn test "-Dtest=EsalMaintenanceServiceTest,EsalApiTest"` | BUILD SUCCESS, 11 tests |
| 2026-05-21 | Verificacion | `mvn test` en `sed-esal-backend` | BUILD SUCCESS, 109 tests |
| 2026-05-21 | T4 | CRUD de representantes legales | `REPRESENTANTE_LEGAL` y `REPRESENTANTE_LEGAL_SUPLENTE` se pueden crear, actualizar y listar por ESAL |
| 2026-05-21 | T4 | Restriccion de tipos de nombramiento | Tipos fuera del alcance de representantes se rechazan con error funcional |
| 2026-05-21 | T8 parcial | Seguridad endpoints representantes | `ADMINISTRADOR` puede mutar representantes; `EXPEDIDOR` recibe 403 en creacion |
| 2026-05-21 | Verificacion | `mvn test "-Dtest=EsalMaintenanceServiceTest,EsalApiTest"` | BUILD SUCCESS, 15 tests |
| 2026-05-21 | Verificacion | `mvn test` en `sed-esal-backend` | BUILD SUCCESS, 113 tests |

## Pendientes Del Siguiente Bloque

- T5 Organo de administracion.
- T6 Cancelacion formal.
- T7 Reactivacion desde `CANCELADO`.
