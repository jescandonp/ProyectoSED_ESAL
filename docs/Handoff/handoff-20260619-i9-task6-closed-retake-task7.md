# Handoff I9 - Task 6 Cerrada / Retake Task 7

Fecha: 2026-06-19.

## Estado

I9 tiene backend documental, descarga, bloqueo de estados y seguridad de endpoint multipart verificados.

## Evidencia

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-backend
mvn test "-Dtest=SecurityConfigTest,DocumentoSoporteServiceTest"
```

Resultado observado:

- 23 tests.
- 0 failures.
- BUILD SUCCESS.
- Advertencias H2 de cierre de base al finalizar JVM, sin fallo Maven.

## Retake

Continuar con Task 7:

1. Extender modelos/API Angular para `DocumentoSoporteDto`.
2. Agregar seccion o tab `Documentos` en mantenimiento ESAL.
3. Mostrar version vigente e historico consultable.
4. Permitir carga solo a ADMINISTRADOR.
5. Permitir consulta y descarga a ADMINISTRADOR/EXPEDIDOR.
6. Ejecutar build Angular.

## Archivos Relevantes

- `sed-esal-angular/src/app/features/esales/esal-maintenance.component.ts`
- `sed-esal-angular/src/app/services/esal-api.service.ts`
- `sed-esal-angular/src/app/models/`
- `sed-esal-backend/src/main/java/co/gov/bogota/sed/esal/controller/EsalController.java`
- `sed-esal-backend/src/test/java/co/gov/bogota/sed/esal/security/SecurityConfigTest.java`
