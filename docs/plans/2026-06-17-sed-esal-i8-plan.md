# I8 Certificado EYRL Exacto - Implementation Plan

> Estado: especificado para ejecucion despues de aprobacion.
> Spec: `docs/specs/2026-06-17-sed-esal-i8-spec.md`.
> Metodologia: SDD Spec-Anchored.

## Goal

Ajustar la generacion PDF para reproducir fielmente la plantilla EYRL oficial, cerrando los GAPs de pagina, tipografia, footer, orden de secciones, tablas y verificacion automatizada.

## Architecture

Se mantiene la arquitectura I6:

```text
GeneracionService
  -> CertificadoAssembler
  -> CertificadoPdfService
  -> byte[] PDF
```

I8 concentra el cambio en `CertificadoPdfService` y en tests de PDF. `CertificadoNarrativoDto` solo se ampliara si hace falta para soportar textos juridicos existentes sin inventar datos.

## Tasks

- [x] Task 1: Capturar contrato verificable de plantilla EYRL
  - Acceptance: documentar en el execution log los tokens finales: pagina, margenes, fuentes, tablas, footer y orden de secciones.
  - Verify: script/inspeccion DOCX reproducible con salida resumida en el log.
  - Files: `docs/plans/2026-06-17-sed-esal-i8-execution-log.md`.

- [x] Task 2: RED tests para fidelidad I8
  - Acceptance: `CertificadoPdfServiceTest` falla por version `I8-EYRL-v1`, footer, tabla de revisoria de 3 columnas, `Atentamente,` y orden textual.
  - Verify: `mvn test "-Dtest=CertificadoPdfServiceTest"`.
  - Files: `sed-esal-backend/src/test/java/co/gov/bogota/sed/esal/service/CertificadoPdfServiceTest.java`.

- [x] Task 3: Ajustar pagina, fuentes, margenes y footer
  - Acceptance: PDF usa Letter, margenes equivalentes y footer institucional de la plantilla.
  - Verify: `mvn test "-Dtest=CertificadoPdfServiceTest"`.
  - Files: `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/service/CertificadoPdfService.java`.

- [x] Task 4: Reordenar secciones y formulas juridicas
  - Acceptance: el texto extraido sigue el orden DOCX: objeto, representacion, funciones, asamblea, funciones, junta, funciones, revisoria, duracion, cierre, atentamente, firmante.
  - Verify: test de orden por indices sobre texto extraido.
  - Files: `CertificadoPdfService.java`, `CertificadoPdfServiceTest.java`.

- [x] Task 5: Ajustar tablas por tipo de bloque
  - Acceptance: representacion y junta usan 5 columnas; revisoria fiscal usa 3 columnas; Asamblea conserva tratamiento narrativo segun plantilla.
  - Verify: test PDF estructural con texto y, si es viable, inspeccion de contenido por cabeceras.
  - Files: `CertificadoPdfService.java`, `CertificadoPdfServiceTest.java`.

- [x] Task 6: Resolver campos no normalizados sin inventar datos
  - Acceptance: articulos estatutarios y funciones usan texto disponible; si no existe dato, aparece marcador controlado y documentado.
  - Verify: tests con DTO completo y DTO con faltantes.
  - Files: `CertificadoPdfService.java`, opcional `CertificadoNarrativoDto.java`.

- [x] Task 7: Regression backend completa
  - Acceptance: backend completo verde.
  - Verify:
    ```powershell
    Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-backend
    mvn test
    mvn package -DskipTests
    ```
  - Files: ninguno adicional salvo fixes derivados.

- [x] Task 8: Documentacion de cierre I8
  - Acceptance: README, ARRANQUE, GUIA_PRUEBAS_FUNCIONALES y execution log indican I8 completado y evidencias.
  - Verify: lectura de docs y `git diff --check`.
  - Files: `README.md`, `docs/ARRANQUE.md`, `docs/GUIA_PRUEBAS_FUNCIONALES.md`, execution log.

- [x] Task 9: Regression Angular de cierre
  - Acceptance: build/test Angular pasan o queda limitacion ambiental documentada.
  - Verify:
    ```powershell
    Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-angular
    node "C:\Program Files\nodejs\node_modules\npm\bin\npm-cli.js" test -- --watch=false --browsers=ChromeHeadless
    node "C:\Program Files\nodejs\node_modules\npm\bin\npm-cli.js" run build
    ```
  - Files: ninguno esperado.

## Risks

| Risk | Mitigation |
|---|---|
| "Exacto" puede implicar pixel-perfect no garantizable con OpenPDF | Convertirlo en contrato verificable: pagina, margenes, tablas, footer, textos y orden |
| Articulos estatutarios no normalizados | No inventar; usar texto disponible o marcador controlado |
| Arial puede no estar disponible igual en todos los hosts | Usar FontFactory y validar render/texto; documentar fallback si aplica |
| Footer puede competir con NOTA 1 | Separar footer institucional fijo de nota legal del certificado |
| Cambios PDF pueden romper tests por extraccion de texto con saltos | Diseñar asserts tolerantes a saltos de linea, pero estrictos en contenido y orden |

## Verification Checkpoints

1. RED confirmado antes de tocar servicio.
2. Test PDF I8 verde.
3. Suite backend completa verde.
4. Documentacion de cierre actualizada.
5. Angular build/test de regresion.

## Retake Point

I8 completado. Siguiente incremento debe abrir nueva SPEC/plan antes de tocar codigo.
