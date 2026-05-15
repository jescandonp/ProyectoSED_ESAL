# Plan I0 - Fundacion Documental Y Arquitectura SED_ESAL

> Estado: ejecutable para cierre documental inicial.  
> Fecha: 2026-05-15.  
> Spec: `docs/specs/2026-05-15-sed-esal-i0-spec.md`.

## 1. Objetivo

Materializar los documentos rectores propios de `SED_ESAL` y dejar listo el proyecto para especificar I1 bajo SDD Spec-Anchored.

## 2. Tareas

1. Crear `docs/CONSTITUTION.md`.
2. Crear `docs/ARCHITECTURE.md`.
3. Crear `docs/TECNOLOGIAS.md`.
4. Crear `docs/ARRANQUE.md`.
5. Crear `docs/GUIA_PRUEBAS_FUNCIONALES.md`.
6. Crear `docs/specs/2026-05-15-sed-esal-i0-spec.md`.
7. Crear `docs/plans/2026-05-15-sed-esal-i0-plan.md`.
8. Verificar existencia de archivos.
9. Verificar ausencia de coordenadas SIGCON como valores canonicos de `SED_ESAL`.
10. Reportar preguntas abiertas para cierre tecnico.

## 3. Verificacion

Comandos sugeridos:

```powershell
Get-ChildItem C:\Users\jmep2\Downloads\SED\ProyectoESAL\docs
Get-ChildItem C:\Users\jmep2\Downloads\SED\ProyectoESAL\docs\specs
Get-ChildItem C:\Users\jmep2\Downloads\SED\ProyectoESAL\docs\plans
Select-String -Path C:\Users\jmep2\Downloads\SED\ProyectoESAL\docs\*.md -Pattern 'SED_ESAL|sed-esal|ESAL_'
```

## 4. Criterio De Cierre

I0 se considera cerrado cuando:

- Los documentos rectores existen.
- La spec I0 y plan I0 existen.
- El PRD vigente puede leerse junto con `CONSTITUTION`, `ARCHITECTURE` y `TECNOLOGIAS`.
- Las preguntas abiertas quedan identificadas para Spec I1 o decision de arquitectura.

## 5. Riesgos

- Las coordenadas tecnicas propuestas pueden cambiar por decision de infraestructura SED.
- El almacenamiento documental/PDF aun no esta definido.
- La integracion Azure AD queda diferida; no debe bloquear I1 local-dev.

