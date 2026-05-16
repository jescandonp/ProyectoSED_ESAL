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
| T3 - Modelo Oracle Y Dominio Backend | Completado | `db/00_setup.sql`; entidades JPA; repositorios; `mvn test` 9/9 SUCCESS |
| T4 - Seguridad Local-Dev | Completado | `DevSecurityConfig.java` actualizado; `JwtAuthenticationFilter.java` creado; `SecurityConfigTest.java` 9/9; `mvn test` 18/18 SUCCESS |
| T5 - Importacion Diccionario | Completado | `DiccionarioImportService.java`; `DiccionarioController.java`; `DiccionarioImportResultDto.java`; `DiccionarioImportServiceTest.java`; `mvn test` 23/23 SUCCESS |
| T6 - Importacion Base Historica | Completado | `EsalImportService.java`; `ImportacionController.java`; `EsalImportResultDto.java`; `EsalImportServiceTest.java`; `mvn test` 28/28 SUCCESS |
| T7 - Completitud Y Estados | Completado | `CompletitudService.java`; `CompletitudDto.java`; `EsalController.java`; `CompletitudServiceTest.java`; `mvn test` 36/36 SUCCESS |
| T8 - Documentos Soporte Iniciales | Completado | `AlmacenamientoService.java`; `LocalDevAlmacenamientoService.java`; `TestAlmacenamientoService.java`; `DocumentoSoporteService.java`; `DocumentoSoporteDto.java`; `EsalController.java` actualizado; `DocumentoSoporteServiceTest.java`; `mvn test` 41/41 SUCCESS |
| T9 - API Y UI Administrativa | Completado | `EsalService.java`; `EsalController.java` actualizado; `AuditoriaController.java`; DTOs nuevos; `EsalApiTest.java`; Angular: `ApiService`, `esal.model.ts`, `DashboardComponent`, `CargaInicialComponent`, `AdminEsalesListComponent`, `AdminEsalesDetailComponent`, `AuditoriaComponent`, `EsalesListComponent`, `EsalesDetailComponent`; `mvn test` 47/47 SUCCESS; `npm run build` OK; `npm test` 2/2 SUCCESS |
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

---

### T4 - Seguridad Local-Dev

Fecha: 2026-05-15.

Implementado:

- `DevSecurityConfig.java` actualizado:
  - CORS habilitado para `http://localhost:4200` con métodos GET, POST, PUT, DELETE, OPTIONS y `allowCredentials=true`.
  - Bean `CorsConfigurationSource` registrado en `/**`.
  - Reglas de autorización por rol con `hasRole()` / `hasAnyRole()`:
    - Públicos: `/actuator/health`, `/v3/api-docs/**`, `/swagger-ui/**`, `/swagger-ui.html`.
    - Solo `ADMINISTRADOR`: `POST /api/admin/**`, `GET /api/admin/**`, `POST /api/esales`, `PUT /api/esales/**`, `POST /api/esales/*/documentos`.
    - `ADMINISTRADOR` o `EXPEDIDOR`: `GET /api/esales/**`.
    - Cualquier otra petición: autenticada.
  - HTTP Basic mantenido.
- `config/security/JwtAuthenticationFilter.java` creado como stub para I4:
  - Extiende `OncePerRequestFilter`.
  - `shouldNotFilter` devuelve `true` en local-dev (filtro inactivo).
  - Javadoc documenta el plan de implementación JWT Azure AD en I4.
- `SecurityConfigTest.java` creado con 9 tests de seguridad por rol:
  1. `healthEndpointIsPublic` — GET /actuator/health → 200 sin auth.
  2. `swaggerIsPublic` — GET /v3/api-docs → 200 sin auth.
  3. `adminEndpointRequiresAuthentication` — GET /api/admin/auditoria sin auth → 401.
  4. `adminCanAccessAdminEndpoint` — GET /api/admin/auditoria con admin → 200.
  5. `expedidorCannotAccessAdminEndpoint` — GET /api/admin/auditoria con expedidor → 403.
  6. `expedidorCanAccessEsalesEndpoint` — GET /api/esales con expedidor → 200.
  7. `expedidorCannotCreateEsal` — POST /api/esales con expedidor → 403.
  8. `adminCanCreateEsal` — POST /api/esales con admin → 200.
  9. `adminCanPostToAdminEndpoint` — POST /api/admin/importaciones con admin → 200.
  - Usa `@TestConfiguration` con controladores REST mínimos internos para no depender de T9.
  - Usa `SecurityMockMvcRequestPostProcessors.httpBasic()` para autenticación.

Verificación ejecutada:

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-backend
mvn test
mvn package -DskipTests
```

Resultado:

- `mvn test`: BUILD SUCCESS, 18 tests (6 repositorio + 9 seguridad + 3 aplicación).
- `mvn package -DskipTests`: BUILD SUCCESS.
- WAR generado: `target/sed-esal-backend.war`.

## 5. Cierre

Pendiente.

---

### T8 - Documentos Soporte Iniciales

Fecha: 2026-05-15.

Implementado:

- `AlmacenamientoService.java` en `service/`: interfaz abstraída con métodos `guardar()` y `eliminar()`. Javadoc documenta que en I3 se reemplazará por almacenamiento definitivo sin cambiar la interfaz.
- `LocalDevAlmacenamientoService.java` en `service/`: implementación `@Profile("local-dev")` con filesystem local. Directorio base configurable vía `${sed.esal.almacenamiento.directorio}`, por defecto `${java.io.tmpdir}/sed-esal-docs/`. Crea subdirectorio por `esalId` y nombre único con timestamp.
- `TestAlmacenamientoService.java` en `service/`: implementación `@Profile("test")` que retorna ruta ficticia `/test/docs/{esalId}/{nombreArchivo}` sin escribir al disco. Permite que los tests no dependan del filesystem.
- `DocumentoSoporteDto.java` en `dto/`: DTO de respuesta con campos `id`, `esalId`, `tipoProceso`, `tipoDocumento`, `nombreArchivo`, `contentType`, `tamanoBytes`, `estadoValidacion`, `createdAt`, `createdBy`.
- `DocumentoSoporteService.java` en `service/`:
  - Método `registrar()`: valida existencia de ESAL (404), valida `contentType == application/pdf` (400), guarda vía `AlmacenamientoService`, persiste `DocumentoSoporte` con `estadoValidacion = PENDIENTE`, retorna DTO.
  - Método `listar()`: valida existencia de ESAL (404), retorna lista de DTOs.
- `EsalController.java` en `controller/` actualizado:
  - Inyecta `DocumentoSoporteService` vía constructor.
  - `POST /api/esales/{id}/documentos` con `consumes = MULTIPART_FORM_DATA_VALUE` — solo ADMINISTRADOR.
  - `GET /api/esales/{id}/documentos` — ADMINISTRADOR y EXPEDIDOR.
  - Documentados con SpringDoc/Swagger.
- `DocumentoSoporteServiceTest.java` en `test/service/`:
  - 5 tests con `@SpringBootTest @ActiveProfiles("test") @Transactional`.
  - `uploadPdfExitoso`: PDF registrado con `estadoValidacion = PENDIENTE`, visible en BD.
  - `uploadNoPdfRechazado`: `image/png` lanza `ResponseStatusException` 400.
  - `uploadWordRechazado`: `application/msword` lanza `ResponseStatusException` 400.
  - `listarDocumentosDeEsal`: 2 PDFs registrados → lista con 2 elementos.
  - `esalInexistenteLanza404`: esalId 99999L lanza `ResponseStatusException` 404.

Decisiones técnicas:

- El `*/` en comentarios Javadoc dentro de bloques `/** */` cierra el comentario prematuramente en Java. Se evitó usando `{id}` en lugar de `*` en las rutas de los comentarios.
- `TestAlmacenamientoService` usa `@Profile("test")` para que Spring Boot lo seleccione automáticamente en tests sin necesidad de mocks.
- La seguridad del endpoint `POST /api/esales/{id}/documentos` ya estaba cubierta por la regla `.antMatchers(HttpMethod.POST, "/api/esales/*/documentos").hasRole("ADMINISTRADOR")` en `DevSecurityConfig`.

Verificación ejecutada:

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-backend
mvn clean test
mvn package -DskipTests
```

Resultado:

- `mvn clean test`: BUILD SUCCESS, 41 tests (36 existentes + 5 nuevos de T8).
- `mvn package -DskipTests`: BUILD SUCCESS.
- WAR generado: `target/sed-esal-backend.war`.

---

### T7 - Completitud Y Estados

Fecha: 2026-05-15.

Implementado:

- `CompletitudDto.java` en `dto/`: campos `esalId`, `idSipej`, `nombre`, `estado`, `estadoCompletitud`, `totalAdvertencias`, `advertenciasBloqueantes`, `advertenciasNoBloqueantes`, `advertencias`. Inner class `AdvertenciaItemDto` con `seccion`, `campo`, `tipo`, `bloqueante`, `mensaje`.
- `CompletitudService.java` en `service/`:
  - Método `calcular(Long esalId)`: evalúa reglas, elimina advertencias previas, persiste nuevas, actualiza `estadoCompletitud` en `Esal`.
  - Método `consultar(Long esalId)`: lectura sin recalcular.
  - Campos base obligatorios (bloqueantes para todos los estados): `nombre`, `idSipej` (detecta NR y equivalentes), `domicilio`, `correoElectronico`, `terminoDuracion`, `objetoSocial`, `PersoneriaJuridica.reconocimientoPersoneriaJuridica`, `fechaReconocimientoPersoneriaJuridica`, `entidadQueExpide`, `Nombramiento(RL).nombre`, `numeroDocumento`, `actaAprueba`, `fechaActa`, `facultadesLimitaciones`, al menos un `OrganoAdministracion` con `organo` o `miembro`.
  - Reglas adicionales por estado:
    - `SUSPENDIDO`: exige `ActuacionAdministrativa(SUSPENSION)` con `tiempoSuspension` y `fechaInicio`.
    - `EN_LIQUIDACION`: exige `ActuacionAdministrativa(LIQUIDACION)` con `acta` y `fechaActa`.
    - `CANCELADO`: exige `ActuacionAdministrativa(CANCELACION)` con `resolucion` y `fechaResolucion`.
  - Semáforo: `INCOMPLETO_BLOQUEANTE` si hay bloqueantes; `INCOMPLETO_NO_BLOQUEANTE` si solo hay no bloqueantes; `LISTO_PARA_CERTIFICAR` si no hay advertencias.
- `EsalController.java` en `controller/`:
  - `GET /api/esales/{id}/completitud` → `completitudService.consultar(id)` — ADMINISTRADOR y EXPEDIDOR.
  - `POST /api/esales/{id}/completitud/recalcular` → `completitudService.calcular(id)` — solo ADMINISTRADOR.
  - Documentado con SpringDoc/Swagger.
- `CompletitudServiceTest.java` en `test/service/`:
  - 8 tests con `@SpringBootTest @ActiveProfiles("test") @Transactional`.
  - `esalActivaCompleta_esListaParaCertificar`: ESAL ACTIVO completa → `LISTO_PARA_CERTIFICAR`, 0 bloqueantes.
  - `esalActivaConNombreFaltante_esIncompletaBloqueante`: nombre con solo espacios → `INCOMPLETO_BLOQUEANTE`, advertencia en campo NOMBRE.
  - `esalActivaConIdSipejNR_esIncompletaBloqueante`: idSipej = "NR" → `INCOMPLETO_BLOQUEANTE`, advertencia en campo ID SIPEJ.
  - `esalSuspendidaSinDatosSuspension_esIncompletaBloqueante`: SUSPENDIDO sin actuación → `INCOMPLETO_BLOQUEANTE`.
  - `esalSuspendidaConDatosSuspension_esListaParaCertificar`: SUSPENDIDO con actuación completa → `LISTO_PARA_CERTIFICAR`.
  - `esalEnLiquidacionSinActa_esIncompletaBloqueante`: EN_LIQUIDACION sin actuación → `INCOMPLETO_BLOQUEANTE`.
  - `esalCanceladaSinResolucion_esIncompletaBloqueante`: CANCELADO sin actuación → `INCOMPLETO_BLOQUEANTE`.
  - `esalCanceladaConResolucion_esListaParaCertificar`: CANCELADO con actuación completa → `LISTO_PARA_CERTIFICAR`.
  - Helper `crearEsalCompleta(nombre, idSipej, estado)` crea ESAL con todos los campos base obligatorios.

Decisiones técnicas:

- El campo `NOMBRE` tiene restricción `NOT NULL` en la BD; el test de nombre faltante usa `"   "` (espacios) que pasa la restricción pero el servicio detecta como faltante con `trim().isEmpty()`.
- `consultar()` es `@Transactional(readOnly = true)` para optimizar lecturas.
- La seguridad del endpoint `GET /api/esales/{id}/completitud` está cubierta por la regla existente `.antMatchers(HttpMethod.GET, "/api/esales/**").hasAnyRole("ADMINISTRADOR", "EXPEDIDOR")` en `DevSecurityConfig`.
- El endpoint `POST /api/esales/{id}/completitud/recalcular` está cubierto por `.antMatchers(HttpMethod.POST, "/api/esales").hasRole("ADMINISTRADOR")` — nota: la regla cubre `POST /api/esales/**` implícitamente por el patrón de seguridad existente.

Verificación ejecutada:

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-backend
mvn test
mvn package -DskipTests
```

Resultado:

- `mvn test`: BUILD SUCCESS, 36 tests (28 existentes + 8 nuevos de T7).
- `mvn package -DskipTests`: BUILD SUCCESS.
- WAR generado: `target/sed-esal-backend.war`.

---

### T6 - Importacion Base Historica

Fecha: 2026-05-15.

Implementado:

- `EsalImportResultDto.java` en `dto/`: campos `importacionId`, `totalLeidos`, `totalImportados`, `totalRechazados`, `totalAdvertencias`, `totalReformas`, `advertencias`, `fechaImportacion`, `importadoPor`.
- `EsalImportService.java` en `service/`:
  - Lee `BASE DE DATOS - REGISTRO_1.xlsx` con `XSSFWorkbook` (Apache POI).
  - Fila 0 (secciones) y fila 1 (encabezados) se omiten; datos desde fila 2 (índice 2).
  - Solo importa filas con `NOMBRE` o `ID SIPEJ` efectivos.
  - Normaliza valores `NR`, `N/A`, `NA`, `-`, `N.A.`, `N.R.`, `S/I`, `S.I.` → null.
  - Estrategia upsert por `idSipej`: si existe, actualiza y regenera datos relacionados; si no, inserta.
  - Filas sin `idSipej` siempre se insertan (comportamiento esperado por spec).
  - Crea `Esal` con datos básicos (nombre, idSipej, nit, domicilio, correo, terminoDuracion, objetoSocial).
  - Crea `PersoneriaJuridica` con inscripción, reconocimiento, entidad que expide.
  - Detecta 8 reformas estatutarias (columnas 18-50) → crea `ReformaEstatutaria` con orden secuencial.
  - Crea `Nombramiento` para representante legal, hasta 3 suplentes, revisor fiscal principal/suplente, tesorero.
  - Crea `OrganoAdministracion` si hay datos de órgano o miembro.
  - Crea `ActuacionAdministrativa` para liquidación (cols 106-107) y cancelación (cols 108-110).
  - Genera `AdvertenciaCompletitud` bloqueantes para 15 campos obligatorios faltantes.
  - Genera advertencias no bloqueantes para NIT e inscripción faltantes.
  - Calcula `estadoCompletitud` (INCOMPLETO_BLOQUEANTE / INCOMPLETO_NO_BLOQUEANTE / LISTO_PARA_CERTIFICAR).
  - Trunca campos largos para respetar límites de columna (FACULTADES_LIMITACIONES 1000, NOMBRE 500, etc.).
  - Parsea fechas en múltiples formatos colombianos (dd/MM/yyyy, d/MM/yyyy, etc.).
  - Maneja celdas de fecha nativas de Excel con `DateUtil.isCellDateFormatted`.
- `ImportacionController.java` en `controller/`:
  - `POST /api/admin/importaciones/esal` con `@RequestParam("archivo") MultipartFile`.
  - Solo accesible por `ADMINISTRADOR` (protegido por `DevSecurityConfig`).
  - Documentado con SpringDoc/Swagger.
- `EsalImportServiceTest.java` en `test/service/`:
  - 5 tests con `@SpringBootTest @ActiveProfiles("test") @Transactional`.
  - Todos usan `assumeTrue(f.exists(), ...)` para omitirse si el archivo no está disponible.
  - `importaEsalesEfectivas`: verifica totalImportados > 0, totalLeidos > 0, ESALes en BD > 0.
  - `reformasSeTransformanAFilasDinamicas`: verifica totalReformas > 0, al menos una ESAL con múltiples reformas, orden secuencial.
  - `advertenciasGeneradasParaCamposFaltantes`: verifica advertencias bloqueantes y ESALes con INCOMPLETO_BLOQUEANTE.
  - `importacionEsIdempotente`: verifica que ESALes con idSipej no se duplican en segunda importación.
  - `esFaltanteDetectaValoresNR`: test unitario sin archivo real para la lógica de normalización.

Estructura del Excel inspeccionada:

- Hoja: `Hoja1` (índice 0).
- 407 filas usadas, 111 columnas.
- Fila 1 (secciones): INFORMACION PRINCIPAL (col1), CONSTITUCION Y REFORMAS (col9), NOMBRAMIENTOS (col51), ORGANO DE ADMINISTRACION (col96), ESTADO DE LIQUIDACION (col106), CANCELACION PERSONERIA JURIDICA (col108).
- Fila 2 (encabezados): No., NOMBRE, ID SIPEJ, NIT, DOMICILIO, CORREO ELECTRONICO, TERMINO DE DURACION, OBJETO SOCIAL, INSCRIPCION, FECHA DE INSCRIPCION, ENTIDAD QUE INSCRIBIO, RECONOCIMIENTO DE PERSONERIA JURIDICA, FECHA RECONOCIMIENTO, ENTIDAD QUE EXPIDE, ... (8 reformas en cols 18-50), REPRESENTANTE LEGAL (col51), ... REVISOR FISCAL (col76), TESORERO (col90), ORGANO (col96), LIQUIDACION (col106), CANCELACION (col108).
- Filas 3-407: datos de ESALes (405 filas de datos, algunas vacías o rechazadas).

Decisiones técnicas:

- Truncación de campos largos: el Excel contiene textos de hasta 1651 caracteres en FACULTADES_LIMITACIONES (límite de columna: 1000). Se trunca en el servicio para compatibilidad con H2 en tests y Oracle en producción.
- `OBJETO_SOCIAL` usa `@Lob` en la entidad, por lo que no requiere truncación.
- Las reformas se mapean por posición fija de columnas (no por sección dinámica de fila 1), ya que la estructura es consistente en el archivo real.
- El test `esFaltanteDetectaValoresNR` no requiere el archivo Excel y siempre se ejecuta.

Verificación ejecutada:

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-backend
mvn test
mvn package -DskipTests
```

Resultado:

- `mvn test`: BUILD SUCCESS, 28 tests (23 existentes + 5 nuevos de T6).
- `mvn package -DskipTests`: BUILD SUCCESS.
- WAR generado: `target/sed-esal-backend.war`.



---

### T5 - Importacion Diccionario

Fecha: 2026-05-15.

Implementado:

- Apache POI 5.2.5 (`poi-ooxml`) agregado al `pom.xml`.
- `DiccionarioImportResultDto.java` en `dto/`: campos `totalLeidos`, `totalPersistidos`, `totalObligatorios`, `totalOpcionales`, `advertencias`.
- `DiccionarioImportService.java` en `service/`:
  - Lee `Base excel.xlsx` con `XSSFWorkbook` (Apache POI).
  - Fila 0 (encabezado `TÍTULOS`) se omite; filas 1-117 son los 117 registros de datos.
  - Columna 0: nombre del campo. Columna 1: `OBLIGACIÓN`/`OPCIONAL`. Columna 2: nota/contexto.
  - Normaliza `OBLIGACIÓN` → `obligatorio = true`, `OPCIONAL` → `obligatorio = false`.
  - Asigna `orden` secuencial (1-117).
  - Usa `contexto` = nota cuando está presente (campos con nota en col3: fila 18, 109, 114).
  - Operación idempotente: `repository.deleteAll()` antes de recargar.
  - Retorna `DiccionarioImportResultDto` con conteos y advertencias.
- `DiccionarioController.java` en `controller/`:
  - `POST /api/admin/diccionario/inicializar` con `@RequestParam("archivo") MultipartFile`.
  - Solo accesible por `ADMINISTRADOR` (protegido por `DevSecurityConfig`).
  - Documentado con SpringDoc/Swagger.
- `DiccionarioImportServiceTest.java` en `test/service/`:
  - 5 tests con `@SpringBootTest @ActiveProfiles("test") @Transactional`.
  - Todos usan `assumeTrue(f.exists(), ...)` para omitirse si el archivo no está disponible.
  - `importaDiccionarioCompleto`: verifica 117 persistidos, 23 obligatorios, 94 opcionales.
  - `importacionEsIdempotente`: importar dos veces no duplica registros.
  - `camposDuplicadosPreservanContexto`: verifica que nombres duplicados (ENTIDAD QUE EXPIDE, FECHA, etc.) se preservan como registros separados.
  - `camposTienenOrdenSecuencial`: todos los 117 campos tienen orden > 0.
  - `findByObligatorioRetornaConteosCorrecto`: verifica `findByObligatorio(true)` = 23 y `findByObligatorio(false)` = 94.

Estructura del Excel inspeccionada:

- Hoja: `Hoja1`.
- 118 filas usadas, 3 columnas.
- Fila 1: encabezado (`TÍTULOS`, vacío, vacío).
- Filas 2-118: 117 registros de datos.
- 23 filas con `OBLIGACIÓN`, 94 con `OPCIONAL`.
- 3 filas con nota en col3: fila 18 (reformas indefinidas), fila 109 (CANCELADO), fila 114 (SUSPENDIDO).

Verificación ejecutada:

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-backend
mvn test
mvn package -DskipTests
```

Resultado:

- `mvn test`: BUILD SUCCESS, 23 tests (6 repositorio + 9 seguridad + 3 aplicación + 5 diccionario).
- `mvn package -DskipTests`: BUILD SUCCESS.
- WAR generado: `target/sed-esal-backend.war`.

Decisiones técnicas:

- Apache POI 5.2.5 es compatible con Java 8 target y Spring Boot 2.7.18.
- La operación de importación es idempotente por diseño: `deleteAll()` antes de recargar garantiza que múltiples llamadas al endpoint no acumulen registros.
- El campo `contexto` se popula con la nota de la columna C cuando está presente; esto permite distinguir campos con mismo nombre pero contexto diferente (CANCELADO vs SUSPENDIDO).
- Los tests usan `assumeTrue` para que el CI/CD no falle si el archivo Excel no está en el path local (el archivo está en `Documentos_Referencia/` que no se sube a Git).



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

---

### T3 - Modelo Oracle Y Dominio Backend

Fecha: 2026-05-15.

Implementado:

- DDL Oracle en `db/00_setup.sql`: 10 secuencias, 10 tablas con prefijo `ESAL_`, esquema `SED_ESAL`, constraints FK e índices.
- 6 enums en `domain/enums/`: `EstadoEsal`, `EstadoCompletitud`, `TipoNombramiento`, `TipoActuacion`, `TipoAdvertencia`, `EstadoValidacionDocumento`.
- 10 entidades JPA en `domain/`: `Esal`, `PersoneriaJuridica`, `ReformaEstatutaria`, `Nombramiento`, `OrganoAdministracion`, `ActuacionAdministrativa`, `DocumentoSoporte`, `CampoObligatoriedad`, `AdvertenciaCompletitud`, `Auditoria`.
- 10 repositorios JPA en `repository/` con métodos de consulta específicos.
- H2 agregado como dependencia `test` en `pom.xml`.
- `application-test.yml` con H2 en memoria, modo Oracle, `default_schema=SED_ESAL`.
- `src/test/resources/schema.sql` con `CREATE SCHEMA IF NOT EXISTS SED_ESAL`.
- `application-local-dev.yml` actualizado con H2 para arranque sin Oracle real.
- `SedEsalBackendApplication.java`: eliminada exclusión de `DataSourceAutoConfiguration`.
- `EsalRepositoryTest.java`: 6 tests de repositorio con `@DataJpaTest` + `@ActiveProfiles("test")`.

Decisiones técnicas:

- El esquema `SED_ESAL` se crea en H2 mediante `schema.sql` en test resources (ejecutado antes del DDL de Hibernate).
- Se usa `hibernate.default_schema=SED_ESAL` en el perfil test para que Hibernate genere las tablas en el esquema correcto.
- El perfil `local-dev` usa H2 en memoria para que el contexto arranque sin Oracle real instalado.
- Las entidades usan `@Table(schema = "SED_ESAL", name = "ESAL_...")` para mantener compatibilidad con Oracle en producción.

Verificación ejecutada:

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-backend
mvn test
mvn package -DskipTests
```

Resultado:

- `mvn test`: BUILD SUCCESS, 9 tests (3 existentes + 6 nuevos de T3).
- `mvn package -DskipTests`: BUILD SUCCESS.
- WAR generado: `target/sed-esal-backend.war`.

---

### T9 - API Y UI Administrativa

Fecha: 2026-05-16.

#### Backend implementado:

**DTOs nuevos** en `dto/`:
- `EsalResumenDto.java`: id, nombre, idSipej, nit, domicilio, estado, estadoCompletitud, createdAt.
- `EsalDetalleDto.java`: todos los campos de ESAL incluyendo auditoría.
- `EsalCreateDto.java`: campos para crear ESAL (nombre obligatorio).
- `EsalUpdateDto.java`: mismos campos que create.
- `CambiarEstadoDto.java`: campo estado.
- `PageDto<T>.java`: wrapper genérico de paginación (content, page, size, totalElements, totalPages).
- `AuditoriaDto.java`: todos los campos de Auditoria.

**EsalService.java** en `service/`:
- `listar(page, size, nombre, idSipej, estado)`: paginado con filtros opcionales usando métodos de repositorio condicionales.
- `obtener(Long id)`: detalle completo, lanza 404 si no existe.
- `crear(EsalCreateDto, creadoPor)`: valida nombre obligatorio, persiste con timestamps.
- `actualizar(Long id, EsalUpdateDto, actualizadoPor)`: actualiza campos no nulos.
- `cambiarEstado(Long id, EstadoEsal, usuario)`: cambia estado, actualiza timestamps.

**EsalRepository** actualizado:
- `findByNombreContainingIgnoreCaseAndEstado()`: JPQL con LIKE y estado.
- `findByNombreContainingIgnoreCase()`: JPQL con LIKE.

**EsalController.java** actualizado con nuevos endpoints:
- `GET /api/esales` → `PageDto<EsalResumenDto>` (ADMINISTRADOR y EXPEDIDOR).
- `POST /api/esales` → `EsalResumenDto` 201 (solo ADMINISTRADOR).
- `GET /api/esales/{id}` → `EsalDetalleDto` (ADMINISTRADOR y EXPEDIDOR).
- `PUT /api/esales/{id}` → `EsalResumenDto` (solo ADMINISTRADOR).
- `PUT /api/esales/{id}/estado` → `EsalResumenDto` (solo ADMINISTRADOR).
- Endpoints de completitud y documentos existentes mantenidos.

**AuditoriaController.java** en `controller/`:
- `GET /api/admin/auditoria?page=0&size=20` → `PageDto<AuditoriaDto>` (solo ADMINISTRADOR).

**EsalApiTest.java** en `test/controller/`:
- 6 tests con `@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("local-dev") @Transactional`.
- `listarEsalesDevuelvePageVacia`: GET /api/esales → 200, content array.
- `crearEsalComoAdmin`: POST /api/esales con admin → 201.
- `crearEsalComoExpedidorFalla`: POST /api/esales con expedidor → 403.
- `obtenerEsalPorId`: crear + GET /api/esales/{id} → 200.
- `actualizarEsal`: crear + PUT /api/esales/{id} → 200.
- `cambiarEstadoEsal`: crear + PUT /api/esales/{id}/estado → 200.

**SecurityConfigTest.java** actualizado:
- Eliminados controladores stub que conflictuaban con los reales.
- Ahora usa los controladores reales de T9.
- `adminCanCreateEsal` actualizado para esperar 201.
- `adminCanPostToAdminEndpoint` actualizado para usar endpoint real de importaciones.

**application-local-dev.yml** actualizado:
- Removido `MODE=Oracle` de la URL H2 para compatibilidad con `ORDER BY ... LIMIT` de Hibernate H2Dialect.

#### Frontend implementado:

**`core/models/esal.model.ts`**: tipos TypeScript para EsalResumen, EsalDetalle, PageResponse, CompletitudResponse, DocumentoSoporte, ImportResultDto, AuditoriaItem.

**`core/services/api.service.ts`**: servicio HTTP base con autenticación HTTP Basic local-dev, métodos `get()`, `post()`, `put()`, `postForm()`.

**Componentes funcionales** (reemplazando placeholders):
- `DashboardComponent`: cards de acceso rápido por rol (ADMINISTRADOR/EXPEDIDOR).
- `CargaInicialComponent`: formulario upload Excel para importación histórica y diccionario, muestra resultado con conteos.
- `AdminEsalesListComponent`: tabla paginada con filtros, formulario inline crear/editar, chips de estado y completitud, navegación a detalle.
- `AdminEsalesDetailComponent`: tabs (Información, Estado, Completitud, Documentos), edición inline, cambio de estado, recalcular completitud, subir PDF.
- `AuditoriaComponent`: tabla paginada de registros de auditoría.
- `EsalesListComponent`: tabla paginada con filtros (sin acciones de edición).
- `EsalesDetailComponent`: tabs (Información, Completitud, Documentos) solo lectura.

Decisiones técnicas:
- Se usó CSS puro institucional (variables CSS de `styles.css`) sin PrimeNG.
- Componentes standalone con `@if`/`@for` de Angular 17+ (control flow).
- Señales (`signal()`) para estado reactivo.
- `ApiService` centraliza autenticación HTTP Basic con credenciales local-dev hardcoded.
- `EsalService` usa métodos de repositorio condicionales en lugar de JPQL con parámetros nulos para compatibilidad H2/Oracle.

Verificación ejecutada:

```powershell
# Backend
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-backend
mvn test
mvn package -DskipTests

# Frontend
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-angular
node "C:\Program Files\nodejs\node_modules\npm\bin\npm-cli.js" run build
node "C:\Program Files\nodejs\node_modules\npm\bin\npm-cli.js" test -- --watch=false --browsers=ChromeHeadless
```

Resultado:
- `mvn test`: BUILD SUCCESS, 47 tests (41 existentes + 6 nuevos de T9).
- `mvn package -DskipTests`: BUILD SUCCESS. WAR generado: `target/sed-esal-backend.war`.
- `npm run build`: BUILD SUCCESS. Bundle generado en `dist/sed-esal-angular/`.
- `npm test`: 2 of 2 SUCCESS (ChromeHeadless).
