# Spec I4 - Seguridad Institucional, Autorizacion Y Hardening

> Estado: propuesta para revision.  
> Fecha: 2026-05-15.  
> Sistema: `SED_ESAL`.  
> Metodologia: SDD Spec-Anchored.  
> PRD base: `docs/specs/2026-05-09-sed-esal-certificados-prd.md`.  
> Depende de: `docs/specs/2026-05-15-sed-esal-i1-spec.md`, `docs/specs/2026-05-15-sed-esal-i2-spec.md`, `docs/specs/2026-05-15-sed-esal-i3-spec.md`.

## 1. Objetivo

Implementar el marco de seguridad institucional para `SED_ESAL`: autenticacion con cuentas SED/Azure AD u Office 365, autorizacion por roles, endurecimiento de API y frontend, proteccion de documentos/PDFs, trazabilidad de seguridad y preparacion para despliegue controlado en WebLogic.

I4 reemplaza la autenticacion local-dev como mecanismo operativo principal en ambiente institucional, conservandola solo para desarrollo local controlado.

## 2. Alcance

Incluye:

- Integracion de autenticacion institucional mediante JWT emitido por Azure AD / Office 365 o proveedor que confirme SED.
- Validacion de tokens en backend.
- Resolucion de usuario institucional.
- Mapeo de roles institucionales a roles de aplicacion.
- Autorizacion backend por endpoint y accion.
- Guardas frontend por rol.
- Politicas CORS por ambiente.
- Cabeceras HTTP de seguridad.
- Proteccion de descarga de documentos soporte y certificados.
- Registro de auditoria de seguridad.
- Manejo seguro de errores.
- Preparacion de configuracion por perfiles `local-dev` y `weblogic`.
- Checklist de hardening previo a despliegue.

Excluye:

- Administracion de usuarios dentro de Azure AD.
- Alta/baja institucional de cuentas SED.
- Firma digital certificada.
- WAF, SIEM o herramientas externas no confirmadas.
- Consulta publica externa.
- QR o codigo publico de verificacion.

## 3. Supuestos

- El sistema es interno SED.
- Los roles funcionales iniciales son `ADMINISTRADOR` y `EXPEDIDOR`.
- En `local-dev` se permite HTTP Basic solo para desarrollo.
- En `weblogic` no se debe usar usuario/clave local para operacion real.
- El proveedor institucional esperado es Azure AD / Office 365, pendiente de confirmacion tecnica definitiva.
- Si la SED entrega una seccion de seguridad base distinta, esta spec debe ajustarse antes de implementacion.

## 4. Roles Y Permisos

### ADMINISTRADOR

Puede:

- Administrar base ESAL.
- Cargar Excel inicial.
- Crear y editar registros.
- Cambiar estados.
- Cargar documentos soporte.
- Configurar numeracion.
- Configurar firmantes.
- Generar y descargar certificados.
- Consultar auditoria.

### EXPEDIDOR

Puede:

- Buscar ESAL.
- Consultar detalle.
- Consultar vista previa.
- Generar certificado si esta habilitado.
- Descargar certificados generados.
- Consultar historial operativo de certificados asociados al flujo permitido.

No puede:

- Editar ESAL.
- Cargar documentos.
- Cambiar estados.
- Configurar numeracion.
- Configurar firmantes.
- Consultar auditoria administrativa global.

### AUDITOR

Rol candidato para iteracion posterior.

Puede:

- Consultar auditoria.
- Consultar certificados expedidos.
- Consultar eventos de seguridad.

No puede:

- Modificar datos.
- Generar certificados.

Regla:

- I4 debe dejar extensible el modelo para `AUDITOR`, aunque no sea obligatorio habilitarlo en MVP si la DIV no lo confirma.

## 5. Autenticacion

### 5.1. Local Dev

Reglas:

- HTTP Basic permitido solo en perfil `local-dev`.
- Usuarios de prueba definidos en I1.
- Passwords solo para desarrollo.
- Swagger puede usarse con Basic en local.

### 5.2. Ambiente Institucional

Reglas:

- Usar token Bearer JWT.
- Validar emisor (`issuer`).
- Validar audiencia (`audience`).
- Validar firma.
- Validar expiracion.
- Rechazar tokens sin usuario identificable.
- Rechazar tokens de tenants no permitidos.
- No confiar en datos de rol enviados por frontend.

Claims esperados:

- Identificador unico de usuario.
- Correo institucional.
- Nombre visible.
- Grupos o roles institucionales.
- Tenant/issuer.

Pendiente:

- Confirmar nombres exactos de claims y grupos SED.

## 6. Autorizacion

Reglas:

- La autorizacion se aplica en backend.
- El frontend solo oculta o muestra opciones; no es fuente de seguridad.
- Cada endpoint debe declarar roles permitidos.
- Las operaciones administrativas requieren `ADMINISTRADOR`.
- Generacion y descarga requieren `ADMINISTRADOR` o `EXPEDIDOR`.
- Auditoria global requiere `ADMINISTRADOR` o futuro `AUDITOR`.

Matriz inicial:

| Modulo | ADMINISTRADOR | EXPEDIDOR |
|---|---:|---:|
| Carga inicial | Si | No |
| CRUD ESAL | Si | No |
| Documentos soporte | Si | No |
| Busqueda | Si | Si |
| Vista previa | Si | Si |
| Generar certificado | Si | Si |
| Descargar certificado | Si | Si |
| Numeracion | Si | No |
| Firmantes | Si | No |
| Auditoria | Si | No |

## 7. Sesion Frontend

Reglas:

- El token se conserva segun politica institucional aprobada.
- No almacenar secretos en codigo.
- No registrar tokens en consola.
- Cerrar sesion debe limpiar estado local del frontend.
- Rutas protegidas deben usar guardas por autenticacion y rol.
- Errores 401/403 deben tener mensajes claros y no filtrar detalles internos.

## 8. CORS Y Cabeceras

CORS:

- `local-dev`: permitir origen local del frontend.
- `weblogic`: permitir solo dominios institucionales confirmados.
- No usar wildcard `*` con credenciales.

Cabeceras minimas:

- `X-Content-Type-Options: nosniff`
- `X-Frame-Options: DENY` o equivalente institucional.
- `Referrer-Policy: no-referrer`
- `Cache-Control` restrictivo para PDFs y respuestas sensibles.
- `Content-Security-Policy` para frontend cuando sea viable en despliegue.

## 9. Proteccion De Archivos

Reglas:

- Documentos soporte y certificados no se exponen por ruta fisica.
- Toda descarga pasa por backend autenticado.
- Backend valida rol antes de entregar bytes.
- Backend registra descarga.
- Backend valida hash del certificado antes de descargar.
- PDFs de certificados deben servirse con nombre controlado.
- Respuestas de descarga deben evitar cache no autorizado.

## 10. Auditoria De Seguridad

Eventos minimos:

- Login exitoso, si el mecanismo lo permite.
- Login fallido o token invalido, si el mecanismo lo permite.
- Acceso denegado 403.
- Token expirado o invalido.
- Descarga de documento soporte.
- Descarga de certificado.
- Cambio de rol/configuracion local, si aplica.
- Cambio de firmante/numeracion.
- Error de validacion de seguridad.

Campos minimos:

- Usuario.
- Correo institucional.
- Rol de aplicacion.
- Accion.
- Fecha/hora.
- IP/equipo si esta disponible.
- User agent si esta disponible.
- Recurso solicitado.
- Resultado.
- Motivo de rechazo, sin exponer secretos.

## 11. Manejo Seguro De Errores

Reglas:

- No exponer stack traces al frontend.
- No exponer rutas fisicas.
- No exponer valores de token.
- Errores funcionales deben usar codigos claros.
- Errores tecnicos deben registrarse server-side con correlacion.
- Respuestas 401/403 deben ser consistentes.

## 12. Configuracion Por Ambiente

### local-dev

- HTTP Basic.
- CORS local.
- Swagger activo.
- Usuarios de prueba.
- Almacenamiento local controlado.

### weblogic

- Bearer JWT.
- CORS institucional.
- Swagger segun politica SED, idealmente restringido.
- Sin usuarios locales de operacion.
- Configuracion externa de issuer, audience, jwks y origenes permitidos.
- Logs orientados a auditoria institucional.

## 13. API

Endpoints o capacidades propuestas:

```text
GET /api/auth/me
GET /api/auth/permisos
GET /api/admin/seguridad/eventos
```

Reglas:

- `/api/auth/me` retorna usuario autenticado, roles y permisos efectivos.
- `/api/auth/permisos` permite al frontend construir navegacion autorizada.
- Eventos de seguridad requieren `ADMINISTRADOR` o futuro `AUDITOR`.

## 14. Pruebas

Backend:

- Rechaza request sin token en perfil institucional.
- Rechaza token expirado.
- Rechaza token con audience incorrecta.
- Rechaza token con issuer incorrecto.
- Mapea usuario institucional a rol de aplicacion.
- Admin accede a endpoints admin.
- Expedidor recibe 403 en endpoints admin.
- Expedidor genera certificado si cumple regla funcional.
- Descarga valida permiso.
- Descarga registra auditoria.
- Error tecnico no expone stack trace.

Frontend:

- Login institucional redirige correctamente.
- `/api/auth/me` carga usuario y permisos.
- Menu oculta opciones no permitidas.
- Guardas bloquean rutas no autorizadas.
- 401 limpia sesion.
- 403 muestra mensaje claro.

Manual:

- Ejecutar casos I4 de `docs/GUIA_PRUEBAS_FUNCIONALES.md`.

## 15. Criterios De Aceptacion

1. Perfil `local-dev` conserva usuarios de desarrollo.
2. Perfil institucional usa Bearer JWT.
3. Backend valida issuer, audience, firma y expiracion.
4. Backend resuelve usuario institucional.
5. Backend mapea roles institucionales a roles de aplicacion.
6. Autorizacion por endpoint se aplica en backend.
7. Expedidor no puede acceder a endpoints administrativos.
8. Admin puede acceder a configuraciones administrativas.
9. Descargas pasan por backend autenticado.
10. Descargas sensibles registran auditoria.
11. CORS esta restringido por ambiente.
12. Cabeceras de seguridad minimas estan configuradas.
13. Errores no exponen stack trace ni rutas fisicas.
14. Frontend usa guardas por autenticacion y rol.
15. `/api/auth/me` informa usuario y permisos efectivos.
16. Eventos de seguridad quedan auditados.
17. Swagger en ambiente institucional queda controlado segun politica.
18. `ARCHITECTURE.md`, `ARRANQUE.md`, `README.md` y guia de pruebas quedan actualizados.

## 16. Fuera De Alcance

- Alta/baja de usuarios en Azure AD.
- Gobierno de grupos institucionales fuera del aplicativo.
- Firma digital certificada.
- Consulta publica.
- QR.
- WAF/SIEM no confirmado.

## 17. Preguntas Abiertas

1. Confirmar tenant, issuer, audience y mecanismo exacto de Azure AD/Office 365.
2. Confirmar nombres de grupos institucionales para `ADMINISTRADOR` y `EXPEDIDOR`.
3. Confirmar si existira rol `AUDITOR` desde MVP o incremento posterior.
4. Confirmar politica de Swagger en ambientes institucionales.
5. Confirmar dominios permitidos para CORS.
6. Confirmar politica de retencion de logs de seguridad.
7. Confirmar cabeceras obligatorias institucionales adicionales.
