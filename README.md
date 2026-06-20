# SED_ESAL

Aplicativo interno para la Direccion de Inspeccion y Vigilancia de la Secretaria de Educacion del Distrito orientado a administrar informacion de ESAL con fines educativos y soportar la expedicion de certificados de existencia y representacion legal.

## Estado

Fase actual: I10 completado bajo Spec-Driven Development (SDD), nivel Spec-Anchored.

`SED_ESAL` cuenta con backend Spring Boot WAR y frontend Angular para busqueda, preview, generacion de certificados, seguridad por roles, mantenimiento operativo de ESAL, gestion documental administrativa transversal con version vigente e historico consultable, y seleccion de plantilla EYRL por estado/documento vigente. La carga Excel se conserva como mecanismo inicial o esporadico; el mantenimiento posterior se realiza desde el aplicativo por secciones administrativas.

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
14. `docs/specs/2026-05-29-sed-esal-i7-spec.md`
15. `docs/specs/2026-06-17-sed-esal-i8-spec.md`
16. `docs/specs/2026-06-19-sed-esal-i9-spec.md`
17. `docs/specs/2026-06-20-sed-esal-i10-spec.md`
18. `docs/GUIA_PRUEBAS_FUNCIONALES.md`

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
| I7 | Completado | Alineacion UI institucional SED_ESAL |
| I8 | Completado | Reproduccion exacta del certificado PDF desde plantilla EYRL |
| I9 | Completado | Gestion documental administrativa transversal |
| I10 | Completado | Seleccion de plantilla EYRL por estado y documento vigente I9 |

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

- Tests backend pasan (164 tests en I10).
- Build Angular pasa; el runner de tests Angular puede requerir ejecucion local fuera del sandbox/watch.
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
