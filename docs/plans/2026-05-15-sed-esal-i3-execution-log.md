# Execution Log I3 - Generacion PDF, Numeracion, Firmante Y Trazabilidad

> Spec: `docs/specs/2026-05-15-sed-esal-i3-spec.md`
> Plan: `docs/plans/2026-05-15-sed-esal-i3-plan.md`
> Estado: aprobado como incremento futuro, pendiente de implementacion.
> Fecha: 2026-05-15.

## Resumen

Se crea la especificacion y el plan de I3 para cubrir la expedicion PDF desde preview validada, numeracion unica, firmante configurable por vigencia, almacenamiento, hash, descarga y auditoria completa.

## Registro

| Fecha | Evento | Resultado |
|---|---|---|
| 2026-05-15 | Creacion de Spec I3 | Define alcance, dominio, API, UI, reglas y criterios de aceptacion |
| 2026-05-15 | Creacion de Plan I3 | Define tareas T1-T12 y gates de calidad |
| 2026-05-15 | Actualizacion documental | README, ARRANQUE y guia de pruebas deben apuntar a I3 |

## Decisiones De Arranque Aprobadas

- I3 no inicia hasta completar I1/I2.
- La numeracion usa formato `<PREFIJO>-<AAAA>-<CONSECUTIVO_6_DIGITOS>` con prefijo inicial `ESAL`.
- El firmante se configura por vigencia y el certificado conserva copia de nombre/cargo usados.
- La herramienta tecnica de conversion DOCX a PDF se cerrara con prueba de concepto antes de implementar I3.
- El almacenamiento definitivo de PDFs se cierra antes de implementar I3; mientras tanto el contrato sera abstraido.
- Los numeros consumidos ante fallos tecnicos no se reutilizan salvo decision institucional posterior de anulacion.
- La seguridad ampliada de `docs/ARCHITECTURE.md` queda aprobada como referencia; lineamientos adicionales de SED se incorporaran antes de implementar si aplican.
