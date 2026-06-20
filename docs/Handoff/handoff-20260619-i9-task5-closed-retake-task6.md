# Handoff I9 - Task 5 Cerrada / Retake Task 6

Fecha: 2026-06-19.

## Estado

I9 tiene implementada la gestion documental base, descarga autenticada y bloqueo de estados obligatorios por documento vigente.

## Evidencia

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-backend
mvn test "-Dtest=EsalMaintenanceServiceTest,DocumentoSoporteServiceTest"
```

Resultado observado:

- 24 tests.
- 0 failures.
- BUILD SUCCESS.
- Advertencias H2 de cierre de base al finalizar JVM, sin fallo Maven.

## Retake

Continuar con Task 6:

1. Actualizar endpoint multipart `POST /api/esales/{id}/documentos`.
2. Aceptar `tipoDocumento`, `subtipoDocumento`, `referencia`, `fechaActo`, `observacion` y archivo PDF.
3. Mantener `ADMINISTRADOR` para carga y permitir `EXPEDIDOR` para consulta/descarga.
4. Agregar/verificar pruebas de seguridad.

## Archivos Relevantes

- `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/controller/EsalController.java`
- `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/security/`
- `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/service/DocumentoSoporteService.java`
- `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/service/EsalMaintenanceService.java`
- `sed-esal-backend/src/test/java/co/gov/bogota/sed/esal/service/EsalMaintenanceServiceTest.java`
