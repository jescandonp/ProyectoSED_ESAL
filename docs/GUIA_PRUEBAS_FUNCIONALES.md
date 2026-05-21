# SED_ESAL - Guia De Pruebas Funcionales

> Estado: I5 completado.
> Tests backend: 131 (BUILD SUCCESS). Frontend Angular: tests y build en verde.
> Fecha: 2026-05-21.
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

Requisito: backend levantado en `http://localhost:8080` y frontend en `http://localhost:4200`.

| ID | Accion | Endpoint / URL | Esperado |
|---|---|---|---|
| T-00-01 | Verificar health backend | `GET /actuator/health` | `{"status":"UP"}` sin autenticacion |
| T-00-02 | Abrir Swagger | `http://localhost:8080/swagger-ui.html` | OpenAPI disponible |
| T-00-03 | Abrir frontend | `http://localhost:4200` | Pantalla login carga |
| T-00-04 | Login ADMINISTRADOR | Usuario `admin@educacionbogota.edu.co` / `admin123` | Sidebar con modulos admin visibles |
| T-00-05 | Login EXPEDIDOR | Usuario `expedidor@educacionbogota.edu.co` / `expedidor123` | Sidebar solo con busqueda/consulta |
| T-00-06 | Verificar tests backend | `mvn test` en `sed-esal-backend` | 131 tests, BUILD SUCCESS |
| T-00-07 | Verificar build frontend | `npm run build` en `sed-esal-angular` | BUILD SUCCESS sin errores |

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

Estado: **implementado**. Backend: 52 tests en verde. Frontend: build y tests en verde.

### I1 - Carga

| ID | Accion | Endpoint | Datos | Esperado |
|---|---|---|---|---|
| I1-CARGA-01 | Cargar diccionario | `POST /api/admin/diccionario/inicializar` | `Base excel.xlsx` como multipart | `totalPersistidos: 117`, `totalObligatorios: 23`, `totalOpcionales: 94` |
| I1-CARGA-02 | Cargar Excel historico | `POST /api/admin/importaciones/esal` | `BASE DE DATOS - REGISTRO_1.xlsx` como multipart | `totalImportados > 0`, resumen con advertencias |
| I1-CARGA-03 | Idempotencia historico | `POST /api/admin/importaciones/esal` (segunda vez) | Mismo archivo | `totalImportados` igual a primera carga; ESALes no duplicadas |
| I1-CARGA-04 | Registro con `NR` | Ver resultado de importacion | Archivo historico con celdas `NR` | ESAL importada con advertencia bloqueante en `ID SIPEJ` |
| I1-CARGA-05 | Crear ESAL nueva | `POST /api/esales` | JSON `{"nombre": "Fundacion Test"}` con admin | 201 con id asignado |

### I1 - Administracion

| ID | Accion | Endpoint | Datos | Esperado |
|---|---|---|---|---|
| I1-ADMIN-01 | Listar ESALes | `GET /api/esales?page=0&size=10` | - | Lista paginada de ESALes |
| I1-ADMIN-02 | Filtrar por nombre | `GET /api/esales?nombre=Fund` | - | Solo ESALes con nombre que contiene `Fund` |
| I1-ADMIN-03 | Filtrar por estado | `GET /api/esales?estado=ACTIVO` | - | Solo ESALes con `estado = ACTIVO` |
| I1-ADMIN-04 | Ver detalle | `GET /api/esales/{id}` | id de ESAL existente | Detalle completo |
| I1-ADMIN-05 | Editar ESAL | `PUT /api/esales/{id}` | JSON con campos a actualizar, rol admin | 200 con datos actualizados |
| I1-ADMIN-06 | Cambiar estado | `PUT /api/esales/{id}/estado` | `{"estado": "SUSPENDIDO"}` con admin | 200, estado actualizado |
| I1-ADMIN-07 | Expedidor intenta editar | `PUT /api/esales/{id}` | Cualquier JSON con rol expedidor | 403 Forbidden |

## 7. Incremento 1 - Estados Y Completitud

Fuente de especificacion: `docs/specs/2026-05-15-sed-esal-i1-spec.md`.

Estado: **implementado**.

| ID | Accion | Endpoint | Datos | Esperado |
|---|---|---|---|---|
| I1-COMP-01 | Consultar completitud | `GET /api/esales/{id}/completitud` | ESAL completa | `estadoCompletitud: LISTO_PARA_CERTIFICAR`, 0 advertencias |
| I1-COMP-02 | Completitud bloqueante | `GET /api/esales/{id}/completitud` | ESAL con `idSipej = NR` | `estadoCompletitud: INCOMPLETO_BLOQUEANTE` |
| I1-COMP-03 | Recalcular | `POST /api/esales/{id}/completitud/recalcular` | id de ESAL, rol admin | 200, semaforo actualizado |
| I1-EST-01 | SUSPENDIDO exige datos | Cambiar estado + recalcular | ESAL sin actuacion SUSPENSION | `INCOMPLETO_BLOQUEANTE` por actuacion faltante |
| I1-EST-02 | EN_LIQUIDACION exige datos | Cambiar estado + recalcular | ESAL sin actuacion LIQUIDACION | `INCOMPLETO_BLOQUEANTE` |
| I1-EST-03 | CANCELADO exige datos | Cambiar estado + recalcular | ESAL sin actuacion CANCELACION | `INCOMPLETO_BLOQUEANTE` |

## 8. Incremento 1 - Documentos Soporte

Fuente de especificacion: `docs/specs/2026-05-15-sed-esal-i1-spec.md`.

Estado: **implementado**.

| ID | Accion | Endpoint | Datos | Esperado |
|---|---|---|---|---|
| I1-DOC-01 | Cargar PDF soporte | `POST /api/esales/{id}/documentos` | Archivo `.pdf` como multipart, rol admin | 200, documento con `estadoValidacion: PENDIENTE` |
| I1-DOC-02 | Cargar no PDF | `POST /api/esales/{id}/documentos` | Archivo `.png` o `.docx` | 400 Bad Request |
| I1-DOC-03 | Listar documentos | `GET /api/esales/{id}/documentos` | id de ESAL con documentos | Lista de documentos |
| I1-DOC-04 | Expedidor lista documentos | `GET /api/esales/{id}/documentos` | Rol expedidor | 200, lista visible |

## 8b. Incremento 1 - Auditoria

Estado: **implementado**.

| ID | Accion | Endpoint | Datos | Esperado |
|---|---|---|---|---|
| I1-AUD-01 | Consultar auditoria | `GET /api/admin/auditoria?page=0&size=20` | Rol admin | Lista de eventos con usuario, rol, accion, resultado |
| I1-AUD-02 | Verificar evento CREAR_ESAL | Crear ESAL y consultar auditoria | - | Registro con `accion: CREAR_ESAL`, `resultado: EXITO` |
| I1-AUD-03 | Verificar evento IMPORTAR_ESAL | Importar Excel y consultar auditoria | - | Registro con detalle de conteos |
| I1-AUD-04 | Verificar evento REGISTRAR_DOCUMENTO | Subir PDF y consultar auditoria | - | Registro con nombre de archivo |
| I1-AUD-05 | Verificar evento RECALCULAR_COMPLETITUD | Recalcular y consultar auditoria | - | Registro con semaforo resultante |
| I1-AUD-06 | Expedidor intenta auditoria | `GET /api/admin/auditoria` | Rol expedidor | 403 Forbidden |

## 9. Incremento 2 - Busqueda Y Vista Previa

Fuente de especificacion: `docs/specs/2026-05-15-sed-esal-i2-spec.md`.

Estado: completado. Implementado en commit feat/T10.

### I2 - Busqueda Backend

| ID | Accion | Endpoint | Datos | Esperado |
|---|---|---|---|---|
| I2-BUS-01 | Buscar sin filtros | `GET /api/busquedas/esales?page=0&size=10` | Rol admin o expedidor | Lista paginada con todos los registros |
| I2-BUS-02 | Buscar por `q` en nombre | `GET /api/busquedas/esales?q=Fund` | Texto parcial existente | Retorna ESALes cuyo nombre contiene `Fund` |
| I2-BUS-03 | Buscar por `q` en idSipej | `GET /api/busquedas/esales?q=SIP-001` | Fragmento de idSipej | Retorna ESALes con coincidencia en idSipej |
| I2-BUS-04 | Buscar por `idSipej` exacto | `GET /api/busquedas/esales?idSipej=SIP-001` | idSipej existente | Retorna la ESAL con ese idSipej |
| I2-BUS-05 | Buscar por `nit` | `GET /api/busquedas/esales?nit=900` | Fragmento de NIT | Retorna ESALes cuyo nit contiene `900` |
| I2-BUS-06 | Filtrar por `estado` | `GET /api/busquedas/esales?estado=ACTIVO` | Estado valido | Lista solo registros con `estado = ACTIVO` |
| I2-BUS-07 | Filtrar por `estadoCompletitud` | `GET /api/busquedas/esales?estadoCompletitud=LISTO_PARA_CERTIFICAR` | Semaforo valido | Lista solo registros con completitud indicada |
| I2-BUS-08 | Combinar filtros | `GET /api/busquedas/esales?estado=ACTIVO&q=Fund` | Multifiltro | Aplica AND entre filtros activos |
| I2-BUS-09 | Sin coincidencias | `GET /api/busquedas/esales?q=XYZINEXISTENTE` | Texto que no existe | `content: []`, `totalElements: 0` |
| I2-BUS-10 | Sin autenticacion | `GET /api/busquedas/esales` | Sin cabecera Authorization | 401 Unauthorized |

### I2 - Detalle Backend

| ID | Accion | Endpoint | Datos | Esperado |
|---|---|---|---|---|
| I2-DET-01 | Obtener detalle completo | `GET /api/busquedas/esales/{id}` | id de ESAL existente, rol admin | Objeto con campos ESAL, personeria, reformas, nombramientos, organos, actuaciones, documentos, completitud |
| I2-DET-02 | Detalle como Expedidor | `GET /api/busquedas/esales/{id}` | id existente, rol expedidor | 200, mismo detalle sin controles de edicion (frontend) |
| I2-DET-03 | Detalle ESAL inexistente | `GET /api/busquedas/esales/99999` | id que no existe | 404 Not Found |
| I2-DET-04 | Auditoria detalle | Obtener detalle y consultar auditoria | - | Registro `accion: DETALLE_ESAL_CONSULTADO`, `resultado: EXITO` |

### I2 - Vista Previa Backend

| ID | Accion | Endpoint | Datos | Esperado |
|---|---|---|---|---|
| I2-PREV-01 | Preview ESAL completa activa | `GET /api/certificados/preview/esales/{id}` | ESAL ACTIVO con todos los campos | `generacionHabilitada: true`, `bloqueos: []` |
| I2-PREV-02 | Preview ESAL con campos faltantes | `GET /api/certificados/preview/esales/{id}` | ESAL con obligatorios vacios | `generacionHabilitada: false`, `bloqueos` con entradas |
| I2-PREV-03 | Preview ESAL cancelada | `GET /api/certificados/preview/esales/{id}` | ESAL CANCELADO completa | `generacionHabilitada: false` independiente de completitud |
| I2-PREV-04 | Preview ESAL suspendida | `GET /api/certificados/preview/esales/{id}` | ESAL SUSPENDIDO | `alertaEstado` con texto de suspension, `generacionHabilitada` segun completitud |
| I2-PREV-05 | Preview ESAL en liquidacion | `GET /api/certificados/preview/esales/{id}` | ESAL EN_LIQUIDACION | `alertaEstado` con texto de liquidacion |
| I2-PREV-06 | Validar preview no habilitada | `POST /api/certificados/preview/esales/{id}/validar` | ESAL con bloqueos | `generacionHabilitada: false`; auditoria `ERROR_VALIDACION_PREVIEW` |
| I2-PREV-07 | Preview ESAL inexistente | `GET /api/certificados/preview/esales/99999` | id que no existe | 404 Not Found |
| I2-PREV-08 | Auditoria preview consultado | Consultar preview y revisar auditoria | - | Registro `accion: PREVIEW_CERTIFICADO_CONSULTADO` o `PREVIEW_CERTIFICADO_BLOQUEADO` |
| I2-PREV-09 | Sin autenticacion preview | `GET /api/certificados/preview/esales/{id}` | Sin cabecera Authorization | 401 Unauthorized |

### I2 - UI Angular

| ID | Accion | URL | Datos | Esperado |
|---|---|---|---|---|
| I2-UI-01 | Abrir buscador | `http://localhost:4200/busqueda` | Rol admin o expedidor | Formulario con filtros q, idSipej, nit, estado, completitud |
| I2-UI-02 | Buscar y ver resultados | `/busqueda` con filtro activo | Texto existente | Tabla de resultados con columnas nombre, idSipej, nit, estado, semaforo |
| I2-UI-03 | Navegar a detalle | Clic en "Detalle" desde resultados | ESAL encontrada | Carga `/busqueda/:id` con 8 pestanas |
| I2-UI-04 | Ver detalle como Expedidor | `/busqueda/:id` con rol expedidor | ESAL existente | Sin controles de edicion ni acciones admin |
| I2-UI-05 | Navegar a preview | Clic en "Preview" desde resultados o detalle | ESAL encontrada | Carga `/certificados/preview/:id` |
| I2-UI-06 | Preview habilitada | `/certificados/preview/:id` | ESAL completa activa | Badge verde "Generacion habilitada", sin bloqueos |
| I2-UI-07 | Preview bloqueada | `/certificados/preview/:id` | ESAL con faltantes obligatorios | Badge rojo, tabla de bloqueos con campos/secciones |
| I2-UI-08 | Alerta de estado | `/certificados/preview/:id` | ESAL SUSPENDIDO o EN_LIQUIDACION | Banner de alerta con texto legal del estado |
| I2-UI-09 | Nav "Buscar ESAL" visible | Shell con rol expedidor | Usuario expedidor autenticado | Item "Buscar ESAL" aparece en navegacion lateral |

## 10. Incremento 3 - Certificado PDF

Fuente de especificacion: `docs/specs/2026-05-15-sed-esal-i3-spec.md`.

Estado: completado. Implementado en commit feat/T12.

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

Estado: completado. Implementado en commit feat/T14. Pendiente activacion weblogic con datos TI SED.

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

## 12. Incremento 5 - CRUD Y Mantenimiento Operativo De ESAL

Fuente de especificacion: `docs/specs/2026-05-21-sed-esal-i5-spec.md`.

Estado: completado. Implementado en commits I5 hasta `feat: align SED ESAL I5 preview with current data`.

### I5 - API Administrativa

| ID | Accion | Endpoint | Datos | Esperado |
|---|---|---|---|---|
| I5-API-01 | Crear ESAL desde mantenimiento | `POST /api/esales/mantenimiento` | Nombre obligatorio y datos basicos con rol admin | ESAL creada, completitud recalculada y auditoria registrada |
| I5-API-02 | Consultar vista mantenimiento | `GET /api/esales/{id}/mantenimiento` | ESAL existente con rol admin | Respuesta con informacion principal, personeria, representantes, organo y estado |
| I5-API-03 | Actualizar informacion principal | `PUT /api/esales/{id}/informacion-principal` | Nombre, NIT, domicilio, correo, objeto social | Datos guardados sin cambiar a `CANCELADO` por edicion libre |
| I5-API-04 | Actualizar personeria juridica | `PUT /api/esales/{id}/personeria-juridica` | Reconocimiento, fecha, entidad, inscripcion | Crea o actualiza un unico registro 1:1 por ESAL |
| I5-API-05 | Crear representante legal | `POST /api/esales/{id}/representantes` | Tipo `REPRESENTANTE_LEGAL`, nombre, documento, vigente | Representante creado y visible en listado |
| I5-API-06 | Editar representante legal | `PUT /api/esales/{id}/representantes/{representanteId}` | Cambio de datos o vigencia | Registro actualizado, sin eliminacion fisica |
| I5-API-07 | Crear miembro organo administracion | `POST /api/esales/{id}/organo-administracion` | Organo, miembro, cargo, documento | Miembro creado y visible en listado |
| I5-API-08 | Editar miembro organo administracion | `PUT /api/esales/{id}/organo-administracion/{miembroId}` | Cambio de cargo o facultades | Registro actualizado |
| I5-API-09 | Rechazar mutacion como expedidor | Cualquier endpoint `POST`/`PUT` I5 | Rol `EXPEDIDOR` | 403 Forbidden |

### I5 - Cancelacion Y Reactivacion

| ID | Accion | Endpoint | Datos | Esperado |
|---|---|---|---|---|
| I5-CAN-01 | Cancelar sin resolucion | `POST /api/esales/{id}/cancelacion` | Fecha y motivo sin resolucion | 400 Bad Request |
| I5-CAN-02 | Cancelar sin fecha | `POST /api/esales/{id}/cancelacion` | Resolucion y motivo sin fecha | 400 Bad Request |
| I5-CAN-03 | Cancelar sin motivo | `POST /api/esales/{id}/cancelacion` | Resolucion y fecha sin motivo | 400 Bad Request |
| I5-CAN-04 | Cancelar con datos completos | `POST /api/esales/{id}/cancelacion` | Resolucion, fecha de cancelacion y motivo | Estado `CANCELADO`, actuacion `CANCELACION`, auditoria y completitud recalculada |
| I5-CAN-05 | Cancelar sin PDF soporte | `POST /api/esales/{id}/cancelacion` | Datos completos sin documento soporte | Guarda cancelacion y deja advertencia `PDF SOPORTE CANCELACION` |
| I5-CAN-06 | UI bloquea edicion ordinaria | `/admin/esales/{id}/mantenimiento` | ESAL en estado `CANCELADO` | Formularios ordinarios bloqueados; accion de reactivacion visible |
| I5-REA-01 | Reactivar sin motivo | `POST /api/esales/{id}/reactivacion` | Motivo vacio | 400 Bad Request |
| I5-REA-02 | Reactivar con motivo | `POST /api/esales/{id}/reactivacion` | Motivo y estado destino permitido | Estado actualizado, actuacion de cancelacion preservada y auditoria registrada |

### I5 - UI Administrativa

| ID | Accion | URL | Datos | Esperado |
|---|---|---|---|---|
| I5-UI-01 | Navegar a mantenimiento desde detalle | `/busqueda/:id` | Rol `ADMINISTRADOR` | Accion `Actualizar informacion` lleva a `/admin/esales/:id/mantenimiento` |
| I5-UI-02 | Guardar informacion principal | `/admin/esales/:id/mantenimiento` | Modificar campos principales | Seccion guarda independiente y muestra resultado |
| I5-UI-03 | Guardar personeria juridica | `/admin/esales/:id/mantenimiento` | Modificar reconocimiento o inscripcion | Seccion guarda independiente y refresca vista |
| I5-UI-04 | Agregar representante | `/admin/esales/:id/mantenimiento` | Nombre, documento, tipo y vigencia | Lista de representantes se actualiza |
| I5-UI-05 | Agregar miembro de organo | `/admin/esales/:id/mantenimiento` | Organo, miembro, cargo | Lista de organo se actualiza |
| I5-UI-06 | Registrar cancelacion | `/admin/esales/:id/mantenimiento` | Resolucion, fecha y motivo | Estado cambia a `CANCELADO` y se bloquea edicion ordinaria |
| I5-UI-07 | Reactivar ESAL cancelada | `/admin/esales/:id/mantenimiento` | Motivo de reactivacion | Estado vuelve a `ACTIVO` u otro destino permitido |
| I5-UI-08 | Ver controles como expedidor | `/busqueda/:id` o mantenimiento directo | Rol `EXPEDIDOR` | Sin accion administrativa; acceso o mutacion denegada segun ruta |

### I5 - Preview Y Certificados

| ID | Accion | Endpoint / URL | Datos | Esperado |
|---|---|---|---|---|
| I5-PREV-01 | Preview posterior a cambio de representante | `GET /api/certificados/preview/esales/{id}` | Representante historico no vigente y nuevo vigente | Preview muestra el representante vigente |
| I5-PREV-02 | Preview con datos historicos sin vigencia | `GET /api/certificados/preview/esales/{id}` | Registros legacy sin bandera vigente | Preview conserva fallback y no queda vacio por migracion parcial |
| I5-CERT-01 | Generar certificado antes de editar ESAL | `POST /api/certificados/esales/{id}` | ESAL completa | Certificado registra `versionDatos` inicial |
| I5-CERT-02 | Editar ESAL despues de generar certificado | Endpoints I5 + consulta certificado historico | Cambiar nombre u otro dato base | Certificado historico conserva `versionDatos` y contenido historico |
| I5-CERT-03 | Generar nuevo certificado despues de editar | `POST /api/certificados/esales/{id}` | ESAL editada y certificable | Nuevo certificado usa version vigente de datos |

## 13. Incrementos Posteriores

### Verificacion Externa Futura

- QR o codigo de verificacion.
- Pagina publica o interna de validacion si la DIV lo aprueba.
- Control de vigencia publica del certificado.
