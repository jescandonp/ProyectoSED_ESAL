# Execution Log I2 - Busqueda Operativa Y Vista Previa De Certificado

> Spec: `docs/specs/2026-05-15-sed-esal-i2-spec.md`
> Plan: `docs/plans/2026-05-15-sed-esal-i2-plan.md`
> Estado: completado. 65 tests backend. Build Angular OK.
> Fecha: 2026-05-15.

## Resumen

Se crea la especificacion y el plan de I2 para cubrir busqueda interna, detalle solo lectura, vista previa certificable, validacion de bloqueos y auditoria previa a expedicion.

## Registro

| Fecha | Evento | Resultado |
|---|---|---|
| 2026-05-15 | Creacion de Spec I2 | Define alcance, API, UI, reglas y criterios de aceptacion |
| 2026-05-15 | Creacion de Plan I2 | Define tareas T1-T10 y gates de calidad |
| 2026-05-15 | Actualizacion documental | ARRANQUE y guia de pruebas deben apuntar a I2 |
| 2026-05-16 | T1 BusquedaResultadoDto + BusquedaDetalleDto | DTOs de respuesta para busqueda paginada y detalle |
| 2026-05-16 | T2 BusquedaService | JpaSpecificationExecutor con filtros dinamicos q/idSipej/nit/estado/completitud |
| 2026-05-16 | T3 BusquedaController `/api/busquedas/esales` | Endpoint paginado con auditoria |
| 2026-05-16 | Fix EsalRepository | Agrega JpaSpecificationExecutor<Esal> a la interfaz |
| 2026-05-16 | Fix application-test.yml | Reemplaza MODE=Oracle por INIT=CREATE SCHEMA para compatibilidad H2 2.x con paginacion Criteria API |
| 2026-05-16 | T4 AuditoriaAcciones + AuditoriaService | Constantes de auditoria y servicio REQUIRES_NEW |
| 2026-05-16 | T5 BloqueoDto + PreviewCertificadoDto | DTOs de vista previa con secciones, campos, bloqueos y advertencias |
| 2026-05-16 | T6 PreviewService | Reglas de estado, campos NR/faltantes, generacionHabilitada |
| 2026-05-16 | T7 PreviewController `/api/certificados/preview/esales/{id}` | Endpoint de vista previa con auditoria |
| 2026-05-16 | T8 BusquedaServiceTest + PreviewServiceTest | 10 nuevos tests, total backend 65, BUILD SUCCESS |
| 2026-05-16 | T9 Angular esal.model.ts | Tipos BusquedaResultado, BloqueoItem, CampoPreview, SeccionPreview, PreviewCertificado |
| 2026-05-16 | T10 Angular esales-list | Filtros q/idSipej/nit/completitud, endpoint /api/busquedas/esales |
| 2026-05-16 | T10 Angular esales-detail | Boton Vista Previa, navegacion a certificados/preview/:id |
| 2026-05-16 | T10 Angular preview-certificado | Componente completo: header, alertas, bloqueos, secciones, pie |
| 2026-05-16 | T10 Angular app.routes.ts | Ruta certificados/preview/:id registrada |
| 2026-05-16 | Build Angular | ng build completado sin errores criticos |

## Decisiones De Arranque Aprobadas

- I2 no inicia hasta completar I1.
- La vista previa se basa en datos vigentes, completitud y reglas de estado implementadas en I1.
- El boton de generacion puede mostrarse deshabilitado durante I2; la accion real queda para I3.
- Los textos legales de suspension/liquidacion/cancelacion se validan funcionalmente antes de cerrar I2.
- La seguridad ampliada de `docs/ARCHITECTURE.md` queda aprobada como referencia; lineamientos adicionales de SED se incorporaran antes de implementar si aplican.
