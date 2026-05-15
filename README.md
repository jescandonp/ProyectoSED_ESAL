# SED_ESAL

Aplicativo interno para la Direccion de Inspeccion y Vigilancia de la Secretaria de Educacion del Distrito orientado a administrar informacion de ESAL con fines educativos y soportar la expedicion de certificados de existencia y representacion legal.

## Estado

Fase actual: I1 en ejecucion bajo Spec-Driven Development (SDD), nivel Spec-Anchored.

El backend base `sed-esal-backend` ya existe como bootstrap Spring Boot WAR para I1. El frontend aun no se ha creado. El repositorio contiene la base documental, PRD, arquitectura propia del proyecto, specs por incremento, planes de implementacion y guia funcional de pruebas.

## Orden De Lectura

1. `docs/CONSTITUTION.md`
2. `docs/ARCHITECTURE.md`
3. `docs/TECNOLOGIAS.md`
4. `docs/DESIGN.md`
5. `docs/ARRANQUE.md`
6. `docs/specs/2026-05-09-sed-esal-certificados-prd.md`
7. `docs/specs/2026-05-15-sed-esal-i0-spec.md`
8. `docs/specs/2026-05-15-sed-esal-i1-spec.md`
9. `docs/specs/2026-05-15-sed-esal-i2-spec.md`
10. `docs/specs/2026-05-15-sed-esal-i3-spec.md`
11. `docs/specs/2026-05-15-sed-esal-i4-spec.md`
12. `docs/GUIA_PRUEBAS_FUNCIONALES.md`

## Incrementos Especificados

| Incremento | Estado | Foco |
|---|---|---|
| I0 | Cerrado | Base documental, arquitectura y metodologia |
| I1 | En ejecucion | Modelo base, carga inicial, estados y completitud |
| I2 | Aprobado futuro | Busqueda operativa y vista previa certificable |
| I3 | Aprobado futuro | Generacion PDF, numeracion, firmante y trazabilidad |
| I4 | Aprobado futuro | Seguridad institucional, autorizacion y hardening |

## Coordenadas Canonicas

| Elemento | Valor |
|---|---|
| Sistema | `SED_ESAL` |
| Backend | `sed-esal-backend` |
| Frontend | `sed-esal-angular` |
| WAR | `sed-esal-backend.war` |
| Contexto WebLogic | `/sed-esal` |
| Paquete Java base | `co.gov.bogota.sed.esal` |
| Esquema Oracle | `SED_ESAL` |
| Prefijo Oracle | `ESAL_` |

## Referencias Locales

Los archivos fuente del area usuaria se mantienen localmente en `Documentos_Referencia/`. Por cautela, esos archivos no se versionan inicialmente en GitHub hasta confirmar politica de publicacion o privacidad.

## Backend I1

El bootstrap backend se encuentra en `sed-esal-backend`.

Comandos de verificacion:

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-backend
mvn test
mvn package -DskipTests
```

Resultados esperados:

- Tests de arranque local-dev pasan.
- Health expone `/actuator/health`.
- OpenAPI expone `BearerAuth`.
- El WAR se genera como `target/sed-esal-backend.war`.

## Flujo De Actualizacion Del Repo

```powershell
git status --short --branch
git add README.md .gitignore docs Documentos_Referencia/README.md
git commit -m "docs: add SED_ESAL specification baseline"
git push -u origin main
```

Para cambios posteriores:

```powershell
git status --short --branch
git add docs README.md
git commit -m "docs: update SED_ESAL specs"
git push
```
