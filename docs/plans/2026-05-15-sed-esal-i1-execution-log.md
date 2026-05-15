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
| T1 - Bootstrap Backend | Completado | `sed-esal-backend`; `mvn test`; `mvn package -DskipTests`; WAR `target/sed-esal-backend.war` |
| T2 - Bootstrap Frontend | Completado | `sed-esal-angular`; `npm run build` OK; `npm test` 2/2 SUCCESS |
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

### T1 - Bootstrap Backend

Fecha: 2026-05-15.

Implementado:

- Proyecto Maven `sed-esal-backend`.
- Spring Boot 2.7.18, Java target 1.8 y empaquetado WAR.
- Dependencias base: Web, Security, JPA, Actuator, SpringDoc OpenAPI, Oracle JDBC y Tomcat provided.
- Clase principal `co.gov.bogota.sed.esal.SedEsalBackendApplication` preparada para WAR.
- Perfil `local-dev`.
- Health endpoint disponible.
- Swagger/OpenAPI con esquema `BearerAuth`.
- Usuarios local-dev aprobados:
  - `admin@educacionbogota.edu.co` / `admin123` / `ADMINISTRADOR`
  - `expedidor@educacionbogota.edu.co` / `expedidor123` / `EXPEDIDOR`

Verificacion ejecutada:

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-backend
mvn test
mvn package -DskipTests
```

Resultado:

- `mvn test`: BUILD SUCCESS, 3 tests.
- `mvn package -DskipTests`: BUILD SUCCESS.
- WAR generado: `target/sed-esal-backend.war`.

Observacion de ambiente:

- La maquina actual ejecuta Maven con Java 21. El proyecto compila con `java.version=1.8`; se mantiene pendiente validar con Oracle JDK 8 antes de despliegue WebLogic.

## 5. Cierre

Pendiente.

---

### T2 - Bootstrap Frontend

Fecha: 2026-05-15.

Implementado:

- PrimeNG 20.4.0 y primeicons instalados como dependencias.
- `proxy.conf.json` configurado hacia `http://localhost:8080/api`.
- `angular.json` actualizado con `proxyConfig`.
- `src/index.html` actualizado: `lang="es"`, título institucional, Google Fonts Public Sans.
- `src/styles.css` con tokens visuales completos de `docs/DESIGN.md` (colores, tipografía, espaciado, componentes).
- `src/app/core/models/user.model.ts`: modelo `User` con rol `ADMINISTRADOR`/`EXPEDIDOR`.
- `src/app/core/auth/auth.service.ts`: servicio con login local-dev, usuarios hardcoded, signal y localStorage.
- `src/app/core/auth/auth.guard.ts`: `authGuard` y `adminGuard` funcionales.
- `src/app/features/login/`: `LoginComponent` con formulario reactivo, validación, estilo institucional SED.
- `src/app/shared/layout/shell.component.*`: layout con sidebar azul oscuro `#001e40`, navegación por rol, header, logout.
- Componentes placeholder con mensaje "En desarrollo - I1":
  - `DashboardComponent`
  - `CargaInicialComponent`
  - `AdminEsalesListComponent`, `AdminEsalesDetailComponent`
  - `AuditoriaComponent`
  - `EsalesListComponent`, `EsalesDetailComponent`
- `src/app/app.routes.ts`: rutas base con lazy loading, `authGuard`, `adminGuard`.
- `src/app/app.config.ts`: `provideHttpClient` agregado.
- `src/app/app.ts` y `app.html` limpiados (solo `<router-outlet />`).
- `src/app/app.spec.ts` actualizado para el nuevo componente simplificado.

Decisiones técnicas:

- PrimeNG 20.4.0 instalado (compatible con Angular 20.3). No se usó en T2 bootstrap; se usará en T9 para UI completa.
- Se usó CSS puro institucional (variables CSS) en lugar de Tailwind CSS para el bootstrap, por simplicidad de setup. Tailwind puede agregarse en T9 si se requiere.
- Analytics de Angular CLI deshabilitado globalmente (`%APPDATA%\@angular\config.json`) para evitar prompt interactivo en CI/scripts.

Verificación ejecutada:

```powershell
node "C:\Program Files\nodejs\node_modules\npm\bin\npm-cli.js" run build
node "C:\Program Files\nodejs\node_modules\npm\bin\npm-cli.js" test -- --watch=false --browsers=ChromeHeadless
```

Resultado:

- `npm run build`: BUILD SUCCESS. Bundle generado en `dist/sed-esal-angular/`.
- `npm test`: 2 of 2 SUCCESS (ChromeHeadless 148.0.0.0 Windows 10).
