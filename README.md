# SED_ESAL

Aplicativo interno para la Direccion de Inspeccion y Vigilancia de la Secretaria de Educacion del Distrito orientado a administrar informacion de ESAL con fines educativos y soportar la expedicion de certificados de existencia y representacion legal.

## Estado

Fase actual: I6 completado bajo Spec-Driven Development (SDD), nivel Spec-Anchored.

`SED_ESAL` cuenta con backend Spring Boot WAR y frontend Angular para busqueda, preview, generacion de certificados, seguridad por roles y mantenimiento operativo de ESAL. La carga Excel se conserva como mecanismo inicial o esporadico; el mantenimiento posterior se realiza desde el aplicativo por secciones administrativas.

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
12. `docs/specs/2026-05-21-sed-esal-i5-spec.md`
13. `docs/specs/2026-05-27-sed-esal-i6-spec.md`
14. `docs/GUIA_PRUEBAS_FUNCIONALES.md`

## Incrementos Especificados

| Incremento | Estado | Foco |
|---|---|---|
| I0 | Cerrado | Base documental, arquitectura y metodologia |
| I1 | Completado | Modelo base, carga inicial, estados y completitud |
| I2 | Completado | Busqueda operativa y vista previa certificable |
| I3 | Completado | Generacion PDF, numeracion, firmante y trazabilidad |
| I4 | Completado | Seguridad institucional, autorizacion y hardening |
| I5 | Completado | CRUD y mantenimiento operativo de ESAL |
| I6 | Completado | Fidelidad del certificado PDF a plantilla oficial |

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

## Verificacion Local

El backend se encuentra en `sed-esal-backend` y el frontend en `sed-esal-angular`.

Comandos de verificacion:

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-backend
mvn test
mvn package -DskipTests

Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-angular
node "C:\Program Files\nodejs\node_modules\npm\bin\npm-cli.js" test -- --watch=false --browsers=ChromeHeadless
node "C:\Program Files\nodejs\node_modules\npm\bin\npm-cli.js" run build
```

Resultados esperados:

- Tests backend pasan.
- Tests y build Angular pasan.
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
