# Execution Log I5 - CRUD Y Mantenimiento Operativo De ESAL

> Spec: `docs/specs/2026-05-21-sed-esal-i5-spec.md`
> Plan: `docs/plans/2026-05-21-sed-esal-i5-plan.md`
> Estado: completado.
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
| 2026-05-21 | T5 | CRUD de organo de administracion | Miembros de organo se pueden crear, actualizar y listar por ESAL |
| 2026-05-21 | T5 | Manejo de inactivacion | No se implementa eliminacion fisica ni inactivacion porque el modelo actual no tiene campo de vigencia para `OrganoAdministracion` |
| 2026-05-21 | T8 parcial | Seguridad endpoints organo de administracion | `ADMINISTRADOR` puede mutar miembros; `EXPEDIDOR` recibe 403 en creacion |
| 2026-05-21 | Verificacion | `mvn test "-Dtest=EsalMaintenanceServiceTest,EsalApiTest"` | BUILD SUCCESS, 19 tests |
| 2026-05-21 | Verificacion | `mvn test` en `sed-esal-backend` | BUILD SUCCESS, 117 tests |
| 2026-05-21 | T6 | Cancelacion formal | `POST /api/esales/{id}/cancelacion` valida resolucion, fecha y motivo, crea actuacion `CANCELACION` y cambia estado a `CANCELADO` |
| 2026-05-21 | T6 | Advertencia por soporte faltante | Cancelacion sin PDF soporte se guarda y registra advertencia no bloqueante `PDF SOPORTE CANCELACION` |
| 2026-05-21 | T8 parcial | Seguridad endpoint cancelacion | `ADMINISTRADOR` puede cancelar; `EXPEDIDOR` recibe 403 |
| 2026-05-21 | Verificacion | `mvn test "-Dtest=EsalMaintenanceServiceTest,EsalApiTest"` | BUILD SUCCESS, 24 tests |
| 2026-05-21 | Verificacion | `mvn test` en `sed-esal-backend` | BUILD SUCCESS, 122 tests |
| 2026-05-21 | T7 | Reactivacion desde cancelacion | `POST /api/esales/{id}/reactivacion` exige motivo y cambia una ESAL desde `CANCELADO` a `ACTIVO` por defecto o a otro estado destino permitido |
| 2026-05-21 | T7 | Preservacion de trazabilidad | La reactivacion no elimina la actuacion administrativa `CANCELACION`; conserva el historico y audita la reactivacion |
| 2026-05-21 | T8 parcial | Seguridad endpoint reactivacion | `ADMINISTRADOR` puede reactivar; `EXPEDIDOR` recibe 403 |
| 2026-05-21 | Verificacion | `mvn test "-Dtest=EsalMaintenanceServiceTest,EsalApiTest"` | BUILD SUCCESS, 29 tests |
| 2026-05-21 | Verificacion | `mvn test` en `sed-esal-backend` | BUILD SUCCESS, 127 tests |
| 2026-05-21 | T8 | Autorizacion y seguridad consolidada | Mutaciones I5 requieren autenticacion; `EXPEDIDOR` recibe 403; recalculo de completitud queda restringido a `ADMINISTRADOR` |
| 2026-05-21 | T8 | Proteccion local-dev y weblogic | Se alinea `POST /api/esales/{id}/completitud/recalcular` como operacion administrativa en ambas configuraciones |
| 2026-05-21 | Verificacion | `mvn test "-Dtest=EsalApiTest"` | BUILD SUCCESS, 20 tests |
| 2026-05-21 | Verificacion | `mvn test` en `sed-esal-backend` | BUILD SUCCESS, 129 tests |
| 2026-05-21 | T9 | UI administrativa Angular | Se agrega ruta `/admin/esales/:id/mantenimiento` protegida para administrador y pantalla de mantenimiento por secciones |
| 2026-05-21 | T9 | Guardado seccional | La UI consume endpoints I5 de informacion principal, personeria juridica, representantes, organo, cancelacion y reactivacion |
| 2026-05-21 | T9 | Bloqueo por cancelacion | La pantalla bloquea edicion ordinaria cuando la ESAL esta `CANCELADO` y mantiene accion explicita de reactivacion |
| 2026-05-21 | Verificacion | `npm test -- --watch=false --browsers=ChromeHeadless` | SUCCESS, 3 tests |
| 2026-05-21 | Verificacion | `npm run build` en `sed-esal-angular` | BUILD SUCCESS con warnings preexistentes de optional/nullish checks |
| 2026-05-21 | T10 | Preview con datos vigentes | Preview de certificado usa representante legal vigente despues de mantenimiento I5 y conserva fallback para registros historicos sin vigencia |
| 2026-05-21 | T10 | Certificados historicos | Certificado generado conserva `versionDatos` aunque despues se edite la ESAL; nuevos certificados usan version vigente |
| 2026-05-21 | Verificacion | `mvn test "-Dtest=PreviewServiceTest,GeneracionServiceTest"` | BUILD SUCCESS, 14 tests |
| 2026-05-21 | Verificacion | `mvn test` en `sed-esal-backend` | BUILD SUCCESS, 131 tests |
| 2026-05-21 | T11 | Documentacion funcional I5 | `README.md`, `docs/ARRANQUE.md` y `docs/GUIA_PRUEBAS_FUNCIONALES.md` actualizados a estado I5 |
| 2026-05-21 | T11 | Guia funcional I5 | Casos de creacion, edicion seccional, cancelacion, reactivacion, seguridad, preview y certificados documentados |
| 2026-05-21 | Cierre I5 | Incremento completado | CRUD y mantenimiento operativo de ESAL queda documentado y verificable por bloques |
| 2026-05-27 | Ajuste I5 | Revision post-cierre de visibilidad | Se confirma que I5 estaba implementado en backend/frontend, pero con acceso frontend poco descubrible |
| 2026-05-27 | Ajuste I5 | Navegacion lateral corregida | Se retira `Buscar ESAL` del sidebar porque apuntaba a `/busqueda`, ruta no registrada en el router Angular actual |
| 2026-05-27 | Ajuste I5 | Acceso a mantenimiento expuesto | El detalle general de ESAL muestra `Actualizar informacion` para `ADMINISTRADOR` y navega a `/admin/esales/:id/mantenimiento` |
| 2026-05-27 | Verificacion | `npm test -- --watch=false --browsers=ChromeHeadless` | SUCCESS, 4 tests |
| 2026-05-27 | Verificacion | `npm run build` en `sed-esal-angular` | BUILD SUCCESS con warnings Angular preexistentes de optional/nullish checks |

## Pendientes Del Siguiente Bloque

- Definir siguiente incremento formal mediante spec, plan y execution log aprobados.
