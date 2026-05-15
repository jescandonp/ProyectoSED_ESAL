# Plan I4 - Seguridad Institucional, Autorizacion Y Hardening

> Estado: propuesta ejecutable, pendiente aprobacion.  
> Fecha: 2026-05-15.  
> Spec: `docs/specs/2026-05-15-sed-esal-i4-spec.md`.  
> Sistema: `SED_ESAL`.

## 1. Objetivo

Implementar la seguridad institucional de `SED_ESAL`: autenticacion por JWT institucional, autorizacion backend/frontend, CORS y cabeceras por ambiente, proteccion de archivos, auditoria de seguridad y hardening previo a despliegue.

## 2. Precondiciones

- I1, I2 e I3 implementados o aprobados como base funcional.
- Roles `ADMINISTRADOR` y `EXPEDIDOR` definidos.
- `docs/ARCHITECTURE.md` actualizado con seccion de seguridad base.
- Datos institucionales de Azure AD/Office 365 pendientes de confirmacion.

## 3. Tareas

### T1 - Configuracion De Perfiles

- Separar configuracion `local-dev` y `weblogic`.
- Mantener HTTP Basic solo en local.
- Activar JWT solo en perfil institucional.
- Externalizar issuer, audience, jwks y origenes CORS.

Verificacion:

- Local sigue funcionando con usuarios de prueba.
- Perfil institucional no acepta Basic como mecanismo operativo.

### T2 - Validacion JWT

- Configurar Spring Security como resource server compatible con Java 8/Spring Boot 2.7.
- Validar firma, issuer, audience y expiracion.
- Manejar errores 401 consistentes.

Verificacion:

- Token valido accede.
- Token expirado falla.
- Issuer/audience incorrectos fallan.

### T3 - Resolucion De Usuario Y Roles

- Extraer correo, nombre e identificador institucional.
- Mapear claims/grupos a `ADMINISTRADOR` y `EXPEDIDOR`.
- Preparar extension a `AUDITOR`.
- Exponer `/api/auth/me` y `/api/auth/permisos`.

Verificacion:

- Usuario admin resuelve rol admin.
- Usuario expedidor resuelve rol expedidor.
- Usuario sin grupo permitido recibe 403 o acceso limitado segun politica.

### T4 - Autorizacion Backend

- Revisar todos los endpoints I1-I3.
- Declarar roles por endpoint.
- Centralizar reglas de acceso.
- Proteger endpoints admin.
- Proteger descargas.

Verificacion:

- Expedidor recibe 403 en carga, CRUD, firmantes, numeracion y auditoria.
- Admin conserva acceso completo.

### T5 - Guardas Frontend

- Implementar servicio de sesion/permisos.
- Implementar guards por autenticacion.
- Implementar guards por rol.
- Ocultar navegacion no autorizada.
- Manejar 401/403.

Verificacion:

- Menu refleja permisos.
- Ruta admin bloquea expedidor.
- 401 limpia sesion.

### T6 - CORS Y Cabeceras

- Configurar CORS por ambiente.
- Agregar cabeceras minimas de seguridad.
- Configurar cache restrictivo para respuestas sensibles.

Verificacion:

- Origen local permitido en local-dev.
- Origen no permitido rechazado en perfil institucional.
- Descargas tienen cabeceras restrictivas.

### T7 - Proteccion De Documentos Y Certificados

- Evitar rutas fisicas expuestas.
- Validar permiso antes de descargar.
- Registrar descarga.
- Validar hash en certificados.

Verificacion:

- Descarga anonima falla.
- Descarga con rol permitido funciona.
- Hash inconsistente bloquea certificado.

### T8 - Auditoria De Seguridad

- Registrar acceso denegado.
- Registrar token invalido/expirado cuando sea viable.
- Registrar descargas.
- Registrar cambios de firmante/numeracion.
- Agregar filtros de eventos de seguridad.

Verificacion:

- Auditoria muestra eventos I4.
- No registra secretos ni tokens.

### T9 - Manejo Seguro De Errores

- Revisar `GlobalExceptionHandler`.
- Evitar stack traces al frontend.
- Evitar rutas fisicas.
- Agregar identificador de correlacion si aplica.

Verificacion:

- Error tecnico retorna mensaje seguro.
- Logs server-side conservan detalle necesario.

### T10 - Documentacion Y Pruebas

- Actualizar `README.md`.
- Actualizar `docs/ARRANQUE.md`.
- Actualizar `docs/GUIA_PRUEBAS_FUNCIONALES.md`.
- Mantener log de ejecucion I4.

Verificacion:

- Documentos apuntan a spec/plan I4.
- Casos funcionales I4 disponibles.

## 4. Orden De Ejecucion

1. T1.
2. T2.
3. T3.
4. T4.
5. T5.
6. T6.
7. T7.
8. T8.
9. T9.
10. T10.

## 5. Gates De Calidad

No cerrar I4 hasta que:

- JWT institucional valide correctamente.
- Roles efectivos se resuelvan en backend.
- Endpoints administrativos esten protegidos.
- Descargas esten autenticadas y auditadas.
- CORS y cabeceras sean por ambiente.
- Frontend bloquee rutas no autorizadas.
- Errores no expongan informacion sensible.
- Documentacion quede actualizada.

## 6. Riesgos

- Claims y grupos institucionales pueden variar respecto al supuesto inicial.
- Politica SED puede exigir cabeceras o controles adicionales.
- Swagger puede requerir restriccion especial en ambientes institucionales.
- La integracion Azure AD puede depender de configuracion externa no disponible en desarrollo.

## 7. No Hacer En I4

- No administrar usuarios dentro de Azure AD.
- No crear consulta publica.
- No implementar firma digital.
- No implementar QR.
- No integrar WAF/SIEM sin confirmacion.
