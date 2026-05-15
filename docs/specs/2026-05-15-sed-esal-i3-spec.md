# Spec I3 - Generacion PDF, Numeracion, Firmante Y Trazabilidad

> Estado: propuesta para revision.  
> Fecha: 2026-05-15.  
> Sistema: `SED_ESAL`.  
> Metodologia: SDD Spec-Anchored.  
> PRD base: `docs/specs/2026-05-09-sed-esal-certificados-prd.md`.  
> Depende de: `docs/specs/2026-05-15-sed-esal-i1-spec.md`, `docs/specs/2026-05-15-sed-esal-i2-spec.md`.

## 1. Objetivo

Implementar la expedicion controlada del certificado de existencia y representacion legal en PDF: numeracion unica, firmante vigente configurable, generacion desde plantilla oficial, almacenamiento del PDF, calculo de hash y traza completa de generacion, descarga y errores.

I3 convierte la vista previa validada de I2 en un certificado expedido. El sistema solo debe emitir PDF cuando la validacion de I2 indique que la generacion esta habilitada.

## 2. Alcance

Incluye:

- Configuracion de numeracion con prefijo y consecutivo anual.
- Generacion de numero unico con formato `<PREFIJO>-<AAAA>-<CONSECUTIVO_6_DIGITOS>`.
- Configuracion de firmante por vigencia.
- Seleccion automatica del firmante vigente al momento de expedicion.
- Generacion de certificado PDF desde `Plantilla Certificado EYRL.docx`.
- Mapeo de datos de la vista previa hacia la plantilla.
- Reglas de contenido por estado administrativo.
- Persistencia del registro de certificado expedido.
- Almacenamiento del PDF generado por servicio abstraido.
- Calculo de hash SHA-256 del PDF.
- Registro de version de datos usada.
- Descarga de certificado.
- Trazabilidad de expedicion exitosa, bloqueo, descarga y error.

Excluye:

- Firma digital certificada.
- QR o codigo publico de verificacion.
- Consulta publica externa.
- Anulacion formal de certificados.
- Integracion Azure AD real.
- Redisenar juridicamente la plantilla.

## 3. Supuestos

- I1 provee modelo base, documentos soporte, estados y completitud.
- I2 provee preview certificable, `versionDatos` y resultado de validacion.
- El PDF se genera usando los datos vigentes al momento de expedicion.
- El numero de certificado se asigna solo si la generacion pasa validacion funcional.
- Si ocurre un error tecnico despues de reservar numero, el evento debe quedar trazado y el numero no debe reutilizarse salvo regla institucional futura de anulacion.
- La politica definitiva de almacenamiento documental esta pendiente; I3 debe implementar una abstraccion reemplazable.
- La seccion ampliada de seguridad de `docs/ARCHITECTURE.md` sigue pendiente y puede modificar controles de descarga, auditoria o cabeceras.

## 4. Actores Y Permisos

### ADMINISTRADOR

Puede:

- Configurar prefijo de numeracion.
- Consultar consecutivos.
- Crear, activar e inactivar firmantes.
- Generar certificados.
- Descargar certificados.
- Consultar auditoria de expedicion.

### EXPEDIDOR

Puede:

- Generar certificado cuando la validacion este habilitada.
- Descargar certificado generado.
- Consultar historial de certificados de una ESAL.

No puede:

- Configurar numeracion.
- Configurar firmantes.
- Editar datos de ESAL.
- Forzar generacion con bloqueos.

## 5. Modelo De Dominio

### 5.1. Certificado

Campos minimos:

- `id`
- `esalId`
- `idSipej`
- `nit`
- `numeroCertificado`
- `estadoCertificado`
- `versionDatos`
- `fechaExpedicion`
- `firmanteId`
- `firmanteNombre`
- `firmanteCargo`
- `plantillaVersion`
- `hashSha256`
- `rutaPdf`
- `nombreArchivo`
- `contentType`
- `tamanoBytes`
- `createdAt`
- `createdBy`

Estados iniciales:

- `GENERADO`
- `FALLIDO`
- `BLOQUEADO`

Reglas:

- `numeroCertificado` debe ser unico.
- Un certificado `GENERADO` no se sobrescribe.
- El PDF descargado debe corresponder al hash registrado.

### 5.2. NumeracionCertificado

Campos minimos:

- `id`
- `prefijo`
- `anio`
- `ultimoConsecutivo`
- `activo`
- `createdAt`
- `updatedAt`

Reglas:

- Valor inicial sugerido de prefijo: `ESAL`.
- Formato: `<PREFIJO>-<AAAA>-<CONSECUTIVO_6_DIGITOS>`.
- Ejemplo: `ESAL-2026-000001`.
- El consecutivo se reinicia por anio salvo decision institucional contraria.
- La asignacion debe ser transaccional para evitar duplicados por concurrencia.

### 5.3. Firmante

Campos minimos:

- `id`
- `nombre`
- `cargo`
- `dependencia`
- `fechaInicioVigencia`
- `fechaFinVigencia`
- `activo`
- `createdAt`
- `createdBy`
- `updatedAt`
- `updatedBy`

Reglas:

- Debe existir un firmante vigente y activo para generar certificado.
- No debe haber dos firmantes activos con vigencias solapadas para la misma fecha de expedicion.
- El certificado conserva copia de nombre y cargo usados, aunque luego cambie la configuracion.

### 5.4. PlantillaCertificado

Campos minimos:

- `id`
- `nombre`
- `version`
- `rutaPlantilla`
- `activo`
- `createdAt`

Reglas:

- I3 puede iniciar con una plantilla activa unica basada en `Plantilla Certificado EYRL.docx`.
- El servicio de plantilla debe aislar el mecanismo tecnico de reemplazo de variables.
- La version de plantilla usada queda registrada en cada certificado.

## 6. Flujo De Expedicion

1. Usuario abre vista previa de I2.
2. Sistema valida nuevamente datos obligatorios y reglas de estado.
3. Si hay bloqueos, registra evento `CERTIFICADO_BLOQUEADO` y no asigna numero.
4. Si no hay bloqueos, sistema obtiene firmante vigente.
5. Sistema reserva numero unico de certificado.
6. Sistema construye documento desde plantilla.
7. Sistema genera PDF.
8. Sistema calcula hash SHA-256.
9. Sistema almacena PDF.
10. Sistema registra certificado `GENERADO`.
11. Sistema registra auditoria de expedicion.
12. Sistema habilita descarga.

## 7. Reglas De Contenido Del PDF

El PDF debe usar los datos de preview de I2 y respetar reglas por estado:

`ACTIVO`:

- Refleja informacion vigente registrada.

`SUSPENDIDO`:

- Incluye alerta visible.
- Incluye tiempo de suspension.

`EN_LIQUIDACION`:

- Incluye `EN LIQUIDACION` junto al nombre.
- Incluye parrafo de disolucion/liquidacion.

`CANCELADO`:

- Limita informacion a constitucion permitida y cancelacion.
- Incluye resolucion de cancelacion y fecha.

Reglas generales:

- El PDF debe incluir numero unico.
- El PDF debe incluir fecha de expedicion.
- El PDF debe incluir firmante vigente.
- No debe incluir QR en I3.
- No debe permitir edicion posterior desde el aplicativo.

## 8. Validaciones

Bloquean generacion:

- Preview inexistente o invalido.
- `GENERACION_BLOQUEADA` desde I2.
- Falta firmante vigente.
- Vigencias de firmante solapadas.
- No existe configuracion de numeracion activa.
- Error al reservar consecutivo.
- Error al cargar plantilla activa.
- Error al mapear un campo obligatorio de plantilla.
- Error al generar o almacenar PDF.

No bloquean generacion:

- Campos opcionales faltantes.
- Advertencias historicas no bloqueantes.

Mensajes deben indicar:

- Causa.
- Seccion o configuracion afectada.
- Accion esperada para corregir.

## 9. API

Endpoints propuestos:

```text
POST /api/certificados/esales/{id}/generar
GET  /api/certificados/{certificadoId}
GET  /api/certificados/{certificadoId}/descargar
GET  /api/esales/{id}/certificados
GET  /api/admin/certificados/numeracion
PUT  /api/admin/certificados/numeracion
GET  /api/admin/firmantes
POST /api/admin/firmantes
PUT  /api/admin/firmantes/{id}
PUT  /api/admin/firmantes/{id}/activar
PUT  /api/admin/firmantes/{id}/inactivar
GET  /api/admin/auditoria?accion=CERTIFICADO
```

Reglas:

- Generacion y descarga permiten `ADMINISTRADOR` y `EXPEDIDOR`.
- Configuracion de numeracion y firmantes requiere `ADMINISTRADOR`.
- Auditoria administrativa requiere `ADMINISTRADOR`.
- Swagger debe documentar errores funcionales y estados del certificado.

DTO minimo de generacion:

- `certificadoId`
- `numeroCertificado`
- `estadoCertificado`
- `fechaExpedicion`
- `idSipej`
- `nit`
- `hashSha256`
- `versionDatos`
- `firmante`
- `downloadUrl`

## 10. UI

Rutas propuestas:

```text
/certificados/preview/:id
/certificados/:certificadoId
/admin/certificados/numeracion
/admin/firmantes
```

Pantallas:

- Vista previa con accion real de generar certificado.
- Resultado de generacion.
- Historial de certificados por ESAL.
- Configuracion de numeracion.
- Administracion de firmantes.
- Auditoria de expedicion.

UX:

- Boton generar solo habilitado si validacion permite expedicion.
- Confirmacion previa antes de generar, indicando que se asignara numero unico.
- Mostrar numero certificado y hash despues de generar.
- Descargar PDF desde pantalla de resultado e historial.
- Mensajes de error deben diferenciar bloqueo funcional de error tecnico.

## 11. Auditoria

Eventos I3:

- `CERTIFICADO_GENERACION_SOLICITADA`
- `CERTIFICADO_BLOQUEADO`
- `CERTIFICADO_GENERADO`
- `CERTIFICADO_GENERACION_FALLIDA`
- `CERTIFICADO_DESCARGADO`
- `NUMERACION_ACTUALIZADA`
- `FIRMANTE_CREADO`
- `FIRMANTE_ACTUALIZADO`
- `FIRMANTE_ACTIVADO`
- `FIRMANTE_INACTIVADO`

Campos minimos:

- Usuario.
- Rol.
- Fecha/hora.
- Accion.
- ESAL.
- `ID SIPEJ`.
- NIT.
- Numero de certificado, cuando exista.
- Version de datos usada.
- Firmante usado.
- IP/equipo si esta disponible.
- Resultado.
- Error funcional o tecnico.
- Hash del PDF, cuando aplique.

## 12. Almacenamiento Y Hash

Reglas:

- El almacenamiento debe pasar por `CertificadoStorageService` o equivalente.
- I3 puede usar filesystem local-dev configurado por propiedad.
- La ruta fisica no debe exponerse directamente al frontend.
- La descarga debe validar permisos.
- El hash SHA-256 debe calcularse sobre los bytes finales del PDF almacenado.
- Si la descarga detecta hash inconsistente, debe bloquear y registrar error.

## 13. Pruebas

Backend:

- Genera numero `ESAL-2026-000001`.
- Incrementa consecutivo anual.
- Evita duplicados por concurrencia.
- Bloquea sin firmante vigente.
- Bloquea con firmantes solapados.
- Genera PDF para ESAL activa.
- Genera PDF para ESAL suspendida con alerta.
- Genera PDF para ESAL en liquidacion con leyenda.
- Genera PDF para ESAL cancelada con contenido restringido.
- Calcula hash SHA-256.
- Registra certificado generado.
- Registra generacion fallida.
- Descarga valida permisos.
- Descarga valida hash.

Frontend:

- Configuracion de numeracion.
- CRUD funcional de firmantes.
- Generacion desde preview habilitada.
- Mensaje de bloqueo desde preview no habilitada.
- Resultado muestra numero, fecha, firmante y hash.
- Descarga disponible.
- Historial por ESAL visible.

Manual:

- Ejecutar casos I3 de `docs/GUIA_PRUEBAS_FUNCIONALES.md`.

## 14. Criterios De Aceptacion

1. Existe configuracion inicial de numeracion con prefijo `ESAL`.
2. El sistema genera numero unico con formato definido.
3. El consecutivo se incrementa por anio.
4. El sistema bloquea si no existe firmante vigente.
5. El sistema bloquea firmantes con vigencias solapadas.
6. Un Administrador puede configurar firmante vigente.
7. Un Expedidor puede generar certificado desde preview habilitada.
8. El sistema no genera certificado desde preview bloqueada.
9. El PDF se genera desde plantilla oficial.
10. El PDF incluye numero unico, fecha y firmante.
11. El PDF respeta reglas de estado.
12. El PDF queda almacenado por servicio abstraido.
13. El sistema calcula y registra hash SHA-256.
14. El certificado queda asociado a version de datos usada.
15. La descarga valida permisos.
16. La descarga registra auditoria.
17. La auditoria registra generacion exitosa, fallida, bloqueada y descarga.
18. La UI muestra resultado de generacion con numero y hash.
19. El historial de certificados por ESAL queda disponible.
20. `ARRANQUE.md`, `README.md` y `GUIA_PRUEBAS_FUNCIONALES.md` quedan actualizados para I3.

## 15. Fuera De Alcance

- Firma digital certificada.
- QR.
- Verificacion publica.
- Anulacion de certificados.
- Reexpedicion con control juridico.
- Azure AD real.

## 16. Preguntas Abiertas

1. Confirmar si el consecutivo anual es definitivo o solo regla inicial.
2. Confirmar politica de numeros consumidos cuando falla la generacion tecnica.
3. Confirmar almacenamiento definitivo de PDFs.
4. Confirmar si se conserva DOCX intermedio o solo PDF final.
5. Confirmar vigencia legal del certificado expedido.
6. Confirmar contenido exacto final para estado `CANCELADO`.
7. Confirmar si la plantilla debe mantenerse editable por negocio o versionada como plantilla tecnica.
8. Confirmar seccion ampliada de seguridad de `docs/ARCHITECTURE.md`.
