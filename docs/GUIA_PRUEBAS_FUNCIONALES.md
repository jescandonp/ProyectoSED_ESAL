# SED_ESAL - Guia De Pruebas Funcionales

> Estado: guia base aprobada, pre-implementacion funcional.
> Fecha: 2026-05-15.
> Marco: SDD Spec-Anchored por incrementos.

## 1. Objetivo

Validar funcionalmente `SED_ESAL` desde la base documental hasta los flujos de administracion de ESAL, carga inicial, busqueda, completitud, documentos soporte, expedicion de certificado, PDF y trazabilidad.

Esta guia evolucionara con cada incremento. Al cierre de cada incremento, debe actualizarse con pruebas manuales y datos concretos.

## 2. Formato De Registro De Evidencia

Usar este formato para cada caso:

```text
ID prueba:
Incremento:
Rol:
Accion:
Datos usados:
Resultado esperado:
Resultado obtenido:
Endpoint observado:
Estado final:
Captura o log:
OK / Error:
Observaciones:
```

## 3. Orden Recomendado De Validacion

1. Prevalidacion tecnica.
2. Carga inicial.
3. Administracion de ESAL.
4. Estados y completitud.
5. Busqueda.
6. Vista previa.
7. Certificado/PDF.
8. Trazabilidad.
9. Seguridad por rol.

## 4. Prevalidacion Tecnica

Pendiente de implementar. Cuando existan backend y frontend, validar:

| ID | Accion | Esperado |
|---|---|---|
| T-00-01 | Abrir health backend | Backend responde `UP` |
| T-00-02 | Abrir frontend | Login local-dev carga |
| T-00-03 | Abrir Swagger | OpenAPI disponible |
| T-00-04 | Login ADMINISTRADOR | Acceso a administracion |
| T-00-05 | Login EXPEDIDOR | Acceso a busqueda/certificados |

## 5. Incremento 0 - Base Documental Y Arquitectura

| ID | Accion | Esperado |
|---|---|---|
| I0-DOC-01 | Revisar `docs/CONSTITUTION.md` | Define autoridad, reglas SDD y gates |
| I0-DOC-02 | Revisar `docs/ARCHITECTURE.md` | Define coordenadas y arquitectura propia |
| I0-DOC-03 | Revisar `docs/TECNOLOGIAS.md` | Define stack canonico |
| I0-DOC-04 | Revisar `docs/ARRANQUE.md` | Explica estado y orden de trabajo |
| I0-DOC-05 | Revisar PRD vigente | Incluye reglas, estados, modelo y criterios MVP |

## 6. Incremento 1 - Modelo Base Y Carga Inicial

Fuente de especificacion: `docs/specs/2026-05-15-sed-esal-i1-spec.md`.

Estado: aprobado como incremento activo, pendiente de implementacion.

Casos previstos:

| ID | Accion | Datos | Esperado |
|---|---|---|---|
| I1-CARGA-01 | Cargar Excel historico valido | `BASE DE DATOS - REGISTRO_1.xlsx` | Importacion con resumen |
| I1-CARGA-02 | Detectar entidades efectivas | Archivo historico | Se reconocen registros con datos ESAL |
| I1-CARGA-03 | Aplicar diccionario de obligatoriedad | `Base excel.xlsx` | 23 obligatorios y 94 opcionales cargados |
| I1-CARGA-04 | Importar registro incompleto historico | Registro con `NR` | Importa con advertencia |
| I1-CARGA-05 | Detectar `ID SIPEJ` faltante o duplicado | Datos de prueba | Advertencia o bloqueo segun regla |

## 7. Incremento 1 - Estados Y Completitud

Fuente de especificacion: `docs/specs/2026-05-15-sed-esal-i1-spec.md`.

| ID | Accion | Datos | Esperado |
|---|---|---|---|
| I1-EST-01 | Marcar ESAL activa | Estado `Activo` | Certificado refleja informacion registrada |
| I1-EST-02 | Marcar ESAL suspendida | Tiempo de suspension | Semaforo exige datos de suspension |
| I1-EST-03 | Marcar ESAL en liquidacion | Acta/liquidador | Vista previa incluye `EN LIQUIDACIÓN` |
| I1-EST-04 | Marcar ESAL cancelada | Resolucion y fecha | Vista previa limita informacion segun regla |
| I1-COMP-01 | Evaluar registro completo | Campos obligatorios completos | `Listo para certificar` |
| I1-COMP-02 | Evaluar faltante opcional | Campo opcional vacio | `Incompleto no bloqueante` |
| I1-COMP-03 | Evaluar faltante obligatorio | Campo obligatorio vacio | `Incompleto bloqueante` |

## 8. Incremento 1 - Documentos Soporte

Fuente de especificacion: `docs/specs/2026-05-15-sed-esal-i1-spec.md`.

| ID | Accion | Datos | Esperado |
|---|---|---|---|
| I1-DOC-01 | Cargar PDF soporte | Archivo `.pdf` | Documento asociado |
| I1-DOC-02 | Cargar formato no permitido | Archivo no PDF | Rechazo |
| I1-DOC-03 | Finalizar constitucion nueva sin documentos | Registro nuevo | Bloqueo por documentos faltantes |
| I1-DOC-04 | Importar historico sin documentos | Registro historico | Advertencia documental, no bloqueo de importacion |

## 9. Incremento 2 - Busqueda Y Vista Previa

Fuente de especificacion: `docs/specs/2026-05-15-sed-esal-i2-spec.md`.

Estado: aprobado como incremento futuro, pendiente de implementacion.

### I2 - Busqueda

| ID | Accion | Datos | Esperado |
|---|---|---|---|
| I2-BUS-01 | Buscar por `ID SIPEJ` exacto | `ID SIPEJ` existente | Retorna la ESAL correcta |
| I2-BUS-02 | Buscar por `ID SIPEJ` parcial | Fragmento de `ID SIPEJ` | Retorna coincidencias paginadas |
| I2-BUS-03 | Buscar por nombre exacto | Nombre completo existente | Retorna la ESAL correcta |
| I2-BUS-04 | Buscar por nombre parcial | Fragmento de nombre | Retorna coincidencias paginadas |
| I2-BUS-05 | Buscar sin coincidencias | Texto inexistente | Muestra mensaje sin resultados |
| I2-BUS-06 | Filtrar por estado | `ACTIVO`, `SUSPENDIDO`, `EN_LIQUIDACION` o `CANCELADO` | Lista solo registros del estado seleccionado |
| I2-BUS-07 | Filtrar por semaforo | Completitud seleccionada | Lista registros segun completitud |

### I2 - Detalle Y Vista Previa

| ID | Accion | Datos | Esperado |
|---|---|---|---|
| I2-DET-01 | Abrir detalle desde resultado | ESAL encontrada | Muestra informacion por secciones |
| I2-DET-02 | Abrir detalle como Expedidor | Usuario `EXPEDIDOR` | No muestra controles de edicion |
| I2-PREV-01 | Abrir vista previa activa | ESAL `ACTIVO` completa | Muestra datos certificables y generacion habilitada para I3 |
| I2-PREV-02 | Abrir vista previa suspendida | ESAL `SUSPENDIDO` | Muestra alerta y tiempo de suspension |
| I2-PREV-03 | Abrir vista previa en liquidacion | ESAL `EN_LIQUIDACION` | Muestra leyenda y parrafo de liquidacion |
| I2-PREV-04 | Abrir vista previa cancelada | ESAL `CANCELADO` | Muestra solo informacion permitida y datos de cancelacion |
| I2-PREV-05 | Validar faltante obligatorio | Campo requerido vacio o `NR` | Bloquea generacion y muestra campo/seccion |
| I2-PREV-06 | Validar faltante opcional | Campo opcional vacio o `NR` | No bloquea generacion |
| I2-PREV-07 | Consultar trazas | Eventos de busqueda/preview | Auditoria registra usuario, rol, accion y resultado |

## 10. Incremento 3 - Certificado PDF

Fuente de especificacion: `docs/specs/2026-05-15-sed-esal-i3-spec.md`.

Estado: aprobado como incremento futuro, pendiente de implementacion.

### I3 - Numeracion Y Firmante

| ID | Accion | Datos | Esperado |
|---|---|---|---|
| I3-NUM-01 | Consultar configuracion inicial | Prefijo `ESAL` | Numeracion activa disponible |
| I3-NUM-02 | Generar primer certificado del anio | Anio 2026 | Numero `ESAL-2026-000001` |
| I3-NUM-03 | Generar segundo certificado del anio | Anio 2026 | Numero `ESAL-2026-000002` |
| I3-FIR-01 | Crear firmante vigente | Nombre, cargo, vigencia | Firmante queda activo |
| I3-FIR-02 | Generar sin firmante vigente | Sin firmante activo | Bloquea generacion |
| I3-FIR-03 | Crear vigencias solapadas | Dos firmantes activos misma fecha | Bloquea configuracion |

### I3 - Generacion Y Descarga

| ID | Accion | Datos | Esperado |
|---|---|---|---|
| I3-PDF-01 | Generar PDF desde preview habilitada | ESAL completa | Certificado generado con numero unico |
| I3-PDF-02 | Generar desde preview bloqueada | ESAL con faltante obligatorio | No genera PDF ni asigna numero |
| I3-PDF-03 | Generar PDF activa | ESAL `ACTIVO` | PDF refleja informacion vigente |
| I3-PDF-04 | Generar PDF suspendida | ESAL `SUSPENDIDO` | PDF incluye alerta y tiempo de suspension |
| I3-PDF-05 | Generar PDF en liquidacion | ESAL `EN_LIQUIDACION` | PDF incluye leyenda y parrafo |
| I3-PDF-06 | Generar PDF cancelada | ESAL `CANCELADO` | PDF limita informacion segun regla |
| I3-PDF-07 | Calcular hash | PDF generado | Hash SHA-256 queda registrado |
| I3-PDF-08 | Descargar certificado | Certificado generado | Descarga PDF y registra auditoria |
| I3-PDF-09 | Consultar historial por ESAL | ESAL con certificados | Lista certificados generados |
| I3-AUD-01 | Consultar trazas de expedicion | Eventos I3 | Auditoria registra generacion, bloqueo, fallo y descarga |

## 11. Incremento 4 - Seguridad Institucional

Fuente de especificacion: `docs/specs/2026-05-15-sed-esal-i4-spec.md`.

Estado: aprobado como incremento futuro, pendiente de implementacion.

### I4 - Autenticacion Y Roles

| ID | Accion | Datos | Esperado |
|---|---|---|---|
| I4-AUTH-01 | Acceder sin token en ambiente institucional | Request anonimo | Respuesta 401 |
| I4-AUTH-02 | Acceder con token expirado | JWT expirado | Respuesta 401 y evento auditable si aplica |
| I4-AUTH-03 | Acceder con audience incorrecta | JWT invalido | Respuesta 401 |
| I4-AUTH-04 | Consultar `/api/auth/me` | JWT valido | Retorna usuario, roles y permisos |
| I4-ROL-01 | Acceder como Administrador | Rol `ADMINISTRADOR` | Acceso a modulos administrativos |
| I4-ROL-02 | Acceder como Expedidor a admin | Rol `EXPEDIDOR` | Respuesta 403 |
| I4-ROL-03 | Acceder como Expedidor a busqueda | Rol `EXPEDIDOR` | Acceso permitido |

### I4 - Hardening Y Auditoria

| ID | Accion | Datos | Esperado |
|---|---|---|---|
| I4-CORS-01 | Consumir API desde origen permitido | Dominio institucional/local | Request permitido |
| I4-CORS-02 | Consumir API desde origen no permitido | Dominio no autorizado | Request rechazado por CORS |
| I4-DESC-01 | Descargar certificado sin autenticacion | Certificado existente | Respuesta 401 |
| I4-DESC-02 | Descargar certificado con rol permitido | Admin o Expedidor | Descarga y auditoria |
| I4-ERR-01 | Forzar error tecnico | Endpoint con error controlado | No expone stack trace ni rutas fisicas |
| I4-AUD-01 | Consultar evento de acceso denegado | 403 generado | Auditoria registra usuario/recurso/resultado |

## 12. Incrementos Posteriores

### I5 - Verificacion Externa Futura

- QR o codigo de verificacion.
- Pagina publica o interna de validacion si la DIV lo aprueba.
- Control de vigencia publica del certificado.
