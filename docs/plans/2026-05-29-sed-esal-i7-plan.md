# I7 Alineacion UI Institucional — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use `agent-skills:frontend-ui-engineering` and `agent-skills:incremental-implementation` to execute this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Alinear la interfaz Angular de `SED_ESAL` con una experiencia institucional SED, adaptando la propuesta visual SIGCON como guia de patron visual sin cambiar backend, dominio, roles ni endpoints.

**Architecture:** I7 es un incremento frontend/documental. La fuente de verdad visual se actualiza primero en `docs/DESIGN.md`; luego se aplican tokens/estilos compartidos en Angular, se ajustan login/shell y se refinan pantallas prioritarias existentes. No se crean rutas paralelas ni dependencias nuevas salvo aprobacion expresa.

**Tech Stack:** Angular 20, TypeScript strict, PrimeNG 20, PrimeIcons 7, Tailwind CSS 3.4, Jasmine/Karma, ChromeHeadless.

---

## Mapa De Archivos

| Archivo | Accion |
|---|---|
| `docs/DESIGN.md` | MODIFICAR - documentar adaptacion I7 desde referencia SIGCON |
| `README.md` | MODIFICAR - registrar I7 en orden de lectura y estado |
| `docs/ARRANQUE.md` | MODIFICAR al cierre - agregar estado/verificacion I7 |
| `docs/GUIA_PRUEBAS_FUNCIONALES.md` | MODIFICAR al cierre - agregar checklist manual UI I7 |
| `docs/plans/2026-05-29-sed-esal-i7-execution-log.md` | CREAR - log de ejecucion |
| `sed-esal-angular/src/styles.css` | MODIFICAR - tokens, utilidades y componentes base |
| `sed-esal-angular/src/app/shared/layout/shell.component.*` | MODIFICAR - shell institucional |
| `sed-esal-angular/src/app/features/login/login.component.*` | MODIFICAR - login institucional |
| `sed-esal-angular/src/app/features/dashboard/dashboard.component.ts` | MODIFICAR - tablero compacto |
| `sed-esal-angular/src/app/features/busqueda/busqueda.component.ts` | MODIFICAR - filtros/tabla/estados |
| `sed-esal-angular/src/app/features/busqueda/busqueda-detalle.component.ts` | MODIFICAR - detalle por secciones |
| `sed-esal-angular/src/app/features/certificados/preview-certificado.component.ts` | MODIFICAR - preview consistente |
| `sed-esal-angular/src/app/features/certificados/resultado-certificado.component.*` | MODIFICAR - resultado/descarga consistente |
| `sed-esal-angular/src/app/features/admin/**` | MODIFICAR selectivo - mantenimiento, administracion y tablas |
| `sed-esal-angular/src/app/**/*.spec.ts` | MODIFICAR/CREAR - pruebas de render y regresion |

Rutas base:

- `C:\Users\jmep2\Downloads\SED\ProyectoESAL`
- `C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-angular`

---

## Task 1: Crear Log De Ejecucion I7 Y Actualizar Descubribilidad

**Files:**

- Create: `docs/plans/2026-05-29-sed-esal-i7-execution-log.md`
- Modify: `README.md`

- [ ] **Step 1: Crear el execution log I7**

Contenido inicial:

```markdown
# Execution Log I7 - Alineacion UI Institucional SED_ESAL

> Estado: en ejecucion.
> Fecha inicio: 2026-05-29.
> Spec: `docs/specs/2026-05-29-sed-esal-i7-spec.md`.
> Plan: `docs/plans/2026-05-29-sed-esal-i7-plan.md`.

## Supuestos De Alcance

- I7 es frontend/documental.
- No se modifica backend, base de datos ni reglas funcionales.
- SIGCON es referencia visual, no dominio funcional.

## Registro

| Tarea | Estado | Evidencia |
|---|---|---|
| T1 | Pendiente | - |
| T2 | Pendiente | - |
| T3 | Pendiente | - |
| T4 | Pendiente | - |
| T5 | Pendiente | - |
| T6 | Pendiente | - |
| T7 | Pendiente | - |
| T8 | Pendiente | - |
```

- [ ] **Step 2: Actualizar `README.md`**

Agregar al orden de lectura:

```markdown
14. `docs/specs/2026-05-29-sed-esal-i7-spec.md`
15. `docs/GUIA_PRUEBAS_FUNCIONALES.md`
```

Agregar fila de incremento:

```markdown
| I7 | Especificado | Alineacion UI institucional SED_ESAL |
```

- [ ] **Step 3: Verificar diff**

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL
git diff -- README.md docs\plans\2026-05-29-sed-esal-i7-execution-log.md
```

**Acceptance:** El repo descubre I7 desde README y existe log de seguimiento.

---

## Task 2: Actualizar `docs/DESIGN.md` Con La Adaptacion I7

**Files:**

- Modify: `docs/DESIGN.md`
- Modify: `docs/plans/2026-05-29-sed-esal-i7-execution-log.md`

- [ ] **Step 1: Agregar seccion "I7 - Adaptacion Referencia SIGCON"**

La seccion debe documentar:

- SIGCON es insumo visual, no dominio funcional.
- Paleta institucional: azul oscuro, naranja SED, grises y estados.
- Decision tipografica: conservar Public Sans salvo aprobacion posterior.
- Layout administrativo: sidebar, header, breadcrumb, version, contacto.
- Componentes: botones, inputs, tablas, chips, estados vacios, mensajes.
- Restricciones: no landing, no gradientes decorativos, no copiar textos SIGCON.

- [ ] **Step 2: Normalizar tokens si hace falta**

Confirmar que los tokens existentes pueden expresar:

- primary/secondary;
- fondo/superficie;
- estados de exito, advertencia, error, informacion;
- border radius 4px/8px;
- densidad de tabla;
- foco accesible.

- [ ] **Step 3: Actualizar log**

Registrar decision de tipografia y paleta.

**Acceptance:** `docs/DESIGN.md` permite implementar I7 sin depender del PDF como contrato externo.

---

## Task 3: Consolidar Estilos Globales Y Componentes Base

**Files:**

- Modify: `sed-esal-angular/src/styles.css`
- Modify/Create: specs frontend relevantes si existen
- Modify: `docs/plans/2026-05-29-sed-esal-i7-execution-log.md`

- [ ] **Step 1: Auditar estilos globales actuales**

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-angular
Get-Content src\styles.css
```

- [ ] **Step 2: Ajustar tokens y utilidades compartidas**

Agregar o consolidar:

- variables de color;
- escala tipografica;
- clases de botones;
- inputs y labels;
- tabla administrativa;
- chips de estado;
- mensajes de error/vacio/carga;
- layout de pagina;
- utilidades de breadcrumb/version/contacto si son globales.

- [ ] **Step 3: Reemplazar emojis donde el componente use iconografia primaria**

Preferir PrimeIcons si ya estan disponibles. Si un emoji es solo decorativo y no afecta profesionalismo, dejarlo solo temporalmente y registrarlo en log.

- [ ] **Step 4: Verificar build rapido**

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-angular
node "C:\Program Files\nodejs\node_modules\npm\bin\npm-cli.js" run build
```

**Acceptance:** La base visual compila y no rompe estilos existentes.

---

## Task 4: Redisenar Login Institucional

**Files:**

- Modify: `sed-esal-angular/src/app/features/login/login.component.html`
- Modify: `sed-esal-angular/src/app/features/login/login.component.css`
- Modify: `sed-esal-angular/src/app/features/login/login.component.ts` solo si se requiere texto/version desde propiedad local
- Modify/Create: `sed-esal-angular/src/app/features/login/login.component.spec.ts` si aplica
- Modify: `docs/plans/2026-05-29-sed-esal-i7-execution-log.md`

- [ ] **Step 1: Redisenar estructura**

Debe incluir:

- titulo `SED_ESAL`;
- subtitulo institucional;
- descripcion funcional;
- version visible;
- formulario actual local-dev;
- boton/placeholder Office 365 deshabilitado o informativo si no hay integracion real;
- bloque de contacto;
- error y validaciones existentes.

- [ ] **Step 2: Mantener comportamiento actual**

No cambiar:

- validacion de formulario;
- llamada a `AuthService`;
- rutas post-login;
- usuarios local-dev.

- [ ] **Step 3: Prueba de render**

Validar que el spec confirme marca, formulario y boton principal.

**Acceptance:** Login se ve institucional y conserva comportamiento de acceso local.

---

## Task 5: Redisenar Shell, Navegacion Y Breadcrumb

**Files:**

- Modify: `sed-esal-angular/src/app/shared/layout/shell.component.html`
- Modify: `sed-esal-angular/src/app/shared/layout/shell.component.css`
- Modify: `sed-esal-angular/src/app/shared/layout/shell.component.ts`
- Modify/Create: spec correspondiente si aplica
- Modify: `docs/plans/2026-05-29-sed-esal-i7-execution-log.md`

- [ ] **Step 1: Agregar version y breadcrumb**

Implementar mapa local de rutas para breadcrumb si no existe infraestructura previa.

Ejemplo conceptual:

```typescript
readonly routeLabels: Record<string, string> = {
  '/dashboard': 'Inicio',
  '/busqueda': 'Busqueda',
  '/admin/esales': 'Administracion / ESAL',
};
```

- [ ] **Step 2: Mejorar menu lateral**

Mantener filtrado por rol y ajustar:

- iconos PrimeIcons;
- labels compactos;
- estado activo;
- usuario/rol;
- accion de cierre de sesion;
- contacto o footer institucional si cabe.

- [ ] **Step 3: Verificar responsive**

El shell no debe solapar contenido en anchos reducidos. Si no hay sidebar movil, documentar limite y dejar layout usable.

**Acceptance:** Shell muestra navegacion, usuario, rol, version y breadcrumb sin cambiar permisos.

---

## Task 6: Redisenar Dashboard Y Busqueda ESAL

**Files:**

- Modify: `sed-esal-angular/src/app/features/dashboard/dashboard.component.ts`
- Modify: `sed-esal-angular/src/app/features/busqueda/busqueda.component.ts`
- Modify/Create: specs correspondientes si aplica
- Modify: `docs/plans/2026-05-29-sed-esal-i7-execution-log.md`

- [ ] **Step 1: Dashboard compacto**

Reorganizar acceso rapido por rol con estilo administrativo:

- cabecera;
- tarjetas/lista de modulos;
- iconos PrimeIcons;
- sin hero ni copy promocional.

- [ ] **Step 2: Busqueda con filtros densos**

Mantener filtros existentes y mejorar:

- jerarquia visual;
- agrupacion;
- tabla;
- botones;
- estado vacio;
- paginacion.

- [ ] **Step 3: No implementar backend nuevo**

Si se agrega mensaje "no existe", debe ser solo estado visual derivado de resultado vacio. No crear flujo real de alta si requiere API adicional.

**Acceptance:** Dashboard y busqueda conservan comportamiento y quedan alineados al patron SIGCON adaptado.

---

## Task 7: Redisenar Detalle, Preview, Resultado Y Administracion Selectiva

**Files:**

- Modify: `sed-esal-angular/src/app/features/busqueda/busqueda-detalle.component.ts`
- Modify: `sed-esal-angular/src/app/features/certificados/preview-certificado.component.ts`
- Modify: `sed-esal-angular/src/app/features/certificados/resultado-certificado.component.*`
- Modify selectivo: `sed-esal-angular/src/app/features/admin/**/*.ts`
- Modify/Create: specs correspondientes si aplica
- Modify: `docs/plans/2026-05-29-sed-esal-i7-execution-log.md`

- [ ] **Step 1: Detalle por secciones**

Revisar pantallas de detalle/mantenimiento y agrupar:

- datos basicos;
- personeria;
- representantes;
- organos;
- documentos;
- estado/completitud;
- acciones.

- [ ] **Step 2: Preview y resultado de certificado**

Conservar flujos I6:

- preview;
- generar;
- descargar PDF autenticado;
- advertencias de bloqueos.

Mejorar solo jerarquia visual.

- [ ] **Step 3: Administracion selectiva**

Aplicar estilos compartidos a tablas/formularios administrativos donde sea de bajo riesgo.

**Acceptance:** Pantallas clave quedan consistentes sin alterar reglas de generacion ni mantenimiento.

---

## Task 8: Verificacion, Documentacion De Cierre Y Preparacion De Commit

**Files:**

- Modify: `docs/ARRANQUE.md`
- Modify: `docs/GUIA_PRUEBAS_FUNCIONALES.md`
- Modify: `docs/plans/2026-05-29-sed-esal-i7-execution-log.md`
- Modify: `README.md` si el estado cambia a completado

- [ ] **Step 1: Ejecutar pruebas frontend**

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-angular
node "C:\Program Files\nodejs\node_modules\npm\bin\npm-cli.js" test -- --watch=false --browsers=ChromeHeadless
```

Esperado: tests Angular en verde. Si ChromeHeadless falla por entorno, registrar salida exacta y ejecutar build como verificacion minima.

- [ ] **Step 2: Ejecutar build Angular**

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-angular
node "C:\Program Files\nodejs\node_modules\npm\bin\npm-cli.js" run build
```

Esperado: build exitoso.

- [ ] **Step 3: Verificacion manual**

Con servidor Angular:

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-angular
node "C:\Program Files\nodejs\node_modules\npm\bin\npm-cli.js" start
```

Validar:

- login desktop;
- login ancho movil;
- shell/menu/breadcrumb;
- dashboard por rol;
- busqueda con resultados;
- busqueda sin resultados;
- detalle ESAL;
- preview certificado;
- resultado/descarga.

- [ ] **Step 4: Actualizar documentacion**

`docs/ARRANQUE.md`:

- agregar estado I7;
- comandos de verificacion frontend.

`docs/GUIA_PRUEBAS_FUNCIONALES.md`:

- agregar checklist manual UI I7.

`docs/plans/2026-05-29-sed-esal-i7-execution-log.md`:

- registrar evidencia de tests/build/manual.

- [ ] **Step 5: Preparar commit**

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL
git status --short
git add README.md docs sed-esal-angular/src/app
git commit -m "feat: align SED ESAL UI with institutional design"
```

**Acceptance:** Tests/build documentados, docs actualizados, y cambios listos para publicar.

---

## Orden De Ejecucion

1. T1 - Log y README.
2. T2 - `docs/DESIGN.md`.
3. T3 - estilos globales.
4. T4 - login.
5. T5 - shell.
6. T6 - dashboard y busqueda.
7. T7 - detalle, preview y administracion selectiva.
8. T8 - verificacion y cierre.

T2 y T3 son prerequisitos para evitar redisenos inconsistentes. T4-T7 pueden avanzar de forma incremental, pero cada pantalla debe conservar comportamiento antes de pasar a la siguiente.

---

## Comandos De Verificacion

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-angular
node "C:\Program Files\nodejs\node_modules\npm\bin\npm-cli.js" test -- --watch=false --browsers=ChromeHeadless
node "C:\Program Files\nodejs\node_modules\npm\bin\npm-cli.js" run build
```

Verificacion backend no es obligatoria para I7 porque no hay cambios backend. Si se toca accidentalmente backend, ejecutar:

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-backend
mvn test
```

---

## Criterios De Cierre I7

- `docs/DESIGN.md` incorpora la adaptacion SIGCON -> SED_ESAL.
- Login y shell institucionales implementados.
- Pantallas prioritarias alineadas visualmente.
- No hay cambios backend ni migraciones.
- Tests Angular ejecutados o limitacion documentada.
- Build Angular exitoso.
- `README.md`, `docs/ARRANQUE.md`, `docs/GUIA_PRUEBAS_FUNCIONALES.md` y execution log actualizados.
- `git status --short` no muestra cambios no intencionales dentro del alcance I7.
