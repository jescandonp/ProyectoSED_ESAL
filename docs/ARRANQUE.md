# ARRANQUE SED_ESAL

> Estado: base documental aprobada, pre-implementacion funcional.
> Metodologia: Spec-Driven Development (SDD), nivel Spec-Anchored.
> Ultima actualizacion: 2026-05-15.

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

`SED_ESAL` esta en fase de implementacion I1 con I0-I4 aprobados como base documental. El backend base `sed-esal-backend` ya fue creado como bootstrap Spring Boot WAR. El frontend `sed-esal-angular` fue creado con Angular 20.3, PrimeNG 20.4.0, rutas base, autenticacion local-dev, shell layout institucional y pantalla de login. La implementacion funcional sigue I1, segun la spec y el plan aprobados.

- Spec 0 de fundacion documental y arquitectura: aprobado.
- Spec I1 de modelo base, carga inicial y completitud: aprobado como incremento activo.
- Spec I2 de busqueda operativa y vista previa certificable: aprobado como incremento futuro.
- Spec I3 de generacion PDF, numeracion, firmante y trazabilidad: aprobado como incremento futuro.
- Spec I4 de seguridad institucional, autorizacion y hardening: aprobado como incremento futuro.
- Plan de implementacion correspondiente al incremento activo.

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

## Proximo Orden De Trabajo

1. Ejecutar I1 segun `docs/specs/2026-05-15-sed-esal-i1-spec.md` y `docs/plans/2026-05-15-sed-esal-i1-plan.md`.
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
