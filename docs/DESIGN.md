---
name: Sistema de Diseño Institucional SED
colors:
  surface: '#f9f9f9'
  surface-dim: '#dadada'
  surface-bright: '#f9f9f9'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f3f3f3'
  surface-container: '#eeeeee'
  surface-container-high: '#e8e8e8'
  surface-container-highest: '#e2e2e2'
  on-surface: '#1a1c1c'
  on-surface-variant: '#43474f'
  inverse-surface: '#2f3131'
  inverse-on-surface: '#f1f1f1'
  outline: '#737780'
  outline-variant: '#c3c6d1'
  surface-tint: '#3a5f94'
  primary: '#001e40'
  on-primary: '#ffffff'
  primary-container: '#003366'
  on-primary-container: '#799dd6'
  inverse-primary: '#a7c8ff'
  secondary: '#a33e00'
  on-secondary: '#ffffff'
  secondary-container: '#fe6500'
  on-secondary-container: '#541d00'
  tertiary: '#001b4b'
  on-tertiary: '#ffffff'
  tertiary-container: '#002f76'
  on-tertiary-container: '#6c98ff'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#d5e3ff'
  primary-fixed-dim: '#a7c8ff'
  on-primary-fixed: '#001b3c'
  on-primary-fixed-variant: '#1f477b'
  secondary-fixed: '#ffdbcd'
  secondary-fixed-dim: '#ffb596'
  on-secondary-fixed: '#360f00'
  on-secondary-fixed-variant: '#7c2e00'
  tertiary-fixed: '#d9e2ff'
  tertiary-fixed-dim: '#b1c5ff'
  on-tertiary-fixed: '#001946'
  on-tertiary-fixed-variant: '#00419d'
  background: '#f9f9f9'
  on-background: '#1a1c1c'
  surface-variant: '#e2e2e2'
typography:
  headline-lg:
    fontFamily: Public Sans
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Public Sans
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
    letterSpacing: -0.01em
  headline-sm:
    fontFamily: Public Sans
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  body-lg:
    fontFamily: Public Sans
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Public Sans
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  body-sm:
    fontFamily: Public Sans
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 16px
  label-lg:
    fontFamily: Public Sans
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
  label-md:
    fontFamily: Public Sans
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
  label-sm:
    fontFamily: Public Sans
    fontSize: 11px
    fontWeight: '700'
    lineHeight: 14px
    letterSpacing: 0.05em
  headline-lg-mobile:
    fontFamily: Public Sans
    fontSize: 26px
    fontWeight: '700'
    lineHeight: 32px
rounded:
  sm: 0.125rem
  DEFAULT: 0.25rem
  md: 0.375rem
  lg: 0.5rem
  xl: 0.75rem
  full: 9999px
spacing:
  unit: 4px
  xs: 4px
  sm: 8px
  md: 16px
  lg: 24px
  xl: 40px
  gutter: 16px
  margin-mobile: 16px
  margin-desktop: 32px
  max-width: 1440px
---

## Brand & Style

The visual identity of the design system is rooted in the institutional values of the Secretaría de Educación del Distrito: transparency, accessibility, and professional rigor. It is designed to serve as a high-efficiency tool for administrative users, prioritizing clarity and data density over decorative elements.

The design style follows a **Corporate / Modern** approach. It leverages a structured layout and a sober color palette to evoke a sense of authority and reliability. The aesthetic is intentionally "quiet," allowing complex administrative tasks and educational data to take center stage without visual fatigue. Every element is aligned to a strict functional purpose, ensuring that the interface feels like a dependable tool for public service.

## Colors

The color palette is strictly institutional, utilizing high-contrast pairings to ensure readability and compliance with accessibility standards.

- **Primary (#003366):** A deep, conservative navy blue used for headers, primary actions, and navigational anchors. It represents the stability of the institution.
- **Secondary (#FF6600):** A vibrant orange used exclusively for highlighting key calls to action (CTAs), alerts, or status indicators that require immediate user attention.
- **Tertiary (#3366CC):** A lighter corporate blue utilized for interactive elements such as text links, icons within data tables, and secondary buttons.
- **Neutral (#F1F1F1):** A cool gray applied to background surfaces and container fills to reduce glare and differentiate sections in a high-density environment.

## Typography

This design system utilizes **Public Sans** as the sole typeface. Chosen for its origins in government design systems, it offers exceptional legibility in both large headings and small, data-heavy tables.

The typographic scale is optimized for information density. **Body-md** (14px) is the standard for administrative forms and documents, while **Body-sm** (12px) is reserved for dense data grids and metadata. Headings use semi-bold and bold weights to provide a clear content hierarchy. Line heights are kept tight but comfortable to maximize the amount of visible information per screen without compromising vertical rhythm.

## Layout & Spacing

The layout is based on a **fixed-fluid hybrid grid** system. Content is contained within a maximum width of 1440px to ensure line lengths remain readable on ultra-wide monitors common in administrative offices.

- **Grid:** A 12-column grid is used for desktop (breakpoint 1024px+), shifting to a 4-column grid for mobile (below 600px).
- **Density:** We utilize a "Compact" spacing model. Gutters are fixed at 16px to allow for more columns of data in tables and dashboards.
- **Rhythm:** All margins and paddings must be multiples of the 4px base unit. For administrative panels, internal card padding should default to `md` (16px) to maintain a professional, airy yet efficient feel.

## Elevation & Depth

To maintain a clean and professional look, the design system avoids heavy shadows and decorative gradients. Depth is conveyed through **Tonal Layers** and **Low-contrast Outlines**:

1.  **Canvas:** The base background is white (#FFFFFF).
2.  **Containers:** Secondary regions, such as sidebars or header backgrounds, use the neutral fill (#F1F1F1).
3.  **Outlines:** Interactive elements like input fields and cards use 1px borders in a soft gray (e.g., #D1D5DB).
4.  **Shadows:** Shadows are used sparingly only for "floating" elements like dropdown menus or modals. These are styled as soft, ambient blurs (Y: 4px, Blur: 12px, Opacity: 8%) with no color tinting, ensuring they do not distract from the data.

## Shapes

The shape language reflects the "Bordes Suaves" (Soft Borders) requirement by employing a consistent 4px radius across all standard UI components.

- **Standard (4px):** Applied to buttons, input fields, checkboxes, and cards. This subtle rounding softens the institutional look without feeling informal.
- **Large (8px):** Reserved for larger containers like modals or main content cards.
- **Pill:** Only used for status "Chips" or "Badges" to distinguish them from interactive buttons.

## Components

The component library is built for high-utility administrative workflows.

- **Buttons:** Primary buttons use the #003366 background with white text. Secondary buttons use a 1px border of #003366 with a transparent background. CTAs use #FF6600.
- **Inputs:** Fields must include a visible 12px label above the input area. Focus states use a 2px offset border in #3366CC.
- **Data Tables:** The core component of this design system. Rows should have a height of 40px for high density. Use alternating row stripes (Zebra striping) using #F1F1F1 for readability in large datasets.
- **Cards:** Used for grouping related administrative information. Cards should have a 1px #D1D5DB border and no shadow by default.
- **Chips:** Small, rounded indicators for status (e.g., "En Proceso", "Completado"). Use low-saturation background tints of the status color with high-contrast text.
- **Navigation:** A vertical sidebar is preferred for administrative profiles to allow for deeply nested information structures (folders and sub-pages).

## I7 - Adaptacion Referencia SIGCON

I7 incorpora como insumo visual la propuesta `PRO-mockup-site-SIGCON_.pdf`, pero no adopta el dominio funcional de SIGCON ni sus textos de contratos. La referencia se usa para reforzar patrones institucionales comunes: login SED, version visible, navegacion lateral, usuario/rol, breadcrumb, busqueda compacta, resultados tabulares, detalle por secciones y bloque de contacto.

### Principios De Adaptacion

- `SED_ESAL` conserva nombre, rutas, roles, textos funcionales y coordenadas canonicas propias.
- La maqueta SIGCON es guia visual, no fuente de reglas de negocio.
- La interfaz debe sentirse como herramienta administrativa interna, no como landing page.
- Todo cambio visual amplio debe reflejarse primero en este documento o en la spec activa.
- Los componentes existentes evolucionan en sitio; no se crean pantallas paralelas para representar el mismo flujo.

### Paleta I7

La paleta se mantiene institucional y de alto contraste:

| Uso | Token | Valor |
|---|---|---|
| Primario institucional | `--color-primary` | `#001e40` |
| Primario navegacion/acciones | `--color-primary-container` | `#003366` |
| Naranja SED/CTA | `--color-secondary-container` | `#fe6500` |
| Interaccion/enlaces/foco | `--color-interactive` | `#3366cc` |
| Fondo app | `--color-background` | `#f9f9f9` |
| Superficie | `--color-surface` | `#ffffff` |
| Superficie alterna | `--color-surface-container-low` | `#f3f3f3` |
| Borde suave | `--color-outline-variant` | `#c3c6d1` |

Los colores alternos de SIGCON (`#f9e04b`, `#64d9d5`, `#54a2e6`) quedan reservados para estados o elementos informativos puntuales si hay una regla de uso clara. No deben convertirse en paleta dominante.

### Tipografia I7

La referencia SIGCON propone Montserrat para titulos y Work Sans para texto. En I7 se conserva `Public Sans` como fuente base por estabilidad, legibilidad administrativa y consistencia con el sistema ya documentado. Un cambio futuro a Montserrat/Work Sans requiere aprobacion explicita y validacion de disponibilidad de fuentes en el entorno SED.

Reglas:

- Titulos de pagina: `headline-md`, 24px, 600.
- Encabezados internos: 16px-20px, 600.
- Formularios y tablas: 14px como base.
- Metadata, version y ayuda: 12px.
- No usar escalamiento por ancho de viewport.

### Layout Administrativo I7

El shell toma de SIGCON estos patrones:

- Barra lateral fija o compacta con secciones por rol.
- Header sobrio con breadcrumb, usuario, rol y version.
- Contenido principal con ancho util y densidad operativa.
- Footer/contacto institucional cuando no compita con el flujo principal.
- Estados vacios, errores y carga siempre visibles.

El layout debe responder a escritorio y anchos reducidos sin solapar texto. Si una pantalla no puede ofrecer navegacion lateral completa en movil, debe degradar a una version apilada legible.

### Componentes I7

- **Botones:** primario azul institucional; secundario con borde azul; CTA naranja solo para accion principal excepcional.
- **Inputs:** label visible sobre campo, ayuda/error debajo, foco azul accesible.
- **Tablas:** cabecera azul, filas de 40px aprox., zebra suave, acciones compactas por fila.
- **Chips:** estado textual mas color; el color nunca es el unico indicador.
- **Cards:** solo para agrupar informacion funcional; no usar cards anidadas.
- **Iconografia:** preferir PrimeIcons sobre emojis para navegacion, modulos y acciones.
- **Breadcrumb:** texto compacto bajo el header o dentro del header, derivado de ruta funcional.
- **Version:** visible como metadata, no como elemento de marketing.

### Restricciones I7

- No copiar nombres, menus ni datos de contratos de SIGCON.
- No introducir gradientes decorativos, sombras fuertes, orbes ni composiciones promocionales.
- No usar hero sections para flujos administrativos.
- No agregar dependencias visuales sin aprobacion.
- No mover autorizacion al frontend.
- No cambiar PDF, endpoints, base de datos ni reglas de dominio dentro de I7.
