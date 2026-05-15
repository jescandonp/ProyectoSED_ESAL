import { Component } from '@angular/core';

@Component({
  selector: 'app-carga-inicial',
  standalone: true,
  template: `
    <div>
      <h2 class="sed-page-title">Carga Inicial</h2>
      <div class="sed-card">
        <p>Carga de base histórica desde Excel.</p>
        <p style="margin-top: 16px; color: var(--color-on-surface-variant);"><em>En desarrollo - I1</em></p>
      </div>
    </div>
  `,
})
export class CargaInicialComponent {}
