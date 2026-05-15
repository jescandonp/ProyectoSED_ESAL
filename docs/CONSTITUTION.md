# SED_ESAL SDD Constitution

> Estado: Activa para estructuracion inicial del proyecto.  
> Metodologia: Spec-Driven Development (SDD), nivel Spec-Anchored.  
> Fecha base: 2026-05-15.

## 1. Autoridad De Artefactos

Cuando exista tension entre documentos, decisiones o codigo, aplicar este orden:

1. `docs/CONSTITUTION.md`
2. `docs/ARCHITECTURE.md`
3. `docs/TECNOLOGIAS.md`
4. PRD vigente en `docs/specs/`
5. Spec tecnica del incremento activo
6. Plan de implementacion aprobado en `docs/plans/`
7. Codigo fuente

El codigo nunca es la fuente primaria de verdad del proyecto. Si el codigo contradice la spec, se corrige el codigo o se actualiza primero la spec y luego el plan.

`SED_ESAL` toma como referencia la arquitectura SED/SIGCON, pero sus coordenadas canonicas son propias:

| Coordenada | Valor SED_ESAL |
|---|---|
| Sistema | `SED_ESAL` |
| Backend | `sed-esal-backend` |
| Frontend | `sed-esal-angular` |
| WAR | `sed-esal-backend.war` |
| Contexto WebLogic propuesto | `/sed-esal` |
| Paquete Java base propuesto | `co.gov.bogota.sed.esal` |
| Esquema Oracle MVP propuesto | `SED_ESAL` |
| Prefijo Oracle propuesto | `ESAL_` |
| Perfil local | `local-dev` |
| Perfil servidor | `weblogic` |

## 2. Reglas SDD

- Todo incremento debe tener spec tecnica escrita, revisada y aprobada antes de implementarse.
- Todo incremento debe tener plan de implementacion en `docs/plans/` antes de ejecutar tareas.
- Todo cambio de alcance entra primero por PRD o spec tecnica, no por codigo.
- Todo cambio de arquitectura entra primero por `docs/ARCHITECTURE.md` o por esta constitucion.
- Todo cambio visual entra primero por `docs/DESIGN.md` o por la spec activa.
- Cada tarea de implementacion debe tener salida verificable y trazabilidad a criterios de aceptacion.
- No se implementa funcionalidad fuera del incremento activo aunque parezca conveniente.
- La especificacion es un contrato vivo: cuando negocio cambia una regla, se actualiza la especificacion antes del codigo.

## 3. Stack No Negociable

Backend:

- Java runtime: Oracle JDK 8.
- Spring Boot: 2.7.x, version canonica 2.7.18.
- Empaquetado: WAR.
- Servidor objetivo: Oracle WebLogic 12.2.1.4.0.
- Swagger/OpenAPI: SpringDoc 1.7.0, siempre activo.
- Base de datos: Oracle 19c+.
- Driver: ojdbc8.

Frontend:

- Angular 20.
- TypeScript strict mode.
- PrimeNG 20.
- Tailwind CSS 3.4.
- MSAL Angular/Browser 3.x para Office 365/Azure AD.

Identidad y seguridad:

- Perfil `local-dev`: HTTP Basic con usuarios de prueba.
- Perfil `weblogic`: Azure AD JWT / Office 365.
- Toda autorizacion por rol se valida en backend.
- El Expedidor no puede modificar informacion de ESAL.
- El Administrador no debe saltarse reglas de trazabilidad o historico.

## 4. Reglas De Dominio No Negociables

- `ID SIPEJ` es el identificador funcional principal de una ESAL.
- La carga historica desde Excel puede importar registros incompletos, pero debe registrar advertencias y semaforo de completitud.
- Los registros nuevos y actualizaciones formales deben validar campos obligatorios y documentos soporte definidos por reglas de negocio.
- Las reformas estatutarias se modelan como relacion 1:N; no se limitan a columnas fijas.
- Los documentos soporte se cargan en PDF.
- Estados minimos de ESAL: `Activo`, `Suspendido`, `En Liquidacion`, `Cancelado`.
- El certificado se genera solo si la ESAL no tiene faltantes bloqueantes segun formato, diccionario de obligatoriedad y reglas de estado.
- Todo certificado debe tener numero unico, PDF, hash, version de datos usada y traza de expedicion.
- La informacion del firmante se configura por vigencia.
- El QR o codigo de verificacion queda fuera del MVP, pero el diseno debe permitir agregarlo en un incremento futuro.

## 5. Reglas De Arquitectura SED

- El backend mantiene la estructura `domain/`, `application/`, `web/`, `config/`.
- Las entidades JPA no se exponen desde controladores; se usan DTOs.
- Todos los listados operativos deben ser paginados.
- Las tablas Oracle usan prefijo del sistema; para `SED_ESAL`, propuesta inicial `ESAL_`.
- `weblogic.xml` debe declarar `prefer-web-inf-classes`.
- `spring.jpa.open-in-view` debe estar deshabilitado.
- La auditoria debe conservar usuario, rol, accion, timestamps y contexto del evento.
- Swagger/OpenAPI debe estar disponible en todos los perfiles.

## 6. Reglas De UX/UI

- `docs/DESIGN.md` gobierna colores, tipografia, densidad y componentes.
- Las pantallas en `Documentos_Referencia/Prototipo/` son referencia visual obligatoria.
- Usar componentes PrimeNG 20 antes de construir controles propios.
- Mantener interfaz institucional, compacta, clara y orientada a gestion administrativa.
- No introducir landing pages para flujos administrativos.
- Los formularios extensos se organizan por secciones, tabs o acordeones con validacion visible.
- Los estados y semaforos deben ser visibles sin saturar la interfaz.

## 7. Fronteras Iniciales Por Incremento

Incremento 0 incluye:

- Documentacion rectora propia de `SED_ESAL`.
- Arquitectura, tecnologias, arranque, guia funcional y plan base.
- Confirmacion de coordenadas tecnicas abiertas.

Incremento 1 incluye:

- Modelo de datos base.
- Carga inicial Excel.
- Diccionario de obligatoriedad.
- Estados ESAL.
- Semaforo de completitud.
- Registro inicial de documentos soporte.
- Usuarios locales de desarrollo y roles base.

Incremento 1 excluye:

- Generacion PDF final.
- Integracion real Azure AD.
- Consulta publica externa.
- QR/codigo de verificacion.
- Firma digital certificada.

## 8. Gates De Calidad

Antes de cerrar una tarea:

- Ejecutar la verificacion definida en el plan.
- Confirmar que no se invadio alcance de otro incremento.
- Confirmar que el cambio respeta `docs/ARCHITECTURE.md`.
- Confirmar que los criterios de aceptacion afectados estan cubiertos.
- Confirmar que las reglas de negocio del PRD vigente no fueron contradichas.

Antes de cerrar un incremento:

- Spec, plan y log de ejecucion deben estar actualizados.
- Pruebas backend relevantes deben pasar.
- Pruebas frontend relevantes deben pasar.
- Flujo funcional manual del incremento debe estar validado.
- `docs/ARRANQUE.md` y `docs/GUIA_PRUEBAS_FUNCIONALES.md` deben reflejar el estado vigente.

## 9. Politica De Evolucion

Esta constitucion es un documento vivo, pero estable. Cualquier cambio debe:

- Explicar que regla cambia.
- Indicar que specs o planes quedan afectados.
- Actualizar los artefactos derivados antes de implementar codigo.
- Registrar el cambio en el log de ejecucion del incremento activo cuando exista.

