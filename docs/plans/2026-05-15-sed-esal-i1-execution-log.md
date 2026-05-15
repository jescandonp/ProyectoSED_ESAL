# Execution Log I1 - Modelo Base, Carga Inicial, Estados Y Completitud

> Estado: aprobado para iniciar ejecucion.
> Fecha de apertura: 2026-05-15.
> Spec: `docs/specs/2026-05-15-sed-esal-i1-spec.md`.
> Plan: `docs/plans/2026-05-15-sed-esal-i1-plan.md`.

## 1. Contexto

I1 formaliza el primer incremento funcional de `SED_ESAL`. El proyecto aun no tiene backend/frontend implementados. Este log debe registrar decisiones, comandos, pruebas y resultados durante la ejecucion.

## 2. Estado De Tareas

| Tarea | Estado | Evidencia |
|---|---|---|
| T1 - Bootstrap Backend | Pendiente |  |
| T2 - Bootstrap Frontend | Pendiente |  |
| T3 - Modelo Oracle Y Dominio Backend | Pendiente |  |
| T4 - Seguridad Local-Dev | Pendiente |  |
| T5 - Importacion Diccionario | Pendiente |  |
| T6 - Importacion Base Historica | Pendiente |  |
| T7 - Completitud Y Estados | Pendiente |  |
| T8 - Documentos Soporte Iniciales | Pendiente |  |
| T9 - API Y UI Administrativa | Pendiente |  |
| T10 - Auditoria Y Documentacion | Pendiente |  |

## 3. Decisiones De Arranque Aprobadas

- Coordenadas tecnicas aprobadas: `sed-esal-backend`, `sed-esal-angular`, `sed-esal-backend.war`, `/sed-esal`, `co.gov.bogota.sed.esal`, esquema `SED_ESAL`, prefijo `ESAL_`.
- I0-I4 aprobados como base documental; I1 queda como incremento activo.
- Almacenamiento definitivo de documentos/PDFs queda diferido para I3; I1 implementa servicio abstraido con filesystem local-dev controlado.
- Carga historica en I1: upload web y lectura desde ruta local-dev para administrador tecnico.
- Registros historicos incompletos: importan con advertencias, pero bloquean certificacion si falta informacion obligatoria.
- `Documentos_Referencia/` permanece ignorado por Git salvo aprobacion explicita de publicacion.
- Rol `AUDITOR` queda fuera del MVP operativo inicial, pero el modelo de seguridad debe quedar extensible para I4.
- Azure AD real queda diferido para I4; I1 usa HTTP Basic solo en perfil `local-dev`.
- Conversion DOCX a PDF queda diferida para I3 con prueba de concepto tecnica previa.

## 4. Evidencia De Verificacion

Pendiente de ejecucion tecnica I1.

## 5. Cierre

Pendiente.
