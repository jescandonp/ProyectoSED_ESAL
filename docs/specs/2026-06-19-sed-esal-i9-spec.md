# Spec I9 - Gestion Documental Administrativa Transversal

> Estado: especificado para revision.
> Fecha: 2026-06-19.
> Sistema: `SED_ESAL`.
> Metodologia: SDD Spec-Anchored.
> Handoff base: `docs/Handoff/handoff-20260619-i8-task2-closed-retake-task3.md`.
> Requerimiento fuente: `Documentos_Referencia/Iteracion/Aplicativo ESAL.docx`.
> Depende de: I5 mantenimiento operativo, I8 certificado PDF EYRL.

## 1. Objetivo

Abrir una nueva iteracion para formalizar la gestion documental administrativa de cada ESAL desde el aplicativo, reutilizando el almacenamiento local/backend existente y extendiendo el contrato de documentos soporte con tipificacion, version vigente, historico consultable y reglas de obligatoriedad.

I9 convierte los documentos soporte en una capacidad transversal dentro del mantenimiento/detalle de una ESAL. El objetivo no es integrar un gestor documental institucional ni crear un modulo global independiente, sino dejar funcional la carga, consulta, descarga, vigencia e historico de los documentos administrativos requeridos por el area usuaria.

El usuario objetivo es el funcionario de Inspeccion y Vigilancia que administra datos de ESAL, soporta actuaciones administrativas y necesita conservar evidencia PDF trazable para creacion/formacion, dignatarios, liquidacion y cancelacion.

## 2. Requerimiento Fuente

El documento `Aplicativo ESAL.docx` define cinco bloques:

1. Modulo de creacion y formacion:
   - Cargar y almacenar resoluciones asociadas a creacion y formacion.
   - Campos opcionales.
   - Permitir consulta y descarga.
   - Mantener trazabilidad historica.
2. Modulo de inscripcion de dignatarios:
   - Cargar resoluciones o actos administrativos relacionados con inscripcion, actualizacion o modificacion de dignatarios.
   - Campos opcionales.
   - Mantener trazabilidad historica.
3. Estado en liquidacion:
   - Cargar oficios de inscripcion del liquidador.
   - Campos obligatorios.
   - Clasificar liquidacion por tramite de cancelacion voluntaria o por termino de duracion.
4. Estado cancelado:
   - Crear campo especifico `Resolucion de cancelacion` para registrar y adjuntar el documento correspondiente.
   - Campos obligatorios.
   - Clasificar cancelacion voluntaria o por orden de autoridad.
5. Cargue de documentos en PDF:
   - Los documentos se cargan en PDF.
   - Peso maximo: 10 MB.

## 3. Supuestos

1. I9 usa el almacenamiento local/backend existente por `AlmacenamientoService`; no integra gestor documental institucional.
2. La arquitectura debe conservar una abstraccion que permita reemplazar el almacenamiento en una iteracion posterior.
3. Todo documento soporte de I9 pertenece a una ESAL.
4. Los documentos de creacion/formacion y dignatarios son opcionales, pero si se cargan deben tener metadatos minimos.
5. Los documentos de liquidacion y cancelacion son obligatorios para habilitar los cambios de estado correspondientes.
6. Un nuevo documento del mismo tipo/subtipo para una ESAL reemplaza la version vigente anterior y conserva el historico.
7. `Documentos_Referencia/` sigue siendo local y no se publica sin aprobacion expresa.

## 4. Tech Stack

- Backend: Java, Spring Boot 2.7.x, Maven, JPA/Hibernate, multipart upload.
- Almacenamiento: `AlmacenamientoService` existente, implementacion local-dev/backend actual.
- Tests backend: JUnit 5, AssertJ, MockMvc cuando aplique.
- Frontend: Angular 20, PrimeNG 20, formularios standalone.
- Seguridad: Spring Security con autorizacion efectiva en backend.

## 5. Catalogo Documental I9

| Tipo documental | Subtipo | Obligatorio | Uso |
|---|---|---:|---|
| `CREACION_FORMACION` | No aplica | No | Resolucion asociada a creacion y formacion de la entidad |
| `DIGNATARIOS` | No aplica | No | Resolucion o acto administrativo de inscripcion, actualizacion o modificacion de dignatarios |
| `LIQUIDACION` | `TRAMITE_CANCELACION_VOLUNTARIA` | Si, para estado en liquidacion | Oficio de inscripcion del liquidador por tramite de cancelacion voluntaria |
| `LIQUIDACION` | `TERMINO_DURACION` | Si, para estado en liquidacion | Oficio de inscripcion del liquidador por termino de duracion |
| `CANCELACION` | `CANCELACION_VOLUNTARIA` | Si, para estado cancelado | Resolucion de cancelacion voluntaria |
| `CANCELACION` | `ORDEN_AUTORIDAD` | Si, para estado cancelado | Resolucion de cancelacion por orden de autoridad |

Reglas:

- Los subtipos solo aplican para `LIQUIDACION` y `CANCELACION`.
- Cualquier subtipo incompatible con el tipo documental debe producir error funcional `400`.
- Un tipo/subtipo sin documento vigente no cumple regla obligatoria.
- El catalogo inicial puede implementarse como enum/constantes de aplicacion en I9; parametrizacion por base de datos queda diferida.

## 6. Metadatos Minimos

Cada carga documental debe registrar:

- ESAL.
- Tipo documental.
- Subtipo, cuando aplique.
- Numero o referencia del acto/oficio/resolucion.
- Fecha del acto/oficio/resolucion.
- Observacion opcional.
- Nombre original del archivo.
- Content type.
- Peso en bytes.
- Ruta o identificador interno de almacenamiento.
- Hash o dato tecnico equivalente si ya existe en el contrato actual.
- Indicador `vigente`.
- Fecha de carga.
- Usuario de carga.

Reglas:

- El archivo debe ser PDF.
- El peso maximo permitido es 10 MB.
- La referencia es obligatoria para todos los tipos cuando se carga documento.
- La fecha del acto/oficio/resolucion es obligatoria para todos los tipos cuando se carga documento.
- La observacion no reemplaza referencia ni fecha.

## 7. Version Vigente E Historico

El sistema debe conservar historico completo por ESAL y tipo/subtipo.

Al cargar un documento nuevo:

1. Validar ESAL, tipo, subtipo, metadatos y archivo.
2. Buscar documento vigente anterior de la misma ESAL y mismo tipo/subtipo.
3. Marcar el anterior como no vigente.
4. Registrar el nuevo documento como vigente.
5. Persistir auditoria de carga y cambio de vigencia.

Reglas:

- No se elimina fisicamente el historico desde I9.
- La consulta debe permitir ver vigentes e historicos.
- La UI debe distinguir claramente vigente vs historico.
- Si una carga falla despues de desmarcar el anterior, la transaccion debe evitar dejar el tipo/subtipo sin vigente por error parcial.

## 8. Estados Administrativos

I9 formaliza la relacion entre documentos y estados administrativos.

### 8.1. Liquidacion

Para cambiar una ESAL a estado en liquidacion, el sistema debe exigir:

- Estado destino `EstadoEsal.EN_LIQUIDACION`.
- Tipo documental `LIQUIDACION`.
- Subtipo `TRAMITE_CANCELACION_VOLUNTARIA` o `TERMINO_DURACION`.
- Documento PDF vigente.
- Referencia y fecha registradas.

Si falta el documento obligatorio, el cambio de estado se bloquea con error funcional.

### 8.2. Cancelacion

Para cambiar una ESAL a estado `CANCELADO`, el sistema debe exigir:

- Tipo documental `CANCELACION`.
- Subtipo `CANCELACION_VOLUNTARIA` o `ORDEN_AUTORIDAD`.
- Documento PDF vigente.
- Referencia y fecha registradas.

I5 permitia cancelacion sin PDF con advertencia. I9 reemplaza esa regla para el nuevo flujo: la cancelacion debe bloquearse si no existe soporte obligatorio vigente.

## 9. API

I9 debe partir de los endpoints existentes de documentos soporte y ajustarlos sin romper la separacion por rol.

Contratos esperados:

- `POST /api/esales/{id}/documentos`
  - Solo `ADMINISTRADOR`.
  - `multipart/form-data`.
  - Campos: `archivo`, `tipoDocumento`, `subtipoDocumento`, `referencia`, `fechaActo`, `observacion`.
  - Retorna documento registrado como vigente.
- `GET /api/esales/{id}/documentos`
  - `ADMINISTRADOR` y `EXPEDIDOR`.
  - Devuelve vigentes e historicos, con filtros opcionales por tipo, subtipo y vigencia si se implementan en I9.
- `GET /api/esales/{id}/documentos/{documentoId}/descarga`
  - `ADMINISTRADOR` y `EXPEDIDOR`.
  - Descarga autenticada por backend, sin exponer ruta fisica.

Reglas API:

- `POST` rechaza no PDF, archivo mayor a 10 MB, referencia vacia, fecha vacia, tipo desconocido o subtipo incompatible.
- `GET` no debe devolver bytes del archivo en listado.
- La descarga debe validar que el documento pertenece a la ESAL indicada.
- Las respuestas de descarga deben usar `application/pdf` y cabeceras restrictivas de cache.

## 10. UI

La gestion documental se ubica como seccion o pestana `Documentos` dentro del mantenimiento/detalle de la ESAL.

Capacidades para `ADMINISTRADOR`:

- Ver documentos vigentes e historicos.
- Cargar documento nuevo por tipo/subtipo.
- Registrar referencia, fecha y observacion.
- Descargar documentos.
- Ver estado de vigencia.

Capacidades para `EXPEDIDOR`:

- Ver documentos vigentes e historicos.
- Descargar documentos.
- No puede cargar ni modificar.

Reglas UI:

- No crear modulo documental global en I9.
- Mostrar peso maximo permitido antes de cargar.
- Validar extension/content type en cliente como ayuda, pero la validacion efectiva queda en backend.
- Distinguir documentos obligatorios faltantes para liquidacion/cancelacion.
- Mantener estilo institucional definido en `docs/DESIGN.md`.

## 11. Seguridad Y Auditoria

Autorizacion:

- `ADMINISTRADOR`: carga documentos, consulta listado y descarga.
- `EXPEDIDOR`: consulta listado y descarga.
- Otros roles: sin acceso salvo definicion posterior.

Auditoria minima:

- `DOCUMENTO_SOPORTE_CREADO`
- `DOCUMENTO_SOPORTE_VIGENCIA_REEMPLAZADA`
- `DOCUMENTO_SOPORTE_DESCARGADO`
- `ESAL_LIQUIDACION_BLOQUEADA_SIN_DOCUMENTO`
- `ESAL_CANCELACION_BLOQUEADA_SIN_DOCUMENTO`

Cada evento debe registrar:

- Usuario.
- Rol cuando este disponible.
- ESAL.
- Tipo/subtipo documental.
- Documento afectado.
- Fecha/hora.
- Resultado.
- Error funcional si aplica.

La auditoria no debe registrar bytes de archivo ni rutas fisicas sensibles.

## 12. Relacion Con Certificados

I9 no cambia la generacion del certificado PDF I8.

Reglas:

- Certificados historicos ya generados permanecen inmutables.
- La gestion documental puede alimentar validaciones administrativas futuras, pero no modifica el contenido del certificado en I9.
- El numero interno del certificado se mantiene como dato interno/trazabilidad y no se reintroduce en el PDF.

## 13. Testing Strategy

### Backend

RED inicial:

- Documento no PDF debe fallar.
- PDF mayor a 10 MB debe fallar.
- Carga sin referencia debe fallar.
- Carga sin fecha debe fallar.
- Subtipo incompatible debe fallar.
- Nueva carga del mismo tipo/subtipo debe dejar solo un vigente.
- `EXPEDIDOR` no puede cargar.
- `EXPEDIDOR` puede listar y descargar.
- Cambio a liquidacion sin documento obligatorio debe fallar.
- Cambio a cancelado sin documento obligatorio debe fallar.

GREEN:

- Extender dominio/DTO/servicio/controlador hasta cumplir contratos.
- Mantener storage local/backend existente.
- Agregar descarga autenticada si no existe.
- Integrar validacion documental en `EsalMaintenanceService`.

Regression:

- `DocumentoSoporteServiceTest`
- `EsalMaintenanceServiceTest`
- `EsalSecurityTest` o prueba equivalente existente
- `mvn test`
- `mvn package -DskipTests`

### Frontend

- Test focalizado de seccion documental si existe patron en Angular.
- Build Angular.
- Verificar que `ADMINISTRADOR` ve formulario de carga y `EXPEDIDOR` no.
- Verificar render de historico/vigente y accion de descarga.

## 14. Boundaries

Always:

- Mantener SDD: SPEC, PLAN, execution log y verificacion antes de cierre.
- Reutilizar `DocumentoSoporte` y `AlmacenamientoService` como base.
- Preservar autorizacion efectiva en backend.
- Mantener historico consultable.
- Bloquear liquidacion/cancelacion sin documento obligatorio vigente.
- Registrar evidencias en execution log.

Ask first:

- Integrar gestor documental institucional.
- Publicar `Documentos_Referencia/`.
- Cambiar almacenamiento definitivo fuera de la abstraccion existente.
- Agregar dependencias externas.
- Cambiar contenido del certificado PDF.

Never:

- Servir documentos por rutas fisicas directas.
- Permitir que frontend sea fuente de autorizacion.
- Eliminar historico documental por defecto.
- Reintroducir numero interno en PDF del certificado.
- Revertir cambios no relacionados del worktree.

## 15. Success Criteria

1. `ADMINISTRADOR` puede cargar PDF de creacion/formacion con referencia y fecha.
2. `ADMINISTRADOR` puede cargar PDF de dignatarios con referencia y fecha.
3. `ADMINISTRADOR` puede cargar PDF de liquidacion con subtipo valido, referencia y fecha.
4. `ADMINISTRADOR` puede cargar PDF de cancelacion con subtipo valido, referencia y fecha.
5. El sistema rechaza archivos no PDF.
6. El sistema rechaza archivos mayores a 10 MB.
7. El sistema rechaza carga sin referencia o sin fecha.
8. Cargar un nuevo documento del mismo tipo/subtipo deja el anterior como historico no vigente.
9. `ADMINISTRADOR` y `EXPEDIDOR` pueden consultar vigentes e historicos.
10. `ADMINISTRADOR` y `EXPEDIDOR` pueden descargar documentos por backend autenticado.
11. `EXPEDIDOR` no puede cargar documentos.
12. Liquidacion se bloquea si no existe documento vigente obligatorio.
13. Cancelacion se bloquea si no existe documento vigente obligatorio.
14. La UI muestra una seccion/pestana `Documentos` dentro del mantenimiento/detalle de ESAL.
15. Tests backend pasan.
16. WAR backend se genera.
17. Tests/build Angular pasan o queda limitacion ambiental documentada.
18. README, ARRANQUE, GUIA_PRUEBAS_FUNCIONALES y execution log quedan actualizados al cierre de I9.

## 16. Open Questions

1. Confirmar si la descarga debe auditarse en cada evento o solo en primer acceso por usuario/sesion. Decision propuesta para I9: auditar cada descarga.
2. Confirmar si el listado debe traer todos los historicos por defecto o solo vigentes con opcion de expandir. Decision propuesta para I9: mostrar vigentes primero e historico consultable en la misma seccion.
