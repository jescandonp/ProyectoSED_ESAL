# ARRANQUE SED_ESAL

> Estado: I10 completado (backend 164 tests OK, WAR OK, build Angular OK; runner Angular test no validado por restriccion sandbox/watch).
> Metodologia: Spec-Driven Development (SDD), nivel Spec-Anchored.
> Ultima actualizacion: 2026-06-20.

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
20. Spec I5: `docs/specs/2026-05-21-sed-esal-i5-spec.md`
21. Plan I5: `docs/plans/2026-05-21-sed-esal-i5-plan.md`
22. Log I5: `docs/plans/2026-05-21-sed-esal-i5-execution-log.md`
23. Spec I6: `docs/specs/2026-05-27-sed-esal-i6-spec.md`
24. Plan I6: `docs/plans/2026-05-27-sed-esal-i6-plan.md`
25. Log I6: `docs/plans/2026-05-27-sed-esal-i6-execution-log.md`
26. Spec I7: `docs/specs/2026-05-29-sed-esal-i7-spec.md`
27. Plan I7: `docs/plans/2026-05-29-sed-esal-i7-plan.md`
28. Log I7: `docs/plans/2026-05-29-sed-esal-i7-execution-log.md`
29. Spec I8: `docs/specs/2026-06-17-sed-esal-i8-spec.md`
30. Plan I8: `docs/plans/2026-06-17-sed-esal-i8-plan.md`
31. Log I8: `docs/plans/2026-06-17-sed-esal-i8-execution-log.md`
32. Spec I9: `docs/specs/2026-06-19-sed-esal-i9-spec.md`
33. Plan I9: `docs/plans/2026-06-19-sed-esal-i9-plan.md`
34. Log I9: `docs/plans/2026-06-19-sed-esal-i9-execution-log.md`
35. Spec I10: `docs/specs/2026-06-20-sed-esal-i10-spec.md`
36. Plan I10: `docs/plans/2026-06-20-sed-esal-i10-plan.md`
37. Log I10: `docs/plans/2026-06-20-sed-esal-i10-execution-log.md`
38. Guia de pruebas funcionales: `docs/GUIA_PRUEBAS_FUNCIONALES.md`

## Estado Del Proyecto

`SED_ESAL` tiene I10 completado. El backend agrega seleccion explicita de plantilla EYRL por estado de ESAL y documento vigente I9 para certificados suspendidos, en liquidacion y cancelados; los demas casos conservan la plantilla default. La verificacion queda en 164 tests backend en verde, WAR generado y build Angular OK.

- Spec 0 de fundacion documental y arquitectura: completado.
- Spec I1 de modelo base, carga inicial y completitud: completado.
- Spec I2 de busqueda operativa y vista previa certificable: completado (65 tests, build Angular OK).
- Spec I3 de generacion PDF, numeracion, firmante y trazabilidad: completado (78 tests, build Angular OK).
- Spec I4 de seguridad institucional, autorizacion y hardening: completado (99 tests, build Angular OK).
- Spec I5 de CRUD y mantenimiento operativo de ESAL: completado (131 tests backend, build Angular OK).
- Spec I6 de fidelidad del certificado PDF a la plantilla oficial: completado (136 tests backend, build Angular OK).
- Spec I7 de alineacion UI institucional SED_ESAL: completado (5 tests Angular, build Angular OK).
- Spec I8 de reproduccion exacta del certificado PDF desde plantilla EYRL: completado (137 tests backend, WAR OK, 5 tests Angular, build Angular OK).
- Spec I9 de gestion documental administrativa transversal: completado (148 tests backend, WAR OK, build Angular OK).
- Spec I10 de seleccion de plantilla EYRL por estado/documento vigente I9: completado (164 tests backend, WAR OK, build Angular OK).
- Pendiente: confirmar con TI SED tenant, issuer, audience, JWKS y CORS institucional para activar perfil weblogic.

## Artefactos Fuente

| Archivo | Uso |
|---|---|
| `Documentos_Referencia/BASE DE DATOS - REGISTRO_1.xlsx` | Base historica y estructura fuente |
| `Documentos_Referencia/Base excel.xlsx` | Diccionario de campos y obligatoriedad |
| `Documentos_Referencia/REGLAS (1).xlsx` | Reglas de negocio |
| `Documentos_Referencia/Maqueta Buscador.xlsx` | Maqueta funcional de busqueda |
| `Documentos_Referencia/Plantilla Certificado EYRL.docx` | Plantilla oficial de certificado |
| `Documentos_Referencia/Iteracion/Aplicativo ESAL.docx` | Requerimiento fuente I9 de gestion documental |
| `Documentos_Referencia/Iteracion/Plantilla Certificado EYRL.docx` | Plantilla default I10 |
| `Documentos_Referencia/Iteracion/Plantilla Certificado EYRL - ESAL Suspendida.docx` | Plantilla I10 para ESAL suspendida |
| `Documentos_Referencia/Iteracion/Plantilla Certificado EYRL - ESAL Estado de Liquidación.docx` | Plantilla I10 para ESAL en liquidacion por tramite |
| `Documentos_Referencia/Iteracion/Plantilla Certificado EYRL - ESAL Estado de Liquidacion por Termino de Duración.docx` | Plantilla I10 para ESAL en liquidacion por termino |
| `Documentos_Referencia/Iteracion/Plantilla Certificado EYRL - ESAL Cancelada Voluntariamente.docx` | Plantilla I10 para ESAL cancelada voluntariamente |
| `Documentos_Referencia/Iteracion/Plantilla Certificado EYRL - ESAL Cancelada Por Orden de Autoridad.docx` | Plantilla I10 para ESAL cancelada por orden de autoridad |
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
mvn test                    # 164 tests, BUILD SUCCESS
mvn package -DskipTests     # genera target/sed-esal-backend.war
mvn spring-boot:run -Dspring-boot.run.profiles=local-dev  # levanta en :8080

# Frontend
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-angular
node "C:\Program Files\nodejs\node_modules\npm\bin\npm-cli.js" run build
node "C:\Program Files\nodejs\node_modules\npm\bin\npm-cli.js" test -- --watch=false --browsers=ChromeHeadless  # validar fuera del sandbox/watch si aplica
node "C:\Program Files\nodejs\node_modules\npm\bin\npm-cli.js" start
```

## Proximo Orden De Trabajo

1. Mantener execution log, commits y push al finalizar cada bloque de trabajo.
2. Mantener `Documentos_Referencia/` fuera del repositorio salvo aprobacion explicita de publicacion.
3. Cerrar decisiones tecnicas diferidas antes del incremento que las requiera: activacion Azure AD/WebLogic institucional, vigencia tecnica de organo de administracion y posibles reglas futuras de verificacion externa.
4. Actualizar `docs/ARCHITECTURE.md` si la seccion de seguridad confirmada por SED cambia esta base.

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
