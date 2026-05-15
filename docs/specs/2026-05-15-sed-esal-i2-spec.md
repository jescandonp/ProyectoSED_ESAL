# Spec I2 - Busqueda Operativa Y Vista Previa De Certificado

> Estado: propuesta para revision.  
> Fecha: 2026-05-15.  
> Sistema: `SED_ESAL`.  
> Metodologia: SDD Spec-Anchored.  
> PRD base: `docs/specs/2026-05-09-sed-esal-certificados-prd.md`.  
> Depende de: `docs/specs/2026-05-15-sed-esal-i1-spec.md`.

## 1. Objetivo

Implementar el flujo operativo previo a la expedicion: busqueda interna de ESAL por `ID SIPEJ` y nombre, visualizacion de resultados, detalle de datos certificables, vista previa estructurada del certificado y validacion de bloqueos antes de generar PDF.

I2 no genera el PDF final ni asigna numero unico de certificado. I2 debe dejar listo el contrato funcional para que I3 pueda emitir el certificado usando los mismos datos validados.

## 2. Alcance

Incluye:

- Busqueda exacta y parcial por `ID SIPEJ`.
- Busqueda exacta y parcial por nombre.
- Resultados paginados, ordenados y filtrables.
- Visualizacion de estado administrativo y semaforo de completitud en resultados.
- Detalle de ESAL en modo consulta para `EXPEDIDOR`.
- Vista previa de los datos que alimentaran el certificado.
- Validacion de datos obligatorios antes de habilitar generacion.
- Bloqueo funcional cuando existan faltantes obligatorios.
- Mensajes de advertencia por campo, seccion y regla.
- Reglas de visualizacion por estado: `ACTIVO`, `SUSPENDIDO`, `EN_LIQUIDACION`, `CANCELADO`.
- Auditoria de busquedas, seleccion de resultado, vista previa y errores de validacion.
- Contrato backend/frontend para futura generacion PDF.

Excluye:

- Generacion PDF.
- Numero unico de certificado.
- Hash de PDF.
- Descarga de certificado.
- Firmante configurable.
- QR o codigo de verificacion.
- Consulta publica externa.
- Integracion Azure AD real.

## 3. Supuestos

- I1 ya dejo disponible el modelo base, la importacion, la completitud y la seguridad local-dev.
- El Expedidor no puede editar informacion.
- La vista previa debe usar la version vigente de datos de la ESAL.
- La validacion de bloqueos se basa en campos requeridos por plantilla, diccionario de obligatoriedad y reglas activas por estado.
- La seccion ampliada de seguridad de `docs/ARCHITECTURE.md` sigue pendiente. Si se confirma antes de implementar I2, debe revisarse autorizacion, auditoria, CORS y proteccion de endpoints.

## 4. Actores Y Permisos

### ADMINISTRADOR

Puede:

- Buscar ESAL.
- Abrir detalle.
- Ver vista previa.
- Ver advertencias y bloqueos.
- Consultar trazas de busqueda y vista previa.

### EXPEDIDOR

Puede:

- Buscar ESAL.
- Abrir detalle en modo lectura.
- Ver vista previa.
- Ver advertencias y bloqueos.

No puede:

- Editar informacion.
- Cambiar estado.
- Cargar documentos.
- Forzar generacion cuando existan bloqueos.

## 5. Flujo Funcional

1. Usuario ingresa al sistema.
2. Usuario abre buscador.
3. Usuario digita `ID SIPEJ` o nombre.
4. Sistema ejecuta busqueda exacta y parcial segun filtros.
5. Sistema muestra resultados con datos minimos.
6. Usuario selecciona una ESAL.
7. Sistema muestra detalle en modo consulta.
8. Usuario abre vista previa de certificado.
9. Sistema construye datos certificables desde la version vigente.
10. Sistema evalua completitud y reglas de estado.
11. Sistema muestra:
    - Vista previa habilitada y lista para generar en I3, o
    - Bloqueos con mensajes especificos.

## 6. Busqueda

### 6.1. Criterios

Campos:

- `idSipej`
- `nombre`
- `nit`
- `estado`
- `estadoCompletitud`

Reglas:

- `ID SIPEJ` debe permitir coincidencia exacta.
- `ID SIPEJ` debe permitir coincidencia parcial.
- Nombre debe permitir coincidencia exacta.
- Nombre debe permitir coincidencia parcial.
- La busqueda debe ignorar diferencias de mayusculas/minusculas.
- La busqueda debe normalizar espacios repetidos.
- Si no se informa criterio, el sistema no debe ejecutar una busqueda masiva sin confirmacion o paginacion explicita.
- Resultados deben ser paginados.

### 6.2. Resultado

Columnas minimas:

- Nombre.
- `ID SIPEJ`.
- NIT.
- Domicilio.
- Estado administrativo.
- Semaforo de completitud.
- Ultima actualizacion.
- Accion para ver detalle.

UX:

- Usar tabla densa.
- Usar chips para estado y completitud.
- Mostrar contador de resultados.
- Mostrar mensaje claro cuando no haya coincidencias.
- Permitir limpiar filtros.

## 7. Detalle En Modo Consulta

El detalle debe organizarse por secciones:

- Informacion principal.
- Constitucion y reformas.
- Nombramientos.
- Organo de administracion.
- Actuaciones administrativas.
- Documentos soporte.
- Completitud.

Reglas:

- `EXPEDIDOR` ve solo lectura.
- `ADMINISTRADOR` puede navegar desde detalle hacia edicion si I1 lo implemento.
- Datos faltantes deben mostrarse como `No registrado` o mensaje equivalente, no como error tecnico.
- Valores `NR` deben tratarse como faltantes para validacion, pero mostrarse con una advertencia explicita cuando provengan de historico.

## 8. Vista Previa De Certificado

### 8.1. Datos A Mostrar

La vista previa debe mostrar los datos que apareceran en el certificado:

- Nombre de la ESAL.
- NIT.
- Domicilio.
- `ID SIPEJ`.
- Personeria juridica.
- Fecha de reconocimiento.
- Entidad que expide.
- Inscripcion, si aplica.
- Reformas estatutarias vigentes.
- Objeto social.
- Representante legal.
- Tipo y numero de documento del representante.
- Facultades y limitaciones del representante.
- Organo de administracion.
- Integrantes/cargos del organo, cuando aplique al formato.
- Revisor fiscal, cuando aplique.
- Duracion.
- Estado administrativo y leyendas especiales.
- Fecha de vista previa.

### 8.2. Estados

`ACTIVO`:

- La vista previa refleja informacion vigente registrada.

`SUSPENDIDO`:

- Muestra alerta visible de suspension.
- Muestra tiempo de suspension.
- Bloquea si falta tiempo de suspension u otro dato obligatorio de la actuacion.

`EN_LIQUIDACION`:

- Muestra `EN LIQUIDACION` junto al nombre.
- Muestra parrafo de disolucion/liquidacion cuando existan datos suficientes.
- Bloquea si faltan acta, organo o fecha requeridos para el parrafo.

`CANCELADO`:

- Muestra solo informacion permitida de constitucion y cancelacion.
- Muestra resolucion de cancelacion y fecha.
- Bloquea si faltan resolucion o fecha de cancelacion.

## 9. Validacion De Bloqueo

La vista previa debe entregar un resultado de validacion:

- `GENERACION_HABILITADA`
- `GENERACION_BLOQUEADA`

Un bloqueo debe incluir:

- Seccion.
- Campo o regla.
- Tipo: campo faltante, documento faltante, dato inconsistente o regla de estado.
- Mensaje para usuario.
- Indicio de si el dato proviene de carga historica.

Reglas:

- Campos opcionales faltantes no bloquean.
- Campos `NR` no bloquean si no son requeridos por formato/regla activa.
- Campos requeridos por la plantilla bloquean si estan vacios, `NR` o inconsistentes.
- Reglas especiales por estado bloquean cuando falten datos necesarios para mostrar el contenido obligatorio.
- El sistema no debe permitir avanzar a generacion PDF en I3 si el resultado es `GENERACION_BLOQUEADA`.

## 10. API

Endpoints propuestos:

```text
GET  /api/busquedas/esales
GET  /api/busquedas/esales/{id}
GET  /api/certificados/preview/esales/{id}
POST /api/certificados/preview/esales/{id}/validar
GET  /api/admin/auditoria?accion=BUSQUEDA_ESAL
```

Parametros de `GET /api/busquedas/esales`:

- `q`
- `idSipej`
- `nombre`
- `nit`
- `estado`
- `estadoCompletitud`
- `page`
- `size`
- `sort`

DTO minimo de resultado:

- `id`
- `nombre`
- `idSipej`
- `nit`
- `domicilio`
- `estado`
- `estadoCompletitud`
- `updatedAt`

DTO minimo de preview:

- `esalId`
- `idSipej`
- `estado`
- `estadoCompletitud`
- `versionDatos`
- `generacionHabilitada`
- `secciones`
- `advertencias`
- `bloqueos`

Reglas:

- Busqueda y preview permiten roles `ADMINISTRADOR` y `EXPEDIDOR`.
- Auditoria avanzada solo permite `ADMINISTRADOR`.
- Swagger debe documentar filtros, paginacion, DTOs y codigos de error.

## 11. UI

Rutas propuestas:

```text
/busqueda
/busqueda/:id
/certificados/preview/:id
```

Pantallas:

- Buscador de ESAL.
- Resultado de busqueda.
- Detalle de ESAL en modo consulta.
- Vista previa de certificado.
- Panel de bloqueos/advertencias.

UX:

- Seguir `docs/DESIGN.md`.
- Interfaz administrativa, compacta y sin landing.
- Acciones principales visibles: buscar, limpiar, ver detalle, ver vista previa.
- Boton "Generar certificado" puede mostrarse deshabilitado con texto de bloqueo, pero la accion real queda para I3.
- Mensajes de bloqueo deben ser accionables para el administrador aunque el expedidor no pueda corregir.

## 12. Auditoria

Eventos I2:

- `BUSQUEDA_ESAL`
- `DETALLE_ESAL_CONSULTADO`
- `PREVIEW_CERTIFICADO_CONSULTADO`
- `PREVIEW_CERTIFICADO_BLOQUEADO`
- `ERROR_VALIDACION_PREVIEW`

Campos minimos:

- Usuario.
- Rol.
- Fecha/hora.
- Accion.
- Criterios de busqueda.
- ESAL, cuando aplique.
- `ID SIPEJ`, cuando aplique.
- NIT, cuando aplique.
- Resultado.
- Detalle.
- IP/equipo si esta disponible.

## 13. Pruebas

Backend:

- Busqueda exacta por `ID SIPEJ`.
- Busqueda parcial por `ID SIPEJ`.
- Busqueda exacta por nombre.
- Busqueda parcial por nombre.
- Busqueda sin resultados.
- Paginacion y ordenamiento.
- Preview de ESAL activa.
- Preview de ESAL suspendida.
- Preview de ESAL en liquidacion.
- Preview de ESAL cancelada.
- Bloqueo por campo obligatorio faltante.
- No bloqueo por campo opcional faltante.
- Auditoria de busqueda y preview.
- Seguridad por rol.

Frontend:

- Buscador renderiza.
- Filtros ejecutan consulta.
- Resultados muestran estado y completitud.
- Detalle solo lectura para expedidor.
- Vista previa muestra secciones del certificado.
- Bloqueos se muestran por campo/seccion.
- Boton de generacion queda deshabilitado si hay bloqueo.

Manual:

- Ejecutar casos I2 de `docs/GUIA_PRUEBAS_FUNCIONALES.md`.

## 14. Criterios De Aceptacion

1. `ADMINISTRADOR` y `EXPEDIDOR` pueden abrir el buscador.
2. La busqueda por `ID SIPEJ` exacto retorna la ESAL correcta.
3. La busqueda por `ID SIPEJ` parcial retorna coincidencias.
4. La busqueda por nombre exacto retorna la ESAL correcta.
5. La busqueda por nombre parcial retorna coincidencias.
6. Los resultados muestran estado administrativo y semaforo.
7. Los resultados son paginados.
8. El usuario puede abrir detalle de una ESAL.
9. `EXPEDIDOR` no puede editar datos desde el detalle.
10. La vista previa muestra los datos del certificado por secciones.
11. La vista previa aplica reglas para ESAL activa.
12. La vista previa aplica alerta para ESAL suspendida.
13. La vista previa aplica leyenda para ESAL en liquidacion.
14. La vista previa limita informacion para ESAL cancelada.
15. La vista previa bloquea generacion cuando faltan datos obligatorios.
16. La vista previa no bloquea por campos opcionales faltantes.
17. Cada bloqueo indica seccion, campo/regla y mensaje claro.
18. El contrato de preview expone `versionDatos`.
19. Se registran trazas de busqueda, detalle, preview y errores.
20. `ARRANQUE.md` y `GUIA_PRUEBAS_FUNCIONALES.md` quedan actualizados para I2.

## 15. Fuera De Alcance

- Emision PDF.
- Consecutivo oficial.
- Firmante.
- Hash.
- Descarga.
- QR.
- Consulta externa.

## 16. Preguntas Abiertas

1. Confirmar texto exacto para mensajes de bloqueo al usuario.
2. Confirmar si el boton de generacion debe ocultarse o mostrarse deshabilitado durante I2.
3. Confirmar campos exactos permitidos en certificado para estado `CANCELADO`.
4. Confirmar texto final de alerta para estado `SUSPENDIDO`.
5. Confirmar texto final del parrafo para estado `EN_LIQUIDACION`.
6. Confirmar seccion ampliada de seguridad de `docs/ARCHITECTURE.md`.
