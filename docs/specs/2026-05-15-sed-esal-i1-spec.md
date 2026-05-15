# Spec I1 - Modelo Base, Carga Inicial, Estados Y Completitud

> Estado: propuesta para revision.  
> Fecha: 2026-05-15.  
> Sistema: `SED_ESAL`.  
> Metodologia: SDD Spec-Anchored.  
> PRD base: `docs/specs/2026-05-09-sed-esal-certificados-prd.md`.  
> Documentos rectores: `docs/CONSTITUTION.md`, `docs/ARCHITECTURE.md`, `docs/TECNOLOGIAS.md`.

## 1. Objetivo

Implementar la base funcional de `SED_ESAL` para administrar el modelo inicial de ESAL, cargar la base historica desde Excel, registrar obligatoriedad de campos, manejar estados administrativos y calcular el semaforo de completitud para expedicion.

I1 no genera certificados PDF finales. I1 deja preparada la informacion y las reglas necesarias para que I2/I3 puedan buscar, previsualizar y generar certificados con trazabilidad.

## 2. Alcance

Incluye:

- Bootstrap tecnico del backend `sed-esal-backend`.
- Bootstrap tecnico del frontend `sed-esal-angular`.
- Modelo de datos base Oracle.
- Usuarios locales de desarrollo y roles `ADMINISTRADOR` / `EXPEDIDOR`.
- Carga inicial desde `BASE DE DATOS - REGISTRO_1.xlsx`.
- Registro del diccionario de obligatoriedad desde `Base excel.xlsx`.
- Modelo por secciones y relaciones, no tabla plana.
- Estados ESAL: `ACTIVO`, `SUSPENDIDO`, `EN_LIQUIDACION`, `CANCELADO`.
- Semaforo de completitud: `LISTO_PARA_CERTIFICAR`, `INCOMPLETO_NO_BLOQUEANTE`, `INCOMPLETO_BLOQUEANTE`.
- Registro inicial de documentos soporte en PDF.
- Auditoria basica de carga, creacion, edicion y consulta.
- Pantallas administrativas iniciales para carga, listado, detalle y completitud.

Excluye:

- Generacion PDF final.
- Numero unico de certificado.
- Firmante configurable.
- Vista previa final del certificado.
- Integracion real Azure AD / Office 365.
- QR o codigo de verificacion.
- Firma digital certificada.
- Consulta publica externa.

## 3. Supuestos

- Las coordenadas tecnicas propuestas se mantienen mientras no haya decision contraria: esquema `SED_ESAL`, prefijo `ESAL_`, paquete `co.gov.bogota.sed.esal`, contexto `/sed-esal`.
- La seccion ampliada de seguridad en `docs/ARCHITECTURE.md` esta pendiente de confirmacion. I1 implementa seguridad minima local-dev y debe quedar preparada para actualizarse cuando esa seccion sea aprobada.
- La carga historica puede importar registros incompletos y registrar advertencias.
- Los registros nuevos creados por formulario deben validar obligatoriedad y reglas documentales que correspondan al flujo implementado.
- El almacenamiento documental definitivo esta pendiente; para I1 se permite una estrategia local-dev controlada y abstraida por servicio.

## 4. Actores Y Roles

### ADMINISTRADOR

Puede:

- Cargar archivos base.
- Consultar resumen de importacion.
- Crear ESAL.
- Editar ESAL.
- Cambiar estado de ESAL.
- Registrar documentos soporte.
- Consultar semaforo de completitud.
- Consultar auditoria basica.

### EXPEDIDOR

Puede:

- Consultar/listar ESAL.
- Ver detalle de ESAL.
- Consultar semaforo de completitud.

No puede:

- Cargar Excel.
- Crear o editar ESAL.
- Cambiar estado.
- Registrar documentos soporte.

## 5. Modelo De Dominio

### 5.1. ESAL

Entidad raiz.

Campos minimos:

- `id`
- `nombre`
- `idSipej`
- `nit`
- `domicilio`
- `correoElectronico`
- `terminoDuracion`
- `objetoSocial`
- `estado`
- `estadoCompletitud`
- `createdAt`
- `createdBy`
- `updatedAt`
- `updatedBy`

Reglas:

- `idSipej` es el identificador funcional principal.
- `idSipej` debe ser unico cuando sea informado.
- La carga historica puede registrar un valor faltante o `NR`, pero debe marcar advertencia bloqueante.
- `nombre` es obligatorio.

### 5.2. PersoneriaJuridica

Campos minimos:

- `id`
- `esalId`
- `reconocimientoPersoneriaJuridica`
- `fechaReconocimientoPersoneriaJuridica`
- `entidadQueExpide`
- `inscripcion`
- `fechaInscripcion`
- `entidadQueInscribio`

### 5.3. ReformaEstatutaria

Lista dinamica 1:N.

Campos minimos:

- `id`
- `esalId`
- `orden`
- `tipoActo`
- `numeroActo`
- `fechaActo`
- `entidadQueExpide`
- `descripcion`

Regla:

- No se limita a ocho reformas. La importacion transforma columnas repetidas del Excel en filas de esta entidad cuando exista informacion.

### 5.4. Nombramiento

Campos minimos:

- `id`
- `esalId`
- `tipoNombramiento`
- `nombre`
- `tipoDocumento`
- `numeroDocumento`
- `cargo`
- `actaAprueba`
- `fechaActa`
- `tarjetaProfesional`
- `facultadesLimitaciones`
- `vigente`

Tipos iniciales:

- `REPRESENTANTE_LEGAL`
- `REPRESENTANTE_LEGAL_SUPLENTE`
- `REVISOR_FISCAL_PRINCIPAL`
- `REVISOR_FISCAL_SUPLENTE`
- `TESORERO`
- `DIGNATARIO`

### 5.5. OrganoAdministracion

Campos minimos:

- `id`
- `esalId`
- `organo`
- `miembro`
- `cargo`
- `tipoDocumento`
- `numeroDocumento`
- `actaAprueba`
- `fechaActa`
- `actaAclaratoria`
- `fechaActaAclaratoria`
- `facultadesLimitaciones`

### 5.6. ActuacionAdministrativa

Campos minimos:

- `id`
- `esalId`
- `tipoActuacion`
- `acta`
- `fechaActa`
- `resolucion`
- `fechaResolucion`
- `motivo`
- `tiempoSuspension`
- `fechaInicio`
- `fechaFin`

Tipos iniciales:

- `SUSPENSION`
- `LIQUIDACION`
- `CANCELACION`

### 5.7. DocumentoSoporte

Campos minimos:

- `id`
- `esalId`
- `tipoProceso`
- `tipoDocumento`
- `nombreArchivo`
- `contentType`
- `tamanoBytes`
- `rutaAlmacenamiento`
- `estadoValidacion`
- `createdAt`
- `createdBy`

Reglas:

- Solo se acepta `application/pdf`.
- En I1 el almacenamiento puede ser local-dev, pero el acceso debe pasar por un servicio abstraido.
- La carga historica registra brechas documentales sin bloquear importacion.

### 5.8. CampoObligatoriedad

Representa el diccionario `Base excel.xlsx`.

Campos minimos:

- `id`
- `nombreCampo`
- `seccion`
- `contexto`
- `obligatorio`
- `nota`
- `orden`

Reglas:

- Se deben cargar 117 definiciones.
- Deben identificarse 23 obligatorias y 94 opcionales.
- Los nombres duplicados deben resolverse por contexto, no por nombre global.

### 5.9. AdvertenciaCompletitud

Campos minimos:

- `id`
- `esalId`
- `seccion`
- `campo`
- `tipo`
- `bloqueante`
- `mensaje`
- `createdAt`

Tipos iniciales:

- `CAMPO_OBLIGATORIO_FALTANTE`
- `DOCUMENTO_REQUERIDO_FALTANTE`
- `DATO_INCONSISTENTE`
- `ADVERTENCIA_HISTORICA`

### 5.10. Auditoria

Campos minimos:

- `id`
- `usuario`
- `rol`
- `accion`
- `entidad`
- `entidadId`
- `idSipej`
- `resultado`
- `detalle`
- `createdAt`

## 6. Reglas De Importacion

### 6.1. Fuente Historica

Archivo: `Documentos_Referencia/BASE DE DATOS - REGISTRO_1.xlsx`.

Reglas:

- Leer hoja `Hoja1`.
- Fila 1 contiene secciones.
- Fila 2 contiene encabezados.
- Filas desde la 3 contienen datos.
- Solo se importan filas que tengan `NOMBRE` o `ID SIPEJ`.
- Valores `NR`, vacios, `N/A` o equivalentes se consideran faltantes para validacion.
- La importacion debe registrar resumen: leidos, importados, rechazados, advertencias.
- La importacion debe ser trazable por usuario y fecha.

### 6.2. Diccionario De Campos

Archivo: `Documentos_Referencia/Base excel.xlsx`.

Reglas:

- Leer nombre de campo, obligatoriedad y nota.
- Normalizar `OBLIGACIÓN` como obligatorio.
- Normalizar `OPCIONAL` como opcional.
- Registrar notas relevantes, incluyendo reformas indefinidas y bloques de cancelado/suspendido.

### 6.3. Reglas De Negocio

Archivo: `Documentos_Referencia/REGLAS (1).xlsx`.

Reglas:

- I1 debe incorporar estados y documentos soporte como reglas de completitud.
- La aplicacion debe poder evolucionar estas reglas sin reescribir el modelo plano.

## 7. Reglas De Completitud

El calculo de completitud combina:

- Diccionario de obligatoriedad.
- Reglas minimas por estado.
- Documentos soporte requeridos para registros nuevos.
- Estado historico importado.

Estados:

- `LISTO_PARA_CERTIFICAR`: no hay advertencias bloqueantes.
- `INCOMPLETO_NO_BLOQUEANTE`: hay faltantes no obligatorios o brechas historicas no bloqueantes.
- `INCOMPLETO_BLOQUEANTE`: faltan campos obligatorios o documentos requeridos.

Reglas iniciales:

- `NOMBRE`, `ID SIPEJ`, `DOMICILIO`, `CORREO ELECTRONICO`, `TERMINO DE DURACION`, `OBJETO SOCIAL`, reconocimiento de personeria, fecha, entidad que expide, representante legal, documento, acta, fecha, facultades y organo de administracion se validan segun diccionario.
- Estado `SUSPENDIDO` exige datos de suspension, incluido tiempo de suspension.
- Estado `EN_LIQUIDACION` exige datos de liquidacion suficientes para la leyenda y parrafo posterior.
- Estado `CANCELADO` exige resolucion de cancelacion y fecha.
- La carga historica puede dejar datos faltantes como advertencias; el sistema debe mostrar si bloquean o no la futura certificacion.

## 8. API Inicial

Endpoints propuestos:

```text
POST /api/admin/importaciones/esal
GET  /api/admin/importaciones/{id}
GET  /api/esales
POST /api/esales
GET  /api/esales/{id}
PUT  /api/esales/{id}
PUT  /api/esales/{id}/estado
GET  /api/esales/{id}/completitud
POST /api/esales/{id}/documentos
GET  /api/esales/{id}/documentos
GET  /api/admin/auditoria
```

Reglas:

- Endpoints administrativos requieren rol `ADMINISTRADOR`.
- Consultas de ESAL y completitud permiten `ADMINISTRADOR` y `EXPEDIDOR`.
- Listados deben ser paginados.
- Swagger debe documentar DTOs y codigos de error.

## 9. UI Inicial

Rutas propuestas:

```text
/login
/dashboard
/admin/carga-inicial
/admin/esales
/admin/esales/:id
/admin/auditoria
/esales
/esales/:id
```

Pantallas I1:

- Login local-dev.
- Dashboard basico por rol.
- Carga inicial Excel.
- Listado paginado de ESAL.
- Detalle ESAL por secciones.
- Panel de completitud.
- Registro de documentos soporte.
- Auditoria basica.

UX:

- Seguir `docs/DESIGN.md`.
- Usar tablas densas y filtros.
- Usar chips para estados y semaforo.
- Formularios por tabs o acordeones.
- Mensajes de validacion deben indicar campo/seccion/regla.

## 10. Seguridad I1

I1 implementa seguridad minima:

- HTTP Basic en `local-dev`.
- Usuarios de prueba:
  - `admin@educacionbogota.edu.co` / `admin123` / `ADMINISTRADOR`
  - `expedidor@educacionbogota.edu.co` / `expedidor123` / `EXPEDIDOR`
- Validacion de permisos en backend.
- CORS local para frontend.

Pendiente:

- La seccion ampliada de seguridad en `docs/ARCHITECTURE.md` sera actualizada cuando sea confirmada. Si esa seccion cambia roles, autenticacion, autorizacion, headers, CORS, auditoria o politicas de archivos, esta spec debe revisarse antes de implementar.

## 11. Pruebas

Backend:

- Importacion de diccionario.
- Importacion de base historica.
- Transformacion de reformas a lista dinamica.
- Validacion de `ID SIPEJ` unico.
- Calculo de completitud.
- Reglas por estado.
- Validacion PDF para documentos soporte.
- Seguridad por rol.

Frontend:

- Login local-dev.
- Render de dashboard por rol.
- Carga inicial muestra resumen.
- Listado de ESAL paginado.
- Detalle muestra secciones.
- Semaforo de completitud visible.
- Expedidor no ve acciones de edicion.

Manual:

- Ejecutar casos I1 de `docs/GUIA_PRUEBAS_FUNCIONALES.md`.

## 12. Criterios De Aceptacion

1. El backend y frontend base existen con nombres definidos.
2. La aplicacion arranca en `local-dev`.
3. Swagger esta disponible.
4. Health backend responde `UP`.
5. Usuarios locales pueden autenticarse.
6. `ADMINISTRADOR` puede cargar Excel historico.
7. `ADMINISTRADOR` puede cargar o inicializar diccionario de obligatoriedad.
8. La importacion reconoce entidades efectivas desde la base historica.
9. El sistema registra resumen de importacion.
10. El modelo no almacena reformas solo como columnas fijas.
11. El detalle ESAL muestra informacion por secciones.
12. El sistema calcula semaforo de completitud.
13. El sistema registra advertencias bloqueantes y no bloqueantes.
14. Estados `ACTIVO`, `SUSPENDIDO`, `EN_LIQUIDACION`, `CANCELADO` existen.
15. El estado afecta reglas de completitud.
16. El sistema acepta solo PDF como documento soporte.
17. `EXPEDIDOR` puede consultar ESAL y completitud.
18. `EXPEDIDOR` no puede editar ni cargar archivos.
19. Auditoria registra carga, creacion, edicion, cambio de estado y consulta relevante.
20. La guia de arranque y pruebas queda actualizada para I1.

## 13. Fuera De Alcance

- Generacion de certificado PDF.
- Numeracion de certificado.
- Firmante configurable.
- Vista previa oficial del certificado.
- Integracion Azure AD real.
- Firma digital.
- QR.
- Consulta publica.

## 14. Preguntas Abiertas

1. Confirmar almacenamiento documental/PDF definitivo.
2. Confirmar si carga historica se ejecutara desde archivo local, upload web o ambos.
3. Confirmar si registros historicos incompletos deben quedar bloqueados para certificacion hasta saneamiento.
4. Confirmar textos exactos de mensajes de completitud.
5. Confirmar seccion ampliada de seguridad de `docs/ARCHITECTURE.md`.

