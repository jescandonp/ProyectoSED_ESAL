# Spec I0 - Fundacion Documental Y Arquitectura SED_ESAL

> Estado: propuesta inicial.  
> Fecha: 2026-05-15.  
> Sistema: `SED_ESAL`.  
> Metodologia: SDD Spec-Anchored.  
> PRD base: `docs/specs/2026-05-09-sed-esal-certificados-prd.md`.

## 1. Objetivo

Establecer la base documental, metodologica y arquitectonica propia de `SED_ESAL` antes de iniciar implementacion funcional.

Esta spec existe para evitar que el proyecto dependa informalmente de documentos de SIGCON. SIGCON se usa como referencia institucional, pero `SED_ESAL` debe contar con sus propios artefactos rectores.

## 2. Alcance

Incluye:

- Crear `docs/CONSTITUTION.md`.
- Crear `docs/ARCHITECTURE.md`.
- Crear `docs/TECNOLOGIAS.md`.
- Crear `docs/ARRANQUE.md`.
- Crear `docs/GUIA_PRUEBAS_FUNCIONALES.md`.
- Confirmar que el PRD vigente apunte a documentos propios de `SED_ESAL`.
- Definir coordenadas tecnicas propuestas para specs posteriores.
- Dejar preguntas abiertas para cerrar antes de codificacion.

Excluye:

- Crear backend.
- Crear frontend.
- Crear DDL.
- Implementar carga Excel.
- Implementar generacion PDF.
- Integrar Azure AD.

## 3. Decisiones

Coordenadas propuestas:

| Elemento | Valor |
|---|---|
| Backend | `sed-esal-backend` |
| Frontend | `sed-esal-angular` |
| WAR | `sed-esal-backend.war` |
| Contexto WebLogic | `/sed-esal` |
| Paquete Java base | `co.gov.bogota.sed.esal` |
| Esquema Oracle | `SED_ESAL` |
| Prefijo Oracle | `ESAL_` |

Estas coordenadas quedan propuestas, no congeladas, hasta revision de lider tecnico/arquitectura.

## 4. Requisitos Funcionales

1. El proyecto debe tener constitucion SDD propia.
2. El proyecto debe tener arquitectura propia, no solo referencia a SIGCON.
3. El proyecto debe tener documento de tecnologias propio.
4. El proyecto debe tener guia de arranque propia.
5. El proyecto debe tener guia inicial de pruebas funcionales.
6. Los documentos deben referenciar `SED_ESAL`, no coordenadas SIGCON.
7. El orden de autoridad documental debe quedar definido.
8. Las reglas no negociables deben quedar claras antes de I1.

## 5. Requisitos No Funcionales

- Los documentos deben ser Markdown.
- Los documentos deben mantenerse en `docs/`.
- Los documentos deben ser legibles para negocio, arquitectura, desarrollo y QA.
- La estructura debe soportar evolucion por incrementos.
- Las decisiones abiertas deben quedar explicitamente marcadas.

## 6. Criterios De Aceptacion

1. Existe `docs/CONSTITUTION.md`.
2. Existe `docs/ARCHITECTURE.md`.
3. Existe `docs/TECNOLOGIAS.md`.
4. Existe `docs/ARRANQUE.md`.
5. Existe `docs/GUIA_PRUEBAS_FUNCIONALES.md`.
6. Existe `docs/plans/`.
7. La constitucion define autoridad de artefactos.
8. La arquitectura define coordenadas `SED_ESAL`.
9. Tecnologias define versiones canonicas.
10. Arranque indica que aun no hay implementacion y que I1 no debe iniciar sin spec y plan.
11. Guia de pruebas incluye formato de evidencia e incremento 0.

## 7. Preguntas Abiertas Para Cierre Posterior

1. Confirmar contexto WebLogic definitivo: `/sed-esal` u otro.
2. Confirmar esquema Oracle definitivo: `SED_ESAL` u otro.
3. Confirmar prefijo de tablas: `ESAL_` u otro.
4. Confirmar estrategia de almacenamiento documental/PDF.
5. Confirmar si `sed-esal-angular` y `sed-esal-backend` seran los nombres finales de carpetas.
6. Confirmar politica de retencion de documentos soporte.

## 8. Verificacion

- Revision manual de existencia de archivos.
- Revision de encabezados y coordenadas.
- Busqueda de referencias heredadas incorrectas a SIGCON.
- Confirmacion de que el PRD vigente queda alineado con la base documental.

