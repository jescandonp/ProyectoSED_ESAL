# Plan I5 - CRUD Y Mantenimiento Operativo De ESAL

> Estado: aprobado como plan de diseno.
> Fecha: 2026-05-21.
> Spec: `docs/specs/2026-05-21-sed-esal-i5-spec.md`.
> Sistema: `SED_ESAL`.

## 1. Objetivo

Implementar el incremento I5 para que `SED_ESAL` permita crear y mantener ESAL desde el aplicativo, por secciones, con guardado independiente, auditoria, recalculo de completitud y manejo formal del estado `CANCELADO`.

## 2. Precondiciones

- I1 implementado: modelo base, estados, documentos soporte, auditoria y completitud.
- I2 implementado: busqueda, detalle y preview certificable.
- I3 implementado: certificados historicos con version de datos.
- I4 implementado: autorizacion por rol y proteccion de endpoints.
- Rol `ADMINISTRADOR` autorizado para mantenimiento.
- Rol `EXPEDIDOR` limitado a consulta/generacion.

## 3. Tareas

### T1 - Contratos Y DTOs I5

- Crear DTOs de informacion principal.
- Crear DTOs de personeria juridica.
- Crear DTOs de representante legal.
- Crear DTOs de organo de administracion.
- Crear DTOs de cancelacion y reactivacion.
- Crear DTO agregado de mantenimiento.

Verificacion:

- DTOs compilan.
- OpenAPI documenta contratos I5.
- No se exponen campos internos innecesarios.

### T2 - Servicio De Mantenimiento Seccional

- Crear `EsalMaintenanceService` o ampliar `EsalService` manteniendo responsabilidades claras.
- Implementar crear ESAL.
- Implementar actualizar informacion principal.
- Implementar obtener vista de mantenimiento.
- Invocar recalculo de completitud despues de cada mutacion.
- Registrar auditoria.

Verificacion:

- Admin crea ESAL.
- Admin actualiza informacion principal.
- Completitud se recalcula.
- Auditoria registra cambios.

### T3 - Personeria Juridica

- Implementar upsert 1:1 de `PersoneriaJuridica`.
- Evitar duplicados por `esalId`.
- Auditar creacion/actualizacion.
- Integrar respuesta en vista de mantenimiento.

Verificacion:

- Si no existe personeria, se crea.
- Si existe, se actualiza.
- No se generan duplicados.
- Preview usa datos actualizados.

### T4 - Representantes Legales

- Implementar listar representantes de la ESAL.
- Implementar crear representante.
- Implementar editar representante.
- Implementar cambio de vigencia.
- Limitar tipos a `REPRESENTANTE_LEGAL` y `REPRESENTANTE_LEGAL_SUPLENTE`.
- Auditar cambios.

Verificacion:

- Se agrega representante principal.
- Se agrega suplente.
- Se cambia vigencia.
- Expedidor recibe 403 en mutaciones.

### T5 - Organo De Administracion

- Implementar listar miembros.
- Implementar crear miembro.
- Implementar editar miembro.
- Definir manejo tecnico de inactivacion si el modelo requiere campo adicional.
- Auditar cambios.

Verificacion:

- Se agrega miembro.
- Se edita miembro.
- La informacion aparece en detalle/preview cuando aplica.
- No se elimina fisicamente informacion historica sin decision explicita.

### T6 - Cancelacion

- Implementar endpoint de cancelacion.
- Validar resolucion, fecha y motivo.
- Crear `ActuacionAdministrativa` tipo `CANCELACION`.
- Cambiar `Esal.estado` a `CANCELADO`.
- Generar advertencia si falta PDF soporte.
- Auditar cancelacion.

Verificacion:

- Cancelacion sin resolucion falla.
- Cancelacion sin fecha falla.
- Cancelacion sin motivo falla.
- Cancelacion valida cambia estado.
- Cancelacion sin PDF deja advertencia pero guarda.

### T7 - Reactivacion O Cambio Desde Cancelado

- Implementar endpoint de reactivacion/cambio de estado desde `CANCELADO`.
- Exigir motivo obligatorio.
- Mantener historico de actuacion de cancelacion.
- Auditar reactivacion.

Verificacion:

- Reactivacion sin motivo falla.
- Reactivacion con motivo cambia estado.
- Auditoria conserva traza.
- Actuacion de cancelacion no se elimina.

### T8 - Autorizacion Y Seguridad

- Proteger endpoints I5 para `ADMINISTRADOR`.
- Garantizar 403 para `EXPEDIDOR` en mutaciones.
- Mantener consulta permitida segun I2/I3.
- Actualizar pruebas de seguridad.

Verificacion:

- Admin accede a I5.
- Expedidor no crea ni edita.
- Usuario anonimo recibe 401.

### T9 - UI Administrativa Angular

- Crear ruta `/admin/esales/:id/mantenimiento`.
- Agregar accion `Actualizar informacion` para administradores.
- Construir secciones con guardado independiente.
- Implementar formularios de informacion principal y personeria.
- Implementar listas editables de representantes y organo.
- Implementar panel de cancelacion/reactivacion.
- Mostrar advertencias y completitud.

Verificacion:

- Admin navega desde detalle a mantenimiento.
- Cada seccion guarda de forma independiente.
- Errores se muestran por seccion.
- ESAL cancelada bloquea edicion ordinaria salvo reactivacion.

### T10 - Integracion Con Preview Y Certificados

- Verificar que preview use datos actualizados.
- Verificar que certificados historicos no cambien.
- Verificar que nuevos certificados usen informacion vigente.
- Agregar pruebas de no regresion.

Verificacion:

- Certificado historico conserva version de datos.
- Preview posterior a edicion refleja cambios.
- Nuevo certificado usa datos vigentes.

### T11 - Documentacion Y Guia Funcional

- Actualizar `README.md` si aplica.
- Actualizar `docs/ARRANQUE.md` solo si cambian endpoints o pruebas relevantes.
- Actualizar `docs/GUIA_PRUEBAS_FUNCIONALES.md`.
- Crear log de ejecucion I5.

Verificacion:

- Documentos referencian I5.
- Guia funcional contiene casos de creacion, edicion, cancelacion y reactivacion.

## 4. Orden De Ejecucion

1. T1 - Contratos y DTOs.
2. T2 - Servicio base de mantenimiento.
3. T3 - Personeria juridica.
4. T4 - Representantes legales.
5. T5 - Organo de administracion.
6. T6 - Cancelacion.
7. T7 - Reactivacion.
8. T8 - Seguridad.
9. T9 - UI Angular.
10. T10 - Integracion con preview/certificados.
11. T11 - Documentacion.

## 5. Gates De Calidad

- `mvn test` en `sed-esal-backend`.
- Build Angular en `sed-esal-angular`.
- Pruebas de autorizacion para `ADMINISTRADOR` y `EXPEDIDOR`.
- Pruebas de cancelacion y reactivacion.
- Prueba de inmutabilidad de certificados historicos.
- Revision documental contra `docs/specs/2026-05-21-sed-esal-i5-spec.md`.

## 6. Riesgos

- `OrganoAdministracion` no tiene campo de vigencia; se debe decidir antes de implementar inactivacion.
- La ausencia de soporte PDF de cancelacion puede requerir ajuste en `CompletitudService`.
- La reactivacion desde `CANCELADO` puede requerir regla juridica adicional antes de produccion.
- Si preview o PDF tienen consultas directas no centralizadas, pueden no reflejar todos los datos editados hasta ajustar servicios I2/I3.

## 7. Resultado Esperado

Al cerrar I5, `SED_ESAL` debe permitir mantenimiento operativo por aplicativo para las secciones prioritarias de una ESAL, registrar cancelaciones formalmente, conservar trazabilidad, proteger certificados historicos y dejar preparada la extension posterior a mas secciones del PRD.

## 8. Ajuste Post-Cierre 2026-05-27

Hallazgo posterior al cierre funcional:

- La funcionalidad I5 estaba implementada, pero su descubribilidad en frontend era incompleta.
- El menu lateral exponia `Buscar ESAL` hacia `/busqueda`, ruta no registrada en el router Angular actual.
- El acceso a mantenimiento I5 quedaba visible en el detalle administrativo, pero no en el detalle general consultado por un `ADMINISTRADOR`.

Acciones de ajuste:

- Retirar la entrada de menu con ruta rota para evitar redireccion al login por fallback.
- Exponer `Actualizar informacion` en el detalle general cuando el usuario autenticado sea `ADMINISTRADOR`.
- Verificar nuevamente navegacion, tests y build frontend.

Verificacion del ajuste:

- `npm test -- --watch=false --browsers=ChromeHeadless`
- `npm run build`
