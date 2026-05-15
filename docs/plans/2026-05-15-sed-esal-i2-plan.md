# Plan I2 - Busqueda Operativa Y Vista Previa De Certificado

> Estado: aprobado como plan futuro.
> Fecha: 2026-05-15.
> Spec: `docs/specs/2026-05-15-sed-esal-i2-spec.md`.
> Sistema: `SED_ESAL`.

## 1. Objetivo

Implementar el segundo incremento funcional de `SED_ESAL`: buscador interno, detalle en modo consulta, vista previa de certificado, validacion de bloqueos y trazabilidad previa a expedicion.

## 2. Precondiciones

- I1 implementado o aprobado como base.
- Modelo de ESAL, completitud, estados y auditoria disponibles.
- Roles `ADMINISTRADOR` y `EXPEDIDOR` configurados en local-dev.
- `docs/ARCHITECTURE.md` aprobado como referencia de seguridad; si SED confirma lineamientos adicionales antes de implementar, revisar plan.

## 3. Tareas

### T1 - Contratos Backend De Busqueda

- Crear DTOs de filtros y resultados.
- Crear endpoint `GET /api/busquedas/esales`.
- Implementar busqueda exacta/parcial por `ID SIPEJ`.
- Implementar busqueda exacta/parcial por nombre.
- Agregar filtros por NIT, estado y completitud.
- Implementar paginacion y ordenamiento.

Verificacion:

- Tests de busqueda exacta y parcial.
- Tests de busqueda sin resultados.
- Tests de paginacion.

### T2 - Auditoria De Busqueda Y Detalle

- Registrar `BUSQUEDA_ESAL`.
- Registrar criterios usados.
- Registrar cantidad de resultados.
- Registrar `DETALLE_ESAL_CONSULTADO`.
- Asociar ESAL, `ID SIPEJ` y NIT cuando aplique.

Verificacion:

- Test de auditoria al buscar.
- Test de auditoria al abrir detalle.

### T3 - Contrato De Detalle Solo Lectura

- Exponer endpoint `GET /api/busquedas/esales/{id}`.
- Retornar informacion por secciones.
- Incluir estado administrativo y completitud.
- Incluir documentos soporte disponibles.
- Incluir advertencias existentes.

Verificacion:

- Admin y Expedidor pueden consultar.
- Expedidor no recibe acciones de edicion.

### T4 - Servicio De Preview Certificable

- Crear servicio de vista previa.
- Construir estructura por secciones del certificado.
- Tomar version vigente de datos.
- Incluir `versionDatos`.
- Incluir estado y completitud.

Verificacion:

- Preview de ESAL activa.
- Preview con reformas dinamicas.
- Preview incluye datos principales del formato.

### T5 - Reglas De Estado En Preview

- Aplicar regla `ACTIVO`.
- Aplicar alerta `SUSPENDIDO`.
- Aplicar leyenda y parrafo `EN_LIQUIDACION`.
- Aplicar vista restringida `CANCELADO`.

Verificacion:

- Tests por cada estado.
- Tests de bloqueo cuando faltan datos de estado.

### T6 - Validacion De Bloqueos

- Crear resultado `GENERACION_HABILITADA` / `GENERACION_BLOQUEADA`.
- Generar bloqueos por campo obligatorio faltante.
- Generar bloqueos por regla de estado.
- Conservar advertencias no bloqueantes.
- Exponer `POST /api/certificados/preview/esales/{id}/validar` si se requiere validacion explicita.

Verificacion:

- Falta obligatorio bloquea.
- Falta opcional no bloquea.
- Valor `NR` obligatorio bloquea.
- Valor `NR` opcional no bloquea.

### T7 - UI Buscador

- Crear ruta `/busqueda`.
- Crear filtros por `ID SIPEJ`, nombre, NIT, estado y completitud.
- Crear tabla de resultados.
- Mostrar chips de estado y semaforo.
- Implementar limpiar filtros.
- Manejar estado sin resultados.

Verificacion:

- Busqueda por filtros desde UI.
- Resultados paginados.
- Mensajes claros.

### T8 - UI Detalle Y Preview

- Crear rutas `/busqueda/:id` y `/certificados/preview/:id`.
- Mostrar detalle por secciones.
- Mostrar vista previa de certificado.
- Mostrar panel de advertencias y bloqueos.
- Mostrar accion de generacion deshabilitada si hay bloqueos.
- Mantener modo solo lectura para Expedidor.

Verificacion:

- Expedidor abre detalle sin controles de edicion.
- Preview muestra secciones.
- Bloqueos son visibles y accionables.

### T9 - Seguridad Y OpenAPI

- Proteger endpoints para `ADMINISTRADOR` y `EXPEDIDOR`.
- Restringir auditoria avanzada a `ADMINISTRADOR`.
- Documentar DTOs en Swagger.
- Documentar errores funcionales.

Verificacion:

- 401 sin autenticacion.
- 403 en auditoria para Expedidor.
- Swagger refleja contratos.

### T10 - Documentacion Y Log

- Actualizar `docs/ARRANQUE.md`.
- Actualizar `docs/GUIA_PRUEBAS_FUNCIONALES.md`.
- Mantener log de ejecucion I2.
- Registrar cambios de seguridad si se confirma seccion ampliada en arquitectura.

Verificacion:

- Documentos apuntan a spec/plan I2.
- Guia tiene casos funcionales I2.

## 4. Orden De Ejecucion

1. T1.
2. T2.
3. T3.
4. T4.
5. T5.
6. T6.
7. T7.
8. T8.
9. T9.
10. T10.

## 5. Gates De Calidad

No cerrar I2 hasta que:

- Busqueda exacta/parcial funcione por `ID SIPEJ` y nombre.
- Resultados sean paginados.
- Vista previa use version vigente de datos.
- Bloqueos distingan faltantes obligatorios de opcionales.
- Reglas por estado sean visibles en preview.
- Expedidor no pueda editar.
- Auditoria registre busqueda, detalle, preview y errores.
- Documentacion quede actualizada.

## 6. Riesgos

- Campos exactos del certificado pueden requerir ajuste al mapear la plantilla tecnica.
- Estado `CANCELADO` requiere confirmacion del contenido permitido final.
- Textos legales de suspension/liquidacion pueden cambiar por decision de DIV.
- La futura seccion de seguridad puede modificar endpoints, headers o auditoria.

## 7. No Hacer En I2

- No generar PDF.
- No asignar numero unico.
- No calcular hash de PDF.
- No descargar certificado.
- No implementar firmante.
- No implementar QR.
- No implementar Azure AD real.
