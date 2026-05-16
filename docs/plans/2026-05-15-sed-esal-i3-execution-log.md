# Execution Log I3 - Generacion PDF, Numeracion, Firmante Y Trazabilidad

> Spec: `docs/specs/2026-05-15-sed-esal-i3-spec.md`
> Plan: `docs/plans/2026-05-15-sed-esal-i3-plan.md`
> Estado: completado. 78 tests backend. Build Angular OK.
> Fecha: 2026-05-15.

## Resumen

Se crea la especificacion y el plan de I3 para cubrir la expedicion PDF desde preview validada, numeracion unica, firmante configurable por vigencia, almacenamiento, hash, descarga y auditoria completa.

## Registro

| Fecha | Evento | Resultado |
|---|---|---|
| 2026-05-15 | Creacion de Spec I3 | Define alcance, dominio, API, UI, reglas y criterios de aceptacion |
| 2026-05-15 | Creacion de Plan I3 | Define tareas T1-T12 y gates de calidad |
| 2026-05-15 | Actualizacion documental | README, ARRANQUE y guia de pruebas deben apuntar a I3 |
| 2026-05-16 | T1 Entidades dominio I3 | EstadoCertificado, Certificado, NumeracionCertificado, Firmante |
| 2026-05-16 | T1 pom.xml OpenPDF 1.3.42 | Dependencia para generacion programatica de PDF |
| 2026-05-16 | T2 Repositorios I3 | CertificadoRepository, NumeracionCertificadoRepository, FirmanteRepository |
| 2026-05-16 | T3 DTOs I3 | CertificadoDto, NumeracionDto, FirmanteDto, FirmanteCreateDto, NumeracionUpdateDto |
| 2026-05-16 | T4 NumeracionService | Consecutivo anual, REQUIRES_NEW transaccional, formato ESAL-AAAA-000001 |
| 2026-05-16 | T5 FirmanteService | CRUD, validacion solapamiento, resolverVigente por fecha |
| 2026-05-16 | T5 CertificadoPdfService | Generacion programatica PDF con OpenPDF: secciones, estado, firmante, pie |
| 2026-05-16 | T6 GeneracionService | Orquestacion: preview->firmante->numero->PDF->hash->almacenamiento->persistencia |
| 2026-05-16 | T7 Auditoria I3 | Eventos: SOLICITADA, BLOQUEADO, GENERADO, FALLIDA, DESCARGADO |
| 2026-05-16 | T8 CertificadoController | POST generar, GET obtener, GET descargar, GET historial |
| 2026-05-16 | T8 NumeracionController | GET /api/admin/certificados/numeracion, PUT actualizar prefijo |
| 2026-05-16 | T8 FirmanteController | GET listar, POST crear, PUT actualizar/activar/inactivar |
| 2026-05-16 | Tests NumeracionServiceTest | 4 tests: formato, incremento, prefijo |
| 2026-05-16 | Tests FirmanteServiceTest | 5 tests: crear, solapamiento, resolverVigente, inactivar |
| 2026-05-16 | Tests GeneracionServiceTest | 4 tests: generar completo, doble numero, sin firmante, historial |
| 2026-05-16 | 78 tests, BUILD SUCCESS | Todos los tests pasan correctamente |
| 2026-05-16 | T9 Angular esal.model.ts | Tipos I3: EstadoCertificado, CertificadoDto, NumeracionDto, FirmanteDto |
| 2026-05-16 | T9 preview-certificado | Boton Generar habilitado, confirmacion previa, navegacion a resultado |
| 2026-05-16 | T9 resultado-certificado | Pantalla resultado con numero, hash, firmante, enlace descarga |
| 2026-05-16 | T9 historial-certificados | Historial de certificados por ESAL |
| 2026-05-16 | T10 firmantes.component | CRUD firmantes admin con validacion solapamiento |
| 2026-05-16 | T10 numeracion.component | Configuracion prefijo y consulta consecutivo |
| 2026-05-16 | T10 app.routes.ts | Rutas: /certificados/:id, /esales/:id/certificados, /admin/firmantes, /admin/numeracion |
| 2026-05-16 | T10 shell.component | Nav: Firmantes y Numeracion para ADMINISTRADOR |
| 2026-05-16 | Build Angular | ng build completado sin errores criticos |

## Decisiones De Arranque Aprobadas

- I3 no inicia hasta completar I1/I2.
- La numeracion usa formato `<PREFIJO>-<AAAA>-<CONSECUTIVO_6_DIGITOS>` con prefijo inicial `ESAL`.
- El firmante se configura por vigencia y el certificado conserva copia de nombre/cargo usados.
- La herramienta tecnica de conversion DOCX a PDF se cerrara con prueba de concepto antes de implementar I3.
- El almacenamiento definitivo de PDFs se cierra antes de implementar I3; mientras tanto el contrato sera abstraido.
- Los numeros consumidos ante fallos tecnicos no se reutilizan salvo decision institucional posterior de anulacion.
- La seguridad ampliada de `docs/ARCHITECTURE.md` queda aprobada como referencia; lineamientos adicionales de SED se incorporaran antes de implementar si aplican.
