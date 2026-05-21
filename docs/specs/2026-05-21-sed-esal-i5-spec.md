# Spec I5 - CRUD Y Mantenimiento Operativo De ESAL

> Estado: aprobado para diseno.
> Fecha: 2026-05-21.
> Sistema: `SED_ESAL`.
> Metodologia: SDD Spec-Anchored.
> PRD base: `docs/specs/2026-05-09-sed-esal-certificados-prd.md`.
> Depende de: `docs/specs/2026-05-15-sed-esal-i1-spec.md`, `docs/specs/2026-05-15-sed-esal-i2-spec.md`, `docs/specs/2026-05-15-sed-esal-i3-spec.md`, `docs/specs/2026-05-15-sed-esal-i4-spec.md`.

## 1. Objetivo

Convertir `SED_ESAL` en la herramienta operativa de creacion y mantenimiento de ESAL desde el aplicativo, reduciendo la dependencia de la carga Excel a escenarios de carga inicial o carga esporadica.

I5 habilita al rol `ADMINISTRADOR` para crear y editar informacion vigente de una ESAL por secciones, con guardado independiente, auditoria, recalculo de completitud y reglas formales para registrar cancelaciones.

## 2. Alcance

Incluye:

- Creacion manual de ESAL desde el aplicativo.
- Edicion de informacion principal.
- Edicion de constitucion y personeria juridica.
- Administracion de representantes legales principales y suplentes.
- Administracion del organo de administracion.
- Cambio de estado a `CANCELADO`.
- Registro formal de cancelacion mediante actuacion administrativa.
- Reactivacion o cambio desde `CANCELADO` solo por `ADMINISTRADOR` con motivo obligatorio.
- Guardado por secciones con validaciones locales.
- Recalculo de completitud despues de cada guardado.
- Advertencias cuando falte soporte documental PDF de cancelacion.
- Auditoria de cambios por seccion.
- UI administrativa para mantenimiento operativo.

Excluye:

- CRUD completo de todas las secciones del PRD.
- Motor dinamico por metadatos para las 111 columnas historicas.
- Anulacion o regeneracion de certificados historicos.
- Flujo formal de aprobacion previa de cambios.
- Versionamiento funcional visible de todos los historicos de datos.
- Edicion masiva.
- Integracion documental avanzada distinta a documentos soporte ya previstos.

## 3. Supuestos

- La carga Excel ya existe como mecanismo inicial o esporadico, pero no debe ser el mecanismo normal de mantenimiento.
- `ADMINISTRADOR` es el unico rol autorizado para crear, editar, cancelar o reactivar ESAL.
- `EXPEDIDOR` conserva consulta, preview y generacion de certificados segun permisos vigentes, pero no modifica datos.
- La generacion de certificados sigue usando la compuerta estricta de completitud definida en I2/I3.
- Los certificados expedidos son historicos inmutables: no se actualizan cuando cambian datos base de la ESAL.
- El modelo actual contiene entidades base suficientes para I5: `Esal`, `PersoneriaJuridica`, `Nombramiento`, `OrganoAdministracion` y `ActuacionAdministrativa`.
- La cancelacion se modela como `ActuacionAdministrativa` de tipo `CANCELACION`; no se duplican campos de cancelacion en `ESAL_ESAL`.

## 4. Actores Y Permisos

### ADMINISTRADOR

Puede:

- Crear ESAL.
- Editar informacion principal.
- Editar personeria juridica.
- Crear, editar y marcar vigencia de representantes legales.
- Crear, editar e inactivar miembros de organo de administracion.
- Cambiar estado a `CANCELADO`.
- Registrar datos formales de cancelacion.
- Reactivar o cambiar estado desde `CANCELADO` con motivo obligatorio.
- Consultar advertencias de completitud.
- Consultar auditoria.

### EXPEDIDOR

Puede:

- Buscar ESAL.
- Consultar detalle.
- Consultar preview.
- Generar certificado si esta habilitado.
- Descargar certificados permitidos.

No puede:

- Crear ESAL.
- Editar informacion de ESAL.
- Cambiar estados.
- Registrar cancelacion.
- Reactivar ESAL canceladas.

## 5. Secciones De Mantenimiento

### 5.1. Informacion Principal

Campos iniciales:

- `nombre`
- `idSipej`
- `nit`
- `domicilio`
- `correoElectronico`
- `terminoDuracion`
- `objetoSocial`
- `estado`

Reglas:

- `nombre` es obligatorio para crear una ESAL.
- `idSipej`, cuando exista, debe conservar unicidad.
- El guardado permite datos incompletos no criticos.
- Despues de guardar se recalcula `estadoCompletitud`.
- Cambios de estado a `CANCELADO` no se hacen como edicion libre del campo; pasan por el flujo de cancelacion.

### 5.2. Constitucion Y Personeria Juridica

Entidad base: `PersoneriaJuridica`.

Campos iniciales:

- `reconocimientoPersoneriaJuridica`
- `fechaReconocimientoPersoneriaJuridica`
- `entidadQueExpide`
- `inscripcion`
- `fechaInscripcion`
- `entidadQueInscribio`

Reglas:

- Funciona como dato vigente 1:1 por ESAL.
- Si no existe registro para la ESAL, el guardado de la seccion debe crearlo.
- Si existe, debe actualizarlo sin crear duplicados.
- Campos faltantes pueden guardarse y deben reflejarse en completitud/advertencias si afectan certificacion.

### 5.3. Representante Legal

Entidad base: `Nombramiento`.

Tipos incluidos:

- `REPRESENTANTE_LEGAL`
- `REPRESENTANTE_LEGAL_SUPLENTE`

Campos iniciales:

- `tipoNombramiento`
- `nombre`
- `tipoDocumento`
- `numeroDocumento`
- `cargo`
- `actaAprueba`
- `fechaActa`
- `facultadesLimitaciones`
- `vigente`

Reglas:

- Funciona como lista por ESAL.
- I5 no elimina fisicamente nombramientos; permite editar y marcar `vigente`.
- Debe poder existir mas de un registro historico, pero el preview/certificado debe usar los vigentes segun reglas existentes.
- El sistema debe auditar cambios de vigencia.

### 5.4. Organo De Administracion

Entidad base: `OrganoAdministracion`.

Campos iniciales:

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

Reglas:

- Funciona como lista por ESAL.
- I5 debe permitir agregar y editar miembros.
- Si el modelo tecnico no tiene campo de vigencia/inactivacion, I5 debe resolverlo antes de implementar borrado logico o declarar que la inactivacion queda diferida.
- El certificado debe seguir usando las reglas de preview/generacion vigentes.

### 5.5. Cancelacion

Entidad base: `ActuacionAdministrativa`.

Tipo:

- `CANCELACION`

Campos obligatorios para cancelar:

- `resolucion`
- `fechaResolucion` como fecha de cancelacion.
- `motivo`

Reglas:

- Cancelar una ESAL crea una actuacion administrativa `CANCELACION` y cambia `Esal.estado` a `CANCELADO`.
- La ausencia de PDF soporte no impide cancelar, pero genera advertencia visible.
- Si existe soporte PDF, debe asociarse mediante el modulo de documentos soporte.
- El certificado de una ESAL cancelada debe seguir las reglas I2/I3: informacion limitada a constitucion permitida y cancelacion.
- Reactivar o cambiar desde `CANCELADO` exige motivo obligatorio y auditoria.
- La reactivacion no elimina la actuacion de cancelacion; deja traza historica.

## 6. API

Contratos sugeridos:

- `POST /api/esales`
- `GET /api/esales/{id}/mantenimiento`
- `PUT /api/esales/{id}/informacion-principal`
- `PUT /api/esales/{id}/personeria-juridica`
- `GET /api/esales/{id}/representantes`
- `POST /api/esales/{id}/representantes`
- `PUT /api/esales/{id}/representantes/{representanteId}`
- `GET /api/esales/{id}/organo-administracion`
- `POST /api/esales/{id}/organo-administracion`
- `PUT /api/esales/{id}/organo-administracion/{miembroId}`
- `POST /api/esales/{id}/cancelacion`
- `POST /api/esales/{id}/reactivacion`

DTOs sugeridos:

- `EsalInformacionPrincipalDto`
- `PersoneriaJuridicaDto`
- `NombramientoDto`
- `OrganoAdministracionDto`
- `CancelacionEsalDto`
- `ReactivacionEsalDto`
- `MantenimientoEsalDto`

Reglas API:

- Endpoints administrativos requieren `ADMINISTRADOR`.
- Endpoints deben devolver errores `400` para validaciones funcionales.
- Endpoints deben devolver `403` si `EXPEDIDOR` intenta modificar datos.
- Despues de cada mutacion debe devolverse el estado actualizado de completitud o una referencia suficiente para refrescarlo.

## 7. UI

Ruta sugerida:

- `/admin/esales/:id/mantenimiento`

Reglas:

- Desde busqueda/detalle, `ADMINISTRADOR` ve accion `Actualizar informacion`.
- La pantalla se organiza por secciones: Informacion principal, Constitucion y personeria, Representantes legales, Organo de administracion, Estado y actuaciones.
- Cada seccion tiene guardado independiente.
- Cada guardado muestra resultado, advertencias y ultima actualizacion.
- Las listas permiten agregar y editar registros.
- Para I5 se prefiere marcar vigencia/inactivacion antes que eliminar fisicamente.
- Si la ESAL esta `CANCELADO`, la UI bloquea edicion ordinaria salvo accion explicita de reactivacion/cambio de estado por `ADMINISTRADOR`.

## 8. Validaciones

Reglas generales:

- El guardado es flexible.
- Se validan tipos, longitudes y consistencia minima.
- Se permite guardar datos incompletos para saneamiento gradual.
- La certificacion mantiene validacion estricta en preview/generacion.
- `nombre` es obligatorio al crear.
- `idSipej` debe ser unico cuando se informe.
- Cancelacion exige resolucion, fecha y motivo.
- Cancelacion sin PDF soporte genera advertencia de completitud/documento faltante.

## 9. Auditoria

Eventos minimos:

- `ESAL_CREADA`
- `ESAL_INFORMACION_PRINCIPAL_ACTUALIZADA`
- `ESAL_PERSONERIA_ACTUALIZADA`
- `ESAL_REPRESENTANTE_CREADO`
- `ESAL_REPRESENTANTE_ACTUALIZADO`
- `ESAL_REPRESENTANTE_VIGENCIA_CAMBIADA`
- `ESAL_ORGANO_MIEMBRO_CREADO`
- `ESAL_ORGANO_MIEMBRO_ACTUALIZADO`
- `ESAL_CANCELADA`
- `ESAL_REACTIVADA`

Cada evento debe registrar:

- Usuario.
- Rol.
- ESAL.
- Seccion afectada.
- Fecha/hora.
- Accion.
- Resultado.
- Valores relevantes anteriores y nuevos cuando sea viable sin exponer informacion sensible innecesaria.

## 10. Relacion Con Certificados

Reglas:

- Certificados historicos ya expedidos no se modifican.
- Cambios de I5 afectan nuevas consultas, previews y certificados futuros.
- El `versionDatos` de certificados ya generados conserva su valor historico.
- Si se requiere marcar certificados anteriores como desactualizados, debe definirse en un incremento posterior.

## 11. Criterios De Aceptacion

1. `ADMINISTRADOR` puede crear una ESAL desde el aplicativo.
2. `ADMINISTRADOR` puede editar informacion principal con guardado independiente.
3. `ADMINISTRADOR` puede crear o actualizar personeria juridica.
4. `ADMINISTRADOR` puede agregar y editar representante legal principal o suplente.
5. `ADMINISTRADOR` puede marcar representantes como vigentes o no vigentes.
6. `ADMINISTRADOR` puede agregar y editar miembros del organo de administracion.
7. `EXPEDIDOR` no puede acceder a endpoints de edicion.
8. Cancelar una ESAL exige resolucion, fecha y motivo.
9. Cancelar cambia estado a `CANCELADO` y crea actuacion administrativa `CANCELACION`.
10. Cancelacion sin PDF soporte guarda la cancelacion y deja advertencia.
11. Reactivar desde `CANCELADO` exige motivo y audita el cambio.
12. Cada guardado recalcula completitud.
13. Certificados historicos no cambian despues de editar datos.
14. Nuevas vistas previas usan la informacion vigente actualizada.

## 12. Pendientes Deliberados

- Definir si `OrganoAdministracion` requiere campo tecnico de vigencia/inactivacion.
- Confirmar si la reactivacion debe crear una nueva actuacion administrativa o solo evento de auditoria.
- Confirmar texto exacto de advertencia por falta de PDF soporte de cancelacion.
- Confirmar si I6 ampliara CRUD a reformas, suspension, liquidacion y documentos soporte avanzados.
