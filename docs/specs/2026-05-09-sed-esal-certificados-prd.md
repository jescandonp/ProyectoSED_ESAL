# PRD - SED_ESAL Certificados de Existencia y Representación Legal

> Estado: borrador funcional V2  
> Fecha base: 2026-05-09  
> Actualizado: 2026-05-15  
> Metodología: Spec-Driven Development (SDD) - Nivel Spec-Anchored  
> Sistema: `SED_ESAL`  
> Entidad solicitante: Dirección de Inspección y Vigilancia (DIV) - Secretaría de Educación del Distrito (SED)  
> Artefactos fuente: `BASE DE DATOS - REGISTRO_1.xlsx`, `Base excel.xlsx`, `REGLAS (1).xlsx`, `Maqueta Buscador.xlsx`, `Plantilla Certificado EYRL.docx`

## 1. Problem Statement

La Dirección de Inspección y Vigilancia de la Secretaría de Educación del Distrito necesita automatizar y agilizar la generación de certificados de existencia y representación legal de ESAL con fines educativos.

Actualmente la información fuente se encuentra en una base Excel amplia, con 117 entidades efectivas y 111 columnas, organizada por secciones funcionales. El archivo físico contiene más filas por formato, espacios y estructura visual, pero el universo inicial identificable para migración corresponde a los registros con datos de ESAL. El proceso depende de consulta manual, validación operativa de datos y elaboración del documento a partir de una plantilla Word, lo que genera riesgo de errores, reprocesos, baja trazabilidad y dificultad para controlar qué versión de la información fue usada en cada certificado expedido.

La información adicional entregada por el área usuaria precisa dos elementos que deben quedar incorporados al producto:

- Un diccionario de campos con obligatoriedad: 23 campos obligatorios y 94 opcionales.
- Reglas de negocio para estados de la ESAL, constitución, reformas, suspensión, cancelación y nombramientos vigentes.

El sistema debe resolver cinco frentes principales:

1. Alimentar, administrar y auditar la base de información de ESAL.
2. Permitir búsqueda interna de entidades por `ID SIPEJ` y nombre.
3. Validar completitud de la información requerida para expedición.
4. Gestionar estados de la ESAL y sus efectos sobre el certificado.
5. Generar certificados en PDF desde la plantilla oficial, usando datos validados y dejando traza completa.

## 2. Solution

Construir `SED_ESAL`, un aplicativo web interno SED para administrar la información de ESAL con fines educativos y expedir certificados de existencia y representación legal en PDF.

La solución debe permitir una carga inicial desde Excel y, después de la migración, administrar los registros mediante formularios de la aplicación. La información debe modelarse por secciones y relaciones, no como una copia plana del Excel, para reflejar la organización real de la base y las reglas de negocio:

- Información principal.
- Constitución y reformas.
- Nombramientos.
- Órgano de administración.
- Estado de liquidación.
- Cancelación de personería jurídica.
- Suspensión.
- Documentos soporte.

El sistema debe manejar los estados `Activo`, `Suspendido`, `En Liquidación` y `Cancelado`, aplicando reglas específicas sobre la visualización del certificado, alertas, leyendas y campos/documentos requeridos.

El sistema tendrá dos perfiles iniciales:

- Administrador: responsable de carga inicial, administración de la base de información, correcciones, histórico y configuración.
- Expedidor de Certificados: responsable de consultar entidades y generar certificados; no podrá modificar información.

La emisión será directa cuando los datos requeridos por el formato del certificado y por las reglas del estado vigente estén completos. Si un campo `NR`, vacío o inconsistente no es requerido por el formato ni por una regla activa, no bloqueará la expedición. Si corresponde a un dato obligatorio para el certificado, el sistema bloqueará la generación y mostrará un mensaje de advertencia claro.

## 3. User Stories

1. Como Administrador, quiero cargar la base inicial desde Excel, para migrar al sistema la información histórica de ESAL.

2. Como Administrador, quiero que el sistema valide la estructura del Excel, para detectar archivos con columnas faltantes, columnas no reconocidas o datos incompatibles.

3. Como Administrador, quiero ver el resultado de la carga inicial, para identificar registros importados, rechazados y registros con advertencias.

4. Como Administrador, quiero administrar la información por secciones, para trabajar de forma ordenada sobre los campos de la base y sus relaciones.

5. Como Administrador, quiero crear un nuevo registro ESAL desde formulario, para registrar entidades que no estén en la carga inicial.

6. Como Administrador, quiero editar información principal de una ESAL, para mantener actualizados nombre, `ID SIPEJ`, NIT, domicilio, correo, duración y objeto social.

7. Como Administrador, quiero editar información de constitución y reformas, para mantener resoluciones, fechas, entidades que expiden y reformas estatutarias.

8. Como Administrador, quiero editar información de nombramientos, para mantener representantes legales, suplentes, revisores fiscales y tesorero.

9. Como Administrador, quiero editar información del órgano de administración, para mantener miembros, cargos, actas y facultades.

10. Como Administrador, quiero registrar estado de liquidación o cancelación de personería jurídica, para reflejar la situación vigente de la entidad.

11. Como Administrador, quiero que cada cambio conserve histórico, para conocer usuario, fecha, campo modificado, valor anterior y valor nuevo.

12. Como Administrador, quiero consultar versiones históricas de una ESAL, para auditar qué información estaba vigente en una fecha determinada.

13. Como Expedidor, quiero buscar una ESAL por `ID SIPEJ`, para ubicar rápidamente una entidad.

14. Como Expedidor, quiero buscar una ESAL por nombre exacto o parcial, para encontrar entidades cuando no conozca el identificador.

15. Como Expedidor, quiero que la búsqueda permita coincidencias parciales y exactas tanto por `ID SIPEJ` como por nombre, para reducir fricción operativa.

16. Como Expedidor, quiero ver una pantalla de resultado con los datos que aparecerán en el certificado, para revisar la información antes de expedir.

17. Como Expedidor, quiero que el sistema me informe si faltan datos obligatorios del formato, para saber por qué no puedo generar el certificado.

18. Como Expedidor, quiero generar el certificado directamente cuando la información esté completa, para expedirlo sin flujo adicional de aprobación.

19. Como Expedidor, quiero descargar el certificado en PDF, para entregarlo o anexarlo al trámite correspondiente.

20. Como Expedidor, quiero que cada certificado tenga un número único, para identificarlo de forma inequívoca.

21. Como Administrador, quiero configurar el prefijo del número único de certificado, para ajustarlo a la convención institucional vigente.

22. Como Administrador, quiero configurar la información del firmante por vigencia, para que el certificado use el nombre y cargo correctos de la Dirección de Inspección y Vigilancia.

23. Como Auditor o Administrador, quiero consultar la traza de búsquedas, vistas previas, generaciones, descargas, errores y cambios, para controlar la operación completa.

24. Como Auditor o Administrador, quiero que la traza registre usuario, rol, fecha/hora, ESAL, `ID SIPEJ`, NIT, número de certificado, versión de datos usada, IP/equipo, resultado y hash del PDF, para reconstruir la expedición.

25. Como equipo técnico SED, quiero que el MVP use usuarios de desarrollo, pero deje preparada la integración con cuentas institucionales SED, para avanzar sin bloquear la arquitectura futura.

26. Como equipo técnico SED, quiero que el sistema siga la arquitectura tecnológica de referencia SED, para facilitar soporte, despliegue y evolución.

27. Como usuario interno SED, quiero una interfaz simple, compacta y administrativa, para operar el sistema sin complejidad innecesaria.

28. Como Administrador, quiero clasificar una ESAL por estado, para que el certificado aplique reglas distintas cuando esté activa, suspendida, en liquidación o cancelada.

29. Como Expedidor, quiero que el certificado de una ESAL suspendida muestre una alerta visible y el tiempo de suspensión, para reflejar la situación administrativa vigente.

30. Como Expedidor, quiero que el certificado de una ESAL en liquidación incluya la leyenda `EN LIQUIDACIÓN` junto al nombre y el párrafo informativo correspondiente, para cumplir la regla de negocio definida por la DIV.

31. Como Expedidor, quiero que el certificado de una ESAL cancelada muestre solo la información permitida y los datos de resolución de cancelación, para no expedir un certificado con información operativa no vigente.

32. Como Administrador, quiero registrar documentos soporte en PDF para constitución, reformas, suspensión, cancelación y dignatarios, para sustentar los actos registrados en el sistema.

33. Como Administrador, quiero que las reformas estatutarias se registren como una lista dinámica, para soportar ESAL con cualquier número de reformas sin limitarme a ocho columnas.

34. Como Administrador, quiero ver un semáforo de completitud para expedición, para saber si una ESAL está lista, incompleta no bloqueante o incompleta bloqueante.

35. Como Expedidor, quiero que la validación de generación combine campos obligatorios y reglas de estado, para evitar certificados contrarios a la información administrativa vigente.

## 4. Implementation Decisions

- El sistema se llamará técnicamente `SED_ESAL`.
- El aplicativo será interno SED. No habrá consulta pública externa en el MVP.
- La carga inicial será desde Excel.
- Después de la carga inicial, el mantenimiento de la información se hará por formularios de la aplicación.
- El identificador único funcional de la ESAL será `ID SIPEJ`.
- La base debe modelarse por secciones y relaciones, no como una única pantalla plana de 111 columnas.
- El CRUD administrativo debe cubrir todos los datos del Excel, organizados por secciones y listas dinámicas cuando corresponda.
- El archivo `Base excel.xlsx` será la fuente inicial de obligatoriedad de campos: 23 campos obligatorios y 94 opcionales.
- Las reformas estatutarias deben modelarse como relación 1:N, porque una ESAL puede tener un número indefinido de reformas.
- Los estados funcionales de una ESAL serán, como mínimo: `Activo`, `Suspendido`, `En Liquidación` y `Cancelado`.
- El sistema debe mantener un semáforo de completitud para expedición: `Listo para certificar`, `Incompleto no bloqueante` e `Incompleto bloqueante`.
- El Expedidor de Certificados solo podrá consultar y generar; no podrá editar datos.
- La emisión del certificado será directa, sin flujo de revisión o aprobación previa.
- La vista previa mostrará los datos que aparecen en el certificado.
- Los campos obligatorios para generación serán los requeridos por la plantilla/formato del certificado, el diccionario de obligatoriedad y las reglas de negocio aplicables al estado de la ESAL.
- Los valores `NR`, vacíos o inconsistentes bloquearán la generación solo cuando correspondan a campos obligatorios del certificado o a reglas activas del estado.
- La carga histórica desde Excel puede importar registros incompletos, pero debe marcarlos con estado de completitud y advertencias.
- Los registros nuevos y actualizaciones formales deben exigir documentos soporte cuando la regla de negocio lo indique.
- Los documentos soporte se cargarán en PDF.
- La constitución debe soportar documentos obligatorios: resolución de reconocimiento de personería jurídica, acta de constitución y aportes, estatutos, notificación y publicación, según la regla entregada por el área.
- Las reformas deben permitir documentos soporte: resolución de aprobación de reforma, estatutos, notificación y publicación.
- Las actuaciones de suspensión o cancelación deben permitir documentos soporte: resolución sancionatoria o de cancelación, notificación y constancia de firmeza.
- La inscripción de dignatarios debe permitir documento soporte y exigir oficio de aprobación de inscripción.
- Para ESAL en estado `Suspendido`, el sistema debe registrar tiempo de suspensión y mostrar alerta visible en el certificado.
- Para ESAL en estado `En Liquidación`, el certificado debe incluir la leyenda `EN LIQUIDACIÓN` junto al nombre y el párrafo informativo de disolución/liquidación.
- Para ESAL en estado `Cancelado`, el certificado debe limitar la información emitida y mostrar la resolución de cancelación y su fecha.
- Cada certificado tendrá un número único con regla de prefijo más consecutivo.
- La regla aprobada para número único es: `<PREFIJO>-<AAAA>-<CONSECUTIVO_6_DIGITOS>`.
- El prefijo debe ser configurable. Valor inicial sugerido: `ESAL`.
- Ejemplo de número: `ESAL-2026-000001`.
- El consecutivo debe reiniciarse por año, salvo que la DIV defina una regla institucional distinta.
- El sistema debe registrar el número único en la traza de expedición y asociarlo al PDF generado.
- La información del firmante debe ser configurable por vigencia, no fija en código ni fija permanentemente en la plantilla.
- La configuración de firmante debe permitir nombre, cargo, fecha de inicio de vigencia, fecha fin de vigencia y estado activo.
- El PDF no incluirá QR ni código público de verificación en el MVP.
- El diseño debe dejar previsto un incremento futuro para QR o código de verificación.
- La autenticación del MVP podrá operar con usuarios locales de desarrollo.
- La arquitectura debe dejar preparada la integración futura con cuentas institucionales SED.
- La referencia tecnológica será la arquitectura SED usada por SIGCON: Angular 20, TypeScript strict, PrimeNG 20, Tailwind CSS, Spring Boot 2.7.18, Java 8, Spring Security 5.7, Spring Data JPA, Oracle Database 19c+, empaquetado WAR y despliegue en Oracle WebLogic 12.2.1.4.
- El backend debe exponer Swagger/OpenAPI activo para facilitar validación de contratos.
- La base de datos objetivo será Oracle, con prefijo de tablas propio de `SED_ESAL`.
- El sistema debe mantener trazabilidad completa de operaciones relevantes.

## 5. Proposed Modules

### 5.1. Módulo de Carga Inicial

Responsable de importar la base Excel histórica, validar estructura y registrar resultados de carga.

Capacidades:

- Lectura del archivo Excel base.
- Validación de columnas esperadas.
- Transformación de filas en entidades de dominio.
- Detección de registros duplicados por `ID SIPEJ`.
- Reporte de registros importados, rechazados y con advertencias.
- Registro de evento de carga en auditoría.

### 5.2. Módulo de Gestión ESAL

Responsable de administrar los datos de una ESAL por secciones.

Secciones:

- Información principal.
- Constitución y reformas.
- Nombramientos.
- Órgano de administración.
- Estado de liquidación.
- Cancelación de personería jurídica.
- Suspensión.
- Documentos soporte.

Capacidades:

- Crear ESAL.
- Consultar ESAL.
- Editar secciones.
- Validar `ID SIPEJ` único.
- Mantener histórico de cambios.
- Consultar versión vigente.
- Consultar semáforo de completitud.
- Administrar estado vigente de la ESAL.

### 5.3. Módulo de Búsqueda

Responsable de permitir consulta operativa por `ID SIPEJ` y nombre de ESAL.

Capacidades:

- Búsqueda exacta por `ID SIPEJ`.
- Búsqueda parcial por `ID SIPEJ`.
- Búsqueda exacta por nombre.
- Búsqueda parcial por nombre.
- Resultados ordenados y legibles.
- Registro de trazas de búsqueda.

### 5.4. Módulo de Vista Previa de Certificado

Responsable de presentar al Expedidor los datos que serán usados en el certificado.

Capacidades:

- Construir la vista con base en la versión vigente de la ESAL.
- Mostrar campos requeridos por la plantilla.
- Marcar datos faltantes o inconsistentes.
- Bloquear generación cuando falten campos obligatorios.
- Registrar evento de vista previa.

### 5.5. Módulo de Generación PDF

Responsable de generar el certificado en PDF desde la plantilla oficial.

Capacidades:

- Mapear datos de ESAL hacia la plantilla.
- Incluir firmante vigente configurable.
- Generar número único de certificado.
- Generar PDF.
- Calcular hash del PDF.
- Registrar expedición exitosa o fallida.
- Entregar archivo para descarga.

### 5.6. Módulo de Numeración

Responsable de administrar la regla de número único.

Capacidades:

- Configurar prefijo.
- Generar consecutivo anual.
- Garantizar unicidad.
- Registrar número asignado.
- Evitar saltos por concurrencia en expediciones simultáneas, salvo anulaciones controladas.

### 5.7. Módulo de Firmantes

Responsable de administrar firmantes por vigencia.

Capacidades:

- Crear configuración de firmante.
- Activar o desactivar vigencias.
- Validar que exista un firmante vigente al generar certificado.
- Usar el firmante correcto según fecha de expedición.

### 5.8. Módulo de Auditoría y Trazabilidad

Responsable de registrar la operación completa.

Eventos mínimos:

- Carga inicial.
- Creación de ESAL.
- Edición de ESAL.
- Consulta de ESAL.
- Búsqueda.
- Vista previa.
- Generación PDF exitosa.
- Generación PDF fallida.
- Descarga de certificado.
- Error de validación.
- Cambio de configuración de firmante.
- Cambio de configuración de numeración.

Campos mínimos de traza:

- Usuario.
- Rol.
- Fecha y hora.
- Acción.
- ESAL.
- `ID SIPEJ`.
- NIT.
- Número de certificado, cuando aplique.
- Versión de datos usada.
- IP/equipo.
- Resultado.
- Detalle del error, cuando aplique.
- Hash del PDF, cuando aplique.

### 5.9. Módulo de Estados y Reglas de Negocio

Responsable de aplicar las reglas de negocio asociadas al estado de la ESAL y a los procesos administrativos.

Estados mínimos:

- `Activo`.
- `Suspendido`.
- `En Liquidación`.
- `Cancelado`.

Reglas:

- Si la ESAL está `Activo`, el certificado refleja la información registrada en el sistema.
- Si la ESAL está `Suspendido`, el sistema debe registrar tiempo de suspensión y el certificado debe mostrar alerta visible.
- Si la ESAL está `En Liquidación`, el certificado debe agregar `EN LIQUIDACIÓN` junto al nombre de la entidad.
- Si la ESAL está `En Liquidación`, el certificado debe incluir el párrafo: `LA ENTIDAD SE ENCUENTRA DISUELTA Y EN ESTADO DE LIQUIDACIÓN SEGÚN ACTA No. XX DE REUNIÓN (ÓRGANO DE ADMINISTRACIÓN) DE FECHA XX DE XXXX DE XXXX.`
- Si la ESAL está `Cancelado`, el certificado debe mostrar la información de constitución permitida y los datos de resolución de cancelación y fecha.
- Las reglas activas deben integrarse con la validación de completitud antes de generar certificado.

### 5.10. Módulo Documental

Responsable de registrar y validar documentos soporte en PDF asociados a procesos administrativos.

Procesos documentales:

- Constitución.
- Reforma estatutaria.
- Suspensión.
- Cancelación.
- Inscripción de dignatarios.

Documentos mínimos por regla:

- Constitución: resolución de reconocimiento de personería jurídica, acta de constitución y aportes, estatutos, notificación y publicación.
- Reforma estatutaria: resolución de aprobación de reforma, estatutos, notificación y publicación.
- Suspensión o cancelación: resolución sancionatoria o resolución de cancelación, notificación y constancia de firmeza.
- Dignatarios: oficio de aprobación de inscripción del dignatario.

Regla de transición:

- La carga histórica desde Excel podrá quedar sin documentos soporte completos, pero debe registrar advertencias y completitud documental.
- Los registros nuevos y actualizaciones formales posteriores a la puesta en marcha deben exigir los documentos soporte definidos para finalizar el proceso.

### 5.11. Módulo de Completitud para Expedición

Responsable de evaluar si una ESAL puede generar certificado.

Estados de completitud:

- `Listo para certificar`: no tiene faltantes bloqueantes.
- `Incompleto no bloqueante`: tiene faltantes opcionales o históricos que no impiden certificado.
- `Incompleto bloqueante`: faltan campos o documentos requeridos por el formato, el diccionario o una regla de estado.

Capacidades:

- Evaluar los 23 campos obligatorios del diccionario.
- Evaluar campos requeridos por la plantilla del certificado.
- Evaluar reglas adicionales por estado.
- Mostrar mensajes de bloqueo específicos por campo o documento.
- Permitir a Administrador consultar brechas por sección.

### 5.12. Módulo de Seguridad

Responsable de autenticación y autorización.

MVP:

- Usuarios locales de desarrollo.
- Roles Administrador y Expedidor.

Evolución:

- Integración con cuentas institucionales SED.
- Integración con Azure AD / Office 365.
- Validación JWT en backend.

## 6. Incremental Delivery Plan

### Incremento 1 - Base funcional para expedición

Objetivo: tener el flujo mínimo completo desde carga inicial hasta generación PDF, incorporando obligatoriedad de campos, estados básicos y validación de completitud.

Alcance:

- Carga inicial desde Excel.
- Persistencia de ESAL por secciones principales.
- Modelo relacional base para información principal, personería, reformas dinámicas, nombramientos, órgano de administración, actuaciones administrativas y documentos.
- Buscador por `ID SIPEJ` y nombre.
- Vista previa de datos del certificado.
- Validación de campos obligatorios según plantilla, diccionario y estado.
- Semáforo de completitud para expedición.
- Estados mínimos: `Activo`, `Suspendido`, `En Liquidación`, `Cancelado`.
- Reglas de certificado para estados `Suspendido`, `En Liquidación` y `Cancelado`.
- Registro inicial de documentos soporte y advertencias de completitud documental.
- Configuración básica de firmante.
- Número único con prefijo y consecutivo.
- Generación PDF desde plantilla.
- Trazabilidad básica de generación y errores.
- Usuarios locales de desarrollo con roles.

Criterio de éxito:

- Un Expedidor puede buscar una ESAL importada, revisar datos, ver si está lista o bloqueada para certificación, generar un PDF con número único cuando aplique y dejar traza auditable.

### Incremento 2 - Administración robusta de información

Objetivo: completar la gestión administrativa de todos los datos.

Alcance:

- CRUD completo de ESAL por todas las secciones.
- Histórico detallado de cambios.
- Validaciones por sección.
- Reporte de datos incompletos requeridos para certificado.
- Gestión completa de documentos soporte por proceso.
- Mejoras de UX para administrar los campos y relaciones sin saturar la pantalla.

Criterio de éxito:

- Un Administrador puede mantener la base sin depender del Excel después de la carga inicial.

### Incremento 3 - Auditoría operativa y reportes

Objetivo: fortalecer control, trazabilidad y consulta operativa.

Alcance:

- Consulta avanzada de logs.
- Filtros por usuario, fecha, ESAL, número de certificado y resultado.
- Reporte de certificados expedidos.
- Consulta de hash y versión de datos usada.
- Registro completo de búsquedas, vistas previas, descargas y errores.

Criterio de éxito:

- La DIV puede reconstruir qué ocurrió con cualquier certificado generado.

### Incremento 4 - Seguridad institucional

Objetivo: alinear autenticación y despliegue con entorno institucional SED.

Alcance:

- Integración con cuentas institucionales SED.
- Azure AD / Office 365.
- Roles definitivos.
- Hardening de seguridad.
- Ajustes de despliegue WebLogic/Oracle.

Criterio de éxito:

- El aplicativo opera con identidad institucional y controles de seguridad alineados a la arquitectura SED.

### Incremento 5 - Verificación externa futura

Objetivo: habilitar mecanismos de verificación del certificado si la DIV lo requiere.

Alcance candidato:

- Código QR.
- Código público de verificación.
- Página de validación de certificado.
- Control de vigencia del certificado.

Criterio de éxito:

- Un tercero autorizado puede verificar autenticidad del certificado sin exponer información no permitida.

## 7. Data Requirements

La base de datos debe cubrir la estructura del Excel de referencia, el diccionario de obligatoriedad y las reglas de negocio entregadas por el área usuaria. La normalización final se definirá en la especificación técnica, pero el modelo conceptual no debe ser una copia plana del Excel: debe separar entidades, listas dinámicas, estados, documentos y trazabilidad.

El diccionario de campos entregado en `Base excel.xlsx` identifica 117 definiciones: 23 obligatorias y 94 opcionales. Los campos duplicados del Excel, como `FECHA`, `ENTIDAD QUE EXPIDE`, `TIPO DE DOCUMENTO`, `NUMERO DE DOCUMENTO`, `ACTA APRUEBA` y otros, deben resolverse por contexto de sección o entidad hija, no por nombre de columna global.

### 7.0. Modelo Conceptual Base

Entidades conceptuales mínimas:

- `ESAL`: entidad principal identificada funcionalmente por `ID SIPEJ`.
- `PersoneriaJuridica`: reconocimiento, fecha y entidad que expide.
- `ReformaEstatutaria`: lista dinámica de reformas y actos relacionados.
- `Nombramiento`: representantes legales, suplentes, revisores fiscales, tesorero y dignatarios.
- `OrganoAdministracion`: órgano, miembros, cargos, actas, documentos y facultades.
- `ActuacionAdministrativa`: suspensión, liquidación y cancelación.
- `DocumentoSoporte`: archivo PDF asociado a un proceso o actuación.
- `Certificado`: número único, fecha, PDF, hash, versión de datos usada y estado de generación.
- `Firmante`: firmantes configurables por vigencia.
- `Auditoria`: trazas de uso, cambios y expedición.

### 7.1. Información Principal

Campos representativos:

- Número interno de registro.
- Nombre.
- `ID SIPEJ`.
- NIT.
- Domicilio.
- Correo electrónico.
- Término de duración.
- Objeto social.

### 7.2. Constitución y Reformas

Campos representativos:

- Inscripción.
- Fecha de inscripción.
- Entidad que inscribió.
- Reconocimiento de personería jurídica.
- Fecha de reconocimiento.
- Entidad que expide.
- Resoluciones aclaratorias.
- Resoluciones adicionadas.
- Reformas estatutarias.
- Fechas de reformas.
- Entidades que expiden.

Regla de modelado:

- Las reformas estatutarias no deben limitarse a ocho bloques fijos. Deben manejarse como una relación 1:N porque el área usuaria indicó que una ESAL puede tener un número indefinido de reformas.

### 7.3. Nombramientos

Campos representativos:

- Representante legal.
- Tipo y número de documento.
- Acta que aprueba.
- Fecha.
- Representantes legales suplentes.
- Facultades y limitaciones del representante legal.
- Revisoría fiscal.
- Revisor fiscal principal.
- Revisor fiscal suplente.
- Tarjeta profesional.
- Tesorero.
- Facultades y limitaciones del tesorero.

### 7.4. Órgano de Administración

Campos representativos:

- Órgano.
- Miembro.
- Cargo.
- Tipo y número de documento.
- Acta que aprueba.
- Fecha.
- Acta aclaratoria.
- Facultades y limitaciones del órgano de administración.

### 7.5. Estado de Liquidación y Cancelación

Campos representativos:

- Acta.
- Fecha.
- Resolución.
- Fecha de resolución.
- Motivo de cancelación.

### 7.6. Suspensión

Campos representativos:

- Acta.
- Fecha.
- Resolución.
- Fecha de resolución.
- Motivo de suspensión.
- Tiempo de suspensión.
- Fecha de inicio de suspensión.
- Fecha de fin de suspensión, si aplica.

Regla:

- El certificado de una ESAL suspendida debe mostrar alerta visible y tiempo de suspensión.

### 7.7. Documentos Soporte

Campos representativos:

- Tipo de proceso.
- Tipo documental.
- Nombre de archivo.
- Formato.
- Fecha de carga.
- Usuario que carga.
- Estado de validación.
- Relación con ESAL, reforma, nombramiento o actuación administrativa.

Reglas:

- Solo se aceptan documentos soporte en formato PDF para los procesos definidos.
- La carga histórica puede quedar con advertencia documental.
- Los registros nuevos o actualizaciones formales deben validar los documentos obligatorios antes de finalizar.

## 8. Certificate Requirements

El certificado debe generarse a partir de la plantilla oficial `Plantilla Certificado EYRL.docx`.

Secciones detectadas en la plantilla:

- Certificado de existencia y representación legal.
- Identificación de la Dirección de Inspección y Vigilancia.
- Datos generales de la ESAL.
- Personería jurídica y reformas estatutarias.
- Objeto social.
- Representación legal.
- Integrantes de representación legal.
- Funciones de la representación legal.
- Asamblea general.
- Funciones de la asamblea general.
- Junta directiva.
- Funciones de la junta directiva.
- Revisoría fiscal.
- Integrantes de revisoría fiscal.
- Duración.
- Fecha de expedición.
- Firmante.

Reglas:

- El certificado debe emitirse en PDF.
- El certificado debe incluir número único.
- El certificado debe usar el firmante vigente configurado.
- El certificado debe bloquearse si faltan datos obligatorios del formato.
- El certificado debe bloquearse si faltan datos obligatorios del diccionario que aplican al formato o a la regla de estado vigente.
- El certificado debe reflejar alertas o leyendas de estado cuando aplique.
- El certificado debe registrar hash del PDF generado.
- El certificado debe quedar asociado a la versión de datos usada.

## 9. Business Rules

### 9.1. Reglas de Estado

1. Si la ESAL está `Activo`, toda la información registrada en el aplicativo debe poder reflejarse en el certificado expedido, sujeto a la plantilla y a la información disponible.

2. Si la ESAL está `Suspendido`, el sistema debe registrar el tiempo de suspensión y generar una alerta visible.

3. Si la ESAL está `Suspendido`, el certificado debe reflejar la información de la ESAL e incluir el tiempo de suspensión y la alerta correspondiente.

4. Si la ESAL está `En Liquidación`, el certificado debe incluir la leyenda `EN LIQUIDACIÓN` junto al nombre de la entidad.

5. Si la ESAL está `En Liquidación`, el estado se activa cuando la entidad haya inscrito oficialmente al liquidador.

6. Si la ESAL está `En Liquidación`, el certificado debe incluir un párrafo informativo sobre disolución y liquidación: `LA ENTIDAD SE ENCUENTRA DISUELTA Y EN ESTADO DE LIQUIDACIÓN SEGÚN ACTA No. XX DE REUNIÓN (ÓRGANO DE ADMINISTRACIÓN) DE FECHA XX DE XXXX DE XXXX.`

7. Si la ESAL está `Cancelado`, el certificado debe mostrar la información permitida de constitución y los datos de resolución de cancelación y fecha.

### 9.2. Reglas de Constitución y Registro

1. Para registrar constitución de una ESAL, el sistema debe exigir documentos en PDF.

2. Los documentos obligatorios de constitución son: resolución de reconocimiento de personería jurídica, acta de constitución y aportes, estatutos, notificación y publicación.

3. El sistema no debe permitir finalizar un registro nuevo de constitución si faltan documentos obligatorios.

4. En carga histórica, los documentos faltantes se registran como advertencia y brecha documental, sin impedir importar el registro.

### 9.3. Reglas de Reforma Estatutaria

1. Para registrar reforma estatutaria, el sistema debe permitir el cargue de documentos soporte PDF.

2. Los documentos requeridos para reforma son: resolución de aprobación de reforma, estatutos, notificación y publicación.

3. Las reformas se deben registrar como lista dinámica, no como columnas fijas.

### 9.4. Reglas de Suspensión y Cancelación

1. Cuando una ESAL sea suspendida o cancelada, el sistema debe permitir el cargue de documentos soporte asociados a la actuación administrativa.

2. Los documentos requeridos para suspensión o cancelación son: resolución sancionatoria o resolución de cancelación, notificación y constancia de firmeza.

3. La suspensión debe registrar tiempo de suspensión para reflejarlo en el certificado.

4. La cancelación debe registrar acto administrativo de cancelación, resolución y fecha.

### 9.5. Reglas de Nombramientos Vigentes

1. Para aprobar la inscripción de un dignatario, el sistema debe permitir el cargue del documento soporte en PDF.

2. El documento obligatorio para inscripción de dignatarios es el oficio de aprobación de inscripción del dignatario.

3. El sistema no debe permitir finalizar la inscripción formal de dignatario sin el documento requerido.

## 10. Technical Architecture Reference

`SED_ESAL` debe alinearse a la arquitectura de referencia SED usada en SIGCON, tomando como base los documentos:

- `C:/Users/jmep2/Downloads/AgenIALab/ProyectoContratosSED/docs/ARCHITECTURE.md`
- `C:/Users/jmep2/Downloads/AgenIALab/ProyectoContratosSED/docs/TECNOLOGIAS.md`

Stack de referencia:

- Frontend: Angular 20, TypeScript strict, PrimeNG 20, Tailwind CSS 3.4, RxJS 7.8, Angular CDK 20.
- Backend: Java 8, Spring Boot 2.7.18, Spring Security 5.7, Spring Data JPA 2.7, Hibernate 5.6, SpringDoc OpenAPI 1.7.0.
- Base de datos: Oracle Database 19c+.
- Servidor objetivo: Oracle WebLogic 12.2.1.4.
- Empaquetado: WAR.
- Identidad futura: Azure AD / Office 365.
- Auth local MVP: usuarios de desarrollo.
- Swagger/OpenAPI: activo.

Decisiones cerradas en documentos técnicos:

- Contexto WebLogic: `/sed-esal`.
- Paquete Java base: `co.gov.bogota.sed.esal`.
- Prefijo definitivo de tablas Oracle: `ESAL_`.
- Esquema Oracle local/prod MVP: `SED_ESAL`.
- Modulos: `sed-esal-backend` y `sed-esal-angular`.
- Ubicacion definitiva de plantillas y PDFs generados: diferida para I3; I1 usara servicio de almacenamiento abstraido con filesystem local-dev controlado.

## 11. Testing Decisions

Las pruebas deben validar comportamiento observable, no detalles internos.

### 11.1. Pruebas de Carga Inicial

- Importa archivo Excel válido.
- Rechaza archivo sin columnas requeridas.
- Detecta registros duplicados por `ID SIPEJ`.
- Reporta filas con datos incompletos.
- Conserva secciones correctamente.

### 11.2. Pruebas de Gestión ESAL

- Crea ESAL con `ID SIPEJ` único.
- Bloquea duplicados de `ID SIPEJ`.
- Edita datos por sección.
- Registra histórico de cambios.
- Permite consultar versión vigente.
- Calcula semáforo de completitud.
- Cambia estado de ESAL y registra traza.

### 11.3. Pruebas de Búsqueda

- Busca por `ID SIPEJ` exacto.
- Busca por `ID SIPEJ` parcial.
- Busca por nombre exacto.
- Busca por nombre parcial.
- Registra evento de búsqueda.

### 11.4. Pruebas de Validación de Certificado

- Permite generación cuando campos obligatorios están completos.
- Bloquea generación cuando falta un campo obligatorio del formato.
- No bloquea por campos `NR` que no son requeridos por el formato.
- Muestra mensaje claro de advertencia.
- Bloquea por reglas de estado cuando falta información requerida.
- Marca como incompleto no bloqueante cuando el faltante es opcional.

### 11.5. Pruebas de Generación PDF

- Genera número único con prefijo, año y consecutivo.
- Genera PDF desde plantilla.
- Usa firmante vigente.
- Calcula hash del PDF.
- Registra versión de datos usada.
- Registra expedición exitosa.
- Registra error de generación.
- Incluye alerta de suspensión cuando aplica.
- Incluye leyenda `EN LIQUIDACIÓN` cuando aplica.
- Limita certificado cancelado a información permitida y datos de resolución.

### 11.6. Pruebas de Roles

- Administrador puede cargar y administrar datos.
- Expedidor puede buscar y generar.
- Expedidor no puede editar datos.

### 11.7. Pruebas de Auditoría

- Registra carga inicial.
- Registra cambios de información.
- Registra búsquedas.
- Registra vistas previas.
- Registra generaciones.
- Registra descargas.
- Registra errores.

### 11.8. Pruebas Documentales

- Valida que documentos soporte se carguen en PDF.
- Exige documentos de constitución para registros nuevos.
- Exige documentos de reforma para reformas nuevas.
- Exige documentos de suspensión o cancelación para actuaciones nuevas.
- Exige oficio de aprobación para inscripción de dignatario.
- Permite importar carga histórica con advertencias documentales.

### 11.9. Pruebas de Modelo Relacional

- Registra múltiples reformas para una misma ESAL.
- Registra múltiples nombramientos o dignatarios.
- Registra documentos soporte asociados a entidad, reforma, nombramiento o actuación.
- Mantiene histórico sin sobrescribir valores previos.

## 12. Out of Scope

Queda fuera del MVP:

- Consulta pública externa.
- Integración real con Azure AD / Office 365.
- Firma digital certificada.
- Código QR en el PDF.
- Código público de verificación.
- Página pública de validación.
- Migraciones desde fuentes distintas al Excel inicial.
- Flujos de aprobación previos a expedición.
- Rediseño jurídico o normativo del formato del certificado.
- Validación jurídica automática del contenido de estatutos o documentos PDF.

## 13. Open Questions

1. ¿Debe existir anulación de certificados expedidos?
2. ¿Los PDFs generados se almacenarán en base de datos, filesystem institucional o gestor documental?
3. ¿Debe conservarse también el DOCX intermedio o solo el PDF final?
4. ¿Qué vigencia legal tiene cada certificado expedido?
5. ¿Qué texto exacto debe mostrarse cuando falten campos obligatorios?
6. ¿La plantilla puede convertirse a una plantilla técnica con variables o debe mantenerse como DOCX base editable por negocio?
7. ¿Cuál será la lista cerrada de estados administrativos permitidos además de los cuatro iniciales?
8. ¿El tiempo de suspensión se registra como rango de fechas, número de días/meses o texto administrativo?
9. ¿Qué información exacta debe mostrarse en certificado para una ESAL cancelada?
10. ¿Cuál será la política de retención, consulta y descarga de documentos soporte?

Preguntas cerradas:

- Contexto WebLogic definitivo para MVP: `/sed-esal`.
- Prefijo Oracle de tablas: `ESAL_`.
- Esquema Oracle MVP: `SED_ESAL`.
- El consecutivo se reinicia anualmente salvo decision institucional posterior.
- La carga historica puede operar con semaforo de incompletitud; bloquea certificacion cuando falten campos obligatorios.

## 14. Acceptance Criteria for MVP

El MVP se considera aceptado cuando:

1. El sistema carga la base inicial desde el Excel de referencia.
2. Los registros quedan consultables por `ID SIPEJ` y nombre.
3. La información se visualiza por secciones.
4. La información se modela por entidades relacionadas y no como tabla plana.
5. Las reformas estatutarias se registran como lista dinámica.
6. Un Administrador puede crear y editar registros desde formulario.
7. Un Expedidor no puede editar registros.
8. El sistema calcula semáforo de completitud para expedición.
9. Un Expedidor puede generar certificado solo cuando los datos requeridos están completos.
10. El sistema bloquea generación con mensaje específico cuando faltan datos obligatorios.
11. El PDF se genera desde la plantilla oficial.
12. Cada certificado generado tiene número único.
13. El firmante se toma desde configuración vigente.
14. El certificado refleja estado `Suspendido`, `En Liquidación` o `Cancelado` según reglas del PRD.
15. El sistema permite registrar documentos soporte PDF y valida documentos requeridos para registros nuevos.
16. Cada generación registra traza con usuario, rol, ESAL, `ID SIPEJ`, NIT, número, versión de datos, resultado y hash.
17. Los errores de datos faltantes se muestran de forma clara.
18. La solución queda alineada a la arquitectura tecnológica SED de referencia.
