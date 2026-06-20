# Spec I10 - Seleccion De Plantilla EYRL Por Estado Y Documento Vigente

> Estado: especificado para revision.
> Fecha: 2026-06-20.
> Sistema: `SED_ESAL`.
> Metodologia: SDD Spec-Anchored.
> Handoff base: `docs/Handoff/handoff-20260619-i9-closed-retake-i10.md`.
> Depende de: I8 certificado PDF EYRL, I9 gestion documental administrativa transversal.

## 1. Objetivo

Abrir una nueva iteracion para que la generacion de certificados EYRL seleccione la plantilla correcta segun el estado administrativo de la ESAL y, para los estados de liquidacion/cancelacion, segun el documento vigente registrado en I9.

I10 no cambia el flujo de expedicion ni el endpoint de descarga. El alcance es incorporar un selector explicito de plantilla y ajustar el PDF generado para reproducir los textos y estructuras especiales de las plantillas oficiales suministradas por estado.

El usuario objetivo es el funcionario expedidor de Inspeccion y Vigilancia que necesita emitir certificados cuya salida juridica y visual corresponda al estado real de la ESAL.

## 2. Plantillas Fuente

Las plantillas fuente locales para I10 son:

| Variante | Archivo fuente |
|---|---|
| Por defecto | `Documentos_Referencia/Iteracion/Plantilla Certificado EYRL.docx` |
| Suspendida | `Documentos_Referencia/Iteracion/Plantilla Certificado EYRL - ESAL Suspendida.docx` |
| Liquidacion por tramite de cancelacion voluntaria | `Documentos_Referencia/Iteracion/Plantilla Certificado EYRL - ESAL Estado de Liquidación.docx` |
| Liquidacion por termino de duracion | `Documentos_Referencia/Iteracion/Plantilla Certificado EYRL - ESAL Estado de Liquidacion por Termino de Duración.docx` |
| Cancelada voluntariamente | `Documentos_Referencia/Iteracion/Plantilla Certificado EYRL - ESAL Cancelada Voluntariamente.docx` |
| Cancelada por orden de autoridad | `Documentos_Referencia/Iteracion/Plantilla Certificado EYRL - ESAL Cancelada Por Orden de Autoridad.docx` |

`Documentos_Referencia/` sigue siendo local y no se publica sin aprobacion expresa.

## 3. Decision Aprobada

I10 usara la opcion 1 aprobada por el usuario: selector explicito de plantilla por regla de negocio.

Regla clave aprobada:

- Para `EN_LIQUIDACION` y `CANCELADO`, la variante de plantilla se decide desde el documento vigente I9.
- La actuacion administrativa puede alimentar datos o trazabilidad, pero no decide la variante.
- Las ESAL que no cumplan una regla especial se generan con la plantilla EYRL por defecto.

## 4. Reglas De Seleccion

| Condicion | Variante esperada |
|---|---|
| `EstadoEsal.SUSPENDIDO` | `EYRL_SUSPENDIDA` |
| `EstadoEsal.EN_LIQUIDACION` + documento vigente `LIQUIDACION.TRAMITE_CANCELACION_VOLUNTARIA` | `EYRL_LIQUIDACION_TRAMITE_CANCELACION_VOLUNTARIA` |
| `EstadoEsal.EN_LIQUIDACION` + documento vigente `LIQUIDACION.TERMINO_DURACION` | `EYRL_LIQUIDACION_TERMINO_DURACION` |
| `EstadoEsal.CANCELADO` + documento vigente `CANCELACION.CANCELACION_VOLUNTARIA` | `EYRL_CANCELADA_VOLUNTARIAMENTE` |
| `EstadoEsal.CANCELADO` + documento vigente `CANCELACION.ORDEN_AUTORIDAD` | `EYRL_CANCELADA_ORDEN_AUTORIDAD` |
| Cualquier otro estado o ausencia de documento vigente compatible | `EYRL_DEFAULT` |

Reglas adicionales:

- `SUSPENDIDO` no depende de documento I9 para seleccionar plantilla.
- `EN_LIQUIDACION` sin documento vigente `LIQUIDACION` compatible cae a `EYRL_DEFAULT` solo si logra llegar a generacion por rutas historicas o datos antiguos. En flujos I9 nuevos, el cambio de estado ya debe estar bloqueado antes.
- `CANCELADO` sin documento vigente `CANCELACION` compatible cae a `EYRL_DEFAULT` solo como fallback defensivo para datos historicos o inconsistentes.
- Si existen varios historicos, solo cuenta el documento `vigente=true`.
- Si existe mas de un documento vigente incompatible por datos inconsistentes, la seleccion debe preferir una unica regla deterministica y registrar el GAP en tests o log. La meta funcional es que I9 evite esa inconsistencia.

## 5. Contrato De Variante

Crear un enum o tipo equivalente, por ejemplo:

```java
public enum CertificadoPlantilla {
    EYRL_DEFAULT,
    EYRL_SUSPENDIDA,
    EYRL_LIQUIDACION_TRAMITE_CANCELACION_VOLUNTARIA,
    EYRL_LIQUIDACION_TERMINO_DURACION,
    EYRL_CANCELADA_VOLUNTARIAMENTE,
    EYRL_CANCELADA_ORDEN_AUTORIDAD
}
```

Cada variante debe tener:

- Identificador tecnico.
- Version de plantilla visible en pie tecnico del PDF.
- Textos juridicos clave verificables.
- Relacion con archivo DOCX fuente.

Versiones propuestas:

| Variante | Version tecnica |
|---|---|
| `EYRL_DEFAULT` | `I10-EYRL-DEFAULT-v1` |
| `EYRL_SUSPENDIDA` | `I10-EYRL-SUSPENDIDA-v1` |
| `EYRL_LIQUIDACION_TRAMITE_CANCELACION_VOLUNTARIA` | `I10-EYRL-LIQUIDACION-TRAMITE-v1` |
| `EYRL_LIQUIDACION_TERMINO_DURACION` | `I10-EYRL-LIQUIDACION-TERMINO-v1` |
| `EYRL_CANCELADA_VOLUNTARIAMENTE` | `I10-EYRL-CANCELADA-VOLUNTARIA-v1` |
| `EYRL_CANCELADA_ORDEN_AUTORIDAD` | `I10-EYRL-CANCELADA-AUTORIDAD-v1` |

## 6. Arquitectura Propuesta

### 6.1. Selector

Agregar un componente backend con responsabilidad unica, por ejemplo `CertificadoTemplateSelector`.

Responsabilidad:

- Recibir estado de ESAL y documentos vigentes relevantes.
- Retornar una `CertificadoPlantilla`.
- No generar PDF.
- No consultar UI.
- Ser testeable con unit tests sin OpenPDF.

Dependencias esperadas:

- `DocumentoSoporteRepository` o datos ya ensamblados desde `CertificadoAssembler`.
- Enums I9:
  - `TipoDocumentoSoporte`
  - `SubtipoDocumentoSoporte`
- `EstadoEsal`.

### 6.2. DTO Narrativo

Extender `CertificadoNarrativoDto` con:

- `CertificadoPlantilla plantilla`.
- Datos administrativos utiles para textos especiales cuando existan:
  - referencia del documento vigente;
  - fecha del documento vigente;
  - subtipo documental vigente;
  - alerta o parrafo juridico especial.

No se deben exponer rutas fisicas ni bytes de documentos.

### 6.3. Assembler

`CertificadoAssembler` debe:

- Consultar documentos soporte vigentes I9 cuando el estado sea `EN_LIQUIDACION` o `CANCELADO`.
- Invocar el selector o preparar datos para que el selector decida.
- Mantener el ensamblaje de representantes, organos, revisoria y personeria existente.
- No mover reglas visuales PDF al assembler.

### 6.4. PDF Service

`CertificadoPdfService` debe:

- Usar la variante seleccionada para construir bloques especificos.
- Mantener bloques comunes: header, preambulo, datos base, tablas institucionales, cierre, firmante, footer y nota.
- Evitar condicionales dispersos. La seleccion debe venir resuelta en el DTO.
- Identificar el PDF con la version tecnica de la variante.

Si durante el plan se evidencia que `CertificadoPdfService` queda demasiado grande, se permite extraer helpers privados o clases pequenas de renderizado, sin introducir un motor externo de plantillas.

## 7. Contrato Textual Por Variante

### 7.1. Default

La plantilla default conserva la salida EYRL I8 para ESAL que no cumple condiciones especiales.

Debe seguir incluyendo:

- `CERTIFICADO DE EXISTENCIA Y REPRESENTACION LEGAL`.
- Preambulo SED.
- Datos de identificacion de la ESAL.
- Objeto social.
- Representacion legal.
- Asamblea general.
- Junta directiva.
- Revisoria fiscal.
- Duracion.
- Cierre y firmante.
- Footer institucional.

### 7.2. Suspendida

Fuente: `Documentos_Referencia/Iteracion/Plantilla Certificado EYRL - ESAL Suspendida.docx`.

Debe incluir texto especial de suspension:

- `LA MENCIONADA ESAL TIENE PERSONERIA JURIDICA SUSPENDIDA`.
- Referencia a resolucion, termino de suspension y fecha de firmeza cuando existan datos.
- Si los datos no existen en el modelo, usar texto controlado sin inventar valores.

Debe conservar las secciones base de organos y revisoria.

### 7.3. Liquidacion Por Tramite De Cancelacion Voluntaria

Fuente: `Plantilla Certificado EYRL - ESAL Estado de Liquidación.docx`.

Debe incluir:

- Nombre de la ESAL con marca `EN LIQUIDACION`.
- Bloque destacado de estado de liquidacion.
- Texto `LA ENTIDAD SE ENCUENTRA DISUELTA Y EN ESTADO DE LIQUIDACION`.
- Representacion legal orientada a liquidador cuando los datos disponibles lo permitan.
- Secciones base de asamblea, junta y revisoria.

### 7.4. Liquidacion Por Termino De Duracion

Fuente: `Plantilla Certificado EYRL - ESAL Estado de Liquidacion por Termino de Duración.docx`.

Debe incluir:

- Nombre de la ESAL con marca `EN LIQUIDACION`.
- Seccion `ESTADO DE LIQUIDACION`.
- Texto que indique liquidacion por cumplimiento del termino de duracion de la ESAL.
- Secciones base de representacion, asamblea, junta y revisoria.

### 7.5. Cancelada Voluntariamente

Fuente: `Plantilla Certificado EYRL - ESAL Cancelada Voluntariamente.docx`.

Debe incluir:

- Texto de carpeta administrativa revisada.
- Texto `LA MENCIONADA ESAL FUE LIQUIDADA Y SU PERSONERIA JURIDICA CANCELADA`.
- Datos de identificacion en pasado.
- Ultima representacion legal.
- Ultima revisoria fiscal.
- Texto que indique que la ESAL efectuo el tramite correspondiente a su liquidacion.

### 7.6. Cancelada Por Orden De Autoridad

Fuente: `Plantilla Certificado EYRL - ESAL Cancelada Por Orden de Autoridad.docx`.

Debe incluir:

- Texto de carpeta administrativa revisada.
- Texto `LA PERSONERIA JURIDICA DE LA MENCIONADA ESAL FUE CANCELADA`.
- Datos de identificacion en pasado.
- Ultima representacion legal.
- Ultima revisoria fiscal.
- Texto que indique que la ESAL no ha adelantado el tramite correspondiente a su liquidacion, si aplica segun plantilla.

## 8. Datos Faltantes Y Politica De Marcadores

Algunas plantillas contienen datos que no estan completamente normalizados en el modelo actual:

- Articulos estatutarios.
- Termino exacto de suspension en meses.
- Fecha de firmeza de resolucion de suspension.
- Tarjeta profesional del revisor fiscal, si no existe campo dedicado.
- Fecha del acta de nombramiento si el modelo solo tiene acta o fecha en otro campo.
- Elaboro, reviso y aprobo.

Politica I10:

- No inventar datos.
- Usar datos disponibles en ESAL, personeria, nombramientos, organos, actuaciones y documento vigente I9.
- Cuando falte un dato no bloqueante, usar marcador controlado como `no registrado`.
- Si un dato es juridicamente imprescindible para una variante, documentarlo como GAP y decidir en el PLAN si se bloquea o se permite con marcador.

## 9. Seguridad Y Trazabilidad

I10 no cambia autorizacion.

- La generacion sigue protegida por los roles ya definidos para certificados.
- No se exponen documentos soporte ni rutas fisicas en el PDF.
- La version de plantilla queda registrada en el texto tecnico del PDF.
- La auditoria de generacion existente debe mantenerse.

## 10. Testing Strategy

### Backend

RED inicial:

- Selector retorna `EYRL_SUSPENDIDA` para ESAL suspendida.
- Selector retorna `EYRL_LIQUIDACION_TRAMITE_CANCELACION_VOLUNTARIA` con documento vigente I9 correspondiente.
- Selector retorna `EYRL_LIQUIDACION_TERMINO_DURACION` con documento vigente I9 correspondiente.
- Selector retorna `EYRL_CANCELADA_VOLUNTARIAMENTE` con documento vigente I9 correspondiente.
- Selector retorna `EYRL_CANCELADA_ORDEN_AUTORIDAD` con documento vigente I9 correspondiente.
- Selector retorna `EYRL_DEFAULT` para estado activo o sin condicion especial.

PDF tests:

- Cada variante imprime su version tecnica.
- Suspendida contiene texto clave de suspension.
- Liquidacion por tramite contiene texto clave de disolucion/estado de liquidacion.
- Liquidacion por termino contiene texto clave de termino de duracion.
- Cancelada voluntariamente contiene texto clave de liquidacion y cancelacion voluntaria.
- Cancelada por autoridad contiene texto clave de cancelacion por autoridad.
- Default conserva comportamiento I8 esperado.

Regression:

- `CertificadoPdfServiceTest`.
- `CertificadoAssemblerTest`.
- `GeneracionServiceTest`.
- Tests I9 relacionados con documento vigente si se toca su contrato.
- `mvn test`.
- `mvn package -DskipTests`.

### Frontend

No se esperan cambios funcionales de UI en I10.

Verificacion:

- Build Angular.
- Tests Angular solo si el runner local esta disponible; si existe restriccion sandbox/watch, documentarla en execution log.

## 11. Boundaries

Always:

- Mantener SDD: SPEC, PLAN, execution log y handoff por hitos.
- Usar documento vigente I9 para decidir variantes de liquidacion/cancelacion.
- Mantener plantilla default como fallback.
- Mantener historicos de certificados inmutables.
- Registrar version tecnica de plantilla en el PDF.
- Validar con tests de selector antes de tocar PDF.

Ask first:

- Agregar dependencia externa o motor de templates.
- Cambiar modelo de datos para nuevos campos juridicos.
- Cambiar endpoints de generacion/descarga.
- Publicar plantillas de `Documentos_Referencia/`.

Never:

- Inventar datos juridicos faltantes.
- Decidir variantes de liquidacion/cancelacion desde el frontend.
- Usar documentos historicos no vigentes para seleccionar plantilla.
- Reintroducir numero interno visible en el PDF.
- Regenerar certificados historicos ya expedidos por lote.
- Revertir cambios no relacionados del worktree.

## 12. Success Criteria

1. El backend selecciona `EYRL_SUSPENDIDA` para ESAL suspendida.
2. El backend selecciona la variante de liquidacion correcta desde documento vigente I9.
3. El backend selecciona la variante de cancelacion correcta desde documento vigente I9.
4. Las ESAL que no cumplen condiciones especiales usan `EYRL_DEFAULT`.
5. Cada PDF incluye version tecnica I10 de la variante usada.
6. Cada PDF contiene los textos juridicos clave de su plantilla fuente.
7. El PDF default mantiene la estructura EYRL ya validada en I8.
8. No se exponen rutas ni bytes de documentos I9.
9. Tests de selector y PDF pasan.
10. Suite backend completa pasa.
11. WAR backend se genera.
12. README, ARRANQUE, GUIA_PRUEBAS_FUNCIONALES, PLAN, execution log y handoff quedan actualizados al cierre de I10.

## 13. Open Questions Para El PLAN

1. Confirmar si los campos de suspension no normalizados se deben dejar como `no registrado` o bloquear generacion de suspendidas hasta que existan datos completos.
2. Confirmar si `tarjeta profesional` del revisor fiscal puede quedar `no registrado` cuando no exista campo dedicado.
3. Confirmar si `Elaboro`, `Reviso` y `Aprobo` quedan fuera de I10 o se poblaran con usuario/firmante disponible.
4. Confirmar si la extraccion visual de las plantillas debe convertirse en fixtures de texto dentro de tests o mantenerse como referencia documental local.
