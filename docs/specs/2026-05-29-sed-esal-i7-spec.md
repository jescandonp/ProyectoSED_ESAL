# Spec I7 - Alineacion UI Institucional SED_ESAL

> Estado: propuesto para revision.
> Fecha: 2026-05-29.
> Sistema: `SED_ESAL`.
> Metodologia: SDD Spec-Anchored.
> PRD base: `docs/specs/2026-05-09-sed-esal-certificados-prd.md`.
> Depende de: `docs/specs/2026-05-27-sed-esal-i6-spec.md`.
> Insumo visual: `C:\Users\jmep2\Downloads\AgenIALab\ProyectoContratosSED\Prototipo\PRO-mockup-site-SIGCON_.pdf`.

## 1. Objetivo

Alinear la experiencia visual del frontend `SED_ESAL` con una guia institucional de interfaces SED, tomando como referencia la propuesta grafica SIGCON sin cambiar el dominio funcional del sistema.

I7 busca que el aplicativo se perciba como una herramienta institucional consistente, compacta y operativa: login con identidad SED, navegacion lateral clara, encabezados con usuario/rol/version, busqueda administrativa densa, tablas legibles, formularios por secciones, estados visibles y footer/contacto institucional donde aplique.

El resultado esperado no es copiar SIGCON, sino adaptar sus patrones de interfaz al contexto `SED_ESAL`, manteniendo las coordenadas propias del proyecto, los roles existentes y los flujos ya implementados hasta I6.

## 2. Supuestos Aprobados Para Esta Spec

- I7 es una iteracion visual/frontend y documental.
- I7 no cambia backend, base de datos, endpoints, roles, reglas de bloqueo ni generacion PDF.
- La maqueta SIGCON es referencia visual, no fuente de nombres, dominio ni permisos.
- `SED_ESAL` conserva marca, textos funcionales y coordenadas canonicas propias.
- `docs/DESIGN.md` sigue siendo el contrato visual base y debe actualizarse antes o junto con cambios Angular.
- Se priorizan pantallas existentes: login, shell, dashboard, busqueda, detalle ESAL, preview/generacion de certificado y administracion.
- El patron "registro no existe, deseas crearlo" se documenta como decision UX condicionada; solo se implementa si puede resolverse sin cambiar reglas de negocio ni endpoints.

## 3. Referencia Visual SIGCON

El PDF de referencia aporta estos patrones reutilizables:

| Pagina | Patron observado | Adaptacion propuesta para SED_ESAL |
|---|---|---|
| 2 | Paleta SED: naranja institucional, azul oscuro, grises y colores alternos | Revisar `docs/DESIGN.md` para alinear tokens con SED sin degradar contraste |
| 3 | Tipografia Montserrat para titulos y Work Sans para texto | Decidir si se migra desde Public Sans o se conserva Public Sans por accesibilidad y estabilidad |
| 4 | Login institucional con version, credenciales, Office 365, contacto y descripcion del sistema | Redisenar login local-dev con estructura equivalente y preparar espacio para Office 365 |
| 5 | Menu lateral, usuario, rol, breadcrumb, version, cerrar sesion, footer | Fortalecer shell actual con breadcrumb, version y contacto institucional |
| 6 | Busqueda compacta con filtros, tabla, paginacion y acciones | Ajustar busqueda ESAL para mayor densidad y jerarquia visual |
| 7 y 9 | Estado "no existe" con pregunta de creacion | Documentar patron y aplicar solo si el flujo actual lo permite sin nuevo backend |
| 8 | Detalle con datos basicos, secciones, obligatorios y acciones de continuidad | Reorganizar detalle ESAL y mantenimiento en secciones/tabs/acordeones consistentes |
| 10 y 11 | Elementos adicionales | Usarlos solo como inspiracion menor si aportan controles reutilizables |

## 4. Alcance

Incluye:

- Actualizar `docs/DESIGN.md` con una seccion I7 que documente la adaptacion de la guia SIGCON a `SED_ESAL`.
- Definir tokens visuales frontend para color, tipografia, espaciado, bordes, estados y densidad administrativa.
- Redisenar el login para reflejar identidad institucional, version, descripcion, contacto y futura autenticacion Office 365.
- Redisenar el shell para incluir menu lateral, usuario, rol, version, breadcrumb y cierre de sesion mas visible.
- Ajustar dashboard para abandonar apariencia de pagina generica y operar como tablero administrativo compacto.
- Ajustar busqueda ESAL con filtros densos, resultados tabulares, acciones consistentes, paginacion y estados vacios.
- Ajustar detalle ESAL, mantenimiento y preview de certificado para usar secciones/tabs/acordeones con obligatorios visibles.
- Unificar botones, inputs, chips, tablas, cards, mensajes de error/vacio y estados de carga.
- Reducir uso de emojis como iconografia primaria; preferir PrimeIcons cuando ya esta disponible en el proyecto.
- Agregar pruebas frontend de renderizado/visibilidad para los cambios con mayor riesgo.
- Validar build Angular.

Excluye:

- Nuevos endpoints backend.
- Cambios a reglas de negocio, seguridad, roles o permisos.
- Cambios al PDF narrativo I6.
- Migraciones Oracle.
- Integracion real Azure AD / Office 365.
- Consulta publica externa.
- Cambio de nombre del sistema a SIGCON o reutilizacion de textos de contratos.
- Implementacion obligatoria del flujo "crear si no existe" si requiere backend nuevo.
- Landing page o pagina promocional.

## 5. Diseno Funcional De UI

### 5.1 Login

El login debe mostrar:

- Nombre `SED_ESAL`.
- Descripcion corta: "Sistema de Administracion de Entidades Sin Animo de Lucro".
- Texto institucional de la Secretaria de Educacion del Distrito.
- Version visible del aplicativo.
- Formulario local-dev existente.
- Boton o placeholder visual para futura autenticacion Office 365, sin activar flujo real en I7.
- Bloque de contacto SED.
- Mensajes de validacion y error con estilo institucional.

### 5.2 Shell Administrativo

El shell debe mantener navegacion lateral y agregar:

- Marca `SED_ESAL` y subtitulo institucional.
- Menu lateral compacto por rol.
- Usuario, rol y accion de cierre de sesion.
- Version visible.
- Breadcrumb derivado de la ruta activa o mapa local de rutas.
- Header sobrio sin duplicar informacion innecesaria.
- Footer/contacto institucional cuando no afecte densidad operativa.

### 5.3 Dashboard

El dashboard debe presentar accesos segun rol con:

- Encabezado administrativo compacto.
- Tarjetas de accion o lista de modulos, sin composicion tipo landing.
- Estados/resumen si ya existen datos disponibles en frontend.
- Iconografia consistente con PrimeIcons.

### 5.4 Busqueda ESAL

La busqueda debe:

- Priorizar ID SIPEJ, nombre y NIT.
- Mostrar filtros en una banda compacta.
- Mantener tabla de resultados con columnas actuales.
- Mantener paginacion y acciones existentes.
- Usar estado vacio claro si no hay resultados.
- Documentar el patron de "crear" para una iteracion futura si el backend no soporta el caso exacto.

### 5.5 Detalle, Mantenimiento Y Preview

Las pantallas de detalle y mantenimiento deben:

- Organizar datos por secciones funcionales.
- Mostrar campos obligatorios y faltantes de forma visible.
- Mantener acciones existentes sin alterar permisos.
- Mejorar jerarquia entre datos basicos, personeria, representantes, organos, documentos y certificado.
- Conservar semaforos y estados de completitud.

## 6. Sistema Visual

`docs/DESIGN.md` debe resolver explicitamente la tension entre la guia actual y la propuesta SIGCON:

- Opcion recomendada: conservar `Public Sans` por estabilidad y legibilidad, documentando Montserrat/Work Sans como referencia de campana no adoptada en I7.
- Mantener una paleta institucional con azul oscuro, naranja SED y grises neutros.
- Evitar gradientes decorativos, landing pages, sombras fuertes y composiciones promocionales.
- Mantener densidad administrativa: tablas de 40px aprox., formularios compactos, labels visibles, cards solo para agrupacion funcional.
- Usar bordes de 4px a 8px maximo.
- Usar PrimeNG/PrimeIcons antes de construir controles propios cuando aplique.

## 7. Arquitectura Frontend

El cambio debe permanecer dentro de `sed-esal-angular`.

Estructura esperada:

```text
sed-esal-angular/src/app/
|-- app.css
|-- shared/layout/
|   |-- shell.component.html
|   |-- shell.component.css
|   |-- shell.component.ts
|-- features/login/
|-- features/dashboard/
|-- features/busqueda/
|-- features/esales/
|-- features/admin/
|-- features/certificados/
```

Reglas:

- No crear rutas paralelas si existe una pantalla funcional que pueda evolucionar.
- Centralizar estilos globales y tokens en `app.css` o el mecanismo existente.
- Mantener Angular standalone components.
- Mantener TypeScript strict.
- No agregar dependencias sin aprobacion expresa.
- No mover responsabilidades de autorizacion al frontend.

## 8. Estrategia De Test

Pruebas minimas:

- `app.spec.ts` o specs existentes siguen pasando.
- Specs nuevas o ajustadas para:
  - login renderiza marca, version y campos de acceso;
  - shell renderiza navegacion segun usuario y muestra rol;
  - busqueda conserva filtros y acciones principales;
  - al menos una pantalla administrativa conserva campos clave.
- Build Angular exitoso.

Comandos:

```powershell
Set-Location C:\Users\jmep2\Downloads\SED\ProyectoESAL\sed-esal-angular
node "C:\Program Files\nodejs\node_modules\npm\bin\npm-cli.js" test -- --watch=false --browsers=ChromeHeadless
node "C:\Program Files\nodejs\node_modules\npm\bin\npm-cli.js" run build
```

Validacion manual recomendada:

- Abrir `http://localhost:4200`.
- Validar login en desktop y ancho movil.
- Validar navegacion lateral y breadcrumb.
- Validar busqueda con resultados, sin resultados y error.
- Validar detalle/preview sin solapamientos de texto.

## 9. Criterios De Aceptacion

- La spec I7 y el plan I7 existen en `docs/specs/` y `docs/plans/`.
- `docs/DESIGN.md` queda actualizado con la adaptacion de la referencia SIGCON.
- Login y shell reflejan identidad institucional SED_ESAL.
- Las pantallas prioritarias mantienen su funcionalidad y mejoran consistencia visual.
- No se introducen cambios backend ni migraciones.
- No se rompen rutas ni permisos existentes.
- Pruebas Angular pasan o se documenta una limitacion tecnica concreta.
- Build Angular pasa.
- `README.md`, `docs/ARRANQUE.md` y `docs/GUIA_PRUEBAS_FUNCIONALES.md` reflejan el estado de I7 al cierre.

## 10. Riesgos

| Riesgo | Mitigacion |
|---|---|
| Copiar SIGCON puede contaminar dominio ESAL con textos de contratos | Usar SIGCON solo como guia visual; todos los textos finales deben mencionar ESAL |
| Cambio de tipografia puede afectar carga y consistencia | Mantener Public Sans salvo aprobacion expresa |
| Redisenar muchas pantallas en un solo incremento puede dispersar alcance | Priorizar estilos compartidos, shell, login, busqueda y detalle/preview |
| Tests Angular pueden requerir ajustes por cambios de DOM | Actualizar specs solo donde cambie estructura visible |
| El patron "crear si no existe" puede requerir backend | Documentar como UX candidata si no puede implementarse con endpoints actuales |

## 11. Fronteras

Always:

- Seguir `docs/CONSTITUTION.md`, `docs/ARCHITECTURE.md` y `docs/DESIGN.md`.
- Mantener identidad `SED_ESAL`.
- Preservar rutas, roles y endpoints existentes.
- Validar con test/build Angular.
- Actualizar documentacion de cierre del incremento.

Ask first:

- Cambiar tipografia base a Montserrat/Work Sans.
- Agregar dependencias.
- Cambiar layout de rutas o navegacion funcional.
- Agregar flujos que requieran backend.
- Modificar autenticacion Office 365 real.

Never:

- Renombrar el sistema a SIGCON.
- Copiar textos funcionales de contratos.
- Introducir landing page administrativa.
- Cambiar permisos solo desde frontend.
- Tocar certificados PDF I6 o migraciones Oracle dentro de I7.

## 12. Preguntas Abiertas

Estas preguntas no bloquean la escritura de la spec ni el plan, pero deben cerrarse antes de implementar si afectan alcance:

- La SED exige adoptar Montserrat/Work Sans en este aplicativo o se conserva Public Sans?
- Existe un numero de version institucional que deba mostrarse distinto al `VS 1.0.3` de la maqueta?
- El bloque de contacto debe usar los datos generales SED del mockup o datos especificos de Inspeccion y Vigilancia?
- El patron "crear si no existe" debe implementarse en I7 o reservarse para una iteracion posterior con backend?
