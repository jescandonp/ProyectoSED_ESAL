# ARRANQUE SED_ESAL

> Estado: I2 completado. I3 es el siguiente incremento activo.
> Metodologia: Spec-Driven Development (SDD), nivel Spec-Anchored.
> Ultima actualizacion: 2026-05-16.

## Orden De Documentos

1. Constitucion SDD: `docs/CONSTITUTION.md`
2. Arquitectura SED_ESAL: `docs/ARCHITECTURE.md`
3. Versiones del stack: `docs/TECNOLOGIAS.md`
4. Sistema de diseno: `docs/DESIGN.md`
5. PRD: `docs/specs/2026-05-09-sed-esal-certificados-prd.md`
6. Spec 0: `docs/specs/2026-05-15-sed-esal-i0-spec.md`
7. Plan Spec 0: `docs/plans/2026-05-15-sed-esal-i0-plan.md`
8. Spec I1: `docs/specs/2026-05-15-sed-esal-i1-spec.md`
9. Plan I1: `docs/plans/2026-05-15-sed-esal-i1-plan.md`
10. Log I1: `docs/plans/2026-05-15-sed-esal-i1-execution-log.md`
11. Spec I2: `docs/specs/2026-05-15-sed-esal-i2-spec.md`
12. Plan I2: `docs/plans/2026-05-15-sed-esal-i2-plan.md`
13. Log I2: `docs/plans/2026-05-15-sed-esal-i2-execution-log.md`
14. Spec I3: `docs/specs/2026-05-15-sed-esal-i3-spec.md`
15. Plan I3: `docs/plans/2026-05-15-sed-esal-i3-plan.md`
16. Log I3: `docs/plans/2026-05-15-sed-esal-i3-execution-log.md`
17. Spec I4: `docs/specs/2026-05-15-sed-esal-i4-spec.md`
18. Plan I4: `docs/plans/2026-05-15-sed-esal-i4-plan.md`
19. Log I4: `docs/plans/2026-05-15-sed-esal-i4-execution-log.md`
20. Guia de pruebas funcionales: `docs/GUIA_PRUEBAS_FUNCIONALES.md`

## Estado Del Proyecto

`SED_ESAL` tiene I2 completado. El backend tiene 70 tests en verde (BUILD SUCCESS). El frontend Angular compila sin errores. Ambos artefactos listos para ejecucion local-dev.

| Incremento | Estado | Foco |
|---|---|---|
| I0 | Cerrado | Base documental, arquitectura y metodologia |
| I1 | Completado | Modelo base, carga inicial, estados y completitud |
| I2 | Completado | Busqueda operativa y vista previa certificable |
| I3 | Proximo activo | Generacion PDF, numeracion, firmante y trazabilidad |
| I4 | Aprobado futuro | Seguridad institucional, autorizacion y hardening |

### Artefactos I1 entregados

Backend (52 tests, BUILD SUCCESS, WAR `target/sed-esal-backend.war`):

- Modelo Oracle + JPA (10 tablas, prefijo `ESAL_`, esquema `SED_ESAL`).
- Seguridad HTTP Basic local-dev con roles `ADMINISTRADOR` / `EXPEDIDOR`.
- Importacion diccionario de obligatoriedad desde Excel (117 campos).
- Importacion base historica desde Excel (405 filas, upsert idempotente).
- Servicio de completitud y semaforo (LISTO_PARA_CERTIFICAR / INCOMPLETO_NO_BLOQUEANTE / INCOMPLETO_BLOQUEANTE).
- Documentos soporte: upload PDF abstraido, rechazo de no-PDF.
- API REST completa: ESAL CRUD, estados, completitud, documentos, importaciones, auditoria.
- Auditoria con `REQUIRES_NEW`: CREAR, EDITAR, CAMBIAR_ESTADO, CONSULTAR, IMPORTAR, REGISTRAR_DOCUMENTO, RECALCULAR_COMPLETITUD, CONSULTAR_COMPLETITUD, IMPORTAR_DICCIONARIO, CONSULTAR_AUDITORIA.

Frontend (2 tests, npm run build OK):

- Angular 20.3, PrimeNG 20.4.0, diseno institucional SED.
- Login local-dev, shell con navegacion por rol.
- Dashboard, carga inicial, listado ESAL, detalle por secciones, completitud, documentos, auditoria.
- `ApiService` con HTTP Basic local-dev.

## Artefactos Fuente

| Archivo | Uso |
|---|---|
| `Documentos_Referencia/BASE DE DATOS - REGISTRO_1.xlsx` | Base historica y estructura fuente |
| `Documentos_Referencia/Base excel.xlsx` | Diccionario de campos y obligatoriedad |
| `Documentos_Referencia/REGLAS (1).xlsx` | Reglas de negocio |
| `Documentos_Referencia/Maqueta Buscador.xlsx` | Maqueta funcional de busqueda |
| `Documentos_Referencia/Plantilla Certificado EYRL.docx` | Plantilla oficial de certificado |
| `docs/DESIGN.md` | Sistema de diseno institucional |
| `Documentos_Referencia/Prototipo/` | Prototipos visuales |

## Coordenadas Canonicas

| Elemento | Valor |
|---|---|
| Backend | `sed-esal-backend` |
| Frontend | `sed-esal-angular` |
| WAR | `sed-esal-backend.war` |
| Contexto WebLogic | `/sed-esal` |
| Paquete Java base | `co.gov.bogota.sed.esal` |
| Esquema Oracle | `SED_ESAL` |
| Prefijo Oracle | `ESAL_` |

Estas coordenadas quedan aprobadas para iniciar I1. Cualquier cambio posterior por infraestructura SED debe actualizar primero arquitectura, tecnologias, spec y plan afectados.

## Stack Previsto

| Capa | Decision |
|---|---|
| Backend | Java 8, Spring Boot 2.7.18, Maven, WAR |
| Servidor | Oracle WebLogic 12.2.1.4.0 |
| Base de datos | Oracle 19c+, esquema `SED_ESAL`, prefijo `ESAL_` |
| Frontend | Angular 20, PrimeNG 20, Tailwind CSS 3.4 |
| Auth local-dev | HTTP Basic |
| Auth weblogic | Azure AD JWT / Office 365 |

## Arranque Local-Dev

```powershell
# Backend
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-backend
mvn test                    # 52 tests, BUILD SUCCESS
mvn package -DskipTests     # genera target/sed-esal-backend.war
mvn spring-boot:run -Dspring-boot.run.profiles=local-dev  # levanta en :8080

# Frontend
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-angular
npm run build               # genera dist/sed-esal-angular/
npm test -- --watch=false --browsers=ChromeHeadless
npm start                   # levanta en :4200 con proxy hacia :8080
```

## Proximo Orden De Trabajo

1. Iniciar I2 segun `docs/specs/2026-05-15-sed-esal-i2-spec.md` y `docs/plans/2026-05-15-sed-esal-i2-plan.md`.
2. Mantener execution log, commits y push al finalizar cada bloque de trabajo.
3. Mantener `Documentos_Referencia/` fuera del repositorio salvo aprobacion explicita de publicacion.
4. Cerrar decisiones tecnicas diferidas antes del incremento que las requiera: almacenamiento definitivo para I3, Azure AD para I4 y conversion DOCX/PDF para I3.
5. Actualizar `docs/ARCHITECTURE.md` si la seccion de seguridad confirmada por SED cambia esta base.

## Prerrequisitos Locales Esperados

| Herramienta | Version minima | Verificacion |
|---|---|---|
| Oracle JDK 8 | 8u361+ | `java -version` |
| Maven | 3.9.x | `mvn -version` |
| Node.js | 20 LTS | `node -v` |
| npm | 9+ | `npm -v` |
| Oracle DB | 19c compatible | validar con DBA |

Nota de ambiente actual: en esta maquina se verifico Maven 3.9.15 con Java 21 y Node.js 22. El backend queda configurado con `java.version=1.8` para compatibilidad objetivo con WebLogic 12.2.1.4; se recomienda validar tambien con Oracle JDK 8 antes de despliegue institucional.

## Usuarios Local-Dev Aprobados Para I1

| Rol | Email | Password |
|---|---|---|
| ADMINISTRADOR | `admin@educacionbogota.edu.co` | `admin123` |
| EXPEDIDOR | `expedidor@educacionbogota.edu.co` | `expedidor123` |

Estos usuarios aplican solo para `local-dev`. En ambiente institucional se reemplazan por Azure AD / Office 365 segun I4.
