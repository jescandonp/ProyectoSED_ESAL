# Plan I3 - Generacion PDF, Numeracion, Firmante Y Trazabilidad

> Estado: aprobado como plan futuro.
> Fecha: 2026-05-15.
> Spec: `docs/specs/2026-05-15-sed-esal-i3-spec.md`.
> Sistema: `SED_ESAL`.

## 1. Objetivo

Implementar el tercer incremento funcional de `SED_ESAL`: emision PDF desde preview validada, numeracion unica, firmante vigente, almacenamiento, hash, descarga y auditoria de expedicion.

## 2. Precondiciones

- I1 implementado: modelo, estados, completitud y auditoria.
- I2 implementado: busqueda, detalle y preview certificable.
- Plantilla oficial disponible localmente: `Documentos_Referencia/Plantilla Certificado EYRL.docx`.
- Politica de seguridad ampliada aprobada como referencia; si SED confirma lineamientos adicionales antes de implementar, revisar descarga, headers, auditoria y almacenamiento.

## 3. Tareas

### T1 - Modelo De Certificados

- Crear entidad `Certificado`.
- Crear entidad `NumeracionCertificado`.
- Crear entidad `Firmante`.
- Crear entidad `PlantillaCertificado` si aplica al enfoque tecnico.
- Crear DDL/migracion correspondiente.

Verificacion:

- Schema valida.
- Unicidad de `numeroCertificado`.
- Estados de certificado disponibles.

### T2 - Numeracion

- Configurar prefijo inicial `ESAL`.
- Implementar consecutivo anual.
- Garantizar asignacion transaccional.
- Exponer endpoints admin de consulta y actualizacion.

Verificacion:

- Primer numero del anio: `ESAL-2026-000001`.
- Segundo numero: `ESAL-2026-000002`.
- No hay duplicados en ejecuciones concurrentes.

### T3 - Firmantes

- Implementar CRUD de firmantes.
- Validar vigencias.
- Evitar solapamientos activos.
- Resolver firmante vigente por fecha de expedicion.

Verificacion:

- Bloqueo sin firmante vigente.
- Bloqueo por solapamiento.
- Seleccion correcta por fecha.

### T4 - Servicio De Plantilla

- Cargar plantilla activa.
- Mapear datos del preview a variables de plantilla.
- Registrar version de plantilla usada.
- Definir errores funcionales por campo obligatorio no mapeable.

Verificacion:

- Template carga correctamente.
- Campos requeridos se mapean.
- Error claro si falta variable obligatoria.

### T5 - Generacion PDF

- Convertir documento final a PDF.
- Aplicar contenido por estado administrativo.
- Generar nombre de archivo controlado.
- Calcular hash SHA-256.

Verificacion:

- PDF existe y no esta vacio.
- PDF incluye numero, fecha y firmante.
- Hash corresponde a bytes finales.

### T6 - Almacenamiento Y Descarga

- Crear servicio abstraido de almacenamiento.
- Configurar ruta local-dev.
- Persistir ruta logica del PDF.
- Implementar descarga con permisos.
- Validar hash antes de descargar.

Verificacion:

- PDF almacenado.
- Descarga exitosa con rol permitido.
- Hash inconsistente bloquea descarga.

### T7 - Flujo De Expedicion

- Integrar preview I2.
- Validar bloqueo antes de generar.
- Reservar numero solo si validacion habilita.
- Registrar certificado generado o fallido.
- Registrar version de datos.

Verificacion:

- Preview bloqueada no genera numero.
- Preview habilitada genera certificado.
- Error tecnico queda trazado.

### T8 - API Y OpenAPI

- Implementar endpoints de generacion, consulta, descarga e historial.
- Implementar endpoints admin de numeracion y firmantes.
- Documentar DTOs y errores en Swagger.

Verificacion:

- Swagger refleja contratos.
- 401/403 correctos.
- Errores funcionales legibles.

### T9 - UI De Expedicion

- Habilitar boton real de generacion desde preview.
- Confirmar generacion antes de asignar numero.
- Mostrar resultado con numero, fecha, firmante y hash.
- Mostrar historial por ESAL.
- Implementar descarga.

Verificacion:

- Expedidor genera desde preview habilitada.
- UI muestra bloqueo si no se puede generar.
- Historial muestra certificado generado.

### T10 - UI Administrativa

- Crear configuracion de numeracion.
- Crear administracion de firmantes.
- Mostrar validaciones de vigencia.
- Mostrar auditoria de expedicion.

Verificacion:

- Admin configura firmante.
- Admin actualiza prefijo si aplica.
- Expedidor no accede a configuracion.

### T11 - Auditoria

- Registrar generacion solicitada.
- Registrar bloqueo.
- Registrar generacion exitosa.
- Registrar generacion fallida.
- Registrar descarga.
- Registrar cambios de numeracion y firmantes.

Verificacion:

- Auditoria contiene usuario, rol, ESAL, `ID SIPEJ`, NIT, numero, version, resultado y hash.

### T12 - Documentacion Y Log

- Actualizar `README.md`.
- Actualizar `docs/ARRANQUE.md`.
- Actualizar `docs/GUIA_PRUEBAS_FUNCIONALES.md`.
- Mantener log de ejecucion I3.

Verificacion:

- Documentos apuntan a spec/plan I3.
- Casos funcionales I3 disponibles.

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
11. T11.
12. T12.

## 5. Gates De Calidad

No cerrar I3 hasta que:

- Numero unico sea transaccional y unico.
- Firmante vigente sea obligatorio.
- PDF se genere desde plantilla.
- Hash se calcule y valide.
- Descarga tenga control de permisos.
- Trazabilidad cubra generacion, bloqueo, fallo y descarga.
- Estados especiales se reflejen en PDF.
- Documentacion quede actualizada.

## 6. Riesgos

- Conversion DOCX a PDF puede requerir dependencia externa o componente institucional.
- Plantilla puede necesitar adaptacion tecnica con variables.
- Almacenamiento definitivo de PDFs no esta cerrado.
- Concurrencia de numeracion debe probarse con cuidado.
- Seguridad ampliada puede cambiar reglas de descarga y retencion.

## 7. No Hacer En I3

- No implementar firma digital certificada.
- No implementar QR.
- No implementar verificacion publica.
- No implementar anulacion.
- No implementar Azure AD real.
