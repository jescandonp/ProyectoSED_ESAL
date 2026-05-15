# Plan I1 - Modelo Base, Carga Inicial, Estados Y Completitud

> Estado: aprobado para ejecucion.
> Fecha: 2026-05-15.
> Spec: `docs/specs/2026-05-15-sed-esal-i1-spec.md`.
> Sistema: `SED_ESAL`.

## 1. Objetivo

Implementar el primer incremento funcional de `SED_ESAL`: base tecnica, modelo de datos, carga historica desde Excel, diccionario de obligatoriedad, estados, documentos soporte iniciales, roles local-dev y semaforo de completitud.

## 2. Precondiciones

- PRD V2 disponible.
- `docs/CONSTITUTION.md`, `docs/ARCHITECTURE.md`, `docs/TECNOLOGIAS.md` disponibles.
- Spec I1 revisada.
- Coordenadas tecnicas aprobadas.
- Seccion ampliada de seguridad de `ARCHITECTURE.md` aprobada como referencia; Azure AD real queda para I4.

## 3. Tareas

### T1 - Bootstrap Backend

- Crear proyecto `sed-esal-backend`.
- Configurar Java 8, Spring Boot 2.7.18, WAR, Spring Web, Security, JPA, Oracle, Actuator, SpringDoc.
- Configurar perfiles `local-dev` y `weblogic`.
- Configurar health y Swagger.
- Crear estructura de paquetes.

Verificacion:

- `mvn test`
- `mvn package -DskipTests`
- Health disponible en local-dev.

### T2 - Bootstrap Frontend

- Crear proyecto `sed-esal-angular`.
- Configurar Angular 20, TypeScript strict, PrimeNG 20, Tailwind CSS.
- Configurar rutas base.
- Configurar proxy local hacia backend.
- Aplicar tokens visuales de `docs/DESIGN.md`.

Verificacion:

- `npm test` o equivalente disponible.
- `npm run build`.
- Pantalla login local-dev renderiza.

### T3 - Modelo Oracle Y Dominio Backend

- Crear DDL inicial en `db/00_setup.sql`.
- Crear entidades JPA:
  - ESAL.
  - PersoneriaJuridica.
  - ReformaEstatutaria.
  - Nombramiento.
  - OrganoAdministracion.
  - ActuacionAdministrativa.
  - DocumentoSoporte.
  - CampoObligatoriedad.
  - AdvertenciaCompletitud.
  - Auditoria.
- Crear enums de estado, completitud, actuaciones, documentos y roles.

Verificacion:

- Tests de repositorio o servicio con base local/test.
- Validacion de schema sin errores.

### T4 - Seguridad Local-Dev

- Crear usuarios en memoria o tabla seed para `ADMINISTRADOR` y `EXPEDIDOR`.
- Configurar HTTP Basic local-dev.
- Proteger endpoints por rol.
- Preparar estructura para JWT futuro.

Verificacion:

- Admin accede a endpoints admin.
- Expedidor recibe 403 en endpoints admin.
- Ambos acceden a consulta permitida.

### T5 - Importacion Diccionario

- Leer `Base excel.xlsx`.
- Persistir 117 definiciones.
- Identificar 23 obligatorias y 94 opcionales.
- Mantener nota y orden.

Verificacion:

- Test con archivo real.
- Conteos exactos.
- Campos duplicados preservan contexto.

### T6 - Importacion Base Historica

- Leer `BASE DE DATOS - REGISTRO_1.xlsx`.
- Detectar filas efectivas.
- Transformar columnas en modelo relacional.
- Convertir reformas a lista dinamica.
- Registrar advertencias por campos faltantes.
- Registrar resumen de importacion.

Verificacion:

- Test con archivo real.
- Conteo de entidades efectivas.
- Al menos una ESAL con multiples reformas queda en tabla hija.
- Advertencias generadas para faltantes obligatorios.

### T7 - Completitud Y Estados

- Implementar servicio de completitud.
- Implementar reglas por estado.
- Persistir advertencias bloqueantes/no bloqueantes.
- Exponer endpoint de completitud.

Verificacion:

- Tests para `ACTIVO`, `SUSPENDIDO`, `EN_LIQUIDACION`, `CANCELADO`.
- Tests para semaforo listo, incompleto no bloqueante e incompleto bloqueante.

### T8 - Documentos Soporte Iniciales

- Crear servicio de almacenamiento abstraido.
- Permitir upload PDF.
- Rechazar formatos no PDF.
- Asociar documento a ESAL/proceso.

Verificacion:

- Upload PDF exitoso.
- Upload no PDF rechazado.
- Registro visible en detalle.

### T9 - API Y UI Administrativa

- Endpoints iniciales de importacion, ESAL, estado, completitud, documentos y auditoria.
- Pantallas:
  - Login.
  - Dashboard.
  - Carga inicial.
  - Listado ESAL.
  - Detalle por secciones.
  - Panel completitud.
  - Documentos soporte.

Verificacion:

- Flujo admin: login -> carga -> listado -> detalle -> completitud.
- Flujo expedidor: login -> listado -> detalle -> completitud sin edicion.

### T10 - Auditoria Y Documentacion

- Registrar eventos de carga, creacion, edicion, cambio de estado, documentos y consulta relevante.
- Actualizar `docs/ARRANQUE.md`.
- Actualizar `docs/GUIA_PRUEBAS_FUNCIONALES.md`.
- Crear/actualizar log de ejecucion.

Verificacion:

- Auditoria visible por admin.
- Documentos actualizados.

## 4. Orden De Ejecucion

1. T1 y T2.
2. T3.
3. T4.
4. T5.
5. T6.
6. T7.
7. T8.
8. T9.
9. T10.

## 5. Gates De Calidad

No cerrar I1 hasta que:

- Backend compile y tests pasen.
- Frontend compile y pruebas disponibles pasen.
- Importacion con archivos reales se haya probado.
- Roles bloqueen acciones no permitidas.
- Completitud genere estados correctos.
- Documentos soporte acepten solo PDF.
- `ARRANQUE.md` y `GUIA_PRUEBAS_FUNCIONALES.md` queden actualizados.

## 6. Riesgos

- La base historica tiene datos faltantes en campos obligatorios.
- Campos duplicados requieren mapeo contextual cuidadoso.
- Almacenamiento documental definitivo no esta cerrado.
- Seccion de seguridad de arquitectura puede modificar decisiones de I1.
- Angular/browser tests pueden requerir ajustes de entorno local.

## 7. No Hacer En I1

- No generar certificado PDF final.
- No implementar numeracion.
- No implementar firmantes.
- No implementar QR.
- No integrar Azure AD real.
- No abrir consulta publica.
