# ARRANQUE SED_ESAL

> Estado: base documental inicial, pre-implementacion.  
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

`SED_ESAL` esta en fase de especificacion SDD. Aun no hay codigo fuente backend/frontend. No se debe iniciar implementacion funcional hasta aprobar:

- Spec 0 de fundacion documental y arquitectura.
- Spec I1 de modelo base, carga inicial y completitud.
- Spec I2 de busqueda operativa y vista previa certificable.
- Spec I3 de generacion PDF, numeracion, firmante y trazabilidad.
- Spec I4 de seguridad institucional, autorizacion y hardening.
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

## Coordenadas Propuestas

| Elemento | Valor |
|---|---|
| Backend | `sed-esal-backend` |
| Frontend | `sed-esal-angular` |
| WAR | `sed-esal-backend.war` |
| Contexto WebLogic | `/sed-esal` |
| Paquete Java base | `co.gov.bogota.sed.esal` |
| Esquema Oracle | `SED_ESAL` |
| Prefijo Oracle | `ESAL_` |

Estas coordenadas deben confirmarse o ajustarse en Spec 0.

## Stack Previsto

| Capa | Decision |
|---|---|
| Backend | Java 8, Spring Boot 2.7.18, Maven, WAR |
| Servidor | Oracle WebLogic 12.2.1.4.0 |
| Base de datos | Oracle 19c+, esquema propuesto `SED_ESAL`, prefijo `ESAL_` |
| Frontend | Angular 20, PrimeNG 20, Tailwind CSS 3.4 |
| Auth local-dev | HTTP Basic |
| Auth weblogic | Azure AD JWT / Office 365 |

## Proximo Orden De Trabajo

1. Revisar y aprobar Spec 0.
2. Revisar y aprobar Spec I1.
3. Revisar y aprobar Spec I2.
4. Revisar y aprobar Spec I3.
5. Revisar y aprobar Spec I4.
6. Cerrar preguntas tecnicas de coordenadas, almacenamiento documental, contexto WebLogic y seguridad institucional.
7. Actualizar `docs/ARCHITECTURE.md` si la seccion de seguridad confirmada por SED cambia esta base.
8. Solo despues iniciar implementacion funcional del incremento aprobado.

## Prerrequisitos Locales Esperados

| Herramienta | Version minima | Verificacion |
|---|---|---|
| Oracle JDK 8 | 8u361+ | `java -version` |
| Maven | 3.9.x | `mvn -version` |
| Node.js | 20 LTS | `node -v` |
| npm | 9+ | `npm -v` |
| Oracle DB | 19c compatible | validar con DBA |

## Usuarios Local-Dev Propuestos

| Rol | Email | Password |
|---|---|---|
| ADMINISTRADOR | `admin@educacionbogota.edu.co` | `admin123` |
| EXPEDIDOR | `expedidor@educacionbogota.edu.co` | `expedidor123` |

La spec activa debe confirmar roles, permisos y nombres definitivos antes de codificar.
