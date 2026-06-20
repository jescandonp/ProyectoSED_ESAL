# Handoff - SED_ESAL I10 Cerrado

> Fecha: 2026-06-20.
> Estado: I10 completado.
> Retake recomendado: abrir SPEC I11 antes de implementar nuevo alcance.

## Resumen

I10 implemento seleccion de plantilla EYRL por estado de ESAL y documento vigente I9. Los estados/documentos especiales usan una variante explicita y los demas casos conservan la plantilla default.

## Cambios Principales

- Nuevo `CertificadoPlantilla` con versiones tecnicas I10.
- Nuevo `CertificadoTemplateSelector` para decidir plantilla desde `EstadoEsal` y documentos soporte vigentes.
- `CertificadoAssembler` integra documentos I9 y expone plantilla/metadatos documentales en `CertificadoNarrativoDto`.
- `CertificadoPdfService` renderiza bloques especificos para suspendida, liquidacion por tramite, liquidacion por termino, cancelada voluntariamente y cancelada por autoridad.
- `GeneracionService` persiste `plantillaVersion` desde la variante seleccionada.
- Documentacion I10 cerrada en README, ARRANQUE, guia funcional, plan y execution log.

## Evidencia

- `mvn test "-Dtest=CertificadoTemplateSelectorTest"`: 8 tests, BUILD SUCCESS.
- `mvn test "-Dtest=CertificadoTemplateSelectorTest,CertificadoAssemblerTest"`: 15 tests, BUILD SUCCESS.
- `mvn test "-Dtest=CertificadoPdfServiceTest"`: 7 tests, BUILD SUCCESS.
- `mvn test "-Dtest=CertificadoTemplateSelectorTest,CertificadoAssemblerTest,CertificadoPdfServiceTest,GeneracionServiceTest"`: 27 tests, BUILD SUCCESS.
- `mvn test "-Dtest=GeneracionServiceTest"`: 5 tests, BUILD SUCCESS.
- `mvn test`: 164 tests, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS.
- `mvn package -DskipTests`: BUILD SUCCESS, WAR `sed-esal-backend/target/sed-esal-backend.war`.
- Angular `npm run build`: BUILD SUCCESS con advertencias NG8102/NG8107 no bloqueantes ya conocidas.

## Retake

1. Leer `docs/ARRANQUE.md`.
2. Revisar `docs/specs/2026-06-20-sed-esal-i10-spec.md`.
3. Revisar `docs/plans/2026-06-20-sed-esal-i10-plan.md`.
4. Revisar `docs/plans/2026-06-20-sed-esal-i10-execution-log.md`.
5. Si se inicia nuevo alcance, crear primero SPEC I11, luego PLAN I11, y solo implementar tras aprobacion.

## Notas

- Mantener `Documentos_Referencia/` fuera de versionamiento salvo aprobacion explicita.
- El artefacto local `.claude/worktrees/practical-chatelet-a3bc4c` estaba presente antes del cierre y no fue modificado.
