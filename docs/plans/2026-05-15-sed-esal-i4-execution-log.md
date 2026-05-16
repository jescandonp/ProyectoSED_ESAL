# Execution Log I4 - Seguridad Institucional, Autorizacion Y Hardening

> Spec: `docs/specs/2026-05-15-sed-esal-i4-spec.md`
> Plan: `docs/plans/2026-05-15-sed-esal-i4-plan.md`
> Estado: completado. 99 tests backend. Build Angular OK.
> Fecha: 2026-05-16.

## Resumen

Se implementa el marco de seguridad institucional de SED_ESAL: perfil weblogic con JWT Azure AD, autorizacion backend por endpoint, headers HTTP de seguridad, auditoria de eventos de seguridad, manejador global de errores sin exposicion de stack traces, y front-end con interceptor 401/403.

Los datos institucionales de Azure AD (tenant, issuer, audience, JWKS, CORS) quedan como variables de entorno con placeholder en `application-weblogic.yml`, pendientes de confirmacion por TI SED antes del despliegue institucional.

## Registro

| Fecha | Evento | Resultado |
|---|---|---|
| 2026-05-15 | Creacion de Spec I4 | Define autenticacion, autorizacion, proteccion de archivos, auditoria y criterios |
| 2026-05-15 | Creacion de Plan I4 | Define tareas T1-T10 y gates de calidad |
| 2026-05-15 | Actualizacion documental | Arquitectura, README, ARRANQUE y guia de pruebas deben apuntar a I4 |
| 2026-05-16 | T1 pom.xml spring-boot-starter-oauth2-resource-server | Dependencia JWT para perfil weblogic |
| 2026-05-16 | T1 application-weblogic.yml | Perfil weblogic con placeholder Azure AD: issuer-uri, jwk-set-uri, audience, cors-origins, rol-claim |
| 2026-05-16 | T2 JwtRolConverter | Mapea claim `roles` de Azure AD a ROLE_ADMINISTRADOR / ROLE_EXPEDIDOR |
| 2026-05-16 | T2 WeblogicSecurityConfig | SecurityFilterChain JWT: validacion issuer, audience, expiracion; autorizacion por endpoint; headers seguridad; CORS institucional |
| 2026-05-16 | T3 UsuarioContextoDto, PermisosDto | DTOs de usuario autenticado y permisos efectivos |
| 2026-05-16 | T3 UsuarioContextoService | Resuelve usuario desde Authentication (Basic o JWT): nombre, email, rol |
| 2026-05-16 | T3 AuthController | GET /api/auth/me y GET /api/auth/permisos |
| 2026-05-16 | T4 DevSecurityConfig actualizado | Agrega security headers (frameOptions, contentType, referrer, cache), EsalAccessDeniedHandler, EsalAuthenticationEntryPoint, cobertura completa endpoints I1-I3 (certificados, PUT admin) |
| 2026-05-16 | T8 AuditoriaAcciones I4 | Constantes: ACCESO_DENEGADO, TOKEN_INVALIDO_O_AUSENTE, ENTIDAD_SEGURIDAD, DOCUMENTO_DESCARGADO |
| 2026-05-16 | T8 EsalAccessDeniedHandler | 403: responde JSON estandar y audita ACCESO_DENEGADO con usuario, rol y recurso |
| 2026-05-16 | T8 EsalAuthenticationEntryPoint | 401: responde JSON estandar y audita TOKEN_INVALIDO_O_AUSENTE |
| 2026-05-16 | T9 GlobalExceptionHandler | Extiende ResponseEntityExceptionHandler: preserva codigos 400 MVC, oculta stack traces, loguea errores tecnicos server-side |
| 2026-05-16 | Tests SecurityTestConfig | Perfil test con reglas de autorizacion equivalentes a local-dev para @WithMockUser |
| 2026-05-16 | Tests JwtRolConverterTest | 5 tests: mapeo admin, expedidor, rol desconocido, sin roles, multiples roles |
| 2026-05-16 | Tests AuthControllerTest | 5 tests: me admin, me expedidor, me sin auth 401, permisos admin, permisos expedidor |
| 2026-05-16 | Tests AuthorizationIntegrationTest | 11 tests: expedidor 403 en admin, expedidor accede busqueda/certificados, admin accede todo, anonimo 401 |
| 2026-05-16 | 99 tests, BUILD SUCCESS | Todos los tests pasan correctamente |
| 2026-05-16 | T5 Angular HttpErrorInterceptor | 401: logout + redirect login; 403: redirect /acceso-denegado |
| 2026-05-16 | T5 Angular AccesoDenegadoComponent | Pantalla 403 con boton volver al inicio |
| 2026-05-16 | T5 Angular app.config.ts | Registro del interceptor HTTP_INTERCEPTORS |
| 2026-05-16 | T5 Angular app.routes.ts | Ruta /acceso-denegado |
| 2026-05-16 | Build Angular | ng build completado sin errores criticos |
| 2026-05-16 | T10 Documentacion | ARRANQUE.md, GUIA_PRUEBAS_FUNCIONALES.md y execution log I4 actualizados |

## Decisiones De Arranque Aprobadas

- I4 no inicia hasta completar o estabilizar I1-I3.
- Azure AD / Office 365 es la referencia institucional esperada.
- El backend sera la fuente efectiva de autorizacion; el frontend solo oculta o muestra opciones.
- `AUDITOR` queda como rol candidato extensible y no entra al MVP operativo inicial salvo decision posterior de DIV/SED.
- Tenant, issuer, audience, JWKS, scopes, grupos/app roles, CORS institucional, politica Swagger, logs y cabeceras se cierran con TI SED antes de activar perfil weblogic.

## Pendiente Para Activacion Weblogic

| Variable de entorno | Descripcion |
|---|---|
| `SED_ESAL_JWK_SET_URI` | URL JWKS de Azure AD (incluye tenant_id) |
| `SED_ESAL_JWT_ISSUER_URI` | Issuer URI de Azure AD (incluye tenant_id) |
| `SED_ESAL_JWT_AUDIENCE` | client_id de la aplicacion en Azure AD |
| `SED_ESAL_ROL_CLAIM` | Nombre del claim de roles (default: `roles`) |
| `SED_ESAL_ADMIN_CLAIM` | Valor del rol admin en el token (default: `ADMINISTRADOR`) |
| `SED_ESAL_EXPEDIDOR_CLAIM` | Valor del rol expedidor en el token (default: `EXPEDIDOR`) |
| `SED_ESAL_CORS_ORIGINS` | Dominios institucionales permitidos (separados por coma) |
| `SED_ESAL_DB_URL` | JDBC Oracle institucional |
| `SED_ESAL_DB_USER` | Usuario Oracle institucional |
| `SED_ESAL_DB_PASSWORD` | Password Oracle institucional |
