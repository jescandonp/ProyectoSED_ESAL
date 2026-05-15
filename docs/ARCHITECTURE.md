# Arquitectura de Referencia - SED_ESAL

> Estado: base inicial para especificaciones SDD.  
> Sistema: `SED_ESAL` - Certificados de Existencia y Representacion Legal ESAL con fines educativos.  
> Fecha base: 2026-05-15.  
> Referencia reutilizada: arquitectura SED/SIGCON, adaptada a coordenadas propias de `SED_ESAL`.

## 1. Vision General

`SED_ESAL` es una aplicacion web empresarial interna para la Direccion de Inspeccion y Vigilancia de la Secretaria de Educacion del Distrito. Su objetivo es administrar informacion de ESAL con fines educativos y soportar la expedicion controlada de certificados de existencia y representacion legal.

El sistema se construye bajo SDD Spec-Anchored: las especificaciones vivas son la fuente primaria de verdad, y el codigo es un artefacto derivado de PRD, arquitectura, specs tecnicas y planes aprobados.

## 2. Coordenadas Canonicas

| Coordenada | Valor |
|---|---|
| Sistema | `SED_ESAL` |
| Backend | `sed-esal-backend` |
| Frontend | `sed-esal-angular` |
| WAR | `sed-esal-backend.war` |
| Contexto WebLogic propuesto | `/sed-esal` |
| Paquete Java base propuesto | `co.gov.bogota.sed.esal` |
| Esquema Oracle MVP propuesto | `SED_ESAL` |
| Prefijo Oracle propuesto | `ESAL_` |
| Perfil local | `local-dev` |
| Perfil servidor | `weblogic` |

Estos valores deben confirmarse en Spec 0 antes de codificar.

## 3. Stack Tecnologico

- Frontend: Angular 20, TypeScript strict, PrimeNG 20, Tailwind CSS 3.4, RxJS 7.8, Angular CDK 20, MSAL Angular 3.x.
- Backend: Java 8, Spring Boot 2.7.18, Spring Security 5.7, Spring OAuth2 Resource Server, Spring Data JPA 2.7, Hibernate 5.6, SpringDoc OpenAPI 1.7.0.
- Base de datos: Oracle Database 19c+.
- Servidor objetivo: Oracle WebLogic 12.2.1.4.
- Empaquetado backend: WAR.
- Identidad futura: Azure AD / Office 365.
- Auth local MVP: HTTP Basic con usuarios de desarrollo.

## 4. Arquitectura Por Capas

```text
Usuario interno SED
   |
   | HTTPS
   v
Angular 20 SPA
   |
   | REST / Authorization: Bearer JWT / Basic solo local-dev
   v
Spring Boot API - WAR en WebLogic
   |
   | valida JWT contra Azure AD JWKS en perfil weblogic
   |
   | JDBC / JPA
   v
Oracle Database 19c+
   |
   +-- almacenamiento documental/PDF por definir
```

## 5. Backend

Estructura esperada:

```text
src/main/java/co/gov/bogota/sed/esal/
|-- domain/
|   |-- entity/
|   |-- enums/
|   |-- repository/
|-- application/
|   |-- service/
|   |-- dto/
|   |-- mapper/
|-- web/
|   |-- controller/
|   |-- exception/
|-- config/
|   |-- SecurityConfig.java
|   |-- DevSecurityConfig.java
|   |-- AuditConfig.java
|   |-- OpenApiConfig.java
```

Reglas:

- Controladores exponen DTOs, no entidades JPA.
- Servicios contienen transacciones y reglas de aplicacion.
- Dominio conserva invariantes de ESAL, certificado, estado, documentos y auditoria.
- Repositorios usan Spring Data JPA.
- Errores funcionales se exponen con `GlobalExceptionHandler`.
- `SecurityConfig` gobierna Azure AD JWT en perfil `weblogic`.
- `DevSecurityConfig` gobierna HTTP Basic solo en perfil `local-dev`.
- `AuditConfig` resuelve usuario actual desde `preferred_username` o usuario local-dev.
- `OpenApiConfig` mantiene Swagger activo con esquema `BearerAuth`.

## 6. Frontend

Estructura esperada:

```text
src/app/
|-- core/
|-- shared/
|-- features/
|   |-- dashboard/
|   |-- esal/
|   |-- busqueda/
|   |-- certificados/
|   |-- documentos/
|   |-- administracion/
```

Reglas:

- Usar Angular standalone components.
- Usar PrimeNG como libreria primaria.
- Usar Tailwind para layout y espaciado.
- Seguir `docs/DESIGN.md` y prototipos de referencia.
- Flujos administrativos deben ser densos, claros y sin composicion tipo landing.

## 7. Modelo De Dominio Conceptual

Entidades base:

- `ESAL`: raiz agregada para informacion principal.
- `PersoneriaJuridica`: reconocimiento, fecha y entidad que expide.
- `ReformaEstatutaria`: lista dinamica de reformas.
- `Nombramiento`: representantes legales, suplentes, revisor fiscal, tesorero y dignatarios.
- `OrganoAdministracion`: organos, miembros, cargos y facultades.
- `ActuacionAdministrativa`: suspension, liquidacion y cancelacion.
- `DocumentoSoporte`: PDF asociado a procesos administrativos.
- `Certificado`: numero unico, PDF, hash y version de datos.
- `Firmante`: firmante configurable por vigencia.
- `Auditoria`: trazas operativas.

## 8. Reglas De Datos

- `ID SIPEJ` es identificador funcional principal y debe ser unico cuando exista.
- La carga historica puede importar datos incompletos y marcarlos con advertencias.
- Nuevos registros y actualizaciones formales deben cumplir obligatoriedad y documentos soporte.
- Campos duplicados por nombre se interpretan por contexto de seccion o entidad hija.
- Las reformas no se modelan como columnas fijas.

## 9. Seguridad

La seguridad de `SED_ESAL` toma como referencia la arquitectura SIGCON/SED y se adapta al dominio ESAL. La regla base es que el frontend nunca es fuente de autorizacion: toda decision efectiva de acceso se valida en backend.

Objetivo de cumplimiento: nivel N2 para aplicacion web interna con datos confidenciales, transacciones de negocio, trazabilidad de expedicion y documentos/PDFs controlados.

### 9.0. Arquitectura De Autenticacion

```text
Usuario interno SED
   |
   | HTTPS
   v
Angular SPA
   |
   | OAuth2 Authorization Code + PKCE
   v
Azure AD / Office 365 - Tenant SED
   |
   | access_token JWT
   v
Angular MsalInterceptor
   |
   | Authorization: Bearer <JWT>
   v
Spring Boot API - WebLogic
   |
   | OAuth2 Resource Server valida firma, issuer, audience y expiracion
   v
Servicios SED_ESAL + Oracle
```

Estrategia por ambiente:

| Ambiente | Backend | Frontend | Uso |
|---|---|---|---|
| `local-dev` | `DevSecurityConfig` + HTTP Basic | `DevSessionService` o equivalente | Desarrollo local sin dependencia de Azure |
| `weblogic` | `SecurityConfig` + OAuth2 Resource Server | MSAL Angular 3.x | Operacion institucional |

### 9.1. Perfiles

`local-dev`:

- HTTP Basic solo para desarrollo.
- Usuarios de prueba definidos por spec activa.
- CORS local para el frontend.
- Swagger activo para validacion tecnica.
- HSTS deshabilitado porque local-dev corre sobre HTTP.
- No usar usuarios local-dev en despliegue institucional.

`weblogic`:

- Bearer JWT institucional.
- Integracion prevista con Azure AD / Office 365 o proveedor que confirme SED.
- Configuracion externa de issuer, audience, JWKS y origenes permitidos.
- Sin usuarios locales de operacion.
- Sesion stateless en backend.
- CSRF deshabilitado para API REST stateless con Bearer JWT.
- HTTPS obligatorio en infraestructura institucional.

### 9.2. Roles

Roles funcionales iniciales:

- `ADMINISTRADOR`: administra base ESAL, documentos, estados, firmantes, numeracion, certificados y auditoria.
- `EXPEDIDOR`: consulta, previsualiza, genera y descarga certificados; no edita datos base.

Rol candidato:

- `AUDITOR`: consulta auditoria y trazas sin modificar datos ni generar certificados.

Roles propuestos para App Registration:

| App Role Azure AD | Rol Aplicativo | Uso |
|---|---|---|
| `ESAL_ADMINISTRADOR` | `ADMINISTRADOR` | Gestion administrativa de ESAL, reglas, documentos, firmantes, numeracion y auditoria |
| `ESAL_EXPEDIDOR` | `EXPEDIDOR` | Busqueda, vista previa, generacion y descarga de certificados |
| `ESAL_AUDITOR` | `AUDITOR` | Consulta de trazas y certificados, si la DIV lo confirma |

### 9.3. Autenticacion Institucional

El frontend institucional usa MSAL Angular 3.x y MSAL Browser 3.x. Debe usar OAuth2 Authorization Code con PKCE, `MsalGuard` para rutas autenticadas y `MsalInterceptor` para adjuntar `Authorization: Bearer` en llamadas a la API.

El backend debe validar cada request protegida como OAuth2 Resource Server:

- Firma del token.
- Emisor (`issuer`).
- Audiencia (`audience`).
- Expiracion.
- Tenant permitido.
- Usuario institucional identificable.

Claims esperados:

- `preferred_username`: correo corporativo O365, usado como principal y auditor.
- `name`: nombre visible.
- `oid` o identificador estable equivalente.
- `roles`: App Roles asignados en Azure AD.
- `tid`: tenant.

Los nombres exactos de claims y grupos quedan pendientes de confirmacion SED.

Reglas:

- El backend no confia en roles calculados en frontend.
- El claim `roles` se convierte a autoridades Spring con prefijo `ROLE_`.
- `preferred_username` se usa como `Authentication.getName()` y como base para `CREATED_BY` / `UPDATED_BY`.
- Si el token no contiene usuario identificable, se rechaza la peticion.

### 9.4. Autorizacion

Reglas:

- Endpoints administrativos requieren `ADMINISTRADOR`.
- Busqueda y vista previa permiten `ADMINISTRADOR` y `EXPEDIDOR`.
- Generacion y descarga de certificados permiten `ADMINISTRADOR` y `EXPEDIDOR` si la regla funcional habilita expedicion.
- Auditoria global requiere `ADMINISTRADOR` o futuro `AUDITOR`.
- Documentos y certificados no se sirven por ruta fisica; toda descarga pasa por backend autenticado.

Matriz base:

| Modulo | ADMINISTRADOR | EXPEDIDOR | AUDITOR candidato |
|---|---:|---:|---:|
| Carga inicial Excel | Si | No | No |
| CRUD ESAL | Si | No | No |
| Documentos soporte | Si | No | Consulta |
| Busqueda | Si | Si | Si |
| Vista previa | Si | Si | Si |
| Generacion certificado | Si | Si | No |
| Descarga certificado | Si | Si | Consulta |
| Numeracion | Si | No | No |
| Firmantes | Si | No | Consulta |
| Auditoria global | Si | No | Si |

### 9.5. CORS Y Cabeceras

Reglas iniciales:

- CORS se configura por ambiente.
- No se permite wildcard `*` con credenciales.
- Respuestas sensibles y descargas deben usar cache restrictivo.
- CORS local permite `http://localhost:4200`.
- CORS institucional permite solo origenes SED confirmados.

Cabeceras minimas:

| Header | Valor base | Proposito |
|---|---|---|
| `X-Frame-Options` | `DENY` | Previene clickjacking |
| `X-Content-Type-Options` | `nosniff` | Previene MIME sniffing |
| `Content-Security-Policy` | `default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'; img-src 'self' data:; frame-ancestors 'none'` | Restringe recursos externos |
| `Strict-Transport-Security` | `max-age=31536000; includeSubDomains` | Fuerza HTTPS en `weblogic`; no aplica en local-dev |
| `Referrer-Policy` | `strict-origin-when-cross-origin` | Limita informacion enviada como referer |
| `Permissions-Policy` | `geolocation=(), camera=(), microphone=()` | Deshabilita APIs browser no usadas |
| `Cache-Control` | `no-cache, no-store, max-age=0, must-revalidate` | Evita cache de respuestas sensibles y PDFs |

Nota: `unsafe-inline` en `style-src` se acepta inicialmente por compatibilidad con Angular/PrimeNG; debe revisarse si el despliegue institucional exige CSP mas estricta.

### 9.6. Auditoria De Seguridad

Eventos minimos:

- Acceso denegado.
- Token invalido o expirado cuando sea viable.
- Descarga de documento soporte.
- Descarga de certificado.
- Cambios de firmante y numeracion.
- Errores de seguridad.

La auditoria no debe persistir secretos, tokens completos ni passwords.

Auditoria de datos:

- Toda tabla principal debe incluir `CREATED_AT`, `CREATED_BY`, `UPDATED_AT`, `UPDATED_BY` cuando aplique y campo de borrado logico si la entidad lo requiere.
- `CREATED_BY` y `UPDATED_BY` deben tomar `preferred_username` en ambiente institucional.
- En `local-dev`, deben tomar el usuario Basic autenticado.
- JPA debe usar `@EnableJpaAuditing` y `AuditorAware<String>`.

### 9.7. Swagger Y OpenAPI

Swagger se mantiene activo para validacion de contratos, alineado con la referencia SED/SIGCON.

Reglas:

- SpringDoc OpenAPI 1.7.0.
- OpenAPI debe declarar esquema `BearerAuth`.
- Endpoints deben documentar roles, codigos 401/403 y errores funcionales.
- Si infraestructura SED exige restriccion adicional para Swagger en ambiente institucional, se documenta como cambio de arquitectura y se ajusta la spec afectada.

### 9.8. Configuracion Requerida Azure AD

Pendiente con TI SED:

```text
AZURE_TENANT_ID
AZURE_CLIENT_ID
AZURE_ISSUER_URI
AZURE_JWKS_URI o issuer-uri resoluble
API_SCOPE de SED_ESAL
Redirect URI produccion: https://[servidor]/sed-esal
Redirect URI desarrollo: http://localhost:4200
App Roles: ESAL_ADMINISTRADOR, ESAL_EXPEDIDOR, ESAL_AUDITOR candidato
```

### 9.9. Checklist De Implementacion Seguridad

Backend:

- Configurar `spring-boot-starter-oauth2-resource-server`.
- Crear `SecurityConfig` con perfil `weblogic`.
- Crear `DevSecurityConfig` con perfil `local-dev`.
- Crear conversor de roles desde claim `roles`.
- Usar `preferred_username` como principal.
- Configurar `AuditConfig` con `AuditorAware`.
- Proteger endpoints por rol.
- Configurar CORS por ambiente.
- Configurar headers HTTP de seguridad.
- Documentar `BearerAuth` en OpenAPI.

Frontend:

- Instalar `@azure/msal-angular` y `@azure/msal-browser`.
- Configurar MSAL con `tenantId`, `clientId` y scopes.
- Usar `MsalGuard` para rutas autenticadas.
- Usar `roleGuard` propio para RBAC granular.
- Usar `MsalInterceptor` para `Authorization: Bearer`.
- Mantener mecanismo local-dev separado.
- No registrar tokens en consola ni persistir secretos.

## 10. Documentos Y PDFs

Decisiones pendientes:

- Almacenamiento: base de datos, filesystem institucional o gestor documental.
- Ruta local-dev para documentos soporte y certificados.
- Politica de retencion.
- Acceso a descarga y auditoria.

Reglas iniciales:

- Documentos soporte se cargan en PDF.
- Certificados generados deben tener hash y version de datos usada.
- QR/codigo de verificacion queda fuera del MVP, pero debe ser extensible.

## 11. Observabilidad Y Auditoria

La auditoria debe registrar:

- Usuario.
- Rol.
- Fecha/hora.
- Accion.
- ESAL.
- `ID SIPEJ`.
- NIT, cuando aplique.
- Numero de certificado, cuando aplique.
- Version de datos.
- IP/equipo si esta disponible.
- Resultado.
- Error funcional o tecnico.
- Hash del PDF, cuando aplique.

## 12. Evolucion Documental

Cambios de version deben actualizar, en este orden:

1. `docs/CONSTITUTION.md` si cambia una regla no negociable.
2. `docs/ARCHITECTURE.md` si cambia una decision tecnica.
3. `docs/TECNOLOGIAS.md` si cambia una version o herramienta.
4. PRD/spec afectada.
5. Plan de implementacion.
6. Codigo.
