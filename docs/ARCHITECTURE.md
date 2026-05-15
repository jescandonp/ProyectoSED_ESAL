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

- Frontend: Angular 20, TypeScript strict, PrimeNG 20, Tailwind CSS 3.4, RxJS 7.8, Angular CDK 20.
- Backend: Java 8, Spring Boot 2.7.18, Spring Security 5.7, Spring Data JPA 2.7, Hibernate 5.6, SpringDoc OpenAPI 1.7.0.
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
   | REST / Bearer JWT futuro / Basic local-dev
   v
Spring Boot API - WAR en WebLogic
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
```

Reglas:

- Controladores exponen DTOs, no entidades JPA.
- Servicios contienen transacciones y reglas de aplicacion.
- Dominio conserva invariantes de ESAL, certificado, estado, documentos y auditoria.
- Repositorios usan Spring Data JPA.
- Errores funcionales se exponen con `GlobalExceptionHandler`.

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

MVP:

- HTTP Basic en `local-dev`.
- Roles: `ADMINISTRADOR`, `EXPEDIDOR`.

Evolucion:

- Azure AD / Office 365.
- JWT validado en backend.
- Autorizacion por rol en backend.

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

