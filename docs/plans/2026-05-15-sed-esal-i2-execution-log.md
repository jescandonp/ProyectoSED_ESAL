# Execution Log I2 - Busqueda Operativa Y Vista Previa De Certificado

> Spec: `docs/specs/2026-05-15-sed-esal-i2-spec.md`
> Plan: `docs/plans/2026-05-15-sed-esal-i2-plan.md`
> Estado: COMPLETADO 2026-05-16.
> Fecha: 2026-05-15.

## Resumen

Se crea la especificacion y el plan de I2 para cubrir busqueda interna, detalle solo lectura, vista previa certificable, validacion de bloqueos y auditoria previa a expedicion.

## Registro

| Fecha | Evento | Resultado |
|---|---|---|
| 2026-05-15 | Creacion de Spec I2 | Define alcance, API, UI, reglas y criterios de aceptacion |
| 2026-05-15 | Creacion de Plan I2 | Define tareas T1-T10 y gates de calidad |
| 2026-05-15 | Actualizacion documental | ARRANQUE y guia de pruebas apuntan a I2 |
| 2026-05-16 | T9 (seguridad) — completado en sesion I1 | DevSecurityConfig actualizado con rutas /api/busquedas/** y /api/certificados/** |
| 2026-05-16 | T1-T3 backend busqueda | BusquedaResultadoDto, BusquedaDetalleDto, BloqueoDto, BusquedaService (JpaSpecificationExecutor), BusquedaController |
| 2026-05-16 | T4-T6 backend preview | PreviewCertificadoDto (SeccionPreviewDto, CampoPreviewDto), PreviewService, PreviewController |
| 2026-05-16 | Tests backend | BusquedaServiceTest (10 tests), PreviewServiceTest (8 tests). Total: 70 tests, BUILD SUCCESS |
| 2026-05-16 | Fix H2 test config | MODE=Oracle incompatible con LIMIT en H2 2.x; reemplazado por INIT=CREATE SCHEMA |
| 2026-05-16 | T7 BusquedaComponent | /busqueda con 5 filtros (q, idSipej, nit, estado, estadoCompletitud), paginacion, acceso a detalle y preview |
| 2026-05-16 | T8 BusquedaDetalleComponent | /busqueda/:id con 8 tabs: info, personeria, reformas, nombramientos, organos, actuaciones, documentos, completitud |
| 2026-05-16 | T8 PreviewCertificadoComponent | /certificados/preview/:id con secciones del certificado, bloqueos resaltados, advertencias, badge habilitada/no |
| 2026-05-16 | Modelos Angular I2 | BusquedaResultado, BusquedaDetalle, PreviewCertificado, BloqueoItem, SeccionPreview, CampoPreview en esal.model.ts |
| 2026-05-16 | Rutas y navegacion Angular | /busqueda, /busqueda/:id, /certificados/preview/:id en app.routes.ts; nav "Buscar ESAL" en shell |
| 2026-05-16 | T10 Documentacion | ARRANQUE, GUIA_PRUEBAS_FUNCIONALES y este log actualizados. I2 cerrado. |

## Gates De Calidad I2

| Gate | Estado |
|---|---|
| 70 tests backend, BUILD SUCCESS | PASS |
| Angular build sin errores (solo warnings no-criticos) | PASS |
| Endpoints /api/busquedas/** protegidos por autenticacion | PASS |
| Endpoints /api/certificados/preview/** protegidos por autenticacion | PASS |
| BusquedaService con filtros dinamicos (JpaSpecificationExecutor) | PASS |
| PreviewService genera bloqueos y alertas por estado | PASS |
| Auditoria registrada para BUSQUEDA_ESAL, DETALLE_ESAL_CONSULTADO, PREVIEW_CERTIFICADO_CONSULTADO/BLOQUEADO | PASS |

## Decisiones De Arranque Aprobadas

- I2 no inicia hasta completar I1.
- La vista previa se basa en datos vigentes, completitud y reglas de estado implementadas en I1.
- El boton de generacion puede mostrarse deshabilitado durante I2; la accion real queda para I3.
- Los textos legales de suspension/liquidacion/cancelacion se validan funcionalmente antes de cerrar I2.
- La seguridad ampliada de `docs/ARCHITECTURE.md` queda aprobada como referencia; lineamientos adicionales de SED se incorporaran antes de implementar si aplican.
