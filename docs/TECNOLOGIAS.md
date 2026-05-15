# TECNOLOGIAS SED_ESAL

> Estado: base inicial pre-implementacion.  
> Fuente rectora: `docs/CONSTITUTION.md` y `docs/ARCHITECTURE.md`.  
> Alcance: versiones canonicas para implementar `SED_ESAL` bajo SDD Spec-Anchored.

## Coordenadas Del Sistema

| Elemento | Valor |
|---|---|
| Sistema | `SED_ESAL` |
| Backend | `sed-esal-backend` |
| Frontend | `sed-esal-angular` |
| WAR | `sed-esal-backend.war` |
| Contexto WebLogic propuesto | `/sed-esal` |
| Paquete Java base propuesto | `co.gov.bogota.sed.esal` |
| Esquema Oracle MVP propuesto | `SED_ESAL` |
| Prefijo Oracle propuesto | `ESAL_` |

## Backend

| Tecnologia | Version canonica | Regla |
|---|---|---|
| Java runtime | Oracle JDK 8 | Obligatorio por WebLogic 12.2.1.4.0 |
| Spring Boot | 2.7.18 | No subir a Spring Boot 3.x |
| Spring Framework | 5.3.x | Transitivo de Spring Boot 2.7.18 |
| Spring Security | 5.7.x | Compatible con Spring Boot 2.7.x |
| Spring OAuth2 Resource Server | 2.7.x | Validacion JWT Azure AD en perfil `weblogic` |
| Spring Data JPA | 2.7.x | Persistencia principal |
| Hibernate | 5.6.x | ORM provider |
| SpringDoc OpenAPI | 1.7.0 | Swagger UI siempre activo |
| Oracle JDBC | ojdbc8 19.x | Compatible con JDK 8 y Oracle 19c |
| Maven | 3.9.x | Build backend |
| Packaging | WAR | Despliegue en WebLogic |
| Lombok | 1.18.x | Permitido para reducir boilerplate |

## Frontend

| Tecnologia | Version canonica | Regla |
|---|---|---|
| Angular | 20.x | Standalone components y strict mode |
| TypeScript | 5.8.x | Strict mode obligatorio |
| PrimeNG | 20.x | Libreria primaria de componentes |
| `@primeng/themes` | 20.x | Tema base con tokens SED |
| Tailwind CSS | 3.4.x | Utilidades de layout y espaciado |
| RxJS | 7.8.x | Observables Angular |
| Angular CDK | 20.x | Overlay, teclado y utilidades |
| `@azure/msal-angular` | 3.x | Integracion Office 365 / Azure AD |
| `@azure/msal-browser` | 3.x | Cliente OAuth2/OIDC con PKCE |
| Node.js | 20 LTS | Solo build/desarrollo |
| npm | Version incluida con Node 20 LTS | Gestor de paquetes por defecto |

## Base De Datos

| Tecnologia | Version / regla |
|---|---|
| Oracle Database | 19c+ |
| Dialecto Hibernate | `org.hibernate.dialect.Oracle12cDialect` |
| Esquema local/prod propuesto | `SED_ESAL` |
| Prefijo de tablas propuesto | `ESAL_` |
| DDL | Scripts SQL en `db/` |
| Datos de prueba | Scripts separados para `local-dev` |

## Infraestructura Y Seguridad

| Area | Decision |
|---|---|
| Servidor objetivo | Oracle WebLogic 12.2.1.4.0 |
| Perfil local | `local-dev` |
| Auth local | HTTP Basic con usuarios de prueba |
| Perfil servidor | `weblogic` |
| Auth servidor | Azure AD JWT / Office 365 con MSAL + OAuth2 Resource Server |
| Swagger | Siempre activo |
| Health | Actuator health accesible |
| Docker/nginx/Keycloak | Fuera de arquitectura inicial |

## UI

| Area | Decision |
|---|---|
| Fuente visual primaria | `docs/DESIGN.md` |
| Prototipos | `Documentos_Referencia/Prototipo/` |
| Tipografia | `Public Sans` |
| Radio base | `4px` |
| Densidad | Administrativa, compacta, orientada a gestion |
| Componentes | PrimeNG 20 antes de controles propios |

## Regla De Evolucion

Cambios de version deben actualizar, en este orden:

1. `docs/CONSTITUTION.md` si cambia una regla no negociable.
2. `docs/ARCHITECTURE.md` si cambia una decision tecnica.
3. Este `docs/TECNOLOGIAS.md`.
4. Specs o plans afectados antes de tocar codigo.
